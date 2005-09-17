package console;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource.DefaultError;
import errorlist.DefaultErrorSource;
    /**
     * An object which hooks up a @ref Log to an @ref ErrorList.
     * Since it uses the ErrorMatchers of the Console, which depend
     * on the ErrorList, it makes sense to put this in the Console plugin.
     * 
     * I wrote this just to see how the ErrorList API worked.
     * 
     * @author ezust
     *
     */

	public class LogHandler implements ListDataListener
	{
	    DefaultErrorSource errorSource;
		ListModel model;
		ErrorMatcher matcher;

		LogHandler() {
			matcher  = ErrorMatcher.bring("generic");
			model = Log.getLogListModel();
			model.addListDataListener(this);
			errorSource = new DefaultErrorSource("Log Messages");
			errorSource.registerErrorSource(errorSource);
		}

		public void contentsChanged(ListDataEvent e)
		{
			
		}

		public void intervalAdded(ListDataEvent e)
		{
			int first = e.getIndex0();
			int last = e.getIndex1();
			
			for (int i=first; i<last; ++i) {
				String current = model.getElementAt(i).toString();

				if (matcher.matchLine(current) != null) 
					addError(matcher.type, matcher.file, matcher.line, matcher.message);
			}
		}
		void addError(int type, String file, String line, String message ) {
				DefaultError error = new DefaultError(errorSource,
							type, matcher.file, Integer.parseInt(line),
							0, 0, matcher.message);
					errorSource.addError(error);
		}
	
		public void intervalRemoved(ListDataEvent e)
		{
			// TODO Auto-generated method stub
		}

	}
