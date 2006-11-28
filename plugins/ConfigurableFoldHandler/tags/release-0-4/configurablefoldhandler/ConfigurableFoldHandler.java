package configurablefoldhandler;
/*
 * 
 *
 * :folding=custom:collapseFolds=0:
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import javax.swing.text.Segment;
import java.util.Hashtable;
import java.util.Enumeration;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.FoldHandler;
import gnu.regexp.RE;
import gnu.regexp.REMatch;

/**
 * fold handler that allows the user to specify a pair of strings that define
 * the start and end of a fold
 */
public class ConfigurableFoldHandler extends FoldHandler
{
	public static final FoldStrings DEFAULT_FOLD_STRINGS =
		new FoldStrings("{", "}");
	
	private static final int FOLD_LEVEL     = 0;
	private static final int LEADING_CLOSES = 1;
	
	// default fold strings for modes / buffers that have none specified
	private FoldStrings defFoldStrings;
	
	// store the fold strings for specific buffers and edit modes
	private Hashtable bufferStrings;
	private Hashtable modeStrings;
	
	private StringBuffer sb = new StringBuffer();
	
	public ConfigurableFoldHandler()
	{
		super("custom");
		bufferStrings = new Hashtable();
		modeStrings   = new Hashtable();
	}
	
	public ConfigurableFoldHandler(FoldStrings defFoldStrings,
		Hashtable modeStrings, Hashtable bufferStrings)
	{
		super("custom");
		this.defFoldStrings = new FoldStrings(defFoldStrings);
		this.modeStrings    = new Hashtable(modeStrings);
		this.bufferStrings  = new Hashtable(bufferStrings);
	}
	
	public ConfigurableFoldHandler(ConfigurableFoldHandler cfh)
	{
		this(cfh.defFoldStrings, cfh.modeStrings, cfh.bufferStrings);
	}
	
	public ConfigurableFoldHandler(Hashtable allBufferStrings)
	{
		super("custom");
		this.bufferStrings = new Hashtable(allBufferStrings);
		this.modeStrings   = new Hashtable();
	}
	
	/**
	 * Returns the fold level of the specified line.
	 * @param buffer The buffer in questionTO
	 * @param lineIndex The line index
	 * @param seg A segment the fold handler can use to obtain any
	 * text from the buffer, if necessary
	 * @return The fold level of the specified line
	 */
	public int getFoldLevel(Buffer buffer, int lineIndex, Segment seg)
	{
		if(lineIndex == 0)
			return 0;
		
		int folds      = countFolds(buffer, lineIndex, seg, FOLD_LEVEL);
		int c1 = countFolds(buffer, lineIndex, seg, LEADING_CLOSES);
		int c2 = countFolds(buffer, lineIndex + 1, seg, LEADING_CLOSES);
		
		int foldLevel = buffer.getFoldLevel(lineIndex - 1)
			+ ((folds + c1 - c2) * buffer.getTabSize());
		
		return Math.max(0, foldLevel);
	}
	
	private int countFolds(Buffer buffer, int lineIndex, Segment seg, int type)
	{
		// TODO: allow substrings e.g. Function, End Function
		
		int starts = 0;
		int ends   = 0;
		
		FoldStrings foldStrings = (FoldStrings)bufferStrings.get(buffer);
		
		if(foldStrings == null)
		{
			foldStrings = (FoldStrings)modeStrings.get(
				buffer.getMode().getName());
		}
		
		if(foldStrings == null)
			foldStrings = defFoldStrings;
		
		 if(!foldStrings.doFolding())
			 return 0;
		
		String foldStart = foldStrings.getStartString();
		String foldEnd   = foldStrings.getEndString();
		
		buffer.getLineText(lineIndex - 1, seg);

		if(!foldStrings.useRegex())
		{
			int offset = seg.offset;
			int count = seg.count;

			int startCount = 0;
			int endCount   = 0;
			
			char curChar;
			
			for(int i = 0; i < count; i++)
			{
				curChar = seg.array[offset + i];
				
				if(curChar == foldStart.charAt(startCount))
				{
					startCount++;
					
					if(startCount == foldStart.length())
					{
						// start of fold
						
						if(type == LEADING_CLOSES)
							return ends;
						
						starts++;
						
						startCount = 0;
						endCount   = 0;
					}
				}
				else
				{
					startCount = 0;
				}
				
				if(curChar == foldEnd.charAt(endCount))
				{
					endCount++;
					
					if(endCount == foldEnd.length())
					{
						// end of fold
						ends++;
						
						endCount   = 0;
						startCount = 0;
					}
				}
				else
				{
					endCount = 0;
				}
			}
            if(type == FOLD_LEVEL)
                return starts - ends;
            else
                return 0;
		}
		else
		{
			REMatch[] startMatches;
			REMatch[] endMatches;
			RE regex;
			
			sb.setLength(0);
			sb.append(seg.array, seg.offset, seg.count);
			
			startMatches = foldStrings.getStartRegex().getAllMatches(sb);
			starts       = startMatches.length;
			
			endMatches = foldStrings.getEndRegex().getAllMatches(sb);
			ends       = endMatches.length;
			
            if(type == FOLD_LEVEL)
			    return (starts - ends);
            else
                return getLeadingCloses(startMatches, endMatches);
		}
	}
	
