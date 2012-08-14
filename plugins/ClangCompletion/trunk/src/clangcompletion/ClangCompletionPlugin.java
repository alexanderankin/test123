package clangcompletion;
//{{{ Imports
import org.gjt.sp.jedit.EditPlugin;
import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;
import errorlist.DefaultErrorSource.DefaultError;
//}}}
public class ClangCompletionPlugin extends EditPlugin
{
	public static  DefaultErrorSource errorSrc;
	public void start()
	{
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
