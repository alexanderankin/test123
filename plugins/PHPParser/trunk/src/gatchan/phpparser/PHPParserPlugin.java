package gatchan.phpparser;

import gatchan.phpparser.project.ProjectManager;
import gatchan.phpparser.project.itemfinder.FrameFindItem;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import java.awt.*;

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

  public static void findClass(View view) {
    findItem(view, FrameFindItem.CLASS_MODE);
  }

  public static void findMethod(View view) {
    findItem(view, FrameFindItem.METHOD_MODE);
  }

  private static void findItem(View view, int mode) {
    moveFindItemWindow(view);
    findItemWindow.init(view, mode);
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
      if (offsetInLine < buffer.getLineLength(caretLine)) {
        buffer.markTokens(caretLine, tokenHandler);
        final Token tokenAtOffset = TextUtilities.getTokenAtOffset(tokenHandler.getTokens(), offsetInLine);
        if (tokenAtOffset.id == Token.LITERAL1) {


          final Token nextToken = tokenAtOffset.next;
          if (nextToken.id == Token.LITERAL1) {
            final int length = nextToken.length;
            textArea.setSelectedText(escape(length, nextToken, buffer, caretLine, s, textArea));
          } else {
            textArea.setSelectedText(escape(tokenAtOffset.length, tokenAtOffset, buffer, caretLine, s, textArea));
          }
          return;
        }
      }
      textArea.setSelectedText(s);
    }
  }

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

          return escapeChar(s, endStringChar);
        }
      }
    }
    return s;
  }

  private static String escapeChar(String s, char charToEscape) {
    int off = s.indexOf(charToEscape);
    int oldPos = 0;
    if (off != -1) {
      final StringBuffer buff = new StringBuffer(s.length() << 1);
      while (off != -1) {
        buff.append(s.substring(oldPos, off)).append('\\');
        oldPos = off;
        off = s.indexOf(charToEscape, oldPos + 1);
      }
      buff.append(s.substring(oldPos));
      return buff.toString();
    }
    return s;
  }

}
