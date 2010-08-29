package menuEditor;

import org.gjt.sp.jedit.EditPlugin;

public class MenuEditorPlugin extends EditPlugin
{
	static public final String OPTION = "options.menuEditor.";
	static public final String MESSAGE = "messages.menuEditor.";

	public void start()
	{
		MenuEditor.start();
	}

	public void stop()
	{
		MenuEditor.stop();
	}
}
