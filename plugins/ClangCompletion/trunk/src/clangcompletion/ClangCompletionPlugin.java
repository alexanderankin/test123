package clangcompletion;
//{{{ Imports
import java.io.*;
import org.gjt.sp.jedit.EditPlugin;
import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;
import errorlist.DefaultErrorSource.DefaultError;
//}}}
public class ClangCompletionPlugin extends EditPlugin
{
	public static  DefaultErrorSource errorSrc;
	
	public static File pluginHome;
	
	public void start()
	{
		pluginHome = getPluginHome();
		pluginHome.mkdirs();
		
		new BufferWatcher();
		if(errorSrc == null)
		{
			errorSrc = new DefaultErrorSource(this.getClass().getName());
			ErrorSource.registerErrorSource(errorSrc);
		}
	}
	public void stop() 
	{
		if(errorSrc != null)
		{
			
			ErrorSource.unregisterErrorSource(errorSrc);errorSrc = null;
		}
	}
}
