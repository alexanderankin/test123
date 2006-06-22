package gatchan.phpparser;

import gatchan.phpparser.project.ProjectManager;
import gatchan.phpparser.project.itemfinder.FrameFindItem;
import org.gjt.sp.jedit.*;

/**
 * The PHP Parser plugin.
 *
 * @author Matthieu Casanova
 */
public final class PHPParserPlugin extends EditPlugin {
  private ProjectManager projectManager;

  private static FrameFindItem findItemWindow;

  public void start() {
    projectManager = ProjectManager.getInstance();
    findItemWindow = new FrameFindItem();
    /*View view = jEdit.getFirstView();
    while (view != null) {
      EditPane[] panes = view.getEditPanes();
      for (int i = 0; i < panes.length; i++) {
        JEditTextArea textArea = panes[i].getTextArea();
        initTextArea(textArea);
      }
      view = view.getNext();
    }  */
  }

  public void stop() {
    projectManager.dispose();
    projectManager = null;
    findItemWindow.dispose();
    findItemWindow = null;
    /*View view = jEdit.getFirstView();
    while (view != null) {
      EditPane[] panes = view.getEditPanes();
      for (int i = 0; i < panes.length; i++) {
        JEditTextArea textArea = panes[i].getTextArea();
        uninitTextArea(textArea);
      }
      view = view.getNext();
    } */
  }
      /*
  private static void uninitTextArea(JEditTextArea textArea) {
    TextAreaPainter painter = textArea.getPainter();
    PHPParserTextAreaExtension highlighter = (PHPParserTextAreaExtension) textArea.getClientProperty(PHPParserTextAreaExtension.class);
    if (highlighter != null) {
      painter.removeExtension(highlighter);
      textArea.putClientProperty(PHPParserTextAreaExtension.class, null);
    }
  }

  private static void initTextArea(JEditTextArea textArea) {
    PHPParserTextAreaExtension highlighter = new PHPParserTextAreaExtension(textArea);
    TextAreaPainter painter = textArea.getPainter();
    painter.addExtension(TextAreaPainter.HIGHEST_LAYER, highlighter);
    textArea.putClientProperty(PHPParserTextAreaExtension.class, highlighter);
  }   */

  public void handleMessage(EBMessage message) {
    /* if (message instanceof BufferUpdate) {
   // BufferUpdate bufferUpdate = (BufferUpdate) message;
   // Object what = bufferUpdate.getWhat();
    /*if (what == BufferUpdate.LOADED) {
      Buffer buffer = bufferUpdate.getBuffer();
      if ("php".equals(buffer.getMode().getName())) {
        buffer.setProperty("sidekick.parser", "PHPParser");
      }
    } else if (what == BufferUpdate.PROPERTIES_CHANGED) {
      Buffer buffer = bufferUpdate.getBuffer();
      if ("php".equals(buffer.getMode().getName())) {
        buffer.setProperty("sidekick.parser", "PHPParser");
      } else if ("PHPParser".equals(buffer.getProperty("sidekick.parser"))) {
        buffer.setProperty("sidekick.parser", null);
      }
    }*/
    /* } else */
  /*  if (message instanceof EditPaneUpdate) {
      handleEditPaneMessage((EditPaneUpdate) message);
    }   */
  }

 /* private static void handleEditPaneMessage(EditPaneUpdate message) {
    JEditTextArea textArea = message.getEditPane().getTextArea();
    Object what = message.getWhat();

    if (what == EditPaneUpdate.CREATED) {
      initTextArea(textArea);
    } else if (what == EditPaneUpdate.DESTROYED) {
      uninitTextArea(textArea);
    }
  }   */

  /**
   * show the dialog to find a class.
   *
   * @param view the jEdit's view
   */
  public static void findClass(View view) {
    findItem(view, FrameFindItem.CLASS_MODE, FrameFindItem.PROJECT_SCOPE);
  }

  /**
   * show the dialog to find a class.
   *
   * @param view the jEdit's view
   */
  public static void findInterface(View view) {
    findItem(view, FrameFindItem.INTERFACE_MODE, FrameFindItem.PROJECT_SCOPE);
  }

  /**
   * show the dialog to find a class.
   *
   * @param view the jEdit's view
   */
  public static void findClassOrInterface(View view) {
    findItem(view, FrameFindItem.CLASS_MODE ^ FrameFindItem.INTERFACE_MODE, FrameFindItem.PROJECT_SCOPE);
  }

  /**
   * show the dialog to find a method.
   *
   * @param view the jEdit's view
   */
  public static void findMethod(View view) {
    findItem(view, FrameFindItem.METHOD_MODE, FrameFindItem.PROJECT_SCOPE);
  }

  /**
   * Find any item in the current file.
   *
   * @param view the jEdit's view
   */
  public static void findInFile(View view) {
    findItem(view, FrameFindItem.ALL_MODE, FrameFindItem.FILE_SCOPE);
  }

  /**
   * Open the find item frame for the view in the given mode
   *
   * @param view  the view
   * @param mode  one of the following  {@link FrameFindItem#ALL_MODE}, {@link FrameFindItem#CLASS_MODE} or {@link
   *              FrameFindItem#METHOD_MODE}
   * @param scope the scope : {@link FrameFindItem#FILE_SCOPE} or {@link FrameFindItem#PROJECT_SCOPE}
   */
  private static void findItem(View view, int mode, int scope) {
    findItemWindow.init(view, mode, scope);
    findItemWindow.setLocationRelativeTo(jEdit.getActiveView());
    findItemWindow.setVisible(true);
  }
}
