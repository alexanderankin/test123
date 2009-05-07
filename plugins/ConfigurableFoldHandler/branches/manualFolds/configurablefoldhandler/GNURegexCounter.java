package configurablefoldhandler;

/*
 * GNURegexCounter.java
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

import java.io.InputStream;
import javax.swing.text.Segment;
import gnu.regexp.RE;
import gnu.regexp.REMatch;
import gnu.regexp.REException;

/**
 * {@link RegexCounter} implementation that uses the <code>gnu.regexp</code>
 * classes to perform pattern matching.
 */
public class GNURegexCounter implements FoldCounter
{
	private RE startRE;
	private RE endRE;
	private int starts;
	private int leadingCloses;
	
	public GNURegexCounter(String startRegex, String endRegex)
		throws IllegalArgumentException
	{
		try
		{
			startRE = new RE(startRegex);
			endRE   = new RE(endRegex);
		}
		catch(REException ex)
		{
			throw new IllegalArgumentException(ex.getMessage());
		}
	}
	
	/**
	 * Counts the folds on <code>line</code>.
	 */
	public void count(Segment seg)
	{
		SegmentInputStream segmentStream = new SegmentInputStream(seg);
		REMatch[] startMatches;
		REMatch[] endMatches;
		
		startMatches   = startRE.getAllMatches(segmentStream);
		int foldStarts = startMatches.length;
		
		segmentStream.restartStream();
		
		endMatches   = endRE.getAllMatches(segmentStream);
		int foldEnds = endMatches.length;
		
		starts = foldStarts - foldEnds;
		
		if(startMatches.length == 0 || endMatches.length == 0)
			leadingCloses = 0;
		else
		{
			int startIndex = startMatches[0].getStartIndex();
			int i;
			
			for(i = 0; i < endMatches.length; i++)
				if(endMatches[i].getStartIndex() > startIndex)
					break;
			
			leadingCloses = i;
		}
	}
	
	/**
	 * Returns the total starts on <code>line</code>. This is the number of fold
	 * start sequences minus the number of fold end sequences.
	 */
	public int getStarts() { return starts; }
	
	/**
	 * Returns the number of fold end sequences that occur on <code>line</code>
	 * before the first fold start sequence.
	 */
	public int getLeadingCloses() { return leadingCloses; }
	
	/**
	 * <code>InputStream</code> that reads characters from a
	 * <code>Segment</code>.
	 */
	private static class SegmentInputStream extends InputStream
	{
		private Segment seg;
		private int index;
		private int end;
		
		SegmentInputStream(Segment seg)
		{
			this.seg = seg;
			index    = seg.offset;
			end      = seg.offset + seg.count;
		}
		
		public int read()
		{
			if(index == end) 
				return -1;
			else
				return seg.array[index++];
		}
		
		public int read(byte[] b)
		{
			if(index == end)
				return -1;
			
			int numBytes = Math.min(b.length, available());
			/* System.arraycopy(seg.array, index, b, 0, numBytes); */
			for(int i = 0; i < numBytes; i++)
			{
				b[i] = (byte) seg.array[index + i];
			}
			index += numBytes;
			return numBytes;
		}
		
		public int read(byte[] b, int offset, int count)
		{
			if(index == end)
				return -1;
			
			int numBytes = Math.min(count, available());
			/* System.arraycopy(seg.array, index, b, offset, numBytes); */
			for(int i = 0; i < numBytes; i++)
			{
				b[offset + i] = (byte) seg.array[index + i];
			}
			index += numBytes;
			return numBytes;
		}
		
		public int available()
		{
			return end - index;
		}
		
		public void skip(int n)
		{
			index += Math.min(n, available());
		}
		
		public void restartStream()
		{
			index = seg.offset;
		}
		
		public boolean markSupported() { return false; }
		
		public void mark()  { throw new UnsupportedOperationException(); }
		public void reset() { throw new UnsupportedOperationException(); }
	}
}
