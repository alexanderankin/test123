/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

Note: The code for the ctags path and the ctags version notice
was taken from the CodeBrowser plugin by Gerd Knops. 
*/

package ctags.sidekick.options;
import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import ctags.sidekick.Plugin;

/** ************************************************************************** */
public class GeneralOptionPane extends AbstractOptionPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***************************************************************************
	 * Vars
	 **************************************************************************/

	private JTextField ctagsPathTF;
	private JCheckBox showGroupSelector;
	private JCheckBox showSortSelector;
	private JCheckBox showFilterSelector;
	private JCheckBox showTextProviderSelector;
	private JCheckBox showIconProviderSelector;
	private JCheckBox show_icons;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	public static final String SHOW_GROUP_SELECTOR = PREFIX + "showGroupSelector";
	private static final String SHOW_GROUP_SELECTOR_LABEL = SHOW_GROUP_SELECTOR + ".label";
	public static final String SHOW_FILTER_SELECTOR = PREFIX + "showFilterSelector";
	private static final String SHOW_FILTER_SELECTOR_LABEL = SHOW_FILTER_SELECTOR + ".label";
	public static final String SHOW_SORT_SELECTOR = PREFIX + "showSortSelector";
	private static final String SHOW_SORT_SELECTOR_LABEL = SHOW_SORT_SELECTOR + ".label";
	public static final String SHOW_TEXT_PROVIDER_SELECTOR = PREFIX + "showTextProviderSelector";
	private static final String SHOW_TEXT_PROVIDER_SELECTOR_LABEL = SHOW_TEXT_PROVIDER_SELECTOR + ".label";
	public static final String SHOW_ICON_PROVIDER_SELECTOR = PREFIX + "showIconProviderSelector";
	private static final String SHOW_ICON_PROVIDER_SELECTOR_LABEL = SHOW_ICON_PROVIDER_SELECTOR + ".label";
	public static final String SORT = PREFIX + "sort";
	public static final String FOLDS_BEFORE_LEAFS = PREFIX + "sort_folds_first";
	public static final String SHOW_ICONS = PREFIX + "show_icons";
	static final String LABEL = "_label";
	
	public static final String ICONS = PREFIX + "icons.";
	
	public static final String PARSE_ACTION_PROP = "CtagsSideKick.parse.action";
	
	/***************************************************************************
	 * Factory methods
	 **************************************************************************/
	public GeneralOptionPane()
	{
		super("CtagsSideKick-general");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JTextArea ta = new JTextArea(jEdit
				.getProperty(PREFIX + "ctags_path_note"), 0, 60);
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setBackground(Color.yellow);

		addComponent(ta);

		addSeparator();

		addComponent(jEdit
				.getProperty(PREFIX + "ctags_path_label"),
				ctagsPathTF = new JTextField(jEdit
						.getProperty(PREFIX + "ctags_path"), 40));

		addSeparator();
		
		JPanel toolBarPanel = new JPanel();
		toolBarPanel.setBorder(new TitledBorder("Show in dockable toolbar:"));
		showGroupSelector = new JCheckBox(
				jEdit.getProperty(SHOW_GROUP_SELECTOR_LABEL),
				jEdit.getBooleanProperty(SHOW_GROUP_SELECTOR, true));
		toolBarPanel.add(showGroupSelector);
		showSortSelector = new JCheckBox(
				jEdit.getProperty(SHOW_SORT_SELECTOR_LABEL),
				jEdit.getBooleanProperty(SHOW_SORT_SELECTOR, true));
		toolBarPanel.add(showSortSelector);
		showFilterSelector = new JCheckBox(
				jEdit.getProperty(SHOW_FILTER_SELECTOR_LABEL),
				jEdit.getBooleanProperty(SHOW_FILTER_SELECTOR, true));
		toolBarPanel.add(showFilterSelector);
		showTextProviderSelector = new JCheckBox(
				jEdit.getProperty(SHOW_TEXT_PROVIDER_SELECTOR_LABEL),
				jEdit.getBooleanProperty(SHOW_TEXT_PROVIDER_SELECTOR, true));
		toolBarPanel.add(showTextProviderSelector);
		showIconProviderSelector = new JCheckBox(
				jEdit.getProperty(SHOW_ICON_PROVIDER_SELECTOR_LABEL),
				jEdit.getBooleanProperty(SHOW_ICON_PROVIDER_SELECTOR, true));
		toolBarPanel.add(showIconProviderSelector);
		
		addComponent(toolBarPanel);
		show_icons = new JCheckBox(
				jEdit.getProperty(SHOW_ICONS + LABEL),
				jEdit.getBooleanProperty(SHOW_ICONS, false));
		addComponent(show_icons);
	}

	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void save()
	{
		jEdit.setProperty(PREFIX + "ctags_path", ctagsPathTF.getText());
		jEdit.setBooleanProperty(SHOW_GROUP_SELECTOR, showGroupSelector.isSelected());
		jEdit.setBooleanProperty(SHOW_SORT_SELECTOR, showSortSelector.isSelected());
		jEdit.setBooleanProperty(SHOW_FILTER_SELECTOR, showFilterSelector.isSelected());
		jEdit.setBooleanProperty(SHOW_TEXT_PROVIDER_SELECTOR, showTextProviderSelector.isSelected());
		jEdit.setBooleanProperty(SHOW_ICON_PROVIDER_SELECTOR, showIconProviderSelector.isSelected());
		jEdit.setBooleanProperty(SHOW_ICONS, show_icons.isSelected());
		jEdit.getAction(jEdit.getProperty(PARSE_ACTION_PROP)).invoke(jEdit.getActiveView());
	}
}
/** ***********************************************************************EOF */

