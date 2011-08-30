/*
Copyright (C) 2007  Shlomy Reinstein

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
*/

package gdb.options;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class GeneralOptionPane extends AbstractOptionPane {

	private static final class NumberInputVerifier extends InputVerifier {
		@Override
		public boolean verify(JComponent arg0) {
			JTextField tf = (JTextField)arg0;
			String s = tf.getText();
			try {
				Integer.valueOf(s);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
	}

	private JTextField gdbPathTF;
	private JCheckBox useExternalCommandsCB;
	private JCheckBox showProgramListInPanelCB;
	private JCheckBox showBreakpointPopupCB;
	private JCheckBox showBreakpointErrorCB;
	private JTextField arrayRangeSplitSizeTF;
	private JCheckBox charArrayAsStringCB;
	private JCheckBox expressionTooltipCB;
	private JTextField expressionRegExpTF;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String GDB_PATH_LABEL = PREFIX + "gdb_path_label";
	static public final String GDB_PATH_PROP = PREFIX + "gdb_path";
	static final String USE_EXTERNAL_COMMANDS_LABEL = PREFIX + "use_external_commands_label";
	static public final String USE_EXTERNAL_COMMANDS_PROP = PREFIX + "use_external_commands";
	static final String SHOW_PROGRAM_LIST_IN_PANEL_LABEL = PREFIX + "show_program_list_in_panel_label";
	static public final String SHOW_PROGRAM_LIST_IN_PANEL_PROP = PREFIX + "show_program_list_in_panel";
	static final String SHOW_BREAKPOINT_POPUP_LABEL = PREFIX + "show_breakpoint_popup_label";
	static public final String SHOW_BREAKPOINT_POPUP_PROP = PREFIX + "show_breakpoint_popup";
	static final String SHOW_BREAKPOINT_ERROR_LABEL = PREFIX + "show_breakpoint_error_label";
	static public final String SHOW_BREAKPOINT_ERROR_PROP = PREFIX + "show_breakpoint_error";
	static final String ARRAY_RANGE_SPLIT_SIZE_LABEL = PREFIX + "array_range_split_size_label";
	static public final String ARRAY_RANGE_SPLIT_SIZE_PROP = PREFIX + "array_range_split_size";
	static final String CHAR_ARRAY_AS_STRING_LABEL = PREFIX + "char_array_as_string_label";
	static public final String CHAR_ARRAY_AS_STRING_PROP = PREFIX + "char_array_as_string";
	static final String EXPRESSION_TOOLTIP_LABEL = PREFIX + "expression_tooltip_label";
	static public final String EXPRESSION_TOOLTIP_PROP = PREFIX + "expression_tooltip";
	static final String EXPRESSION_REGEXP_LABEL = PREFIX + "expression_regexp_label";
	static final String EXPRESSION_REGEXP_TOOLTIP = PREFIX + "expression_regexp_tooltip";
	static public final String EXPRESSION_REGEXP_PROP = PREFIX + "expression_regexp";
	static public final String DEFAULT_EXPRESSION_REGEXP_PROP = PREFIX + "default_expression_regexp";
	
	public GeneralOptionPane() {
		super("debugger.gdb");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		gdbPathTF = new JTextField(40);
		addComponent(jEdit.getProperty(GDB_PATH_LABEL), gdbPathTF);
		gdbPathTF.setText(jEdit.getProperty(GDB_PATH_PROP));
		useExternalCommandsCB = new JCheckBox(
				jEdit.getProperty(USE_EXTERNAL_COMMANDS_LABEL));
		addComponent(useExternalCommandsCB);
		useExternalCommandsCB.setSelected(
				jEdit.getBooleanProperty(USE_EXTERNAL_COMMANDS_PROP)); 
		showProgramListInPanelCB = new JCheckBox(
				jEdit.getProperty(SHOW_PROGRAM_LIST_IN_PANEL_LABEL));
		addComponent(showProgramListInPanelCB);
		showProgramListInPanelCB.setSelected(
				jEdit.getBooleanProperty(SHOW_PROGRAM_LIST_IN_PANEL_PROP)); 
		showBreakpointPopupCB = new JCheckBox(
				jEdit.getProperty(SHOW_BREAKPOINT_POPUP_LABEL));
		addComponent(showBreakpointPopupCB);
		showBreakpointPopupCB.setSelected(
				jEdit.getBooleanProperty(SHOW_BREAKPOINT_POPUP_PROP)); 
		showBreakpointErrorCB = new JCheckBox(
				jEdit.getProperty(SHOW_BREAKPOINT_ERROR_LABEL));
		addComponent(showBreakpointErrorCB);
		showBreakpointErrorCB.setSelected(
				jEdit.getBooleanProperty(SHOW_BREAKPOINT_ERROR_PROP)); 
		arrayRangeSplitSizeTF = new JTextField();
		addComponent(jEdit.getProperty(ARRAY_RANGE_SPLIT_SIZE_LABEL),
				arrayRangeSplitSizeTF);
		arrayRangeSplitSizeTF.setText(String.valueOf(
				jEdit.getIntegerProperty(ARRAY_RANGE_SPLIT_SIZE_PROP, 100)));
		arrayRangeSplitSizeTF.setInputVerifier(new NumberInputVerifier());
		charArrayAsStringCB = new JCheckBox(
				jEdit.getProperty(CHAR_ARRAY_AS_STRING_LABEL));
		addComponent(charArrayAsStringCB);
		charArrayAsStringCB.setSelected(
				jEdit.getBooleanProperty(CHAR_ARRAY_AS_STRING_PROP)); 
		expressionTooltipCB = new JCheckBox(
				jEdit.getProperty(EXPRESSION_TOOLTIP_LABEL));
		addComponent(expressionTooltipCB);
		expressionTooltipCB.setSelected(
				jEdit.getBooleanProperty(EXPRESSION_TOOLTIP_PROP));
		JPanel regexpPane = new JPanel(new BorderLayout());
		expressionRegExpTF = new JTextField(40);
		regexpPane.add(expressionRegExpTF, BorderLayout.CENTER);
		expressionRegExpTF.setToolTipText(jEdit.getProperty(EXPRESSION_REGEXP_TOOLTIP));
		expressionRegExpTF.setText(jEdit.getProperty(EXPRESSION_REGEXP_PROP));
		JButton resetRegexp = new JButton("Reset to default");
		regexpPane.add(resetRegexp, BorderLayout.EAST);
		resetRegexp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				expressionRegExpTF.setText(
						jEdit.getProperty(DEFAULT_EXPRESSION_REGEXP_PROP));
			}
		});
		addComponent(jEdit.getProperty(EXPRESSION_REGEXP_LABEL), regexpPane);
	}

	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	@Override
	protected void _save()
	{
		jEdit.setProperty(GDB_PATH_PROP, gdbPathTF.getText());
		jEdit.setBooleanProperty(USE_EXTERNAL_COMMANDS_PROP, useExternalCommandsCB.isSelected()); 
		jEdit.setBooleanProperty(SHOW_PROGRAM_LIST_IN_PANEL_PROP, showProgramListInPanelCB.isSelected()); 
		jEdit.setBooleanProperty(SHOW_BREAKPOINT_POPUP_PROP, showBreakpointPopupCB.isSelected()); 
		jEdit.setBooleanProperty(SHOW_BREAKPOINT_ERROR_PROP, showBreakpointErrorCB.isSelected()); 
		jEdit.setIntegerProperty(ARRAY_RANGE_SPLIT_SIZE_PROP,
				Integer.valueOf(arrayRangeSplitSizeTF.getText()).intValue());
		jEdit.setBooleanProperty(CHAR_ARRAY_AS_STRING_PROP,
				charArrayAsStringCB.isSelected());
		jEdit.setBooleanProperty(EXPRESSION_TOOLTIP_PROP,
				expressionTooltipCB.isSelected());
		jEdit.setProperty(EXPRESSION_REGEXP_PROP, expressionRegExpTF.getText());
		jEdit.propertiesChanged();
	}

}
