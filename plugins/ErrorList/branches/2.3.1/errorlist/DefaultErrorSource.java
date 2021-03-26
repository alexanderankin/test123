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
 * TODO: add an 'equals' method here and in DefaultError
 * @author Slava Pestov
 */
public class DefaultErrorSource extends ErrorSource
{
	public static final String[] EMPTY_STRING = new String[0];
	public static final Error[] EMPTY_ERROR_ARRAY = new Error[0];

	//{{{ DefaultErrorSource constructors
	/**
	 * Creates a new default error source.
	 * @param v  which View we want errors to be sent to.
	 *  if null, errors may be sent to all Views.
	*/
	public DefaultErrorSource(String name, View v)
	{
		errors = new LinkedHashMap<>();
		this.name = name;
		this.view = v;
	}

	/** @deprecated please supply a View using the other ctor. */
	@Deprecated
	public DefaultErrorSource(String name)
	{
		this(name, null);
	}//}}}

	//{{{ getName() method
	/**
	 * Returns a string description of this error source.
	 */
	@Override
	public String getName()
	{
		return name;
	} //}}}

	//{{{ getView() method
	/** Returns the View that messages should be displayed in */
	@Override
	public View getView() {
		return view;

	}//}}}

	//{{{ getErrorCount() method
	/**
	 * Returns the number of errors in this source.
	 */
	@Override
	public int getErrorCount()
	{
		return errorCount;
	} //}}}

	//{{{ getAllErrors() method
	/**
	 * Returns all errors.
	 */
	@Override
	public synchronized ErrorSource.Error[] getAllErrors()
	{
		if(errors.isEmpty())
			return null;

		List<Error> errorList = new LinkedList<>();

        errors.values().forEach(errorList::addAll);

		return errorList.toArray(EMPTY_ERROR_ARRAY);
	} //}}}

