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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	private JCheckBox sort;
	private JCheckBox folds_first;
	private JCheckBox show_icons;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	public static final String SORT = PREFIX + "sort";
	public static final String FOLDS_BEFORE_LEAFS = PREFIX + "sort_folds_first";
	public static final String SHOW_ICONS = PREFIX + "show_icons";
	static final String LABEL = "_label";
	
	public static final String ICONS = PREFIX + "icons.";
	
	static final String PARSE_ACTION_PROP = "CtagsSideKick.parse.action";
	
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
		
		JPanel sortPanel = new JPanel();
		sort = new JCheckBox(
				jEdit.getProperty(SORT + LABEL),
				jEdit.getBooleanProperty(SORT, false));
		sortPanel.add(sort);
		folds_first = new JCheckBox(
				jEdit.getProperty(FOLDS_BEFORE_LEAFS + LABEL),
				jEdit.getBooleanProperty(FOLDS_BEFORE_LEAFS, true));
		sortPanel.add(folds_first);
		sort.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0) {
				folds_first.setEnabled(sort.isSelected());
			}			
		});
		addComponent(sortPanel);
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
		jEdit.setProperty("options.CtagsSideKick.ctags_path", ctagsPathTF
				.getText());
		jEdit.setBooleanProperty(SORT, sort.isSelected());
		jEdit.setBooleanProperty(FOLDS_BEFORE_LEAFS, folds_first.isSelected());
		jEdit.setBooleanProperty(SHOW_ICONS, show_icons.isSelected());
		jEdit.getAction(jEdit.getProperty(PARSE_ACTION_PROP)).invoke(jEdit.getActiveView());
	}
}
/** ***********************************************************************EOF */

