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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

/**
 * This is the option pane that jEdit displays for XSearch's options.
 */
public class XSearchOptionPane extends AbstractOptionPane
// implements ActionListener
{

	public XSearchOptionPane()
	{
		super("xsearch");
	}


	public void _init()
	{
		wordPartSearch = new JCheckBox(jEdit.getProperty("xsearch.options.wordPartSearch"),
			jEdit.getBooleanProperty("xsearch.wordPartSearch", true));

		columnSearch = new JCheckBox(jEdit.getProperty("xsearch.options.columnSearch"),
			jEdit.getBooleanProperty("xsearch.columnSearch", true));

		rowSearch = new JCheckBox(jEdit.getProperty("xsearch.options.rowSearch"),
			jEdit.getBooleanProperty("xsearch.rowSearch", true));

		foldSearch = new JCheckBox(jEdit.getProperty("xsearch.options.foldSearch"),
			jEdit.getBooleanProperty("xsearch.foldSearch", true));

		commentSearch = new JCheckBox(jEdit.getProperty("xsearch.options.commentSearch"),
			jEdit.getBooleanProperty("xsearch.commentSearch", true));

		tentativSearch = new JCheckBox(jEdit.getProperty("xsearch.options.tentativSearch"),
			jEdit.getBooleanProperty("xsearch.tentativSearch", false));

		replaceCaseSensitiv = new JCheckBox(jEdit.getProperty("xsearch.options.replaceCaseSensitiv"),
			jEdit.getBooleanProperty("xsearch.replaceCaseSensitiv", true));

		hyperRange = new JCheckBox(jEdit.getProperty("xsearch.options.hyperRange"),
			jEdit.getBooleanProperty("xsearch.hyperRange", true));

		findAllButton = new JCheckBox(jEdit.getProperty("xsearch.options.findAllButton"),
			jEdit.getBooleanProperty("xsearch.findAllButton", true));

		resetButton = new JCheckBox(jEdit.getProperty("xsearch.options.resetButton"),
			jEdit.getBooleanProperty("xsearch.resetButton", true));


		addSeparator("xsearch.options.separator.searchoptions");
		addComponent(wordPartSearch);
		addComponent(columnSearch);
		addComponent(rowSearch);
		addComponent(foldSearch);
		addComponent(commentSearch);
		addComponent(tentativSearch);
		addComponent(replaceCaseSensitiv);
		addComponent(hyperRange);

		addSeparator("xsearch.options.separator.buttons");
		addComponent(findAllButton);
		addComponent(resetButton);

	}

//	public void actionPerformed(ActionEvent e) {
//	}

	public void _save() {
		jEdit.setBooleanProperty("xsearch.wordPartSearch", wordPartSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.columnSearch", columnSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.rowSearch", rowSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.foldSearch", foldSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.commentSearch", commentSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.tentativSearch", tentativSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.replaceCaseSensitiv", replaceCaseSensitiv.isSelected());
		jEdit.setBooleanProperty("xsearch.hyperRange", hyperRange.isSelected());
		jEdit.setBooleanProperty("xsearch.findAllButton", findAllButton.isSelected());
		jEdit.setBooleanProperty("xsearch.resetButton", resetButton.isSelected());

	}

	private JCheckBox wordPartSearch;
	private JCheckBox columnSearch;
	private JCheckBox rowSearch;
	private JCheckBox foldSearch;
	private JCheckBox commentSearch;
	private JCheckBox tentativSearch;
	private JCheckBox replaceCaseSensitiv;
	private JCheckBox hyperRange;
	private JCheckBox findAllButton;
	private JCheckBox resetButton;
}

