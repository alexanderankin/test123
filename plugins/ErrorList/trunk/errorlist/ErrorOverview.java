/*
 * ErrorOverview.java - Error overview component
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
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

package errorlist;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.GUIUtilities;

public class ErrorOverview extends JPanel
{
	//{{{ ErrorOverview constructor
	public ErrorOverview(final JEditTextArea textArea)
	{
		super(new BorderLayout());
		this.textArea = textArea;
		close = new RolloverButton(GUIUtilities.loadIcon("closebox.gif"));
		add(BorderLayout.NORTH,close);
		close.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				ErrorListPlugin.removeErrorOverview(textArea);
			}
		});
	} //}}}

	//{{{ getPreferredSize() method
	public Dimension getPreferredSize()
	{
		return new Dimension(close.getPreferredSize().height,0);
	} //}}}

	//{{{ Private members
	private JEditTextArea textArea;
	private RolloverButton close;
	//}}}
}
