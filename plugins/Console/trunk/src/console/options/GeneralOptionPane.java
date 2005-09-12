/*
 * GeneralOptionPane.java - General settings
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2004 Slava Pestov
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

package console.options;

// {{{ Imports
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.FontSelector;

public class GeneralOptionPane extends AbstractOptionPane {
 
	private static final long serialVersionUID = 11234516L;

	public GeneralOptionPane() {
		super("console.general");
	} 

	// {{{ Protected members

	// {{{ _init() method
	protected void _init() {

		font = new FontSelector(jEdit.getFontProperty("console.font"));
		addComponent(jEdit.getProperty("options.console.general.font"), font);

		addComponent(jEdit.getProperty("options.console.general.bgColor"),
				bgColor = createColorButton("console.bgColor"));
		addComponent(jEdit.getProperty("options.console.general.plainColor"),
				plainColor = createColorButton("console.plainColor"));
		addComponent(jEdit.getProperty("options.console.general.caretColor"),
				caretColor = createColorButton("console.caretColor"));
		addComponent(jEdit.getProperty("options.console.general.infoColor"),
				infoColor = createColorButton("console.infoColor"));
		addComponent(jEdit.getProperty("options.console.general.warningColor"),
				warningColor = createColorButton("console.warningColor"));
		addComponent(jEdit.getProperty("options.console.general.errorColor"),
				errorColor = createColorButton("console.errorColor"));
	} // }}}

	// {{{ _save() method
	protected void _save() {
		
		jEdit.setFontProperty("console.font", font.getFont());

		jEdit.setColorProperty("console.bgColor", bgColor.getBackground());
		jEdit
				.setColorProperty("console.plainColor", plainColor
						.getBackground());
		jEdit
				.setColorProperty("console.caretColor", caretColor
						.getBackground());
		jEdit.setColorProperty("console.infoColor", infoColor.getBackground());
		jEdit.setColorProperty("console.warningColor", warningColor
				.getBackground());
		jEdit
				.setColorProperty("console.errorColor", errorColor
						.getBackground());
	} // }}}

	// }}}

	// {{{ Private members

	private FontSelector font;

	private JButton bgColor;

	private JButton plainColor;

	private JButton caretColor;

	private JButton infoColor;

	private JButton warningColor;

	private JButton errorColor;

	// }}}

	// {{{ createColorButton() method
	private JButton createColorButton(String property) {
		final JButton b = new JButton(" ");
		b.setBackground(jEdit.getColorProperty(property));
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Color c = JColorChooser.showDialog(GeneralOptionPane.this,
						jEdit.getProperty("colorChooser.title"), b
								.getBackground());
				if (c != null)
					b.setBackground(c);
			}
		});

		b.setRequestFocusEnabled(false);
		return b;
	} // }}}

	// }}}
}
