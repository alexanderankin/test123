package gdb.options;

import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class GeneralOptionPane extends AbstractOptionPane {

	private JTextField gdbPathTF;
	private JTextField arrayRangeSplitSizeTF;
	private JCheckBox charArrayAsStringCB;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String GDB_PATH_LABEL = PREFIX + "gdb_path_label";
	static public final String GDB_PATH_PROP = PREFIX + "gdb_path";
	static final String ARRAY_RANGE_SPLIT_SIZE_LABEL = PREFIX + "array_range_split_size_label";
	static public final String ARRAY_RANGE_SPLIT_SIZE_PROP = PREFIX + "array_range_split_size";
	static final String CHAR_ARRAY_AS_STRING_LABEL = PREFIX + "char_array_as_string_label";
	static public final String CHAR_ARRAY_AS_STRING_PROP = PREFIX + "char_array_as_string";
	
	public GeneralOptionPane() {
		super("debugger.gdb");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		gdbPathTF = new JTextField(40);
		addComponent(jEdit.getProperty(GDB_PATH_LABEL), gdbPathTF);
		gdbPathTF.setText(jEdit.getProperty(GDB_PATH_PROP));
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
	}

	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void _save()
	{
		jEdit.setProperty(GDB_PATH_PROP, gdbPathTF.getText());
		jEdit.setIntegerProperty(ARRAY_RANGE_SPLIT_SIZE_PROP,
				Integer.valueOf(arrayRangeSplitSizeTF.getText()).intValue());
		jEdit.setBooleanProperty(CHAR_ARRAY_AS_STRING_PROP,
				charArrayAsStringCB.isSelected());
	}

}
