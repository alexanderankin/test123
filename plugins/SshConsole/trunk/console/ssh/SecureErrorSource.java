package console.ssh;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import errorlist.ErrorSourceUpdate;

public class SecureErrorSource extends DefaultErrorSource
{
	
	ConsoleState consoleState;
	
	public SecureErrorSource(ConsoleState cs) {
		super("sshconsole");
		ErrorSource.registerErrorSource(this);
		consoleState = cs;
	}

	
	public synchronized void addError(final DefaultError error)
	{
		if (error.getErrorSource() != this) {
			Log.log(Log.WARNING, this, "Why different error source?");
			ErrorSource.registerErrorSource(error.getErrorSource());
		}
		
		String path = error.getFilePath();
		if (!path.startsWith("sftp:")) {
			String base = ConnectionManager.extractBase(consoleState.getPath());
			path = base + path;
			error.setFilePath(path);
		}
		ErrorListForPath list = errors.get(path);
		if(list == null)
		{
			list = new ErrorListForPath();
			errors.put(path, list);
		}
		if(list.add(error))
		{
			errorCount++;
			removeOrAddToBus();
			if(registered)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						ErrorSourceUpdate message = new ErrorSourceUpdate(SecureErrorSource.this,
							ErrorSourceUpdate.ERROR_ADDED,error);
						EditBus.send(message);
					}
				});
			}
		}
	} //}}}

}
