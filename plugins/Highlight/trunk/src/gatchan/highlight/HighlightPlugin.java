package gatchan.highlight;

import gnu.regexp.REException;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.util.Log;

import javax.swing.*;

/**
 * The HighlightPlugin. This is my first plugin for jEdit, some parts of my code were inspired by the ErrorList plugin
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public final class HighlightPlugin extends EBPlugin {
  private static HighlightManager highlightManager;

  public static final String NAME = "highlight";
  public static final String PROPERTY_PREFIX = "plugin.Highlight.";
  public static final String MENU = "highlight.menu";
  public static final String OPTION_PREFIX = "options.highlight.";

  /** Initialize the plugin. When starting this plugin will add an Highlighter on each text area */
  public void start() {
    highlightManager = HighlightManagerTableModel.getManager();
    View view = jEdit.getFirstView();
    while (view != null) {
      EditPane[] panes = view.getEditPanes();
      for (int i = 0; i < panes.length; i++) {
        JEditTextArea textArea = panes[i].getTextArea();
        initTextArea(textArea);
      }
      view = view.getNext();
    }
  }


  /** uninitialize the plugin. we will remove the Highlighter on each text area */
  public void stop() {
    if (highlightManager.countHighlights() == 0) {
      jEdit.setProperty("plugin.gatchan.highlight.HighlightPlugin.activate", "defer");
    } else {
      jEdit.setProperty("plugin.gatchan.highlight.HighlightPlugin.activate", "startup");
    }

    highlightManager.dispose();
    highlightManager = null;
    View view = jEdit.getFirstView();
    while (view != null) {
      EditPane[] panes = view.getEditPanes();
      for (int i = 0; i < panes.length; i++) {
        JEditTextArea textArea = panes[i].getTextArea();
        uninitTextArea(textArea);
      }
      view = view.getNext();
    }
  }

  /**
   * Remove the highlighter from a text area.
   *
   * @param textArea the textarea from wich we will remove the highlighter
   * @see #stop()
   * @see #handleEditPaneMessage(EditPaneUpdate)
   */
  private static void uninitTextArea(JEditTextArea textArea) {
    TextAreaPainter painter = textArea.getPainter();
    Highlighter highlighter = (Highlighter) textArea.getClientProperty(Highlighter.class);
    if (highlighter != null) {
      painter.removeExtension(highlighter);
      textArea.putClientProperty(Highlighter.class, null);
    }
  }

  /**
   * Initialize the textarea with a highlight painter.
   *
   * @param textArea the textarea to initialize
   * @return the new highlighter for the textArea
   */
  private static Highlighter initTextArea(JEditTextArea textArea) {
    Highlighter highlighter = new Highlighter(textArea);
    TextAreaPainter painter = textArea.getPainter();
    painter.addExtension(TextAreaPainter.HIGHEST_LAYER, highlighter);
    textArea.putClientProperty(Highlighter.class, highlighter);
    return highlighter;
  }


  public void handleMessage(EBMessage message) {
    if (message instanceof EditPaneUpdate) {
      handleEditPaneMessage((EditPaneUpdate) message);
    }
  }


  private static void handleEditPaneMessage(EditPaneUpdate message) {
    JEditTextArea textArea = message.getEditPane().getTextArea();
    Object what = message.getWhat();

    if (what == EditPaneUpdate.CREATED) {
      initTextArea(textArea);
    } else if (what == EditPaneUpdate.DESTROYED) {
      uninitTextArea(textArea);
    }
  }

  /**
   * Highlight a word in a textarea. If a text is selected this text will be highlighted, if no text is selected we will
   * ask the textarea to select a word
   *
   * @param textArea the textarea
   */
  public static void highlightThis(JEditTextArea textArea) {
    String text = getCurrentWord(textArea);
    try {
      highlightManager.addElement(new Highlight(text));
    } catch (REException e) {
      Log.log(Log.MESSAGE, HighlightPlugin.class, "This should never happens here " + e.getMessage());
    }
  }

  /**
   * Get the current word. If nothing is selected, it will select it.
   *
   * @param textArea the textArea
   * @return the current word
   */
  private static String getCurrentWord(JEditTextArea textArea) {
    String text = textArea.getSelectedText();
    if (text == null) {
      textArea.selectWord();
      text = textArea.getSelectedText();
    }
    return text;
  }

  /**
   * Highlight a word in a textarea. If a text is selected this text will be highlighted, if no text is selected we will
   * ask the textarea to select a word. only the entire word will be highlighted
   *
   * @param textArea the textarea
   */
  public static void highlightEntireWord(JEditTextArea textArea) {
    String text = getCurrentWord(textArea);

    try {
      Highlight highlight = new Highlight("\\<" + text + "\\>", true, false);
      highlightManager.addElement(highlight);
    } catch (REException e) {
      Log.log(Log.MESSAGE, HighlightPlugin.class, "This should never happens here " + e.getMessage());
    }
  }

  public static void highlightCurrentSearch() {
    try {
      Highlight h = new Highlight();
      h.init(SearchAndReplace.getSearchString(), SearchAndReplace.getRegexp(), false, Highlight.getNextColor());
      addHighlight(h);
    } catch (REException e) {
      Log.log(Log.WARNING, HighlightPlugin.class, "This should never happens");
      Log.log(Log.WARNING, HighlightPlugin.class, e);
    }
  }

  /**
   * Show an highlight dialog.
   *
   * @param view the current view
   */
  public static void highlightDialog(View view) {
    HighlightDialog d = new HighlightDialog(view);
    d.setVisible(true);
  }

  public static void addHighlight(Highlight highlight) {
    highlightManager.addElement(highlight);
  }

  public static void removeAllHighlights() {
    highlightManager.removeAll();
  }

  public static void enableHighlights() {
    highlightManager.setHighlightEnable(true);
  }

  public static void disableHighlights() {
    highlightManager.setHighlightEnable(false);
  }

  public static void toggleHighlights() {
    highlightManager.setHighlightEnable(!highlightManager.isHighlightEnable());
  }

  public static boolean isHighlightEnable() {
    return highlightManager.isHighlightEnable();
  }
}
