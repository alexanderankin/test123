/*
 * XSearchAndReplace.java - Search and replace: derived from SearchAndReplace
 * :tabSize=2:indentSize=2:noTabs=false:
 * :folding=explicit:collapseFolds=5:
 *
 * Copyright (C) 2002 Rudolf Widmann
 * Portions copyright (C) 1999, 2000, 2001, 2002 Slava Pestov
 * Portions copyright (C) 2001 Tom Locke
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

//{{{ Imports
import bsh.BshMethod;
import java.util.ArrayList;
import javax.swing.text.Segment;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.SearchSettingsChanged;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.search.*;
import org.gjt.sp.util.Log;
//}}}

/**
 * Class that implements regular expression and literal search within
 * jEdit buffers.<p>
 *
 * There are two main groups of methods in this class:
 * <ul>
 * <li>Property accessors - for changing search and replace settings.</li>
 * <li>Actions - for performing search and replace.</li>
 * </ul>
 *
 * The "HyperSearch" and "Keep dialog" features, as reflected in
 * checkbox options in the search dialog, are not handled from within
 * this class. If you wish to have these options set before the search dialog
 * appears, make a prior call to either or both of the following:
 *
 * <pre> jEdit.setBooleanProperty("search.hypersearch.toggle",true);
 * jEdit.setBooleanProperty("search.keepDialog.toggle",true);</pre>
 *
 * If you are not using the dialog to undertake a search or replace, you may
 * call any of the search and replace methods (including
 * {@link #hyperSearch(View)}) without concern for the value of these properties.
 *
 * @author Slava Pestov
 * @author John Gellene (API documentation)
 * @author Rudi Widmann (XSearch extension)
 * @version $Id$
 */
public class XSearchAndReplace
{
	public static boolean debug=false; // debug-flag, may be modified by bsh
																		 // xsearch.XSearchAndReplace.debug=true
	
	//{{{ Getters and setters

	//{{{ setSearchString() method
	/**
	 * Sets the current search string.
	 * If wordpart search activ (or tentativ), a regexp is constructed
	 * the original string is saved in origSearch
	 * @param search The new search string
	 */
	public static void setSearchString(String search)
	{
		if (debug) Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.60: search = "+search+", origSearch = "+origSearch+", XSearchAndReplace.search = "+XSearchAndReplace.search);
		boolean searchSettingsChanged = false;
		
		if(!search.equals(origSearch)) {
			origSearch = search;
			searchSettingsChanged = true;
		}
		if (wordPart == XSearchDialog.SEARCH_PART_NONE && !tentativSearch) {
			if(!search.equals(XSearchAndReplace.search)) {
				XSearchAndReplace.search = search;
				searchSettingsChanged = true;
			}
		} else {
			// manipulate search string to a regexp 
			String regExpString=search;
			if (tentativSearch) {
				regExpString = constructTentativSearchString(search);
			}
			regExpString = MiscUtilities.charsToEscapes(regExpString);
			switch (wordPart) {
				case XSearchDialog.SEARCH_PART_WHOLE_WORD:
					regExpString = "\\<"+regExpString+"\\>";
					break;
				case XSearchDialog.SEARCH_PART_PREFIX:
					regExpString = "\\<"+regExpString;
					break;
				case XSearchDialog.SEARCH_PART_SUFFIX:
					regExpString = regExpString+"\\>";
					break;
			}
			if(!regExpString.equals(XSearchAndReplace.search)) {
				XSearchAndReplace.search = regExpString;
				searchSettingsChanged = true;
			}
		}
		if (debug) Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.89: origSearch = "+origSearch+", XSearchAndReplace.search = "+XSearchAndReplace.search+", searchSettingsChanged = "+searchSettingsChanged);
		if (searchSettingsChanged) {
			matcher = null;
			EditBus.send(new SearchSettingsChanged(null));
		}
	} //}}}

	//{{{ getSearchString() method
	/**
	 * Returns the current search string.
	 */
	public static String getSearchString()
	{
		return origSearch;
	} //}}}

	//{{{ setReplaceString() method
	/**
	 * Sets the current replacement string.
	 * @param replace The new replacement string
	 */
	public static void setReplaceString(String replace)
	{
		if(replace.equals(XSearchAndReplace.replace))
			return;

		XSearchAndReplace.replace = replace;
		matcher = null;

		EditBus.send(new SearchSettingsChanged(null));
	} //}}}

	//{{{ getReplaceString() method
	/**
	 * Returns the current replacement string.
	 */
	public static String getReplaceString()
	{
		return replace;
	} //}}}

	//{{{ setFindAll() method
	/**
	 * Sets the find all flag.
	 */
	public static void setFindAll(boolean findAllFlag)
	{
		XSearchAndReplace.findAll = findAllFlag;
	} //}}}

	//{{{ setIgnoreCase() method
	/**
	 * Sets the ignore case flag.
	 * @param ignoreCase True if searches should be case insensitive,
	 * false otherwise
	 */
	public static void setIgnoreCase(boolean ignoreCase)
	{
		if(ignoreCase == XSearchAndReplace.ignoreCase)
			return;

		XSearchAndReplace.ignoreCase = ignoreCase;
		matcher = null;

		EditBus.send(new SearchSettingsChanged(null));
	} 

	// getIgnoreCase() method
	/**
	 * Returns the state of the ignore case flag.
	 * @return True if searches should be case insensitive,
	 * false otherwise
	 */
	public static boolean getIgnoreCase()
	{
		return ignoreCase;
	} //}}}

	//{{{ setRegexp() method
	/**
	 * Sets the state of the regular expression flag.
	 * @param regexp True if regular expression searches should be
	 * performed
	 */
	public static void setRegexp(boolean regexp)
	{
		if(regexp == XSearchAndReplace.regexp)
			return;

		XSearchAndReplace.regexp = regexp;
		if(regexp && reverse)
			reverse = false;

		matcher = null;

		EditBus.send(new SearchSettingsChanged(null));
	} //}}}

	//{{{ getRegexp() method
	/**
	 * Returns the state of the regular expression flag.
	 * @return True if regular expression searches should be performed
	 */
	public static boolean getRegexp()
	{
		return regexp;
	} //}}}

	//{{{ setReverseSearch() method
	/**
	 * Determines whether a reverse search will conducted from the current
	 * position to the beginning of a buffer. Note that reverse search and
	 * regular expression search is mutually exclusive; enabling one will
	 * disable the other.
	 * @param reverse True if searches should go backwards,
	 * false otherwise
	 */
	public static void setReverseSearch(boolean reverse)
	{
		if(reverse == XSearchAndReplace.reverse)
			return;
		// this is highly disturbing: user wants to search back, but forward is executed
		// XSearchAndReplace.reverse = (regexp ? false : reverse);
		// It is better to throw an "illegal search settings" to the user
		XSearchAndReplace.reverse = reverse;

		matcher = null;

		EditBus.send(new SearchSettingsChanged(null));
	} //}}}

	//{{{ getReverseSearch() method
	/**
	 * Returns the state of the reverse search flag.
	 * @return True if searches should go backwards,
	 * false otherwise
	 */
	public static boolean getReverseSearch()
	{
		return reverse;
	} //}}}

	//{{{ resetIgnoreFromTop() method
	/**
	 * Resets ignoreFromTop flag
		 Note: ignoreFromTop flag is set, when find was invoked
		       This allowes "refind" without resetting "fromTop" manually
					 If any search settings change, ignoreFromTop flag shall be reset
					 If end reached (refind results in "not found"), the next "refind" shall restart from top ==> reset ignoreFromTop
	 */
	public static void resetIgnoreFromTop()
	{
		ignoreFromTop = false;
	} //}}}

	//{{{ setSearchFromTop() method
	/**
	 * Sets the search from top flag.
	 * @param fromTop True if searches should start from top,
	 * false otherwise
	 */
	public static void setSearchFromTop(boolean fromTop)
	{
		if(fromTop == XSearchAndReplace.fromTop)
			return;

		XSearchAndReplace.fromTop = fromTop;
		if (fromTop) reverse = false;

		matcher = null;

		EditBus.send(new SearchSettingsChanged(null));
	} //}}}

