package gatchan.phpparser;

import gatchan.phpparser.project.ProjectManager;
import gatchan.phpparser.project.itemfinder.FrameFindItem;
import gatchan.phpparser.parser.PHPParser;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.ParserRuleSet;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.syntax.ParserRule;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

import java.awt.*;
import java.util.StringTokenizer;

/**
 * The PHP Parser plugin.
 *
 * @author Matthieu Casanova
 */
public final class PHPParserPlugin extends EBPlugin {
  private ProjectManager projectManager;

  private static FrameFindItem findItemWindow;

  public void start() {
    projectManager = ProjectManager.getInstance();
    findItemWindow = new FrameFindItem();
  }

  public void stop() {
    projectManager.dispose();
    projectManager = null;
    findItemWindow.dispose();
    findItemWindow = null;
  }

  public void handleMessage(EBMessage message) {
    if (message instanceof BufferUpdate) {
      final BufferUpdate bufferUpdate = (BufferUpdate) message;
      final Object what = bufferUpdate.getWhat();
      if (what == BufferUpdate.LOADED) {
        final Buffer buffer = bufferUpdate.getBuffer();
        if ("php".equals(buffer.getMode().getName())) {
          buffer.setProperty("sidekick.parser", "PHPParser");
        }
      } else if (what == BufferUpdate.PROPERTIES_CHANGED) {
        final Buffer buffer = bufferUpdate.getBuffer();
        if ("php".equals(buffer.getMode().getName())) {
          buffer.setProperty("sidekick.parser", "PHPParser");
        } else if ("PHPParser".equals(buffer.getProperty("sidekick.parser"))) {
          buffer.setProperty("sidekick.parser", null);
        }
      }
    }
  }

  /**
   * show the dialog to find a class.
   *
   * @param view the jEdit's view
   */
  public static void findClass(View view) {
    findItem(view, FrameFindItem.CLASS_MODE);
  }

  /**
   * show the dialog to find a method.
   *
   * @param view the jEdit's view
   */
  public static void findMethod(View view) {
    findItem(view, FrameFindItem.METHOD_MODE);
  }

  /**
   * Open the find item frame for the view in the given mode
   *
   * @param view the view
   * @param mode one of the following  {@link FrameFindItem#CLASS_MODE} or {@link FrameFindItem#METHOD_MODE}
   */
  private static void findItem(View view, int mode) {
    moveFindItemWindow(view);
    findItemWindow.init(view, mode);
    GUIUtilities.centerOnScreen(findItemWindow);
    findItemWindow.setVisible(true);
  }

  private static void moveFindItemWindow(View view) {
    final Dimension viewSize = view.getSize();
    final Point locationOnScreen = view.getLocationOnScreen();
    final Dimension findItemWindowsSize = findItemWindow.getSize();
    findItemWindow.setLocation(locationOnScreen.x + ((viewSize.width - findItemWindowsSize.width) >> 1),
                    locationOnScreen.y + ((viewSize.height - findItemWindowsSize.height) >> 1));
  }


  public static void pastePHP(Buffer buffer, JEditTextArea textArea) {
    if ("php".equals(buffer.getMode().getName())) {
      final String s = Registers.getRegister('$').toString();
      final DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
      final int caretLine = textArea.getCaretLine();
      final int offsetInLine = textArea.getCaretPosition() - textArea.getLineStartOffset(caretLine);

      int caretPosition = textArea.getCaretPosition();
      ParserRuleSet rule = buffer.getRuleSetAtOffset(caretPosition);
      if (!"PHP_LITERAL".equals(rule.getSetName())) {
        rule = buffer.getRuleSetAtOffset(caretPosition + 1);
        if (!"PHP_LITERAL".equals(rule.getSetName())) {
          rule = buffer.getRuleSetAtOffset(caretPosition - 1);
          caretPosition = caretPosition - 2;
        }
      } else {
        caretPosition = caretPosition - 1;
      }
      Log.log(Log.DEBUG, PHPParserPlugin.class, "Rule :" + rule.getSetName());


      if ("PHP_LITERAL".equals(rule.getSetName())) {
        while (buffer.getRuleSetAtOffset(++caretPosition) == rule) {

        }
        rule = buffer.getRuleSetAtOffset(caretPosition);
        final ParserRule escapeRule = rule.getEscapeRule();
        char charForToken0 = textArea.getText(caretPosition - 1, 1).charAt(0);
        char charForToken = textArea.getText(caretPosition, 1).charAt(0);
        Log.log(Log.DEBUG, PHPParserPlugin.class, "Prev :" + charForToken0);
        Log.log(Log.DEBUG,
                PHPParserPlugin.class,
                        "Escaping :" + charForToken + " caret : " + caretPosition + " rule : " + buffer.getRuleSetAtOffset(caretPosition).getSetName());
        String selectedText = escapeChar(s, escapeRule.hashChar, escapeRule.hashChar);
        selectedText = escapeChar(selectedText,charForToken,escapeRule.hashChar);
        textArea.setSelectedText(selectedText);
      } else {
        Registers.paste(textArea, '$');
      }
    } else {
      Registers.paste(textArea, '$');
    }
  }

  private static char getLastTokenOfKind(DefaultTokenHandler tokenHandler,
                                         Buffer buffer,
                                         JEditTextArea textArea,
                                         ParserRuleSet rule,
                                         int line,
                                         Token token) {
    Token nextToken = token.next;
    int tokenLine = line;
    while (nextToken.rules == rule) {
      token = nextToken;
      nextToken = token.next;
      tokenLine = line;
      if (nextToken == null) { // end of line
        buffer.markTokens(++line, tokenHandler);
        nextToken = TextUtilities.getTokenAtOffset(tokenHandler.getTokens(), 0);
      }
    }
    if (tokenLine != line) {
      return textArea.getText(textArea.getLineStartOffset(tokenLine) + token.offset - 1, 1).charAt(0);
    } else {
      return textArea.getText(textArea.getLineStartOffset(tokenLine) + token.offset, 1).charAt(0);
    }
  }
  /*
   private static String escape(int length,
                                Token nextToken,
                                Buffer buffer,
                                int caretLine,
                                String s,
                                JEditTextArea textArea) {
     if (length == 1) {
       final int nextTokenOffset = nextToken.offset;
       final String lineText = buffer.getLineText(caretLine);
       if (nextTokenOffset < lineText.length()) {
         final char endStringChar = lineText.charAt(nextTokenOffset);
         if (endStringChar == '\'' || endStringChar == '"' || endStringChar == '`') {

           return escapeChar(s, endStringChar, escapeRule.hashChar);
         }
       }
     }
     return s;
   }     */

  private static String escapeChar(String s, char charToEscape, char hashChar) {
    int off = s.indexOf(charToEscape);
    int oldPos = 0;
    if (off != -1) {
      final StringBuffer buff = new StringBuffer(s.length() << 1);
      while (off != -1) {
        buff.append(s.substring(oldPos, off)).append(hashChar);
        oldPos = off;
        off = s.indexOf(charToEscape, oldPos + 1);
      }
      buff.append(s.substring(oldPos));
      return buff.toString();
    }
    return s;
  }
}
