package clangcompletion;
import org.gjt.sp.jedit.EditPlugin;

public class ClangCompletionPlugin extends EditPlugin
{
	public void start()
	{
		getPluginHome().mkdirs();
		new BufferWatcher();
	}
	public void stop() 
	{
	}
}
