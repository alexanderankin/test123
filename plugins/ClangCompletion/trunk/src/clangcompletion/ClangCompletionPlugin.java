package clangcompletion;
import java.io.*;
import org.gjt.sp.jedit.EditPlugin;
import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;
import errorlist.DefaultErrorSource.DefaultError;
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
