/*
 * DefaultErrorSource.java - Default error source
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999-2012 Slava Pestov, Alan Ezust

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

import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;
//}}}

/** A concrete implementation of ErrorSource that is suitable for Plugins to use/extend.
 *
 * @author Slava Pestov
 */
public class DefaultErrorSource extends ErrorSource
{
	//{{{ DefaultErrorSource constructors
	/**
	 * Creates a new default error source.
	 * @param v  which View we want errors to be sent to.
	 *  if null, errors may be sent to all Views.
	*/
	public DefaultErrorSource(String name, View v)
	{
		errors = new LinkedHashMap<String, ErrorListForPath>();
		this.name = name;
		this.view = v;
	}

	/** @deprecated please supply a View using the other ctor. */
	@Deprecated
	public DefaultErrorSource(String name) {
		this(name, null);
	}//}}}

	//{{{ getName() method
	/**
	 * Returns a string description of this error source.
	 */
	public String getName()
	{
		return name;
	} //}}}

	//{{{ getView() method
	/** Returns the View that messages should be displayed in */
	public View getView() {
		return view;

	}//}}}

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
	public synchronized ErrorSource.Error[] getAllErrors()
	{
		if(errors.size() == 0)
			return null;

		List<Error> errorList = new LinkedList<Error>();

		Iterator<ErrorListForPath> iter = errors.values().iterator();
		while(iter.hasNext())
			errorList.addAll(iter.next());

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
		ErrorListForPath list = (ErrorListForPath)errors.get(path);
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
		ErrorListForPath list = errors.get(path);
		if(list == null || list.size() == 0)
			return null;

		return list.toArray(new ErrorSource.Error[list.size()]);
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

		ErrorListForPath list = errors.get(path);
		if(list == null)
			return null;
		Collection<Error> inRange = list.subSetInLineRange(startLineIndex, endLineIndex);
		if(inRange.size() == 0)
			return null;
		else
		{
			return inRange.toArray(new ErrorSource.Error[inRange.size()]);
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
					ErrorSourceUpdate message = new ErrorSourceUpdate(
						DefaultErrorSource.this, ErrorSourceUpdate.ERRORS_CLEARED);
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
		final ErrorListForPath list = errors.remove(path);
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
					for (Error error: list) {
						ErrorSourceUpdate message = new ErrorSourceUpdate(
							DefaultErrorSource.this, ErrorSourceUpdate.ERROR_REMOVED, error);
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
		ErrorListForPath list = errors.get(error.getFilePath());
		if(list == null)
		{
			list = new ErrorListForPath();
			errors.put(error.getFilePath(), list);
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
						ErrorSourceUpdate message = new ErrorSourceUpdate(DefaultErrorSource.this,
							ErrorSourceUpdate.ERROR_ADDED,error);
						EditBus.send(message);
					}
				});
			}
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
		DefaultError newError = new DefaultError(this, type, path, lineIndex, start, end, error);

		addError(newError);
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		return getClass().getName() + "[" + name + "]";
	} //}}}

	//{{{ handleBufferMessage() method
	@EBHandler
	public synchronized void handleBufferMessage(BufferUpdate message)
	{
		Buffer buffer = message.getBuffer();

		if(message.getWhat() == BufferUpdate.LOADED)
		{
			ErrorListForPath list = (ErrorListForPath)errors.get(buffer.getSymlinkPath());
			if(list != null)
			{
				Iterator<Error> i = list.iterator();
				while(i.hasNext())
				{
					((DefaultError)i.next()).openNotify(buffer);
				}
			}
		}
		else if(message.getWhat() == BufferUpdate.CLOSED)
		{
			ErrorListForPath list = (ErrorListForPath)errors.get(buffer.getSymlinkPath());
			if(list != null)
			{
				Iterator<Error> i = list.iterator();
				while(i.hasNext())
				{
					((DefaultError)i.next()).closeNotify(buffer);
				}
			}
		}
	} //}}}

	//{{{ Protected members
	protected String name;
	protected View view;
	protected int errorCount;
	protected Map<String, ErrorListForPath> errors;
	//}}}

	//{{{ Private members
	private boolean addedToBus;

	//{{{ removeOrAddToBus() method
	protected void removeOrAddToBus()
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
		 * @param source The ErrorSource
		 * @param type The error type
		 * @param path The absolute path, or just a filename, in which case
		     ErrorList will try to open the file using a FileOpenerService
		 * @param lineIndex The line Index
		 * @param start The start offset
		 * @param end The end offset
		 * @param error The error message
		 */
		public DefaultError(ErrorSource source, int type, String path,
			int lineIndex, int start, int end, String error)
		{
			this.source = source;
			this.type = type;
			setFilePath(path); // also sets the name

			this.lineIndex = lineIndex;
			this.start = start;
			this.end = end;
			this.error = error;

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

		//{{{ setFilePath() method
		/**
		 * Changes the filePath of this error
		 * @param newPath the new path
		 */
		public void setFilePath(String newPath)
		{
			path = MiscUtilities.resolveSymlinks(newPath);
			name = MiscUtilities.getFileName(path);
		}// }}}

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
				extras = new ArrayList<String>();
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
		private List<String> extras;
		//}}}
	} //}}}

	//{{{ ErrorListForPath class
	/**
	 * A list of errors sorted by line number,
	 * then start and end offsets in the line,
	 * and finally by message.
	 */
	protected static class ErrorListForPath extends TreeSet<Error>
	{
		public ErrorListForPath()
		{
			super(new ErrorComparator());
		}

		public Collection<Error> subSetInLineRange(int start, int end)
		{
			return subSet(new LineKey(start), new LineKey(end + 1));
		}

		//{{{ class ErrorComparator
		/**
		 * Comparator based on line number,
		 * then start and end offsets in the line,
		 * and finally by message.
		 */
		private static class ErrorComparator implements Comparator<Error>
		{
			public int compare(Error e1, Error e2)
			{
				int line1 = e1.getLineNumber();
				int line2 = e2.getLineNumber();
				if (line1 < line2) return -1;
				else if (line1 > line2) return 1;

				// Following comparisons enable multiple
				// errors at same line. Make sure that
				// class LineKey implements the corresponding
				// methods.

				//first one first
				int o1 = e1.getStartOffset();
				int o2 = e2.getStartOffset();
				if (o1 < o2) return -1;
				else if (o1 > o2) return 1;

				//smaller one first
				o1 = e1.getEndOffset();
				o2 = e2.getEndOffset();
				if (o1 < o2) return -1;
				else if (o1 > o2) return 1;

				String message1 = e1.getErrorMessage();
				String message2 = e2.getErrorMessage();
				int message_sign = message1.compareTo(message2);
				if (message_sign != 0) return message_sign;

				return 0;
			}
		} //}}}

		//{{{ class LineKey
		/**
		 * Dummy instance used as a key of set operations.
		 * This class returns a meaningfull value only from methods
		 * which is used by ErrorComparator.
		 */
		private static class LineKey implements ErrorSource.Error
		{
			private int line;

			public LineKey(int line)
			{
				this.line = line;
			}

			public int getLineNumber()
			{
				return line;
			}

			public String getErrorMessage()
			{
				return "";
			}

			public int getErrorType() { return 0; }
			public ErrorSource getErrorSource() { return null; }
			public Buffer getBuffer() { return null; }
			public String getFilePath() { return null; }
			public String getFileName() { return null; }
			public int getStartOffset() { return 0; }
			public int getEndOffset() { return 0; }
			public String[] getExtraMessages() { return null; }
		} ///}}}
	} ///}}}

}
