/*
 * OkCancelButtons.java - a button pane for EnhancedDialog instances.
 * Copyright (c) 2005 Marcelo Vanzin
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package common.gui;

//{{{ Imports
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.jEdit;
//}}}

/**
 *	A pair of buttons (OK/Cancel) to be added to instances of
 *	EnhancedDialog. Calls <code>ok()</code> when OK is pressed, and
 *	<code>cancel()</code> when Cancel is pressed.
 *
 *	<p>Copied from the same class that belonged to the ProjectViewer plugin.</p>
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		CC 0.9.0
 */
public class OkCancelButtons extends JPanel
							 implements ActionListener
{

	private EnhancedDialog	target;
	private JButton 		cancel;
	private JButton 		ok;

	public OkCancelButtons(EnhancedDialog target)
	{
		super(new FlowLayout());
		this.target = target;

		cancel	= new JButton(jEdit.getProperty("common.cancel"));
		ok 		= new JButton(jEdit.getProperty("common.ok"));

		ok.setPreferredSize(cancel.getPreferredSize());
		ok.addActionListener(this);
		cancel.addActionListener(this);

		add(ok);
		add(cancel);
		resizeButtons();
	}

	public void setOkText(String text)
	{
		ok.setText(text);
		resizeButtons();
	}

	public void setCancelText(String text)
	{
		cancel.setText(text);
		resizeButtons();
	}

	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == cancel)
			target.cancel();
		else if (ae.getSource() == ok)
			target.ok();
	}

	private void resizeButtons()
	{
		Dimension d1 = ok.getPreferredSize();
		Dimension d2 = cancel.getPreferredSize();
		if (d1.getWidth() > d2.getWidth())
			cancel.setPreferredSize(d1);
		else
			ok.setPreferredSize(d2);
	}

}

