/*
 * SettingsHistoryModel.java 
 * :tabSize=2:indentSize=2:noTabs=false:
 * :folding=explicit:collapseFolds=5:
 *
 * Copyright (C) 2003 Rudolf Widmann
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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.HistoryModel;
import org.gjt.sp.util.Log;

/**
 * Class that saves XSearch settings in a stack
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
 * @author R. Widmann
 * @version $Id$
 */
public class SettingsHistoryModel
{
	/**
	 * SettingsHistoryModel constructor: construct with current settings
	 */
  public SettingsHistoryModel(String name) {
		historyModel = HistoryModel.getModel(name);
	}

	public void addItem(String searchString, SearchSettings settings) {
		/* layout of item:
		 * 0-5: length of search string
		 * 6-x: search String
		 * x+1-y: search setting string
		 */
		StringBuffer sb = new StringBuffer();
		sb.append(SearchSettings.fillNumberWithLeadingZeros(searchString.length(),5));
		sb.append(searchString);
		sb.append(settings.toString());
		//Log.log(Log.DEBUG, BeanShell.class,"+++ SettingsHistoryModel.69: addItem sb = "+sb.toString());
		historyModel.addItem(sb.toString());
		//Log.log(Log.DEBUG, BeanShell.class,"+++ SettingsHistoryModel.72: historyModel.getSize() = "+historyModel.getSize());
	}
	
	public SearchSettings getItem(String searchString) {
		//Log.log(Log.DEBUG, BeanShell.class,"+++ SettingsHistoryModel.73: searchString = "+searchString+", historyModel.getSize() = "+historyModel.getSize());
		boolean searchStringMatched = false;
		int slen = searchString.length();
		for (int i=0; i<historyModel.getSize() && !searchStringMatched; i++) {
			String currItem = historyModel.getItem(i);
			//Log.log(Log.DEBUG, BeanShell.class,"+++ SettingsHistoryModel.77: searchString = "+searchString+", slen = "+slen+", currItem = "+currItem);
			if (currItem.length() >= 5+slen+SearchSettings.SEARCH_SETTINGS_SIZE+5)
				try {
					//Log.log(Log.DEBUG, BeanShell.class,"+++ SettingsHistoryModel.83: currItem.substring(0,5) = "+currItem.substring(0,5)+", currItem.substring(5,5+slen) = "+currItem.substring(5,5+slen));
					if (Integer.parseInt(currItem.substring(0,5)) == slen
					&& currItem.substring(5,5+slen).equals(searchString)) {
						// search string matches found item
						searchStringMatched = true;
						SearchSettings ss = new SearchSettings(); 
						if (ss.loadString(currItem.substring(slen+5)))
							return ss;
						else
							return null;
					}
				}
				catch(NumberFormatException e)
				{
					Log.log(Log.DEBUG, BeanShell.class,"+++ SettingsHistoryModel.91: NumberFormatException");
					return null;
				}
			else
				Log.log(Log.DEBUG, BeanShell.class,"+++ SettingsHistoryModel.102: currItem invalid length = "+currItem);
		}
		Log.log(Log.DEBUG, BeanShell.class,"+++ SettingsHistoryModel.104: nothing found");
		return null;
	}
	private HistoryModel historyModel;
	
}
