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
import org.gjt.sp.util.*;
import errorlist.*;
//}}}

class SideKick implements EBComponent
{
	public static final int MAX_ERRORS = 100;

	//{{{ SideKick constructor
	SideKick(View view)
	{
		this.view = view;

		errorSource = new DefaultErrorSource("SideKick")
		{
			public void addError(int type, String path,
				int lineIndex, int start, int end, String error)
			{
				if(errorSource.getErrorCount() >= MAX_ERRORS)
					maxErrors = true;
				else
				{
					super.addError(type,path,lineIndex,
						start,end,error);
				}
			}
		};

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

		Buffer buffer = view.getBuffer();

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
		{
			showNotParsedMessage();
		}

		EditBus.addToBus(this);
	} //}}}

	//{{{ parse() method
	/**
	 * Immediately begins parsing the current buffer in a background thread.
	 * @param showParsingMessage Clear the tree and show a status message
	 * there?
	 */
	void parse(final boolean showParsingMessage)
	{
		buffer = view.getBuffer();

		if(SideKickPlugin.isParsingBuffer(buffer))
			return;

		SideKickPlugin.startParsingBuffer(buffer);

		maxErrors = false;

		this.showParsingMessage = showParsingMessage;

		//{{{ Run this when I/O is complete
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				SideKickParsedData.setParsedData(view,null);

				//{{{ check for non-XML file
				if(SideKickPlugin.getParserForBuffer(buffer) == null)
				{
					showNotParsedMessage();
					SideKickPlugin.finishParsingBuffer(buffer);
					return;
				} //}}}
				//{{{ Show 'parsing in progress' message
				else if(showParsingMessage)
				{
					SideKickParsedData data = new SideKickParsedData(buffer.getName());
					data.root.add(new DefaultMutableTreeNode(
						jEdit.getProperty("sidekick-tree.parsing")));
					SideKickParsedData.setParsedData(view,data);

					sendUpdate();
				} //}}}

				errorSource.clear();

				SideKickParser parserImpl = SideKickPlugin.getParserForBuffer(buffer);
				SideKickParsedData[] data = new SideKickParsedData[1];

				SideKickPlugin.addWorkRequest(new ParseRequest(
					parserImpl,buffer,errorSource,data),false);
				SideKickPlugin.addWorkRequest(new ParseAWTRequest(
					parserImpl,buffer,data),false);
			}
		}); //}}}
	} //}}}

	//{{{ dispose() method
	void dispose()
	{
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
			else if(bmsg.getWhat() == BufferUpdate.PROPERTIES_CHANGED)
			{
				if(SideKickPlugin.getParserForBuffer(buffer) == null)
					removeBufferChangeListener(buffer);
				else
					addBufferChangeListener(buffer);

				if(buffer.getBooleanProperty(
					"sidekick.buffer-change-parse")
					|| buffer.getBooleanProperty(
					"sidekick.keystroke-parse"))
				{
					parse(true);
				}
				else
				{
					showNotParsedMessage();
				}
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
		} //}}}
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private Buffer buffer;

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

		buffer = view.getBuffer();

		SideKickParsedData data = new SideKickParsedData(buffer.getName());
		data.root.add(new DefaultMutableTreeNode(
			jEdit.getProperty("sidekick-tree.not-parsed")));

		SideKickParsedData.setParsedData(view,data);

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

	//{{{ sendUpdate() method
	private void sendUpdate()
	{
		if(view.isClosed())
			return;

		EditBus.send(new SideKickUpdate(view));
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ ParseRequest class
	static class ParseRequest implements Runnable
	{
		SideKickParser parserImpl;
		Buffer buffer;
		DefaultErrorSource errorSource;
		SideKickParsedData[] data;

		ParseRequest(SideKickParser parserImpl, Buffer buffer,
			DefaultErrorSource errorSource, SideKickParsedData[] data)
		{
			this.parserImpl = parserImpl;
			this.buffer = buffer;
			this.errorSource = errorSource;
			this.data = data;
		}

		public void run()
		{
			data[0] = parserImpl.parse(buffer,errorSource);
		}
	} //}}}

	//{{{ ParseAWTRequest class
	class ParseAWTRequest implements Runnable
	{
		SideKickParser parserImpl;
		Buffer buffer;
		SideKickParsedData[] data;

		ParseAWTRequest(SideKickParser parserImpl, Buffer buffer,
			SideKickParsedData[] data)
		{
			this.parserImpl = parserImpl;
			this.buffer = buffer;
			this.data = data;
		}

		public void run()
		{
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

			View[] views = jEdit.getViews();
			for(int i = 0; i < views.length; i++)
			{
				if(views[i].getBuffer() == buffer)
				{
					SideKickParsedData.setParsedData(view,data[0]);
				}
			}

			sendUpdate();
			SideKickPlugin.finishParsingBuffer(buffer);
		}
	} //}}}

	//{{{ BufferChangeHandler class
	class BufferChangeHandler extends BufferChangeAdapter
	{
		//{{{ contentInserted() method
		public void contentInserted(Buffer buffer, int startLine, int offset,
			int numLines, int length)
		{
			if(buffer != SideKick.this.buffer)
			{
				Log.log(Log.ERROR,this,"We have " + SideKick.this.buffer
					+ " but got event for " + buffer);
				return;
			}

			if(buffer.isLoaded() && buffer.getBooleanProperty("sidekick.keystroke-parse"))
				parseWithDelay();
		} //}}}

		//{{{ contentRemoved() method
		public void contentRemoved(Buffer buffer, int startLine, int offset,
			int numLines, int length)
		{
			if(buffer != SideKick.this.buffer)
			{
				Log.log(Log.ERROR,this,"We have " + SideKick.this.buffer
					+ " but got event for " + buffer);
				return;
			}

			if(buffer.isLoaded() && buffer.getBooleanProperty("sidekick.keystroke-parse"))
				parseWithDelay();
		} //}}}
	} //}}}

	//}}}
}
