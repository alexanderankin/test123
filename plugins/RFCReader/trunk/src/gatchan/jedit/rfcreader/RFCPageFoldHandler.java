package gatchan.jedit.rfcreader;

import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import javax.swing.text.Segment;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class RFCPageFoldHandler extends FoldHandler
{
    public RFCPageFoldHandler()
    {
        super("rfc-page");
    }

    public int getFoldLevel(JEditBuffer buffer, int lineIndex, Segment seg)
    {
        if(lineIndex == 0)
			return 0;

        int foldLevel = buffer.getFoldLevel(lineIndex - 1);
        buffer.getLineText(lineIndex - 1,seg);
        if (segmentIsPage(seg))
        {
            return foldLevel + 1;
        }
        else if (seg.count == 0)
        {
            buffer.getLineText(lineIndex,seg);
            if (segmentIsPage(seg))
            {
                return Math.max(foldLevel - 1,0);
            }
        }
        return foldLevel;
    }

    private boolean segmentIsPage(Segment seg)
    {
        int offset = seg.offset;
        int count = seg.count;
        if (count < 8)
            return false;

        char[] chars = seg.array;
        if (chars[offset + count - 1] != ']')
            return false;

        int i;
        for (i = count - 2;i>0;i--)
        {
            char c = chars[offset + i];
            if (Character.isWhitespace(c))
            {
                i--;
                break;
            }

            if (!Character.isDigit(c))
                return false;
        }
        if (i < 5)
            return false;

        if (chars[offset + i] == 'e' &&
            chars[offset + i-1] == 'g' &&
            chars[offset + i-2] == 'a' &&
            chars[offset + i-3] == 'P' &&
            chars[offset + i-4] == '[')
        {
            return true;
        }
        return false;
    }
}