	/**
	 * Returns the number of fold closes that appear before the first opener
	 */
	private int getLeadingCloses(REMatch[] opens, REMatch[] closes)
	{
		if(opens.length == 0 || closes.length == 0)
			return 0;
		
		int startIndex = opens[0].getStartIndex();
		
		int i;
		for(i = 0; i < closes.length; i++)
			if(closes[i].getStartIndex() > startIndex)
				break;
		
		return i;
	}
	
	/**
	 * sets the fold strings that will be used for a buffer whose mode has no
	 * fold strings specified and that has no strings of its own.
	 */
	public void setDefaultFoldStrings(FoldStrings foldStrings)
	{
		defFoldStrings = foldStrings;
	}
	
	/**
	 * specifies fold strings for the buffer
	 */
	public void setBufferFoldStrings(Buffer buffer, FoldStrings foldStrings)
	{
		if(foldStrings == null) {
			bufferStrings.remove(buffer);
		} else {
			bufferStrings.put(buffer, foldStrings);
		}
	}
	
	/**
	 * specifies fold strings for the named mode
	 */
	public void setModeFoldStrings(String modeName, FoldStrings foldStrings)
	{
		modeStrings.put(modeName, foldStrings);
	}
	
	/**
	 * returns true if the default fold strings match and the maps containing
	 * buffer and mode fold strings are equal
	 */
	public boolean equals(Object obj)
	{
		if(obj == null || !obj.getClass().equals(getClass()))
			return false;
		
		ConfigurableFoldHandler cfh = (ConfigurableFoldHandler)obj;
		
		if(cfh.defFoldStrings.equals(defFoldStrings) &&
			cfh.bufferStrings.equals(bufferStrings) &&
			cfh.modeStrings.equals(modeStrings))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * returns the fold strings specific to this buffer
	 * 
	 * @return the fold strings specific to this buffer, or null if no strings
	 * have been specified for this buffer
	 */
	public FoldStrings getBufferFoldStrings(Buffer buffer)
	{
		return (FoldStrings)bufferStrings.get(buffer);
	}
	
	/**
	 * checks if any of the buffers with their own fold strings have closed and
	 * if so removes references to them from bufferStrings
	 */
	void checkBuffers()
	{
		Buffer[] buffers = jEdit.getBuffers();
		Enumeration enum = bufferStrings.keys();
		Buffer curBuffer;
		int i;
		
loop:	while(enum.hasMoreElements())
		{
			curBuffer = (Buffer)enum.nextElement();
			
			for(i = 0; i < buffers.length; i++)
				if(buffers[i] == curBuffer)
					continue loop;
			
			bufferStrings.remove(curBuffer);
		}
	}
	
	/**
	 * returns the hashtable that maps buffers to their fold strings
	 */
	public Hashtable getAllBufferFoldStrings() { return bufferStrings; }
	
	/**
	 * Returns the {@link FoldStrings} used for any <code>Buffer</code> that
	 * doesn't have a specific set of strings specified and whose mode doesn't
	 * either
	 *
	 * @return the {@link FoldStrings} used for any <code>Buffer</code> that
	 * doesn't have a specific set of strings specified and whose mode doesn't
	 * either
	 */
	public FoldStrings getDefaultFoldStrings() { return defFoldStrings; }
	
	/**
	 * Returns the {@link FoldStrings} for the specified edit mode. If there are
	 * no strings for the specified mode or the name doesn't correspond to a
	 * valid mode then the default fold strings will be returned
	 *
	 * @param modeName the name of the mode whose fold strings are required
	 * @return the {@link FoldStrings} for the specified mode or
	 * <code>null</code> if none are set for that mode (or if the mode name isn't
	 * valid)
	 */
	public FoldStrings getModeFoldStrings(String modeName)
	{
		return (FoldStrings)modeStrings.get(modeName);
	}
}
