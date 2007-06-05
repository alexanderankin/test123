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

	private JTextField gdbPathTF;
	private JCheckBox showBreakpointPopupCB;
	private JCheckBox useJNICB;
	private JTextField arrayRangeSplitSizeTF;
	private JCheckBox charArrayAsStringCB;
	private JCheckBox expressionTooltipCB;
	private JTextField expressionRegExpTF;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String GDB_PATH_LABEL = PREFIX + "gdb_path_label";
	static public final String GDB_PATH_PROP = PREFIX + "gdb_path";
	static final String SHOW_BREAKPOINT_POPUP_LABEL = PREFIX + "show_breakpoint_popup_label";
	static public final String SHOW_BREAKPOINT_POPUP_PROP = PREFIX + "show_breakpoint_popup";
	static final String USE_JNI_LABEL = PREFIX + "use_jni_label";
	static final String USE_JNI_TOOLTIP = PREFIX + "use_jni_tooltip";
	static public final String USE_JNI_PROP = PREFIX + "use_jni";
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
		showBreakpointPopupCB = new JCheckBox(
				jEdit.getProperty(SHOW_BREAKPOINT_POPUP_LABEL));
		addComponent(showBreakpointPopupCB);
		showBreakpointPopupCB.setSelected(
				jEdit.getBooleanProperty(SHOW_BREAKPOINT_POPUP_PROP)); 
		useJNICB = new JCheckBox(
				jEdit.getProperty(USE_JNI_LABEL));
		addComponent(useJNICB);
		useJNICB.setSelected(
				jEdit.getBooleanProperty(USE_JNI_PROP)); 
		useJNICB.setToolTipText(jEdit.getProperty(USE_JNI_TOOLTIP));
		arrayRangeSplitSizeTF = new JTextField();
		addComponent(jEdit.getProperty(ARRAY_RANGE_SPLIT_SIZE_LABEL),
				arrayRangeSplitSizeTF);
		arrayRangeSplitSizeTF.setText(String.valueOf(
				jEdit.getIntegerProperty(ARRAY_RANGE_SPLIT_SIZE_PROP, 100)));
		arrayRangeSplitSizeTF.setInputVerifier(new InputVerifier() {
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
		});
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
		JPanel regexpPane = new JPanel();
		expressionRegExpTF = new JTextField(40);
		regexpPane.add(expressionRegExpTF);
		expressionRegExpTF.setToolTipText(jEdit.getProperty(EXPRESSION_REGEXP_TOOLTIP));
		expressionRegExpTF.setText(jEdit.getProperty(EXPRESSION_REGEXP_PROP));
		JButton resetRegexp = new JButton("Reset to default");
		regexpPane.add(resetRegexp);
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
	public void _save()
	{
		jEdit.setProperty(GDB_PATH_PROP, gdbPathTF.getText());
		jEdit.setBooleanProperty(SHOW_BREAKPOINT_POPUP_PROP, showBreakpointPopupCB.isSelected()); 
		jEdit.setBooleanProperty(USE_JNI_PROP, useJNICB.isSelected()); 
		jEdit.setIntegerProperty(ARRAY_RANGE_SPLIT_SIZE_PROP,
				Integer.valueOf(arrayRangeSplitSizeTF.getText()).intValue());
		jEdit.setBooleanProperty(CHAR_ARRAY_AS_STRING_PROP,
				charArrayAsStringCB.isSelected());
		jEdit.setBooleanProperty(EXPRESSION_TOOLTIP_PROP,
				expressionTooltipCB.isSelected());
		jEdit.setProperty(EXPRESSION_REGEXP_PROP, expressionRegExpTF.getText());
	}

}
