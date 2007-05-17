/*
 * DefaultErrorSource.java - Default error source
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2005 Slava Pestov
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
import javax.swing.text.Position;
import javax.swing.SwingUtilities;
import java.util.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;
//}}}

/**
 * @author Slava Pestov
 */
public class DefaultErrorSource extends ErrorSource implements EBComponent
{
	//{{{ DefaultErrorSource constructor
	/**
	 * Creates a new default error source.
	 */
	public DefaultErrorSource(String name)
	{
		errors = new LinkedHashMap();
		this.name = name;
	} //}}}

	//{{{ getName() method
	/**
	 * Returns a string description of this error source.
	 */
	public String getName()
	{
		return name;
	} //}}}

	//{{{ getErrorCount() method
	/**
	 * Returns the number of errors in this source.
	 */
	public int getErrorCount()
	{
		return errorCount;
	} //}}}

	//{{{ getAllErrors() method
	/**
	 * Returns all errors.
	 */
	public ErrorSource.Error[] getAllErrors()
	{
		if(errors.size() == 0)
			return null;

		List errorList = new LinkedList();

		Iterator iter = errors.values().iterator();
		while(iter.hasNext())
			errorList.addAll((List)iter.next());

		return (ErrorSource.Error[])errorList.toArray(
			new ErrorSource.Error[errorList.size()]);
	} //}}}

	//{{{ getFileErrorCount() method
	/**
	 * Returns the number of errors in the specified file.
	 * @param path The full path name
	 */
	public int getFileErrorCount(String path)
	{
		List list = (List)errors.get(path);
		if(list == null)
			return 0;
		else
			return list.size();
	} //}}}

	//{{{ getFileErrors() method
	/**
	 * Returns all errors in the specified file.
	 * @param path The full path name
	 */
	public ErrorSource.Error[] getFileErrors(String path)
	{
		List list = (List)errors.get(path);
		if(list == null || list.size() == 0)
			return null;

		return (ErrorSource.Error[])list.toArray(
			new ErrorSource.Error[list.size()]);
	} //}}}

	//{{{ getLineErrors() method
	/**
	 * Returns all errors in the specified line range.
	 * @param path The file path
	 * @param startLineIndex The line number
	 * @param endLineIndex The line number
	 * @since ErrorList 1.3
	 */
	public ErrorSource.Error[] getLineErrors(String path,
		int startLineIndex, int endLineIndex)
	{
		if(errors.size() == 0)
			return null;

		List list = (List)errors.get(path);
		if(list == null)
			return null;

		List errorList = new LinkedList();

		int startIndex = findError(list,startLineIndex);

		if(startIndex != list.size())
		{
			for(int i = startIndex; i >= 0; i--)
			{
				if(((ErrorSource.Error)list.get(i)).getLineNumber()
					>= startLineIndex)
				{
					startIndex = i;
				}
				else
					break;
			}

			for(int i = startIndex; i < list.size(); i++)
			{
				DefaultError error = (DefaultError)list.get(i);
				if(error.getLineNumber() <= endLineIndex)
				{
					errorList.add(error);
				}
				else
					break;
			}
		}

		if(errorList.size() == 0)
			return null;
		else
		{
			return (ErrorSource.Error[])errorList.toArray(
				new ErrorSource.Error[errorList.size()]);
		}
	} //}}}

