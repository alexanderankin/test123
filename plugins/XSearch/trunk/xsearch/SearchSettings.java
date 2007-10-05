/*
 * SearchSettings.java 
 * :tabSize=2:indentSize=2:noTabs=false:
 * :folding=explicit:collapseFolds=5:
 *
 * Copyright (C) 2002 Rudolf Widmann
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

// package org.gjt.sp.jedit.search;
package xsearch;

import java.util.EmptyStackException;
import java.util.Stack;


import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.search.AllBufferSet;
import org.gjt.sp.jedit.search.CurrentBufferSet;
import org.gjt.sp.jedit.search.DirectoryListSet;
import org.gjt.sp.jedit.search.SearchFileSet;
import org.gjt.sp.util.Log;

/**
 * Class that saves following XSearch settings in a stack
 		regexp
		ignoreCase
		reverse
		fromTop
		beanshell
		wrap
		columnSearchEnabled
		columnSearchExpandTabs
		columnSearchLeftCol
		columnSearchRightCol
		rowSearchEnabled
		rowSearchLeftRow
		rowSearchRightRow
		commentSearch
		foldSearch
		wordPart
		tentativSearch
 * @author R. Widmann
 * @version $Id$
 */
public class SearchSettings
{
	public static final int SEARCH_SETTINGS_SIZE = 30;
	/**
	 * SearchSettings constructor: construct with current settings
	 */
  public SearchSettings() {
	}
  public SearchSettings(String setString) {
		loadString(setString);
	}
	/**
	 * loadString: construct SearchSettings with settings passed via string
	 * Layout of SearchSettings string:
	 *  0: regexp
	 *  1: ignoreCase
	 *  2: reverse
	 *  3: fromTop
	 *  4: beanshell
	 *  5: wrap
	 *  6: columnSearchEnabled
	 *  7: columnSearchExpandTabs
	 *  8..10: columnSearchLeftCol
	 * 11..13: columnSearchRightCol
	 * 14: rowSearchEnabled
	 * 15..20: rowSearchLeftRow
	 * 21..26: rowSearchRightRow
	 * 27: commentSearch
	 * 28: foldSearch
	 * 29: wordPart
	 * 30..34: length filter
	 * 35..x: filter
	 * x+1..x+6: length directory
	 * x+7..y: directory
	 * y+1: subdirectory
	 */
  public boolean loadString(String setString) {
		if (setString.length() < SEARCH_SETTINGS_SIZE) {
			Log.log(Log.DEBUG, BeanShell.class,"+++ SearchSettings.95: setString.length() = "+setString.length());
			return false;
		}
		SearchSettings ss = new SearchSettings();
		try {
			//load settings
			regexp = setString.charAt(0) == '1';
			ignoreCase = setString.charAt(1) == '1';
			reverse = setString.charAt(2) == '1';
			fromTop = setString.charAt(3) == '1';
			beanshell = setString.charAt(4) == '1';
			wrap = setString.charAt(5) == '1';
			columnSearchEnabled = setString.charAt(6) == '1';
			columnSearchExpandTabs = setString.charAt(7) == '1';
			columnSearchLeftCol = Integer.parseInt(setString.substring(8,11));
			columnSearchRightCol = Integer.parseInt(setString.substring(11,14));
			rowSearchEnabled = setString.charAt(14) == '1';
			rowSearchLeftRow = Integer.parseInt(setString.substring(15,21));
			rowSearchRightRow = Integer.parseInt(setString.substring(21,27));
			commentSearch = Character.getNumericValue(setString.charAt(27));
			foldSearch = Character.getNumericValue(setString.charAt(28));
			wordPart = Character.getNumericValue(setString.charAt(29));
			tentativSearch = false;  // default
			int ofs = 30;
			int filterLength = Integer.parseInt(setString.substring(ofs, ofs+5));
			ofs += 5;
			if (filterLength == 0)
				fileset = new CurrentBufferSet();
			else {
				String filter = setString.substring(ofs, ofs+filterLength);
				ofs += filterLength;
				int dirLength = Integer.parseInt(setString.substring(ofs, ofs+5));
				ofs += 5;
				if (dirLength == 0)
					fileset = new AllBufferSet(filter);
				else {
					String dir = setString.substring(ofs, ofs+dirLength);
					ofs += dirLength;
					boolean recurse = setString.charAt(ofs) == '1';
					fileset = new DirectoryListSet(dir, filter, recurse);
				}
			}
			//Log.log(Log.DEBUG, BeanShell.class,"+++ SearchSettings.130: fileset = "+fileset);
		}
		catch(IndexOutOfBoundsException e)
		{
			Log.log(Log.DEBUG, BeanShell.class,"+++ SearchSettings.138: IndexOutOfBoundsException for "+setString);
			return false;
		}
		catch(NumberFormatException e)
		{
			Log.log(Log.DEBUG, BeanShell.class,"+++ SearchSettings.142: NumberFormatException for "+setString);
			return false;
		}
		//Log.log(Log.DEBUG, BeanShell.class,"+++ SearchSettings.143: loaded "+setString);
		return true;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(regexp ? "1" : "0");
		sb.append(ignoreCase ? "1" : "0");
		sb.append(reverse ? "1" : "0");
		sb.append(fromTop ? "1" : "0");
		sb.append(beanshell ? "1" : "0");
		sb.append(wrap ? "1" : "0");
		sb.append(columnSearchEnabled ? "1" : "0");
		sb.append(columnSearchExpandTabs ? "1" : "0");
		sb.append(fillNumberWithLeadingZeros(columnSearchLeftCol,3));
		sb.append(fillNumberWithLeadingZeros(columnSearchRightCol,3));
		sb.append(rowSearchEnabled ? "1" : "0");
		sb.append(fillNumberWithLeadingZeros(rowSearchLeftRow,6));
		sb.append(fillNumberWithLeadingZeros(rowSearchRightRow,6));
		sb.append(Integer.toString(commentSearch));
		sb.append(Integer.toString(foldSearch));
		sb.append(Integer.toString(wordPart));

		if (fileset instanceof CurrentBufferSet)
			sb.append("00000");
		else if (fileset instanceof AllBufferSet) {
			sb.append(fillNumberWithLeadingZeros(((AllBufferSet)fileset).getFileFilter().length(),5));
			sb.append(((AllBufferSet)fileset).getFileFilter());
			sb.append("00000");
		}
		else { // DirectoryListSet
			sb.append(fillNumberWithLeadingZeros(((DirectoryListSet)fileset).getFileFilter().length(),5));
			sb.append(((DirectoryListSet)fileset).getFileFilter());
			sb.append(fillNumberWithLeadingZeros(((DirectoryListSet)fileset).getDirectory().length(),5));
			sb.append(((DirectoryListSet)fileset).getDirectory());
			sb.append(((DirectoryListSet)fileset).isRecursive() ? "1" : "0");
		}
		return sb.toString();
	}

