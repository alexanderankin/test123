package foldTools;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;

public class Plugin extends EditPlugin
{
	public static void showFoldContext(View view)
	{
		FoldContext fc = new FoldContext(view);
		String s = fc.toString();
		StringBuilder sb = new StringBuilder();
		for (char c: s.toCharArray())
		{
			if (c == '\t')
				sb.append("    ");
			else
				sb.append(c);
		}
		JOptionPane.showMessageDialog(view, sb.toString());
	}
}