	//{{{ clear() method
	/**
	 * Removes all errors from this error source. This method is thread
	 * safe.
	 */
	public synchronized void clear()
	{
		if(errorCount == 0)
			return;

		errors.clear();
		errorCount = 0;
		removeOrAddToBus();

		if(registered)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					ErrorSourceUpdate message = new ErrorSourceUpdate(DefaultErrorSource.this,
						ErrorSourceUpdate.ERRORS_CLEARED,null);
					EditBus.send(message);
				}
			});
		}
	} //}}}

	//{{{ removeFileErrors() method
	/**
	 * Removes all errors in the specified file. This method is thread-safe.
	 * @param path The file path
	 * @since ErrorList 1.3
	 */
	public synchronized void removeFileErrors(String path)
	{
		final List list = (List)errors.remove(path);
		if(list == null)
			return;

		errorCount -= list.size();
		removeOrAddToBus();

		if(registered)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					for(int i = 0; i < list.size(); i++)
					{
						DefaultError error = (DefaultError)list.get(i);
						ErrorSourceUpdate message = new ErrorSourceUpdate(DefaultErrorSource.this,
							ErrorSourceUpdate.ERROR_REMOVED,error);
						EditBus.send(message);
					}
				}
			});
		}
	} //}}}

	//{{{ addError() method
	/**
	 * Adds an error to this error source. This method is thread-safe.
	 * @param error The error
	 */
	public synchronized void addError(final DefaultError error)
	{
		List list = (List)errors.get(error.getFilePath());
		if(list == null)
		{
			list = new ArrayList();
			errors.put(error.getFilePath(),list);
		}

		boolean added = false;

		for(int i = 0; i < list.size(); i++)
		{
			Error _error = (Error)list.get(i);
			if(_error.getLineNumber() > error.getLineNumber())
			{
				list.add(i,error);
				added = true;
				break;
			}
		}

		if(!added)
			list.add(error);
		errorCount++;
		removeOrAddToBus();

		if(registered)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					ErrorSourceUpdate message = new ErrorSourceUpdate(DefaultErrorSource.this,
						ErrorSourceUpdate.ERROR_ADDED,error);
					EditBus.send(message);
				}
			});
		}
	} //}}}

	//{{{ addError() method
	/**
	 * Adds an error to this error source. This method is thread-safe.
	 * @param errorType The error type (ErrorSource.ERROR or
	 * ErrorSource.WARNING)
	 * @param path The path name
	 * @param lineIndex The line number
	 * @param start The start offset
	 * @param end The end offset
	 * @param error The error message
	 */
	public void addError(int type, String path,
		int lineIndex, int start, int end, String error)
	{
		DefaultError newError = new DefaultError(this,type,path,lineIndex,
			start,end,error);

		addError(newError);
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage message)
	{
		if(message instanceof BufferUpdate)
			handleBufferMessage((BufferUpdate)message);
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		return getClass().getName() + "[" + name + "]";
	} //}}}

	//{{{ Protected members
	protected String name;
	protected int errorCount;
	protected Map errors;
	//}}}

	//{{{ Private members
	private boolean addedToBus;

	//{{{ findError() method
	private int findError(List errorList, int line)
	{
		int start = 0;
		int end = errorList.size() - 1;

		for(;;)
		{
			switch(end - start)
			{
			case 0:
				if(((Error)errorList.get(start)).getLineNumber()
					>= line)
					return start;
				else
					return start + 1;
			case 1:
				if(((Error)errorList.get(start)).getLineNumber()
					== line)
				{
					return start;
				}
				else
				{
					start++;
					break;
				}
			default:
				int pivot = (end + start) / 2;
				int value = ((Error)errorList.get(pivot))
					.getLineNumber();
				if(value == line)
					return pivot;
				else if(value < line)
					start = pivot + 1;
				else
					end = pivot - 1;
				break;
			}
		}
	} //}}}

	//{{{ removeOrAddToBus() method
	private void removeOrAddToBus()
	{
		if(addedToBus && errorCount == 0)
		{
			addedToBus = false;
			EditBus.removeFromBus(this);
		}
		else if(!addedToBus && errorCount != 0)
		{
			addedToBus = true;
			EditBus.addToBus(this);
		}
	} //}}}

	//{{{ handleBufferMessage() method
	private synchronized void handleBufferMessage(BufferUpdate message)
	{
		Buffer buffer = message.getBuffer();

		if(message.getWhat() == BufferUpdate.LOADED)
		{
			List list = (List)errors.get(buffer.getSymlinkPath());
			if(list != null)
			{
				for(int i = 0; i < list.size(); i++)
				{
					((DefaultError)list.get(i))
						.openNotify(buffer);
				}
			}
		}
		else if(message.getWhat() == BufferUpdate.CLOSED)
		{
			List list = (List)errors.get(buffer.getSymlinkPath());
			if(list != null)
			{
				for(int i = 0; i < list.size(); i++)
				{
					((DefaultError)list.get(i))
						.closeNotify(buffer);
				}
			}
		}
	} //}}}

	//}}}

	//{{{ DefaultError class
	/**
	 * An error.
	 */
	public static class DefaultError implements ErrorSource.Error
	{
		//{{{ DefaultError constructor
		/**
		 * Creates a new default error.
		 * @param type The error type
		 * @param path The path
		 * @param start The start offset
		 * @param end The end offset
		 * @param error The error message
		 */
		public DefaultError(ErrorSource source, int type, String path,
			int lineIndex, int start, int end, String error)
		{
			this.source = source;

			this.type = type;

			// Create absolute path
			if(MiscUtilities.isURL(path))
				this.path = path;
			else
			{
				this.path = MiscUtilities.constructPath(System
					.getProperty("user.dir"),path);
				this.path = MiscUtilities.resolveSymlinks(
					this.path);
			}

			this.lineIndex = lineIndex;
			this.start = start;
			this.end = end;
			this.error = error;

			// Shortened name used in display
			name = MiscUtilities.getFileName(path);

			// If the buffer is open and loaded, this creates
			// a floating position
			Buffer buffer = jEdit.getBuffer(this.path);
			if(buffer != null && buffer.isLoaded())
				openNotify(buffer);
		} //}}}

		//{{{ getErrorSource() method
		/**
		 * Returns the error source.
		 */
		public ErrorSource getErrorSource()
		{
			return source;
		} //}}}

		//{{{ getErrorType() method
		/**
		 * Returns the error type.
		 */
		public int getErrorType()
		{
			return type;
		} //}}}

		//{{{ getBuffer() method
		/**
		 * Returns the buffer involved, or null if it is not open.
		 */
		public Buffer getBuffer()
		{
			return buffer;
		} //}}}

		//{{{ getFilePath() method
		/**
		 * Returns the file name involved.
		 */
		public String getFilePath()
		{
			return path;
		} //}}}

		//{{{ getFileName() method
		/**
		 * Returns the name portion of the file involved.
		 */
		public String getFileName()
		{
			return name;
		} //}}}

		//{{{ getLineNumber() method
		/**
		 * Returns the line number.
		 */
		public int getLineNumber()
		{
			if(startPos != null)
			{
				return buffer.getLineOfOffset(startPos.getOffset());
			}
			else
				return lineIndex;
		} //}}}

		//{{{ getStartOffset() method
		/**
		 * Returns the start offset.
		 */
		public int getStartOffset()
		{
			if(startPos != null)
			{
				return startPos.getOffset()
					- buffer.getLineStartOffset(
					getLineNumber());
			}
			else
				return start;
		} //}}}

		//{{{ getEndOffset() method
		/**
		 * Returns the end offset.
		 */
		public int getEndOffset()
		{
			if(endPos != null)
			{
				return endPos.getOffset()
					- buffer.getLineStartOffset(
					getLineNumber());
			}
			else
				return end;
		} //}}}

		//{{{ getErrorMessage() method
		/**
		 * Returns the error message.
		 */
		public String getErrorMessage()
		{
			return error;
		} //}}}

		//{{{ addExtraMessage() method
		/**
		 * Adds an additional message to the error. This must be called
		 * before the error is added to the error source, otherwise it
		 * will have no effect.
		 * @param message The message
		 */
		public void addExtraMessage(String message)
		{
			if(extras == null)
				extras = new ArrayList();
			extras.add(message);
		} //}}}

		//{{{ getExtraMessages() method
		/**
		 * Returns the error message.
		 */
		public String[] getExtraMessages()
		{
			if(extras == null)
				return new String[0];

			return (String[])extras.toArray(
				new String[extras.size()]);
		} //}}}

		//{{{ toString() method
		/**
		 * Returns a string representation of this error.
		 */
		public String toString()
		{
			return getFileName() + ":" + (getLineNumber() + 1)
				+ ":" + getErrorMessage();
		} //}}}

		//{{{ Package-private members
		DefaultError next;

		//{{{ openNotify() method
		/*
		 * Notifies the compiler error that a buffer has been opened.
		 * This creates the floating position if necessary.
		 *
		 * I could make every CompilerError listen for buffer open
		 * events, but it's too much effort remembering to unregister
		 * the listeners, etc.
		 */
		void openNotify(Buffer buffer)
		{
			this.buffer = buffer;
			int lineIndex = Math.min(this.lineIndex,
				buffer.getLineCount() - 1);

			start = Math.min(start,buffer.getLineLength(lineIndex));

			int lineStart = buffer.getLineStartOffset(lineIndex);
			startPos = buffer.createPosition(lineStart + start);

			if(end != 0)
			{
				endPos = buffer.createPosition(
					lineStart + end);
			}
			else
				endPos = null;
		} //}}}

		//{{{ closeNotify() method
		/*
		 * Notifies the error that a buffer has been closed.
		 * This clears the floating position if necessary.
		 *
		 * I could make every error listen for buffer closed
		 * events, but it's too much effort remembering to unregister
		 * the listeners, etc.
		 */
		void closeNotify(Buffer buffer)
		{
			this.buffer = null;
			linePos = null;
			startPos = null;
			endPos = null;
		} //}}}

		//}}}

		//{{{ Private members
		private ErrorSource source;

		private int type;

		private String path;
		private String name;
		private Buffer buffer;

		private int lineIndex;
		private int start;
		private int end;

		private Position linePos;
		private Position startPos;
		private Position endPos;

		private String error;
		private List extras;
		//}}}
	} //}}}
}
