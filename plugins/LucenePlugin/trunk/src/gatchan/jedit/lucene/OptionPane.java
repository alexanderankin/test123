/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2011 Matthieu Casanova
 * Copyright (C) 2009, 2011 Shlomy Reinstein
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.jedit.lucene;

import java.awt.GridLayout;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.StandardUtilities;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane
{

	public static final String PREFIX = "lucene.options.";
	private static final String INCLUDE_GLOBS_OPTION = PREFIX + "IncludeGlobs";
	private static final String INCLUDE_GLOBS_LABEL = INCLUDE_GLOBS_OPTION + ".label";
	private static final String EXCLUDE_GLOBS_OPTION = PREFIX + "ExcludeGlobs";
	private static final String EXCLUDE_GLOBS_LABEL = EXCLUDE_GLOBS_OPTION + ".label";
	private static final String SEARCH_DOCKABLE_LABEL = PREFIX + "SearchDockable.label";
	private static final String SEARCH_STRING_LENGTH = PREFIX + "SearchStringLength";
	private static final String SEARCH_STRING_LENGTH_LABEL = SEARCH_STRING_LENGTH + ".label";
	private static final String USE_SHORT_LABELS = PREFIX + "ShortLabels";
	private static final String USE_SHORT_LABELS_LABEL = USE_SHORT_LABELS + ".label";
	private static Pattern include;
	private static Pattern exclude;

	private JTextField includeFilesTF;
	private JTextField excludeFilesTF;
	private JSpinner searchStringLength;
	private JCheckBox useShortLabels;

	public OptionPane()
	{
		super("Lucene");
	}

	@Override
	protected void _init()
	{
		setBorder(new EmptyBorder(5, 5, 5, 5));

		includeFilesTF = new JTextField(includeGlobs());
		addComponent(jEdit.getProperty(INCLUDE_GLOBS_LABEL), includeFilesTF);
		excludeFilesTF = new JTextField(excludeGlobs());
		addComponent(jEdit.getProperty(EXCLUDE_GLOBS_LABEL), excludeFilesTF);
		JPanel searchResultsPanel = new JPanel(new GridLayout(0, 1));
		searchResultsPanel.setBorder(BorderFactory.createTitledBorder(
			jEdit.getProperty(SEARCH_DOCKABLE_LABEL)));
		searchStringLength = new JSpinner(new SpinnerNumberModel(
			getSearchStringLength(), 0, 120, 1));
		JPanel p = new JPanel();
		p.add(new JLabel(jEdit.getProperty(SEARCH_STRING_LENGTH_LABEL)));
		p.add(searchStringLength);
		searchResultsPanel.add(p);
		useShortLabels = new JCheckBox(jEdit.getProperty(USE_SHORT_LABELS_LABEL),
			getUseShortLabels());
		searchResultsPanel.add(useShortLabels);
		addComponent(searchResultsPanel);
	}

	@Override
	public void _save()
	{
		jEdit.setProperty(INCLUDE_GLOBS_OPTION, includeFilesTF.getText());
		jEdit.setProperty(EXCLUDE_GLOBS_OPTION, excludeFilesTF.getText());
		jEdit.setIntegerProperty(SEARCH_STRING_LENGTH, (Integer) searchStringLength.getValue());
		jEdit.setBooleanProperty(USE_SHORT_LABELS, useShortLabels.isSelected());
		updateFilter();
	}

	public static String includeGlobs()
	{
		return jEdit.getProperty(INCLUDE_GLOBS_OPTION);
	}

	public static String excludeGlobs()
	{
		return jEdit.getProperty(EXCLUDE_GLOBS_OPTION);
	}

	public static int getSearchStringLength()
	{
		return jEdit.getIntegerProperty(SEARCH_STRING_LENGTH, 10);
	}

	public static boolean getUseShortLabels()
	{
		return jEdit.getBooleanProperty(USE_SHORT_LABELS);
	}

	private static Pattern globToPattern(String filter)
	{
		String[] parts = filter.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String part : parts)
		{
			if (sb.length() > 0)
				sb.append('|');
			String regexp = StandardUtilities.globToRE(part);
			sb.append(regexp);
		}
		return Pattern.compile(sb.toString());
	}

	private static void updateFilter()
	{
		include = globToPattern(includeGlobs());
		exclude = globToPattern(excludeGlobs());
	}

	public static boolean accept(String path)
	{
		if (include == null || exclude == null)
			updateFilter();
		return (include.matcher(path).matches() &&
			!exclude.matcher(path).matches());
	}
}
