package gdb.options;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

@SuppressWarnings("serial")
public class GeneralOptionPane extends AbstractOptionPane {

	private JTextField gdbPathTF;
	
	static final String PREFIX = Plugin.OPTION_PREFIX;
	
	static final String GDB_PATH_LABEL = PREFIX + "gdb_path_label";
	static public final String GDB_PATH_PROP = PREFIX + "gdb_path";
	
	public GeneralOptionPane() {
		super("debugger.gdb");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		gdbPathTF = new JTextField(40);
		addComponent(jEdit.getProperty(GDB_PATH_LABEL), gdbPathTF);
		gdbPathTF.setText(jEdit.getProperty(GDB_PATH_PROP));
	}

	/***************************************************************************
	 * Implementation
	 **************************************************************************/
	public void _save()
	{
		jEdit.setProperty(GDB_PATH_PROP, gdbPathTF.getText());
	}

}
