/*
 *  {{{ header
 *  ReSupportPopup.java - provides popup actions for Dim
 *  Copyright (c) 2004 Rudi Widmann
 *
 *  :tabSize=4:indentSize=4
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *  }}}
 */
package xsearch;

//{{{ imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
//}}}

/**
 * A popup menu for RE support.
 *
 *@author     Rudi Widmann
 *@created    4. Januar 2005
 */
public class ReSupportPopup extends JPopupMenu {
	private SearchReplaceFieldData srFieldData;
	//{{{ instance variables
	private View       view;
	//{{{ +ReSupportPopup(View, JTree, TreePath[]) : <init>
	//public ReSupportPopup(View view, JTextField textField)
	/**
	 *Constructor for the ReSupportPopup object
	 *
	 *@param  view         Description of the Parameter
	 *@param  srFieldData  Description of the Parameter
	 */
	public ReSupportPopup(View view, SearchReplaceFieldData srFieldData) {
		this.view = view;
		this.srFieldData = srFieldData;
		String title = jEdit.getProperty("search.ext.regexp-support.popup-title");
		//String title = "popup.title";
		add(title).setEnabled(false);
		addSeparator();
		if (srFieldData.isReplaceField())
			addMenuItem("replace");
		else
			addMenuItem("find");
	}                                                //}}}

	//{{{ -createMenuItem(String) : JMenuItem
	/**
	 *  Adds a feature to the MenuItem attribute of the ReSupportPopup object
	 *
	 *@param  name  The feature to be added to the MenuItem attribute
	 */
	private void addMenuItem(String name) {
		int idx = 1;
		String label;
		boolean labelFound = true;
		int maxLen = 4;
		while (labelFound) {
			label = jEdit.getProperty("search.ext.regexp-support." + name + Integer.toString(idx) + ".label");
			if (label == null) {
				labelFound = false;
			}
			else {
				String actionCommand = jEdit.getProperty("search.ext.regexp-support." + name + Integer.toString(idx) + ".value");
				if (actionCommand.length() < maxLen)
					label = actionCommand + StandardUtilities.createWhiteSpace(maxLen - actionCommand.length(), 0) + ": " + label;
				else
					label = actionCommand + " : " + label;
				JMenuItem mi = new JMenuItem(label);
				mi.setActionCommand(actionCommand);
				mi.addActionListener(new ActionHandler());
				add(mi);
				idx++;
			}
		}
	}                                                //}}}

	//{{{ -class ActionHandler
	/**
	 *  Description of the Class
	 *
	 *@author     widmann
	 *@created    4. Januar 2005
	 */
	private class ActionHandler implements ActionListener {

		/**
		 *  Description of the Method
		 *
		 *@param  evt  Description of the Parameter
		 */
		public void actionPerformed(ActionEvent evt) {
			String regexString = evt.getActionCommand();
			Log.log(Log.DEBUG, ReSupportPopup.class, "+++ ReSupportPopup.117: regexString = " + regexString);
			//			if(actionCommand.equals("expand-all"))
			srFieldData.setNewRegexpString(regexString);
			// help out the garbage collector
			view = null;
		}                                               //}}}
	}                                                //}}}
}
