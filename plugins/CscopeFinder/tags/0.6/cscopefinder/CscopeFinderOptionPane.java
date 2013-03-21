/*
 * GeneralOptionPane.java
 *
 * Copyright 2004 Ollie Rutherfurd <oliver@jedit.org>
 *
 * This file is part of CscopeFinderPlugin
 *
 * CscopeFinderPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * CscopeFinderPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA	02111-1307, USA.
 *
 * $Id: GeneralOptionPane.java,v 1.1 2004/11/07 15:52:36 orutherfurd Exp $
 */
/*
 * This file originates from the Tags Plugin version 2.0.1
 * whose copyright and licensing is seen above.
 * The original file was modified to become the derived work you see here
 * in accordance with Section 2 of the Terms and Conditions of the GPL v2.
 *
 * The derived work is called the CscopeFinder Plugin and is
 * Copyright 2006 Dean Hall.
 *
 * 2006/08/09
 */

package cscopefinder;

//{{{ imports
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.*;

import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import cscopefinder.*;
//}}}

public class CscopeFinderOptionPane extends AbstractOptionPane
{
	//{{{ private declarations
	JTextField cscopeIndexFilename;
//    JCheckBox extendsThroughDot;
	//}}}

	//{{{ GeneralOptionPane constructor
	public CscopeFinderOptionPane()
	{
		super("cscopefinder");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		cscopeIndexFilename = new JTextField("" +
			jEdit.getProperty("cscopefinder.index-filename"));
		addComponent(jEdit.getProperty("options.cscopefinder.index-filename"),
			         cscopeIndexFilename);

/*		extendsThroughDot = new JCheckBox(
			jEdit.getProperty(
                "options.cscopefinder.target-extends-through-dot"),
			jEdit.getBooleanProperty(
                "cscopefinder.target-extends-through-dot", false));
		addComponent(extendsThroughDot);
*/
	} //}}}

	protected void _save()
	{
		jEdit.setProperty("cscopefinder.index-filename",
			              cscopeIndexFilename.getText());

/*        jEdit.setBooleanProperty("cscopefinder.target-extends-through-dot",
                                 extendsThroughDot.isSelected());
*/
	}
}
