package menuEditor;

import javax.swing.JDialog;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;

public class MenuEditorPlugin extends EditPlugin
{
	
	static public final String OPTION = "options.menuEditor.";
	static public final String MESSAGE = "messages.menuEditor.";

	public void start()
	{
	}

	public void stop()
	{
	}

	public static void showMenuEditor(View view)
	{
		JDialog dialog = new JDialog(view, "Menu Editor");
		dialog.setContentPane(new MenuEditor(view));
		dialog.pack();
		dialog.setVisible(true);
	}
}
