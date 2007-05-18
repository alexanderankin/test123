package gdb.options;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class GeneralOptionPane extends AbstractOptionPane {

	private JTextField gdbPathTF;
	private JTextField childDisplayLimitTF;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String GDB_PATH_LABEL = PREFIX + "gdb_path_label";
	static public final String GDB_PATH_PROP = PREFIX + "gdb_path";
	static final String CHILD_DISPLAY_LIMIT_LABEL = "child_display_limit_label";
	static public final String CHILD_DISPLAY_LIMIT_PROP = "child_display_limit";
	
	public GeneralOptionPane() {
		super("debugger.gdb");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		gdbPathTF = new JTextField(40);
		addComponent(jEdit.getProperty(GDB_PATH_LABEL), gdbPathTF);
		gdbPathTF.setText(jEdit.getProperty(GDB_PATH_PROP));
		childDisplayLimitTF = new JTextField();
		addComponent(jEdit.getProperty(CHILD_DISPLAY_LIMIT_LABEL),
				childDisplayLimitTF);
		childDisplayLimitTF.setText(String.valueOf(
				jEdit.getIntegerProperty(CHILD_DISPLAY_LIMIT_PROP, 100)));
		childDisplayLimitTF.setInputVerifier(new InputVerifier() {
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
	}

	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void _save()
	{
		jEdit.setProperty(GDB_PATH_PROP, gdbPathTF.getText());
		jEdit.setIntegerProperty(CHILD_DISPLAY_LIMIT_PROP,
				Integer.valueOf(childDisplayLimitTF.getText()).intValue());
	}

}
