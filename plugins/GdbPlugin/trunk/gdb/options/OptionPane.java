package gdb.options;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane {

	private JTextField gdbPathTF;
	private JCheckBox singleVarsViewCB;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String SINGLE_VARS_VIEW_LABEL = PREFIX + "single_vars_view_label";
	static public final String SINGLE_VARS_VIEW_PROP = PREFIX + "single_vars_view";
	static final String GDB_PATH_LABEL = PREFIX + "gdb_path_label";
	static public final String GDB_PATH_PROP = PREFIX + "gdb_path";
	
	public OptionPane() {
		super("Gdb");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		gdbPathTF = new JTextField(40);
		addComponent(jEdit.getProperty(GDB_PATH_LABEL), gdbPathTF);
		gdbPathTF.setText(jEdit.getProperty(GDB_PATH_PROP));
		singleVarsViewCB = new JCheckBox(jEdit.getProperty(SINGLE_VARS_VIEW_LABEL),
				jEdit.getBooleanProperty(SINGLE_VARS_VIEW_PROP));
		addComponent(singleVarsViewCB);
	}

	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void save()
	{
		jEdit.setProperty(GDB_PATH_PROP, gdbPathTF.getText());
		jEdit.setBooleanProperty(SINGLE_VARS_VIEW_PROP, singleVarsViewCB.isSelected());
	}

}
