package clangcompletion;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClangCompletionOptionPane extends AbstractOptionPane 
{
	
	private static final long serialVersionUID = 1L;
	
	private JTextField clangPathTF;
	private JCheckBox passBufferCheckBox;
	// private JCheckBox macroCheckBox;
	private JCheckBox objcppSupportCheckBox;
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
		
		/* macroCheckBox = new JCheckBox(
			"Show macros from CtagsInterface",
			jEdit.getBooleanProperty("clangcompletion.show_macro", true));
		addComponent(macroCheckBox); */
		
		objcppSupportCheckBox = new JCheckBox(
			"Use ClangCompletion for objective-c++ (.mm)",
			jEdit.getBooleanProperty("clangcompletion.support_objcpp", true));
		addComponent(objcppSupportCheckBox);
	}
	
	
	@Override
	protected void _save()
	{
		jEdit.setProperty("clangcompletion.clang_path", clangPathTF.getText());
		jEdit.setBooleanProperty("clangcompletion.parse_buffer", passBufferCheckBox.isSelected());
		// jEdit.setBooleanProperty("clangcompletion.show_macro", macroCheckBox.isSelected());
		jEdit.setBooleanProperty("clangcompletion.support_objcpp", objcppSupportCheckBox.isSelected());
	}
}

