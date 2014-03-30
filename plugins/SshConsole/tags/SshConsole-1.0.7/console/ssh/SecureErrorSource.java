/*          DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE (WTFPL)
                    Version 4, October 2012
	    Based on the wtfpl: http://sam.zoy.org/wtfpl/

 Copyright © 2012 Alan Ezust

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FRAK YOU WANT TO.
  1. It is provided "as is" without any warranty whatsoever.
*/


package console.ssh;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import errorlist.ErrorSourceUpdate;

// {{{ class SecureErrorSource
/**
    A customized ErrorSource which prepends the proper sftp://
    address that corresponds to the current connection
    @author ezust
    @version $Id$
*/
public class SecureErrorSource extends DefaultErrorSource
{

	ConsoleState consoleState;

	public SecureErrorSource(ConsoleState cs, View v) {
		super("sshconsole", v);
		ErrorSource.registerErrorSource(this);
		consoleState = cs;
	}


    // {{{ addError() method
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
			try {
				error.setFilePath(path);
			}
			catch (Exception e) {
				// invalid path - don't create an error
				return;
			}
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

} // }}}
// :folding=explicit:
