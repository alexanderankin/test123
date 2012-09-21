package clangcompletion;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.HashMap;
import java.util.Vector;
import java.io.*;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;

import projectviewer.ProjectManager;
import projectviewer.config.OptionsService;
import projectviewer.vpt.VPTProject;
import projectviewer.ProjectViewer;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class ClangCompletionOptionPane extends AbstractOptionPane 
{
	
	private static final long serialVersionUID = 1L;
	
	private JTextField clangPathTF;
	private JCheckBox passBufferCheckBox;
	
	public ClangCompletionOptionPane()
	{
		super("ClangCompletion");
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		clangPathTF = new JTextField(jEdit.getProperty( "clangcompletion.clang_path", "clang") , 40);
		addComponent("Clang Path: ", clangPathTF);
		
		passBufferCheckBox = new JCheckBox(
			"Parse buffer on file save",
			jEdit.getBooleanProperty("clangcompletion.parse_buffer", true));
		addComponent(passBufferCheckBox);
	}
	
	
	@Override
	protected void _save()
	{
		jEdit.setProperty("clangcompletion.clang_path", clangPathTF.getText());
		jEdit.setBooleanProperty("clangcompletion.parse_buffer", passBufferCheckBox.isSelected());
	}
}

