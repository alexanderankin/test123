package lucene;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane {
	
	public static final String PREFIX = "options.LucenePlugin.";
	private static final String INDEX_PATH_OPTION = PREFIX + "index_path";
	private static final String INDEX_PATH_LABEL = INDEX_PATH_OPTION + ".label";
	private static final String EXCLUDE_DIRS_OPTION = PREFIX + "exclude_dirs"; 
	private static final String EXCLUDE_DIRS_LABEL = EXCLUDE_DIRS_OPTION + ".label";
	private static final String EXCLUDE_FILES_OPTION = PREFIX + "exclude_files"; 
	private static final String EXCLUDE_FILES_LABEL = EXCLUDE_FILES_OPTION + ".label";
	private static final String INCLUDE_FILES_OPTION = PREFIX + "include_files"; 
	private static final String INCLUDE_FILES_LABEL = INCLUDE_FILES_OPTION + ".label";

	private JTextField indexPathTF;
	private JTextField excludeDirsTF;
	private JTextField includeFilesTF;
	private JTextField excludeFilesTF;

	public OptionPane() {
		super("LucenePlugin");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		indexPathTF = new JTextField(indexPath());
		addComponent(jEdit.getProperty(INDEX_PATH_LABEL), indexPathTF);
		excludeDirsTF = new JTextField(excludeDirs());
		addComponent(jEdit.getProperty(EXCLUDE_DIRS_LABEL), excludeDirsTF);
		includeFilesTF = new JTextField(includeFiles());
		addComponent(jEdit.getProperty(INCLUDE_FILES_LABEL), includeFilesTF);
		excludeFilesTF = new JTextField(excludeFiles());
		addComponent(jEdit.getProperty(EXCLUDE_FILES_LABEL), excludeFilesTF);
	}
	public void save()
	{
		jEdit.setProperty(INDEX_PATH_OPTION, indexPathTF.getText());
		jEdit.setProperty(EXCLUDE_DIRS_OPTION, excludeDirsTF.getText());
		jEdit.setProperty(INCLUDE_FILES_OPTION, includeFilesTF.getText());
		jEdit.setProperty(EXCLUDE_FILES_OPTION, excludeFilesTF.getText());
	}

	static public String includeFiles()
	{
		return jEdit.getProperty(INCLUDE_FILES_OPTION);
	}
	static public String excludeFiles()
	{
		return jEdit.getProperty(EXCLUDE_FILES_OPTION);
	}
	static public String excludeDirs()
	{
		return jEdit.getProperty(EXCLUDE_DIRS_OPTION);
	}
	static public String indexPath()
	{
		return jEdit.getProperty(INDEX_PATH_OPTION);
	}

}
