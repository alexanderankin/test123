/*
 * SideKick.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
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

package sidekick;

//{{{ Imports
import javax.swing.tree.*;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;
import errorlist.*;
//}}}

public class SideKick implements EBComponent
{
	public static final int MAX_ERRORS = 100;

	//{{{ SideKick constructor
	public SideKick(View view)
	{
		this.view = view;

		errorSource = new DefaultErrorSource("SideKick");
		ErrorSource.registerErrorSource(errorSource);

		bufferHandler = new BufferChangeHandler();

		propertiesChanged();

		keystrokeTimer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				parse(false);
			}
		});

		EditBus.addToBus(this);
	} //}}}

	//{{{ parse() method
	public void parse(final boolean showParsingMessage)
	{
		stopThread();

		maxErrors = false;

		buffer = view.getBuffer();
		this.showParsingMessage = showParsingMessage;

		//{{{ Run this when I/O is complete
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				if(thread != null)
					return;

				EditPane editPane = view.getEditPane();
				editPane.putClientProperty(SideKickPlugin.PARSED_DATA_PROPERTY,null);

				//{{{ check for non-XML file
				if(SideKickPlugin.getParserForBuffer(buffer) == null)
				{
					showNotParsedMessage();
					return;
				} //}}}
				//{{{ Show 'parsing in progress' message
				else if(showParsingMessage)
				{
					SideKickParsedData data = new SideKickParsedData(buffer.getName());
					data.root.add(new DefaultMutableTreeNode(
						jEdit.getProperty("sidekick-tree.parsing")));
					editPane.putClientProperty(SideKickPlugin.PARSED_DATA_PROPERTY,data);

					sendUpdate();
				} //}}}

				_parse();
			}
		}); //}}}
	} //}}}

	//{{{ dispose() method
	public void dispose()
	{
		stopThread();

		errorSource.clear();
		ErrorSource.unregisterErrorSource(errorSource);

		EditBus.removeFromBus(this);

		removeBufferChangeListener(buffer);
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		//{{{ BufferUpdate
		if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if(bmsg.getBuffer() != buffer)
				return;

			if(bmsg.getWhat() == BufferUpdate.SAVED)
			{
				if(thread != null)
					return;

				if(buffer.getBooleanProperty(
					"sidekick.buffer-change-parse")
					|| buffer.getBooleanProperty(
					"sidekick.keystroke-parse"))
				{
					parse(true);
				}
				else
					showNotParsedMessage();
			}
			else if(bmsg.getWhat() == BufferUpdate.PROPERTIES_CHANGED
				|| bmsg.getWhat() == BufferUpdate.LOADED)
			{
				if(SideKickPlugin.getParserForBuffer(buffer) == null)
					removeBufferChangeListener(buffer);
				else
					addBufferChangeListener(buffer);

				if(thread != null)
					return;

				if(buffer.getBooleanProperty(
					"sidekick.buffer-change-parse")
					|| buffer.getBooleanProperty(
					"sidekick.keystroke-parse"))
				{
					parse(true);
				}
				else
					showNotParsedMessage();
			}
			else if(bmsg.getWhat() == BufferUpdate.CLOSED)
			{
				errorSource.clear();
			}
		} //}}}
		//{{{ EditPaneUpdate
		else if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)msg;
			EditPane editPane = epu.getEditPane();
			if(editPane.getView() != view)
				return;

			if(epu.getWhat() == EditPaneUpdate.DESTROYED)
			{
				// check if this is the currently focused edit pane
				if(editPane == editPane.getView().getEditPane())
					removeBufferChangeListener(this.buffer);
			}
			else if(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
			{
				// check if this is the currently focused edit pane
				if(editPane == view.getEditPane())
				{
					removeBufferChangeListener(this.buffer);

					Buffer buffer = editPane.getBuffer();
					if(SideKickPlugin.getParserForBuffer(buffer) != null)
						addBufferChangeListener(buffer);
					if(buffer.getBooleanProperty(
						"sidekick.buffer-change-parse")
						|| buffer.getBooleanProperty(
						"sidekick.keystroke-parse"))
					{
						parse(true);
					}
					else
						showNotParsedMessage();
				}
				else
				{
					editPane.putClientProperty(
						SideKickPlugin.PARSED_DATA_PROPERTY,
						null);
				}
			}
		} //}}}
		//{{{ ViewUpdate
		else if(msg instanceof ViewUpdate)
		{
			ViewUpdate vu = (ViewUpdate)msg;
			if(vu.getView() == view && vu.getWhat() == ViewUpdate.EDIT_PANE_CHANGED)
			{
				removeBufferChangeListener(SideKick.this.buffer);

				Buffer buffer = view.getBuffer();

				if(SideKickPlugin.getParserForBuffer(buffer) != null)
					addBufferChangeListener(buffer);

				if(buffer.getBooleanProperty(
					"sidekick.buffer-change-parse")
					|| buffer.getBooleanProperty(
					"sidekick.keystroke-parse"))
				{
					//if(buffer != SideKick.this.buffer)
						parse(true);
					//else
					//	sendUpdate();
				}
				else
					showNotParsedMessage();
			}
		}
	} //}}}

	//{{{ getBuffer() method
	public Buffer getBuffer()
	{
		return buffer;
	} //}}}

	//{{{ addError() method
	public boolean addError(int type, String path, int line, String message)
	{
		if(errorSource.getErrorCount() >= MAX_ERRORS)
		{
			maxErrors = true;
			return false;
		}
		else
		{
			errorSource.addError(type,path,line,0,0,message);
			return true;
		}
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private Buffer buffer;

	private ParseThread thread;

	private String text;

	private SideKickParser parserImpl;

	private DefaultErrorSource errorSource;

	private boolean showParsingMessage;

	private int delay;
	private Timer keystrokeTimer;

	private boolean maxErrors;

	private BufferChangeHandler bufferHandler;
	private boolean addedBufferChangeHandler;
	//}}}

	//{{{ addBufferChangeListener() method
	private void addBufferChangeListener(Buffer buffer)
	{
		if(!addedBufferChangeHandler)
		{
			buffer.addBufferChangeListener(bufferHandler);
			addedBufferChangeHandler = true;
		}
	} //}}}

	//{{{ removeBufferChangeListener() method
	private void removeBufferChangeListener(Buffer buffer)
	{
		if(addedBufferChangeHandler)
		{
			buffer.removeBufferChangeListener(bufferHandler);
			addedBufferChangeHandler = false;
		}
	} //}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		try
		{
			delay = Integer.parseInt(jEdit.getProperty("sidekick.auto-parse-delay"));
		}
		catch(NumberFormatException nf)
		{
			delay = 1500;
		}
	} //}}}

	//{{{ showNotParsedMessage() method
	private void showNotParsedMessage()
	{
		errorSource.clear();

		stopThread();

		buffer = view.getBuffer();

		SideKickParsedData data = new SideKickParsedData(buffer.getName());
		data.root.add(new DefaultMutableTreeNode(
			jEdit.getProperty("sidekick-tree.not-parsed")));

		view.getEditPane().putClientProperty(SideKickPlugin.PARSED_DATA_PROPERTY,data);

		sendUpdate();
		return;
	} //}}}

	//{{{ parseWithDelay() method
	private void parseWithDelay()
	{
		if(keystrokeTimer.isRunning())
			keystrokeTimer.stop();

		keystrokeTimer.setInitialDelay(delay);
		keystrokeTimer.setRepeats(false);
		keystrokeTimer.start();
	} //}}}

	//{{{ _parse() method
	private void _parse()
	{
		errorSource.clear();

		// get buffer text
		text = buffer.getText(0,buffer.getLength());

		// start parser thread
		stopThread();

		parserImpl = SideKickPlugin.getParserForBuffer(buffer);

		thread = new ParseThread();
		thread.start();
	} //}}}

	//{{{ stopThread() method
	private void stopThread()
	{
		if(thread != null)
		{
			thread.stop();
			thread = null;
		}
	} //}}}

	//{{{ sendUpdate() method
	private void sendUpdate()
	{
		if(view.isClosed())
			return;

		EditBus.send(new SideKickUpdate(view));
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ ParseThread class
	class ParseThread extends Thread
	{
		ParseThread()
		{
			super("SideKick parser thread");
			setPriority(Thread.MIN_PRIORITY);
		}

		public void run()
		{
			final SideKickParsedData data;

			synchronized(SideKick.this)
			{
				if(parserImpl == null)
					return;

				data = parserImpl.parse(SideKick.this,text);
			}

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					synchronized(SideKick.this)
					{
						if(thread == null)
							return;

						int errorCount = errorSource.getErrorCount();

						if(showParsingMessage || errorCount != 0)
						{
							String label = jEdit.getProperty("sidekick.parser."
								+ parserImpl.getName() + ".label");
							if(maxErrors)
							{
								Object[] pp = { label, new Integer(errorCount) };
								view.getStatus().setMessageAndClear(jEdit.getProperty(
									"sidekick.parsing-complete-errors",pp));
							}
							else
							{
								Object[] pp = { label, new Integer(errorCount) };
								view.getStatus().setMessageAndClear(jEdit.getProperty(
									"sidekick.parsing-complete",pp));
							}
						}

						view.getEditPane().putClientProperty(
							SideKickPlugin.PARSED_DATA_PROPERTY,
							data);

						thread = null;

						// to avoid keeping pointers to stale objects.
						// we could reuse a single parser instance to preserve
						// performance, but it eats too much memory, especially
						// if the file being parsed has an associated DTD.
						text = null;

						parserImpl = null;

						sendUpdate();
					}
				}
			});
		}
	} //}}}

	//{{{ BufferChangeHandler class
	class BufferChangeHandler extends BufferChangeAdapter
	{
		//{{{ contentInserted() method
		public void contentInserted(Buffer buffer, int startLine, int offset,
			int numLines, int length)
		{
			if(buffer == SideKick.this.buffer
				&& buffer.isLoaded()
				&& SideKickPlugin.getParserForBuffer(buffer) != null
				&& buffer.getBooleanProperty("sidekick.keystroke-parse"))
				parseWithDelay();
		} //}}}

		//{{{ contentRemoved() method
		public void contentRemoved(Buffer buffer, int startLine, int offset,
			int numLines, int length)
		{
			if(buffer == SideKick.this.buffer
				&& buffer.isLoaded()
				&& SideKickPlugin.getParserForBuffer(buffer) != null
				&& buffer.getBooleanProperty("sidekick.keystroke-parse"))
				parseWithDelay();
		} //}}}
	} //}}}

	//}}}
}
