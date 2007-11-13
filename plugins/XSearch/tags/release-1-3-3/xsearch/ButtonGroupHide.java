/*
 * ButtonGroupHide.java - Button group with hidable buttons
 * :tabSize=2:indentSize=2:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package xsearch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class ButtonGroupHide extends ButtonGroup {
	private AbstractButton defaultButton;
	private AbstractButton actualButton;

	public ButtonGroupHide() {
		super();
	}
	public void add(AbstractButton b) {
		super.add(b);
		if (this.getButtonCount() == 1) {
			// first button is default button
			defaultButton = b;
			actualButton = b;
		}
		if (b.isSelected())
			actualButton = b;
		b.addActionListener(new ButtonGroupHideActionListener());
	}

	/**
	 * when a button inside the group shall be set selected, actualButton must be set
	 */
	
	public void setSelected(AbstractButton b, boolean value)
	{
		b.setSelected(value);
		if (value)
			actualButton = b;
		else
		{
			if (actualButton == b)
			{
				// b was selected ==> select default
				defaultButton.setSelected(true);
				actualButton = defaultButton;
			}
		}
	}

	//{{{ ButtonGroupHideActionListener class
	class ButtonGroupHideActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if (source == actualButton) {
				// the already selected button is selected once more ==> select default button
				defaultButton.setSelected(true);
				actualButton = defaultButton;
			} else {
//				((AbstractButton)source).setSelected(true);
				actualButton = (AbstractButton)source;
			}
		}
	} //}}}

}
