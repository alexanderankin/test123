package xml.parser;

import java.util.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;

public class TwoWayTokenIterator
{
	private int currentLine;
	private int currentLineStart;
	private List<Token> currentTokens;
	private int iCurrentToken;
	private JEditBuffer buf;
	private boolean atBegin,atEnd;
	
	public TwoWayTokenIterator(JEditBuffer buf, int startPos){
		this.buf = buf;
		currentLine = buf.getLineOfOffset(startPos);
		currentTokens = new ArrayList<Token>();
		parseLine(true);
		
		iCurrentToken = -1;
		int lineStart = currentLineStart;
		for(int i=0;iCurrentToken==-1 && i<currentTokens.size();i++)
		{
			Token token = currentTokens.get(i);
			int next = lineStart + token.length;
			if (lineStart <= startPos && next > startPos)
			{
				iCurrentToken = i;
			}
			else if(lineStart == startPos)// when caret is at end of line !
			{
				iCurrentToken = i;
			}
			else
			{
				lineStart = next;
				token = token.next;
			}
		}
		atBegin = iCurrentToken == -1;
		atEnd = atBegin;
	}
	
	public boolean previous()
	{
		if(atBegin)return false;
		
		if(iCurrentToken>0)
		{
			iCurrentToken--;
			atEnd = false;
			return true;
		}
		else
		{
			if(currentLine > 0)
			{
				currentLine--;
				return parseLine(true);
			}
			else
			{
				atBegin = true;
				return false;
			}
		}
	}
	
	public boolean next()
	{
		if(atEnd)return false;
		if(iCurrentToken<currentTokens.size()-1)
		{
			iCurrentToken++;
			atBegin = false;
			return true;
		}
		else
		{
			if(currentLine < buf.getLineCount()-1)
			{
				currentLine++;
				return parseLine(false);
			}
			else
			{
				return false;
			}
		}
	}

	public Token current()
	{
		if(atEnd||atBegin)return null;
		return currentTokens.get(iCurrentToken);
	}
	
	public String getCurrentText()
	{
		if(atEnd||atBegin)return null;
		return buf.getText(currentLineStart+currentTokens.get(iCurrentToken).offset,
			+currentTokens.get(iCurrentToken).length);
	}
	
	public int getCurrentCaret()
	{
		if(atEnd||atBegin)return -1;
		else return currentLineStart + current().offset;
	}
	
	private boolean parseLine(boolean backward)
	{
		DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
		buf.markTokens(currentLine,tokenHandler);
		currentTokens.clear();
		Token token = tokenHandler.getTokens();

		while(token.id != Token.END)
		{
			currentTokens.add(token);
			token = token.next;
		}
		// add END token, otherwise, can't initialize the iterator
		// at end of line
		currentTokens.add(token);
		//emptyLine
		if(currentTokens.isEmpty())
		{
			if(backward)
			{
				if(currentLine > 0)
				{
					currentLine = currentLine - 1;
					return parseLine(backward);
				}
				else return false;
			}
			else
			{
				if(currentLine < buf.getLineCount()-1)
				{
					currentLine = currentLine + 1;
					return parseLine(backward);
				}
				else return false;
			}
		}
		else
		{
			if(backward)
			{
				iCurrentToken = currentTokens.size()-1;
				atEnd = false;
			}
			else
			{
				iCurrentToken = 0;
				atBegin = false;
			}
			
			currentLineStart = buf.getLineStartOffset(currentLine);
			return true;
		}
	}
}
