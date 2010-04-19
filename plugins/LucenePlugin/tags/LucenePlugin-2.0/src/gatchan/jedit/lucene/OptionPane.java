package gatchan.jedit.lucene;

import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.StandardUtilities;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane
{

	public static final String PREFIX = "lucene.options.";
	private static final String INCLUDE_GLOBS_OPTION = PREFIX + "IncludeGlobs";
	private static final String INCLUDE_GLOBS_LABEL = INCLUDE_GLOBS_OPTION + ".label";
	private static final String EXCLUDE_GLOBS_OPTION = PREFIX + "ExcludeGlobs";
	private static final String EXCLUDE_GLOBS_LABEL = EXCLUDE_GLOBS_OPTION + ".label";
	private static Pattern include = null, exclude = null;

	private JTextField includeFilesTF;
	private JTextField excludeFilesTF;

	public OptionPane()
	{
		super("Lucene");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		includeFilesTF = new JTextField(includeGlobs());
		addComponent(jEdit.getProperty(INCLUDE_GLOBS_LABEL), includeFilesTF);
		excludeFilesTF = new JTextField(excludeGlobs());
		addComponent(jEdit.getProperty(EXCLUDE_GLOBS_LABEL), excludeFilesTF);
	}

	public void save()
	{
		jEdit.setProperty(INCLUDE_GLOBS_OPTION, includeFilesTF.getText());
		jEdit.setProperty(EXCLUDE_GLOBS_OPTION, excludeFilesTF.getText());
		updateFilter();
	}

	public static String includeGlobs()
	{
		return jEdit.getProperty(INCLUDE_GLOBS_OPTION);
	}

	public static String excludeGlobs()
	{
		return jEdit.getProperty(EXCLUDE_GLOBS_OPTION);
	}

	private static Pattern globToPattern(String filter)
	{
		String[] parts = filter.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String part : parts)
		{
			if (sb.length() > 0)
				sb.append("|");
			String regexp = StandardUtilities.globToRE(part);
			sb.append(regexp);
		}
		return Pattern.compile(sb.toString());
	}

	private static void updateFilter()
	{
		include = globToPattern(includeGlobs());
		exclude = globToPattern(excludeGlobs());
	}

	public static boolean accept(String path)
	{
		if (include == null || exclude == null)
			updateFilter();
		return (include.matcher(path).matches() &&
			!exclude.matcher(path).matches());
	}
}
