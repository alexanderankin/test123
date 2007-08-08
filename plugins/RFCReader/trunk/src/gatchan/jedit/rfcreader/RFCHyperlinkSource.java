package gatchan.jedit.rfcreader;

import gatchan.jedit.hyperlinks.Hyperlink;
import gatchan.jedit.hyperlinks.HyperlinkSource;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;

/**
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class RFCHyperlinkSource implements HyperlinkSource
{
	private static final String NO_WORD_SEP = "[]";

	private Hyperlink currentLink;

	public Hyperlink getHyperlink(Buffer buffer, int caretPosition)
	{
		if (currentLink != null)
		{
			if (currentLink.getStartOffset() <= caretPosition && currentLink.getEndOffset() >= caretPosition)
			{
				return currentLink;
			}
		}
		int line = buffer.getLineOfOffset(caretPosition);
		int lineStart = buffer.getLineStartOffset(line);
		int lineLength = buffer.getLineLength(line);
		if (lineLength == 0)
			return null;
		int offset = caretPosition - lineStart;
		String lineText = buffer.getLineText(line);
		if (offset == lineLength)
			offset--;


		int wordStart = TextUtilities.findWordStart(lineText, offset,
							    NO_WORD_SEP, true, false, false);
		int wordEnd = TextUtilities.findWordEnd(lineText, offset + 1,
							NO_WORD_SEP, true, false, false);


		String currentWord = lineText.substring(wordStart, wordEnd).toLowerCase();
		if (!currentWord.startsWith("[rfc") || currentWord.charAt(currentWord.length() - 1) != ']')
		{
			return null;
		}
		for (int i = 4; i < currentWord.length() - 1; i++)
		{
			if (!Character.isDigit(currentWord.charAt(i)))
				return null;
		}
		int rfcNum = Integer.parseInt(currentWord.substring(4, currentWord.length() - 1));

		currentLink = new RFCHyperlink(lineStart + wordStart + 1, lineStart + wordEnd - 1, line,"rfc"+ rfcNum, rfcNum);
		return currentLink;
	}
}
