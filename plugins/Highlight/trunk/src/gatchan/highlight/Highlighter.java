package gatchan.highlight;

import org.gjt.sp.jedit.search.SearchMatcher;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.util.CharIndexedSegment;

import javax.swing.text.Segment;
import java.awt.*;

/**
 * The Highlighter is the TextAreaExtension that will look for some String to highlight in the textarea and draw a
 * rectangle in it's background.
 *
 * @author Matthieu Casanova
 */
final class Highlighter extends TextAreaExtension {
  private final JEditTextArea textArea;
  private final Segment seg;
  private final Point point;
  private final FontMetrics fm;
  private final Color highlightColor = new Color(153, 255, 204);

  private Highlight highlight;

  public Highlighter(JEditTextArea textArea) {
    this.textArea = textArea;
    final TextAreaPainter painter = textArea.getPainter();
    fm = painter.getFontMetrics();
    seg = new Segment();
    point = new Point();
  }

  public void setHighlight(Highlight highlight) {
    if (highlight == null || !highlight.equals(this.highlight)) {
      this.highlight = highlight;
      textArea.invalidateLineRange(0, textArea.getLineCount());
    }
  }

  public void paintValidLine(Graphics2D gfx,
                             int screenLine,
                             int physicalLine,
                             int start,
                             int end,
                             int y) {
    if (highlight != null) {
      highlight(gfx, physicalLine, start, end, y);
    }
  }


  private void highlight(Graphics2D gfx, int physicalLine, int lineStartOffset, int lineEndOffset, int y) {
    String lineContent = textArea.getLineText(physicalLine);
    if (highlight.isRegexp()) {
      SearchMatcher searchMatcher = highlight.getSearchMatcher();
      Segment segment = new Segment(lineContent.toCharArray(), 0, lineContent.length());
      SearchMatcher.Match match = null;
      int i = 0;
      while (true) {
        match = searchMatcher.nextMatch(new CharIndexedSegment(segment, false),
                                        physicalLine == 0,
                                        physicalLine == textArea.getLineCount(),
                                        match == null,
                                        false);
        if (match == null) {
          break;
        }
        String s = lineContent.substring(match.start, match.end);
        _highlight(s, match.start, physicalLine, lineStartOffset + i, lineStartOffset, lineEndOffset, gfx, y);

        if (match.end == lineContent.length()) {
          break;
        }
        lineContent = lineContent.substring(match.end);
        i+= match.end;
        segment = new Segment(lineContent.toCharArray(),0,lineContent.length());

      }
    } else {
      highlightStringInLine(lineContent, highlight.getStringToHighlight(), physicalLine, lineStartOffset, lineStartOffset, lineEndOffset, gfx, y);
    }
  }

  private void highlightStringInLine(String lineString,
                                     String stringToHighlight,
                                     int physicalLine,
                                     int lineStart,
                                     int lineStartOffset, int lineEndOffset,
                                     Graphics2D gfx,
                                     int y) {
    int start = lineString.indexOf(stringToHighlight);
    if (start == -1) return;
    _highlight(stringToHighlight, start, physicalLine, lineStart, lineStartOffset, lineEndOffset, gfx, y);
    while (true) {
      start = lineString.indexOf(stringToHighlight, start + 1);
      if (start == -1) return;
      _highlight(stringToHighlight, start, physicalLine, lineStart, lineStartOffset, lineEndOffset, gfx, y);
    }
  }

  private void _highlight(String stringToHighlight,
                          int start,
                          int physicalLine,
                          int lineStart,
                          int lineStartOffset,
                          int lineEndOffset,
                          Graphics2D gfx,
                          int y) {
    int end = start + stringToHighlight.length();

    if (start == 0 && end == 0) {
      textArea.getLineText(physicalLine, seg);
      for (int j = 0; j < seg.count; j++) {
        if (Character.isWhitespace(seg.array[seg.offset + j])) {
          start++;
        } else {
          break;
        }
      }

      end = seg.count;
    }

    if (start + lineStart >= lineEndOffset || end + lineStart <= lineStartOffset) {
      return;
    }

    //  Gutter gutter = textArea.getGutter();
    // gutter.setBorder(20,Color.red,Color.green,Color.blue);


    final int startX;

    if (start + lineStart >= lineStartOffset) {
      startX = textArea.offsetToXY(physicalLine, start, point).x;
    } else {
      startX = 0;
    }
    final int endX;

    if (end + lineStart >= lineEndOffset) {
      endX = textArea.offsetToXY(physicalLine, lineEndOffset - lineStart - 1, point).x;
    } else {
      endX = textArea.offsetToXY(physicalLine, end, point).x;
    }

    gfx.setColor(highlightColor);
    gfx.fillRect(startX, y, endX - startX, fm.getHeight());
  }
}
