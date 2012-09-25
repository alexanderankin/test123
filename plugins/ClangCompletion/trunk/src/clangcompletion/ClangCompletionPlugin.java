package clangcompletion;
import org.gjt.sp.jedit.EditPlugin;

public class ClangCompletionPlugin extends EditPlugin
{
	BufferWatcher bw;
	public void start()
	{
		getPluginHome().mkdirs();
		bw = new BufferWatcher();
	}
	public void stop() 
	{
		bw.shutdown();
		bw = null;
	}
}
