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
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

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
		//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		replaceBuiltInActions = new JCheckBox(jEdit.getProperty("xsearch.options.replaceBuiltInActions"),
			ReplaceActions.isEnabled());
		replaceBuiltInActions.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.replaceBuiltInActions"));
		
		wordPartSearch = new JCheckBox(jEdit.getProperty("xsearch.options.wordPartSearch"),
			jEdit.getBooleanProperty("xsearch.wordPartSearch", true));
		wordPartSearch.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.wordPartSearch"));

		columnSearch = new JCheckBox(jEdit.getProperty("xsearch.options.columnSearch"),
			jEdit.getBooleanProperty("xsearch.columnSearch", true));
		columnSearch.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.columnSearch"));

		rowSearch = new JCheckBox(jEdit.getProperty("xsearch.options.rowSearch"),
			jEdit.getBooleanProperty("xsearch.rowSearch", true));
		rowSearch.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.rowSearch"));

		foldSearch = new JCheckBox(jEdit.getProperty("xsearch.options.foldSearch"),
			jEdit.getBooleanProperty("xsearch.foldSearch", true));
		foldSearch.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.foldSearch"));

		commentSearch = new JCheckBox(jEdit.getProperty("xsearch.options.commentSearch"),
			jEdit.getBooleanProperty("xsearch.commentSearch", true));
		commentSearch.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.commentSearch"));

		tentativSearch = new JCheckBox(jEdit.getProperty("xsearch.options.tentativSearch"),
			jEdit.getBooleanProperty("xsearch.tentativSearch", false));
		tentativSearch.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.tentativSearch"));

		hyperRange = new JCheckBox(jEdit.getProperty("xsearch.options.hyperRange"),
			jEdit.getBooleanProperty("xsearch.hyperRange", true));
		hyperRange.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.hyperRange"));

		settingsHistory = new JCheckBox(jEdit.getProperty("xsearch.options.settingsHistory"),
			jEdit.getBooleanProperty("xsearch.settingsHistory", true));
		settingsHistory.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.settingsHistory"));

		findAllButton = new JCheckBox(jEdit.getProperty("xsearch.options.findAllButton"),
			jEdit.getBooleanProperty("xsearch.findAllButton", true));
		findAllButton.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.findAllButton"));

		resetButton = new JCheckBox(jEdit.getProperty("xsearch.options.resetButton"),
			jEdit.getBooleanProperty("xsearch.resetButton", true));
		resetButton.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.resetButton"));

		hyperReplace = new JCheckBox(jEdit.getProperty("xsearch.options.hyperReplace"),
			jEdit.getBooleanProperty("xsearch.hyperReplace", true));
		hyperReplace.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.hyperReplace"));

		replaceCaseSensitiv = new JCheckBox(jEdit.getProperty("xsearch.options.replaceCaseSensitiv"),
			jEdit.getBooleanProperty("xsearch.replaceCaseSensitiv", true));
		replaceCaseSensitiv.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.replaceCaseSensitiv"));

		textAreaFont = new JCheckBox(jEdit.getProperty("xsearch.options.textAreaFont"),
			jEdit.getBooleanProperty("xsearch.textAreaFont", true));
		textAreaFont.setToolTipText(jEdit.getProperty("xsearch.options.tooltip.textAreaFont"));

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
		
		JPanel panel = new JPanel ();
		panel.setBorder(new TitledBorder(jEdit.getProperty("xsearch.options.checkBoxLabel")));
		panel.setLayout(new GridLayout(0, 2));
		panel.add(wordPartSearch);
		panel.add(columnSearch);
		panel.add(rowSearch);
		panel.add(foldSearch);
		panel.add(commentSearch);
		panel.add(tentativSearch);
		panel.add(hyperRange);
		panel.add(settingsHistory);
		addComponent(panel);
		
/*		addComponent(wordPartSearch);
		addComponent(columnSearch);
		addComponent(rowSearch);
		addComponent(foldSearch);
		addComponent(commentSearch);
		addComponent(tentativSearch);
		addComponent(hyperRange);
		addComponent(settingsHistory);
*/



	}

//	public void actionPerformed(ActionEvent e) {
//	}

	public void _save() {
		ReplaceActions.setEnabled(replaceBuiltInActions.isSelected());
		ReplaceActions.reset();
		jEdit.setBooleanProperty("xsearch.wordPartSearch", wordPartSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.columnSearch", columnSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.rowSearch", rowSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.foldSearch", foldSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.commentSearch", commentSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.tentativSearch", tentativSearch.isSelected());
		jEdit.setBooleanProperty("xsearch.hyperRange", hyperRange.isSelected());
		jEdit.setBooleanProperty("xsearch.settingsHistory", settingsHistory.isSelected());
		jEdit.setBooleanProperty("xsearch.findAllButton", findAllButton.isSelected());
		jEdit.setBooleanProperty("xsearch.resetButton", resetButton.isSelected());
		jEdit.setBooleanProperty("xsearch.hyperReplace", hyperReplace.isSelected());
		jEdit.setBooleanProperty("xsearch.replaceCaseSensitiv", replaceCaseSensitiv.isSelected());
		jEdit.setBooleanProperty("xsearch.textAreaFont", textAreaFont.isSelected());
		
		// reset settings which are not desired
		if (!wordPartSearch.isSelected())
			SearchAndReplace.setWordPartOption(XSearch.SEARCH_PART_NONE);
		if (!columnSearch.isSelected())
			SearchAndReplace.resetColumnSearch();
		if (!rowSearch.isSelected())
			SearchAndReplace.resetRowSearch();
		if (!foldSearch.isSelected())
			SearchAndReplace.setFoldOption(XSearch.SEARCH_IN_OUT_NONE);
		if (!commentSearch.isSelected())
			SearchAndReplace.setCommentOption(XSearch.SEARCH_IN_OUT_NONE);
		if (!tentativSearch.isSelected())
			SearchAndReplace.setTentativOption(false);
		if (!hyperRange.isSelected())
			SearchAndReplace.setHyperRange(-1, -1);
//		XSearchPanel.getSearchPanel(jEdit.getActiveView());
	}

	private JCheckBox replaceBuiltInActions;
	private JCheckBox wordPartSearch;
	private JCheckBox columnSearch;
	private JCheckBox rowSearch;
	private JCheckBox foldSearch;
	private JCheckBox commentSearch;
	private JCheckBox tentativSearch;
	private JCheckBox hyperRange;
	private JCheckBox findAllButton;
	private JCheckBox resetButton;
	private JCheckBox settingsHistory;
	private JCheckBox hyperReplace;
	private JCheckBox replaceCaseSensitiv;
	private JCheckBox textAreaFont;
}

