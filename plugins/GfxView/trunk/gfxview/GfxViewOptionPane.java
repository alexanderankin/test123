//{{{ imports
import java.awt.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
//}}}

public class GfxViewOptionPane extends AbstractOptionPane {
//	private JTextField historyFile;
	private JTextField historyLimits;

	//{{{ +GfxViewOptionPane() : <init>
	public GfxViewOptionPane() {
		super(GfxViewPlugin.NAME);
	}//}}}
	
	//{{{ +_init() : void
	public void _init() {
/*
		historyFile = new JTextField(
			jEdit.getProperty(GfxViewPlugin.OPTION_PREFIX + "historyfile"));
		addComponent(jEdit.getProperty(GfxViewPlugin.OPTION_PREFIX + "historyfile.label"),
			historyFile);
*/
		historyLimits = new JTextField(
			jEdit.getProperty(GfxViewPlugin.OPTION_PREFIX + "historylimits"));
		addComponent(jEdit.getProperty(GfxViewPlugin.OPTION_PREFIX + "historylimits.label"),
			historyLimits);
	}//}}}

	//{{{ +_save() : void
	public void _save() {
/*
		jEdit.setProperty(GfxViewPlugin.OPTION_PREFIX + "historyfile",
			historyFile.getText());
*/
		jEdit.setProperty(GfxViewPlugin.OPTION_PREFIX + "historylimits",
			historyLimits.getText());
	}//}}}
}

/* :folding=explicit:tabSize=2:indentSize=2: */