	//{{{ getSearchFromTop() method
	/**
	 * Returns the state of the search from top flag.
	 */
	public static boolean getSearchFromTop()
	{
		return fromTop;
	} //}}}

	//{{{ setBeanShellReplace() method
	/**
	 * Sets the state of the BeanShell replace flag.
	 * @param regexp True if the replace string is a BeanShell expression
	 * @since jEdit 3.2pre2
	 */
	public static void setBeanShellReplace(boolean beanshell)
	{
		if(beanshell == XSearchAndReplace.beanshell)
			return;

		XSearchAndReplace.beanshell = beanshell;
		matcher = null;

		EditBus.send(new SearchSettingsChanged(null));
	} //}}}

	//{{{ getBeanShellReplace() method
	/**
	 * Returns the state of the BeanShell replace flag.
	 * @return True if the replace string is a BeanShell expression
	 * @since jEdit 3.2pre2
	 */
	public static boolean getBeanShellReplace()
	{
		return beanshell;
	} //}}}

	//{{{ setAutoWrap() method
	/**
	 * Sets the state of the auto wrap around flag.
	 * @param wrap If true, the 'continue search from start' dialog
	 * will not be displayed
	 * @since jEdit 3.2pre2
	 */
	public static void setAutoWrapAround(boolean wrap)
	{
		if(wrap == XSearchAndReplace.wrap)
			return;

		XSearchAndReplace.wrap = wrap;

		EditBus.send(new SearchSettingsChanged(null));
	} //}}}

	//{{{ getAutoWrap() method
	/**
	 * Returns the state of the auto wrap around flag.
	 * @param wrap If true, the 'continue search from start' dialog
	 * will not be displayed
	 * @since jEdit 3.2pre2
	 */
	public static boolean getAutoWrapAround()
	{
		return wrap;
	} //}}}

	//{{{ setSearchMatcher() method
	/**
	 * Sets a custom search string matcher. Note that calling
	 * {@link #setSearchString(String)}, {@link #setReplaceString(String)},
	 * {@link #setIgnoreCase(boolean)}, {@link #setRegexp(boolean)},
	 * {@link #setReverseSearch(boolean)} or
	 * {@link #setBeanShellReplace(boolean)} will reset the matcher to the
	 * default.
	 */
	public static void setSearchMatcher(SearchMatcher matcher)
	{
		XSearchAndReplace.matcher = matcher;

		EditBus.send(new SearchSettingsChanged(null));
	} //}}}

	//{{{ getSearchMatcher() method
	/**
	 * Returns the current search string matcher.
	 * @exception IllegalArgumentException if regular expression search
	 * is enabled, the search string or replacement string is invalid
	 * @since jEdit 4.1pre7
	 */
	public static SearchMatcher getSearchMatcher()
		throws Exception
	{
		if(matcher != null)
			return matcher;

		if(search == null || "".equals(search))
			return null;

		// replace must not be null 
		String replace = (XSearchAndReplace.replace == null ? "" : XSearchAndReplace.replace);

		BshMethod replaceMethod; 
		if(beanshell && replace.length() != 0)
		{
			replaceMethod = BeanShell.cacheBlock("replace","return ("
				+ replace + ");",true);
		}
		else
			replaceMethod = null;

		if(regexp || wordPart != XSearchDialog.SEARCH_PART_NONE || tentativSearch)
			matcher = new RESearchMatcher(search,replace,ignoreCase,
				beanshell,replaceMethod);
		else
		{
			matcher = new BoyerMooreSearchMatcher(search,replace,
				ignoreCase,beanshell,replaceMethod);
		}

		return matcher;
	} //}}}

	//{{{ getMatchedSubexpressions() method
	/**
	 * Return the found subexpressions ico regexp
	 * @param match the String which has been matched
	 */
	public static String[] getMatchedSubexpressions(String match) {
		if (debug) testMatcher();
		if (matcher instanceof RESearchMatcher) {
			return ((RESearchMatcher)matcher).getSubexpressions(match);
		}
		else
			return null;
	}//}}}

	//{{{ setSearchFileSet() method
	/**
	 * Sets the current search file set.
	 * @param fileset The file set to perform searches in
	 * @see AllBufferSet
	 * @see CurrentBufferSet
	 * @see DirectoryListSet
	 */
	public static void setSearchFileSet(SearchFileSet fileset)
	{
		XSearchAndReplace.fileset = fileset;

		EditBus.send(new SearchSettingsChanged(null));
	} //}}}
	
	//{{{ getSearchFileSet() method
	/**
	 * Returns the current search file set.
	 */
	public static SearchFileSet getSearchFileSet()
	{
		return fileset;
	} //}}}


	//{{{ setColumnSearchOptions() method
	/**
	 * Sets the column search parameters
	 * @param expandTabs
	 * @param leftColumn
	 * @param rightColumn
	 */
	public static void setColumnSearchOptions(boolean expandTabs, int leftCol, int rightCol)
	{
		XSearchAndReplace.columnSearchEnabled = true;
		XSearchAndReplace.columnSearchExpandTabs = expandTabs;
		XSearchAndReplace.columnSearchLeftCol = leftCol;
		XSearchAndReplace.columnSearchRightCol = rightCol;
	} //}}}
	//{{{ methodes for Column Options
	/**
	 * gets the column option
	 */
	public static boolean getColumnOption()	{
		return columnSearchEnabled;
	} 
	public static boolean getColumnExpandTabsOption()	{
		return columnSearchExpandTabs;
	} 
	public static int getColumnLeftCol()	{
		return columnSearchLeftCol;
	} 
	public static int getColumnRightCol()	{
		return columnSearchRightCol;
	} 
	// resetColumnSearch() method
	/**
	 * Resets the column search parameters
	 */
	public static void resetColumnSearch()	{
		columnSearchEnabled = false;
	} //}}}
	//{{{ setRowSearchOptions() method
	/**
	 * Sets the row search parameters
	 * @param expandTabs
	 * @param leftRow
	 * @param rightRow
	 */
	public static void setRowSearchOptions( int leftRow, int rightRow)
	{
		XSearchAndReplace.rowSearchEnabled = true;
		XSearchAndReplace.rowSearchLeftRow = leftRow;
		XSearchAndReplace.rowSearchRightRow = rightRow;
	} //}}}
	//{{{ methodes for Row Options
	/**
	 * gets the row option
	 */
	public static boolean getRowOption()	{
		return rowSearchEnabled;
	} 
	public static int getRowLeftRow()	{
		return rowSearchLeftRow;
	} 
	public static int getRowRightRow()	{
		return rowSearchRightRow;
	} 
	// resetRowSearch() method
	/**
	 * Resets the row search parameters
	 */
	public static void resetRowSearch()	{
		rowSearchEnabled = false;
	} //}}}

	//{{{ set/get CommentOption() method
	/**
	 * Sets the CommentOption search parameters
	 */
	public static void setCommentOption(int commentOption)
	{
		commentSearch = commentOption;
	} 
	// getCommentOption() method
	/**
	 * gets the CommentOption search parameters
	 */
	public static int getCommentOption()
	{
		return commentSearch;
	} //}}}

	//{{{ set/get FoldOption() method
	/**
	 * Sets the FoldOption search parameters
	 */
	public static void setFoldOption(int foldOption)
	{
		foldSearch = foldOption;
	} 
	// getFoldOption() method
	/**
	 * gets the FoldOption search parameters
	 */
	public static int getFoldOption()
	{
		return foldSearch;
	} //}}}

	//{{{ set/get WordPartOption() method
	/**
	 * Sets the WordPartOption search parameters
	 */
	public static void setWordPartOption(int wordPartOption)
	{
		XSearchAndReplace.wordPart = wordPartOption;
	}
	// getWordPartOption() method
	/**
	 * Sets the WordPartOption search parameters
	 */
	public static int getWordPartOption()
	{
		return wordPart;
	} //}}}
	//{{{ set/get TentativOption() method
	/**
	 * Sets the TentativOption
	 */
	public static void setTentativOption(boolean tentOption)
	{
		tentativSearch = tentOption;
	}
	/**
	 * Gets the TentativOption
	 */
	public static boolean getTentativOption()
	{
		return tentativSearch;
	} //}}}
	//{{{ set/get hyperRange method
	/**
	 * Sets the hyperRange
	 */
	public static void setHyperRange(int upper, int lower)
	{
		hyperRangeUpper = upper;
		hyperRangeLower = lower;
	}
	public static int getHyperRangeUpper()
	{
		return hyperRangeUpper;
	}
	public static int getHyperRangeLower()
	{
		return hyperRangeLower;
	} //}}}