	public static String fillNumberWithLeadingZeros(int nbr, int size) {
		String numStr = Integer.toString(nbr);
		if (numStr.length() < size) {
			StringBuffer nulls = new StringBuffer();
			for (int i = numStr.length(); i<size; i++)
				nulls.append(0);
			return nulls.toString() + numStr;
		}
		else
			return numStr;
	}
		
	private static Stack settingList = new Stack();
	/**
	 * Copy the actual XSearch settings and pushes it in a stack
	 */
	public static void push() {
		SearchSettings currSs = new SearchSettings();
		currSs.load();
		settingList.push(currSs);
	}
	/**
	 * Copy the last pushed settings into XSearch settings
	 * throws EmptyStackException
	 */
	public static void pop() throws EmptyStackException {
		SearchSettings currSs = (SearchSettings)settingList.pop();
		currSs.update();
	}
	//{{{ reset
	/**
	 * Reset search settings to default values
	 * Default = forward, no options
	 */
	public static void resetSettings() {
		//Log.log(Log.DEBUG, BeanShell.class,"+++ SearchSettings.186: reset settings");
		SearchAndReplace.setRegexp(false);
		SearchAndReplace.setIgnoreCase(false);
		SearchAndReplace.setReverseSearch(false);  
		SearchAndReplace.setSearchFromTop(false);  
		SearchAndReplace.resetIgnoreFromTop();  
		SearchAndReplace.setBeanShellReplace(false);
		SearchAndReplace.setAutoWrapAround(false);	    
		SearchAndReplace.resetColumnSearch();
		SearchAndReplace.resetRowSearch();
		SearchAndReplace.setCommentOption(XSearch.SEARCH_IN_OUT_NONE);
		SearchAndReplace.setFoldOption(XSearch.SEARCH_IN_OUT_NONE);
		SearchAndReplace.setWordPartOption(XSearch.SEARCH_PART_NONE);
		SearchAndReplace.setTentativOption(false);
		SearchAndReplace.setHyperRange(-1, -1);
		SearchAndReplace.setSearchFileSet(new CurrentBufferSet());
	//}}}
	}
	
	public static void setPopAfterHypersearch(boolean value) {
		popAfterHypersearch = value;
	}
	
