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

package ctags.sidekick;
import java.awt.Color;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/** ************************************************************************** */
public class OptionPane extends AbstractOptionPane {
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
	private JComboBox mapper;
	private JCheckBox show_icons;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	static final String SORT = PREFIX + "sort";
	static final String FOLDS_BEFORE_LEAFS = PREFIX + "sort_folds_first";
	static final String MAPPER = PREFIX + "mapper";
	static final String SHOW_ICONS = PREFIX + "show_icons";
	static final String LABEL = "_label";
	
	static final String NAMESPACE_MAPPER_NAME = MAPPER + ".namespace.name";
	static final String FLAT_NAMESPACE_MAPPER_NAME = MAPPER + ".flat_namespace.name";
	static final String KIND_MAPPER_NAME = MAPPER + ".kind.name";
	
	static final String ICONS = PREFIX + "icons.";
	
	static final String PARSE_ACTION_PROP = "CtagsSideKick.parse.action";
	
	/***************************************************************************
	 * Factory methods
	 **************************************************************************/
	public OptionPane()
	{
		super("CtagsSideKick");
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
		Vector<String> mappers = new Vector<String>();
		mappers.add(jEdit.getProperty(KIND_MAPPER_NAME));
		mappers.add(jEdit.getProperty(NAMESPACE_MAPPER_NAME));
		mappers.add(jEdit.getProperty(FLAT_NAMESPACE_MAPPER_NAME));
		JPanel mapperPanel = new JPanel();
		JLabel mapperLabel = new JLabel(jEdit.getProperty(MAPPER + LABEL));
		mapperPanel.add(mapperLabel);
		mapper = new JComboBox(mappers);
		mapper.setSelectedItem(jEdit.getProperty(MAPPER));
		mapperPanel.add(mapper);
		addComponent(mapperPanel);
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
		jEdit.setProperty(MAPPER, (String)mapper.getSelectedItem());
		jEdit.setBooleanProperty(SHOW_ICONS, show_icons.isSelected());
		jEdit.getAction(jEdit.getProperty(PARSE_ACTION_PROP)).invoke(jEdit.getActiveView());
	}
}
/** ***********************************************************************EOF */