	//{{{ staticToString() method
	// defined static because this is a pure static class
	/**
	 * Returns the current settings
	 */
	public static String staticToString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("XSearchAndReplace.search = "+XSearchAndReplace.search);
		sb.append("XSearchAndReplace.origSearch = "+XSearchAndReplace.origSearch);
		sb.append("XSearchAndReplace.replace = "+XSearchAndReplace.replace);
		sb.append("XSearchAndReplace.getRegexp() = "+XSearchAndReplace.getRegexp());
		sb.append("XSearchAndReplace.getIgnoreCase() = "+XSearchAndReplace.getIgnoreCase());
		sb.append("XSearchAndReplace.getReverseSearch() = "+XSearchAndReplace.getReverseSearch());  
		sb.append("XSearchAndReplace.getSearchFromTop() = "+XSearchAndReplace.getSearchFromTop());  
		sb.append("XSearchAndReplace.getBeanShellReplace() = "+XSearchAndReplace.getBeanShellReplace());
		sb.append("XSearchAndReplace.getAutoWrapAround() = "+XSearchAndReplace.getAutoWrapAround());	    
		sb.append("XSearchAndReplace.getIgnoreCase() = "+XSearchAndReplace.getIgnoreCase());
		sb.append("XSearchAndReplace.matcher = "+XSearchAndReplace.matcher);
		sb.append("XSearchAndReplace.fileset = "+XSearchAndReplace.fileset);
		sb.append("XSearchAndReplace.getColumnOption() = "+XSearchAndReplace.getColumnOption());
		sb.append("XSearchAndReplace.getColumnExpandTabsOption() = "+XSearchAndReplace.getColumnExpandTabsOption());
		sb.append("XSearchAndReplace.getColumnLeftCol() = "+XSearchAndReplace.getColumnLeftCol());
		sb.append("XSearchAndReplace.getColumnRightCol() = "+XSearchAndReplace.getColumnRightCol());
		sb.append("XSearchAndReplace.getRowOption() = "+XSearchAndReplace.getRowOption());
		sb.append("XSearchAndReplace.getRowLeftRow() = "+XSearchAndReplace.getRowLeftRow());
		sb.append("XSearchAndReplace.getRowRightRow() = "+XSearchAndReplace.getRowRightRow());
		sb.append("XSearchAndReplace.getCommentOption() = "+XSearchAndReplace.getCommentOption());
		sb.append("XSearchAndReplace.getFoldOption() = "+XSearchAndReplace.getFoldOption());
		sb.append("XSearchAndReplace.getWordPartOption() = "+XSearchAndReplace.getWordPartOption());
		return sb.toString();
	} //}}}

	//}}}

	//{{{ Actions

	//{{{ hyperSearch() method
	/**
	 * Performs a HyperSearch.
	 * @param view The view
	 * @since jEdit 2.7pre3
	 */
	public static boolean hyperSearch(View view)
	{
		return hyperSearch(view,false);
	} //}}}

	//{{{ hyperSearch() method
	/**
	 * Performs a HyperSearch.
	 * @param view The view
	 * @param selection If true, will only search in the current selection.
	 * Note that the file set must be the current buffer file set for this
	 * to work.
	 * @since jEdit 4.0pre1
	 */
	public static boolean hyperSearch(View view, boolean selection)
	{
		// component that will parent any dialog boxes
		Object obj = XSearchDialog.getSearchDialog(view);
		Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.671: obj = "+obj.getClass());
		Component comp = (Component)obj;
		if(comp == null)
			comp = view;

		record(view,"hyperSearch(view," + selection + ")",false,
			!selection);

		view.getDockableWindowManager().addDockableWindow(
			HyperSearchResults.NAME);
		final HyperSearchResults results = (HyperSearchResults)
			view.getDockableWindowManager()
			.getDockable(HyperSearchResults.NAME);
		results.searchStarted();

		try
		{
			SearchMatcher matcher = getSearchMatcher();
			if(matcher == null)
			{
				view.getToolkit().beep();
				results.searchFailed();
				return false;
			}

			Selection[] s;
			if(selection)
			{
				s = view.getTextArea().getSelection();
				if(s == null)
				{
					results.searchFailed();
					return false;
				}
			}
			else
				s = null;
			if (hyperRangeUpper == -1 && hyperRangeLower == -1)
				VFSManager.runInWorkThread(new HyperSearchRequest(view,
				matcher,results,s));
			else {
				int hyUp = hyperRangeUpper == -1 ? hyperRangeLower : hyperRangeUpper;
				int hyLo = hyperRangeLower == -1 ? hyperRangeUpper : hyperRangeLower;
				VFSManager.runInWorkThread(new HyperSearchRequest(view,
				matcher,results,s,
				hyUp, hyLo));
			}
			return true;
		}
		catch(Exception e)
		{
			results.searchFailed();
			Log.log(Log.ERROR,XSearchAndReplace.class,e);
			Object[] args = { e.getMessage() };
			if(args[0] == null)
				args[0] = e.toString();
			GUIUtilities.error(comp,"searcherror",args);
			return false;
		}
	} //}}}

	//{{{ quickXfind() method
	/**
	 * Quick Xfind
	 */
	public void quickXfind(View view, JEditTextArea textArea)
	{
		String text = textArea.getSelectedText();
		if(text == null)
		{
			textArea.selectWord();
			text = textArea.getSelectedText();
		}

		if(text != null && text.indexOf('\n') == -1)
		{
			//HistoryModel.getModel("find").addItem(text);
			XSearchAndReplace.setSearchString(text);
			XSearchAndReplace.setSearchFileSet(new CurrentBufferSet());
			XSearchAndReplace.find(view);
		}
		else
			XSearchDialog.showSearchDialog(view,textArea.getSelectedText(),
				XSearchDialog.CURRENT_BUFFER);
	} //}}}


	//{{{ find() method
	/**
	 * Finds the next occurance of the search string.
	 * @param view The view
	 * @return True if the operation was successful, false otherwise
	 */
	public static boolean find(View view)
	{
		// component that will parent any dialog boxes
		Component comp = XSearchDialog.getSearchDialog(view);
		if(comp == null)
			comp = view;

		boolean repeat = false;
		if (findAll) {
			resetIgnoreFromTop(); // findAll always from top
			setReverseSearch(false);
			setSearchFromTop(true);
		}
		// check search settings
		if (!areSearchSettingsOk()) {
			Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.668");
			view.getToolkit().beep();
			view.getStatus().setMessageAndClear(jEdit.getProperty(
			"view.status.illegal-search-settings"));

			return false;
		}
		String path = fileset.getNextFile(view,null);
		if(path == null)
		{
			GUIUtilities.error(comp,"empty-fileset",null);
			return false;
		}

		try
		{
			SearchMatcher matcher = getSearchMatcher();
			if (debug) Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.609: matcher = "+matcher+", search = "+search);
			if(matcher == null)
			{
				view.getToolkit().beep();
				return false;
			}

			record(view,"find(view)",false,true);

			view.showWaitCursor();
			
			boolean _reverse = reverse && fileset instanceof CurrentBufferSet;
			Selection[] lastSelection=null;
			int lastCaret = 0;
			
loop:			for(;;) //{{{
			{
				while(path != null)
				{
					Buffer buffer = jEdit.openTemporary(
						view,null,path,false);

					/* this is stupid and misleading.
					 * but 'path' is not used anywhere except
					 * the above line, and if this is done
					 * after the 'continue', then we will
					 * either hang, or be forced to duplicate
					 * it inside the buffer == null, or add
					 * a 'finally' clause. you decide which one's
					 * worse. */
					path = fileset.getNextFile(view,path);

					if(buffer == null)
						continue loop;

					// Wait for the buffer to load
					if(!buffer.isLoaded())
						VFSManager.waitForRequests();

					int start, end=0;
					JEditTextArea textArea = view.getTextArea();
					if(view.getBuffer() == buffer && !repeat)
					{
						// eval findFromTop
						if (fromTop && !ignoreFromTop) {
							if (rowSearchEnabled)
								start = buffer.getLineStartOffset(rowSearchLeftRow);
							else
								start=0;
							if (debug) Log.log(Log.DEBUG, BeanShell.class,"fromTop invoked");
						}
						else {
							Selection s = textArea.getSelectionAtOffset(
								textArea.getCaretPosition());
							if(s == null)
								start = textArea.getCaretPosition();
							else if(_reverse)
								start = s.getStart();
							else
								start = s.getEnd();
						}
						if (debug) Log.log(Log.DEBUG, BeanShell.class,"tp508: start = "+start);
					}
					else if(_reverse)
						start = buffer.getLength();
					else {
						if (rowSearchEnabled) 
							start = buffer.getLineStartOffset(rowSearchLeftRow);
						else
							start = 0;
					}
					if (rowSearchEnabled && rowSearchRightRow < buffer.getLineCount())
						end = buffer.getLineEndOffset(rowSearchRightRow);
					else 
						end = buffer.getLength();
					if(find(view,buffer,start,end,repeat,_reverse)) {
						if (debug) Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.674: found at start = "+start+", search = "+search);
						testMatcher();
						ignoreFromTop = true;
						// if "find" was restarted, ask user if wrapping allowed
						if (debug) Log.log(Log.DEBUG, BeanShell.class,"tp591: lastSelection = "+lastSelection);
						if (lastSelection != null) {
							if (textArea.getCaretPosition() != lastCaret) {
								Integer[] args = { new Integer(_reverse ? 1 : 0) };
								if (GUIUtilities.confirm(view,
								"keepsearching",args,
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
									lastSelection = null;
									return true;
								}
							}
							// reset Selection to former state
							 textArea.setSelection(lastSelection);
							 textArea.moveCaretPosition(lastCaret,true);
						} else {
							if (findAll) {
								/* find all was selected: mark all matches
								 * display status line
								 * setSelection
								 * move cursor to each Selection to set it visible
								 * reset findAll flag
								 */
								Selection[] foundSelections = (Selection[])findAllSelections.toArray(new Selection[1]);
								view.getStatus().setMessageAndClear(jEdit.getProperty(
									"view.status.findAll.number-of-occurance-found",
									new Object[] { new Integer(foundSelections.length) } ));
								if (debug) for (int i=0;i<foundSelections.length;i++) 
									Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.706: foundSelections[i].getEnd() = "+foundSelections[i].getEnd()+", foundSelections[i].getStart() = "+foundSelections[i].getStart());
								textArea.setSelection(foundSelections);
								for (int i=foundSelections.length-1;i>=0;i--) 
									textArea.moveCaretPosition(foundSelections[i].getEnd());
								findAll = false;
							} 
							return true;
						}
					}
				}

				if(repeat)
				{
					if(!BeanShell.isScriptRunning())
					{
						view.getStatus().setMessageAndClear(
							jEdit.getProperty("view.status.search-not-found"));

						view.getToolkit().beep();
					}
					return false;
				}

				boolean restart;

				if(BeanShell.isScriptRunning())
				{
					restart = true;  // this leads to endless loops in bs-scripts when scanning a file
								// i keep this function for compatibility
				}
				else if(wrap)
				{
					view.getStatus().setMessageAndClear(
						jEdit.getProperty("view.status.auto-wrap"));
					// beep if beep property set
					if(jEdit.getBooleanProperty("search.beepOnSearchAutoWrap"))
					{
						view.getToolkit().beep();
					}
					restart = true;
				}
				else
				{
					if (fromTop) {
						// rwchg: don't ask for wrap if search from top. Give a message instead
						if(!BeanShell.isScriptRunning()) {
							view.getStatus().setMessageAndClear(jEdit.getProperty(
								"view.status.no-further-search-string-found"));
						}
						ignoreFromTop = false; // v0.7: enable re-find after "no further found"
						restart = false;
					} else {
						/* {{{ it is better to first check if there are further matches when
						 * keeping searching.
						 * therefore, the current selection has to be memorized, and the
						 * user dialog done after ignoreFromTop
						 
						Integer[] args = { new Integer(reverse ? 1 : 0) };
						// rwchg: error in GUIUtilities.confirm !!!
						int result = GUIUtilities.confirm(comp,
							"keepsearching",args,
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
						restart = (result == JOptionPane.YES_OPTION);
						}}} */
						lastSelection = view.getTextArea().getSelection();
						lastCaret = view.getTextArea().getCaretPosition();
						if (lastSelection.length > 0 && debug) Log.log(Log.DEBUG, BeanShell.class,"tp662: lastSelection = "+
							view.getTextArea().getSelectedText(lastSelection[0]));
						restart = true;
					}
				}

				if(restart)
				{
					// start search from beginning
					path = fileset.getFirstFile(view);
					repeat = true;
				}
				else
					break loop;
			} //}}}
		} 
		catch(Exception e)
		{
			Log.log(Log.ERROR,XSearchAndReplace.class,e);
			Object[] args = { e.getMessage() };
			if(args[0] == null)
				args[0] = e.toString();
			GUIUtilities.error(comp,"searcherror",args);
		}
		finally
		{
			view.hideWaitCursor();
		}

		return false;
	} //}}}

	//{{{ find() method
	/**
	 * Finds the next instance of the search string in the specified
	 * buffer.
	 * @param view The view
	 * @param buffer The buffer
	 * @param start Location where to start the search
	 */
	public static boolean find(View view, Buffer buffer, int start)
		throws Exception
	{
		return find(view, buffer, start, buffer.getLength(), false, false);
	} //}}}

	//{{{ find() method
	/**
	 * Finds the next instance of the search string in the specified
	 * buffer.
	 * @param view The view
	 * @param buffer The buffer
	 * @param start Location where to start the search
	 * @param firstTime See {@link SearchMatcher#nextMatch(CharIndexed,
	 * boolean,boolean,boolean,boolean)}.
	 * @since jEdit 4.1pre7
	 */
	public static boolean find(View view, Buffer buffer, int start, int end,
		boolean firstTime, boolean reverse) throws Exception 
	{
		SearchMatcher matcher = getSearchMatcher();
		if(matcher == null)
		{
			view.getToolkit().beep();
			return false;
		}

		Segment text = new Segment();
		// remark: Segment is like a String reference without copying
		//         ==> buffer.getText doesn't copy the whole buffer
		//         Therefore, multiple assignment of "text" shouldn't be a performance problem
		boolean xFound = false;  // matched extended options, too
		if (findAll) findAllSelections = new ArrayList();
		// rwchg loop for xsearch-check
		int secCnt=100000;
		while (!xFound && secCnt-- > 0) {
			if(reverse)
				buffer.getText(0,start,text);
			else
				// buffer.getText(start,buffer.getLength() - start,text);
				buffer.getText(start, 
					end - start > 0 ? end - start : 0,  // maybe start > end
					text);
	
			// the start and end flags will be wrong with reverse search enabled,
			// but they are only used by the regexp matcher, which doesn't
			// support reverse search yet.
			//
			// REMIND: fix flags when adding reverse regexp search.
			int[] match = matcher.nextMatch(new CharIndexedSegment(text,reverse),
				start == 0,true,firstTime,reverse);
	
			if(match != null)
			{
				int matchOffsetBegin, matchOffsetEnd;  // offset inside buffer
				if(reverse) {
					matchOffsetBegin = start - match[1];
					matchOffsetEnd = start - match[0];
				} else {
					matchOffsetBegin = start + match[0];
					matchOffsetEnd = start + match[1];
				}

				if (debug) Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.867: matched at matchOffsetBegin = "+matchOffsetBegin);
	
				if (checkXSearchParameters(view.getTextArea(), buffer, matchOffsetBegin, matchOffsetEnd, false)) {
					jEdit.commitTemporary(buffer);
					view.setBuffer(buffer);
					JEditTextArea textArea = view.getTextArea();
					
					if(reverse)
					{
						textArea.setSelection(new Selection.Range(
							start - match[1],
							start - match[0]));
						textArea.moveCaretPosition(start - match[1]);
					}
					else
					{
						Selection currSel = new Selection.Range(
							start + match[0],
							start + match[1]);
						if (findAll) {
							findAllSelections.add(currSel);
							start += match[1];
						}
						else {
							textArea.setSelection(currSel);
							textArea.moveCaretPosition(start + match[1]);
						}
					}
					if (!findAll) xFound = true;
				} else {
					// target matched, but nok because of xParameters
					// find next
					if (reverse) start -= match[1];
					else start += match[1];
					Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.901: start = "+start);
				}
			}	else {
				if (findAll && !findAllSelections.isEmpty()) {
					// previously we did find anything ==> set xFound
					xFound = true;
				} else return false;
			}
		}
		return xFound;  // always true
	} //}}}

	//{{{ replace() method
	/**
	 * Replaces the current selection with the replacement string.
	 * @param view The view
	 * @return True if the operation was successful, false otherwise
	 */
	public static boolean replace(View view)
	{
		// component that will parent any dialog boxes
		Component comp = XSearchDialog.getSearchDialog(view);
		if(comp == null)
			comp = view;

		JEditTextArea textArea = view.getTextArea();

		Buffer buffer = view.getBuffer();
		if(!buffer.isEditable()) {
			view.getStatus().setMessageAndClear(jEdit.getProperty(
			"view.status.replace.buffer-is-not-editable"));
			return false;
		}

		boolean smartCaseReplace = (replace != null
			&& TextUtilities.getStringCase(replace)
			== TextUtilities.LOWER_CASE);

		Selection[] selection = textArea.getSelection();
		if(selection.length == 0)
		{
			// rwchg: if there is nothing selected, we first search for an occurance,
			// then replace it
			if (find(view)) {
				if (debug) Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.1058");
				selection = textArea.getSelection();
			}
			if(selection.length == 0)
			{
				view.getToolkit().beep();
				return false;
			}
		}

		record(view,"replace(view)",true,false);

		// a little hack for reverse replace and find
		int caret = textArea.getCaretPosition();
		Selection s = textArea.getSelectionAtOffset(caret);
		if(s != null)
			caret = s.getStart();

		try
		{
			buffer.beginCompoundEdit();

			SearchMatcher matcher = getSearchMatcher();
			if(matcher == null)
				return false;

			int retVal = 0;

			for(int i = 0; i < selection.length; i++)
			{
				s = selection[i];

				/* if an occurence occurs at the
				beginning of the selection, the
				selection start will get moved.
				this sucks, so we hack to avoid it. */
				int start = s.getStart();

				if(s instanceof Selection.Range)
				{
					retVal += _replace(view,buffer,matcher,
						s.getStart(),s.getEnd(),
						smartCaseReplace);

					textArea.removeFromSelection(s);
					textArea.addToSelection(new Selection.Range(
						start,s.getEnd()));
				}
				else if(s instanceof Selection.Rect)
				{
					for(int j = s.getStartLine(); j <= s.getEndLine(); j++)
					{
						retVal += _replace(view,buffer,matcher,
							s.getStart(buffer,j),s.getEnd(buffer,j),
							smartCaseReplace);
					}
					textArea.addToSelection(new Selection.Rect(
						start,s.getEnd()));
				}
			}

			if(reverse)
			{
				// so that Replace and Find continues from
				// the right location
				textArea.moveCaretPosition(caret);
			}
			else
			{
				s = textArea.getSelectionAtOffset(
				textArea.getCaretPosition());
			if(s != null)
				textArea.moveCaretPosition(s.getEnd());
			}

			if(retVal == 0)
			{
				view.getToolkit().beep();
				return false;
			}

			return true;
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,XSearchAndReplace.class,e);
			Object[] args = { e.getMessage() };
			if(args[0] == null)
				args[0] = e.toString();
			GUIUtilities.error(comp,"searcherror",args);
		}
		finally
		{
			buffer.endCompoundEdit();
		}

		return false;
	} //}}}

	//{{{ replace() method
	/**
	 * Replaces text in the specified range with the replacement string.
	 * @param view The view
	 * @param buffer The buffer
	 * @param start The start offset
	 * @param end The end offset
	 * @return True if the operation was successful, false otherwise
	 */
	public static boolean replace(View view, Buffer buffer, int start, int end)
	{
		if(!buffer.isEditable())
			return false;

		// component that will parent any dialog boxes
		Component comp = XSearchDialog.getSearchDialog(view);
		if(comp == null)
			comp = view;

		boolean smartCaseReplace = (replace != null
			&& TextUtilities.getStringCase(replace)
			== TextUtilities.LOWER_CASE);

		try
		{
			buffer.beginCompoundEdit();

			SearchMatcher matcher = getSearchMatcher();
			if(matcher == null)
				return false;

			int retVal = 0;

			retVal += _replace(view,buffer,matcher,start,end,
				smartCaseReplace);

			if(retVal != 0)
				return true;
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,XSearchAndReplace.class,e);
			Object[] args = { e.getMessage() };
			if(args[0] == null)
				args[0] = e.toString();
			GUIUtilities.error(comp,"searcherror",args);
		}
		finally
		{
			buffer.endCompoundEdit();
		}

		return false;
	} //}}}

	//{{{ replaceAll() method
	/**
	 * Replaces all occurances of the search string with the replacement
	 * string.
	 * @param view The view
	 */
	public static boolean replaceAll(View view)
	{
		// component that will parent any dialog boxes
		Component comp = XSearchDialog.getSearchDialog(view);
		if(comp == null)
			comp = view;

		int fileCount = 0;
		int occurCount = 0;

		if(fileset.getFileCount(view) == 0)
		{
			GUIUtilities.error(comp,"empty-fileset",null);
			return false;
		}

		record(view,"replaceAll(view)",true,true);

		view.showWaitCursor();

		boolean smartCaseReplace = (replace != null
			&& TextUtilities.getStringCase(replace)
			== TextUtilities.LOWER_CASE);

		try
		{
			SearchMatcher matcher = getSearchMatcher();
			if(matcher == null)
				return false;

			String path = fileset.getFirstFile(view);
loop:			while(path != null)
			{
				Buffer buffer = jEdit.openTemporary(
					view,null,path,false);

				/* this is stupid and misleading.
				 * but 'path' is not used anywhere except
				 * the above line, and if this is done
				 * after the 'continue', then we will
				 * either hang, or be forced to duplicate
				 * it inside the buffer == null, or add
				 * a 'finally' clause. you decide which one's
				 * worse. */
				path = fileset.getNextFile(view,path);

				if(buffer == null)
					continue loop;

				// Wait for buffer to finish loading
				if(buffer.isPerformingIO())
					VFSManager.waitForRequests();

				if(!buffer.isEditable())
					continue loop;

				// Leave buffer in a consistent state if
				// an error occurs
				int retVal = 0;
				
				int start, end;
				if (rowSearchEnabled) { 
					if (rowSearchLeftRow <= buffer.getLineCount())
						start = buffer.getLineStartOffset(rowSearchLeftRow);
					else // this may occur cause settings apply to all buffers
						start = buffer.getLength();
					if (rowSearchRightRow < buffer.getLineCount())
						end = buffer.getLineEndOffset(rowSearchRightRow);
					else
						end = buffer.getLength();
				}
				else {
					start = 0;
					end = buffer.getLength();
				}
				try
				{
					buffer.beginCompoundEdit();
					retVal = _replace(view,buffer,matcher,
						// 0, buffer.getLength(),
						start, end,
						smartCaseReplace);
				}
				finally
				{
					buffer.endCompoundEdit();
				}

				if(retVal != 0)
				{
					fileCount++;
					occurCount += retVal;
					jEdit.commitTemporary(buffer);
				}
			}
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,XSearchAndReplace.class,e);
			Object[] args = { e.getMessage() };
			if(args[0] == null)
				args[0] = e.toString();
			GUIUtilities.error(comp,"searcherror",args);
		}
		finally
		{
			view.hideWaitCursor();
		}

		/* Don't do this when playing a macro, cos it's annoying */
		if(!BeanShell.isScriptRunning())
		{
			Object[] args = { new Integer(occurCount),
				new Integer(fileCount) };
			view.getStatus().setMessageAndClear(jEdit.getProperty(
				"view.status.replace-all",args));
			if(occurCount == 0)
				view.getToolkit().beep();
		}

		return (fileCount != 0);
	} //}}}

	//}}}

	//{{{ load() method
	/**
	 * Loads search and replace state from the properties.
	 */
	public static void load()
	{
		search = jEdit.getProperty("search.find.value");
		origSearch = search;
		replace = jEdit.getProperty("search.replace.value");
		ignoreCase = jEdit.getBooleanProperty("search.ignoreCase.toggle");
		regexp = jEdit.getBooleanProperty("search.regexp.toggle");
		beanshell = jEdit.getBooleanProperty("search.beanshell.toggle");
		wrap = jEdit.getBooleanProperty("search.wrap.toggle");
		
		// load extended properties
		fromTop = jEdit.getBooleanProperty("search.ext.fromTop.toggle");
		tentativSearch = jEdit.getBooleanProperty("search.ext.tentativ.toggle");
		columnSearchEnabled = jEdit.getBooleanProperty("search.ext.column.toggle");
		columnSearchExpandTabs = jEdit.getBooleanProperty("search.ext.column.expand-tabs.toggle");
		columnSearchLeftCol = jEdit.getIntegerProperty("search.ext.column.left.value",0);
		columnSearchRightCol = jEdit.getIntegerProperty("search.ext.column.right.value",0);
		rowSearchEnabled = jEdit.getBooleanProperty("search.ext.row.toggle");
		rowSearchLeftRow = jEdit.getIntegerProperty("search.ext.row.left.value",0);
		rowSearchRightRow = jEdit.getIntegerProperty("search.ext.row.right.value",0);
		wordPart = jEdit.getIntegerProperty("search.ext.wordpart.value", XSearchDialog.SEARCH_PART_NONE);
		foldSearch = 
			jEdit.getIntegerProperty("search.ext.foldSearch.value", XSearchDialog.SEARCH_IN_OUT_NONE);
		commentSearch = 
			jEdit.getIntegerProperty("search.ext.commentSearch.value", XSearchDialog.SEARCH_IN_OUT_NONE);
		hyperRangeUpper = -1;
		hyperRangeLower = -1;
		findAll = false;
		fileset = new CurrentBufferSet();

		// Tags plugin likes to call this method at times other than
		// startup; so we need to fire a SearchSettingsChanged to
		// notify the search bar and so on.
		matcher = null;
		EditBus.send(new SearchSettingsChanged(null));
	} //}}}

	//{{{ save() method
	/**
	 * Saves search and replace state to the properties.
	 */
	public static void save()
	{
		jEdit.setProperty("search.find.value",search);
		jEdit.setProperty("search.replace.value",replace);
		jEdit.setBooleanProperty("search.ignoreCase.toggle",ignoreCase);
		jEdit.setBooleanProperty("search.regexp.toggle",regexp);
		jEdit.setBooleanProperty("search.beanshell.toggle",beanshell);
		jEdit.setBooleanProperty("search.wrap.toggle",wrap);
		// save extended properties
		jEdit.setBooleanProperty("search.ext.fromTop.toggle",fromTop);
		jEdit.setBooleanProperty("search.ext.tentativ.toggle",tentativSearch);
		jEdit.setBooleanProperty("search.ext.column.toggle",columnSearchEnabled);
		jEdit.setBooleanProperty("search.ext.column.expand-tabs.toggle",columnSearchExpandTabs);
		jEdit.setIntegerProperty("search.ext.column.left.value",columnSearchLeftCol);
		jEdit.setIntegerProperty("search.ext.column.right.value",columnSearchRightCol);
		jEdit.setBooleanProperty("search.ext.row.toggle",rowSearchEnabled);
		jEdit.setIntegerProperty("search.ext.row.left.value",rowSearchLeftRow);
		jEdit.setIntegerProperty("search.ext.row.right.value",rowSearchRightRow);
		jEdit.setIntegerProperty("search.ext.wordpart.value",wordPart);
		jEdit.setIntegerProperty("search.ext.foldSearch.value",foldSearch);
		jEdit.setIntegerProperty("search.ext.commentSearch.value",commentSearch);
		
/*
		jEdit.setBooleanProperty("search.show-settings.toggle",showSettings);		
		jEdit.setBooleanProperty("search.show-replace.toggle",showReplace);
		jEdit.setBooleanProperty("search.show-extended.toggle",showExtended);
*/
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private static String search;
	private static String origSearch;
	private static String replace;
	private static boolean regexp;
	private static boolean ignoreCase;
	private static boolean reverse;     // search backward
	private static boolean fromTop;     // search from top
	private static boolean beanshell;   // use bean shell snippet
	private static boolean wrap;	      // wrap search
	private static boolean ignoreFromTop;	  // rwchg: ignore "fromTop", when multiple "find" invoked
	private static boolean findAll;
	public static boolean tentativSearch; // bs: XSearchAndReplace.tentativSearch=true

	private static SearchMatcher matcher;
	private static SearchFileSet fileset;

	private static ArrayList findAllSelections;
	
	/* selectiv display
	private static boolean showSettings;  // display the standart search settings
	private static boolean showReplace;   // display the replace options and buttons
	private static boolean showExtended;  // display the extended options and buttons
*/
	// column search
	private static boolean columnSearchEnabled;
	private static boolean columnSearchExpandTabs;
	private static int columnSearchLeftCol;
	private static int columnSearchRightCol;
	// row search
	private static boolean rowSearchEnabled;
	private static int rowSearchLeftRow;
	private static int rowSearchRightRow;
	// hyperRange	
	private static int hyperRangeUpper;
	private static int hyperRangeLower;


	// comment search
	private static int commentSearch = XSearchDialog.SEARCH_IN_OUT_NONE;

	// folding search
	private static int foldSearch = XSearchDialog.SEARCH_IN_OUT_NONE;

	// word part search
	private static int wordPart = XSearchDialog.SEARCH_PART_NONE;
	
	private static final String keyboard = "12345567890ß qwertzuiopü+ asdfghjklöä# <yxcvbnm,.-";
	
	//}}}

	//{{{ areSearchSettingsOk
	/**
	 * checks if the search settings are consistent.
	 * not allowed combinations:
	 * - searchback and regexp (wordpart and tentativ are regexp, too)
	 */
	private static boolean areSearchSettingsOk() {
		if (reverse && (regexp || wordPart != XSearchDialog.SEARCH_PART_NONE || tentativSearch))
			return false;
		
		return true;
	} //}}}

	//{{{ constructTentativSearchString method
	/**
	 * Translates a String into a regexp which shall find following mistypings:
	 * - double typed char (toown ico town)
	 * - few char (ton ico toon)
	 * - wrong char typed (Rzdi ico Rudi)
	 * - wrong sequence (Rdui ico Rudi)
	 * @param searchString
	 */
	private static String constructTentativSearchString(String searchString) {
		StringBuffer dest = new StringBuffer();
		// iterate over searchString. As the loop works one char ahead, we add a blank
		StringCharacterIterator it = new StringCharacterIterator(new String(searchString+" "));
		char prevChar = ' ';
		char pre2Char = ' ';
		boolean skipFirst = true; // we need the following character, so process one char later
		// read characters which will be included always in search characters
		String tendCharacters = jEdit.getProperty("search.ext.tentativ.addition");
		for(char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			char currChar = Character.toLowerCase(c);
			if (skipFirst) skipFirst = false;
			else {
				if (pre2Char != prevChar) {
					// skip identical characters
					StringBuffer charBuff = new StringBuffer();
					int keyBoardIdx = keyboard.indexOf(prevChar);
					if (keyBoardIdx != -1 && !Character.isWhitespace(prevChar)) {
						int leftIdx = keyBoardIdx-1;
						if (leftIdx < 0) leftIdx = 0;
						int rightIdx = keyBoardIdx+2;
						if (rightIdx > keyboard.length()) rightIdx = keyboard.length();
						charBuff.append(keyboard.substring(leftIdx,rightIdx).trim());
					} else {
						charBuff.append(prevChar);
					}
					// add previous and following char to list, skip if already in charBuff
					if (!Character.isWhitespace(pre2Char) && charBuff.toString().indexOf(pre2Char) == -1)
						charBuff.append(pre2Char);
					if (!Character.isWhitespace(currChar) && charBuff.toString().indexOf(currChar) == -1)
						charBuff.append(currChar);
					// add uppercases
					String lcString = charBuff.toString();
					StringCharacterIterator lcIter = new StringCharacterIterator(lcString);
					for(char lc = lcIter.first(); lc != CharacterIterator.DONE; lc = lcIter.next()) {
						if (Character.isLowerCase(lc)) charBuff.append(Character.toUpperCase(lc));
					}
					// add tend characters in list, skip if already in charBuff
					StringCharacterIterator tendIter = new StringCharacterIterator(tendCharacters);
					for(char tc = tendIter.first(); tc != CharacterIterator.DONE; tc = tendIter.next()) {
						if (charBuff.toString().indexOf(tc) == -1) charBuff.append(tc);
					}
					//Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.1351: charBuff = "+charBuff);
					dest.append("["+charsToEscapes(charBuff.toString())+"]+");
					//Log.log(Log.DEBUG, BeanShell.class,"XSearchAndReplace.1353: dest = "+dest);
				}
			}
			pre2Char = prevChar;
			prevChar = currChar;
    }
		return dest.toString();
	} //}}}
	
	//{{{ charsToEscapes() method
	/**
	 * Escapes all regexp special characters in the specified
	 * string.
	 * @param str The string
	 */
	public static String charsToEscapes(String str)
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			if ("+*()[]./{}^$?|\"".indexOf(c) != -1) { // char "\\" is handled in MiscUtilities
				buf.append("\\");
			}
			buf.append(c);
		}
		return buf.toString();
	} //}}}
	
	//{{{ checkXSearchParameters method
	/**
	 * Checks if the search result matches the extended parameters, too
	 * @param textArea
	 * @param buffer The buffer
	 * @param matchBegin Location where match starts
	 * @param matchBegin Location where match ends
	 */
	static boolean checkXSearchParameters(JEditTextArea textArea, Buffer buffer, 
		int matchBegin, int matchEnd, boolean hyperSearchFlag) {
		if (debug) Log.log(Log.DEBUG, BeanShell.class,
			"checkXsp: matchBegin = "+matchBegin+", matchEnd = "+matchEnd);
		boolean xMatchOk = true;
		int matchLine = buffer.getLineOfOffset(matchBegin);
		int startMatchLine = buffer.getLineStartOffset(matchLine);
//		int startMatchEndLine = buffer.getLineStartOffset(buffer.getLineOfOffset(matchEnd));
		if (debug) Log.log(Log.DEBUG, BeanShell.class,"tp1111: matchLine = "+matchLine+
			", startMatchLine = "+startMatchLine);
		/************************************************************************************
		 * check fold status
		 ************************************************************************************/
		if (xMatchOk && foldSearch != XSearchDialog.SEARCH_IN_OUT_NONE) {
			if (foldSearch == XSearchDialog.SEARCH_IN_OUT_OUTSIDE  
			^ textArea.getFoldVisibilityManager().isLineVisible(matchLine)) {
				xMatchOk = false;
			}
		}
		/************************************************************************************
		 * check comment status
		 ************************************************************************************/
		if (xMatchOk && commentSearch != XSearchDialog.SEARCH_IN_OUT_NONE) {
			// now we know if we are inside / outside of a comment ==> evaluate request
			if (commentSearch == XSearchDialog.SEARCH_IN_OUT_OUTSIDE ^	// xor
				  isOutsideComments(textArea, buffer, matchBegin, hyperSearchFlag))
//				outsideCmt) 
				xMatchOk = false;
		}
		/************************************************************************************
		 * check columns
		 ************************************************************************************/
		if (columnSearchEnabled) {
			// reject if match on multiple lines
			if (buffer.getLineOfOffset(matchEnd) != matchLine) xMatchOk = false;
			else {
			// Log.log(Log.DEBUG, BeanShell.class,"matchBegin = "+matchBegin+", matchEnd = "+matchEnd);
//			BeanShellUtility bsu = new BeanShellUtility(view);
				if (columnSearchExpandTabs) {
					int matchRowStart = matchBegin - startMatchLine;
					int matchRowEnd   = matchEnd   - startMatchLine;
					matchBegin = startMatchLine + XSearchBeanShellUtility.getVisiblePosition(buffer.getTabSize(),
						matchRowStart, buffer.getLineText(matchLine));
					matchEnd = startMatchLine + XSearchBeanShellUtility.getVisiblePosition(buffer.getTabSize(), 
						matchRowEnd, buffer.getLineText(matchLine));
				}
				// check match inside columns ( add +1 to ranges, because visible row starts with 1)
				if (debug) Log.log(Log.DEBUG, BeanShell.class,"tp1197: matchBegin = "+matchBegin+", matchLine = "+matchLine+
					", buffer.getLineStartOffset(matchLine) = "+buffer.getLineStartOffset(matchLine)+
				", columnSearchLeftCol = "+columnSearchLeftCol);
				if (matchBegin - buffer.getLineStartOffset(matchLine) < columnSearchLeftCol-1) {
					xMatchOk = false;
				}
				if (debug) Log.log(Log.DEBUG, BeanShell.class,"tp1203: matchEnd = "+matchEnd+"matchLine = "+matchLine+
				"buffer.getLineStartOffset(matchLine) = "+buffer.getLineStartOffset(matchLine)+
				"columnSearchRightCol = "+columnSearchRightCol);
				if (matchEnd - buffer.getLineStartOffset(matchLine) > columnSearchRightCol) {
					xMatchOk = false;
				}
			}
		}
		/************************************************************************************
		 ************************************************************************************/
		if (debug) Log.log(Log.DEBUG, BeanShell.class,"checkXsp: xMatchOk = "+xMatchOk);
		return xMatchOk;
	}
	//}}}
	
	//{{{ isOutsideComments(int currPos) method
	/**
	 * Checks if the passed buffer position is inside comments
	 * Note: in case of hypersearch, the token methode doesn't work (Edit Mode not parsed yet ?!)
	 *       therefore, a simplified methode with default comment signs is used
	 * @param textArea
	 * @param buffer
	 * @param currPos  Position in buffer to be checked
	 * @param hyperSearchFlag  Indicates if normal or hypersearch
	 */
	
	private static boolean isOutsideComments(JEditTextArea textArea, Buffer buffer, 
		int currPos, boolean hyperSearchFlag) {
		// Note: as getToken doesn't work for blanks, we have to find the first nonblank
		// int caretPos = textArea.getCaretPosition();
		boolean outsideCmt;
		if (debug) Log.log(Log.DEBUG, BeanShell.class,"tp1280: currPos = "+currPos);
		if (hyperSearchFlag) {
		// {{{ hypersearch
			int matchLine = buffer.getLineOfOffset(currPos);
			outsideCmt = true;
			// check line comment
			String currLine = buffer.getLineText(matchLine);
//			if (currLine.lastIndexOf("//",currPos-buffer.getLineStartOffset(matchLine)) != -1)
			if (currLine.lastIndexOf(jEdit.getProperty("search.comment.line"),
				currPos-buffer.getLineStartOffset(matchLine)) != -1)
				outsideCmt = false;
			// check block comment
			if (outsideCmt) {
				// search for "start-comment" before match
				SearchMatcher cmtMatcher = new BoyerMooreSearchMatcher(
					jEdit.getProperty("search.comment.blockbegin"),		// search,
					"",		    //replace,
					false,		//ignoreCase,
					//true,		  //reverse
					false,		//beanshell,
					null		  //replaceMethod);
				);
				Segment textBeforeMatch = new Segment();
				buffer.getText(0,currPos,textBeforeMatch);
	
				int[] openCmtMatch = cmtMatcher.nextMatch(new CharIndexedSegment(textBeforeMatch,true),
				false,true,true
				,true // reverse
				);
	
				if(openCmtMatch != null) {
					// we found an open comment before match ==> check if alreday closed 
					if (debug) Log.log(Log.DEBUG, BeanShell.class,"found open cmt at = "
					+openCmtMatch[0]+"-"+openCmtMatch[1]);
					cmtMatcher = new BoyerMooreSearchMatcher(
						jEdit.getProperty("search.comment.blockend"),		// search,
						"",		    //replace,
						false,		//ignoreCase,
						//true,	  	//reverse
						false,		//beanshell,
						null		  //replaceMethod);
					);
					int[] closeCmtMatch = cmtMatcher.nextMatch(new CharIndexedSegment(textBeforeMatch,true),
					false,true,true
					,true // reverse
					);
					if(closeCmtMatch == null) {
						// no close found ==> inside comment
						outsideCmt = false;
					} else {
						if (debug) Log.log(Log.DEBUG, BeanShell.class,"found close cmt at = "
						+closeCmtMatch[0]+"-"+closeCmtMatch[1]);
						// we found a close comment ==> check which was earlier
						if (openCmtMatch[0] < closeCmtMatch[0]) outsideCmt = false;
					}
				}
			}
		// }}}
			
			
		} else { //{{{ not hypersearch
			boolean blankChar = true;
			for (int i=currPos; blankChar && i<buffer.getLength();i++) {
				if (!Character.isWhitespace(buffer.getText(i,1).charAt(0))) {
				/*
				String currChar = buffer.getText(i,1);
				if (" \r\n\t".indexOf(currChar) == -1) {
					*/
					blankChar = false;
					currPos = i;
				}
			}
			int line = textArea.getLineOfOffset(currPos);
			if (debug) Log.log(Log.DEBUG, BeanShell.class,"tp1293: lineNr = "+line);
			int position = currPos - textArea.getLineStartOffset(line);
			if (debug) Log.log(Log.DEBUG, BeanShell.class,"tp1296: position = "+position);
			
			DefaultTokenHandler tokens = new DefaultTokenHandler();
			buffer.markTokens(line, tokens);
			org.gjt.sp.jedit.syntax.Token token =
			TextUtilities.getTokenAtOffset(tokens.getTokens(), position);
			if(token.id == Token.COMMENT1 || token.id == Token.COMMENT2)
				outsideCmt = false;
			else
				outsideCmt = true;
		}  //}}}
		return outsideCmt;
	}
	//}}}
	

	//{{{ record() method
	private static void record(View view, String action,
		boolean replaceAction, boolean recordFileSet)
	{
		Macros.Recorder recorder = view.getMacroRecorder();

		if(recorder != null)
		{
			recorder.record("xsearch.SearchSettings.push();");
			recorder.record("xsearch.SearchSettings.resetSettings();");
			
			recorder.record("xsearch.XSearchAndReplace.setSearchString(\""
				+ MiscUtilities.charsToEscapes(search) + "\");");

			if(replaceAction)
			{
				recorder.record("xsearch.XSearchAndReplace.setReplaceString(\""
					+ MiscUtilities.charsToEscapes(replace) + "\");");
				if (beanshell) recorder.record("xsearch.XSearchAndReplace.setBeanShellReplace("
					+ beanshell + ");");
			}
			else
			{
				// only record this if doing a find next
				if (wrap) recorder.record("xsearch.XSearchAndReplace.setAutoWrapAround("
					+ wrap + ");");
				if (reverse) recorder.record("xsearch.XSearchAndReplace.setReverseSearch("
					+ reverse + ");");
			}

			if (ignoreCase) recorder.record("xsearch.XSearchAndReplace.setIgnoreCase("
				+ ignoreCase + ");");
			if (regexp) recorder.record("xsearch.XSearchAndReplace.setRegexp("
				+ regexp + ");");
			// add extended options
			if (fromTop && !findAll) recorder.record("xsearch.XSearchAndReplace.setSearchFromTop("
				+ fromTop + ");");
			if (columnSearchEnabled) recorder.record("xsearch.XSearchAndReplace.setColumnSearchOptions("
				+ columnSearchExpandTabs+", "+columnSearchLeftCol+", "+columnSearchRightCol + ");");
			if (rowSearchEnabled) recorder.record("xsearch.XSearchAndReplace.setRowSearchOptions("
				+ rowSearchLeftRow+", "+rowSearchRightRow + ");");
			if (commentSearch != XSearchDialog.SEARCH_IN_OUT_NONE) recorder.record("xsearch.XSearchAndReplace.setCommentOption("
				+ commentSearch + ");");
			if (foldSearch != XSearchDialog.SEARCH_IN_OUT_NONE) recorder.record("xsearch.XSearchAndReplace.setFoldOption("
				+ foldSearch + ");");
			if (wordPart != XSearchDialog.SEARCH_PART_NONE) recorder.record("xsearch.XSearchAndReplace.setWordPartOption("
				+ wordPart + ");");
			
			if(recordFileSet)
			{
				recorder.record("xsearch.XSearchAndReplace.setSearchFileSet("
					+ fileset.getCode() + ");");
			}
			if (findAll) recorder.record("xsearch.XSearchAndReplace.setFindAll(true);");

			recorder.record("xsearch.XSearchAndReplace." + action + ";");
			recorder.record("xsearch.SearchSettings.pop();");
		}
	} //}}}

	//{{{ _replace() method
	/**
	 * Replaces all occurances of the search string with the replacement
	 * string.
	 * @param view The view
	 * @param buffer The buffer
	 * @param start The start offset
	 * @param end The end offset
	 * @param matcher The search matcher to use
	 * @param smartCaseReplace See user's guide
	 * @return The number of occurrences replaced
	 */
	private static int _replace(View view, Buffer buffer,
		SearchMatcher matcher, int start, int end,
		boolean smartCaseReplace)
		throws Exception
	{
		// make smart case replace optional
		smartCaseReplace = smartCaseReplace && jEdit.getBooleanProperty("xsearch.replaceCaseSensitiv", true);
		int occurCount = 0;

		boolean endOfLine = (buffer.getLineEndOffset(
			buffer.getLineOfOffset(end)) - 1 == end);

		Segment text = new Segment();
		int offset = start;
loop:		for(int counter = 0; ; counter++)
		{
			buffer.getText(offset,end - offset,text);

			boolean startOfLine = (buffer.getLineStartOffset(
				buffer.getLineOfOffset(offset)) == offset);

			int[] occur = matcher.nextMatch(
				new CharIndexedSegment(text,false),
				startOfLine,endOfLine,counter == 0,
				false);
			if(occur == null)
				break loop;
			int _start = occur[0];
			int _length = occur[1] - occur[0];
			// check xsearch parameters
			if (!checkXSearchParameters(view.getTextArea(), buffer,
				offset + _start, offset + _start + _length, false)) {
				// current match was not ok ==> skip replacement
				offset += _start + _length;
				if (debug) Log.log(Log.DEBUG, BeanShell.class,"offset = "+offset);
			} else {
				String found = new String(text.array,text.offset + _start,_length);
				String subst = matcher.substitute(found);
				if(smartCaseReplace && ignoreCase)
				{
					int strCase = TextUtilities.getStringCase(found);
					if(strCase == TextUtilities.LOWER_CASE)
						subst = subst.toLowerCase();
					else if(strCase == TextUtilities.UPPER_CASE)
						subst = subst.toUpperCase();
					else if(strCase == TextUtilities.TITLE_CASE)
						subst = TextUtilities.toTitleCase(subst);
				}
	
				if(subst != null)
				{
					buffer.remove(offset + _start,_length);
					buffer.insert(offset + _start,subst);
					occurCount++;
					offset += _start + subst.length();
	
					end += (subst.length() - found.length());
				}
				else
					offset += _start + _length;
			}
		}
		return occurCount;
	} //}}}
  private static void testMatcher() {
		if (debug) Log.log(Log.DEBUG, BeanShell.class,"tp1433: matcher = "+ matcher +
			" is " + matcher.getClass().getName());
	}
	//}}}
}
