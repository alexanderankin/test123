/*
 * XSearchOptionPane.java - plugin options pane for BufferList
 * Copyright (c) 2000,2001 Dirk Moebius
 *
 * :tabSize=2:indentSize=2:noTabs=false:maxLineLen=0:
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

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * This is the option pane that jEdit displays for XSearch's options.
 */
public class XSearchOptionPane extends AbstractOptionPane
// implements ActionListener
{

	private JCheckBox replaceBuiltInActions;
	private JCheckBox findAllButton;
	private JCheckBox resetButton;
	private JCheckBox hyperReplace;
	private JCheckBox replaceCaseSensitiv;
	private JCheckBox textAreaFont;
	private JCheckBox tabbedLayout;

	public XSearchOptionPane()
	{
		super("xsearch");
	}


	public void _init()
	{
		//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		replaceBuiltInActions = new JCheckBox(jEdit.getProperty("xsearch.options.replaceBuiltInActions"),
			ReplaceActions.isEnabled());
		replaceBuiltInActions.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.replaceBuiltInActions"));
		

		

		findAllButton = new JCheckBox(jEdit.getProperty("xsearch.options.findAllButton"),
			jEdit.getBooleanProperty("xsearch.findAllButton", true));
		findAllButton.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.findAllButton"));

		resetButton = new JCheckBox(jEdit.getProperty("xsearch.options.resetButton"),
			jEdit.getBooleanProperty("xsearch.resetButton", true));
		resetButton.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.resetButton"));

		hyperReplace = new JCheckBox(jEdit.getProperty("xsearch.options.hyperReplace"),
			jEdit.getBooleanProperty("xsearch.hyperReplace", true));
		hyperReplace.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.hyperReplace"));

		tabbedLayout= new JCheckBox(jEdit.getProperty("xsearch.options.tabbedLayout"),
			jEdit.getBooleanProperty("xsearch.tabbedLayout", true));
		tabbedLayout.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.tabbedLayout"));

		
		replaceCaseSensitiv = new JCheckBox(jEdit.getProperty("xsearch.options.replaceCaseSensitiv"),
			jEdit.getBooleanProperty("xsearch.replaceCaseSensitiv", true));
		replaceCaseSensitiv.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.replaceCaseSensitiv"));

		textAreaFont = new JCheckBox(jEdit.getProperty("xsearch.options.textAreaFont"),
			jEdit.getBooleanProperty("xsearch.textAreaFont", true));
		textAreaFont.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.textAreaFont"));

		
		
		addComponent(tabbedLayout);
		addComponent(replaceBuiltInActions);
		addComponent(hyperReplace);
		addComponent(replaceCaseSensitiv);
		addComponent(textAreaFont);

		JPanel bpanel = new JPanel();
		bpanel.setBorder(new TitledBorder(jEdit.getProperty("xsearch.options.buttonLabel")));
		bpanel.setLayout(new GridLayout(0,2));
		bpanel.add(findAllButton);
		bpanel.add(resetButton);
		addComponent(bpanel);
		

	}


	public void _save() {
		ReplaceActions.setEnabled(replaceBuiltInActions.isSelected());
		ReplaceActions.reset();
		jEdit.setBooleanProperty("xsearch.findAllButton", findAllButton.isSelected());
		jEdit.setBooleanProperty("xsearch.resetButton", resetButton.isSelected());
		jEdit.setBooleanProperty("xsearch.hyperReplace", hyperReplace.isSelected());
		jEdit.setBooleanProperty("xsearch.replaceCaseSensitiv", replaceCaseSensitiv.isSelected());
		jEdit.setBooleanProperty("xsearch.textAreaFont", textAreaFont.isSelected());
		jEdit.setBooleanProperty("xsearch.tabbedLayout", tabbedLayout.isSelected());
	}
	
}

