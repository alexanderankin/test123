package gatchan.jedit.rfcreader;

import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import javax.swing.text.Segment;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class RFCChaptersFoldHandler extends FoldHandler
{
    public RFCChaptersFoldHandler()
    {
        super("rfc-chapters");
    }

    public int getFoldLevel(JEditBuffer buffer, int lineIndex, Segment seg)
    {
        if(lineIndex == 0)
			return 0;

        int foldLevel = buffer.getFoldLevel(lineIndex - 1);
        buffer.getLineText(lineIndex - 1,seg);
        if (segmentIsChapter(seg))
        {
            return foldLevel + 1;
        }
        else if (seg.count == 0)
        {
            buffer.getLineText(lineIndex,seg);
            if (segmentIsChapter(seg))
            {
                return Math.max(foldLevel - 1,0);
            }
        }
        return foldLevel;
    }

    private boolean segmentIsChapter(Segment seg)
    {
        if (seg.count < 3)
            return false;

        return Character.isDigit(seg.array[seg.offset]);
    }
}