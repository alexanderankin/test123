/*
 * AncestorButton.java - The button representation for an Ancestor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007, 201 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.jedit.ancestor;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class AncestorButton extends JButton
{
	private Ancestor ancestor;

	//{{{ AncestorButton constructor
	/**
	 * Creates a button with no set text or icon.
	 */
	public AncestorButton()
	{
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (ancestor != null)
				{
					if((e.getModifiers() & ActionEvent.CTRL_MASK) != 0)
						ancestor.closeContainedFiles();
					else
						ancestor.doAction();
				}
			}
		});
		setMargin(new Insets(0,0,0,0));
	} //}}}

	//{{{ setAncestor() method
	public void setAncestor(Ancestor ancestor)
	{
		this.ancestor = ancestor;
		setText(ancestor.getName());
	} //}}}
}
