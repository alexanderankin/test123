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

import java.util.Stack;
import java.util.EmptyStackException;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.search.*;
import org.gjt.sp.util.Log;

/**
 * Class that saves XSearch settings in a stack
 * @author R. Widmann
 * @version $Id$
 */
public class SearchSettings
{
	/**
	 * SearchSettings constructor
	 * Is private, because only constructed from static
	 */
  private SearchSettings() {
		load();
	}
		
	private static Stack settingList = new Stack();
	/**
	 * Copy the actual XSearch settings and pushes it in a stack
	 */
	public static void push() {
		SearchSettings currSs = new SearchSettings();
		settingList.push(currSs);
	}
	/**
	 * Copy the last pushed settings into XSearch settings
	 * throws EmptyStackException
	 */
	public static void pop() throws EmptyStackException {
		SearchSettings currSs = (SearchSettings)settingList.pop();
		currSs.unload();
	}
	//{{{ reset
	/**
	 * Reset search settings to default values
	 * Default = forward, no options
	 */
	public static void resetSettings() {
		XSearchAndReplace.setRegexp(false);
		XSearchAndReplace.setIgnoreCase(false);
		XSearchAndReplace.setReverseSearch(false);  
		XSearchAndReplace.setSearchFromTop(false);  
		XSearchAndReplace.resetIgnoreFromTop();  
		XSearchAndReplace.setBeanShellReplace(false);
		XSearchAndReplace.setAutoWrapAround(false);	    
		XSearchAndReplace.resetColumnSearch();
		XSearchAndReplace.resetRowSearch();
		XSearchAndReplace.setCommentOption(XSearchDialog.SEARCH_IN_OUT_NONE);
		XSearchAndReplace.setFoldOption(XSearchDialog.SEARCH_IN_OUT_NONE);
		XSearchAndReplace.setWordPartOption(XSearchDialog.SEARCH_PART_NONE);
		XSearchAndReplace.setHyperRange(-1, -1);
	//}}}
	}
	//{{{ Private members

	//{{{ Instance variables
	private String search;
	private String origSearch;
	private String replace;
	private boolean regexp;
	private boolean ignoreCase;
	private boolean reverse;     // search backward
	private boolean fromTop;     // search from top
	private boolean beanshell;   // use bean shell snippet
	private boolean wrap;	    // wrap search
	private boolean ignoreFromTop;	  // rwchg: ignore "fromTop", when multiple "find" invoked
	private SearchMatcher matcher;
	private SearchFileSet fileset;
	private boolean columnSearchEnabled;
	private boolean columnSearchExpandTabs;
	private int columnSearchLeftCol;
	private int columnSearchRightCol;
	private boolean rowSearchEnabled;
	private int rowSearchLeftRow;
	private int rowSearchRightRow;
	private int commentSearch = XSearchDialog.SEARCH_IN_OUT_NONE;
	private int foldSearch = XSearchDialog.SEARCH_IN_OUT_NONE;
	private int wordPart = XSearchDialog.SEARCH_PART_NONE;
	
	//}}}
	//{{{ load
	private void load() {
 		// search = XSearchAndReplace.search;
		// origSearch = XSearchAndReplace.origSearch;
		// replace = XSearchAndReplace.replace;
		regexp = XSearchAndReplace.getRegexp();
		ignoreCase = XSearchAndReplace.getIgnoreCase();
		reverse = XSearchAndReplace.getReverseSearch();  
		fromTop = XSearchAndReplace.getSearchFromTop();  
		beanshell = XSearchAndReplace.getBeanShellReplace();
		wrap = XSearchAndReplace.getAutoWrapAround();	    
		// ignoreFromTop = XSearchAndReplace.getIgnoreCase();
		// matcher = XSearchAndReplace.matcher;
		// fileset = XSearchAndReplace.fileset;
		columnSearchEnabled = XSearchAndReplace.getColumnOption();
		columnSearchExpandTabs = XSearchAndReplace.getColumnExpandTabsOption();
		columnSearchLeftCol = XSearchAndReplace.getColumnLeftCol();
		columnSearchRightCol = XSearchAndReplace.getColumnRightCol();
		rowSearchEnabled = XSearchAndReplace.getRowOption();
		rowSearchLeftRow = XSearchAndReplace.getRowLeftRow();
		rowSearchRightRow = XSearchAndReplace.getRowRightRow();
		commentSearch = XSearchAndReplace.getCommentOption();
		foldSearch = XSearchAndReplace.getFoldOption();
		wordPart = XSearchAndReplace.getWordPartOption();
	//}}}
	}
	//{{{ unload
	private void unload() {
		XSearchAndReplace.setRegexp(regexp);
		XSearchAndReplace.setIgnoreCase(ignoreCase);
		XSearchAndReplace.setReverseSearch(reverse);  
		XSearchAndReplace.setSearchFromTop(fromTop);  
		XSearchAndReplace.setBeanShellReplace(beanshell);
		XSearchAndReplace.setAutoWrapAround(wrap);	    
		if (columnSearchEnabled) XSearchAndReplace.setColumnSearchOptions(columnSearchExpandTabs,
			columnSearchLeftCol, columnSearchRightCol);
		else XSearchAndReplace.resetColumnSearch();
		if (rowSearchEnabled) XSearchAndReplace.setRowSearchOptions(
			rowSearchLeftRow, rowSearchRightRow);
		else XSearchAndReplace.resetRowSearch();
		XSearchAndReplace.setCommentOption(commentSearch);
		XSearchAndReplace.setFoldOption(foldSearch);
		XSearchAndReplace.setWordPartOption(wordPart);
	//}}}
	}
}
