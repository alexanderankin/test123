package configurablefoldhandler;

/*
 * JavaRegexCounter.java
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

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.Segment;

/**
 * {@link RegexCounter} implementation that uses the
 * <code>java.util.regex</code> classes to perform pattern matching.
 */
public class JavaRegexCounter implements FoldCounter
{
	private Pattern startPattern;
	private Pattern endPattern;
	private int starts;
	private int leadingCloses;
	
	public JavaRegexCounter(String startRegex, String endRegex)
		throws IllegalArgumentException
	{
		try
		{
			startPattern = Pattern.compile(startRegex);
			endPattern   = Pattern.compile(endRegex);
		}
		catch(PatternSyntaxException ex)
		{
			throw new IllegalArgumentException(ex.getMessage());
		}
	}
	
	/**
	 * Counts the folds on <code>line</code>.
	 */
	public void count(Segment seg)
	{
		CharSequence sequence = new SegmentSequence(seg);
		Matcher startMatcher  = startPattern.matcher(sequence);
		Matcher endMatcher    = endPattern.matcher(sequence);
		
		int foldStarts = 0;
		int firstStart = 0;
		
		// count the fold starts and remember where the first one starts
		if(startMatcher.find())
		{
			foldStarts = 1;
			firstStart = startMatcher.start();
			
			while(startMatcher.find())
			{
				foldStarts++;
			}
		}
		leadingCloses = 0;
		int foldEnds = 0;
		
		while(endMatcher.find())
		{
			foldEnds++;
			if(endMatcher.start() < firstStart)
			{
				leadingCloses++;
			}
		}
		starts = foldStarts - foldEnds;
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
	 * <code>CharSequence</code> implementation backed by a
	 * <code>Segment</code>.
	 */
	private static class SegmentSequence implements CharSequence
	{
		private Segment seg;
		private int offset;
		private int count;
		
		SegmentSequence(Segment seg, int offset, int count)
		{
			this.seg    = seg;
			this.offset = seg.offset + offset;
			this.count  = count;
		}
		
		SegmentSequence(Segment seg)
		{
			this.seg = seg;
			offset   = seg.offset;
			count    = seg.count;
		}
		
		public char charAt(int index)
		{
			return seg.array[offset + index];
		}
		
		public CharSequence subSequence(int start, int end)
		{
			return new SegmentSequence(seg, offset + start, end - start);
		}
		
		public String toString()
		{
			return new String(seg.array, offset, count);
		}
		
		public int length() { return count; }
	}
}
