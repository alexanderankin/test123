/*
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
package textfilter;

//{{{ Imports
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.EnhancedDialog;
//}}}

/**
 *  A panel with an OK and a Cancel button to the added to EnhancedDialogs. It
 *	also sets the "OK" button to be the default button.
 *
 *	@author		Marcelo Vanzin
 *  @version	$Id$
 */
public class OkCancelButtonPane extends JPanel
									implements ActionListener {

	//{{{ Private members
	private EnhancedDialog parent;
	private JButton okBtn;
	private JButton cancelBtn;
	//}}}

	//{{{ +OkCancelButtonPane(EnhancedDialog) : <init>
	public OkCancelButtonPane(EnhancedDialog parent) {
		this(parent, jEdit.getProperty("common.ok"));
	} //}}}

	//{{{ +OkCancelButtonPane(EnhancedDialog) : <init>
	public OkCancelButtonPane(EnhancedDialog parent, String okLabel) {
		this.parent = parent;

		// shamelessly copied from ContextOptionPane class, and slightly modded
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		okBtn = new JButton(okLabel);
		okBtn.addActionListener(this);
		parent.getRootPane().setDefaultButton(okBtn);
		add(okBtn);
		cancelBtn = new JButton(jEdit.getProperty("common.cancel"));
		cancelBtn.addActionListener(this);
		add(cancelBtn);
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == okBtn)
			parent.ok();
		else
			parent.cancel();
	} //}}}

}

