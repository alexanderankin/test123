/*
 * ErrorSourceUpdate.java - Message that an error source has changed
 * :tabSize=4:indentSize=4:noTabs=false:
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

import org.gjt.sp.jedit.*;

/**
 * Message that an error source has changed.
 * @author Slava Pestov
 */
public class ErrorSourceUpdate extends EBMessage
{
	//{{{ Message types
	/**
	 * An error source has been added. The message contains the errors
	 * collected by the source up to the moment of sending the message,
	 * available through <code>getErrors</code>.
	 */
	public static final Object ERROR_SOURCE_ADDED = "ERROR_SOURCE_ADDED";

	/**
	 * An error source has been removed.
	 */
	public static final Object ERROR_SOURCE_REMOVED = "ERROR_SOURCE_REMOVED";

	/**
	 * An error has been added. The message contains the error,
	 * available through <code>getError</code>.
	 */
	public static final Object ERROR_ADDED = "ERROR_ADDED";

	/**
	 * An error has been removed. The message contains the error,
	 * available through <code>getError</code>.
	 */
	public static final Object ERROR_REMOVED = "ERROR_REMOVED";

	/**
	 * All errors have been removed from this source.
	 */
	public static final Object ERRORS_CLEARED = "ERRORS_CLEARED";
	//}}}

	//{{{ ErrorSourceUpdate constructors
	/**
	 * Creates a new error source update message. This constructor
	 * does not fill neither <code>error</code> nor <code>errors</code>.
	 * @param what What changed
	 * @param errorSource The error source
	 */
	public ErrorSourceUpdate(ErrorSource errorSource, Object what)
	{
		super(null);
		if(what == null || errorSource == null)
			throw new NullPointerException("What and error source must be non-null");

		this.what = what;
		this.errorSource = errorSource;
	}

	/**
	 * Creates a new error source update message, with
	 * <code>error</code> member filled.
	 * @param what What changed
	 * @param errorSource The error source
	 * @param error The error. Null unless <code>what</code> is
	 * <code>ERROR_ADDED</code> or <code>ERROR_REMOVED</code>
	 */
	public ErrorSourceUpdate(ErrorSource errorSource, Object what,
		ErrorSource.Error error)
	{
		this(errorSource, what);
		this.error = error;
	}

	/**
	 * Creates a new error source update message with
	 * <code>errors</code> member. Used only by
	 * <code>ERROR_SOURCE_ADDED</code>.
	 * @param what What changed
	 * @param errorSource The error source
	 * @param errors The errors
	 */
	public ErrorSourceUpdate(ErrorSource errorSource, Object what,
		ErrorSource.Error[] errors)
	{
		this(errorSource, what);
		this.errors = errors;
	}

	//}}}

	//{{{ getWhat() method
	/**
	 * Returns what changed.
	 */
	public Object getWhat()
	{
		return what;
	} //}}}

	//{{{ getErrorSource() method
	/**
	 * Returns the error source.
	 */
	public ErrorSource getErrorSource()
	{
		return errorSource;
	} //}}}

	//{{{ getError() method
	/**
	 * Returns the error involved. Null unless <code>what</code>
	 * is <code>ERROR_ADDED</code> or <code>ERROR_REMOVED</code>.
	 */
	public ErrorSource.Error getError()
	{
		return error;
	} //}}}

	//{{{ getErrors() method
	/**
	 * Returns the errors involved. Only for
	 * <code>ERROR_SOURCE_ADDED</code>, otherwise <code>null</code>.
	 */
	public ErrorSource.Error[] getErrors()
	{
		return errors;
	} //}}}

	//{{{ paramString() method
	public String paramString()
	{
		return super.paramString() + ",what=" + what
			+ ",errorSource=" + errorSource
			+ ",error=" + error;
	} //}}}

	//{{{ Private members
	private Object what;
	private ErrorSource errorSource;
	private ErrorSource.Error error;
	private ErrorSource.Error[] errors;
	//}}}
}