	//{{{ getFileErrorCount() method
	/**
	 * Returns the number of errors in the specified file.
	 * @param path The full path name
	 */
	@Override
	public int getFileErrorCount(String path)
	{
		ErrorListForPath list = errors.get(path);
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
	@Override
	public ErrorSource.Error[] getFileErrors(String path)
	{
		ErrorListForPath list = errors.get(path);
		if(list == null || list.isEmpty())
			return null;

		return list.toArray(EMPTY_ERROR_ARRAY);
	} //}}}

	//{{{ getLineErrors() method
	/**
	 * Returns all errors in the specified line range.
	 * @param path The file path
	 * @param startLineIndex The line number
	 * @param endLineIndex The line number
	 * @since ErrorList 1.3
	 */
	@Override
	public ErrorSource.Error[] getLineErrors(String path, int startLineIndex, int endLineIndex)
	{
		if(errors.isEmpty())
			return null;

		ErrorListForPath list = errors.get(path);
		if(list == null)
			return null;
		Collection<Error> inRange = list.subSetInLineRange(startLineIndex, endLineIndex);
		if(inRange.isEmpty())
			return null;
		else
		{
			return inRange.toArray(EMPTY_ERROR_ARRAY);
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
			SwingUtilities.invokeLater(() -> sendErrorSourceUpdate(ErrorSourceUpdate.ERRORS_CLEARED));
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
			SwingUtilities.invokeLater(() -> list.forEach(error -> sendErrorSourceUpdate(ErrorSourceUpdate.ERROR_REMOVED, error)));
		}
	} //}}}

	//{{{ addError() method
	/**
	 * Adds an error to this error source. This method is thread-safe.
	 * @param error The error
	 */
	@Override
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
				SwingUtilities.invokeLater(() -> sendErrorSourceUpdate(ErrorSourceUpdate.ERROR_ADDED, error));
			}
		}
	} //}}}

	//{{{ addError() method
	/**
	 * Adds an error to this error source. This method is thread-safe.
	 * @param type The error type (ErrorSource.ERROR or
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
	
	public boolean equals(Object o)
	{
		if (!(o instanceof DefaultErrorSource))
			return false;
		DefaultErrorSource other = (DefaultErrorSource)o;
		if (getView() != null && !getView().equals(other.getView()))
			return false;
		if (getName() != null && !getName().equals(other.getName()))
			return false;
		if (getErrorCount() != other.getErrorCount())
			return false;
		return true;
	}
	
	public int hashCode()
	{
		int hc = 0;
		if (getView() != null)
			hc += getView().hashCode();
		if (getName() != null)
			hc += getName().hashCode();
		hc += getErrorCount() * 13;
		return hc;
	}

	//{{{ handleBufferMessage() method
	@EBHandler
	public synchronized void handleBufferMessage(BufferUpdate message)
	{
		Buffer buffer = message.getBuffer();

		if(message.getWhat() == BufferUpdate.LOADED)
		{
			ErrorListForPath list = errors.get(buffer.getSymlinkPath());
			if(list != null)
			{
				list.forEach(error -> ((DefaultError) error).openNotify(buffer));
			}
		}
		else if(message.getWhat() == BufferUpdate.CLOSED)
		{
			ErrorListForPath list = errors.get(buffer.getSymlinkPath());
			if(list != null)
			{
				list.forEach(error -> ((DefaultError) error).closeNotify(buffer));
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


	//{{{ sendErrorSourceUpdate() method
	private void sendErrorSourceUpdate(Object what)
	{
		EditBus.send(new ErrorSourceUpdate(this, what));
	} //}}}

	//{{{ sendErrorSourceUpdate() method
	private void sendErrorSourceUpdate(Object what, ErrorSource.Error error)
	{
		EditBus.send(new ErrorSourceUpdate(this, what, error));
	} //}}}

	//}}}

	//{{{ DefaultError class
	/**
	 * An error.
	 * TODO: add an 'equals' method
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
		@Override
		public ErrorSource getErrorSource()
		{
			return source;
		} //}}}

		//{{{ getErrorType() method
		/**
		 * Returns the error type.
		 */
		@Override
		public int getErrorType()
		{
			return type;
		} //}}}

		//{{{ getBuffer() method
		/**
		 * Returns the buffer involved, or null if it is not open.
		 */
		@Override
		public Buffer getBuffer()
		{
			return buffer;
		} //}}}

		//{{{ getFilePath() method
		/**
		 * Returns the file name involved.
		 */
		@Override
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
		@Override
		public String getFileName()
		{
			return name;
		} //}}}

		//{{{ getLineNumber() method
		/**
		 * Returns the line number.
		 */
		@Override
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
		@Override
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
		@Override
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
		@Override
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
		@Override
		public String[] getExtraMessages()
		{
			if(extras == null)
				return EMPTY_STRING;

			return extras.toArray(EMPTY_STRING);
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
		
		public boolean equals(Object o)
		{
			if (!(o instanceof DefaultError))
			{
				return false;	
			}
			DefaultError other = (DefaultError)o;
			if (getBuffer() != null && !getBuffer().equals(other.getBuffer()))
				return false;
			if (getEndOffset() != other.getEndOffset())
				return false;
			if (getStartOffset() != other.getStartOffset())
				return false;
			if (getLineNumber() != other.getLineNumber())
				return false;
			if (getFileName() != null && !getFileName().equals(other.getFileName()))
				return false;
			if (getFilePath() != null && !getFilePath().equals(other.getFilePath()))
				return false;
			return true;
		}
		
		public int hashCode()
		{
			int hc = 0;
			if (getBuffer() != null)
				hc += getBuffer().hashCode();
			hc += getEndOffset() * 19;
			hc += getStartOffset() * 23;
			hc += getLineNumber() * 29;
			if (getFileName() != null)
				hc += getFileName().hashCode();
			if (getFilePath() != null)
				hc += getFilePath().hashCode();
			return hc;
		}

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
		private final ErrorSource source;

		private final int type;

		private String path;
		private String name;
		private Buffer buffer;

		private final int lineIndex;
		private int start;
		private final int end;

		private Position linePos;
		private Position startPos;
		private Position endPos;

		private final String error;
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
			@Override
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
				int message_sign;
				if(message1 == null)
				{
					message_sign = message2 == null ? 0 : -1;
				}
				else if(message2 == null)
				{
					message_sign = 1;
				}
				else
				{
					message_sign = message1.compareTo(message2);
				}
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
			private final int line;

			LineKey(int line)
			{
				this.line = line;
			}

			@Override
			public int getLineNumber()
			{
				return line;
			}

			@Override
			public String getErrorMessage()
			{
				return "";
			}

			@Override
			public int getErrorType() { return 0; }
			@Override
			public ErrorSource getErrorSource() { return null; }
			@Override
			public Buffer getBuffer() { return null; }
			@Override
			public String getFilePath() { return null; }
			@Override
			public String getFileName() { return null; }
			@Override
			public int getStartOffset() { return 0; }
			@Override
			public int getEndOffset() { return 0; }
			@Override
			public String[] getExtraMessages() { return null; }
		} ///}}}
	} ///}}}

}
