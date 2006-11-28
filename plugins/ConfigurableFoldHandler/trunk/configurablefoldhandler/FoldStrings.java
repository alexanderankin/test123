package configurablefoldhandler;

/*
 * FoldStrings.java
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

import org.gjt.sp.util.Log;
import gnu.regexp.RE;
import gnu.regexp.REException;

/**
 * This class holds a pair of strings that define the start and ends of folds
 */
public class FoldStrings
{
	private String foldStart;
	private String foldEnd;
	private RE startRE;
	private RE endRE;
	private boolean doFolding;
	private boolean useRegex;
	
	/**
	 * Sets the strings that will be used to determine the start and end of a
	 * folding section. These can be any strings with the following
	 * restrictions:
	 * <ul>
	 * <li>neither can be an empty string</li>
	 * <li>neither can be be a substring of the other</li>
	 * </ul>
	 * 
	 * @param startStr the string that starts a folded section
	 * @param endStr   the string that ends a folded section
	 */
	public FoldStrings(String startStr, String endStr, boolean useRegex)
	{
		boolean foldChanged;
		
		this.useRegex = useRegex;
		
		if(foldStart != startStr || foldEnd != endStr)
			foldChanged = true;
		else
			foldChanged = false;
		
		foldStart = startStr;
		foldEnd   = endStr;
		
		if(foldStart.equals("") || foldEnd.equals(""))
		{
			Log.log(Log.ERROR, this, "fold start and end strings cannot " +
				"be empty. configurable folding disabled");
			
			doFolding = false;
		}
		/* else if(foldEnd.indexOf(foldStart) != -1 ||
			foldStart.indexOf(foldEnd) != -1)
		{
			Log.log(Log.ERROR, this, "one fold limit string is a " +
				"substring of the other. configurable folding disabled");
			
			doFolding = false;
		} */
		else
		{
			doFolding = true;
			
			if(useRegex)
			{
				try
				{
					startRE = new RE(startStr);
					endRE   = new RE(endStr);
				}
				catch(REException e)
				{
					Log.log(Log.ERROR, this, "could not create regular " +
						"expressions. configurable folding disabled");
					
					doFolding = false;
				}
			}
		}
	}
	
	public FoldStrings(String startStr, String endStr)
	{
		this(startStr, endStr, false);
	}
	
	/**
	 * copy constructor
	 */
	public FoldStrings(FoldStrings fs)
	{
		this(fs.getStartString(), fs.getEndString(), fs.useRegex());
	}
	
	public boolean equals(Object obj)
	{
		if(obj == null || !obj.getClass().equals(getClass()))
			return false;
		
		FoldStrings fs = (FoldStrings)obj;
		
		if(fs.foldStart.equals(foldStart) && fs.foldEnd.equals(foldEnd) &&
			(fs.useRegex == useRegex))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String toString()
	{
		return "FoldStrings [foldStart: " + foldStart + ", foldEnd: " +
			foldEnd + ", useRegex: " + useRegex + "]";
	}
	
	public String getStartString() { return foldStart; }
	public String getEndString()   { return foldEnd; }
	
	/**
	 * signals whether this object represents a valid pair of fold strings. if
	 * it does not (if one of the strings is empty for example) then this method
	 * will return false and the fold handler will disable folding
	 */
	public boolean doFolding()     { return doFolding; }
	
	/**
	 * returns true if regular expressions should be used to determine the start
	 * and end of folds
	 */
	public boolean useRegex()      { return useRegex; }
	
	/**
	 * returns the regular expression for the start of folding. if regular
	 * expressions are not enabled then null is returned
	 */
	public RE getStartRegex()      { return startRE; }
	
	/**
	 * returns the regular expression for the end of folding. if regular
	 * expressions are not enabled then null is returned
	 */
	public RE getEndRegex()        { return endRE; }
}
