package foldTools;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;

public class Plugin extends EditPlugin
{
	public static final String FOLD_HANDLER_SERVICE = "org.gjt.sp.jedit.buffer.FoldHandler";

	public void start()
	{
		OptionPane.loadModes(new OptionPane.LoadedModeProcessor()
		{
			public void process(HandlerItem handler)
			{
				handler.createService();
			}
		});
	}
	public void stop()
	{
		// no need to unload services here; will be done by plugin manager
	}

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
