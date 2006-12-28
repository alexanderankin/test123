package configurablefoldhandler;

/*
 * PlainCounter.java
 * 
 * Copyright (c) 2002 C.J.Kent
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=custom:collapseFolds=0:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import javax.swing.text.Segment;

/**
 * {@link FoldCounter} implementation that uses simple string matching to count
 * folds.
 */
public class PlainCounter implements FoldCounter
{
	private int starts;
	private int leadingCloses;
	private String foldStart;
	private String foldEnd;
	
	public PlainCounter(String foldStart, String foldEnd)
	{
		this.foldStart = foldStart;
		this.foldEnd   = foldEnd;
	}
	
	/**
	 * Counts the folds in <code>seg</code>.
	 */
	public void count(Segment seg)
	{
		starts        = 0;
		leadingCloses = 0;
		
		boolean leadingClosesFound = false;
		
		int ends = 0;
		int offset = seg.offset;
		int count  = seg.count;

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
					
					// only want to do this when we find the first end
					if(!leadingClosesFound)
					{
						leadingCloses = ends;
						leadingClosesFound = true;
					}
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
		starts -= ends;
	}
	
	/**
	 * Returns the total starts on <code>line</code>. This is the number of fold
	 * start sequences minus the number of fold end sequences.
	 */
	public int getStarts()
	{
		return starts;
	}
	
	/**
	 * Returns the number of fold end sequences that occur on <code>line</code>
	 * before the first fold start sequence.
	 */
	public int getLeadingCloses()
	{
		return leadingCloses;
	}
}
