/*
 * ErrorSource.java - An error source
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package errorlist;

//{{{ Imports
import org.gjt.sp.jedit.*;
import java.util.Vector;
//}}}

/**
 * An error source.
 * @author Slava Pestov
 */
public abstract class ErrorSource implements EBComponent
{
	//{{{ Static part

	//{{{ registerErrorSource() method
	/**
	 * Registers an error source.
	 * @param errorSource The error source
	 */
	public static void registerErrorSource(ErrorSource errorSource)
	{
		if(errorSource.registered)
			return;

		synchronized(errorSources)
		{
			errorSources.addElement(errorSource);
			errorSource.registered = true;
			cachedErrorSources = null;
			EditBus.send(new ErrorSourceUpdate(errorSource,
				ErrorSourceUpdate.ERROR_SOURCE_ADDED,null));
		}
	} //}}}

	//{{{ unregisterErrorSource() method
	/**
	 * Unregisters an error source.
	 * @param errorSource The error source
	 */
	public static void unregisterErrorSource(ErrorSource errorSource)
	{
		if(!errorSource.registered)
			return;

		EditBus.removeFromBus(errorSource);

		synchronized(errorSources)
		{
			errorSources.removeElement(errorSource);
			errorSource.registered = false;
			cachedErrorSources = null;
			EditBus.send(new ErrorSourceUpdate(errorSource,
				ErrorSourceUpdate.ERROR_SOURCE_REMOVED,null));
		}
	} //}}}

	//{{{ getErrorSources() method
	/**
	 * Returns an array of registered error sources.
	 */
	public static ErrorSource[] getErrorSources()
	{
		synchronized(errorSources)
		{
			if(cachedErrorSources == null)
			{
				cachedErrorSources = new ErrorSource[
					errorSources.size()];
				errorSources.copyInto(cachedErrorSources);
			}
			return cachedErrorSources;
		}
	} //}}}

	//}}}

	//{{{ Constants
	/**
	 * An error.
	 */
	public static final int ERROR = 0;

	/**
	 * A warning.
	 */
	public static final int WARNING = 1;
	//}}}

	//{{{ getName() method
	/**
	 * Returns a string description of this error source.
	 */
	public abstract String getName();
	//}}}

	//{{{ getErrorCount() method
	/**
	 * Returns the number of errors in this source.
	 */
	public abstract int getErrorCount();
	//}}}

	//{{{ getAllErrors() method
	/**
	 * Returns an array of all errors in this error source.
	 */
	public abstract Error[] getAllErrors();
	//}}}

	//{{{ getFileErrorCount() method
	/**
	 * Returns the number of errors in the specified file.
	 * @param path Full path name
	 */
	public abstract int getFileErrorCount(String path);
	//}}}

	//{{{ getFileErrors() method
	/**
	 * Returns all errors in the specified file.
	 * @param path Full path name
	 */
	public abstract Error[] getFileErrors(String path);
	//}}}

	//{{{ getLineErrors() method
	/**
	 * Returns all errors in the specified line range.
	 * @param startLineIndex The line number
	 * @param endLineIndex The line number
	 * @since ErrorList 1.3
	 */
	public abstract ErrorSource.Error[] getLineErrors(Buffer buffer,
		int startLineIndex, int endLineIndex);
	//}}}

	//{{{ Private members

	// unregistered error sources do not fire events.
	// the console uses this fact to 'batch' multiple errors together
	// for improved performance
	protected boolean registered;

	private static Vector errorSources = new Vector();
	private static ErrorSource[] cachedErrorSources;
	//}}}

	//{{{ Error interface
	/**
	 * An error.
	 */
	public interface Error
	{
		//{{{ getErrorType() method
		/**
		 * Returns the error type (error or warning)
		 */
		int getErrorType();
		//}}}

		//{{{ getErrorSource() method
		/**
		 * Returns the source of this error.
		 */
		ErrorSource getErrorSource();
		//}}}

		//{{{ getBuffer() method
		/**
		 * Returns the buffer involved, or null if it is not open.
		 */
		Buffer getBuffer();
		//}}}

		//{{{ getFilePath() method
		/**
		 * Returns the file path name involved.
		 */
		String getFilePath();
		//}}}

		//{{{ getFileName() method
		/**
		 * Returns just the name portion of the file involved.
		 */
		String getFileName();
		//}}}

		//{{{ getLineNumber() method
		/**
		 * Returns the line number.
		 */
		int getLineNumber();
		//}}}

		//{{{ getStartOffset() method
		/**
		 * Returns the start offset.
		 */
		int getStartOffset();
		//}}}

		//{{{ getEndOffset() method
		/**
		 * Returns the end offset.
		 */
		int getEndOffset();
		//}}}

		//{{{ getErrorMessage() method
		/**
		 * Returns the error message.
		 */
		String getErrorMessage();
		//}}}

		//{{{ getExtraMessages() method
		/**
		 * Returns the extra error messages.
		 */
		String[] getExtraMessages();
		//}}}
	} //}}}
}
