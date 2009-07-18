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

import java.util.Vector;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.text.Segment;

/**
 * {@link RegexCounter} implementation that uses the <code>gnu.regexp</code>
 * classes to perform pattern matching.
 */
public class GNURegexCounter implements FoldCounter
{
	private Pattern startRE;
	private Pattern endRE;
	private int starts;
	private int leadingCloses;
	
	public GNURegexCounter(String startRegex, String endRegex)
		throws IllegalArgumentException
	{
		try
		{
			startRE = Pattern.compile(startRegex);
			endRE   = Pattern.compile(endRegex);
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
		Matcher m = startRE.matcher(seg);
		Vector<MatchResult> startMatches = new Vector<MatchResult>();
		while (m.find())
			startMatches.add(m.toMatchResult());
		int foldStarts = startMatches.size();
		
		m = endRE.matcher(seg);
		Vector<MatchResult> endMatches = new Vector<MatchResult>();
		while (m.find())
			endMatches.add(m.toMatchResult());
		int foldEnds = endMatches.size();
		
		starts = foldStarts - foldEnds;
		
		if(foldStarts == 0 || foldEnds == 0)
			leadingCloses = 0;
		else
		{
			int startIndex = startMatches.firstElement().start();
			int i;
			
			for(i = 0; i < foldEnds; i++)
				if(endMatches.get(i).start() > startIndex)
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
	
}