	public static boolean getPopAfterHypersearch() {
		return popAfterHypersearch;
	}
	
	private static boolean popAfterHypersearch = false;
	//{{{ Private members

	//{{{ Instance variables
	//private String search;
	//private String origSearch;
	//private String replace;
	private boolean regexp;
	private boolean ignoreCase;
	private boolean reverse;     // search backward
	private boolean fromTop;     // search from top
	private boolean beanshell;   // use bean shell snippet
	private boolean wrap;	    // wrap search
	//private boolean ignoreFromTop;	  // rwchg: ignore "fromTop", when multiple "find" invoked
	//private SearchMatcher matcher;
	private SearchFileSet fileset;
	private boolean columnSearchEnabled;
	private boolean columnSearchExpandTabs;
	private int columnSearchLeftCol;
	private int columnSearchRightCol;
	private boolean rowSearchEnabled;
	private int rowSearchLeftRow;
	private int rowSearchRightRow;
	private int commentSearch = XSearch.SEARCH_IN_OUT_NONE;
	private int foldSearch = XSearch.SEARCH_IN_OUT_NONE;
	private int wordPart = XSearch.SEARCH_PART_NONE;
	private boolean tentativSearch;
	// ### fileset extensions
	//private boolean searchSelection; not present in XSearch
	
	
	//}}}
	//{{{ load
	public void load() {
 		// search = SearchAndReplace.search;
		// origSearch = SearchAndReplace.origSearch;
		// replace = SearchAndReplace.replace;
		// ignoreFromTop = SearchAndReplace.getIgnoreCase();
		// matcher = SearchAndReplace.matcher;

		regexp = SearchAndReplace.getRegexp();
		ignoreCase = SearchAndReplace.getIgnoreCase();
		reverse = SearchAndReplace.getReverseSearch();  
		fromTop = SearchAndReplace.getSearchFromTop();  
		beanshell = SearchAndReplace.getBeanShellReplace();
		wrap = SearchAndReplace.getAutoWrapAround();	    
		columnSearchEnabled = SearchAndReplace.getColumnOption();
		columnSearchExpandTabs = SearchAndReplace.getColumnExpandTabsOption();
		columnSearchLeftCol = SearchAndReplace.getColumnLeftCol();
		columnSearchRightCol = SearchAndReplace.getColumnRightCol();
		rowSearchEnabled = SearchAndReplace.getRowOption();
		rowSearchLeftRow = SearchAndReplace.getRowLeftRow();
		rowSearchRightRow = SearchAndReplace.getRowRightRow();
		commentSearch = SearchAndReplace.getCommentOption();
		foldSearch = SearchAndReplace.getFoldOption();
		wordPart = SearchAndReplace.getWordPartOption();
		tentativSearch = SearchAndReplace.getTentativOption();
		fileset = cloneFileset(SearchAndReplace.getSearchFileSet());
	//}}}
	}
	//{{{ update
	public void update() {
		SearchAndReplace.setRegexp(regexp);
		SearchAndReplace.setIgnoreCase(ignoreCase);
		SearchAndReplace.setReverseSearch(reverse);  
		SearchAndReplace.setSearchFromTop(fromTop);  
		SearchAndReplace.setBeanShellReplace(beanshell);
		SearchAndReplace.setAutoWrapAround(wrap);	    
		if (columnSearchEnabled) SearchAndReplace.setColumnSearchOptions(columnSearchExpandTabs,
			columnSearchLeftCol, columnSearchRightCol);
		else SearchAndReplace.resetColumnSearch();
		if (rowSearchEnabled) SearchAndReplace.setRowSearchOptions(
			rowSearchLeftRow, rowSearchRightRow);
		else SearchAndReplace.resetRowSearch();
		SearchAndReplace.setCommentOption(commentSearch);
		SearchAndReplace.setFoldOption(foldSearch);
		SearchAndReplace.setWordPartOption(wordPart);
		SearchAndReplace.setTentativOption(tentativSearch);
		SearchAndReplace.setSearchFileSet(fileset);
	//}}}
	}
	private SearchFileSet cloneFileset(SearchFileSet source) {
		SearchFileSet clone;
		if (source instanceof DirectoryListSet)
			clone = new DirectoryListSet(
				((DirectoryListSet)source).getDirectory(),
				((DirectoryListSet)source).getFileFilter(),
				((DirectoryListSet)source).isRecursive());
		else if (source instanceof AllBufferSet)
			clone = new AllBufferSet(
				((AllBufferSet)source).getFileFilter());
		else clone = new CurrentBufferSet();
		return clone;
	}
}
