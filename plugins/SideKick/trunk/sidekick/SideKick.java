/*
 * SideKick.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2005 Slava Pestov
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
	//{{{ SideKick constructor
	SideKick(View view)
	{
		this.view = view;

		bufferHandler = new BufferChangeHandler();

		propertiesChanged();

		keystrokeTimer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				parse(false);
			}
		});

		buffer = view.getBuffer();
		autoParse();
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
		if(keystrokeTimer.isRunning())
			keystrokeTimer.stop();

		if(!buffer.isLoaded())
			return;

		if(SideKickPlugin.isParsingBuffer(buffer))
			return;
		else
			SideKickPlugin.startParsingBuffer(buffer);

		this.showParsingMessage = showParsingMessage;

		//{{{ check for unknown file
		if(parser == null)
		{
			Log.log(Log.DEBUG,this,"No parser");
			setErrorSource(null);
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

		DefaultErrorSource errorSource = new DefaultErrorSource("SideKick");
		SideKickParsedData[] data = new SideKickParsedData[1];

		SideKickPlugin.addWorkRequest(new ParseRequest(
			parser,buffer,errorSource,data),false);
		SideKickPlugin.addWorkRequest(new ParseAWTRequest(
			parser,buffer,errorSource,data),true);
	} //}}}

	//{{{ dispose() method
	void dispose()
	{
		setErrorSource(null);
		EditBus.removeFromBus(this);
		removeBufferChangeListener(buffer);
	} //}}}

	//{{{ getParser() method
	SideKickParser getParser()
	{
		return parser;
	} //}}}

	//{{{ setParser() method
	void setParser()
	{
		deactivateParser();
		parser = SideKickPlugin.getParserForBuffer(buffer);
		activateParser();
		autoParse();
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof PropertiesChanged)
			propertiesChanged();
		else if(msg instanceof BufferUpdate)
			handleBufferUpdate((BufferUpdate)msg);
		else if(msg instanceof EditPaneUpdate)
			handleEditPaneUpdate((EditPaneUpdate)msg);
		else if(msg instanceof ViewUpdate)
			handleViewUpdate((ViewUpdate)msg);
		else if(msg instanceof PluginUpdate)
		{
			PluginUpdate pmsg = (PluginUpdate)msg;
			if(pmsg.getWhat() == PluginUpdate.UNLOADED
				|| pmsg.getWhat() == PluginUpdate.LOADED)
			{
				/* Pick a parser again in case our parser
				plugin was loaded or unloaded. */
				setParser();
			}
		}
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private EditPane editPane;
	private Buffer buffer;

	private SideKickParser parser;

	private DefaultErrorSource errorSource;

	private boolean showParsingMessage;

	private int delay;
	private Timer keystrokeTimer;

	private BufferChangeHandler bufferHandler;
	private boolean addedBufferChangeHandler;
	//}}}
	
	//{{{ autoParse() method
	private void autoParse()
	{
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
	} //}}}

	//{{{ setErrorSource() method
	private void setErrorSource(DefaultErrorSource errorSource)
	{
		if(this.errorSource != null)
		{
			ErrorSource.unregisterErrorSource(this.errorSource);
			this.errorSource.clear();
		}
		
		this.errorSource = errorSource;

		if(errorSource != null)
		{
			int errorCount = errorSource.getErrorCount();
			if(errorCount != 0)
				ErrorSource.registerErrorSource(errorSource);
		}
	} //}}}

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
			delay = Integer.parseInt(jEdit.getProperty(
				"sidekick.auto-parse-delay"));
		}
		catch(NumberFormatException nf)
		{
			delay = 1500;
		}
	} //}}}

	//{{{ showNotParsedMessage() method
	private void showNotParsedMessage()
	{
		setErrorSource(null);

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

	//{{{ handleBufferUpdate() method
	private void handleBufferUpdate(BufferUpdate bmsg)
	{
		if(bmsg.getBuffer() != buffer)
			/* do nothing */;
		else if(bmsg.getWhat() == BufferUpdate.SAVED
			|| bmsg.getWhat() == BufferUpdate.LOADED)
			autoParse();
		else if(bmsg.getWhat() == BufferUpdate.PROPERTIES_CHANGED)
			setParser();
		else if(bmsg.getWhat() == BufferUpdate.CLOSED)
			setErrorSource(null);
	} //}}}
	
	//{{{ handleEditPaneUpdate() method
	private void handleEditPaneUpdate(EditPaneUpdate epu)
	{
		EditPane editPane = epu.getEditPane();
		if(editPane.getView() != view)
			return;

		if(epu.getWhat() == EditPaneUpdate.DESTROYED)
		{
			// check if this is the currently focused edit pane
			if(editPane == editPane.getView().getEditPane())
			{
				removeBufferChangeListener(this.buffer);
				deactivateParser();
			}
		}
		else if(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
		{
			// check if this is the currently focused edit pane
			if(editPane == view.getEditPane())
			{
				removeBufferChangeListener(this.buffer);
				deactivateParser();

				buffer = editPane.getBuffer();
				parser = SideKickPlugin.getParserForBuffer(buffer);
				activateParser();

				autoParse();
			}
		}
	} //}}}

	//{{{ handleViewUpdate() method
	private void handleViewUpdate(ViewUpdate vu)
	{
		if(vu.getView() == view
			&& vu.getWhat() == ViewUpdate.EDIT_PANE_CHANGED)
		{
			removeBufferChangeListener(this.buffer);
			deactivateParser();

			buffer = view.getBuffer();
			this.editPane = view.getEditPane();

			parser = SideKickPlugin.getParserForBuffer(buffer);
			activateParser();

			autoParse();
		}
	} //}}}

	//{{{ deactivateParser() method
	private void deactivateParser()
	{
		if(parser != null)
		{
			if(this.editPane == null)
				Log.log(Log.ERROR,this,"Null editPane!");
			else
				parser.deactivate(this.editPane);
			this.editPane = null;
		}
	} //}}}

	//{{{ activateParser() method
	private void activateParser()
	{
		EditPane editPane = view.getEditPane();

		if(parser != null)
		{
			addBufferChangeListener(buffer);
			this.editPane = editPane;
			parser.activate(editPane);
		}
		else
			removeBufferChangeListener(buffer);
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ ParseRequest class
	static class ParseRequest implements Runnable
	{
		SideKickParser parser;
		Buffer buffer;
		DefaultErrorSource errorSource;
		SideKickParsedData[] data;

		ParseRequest(SideKickParser parser, Buffer buffer,
			DefaultErrorSource errorSource, SideKickParsedData[] data)
		{
			this.parser = parser;
			this.buffer = buffer;
			this.errorSource = errorSource;
			this.data = data;
		}

		public void run()
		{
			System.err.println(errorSource);
			data[0] = parser.parse(buffer,errorSource);
		}
	} //}}}

	//{{{ ParseAWTRequest class
	class ParseAWTRequest implements Runnable
	{
		SideKickParser parser;
		Buffer buffer;
		SideKickParsedData[] data;
		DefaultErrorSource errorSource;

		ParseAWTRequest(SideKickParser parser, Buffer buffer,
			DefaultErrorSource errorSource, SideKickParsedData[] data)
		{
			this.parser = parser;
			this.buffer = buffer;
			this.data = data;
			this.errorSource = errorSource;
		}

		public void run()
		{
			try
			{
				Log.log(Log.DEBUG,this,"ParseAWTRequest");

				setErrorSource(errorSource);
	
				int errorCount = errorSource.getErrorCount();
				
				if(showParsingMessage || errorCount != 0)
				{
					String label = jEdit.getProperty("sidekick.parser."
						+ parser.getName() + ".label");
					Object[] pp = { label, new Integer(errorCount) };
					view.getStatus().setMessageAndClear(jEdit.getProperty(
						"sidekick.parsing-complete",pp));
				}
	
				buffer.setProperty(SideKickPlugin.PARSED_DATA_PROPERTY,data[0]);
				if(buffer.getProperty("folding").equals("sidekick"))
					buffer.invalidateCachedFoldLevels();
	
				View _view = jEdit.getFirstView();
				while(_view != null)
				{
					System.err.println(_view);
					if(_view.getBuffer() == buffer)
						SideKickParsedData.setParsedData(_view,data[0]);
					_view = _view.getNext();
				}
	
				sendUpdate();
			}
			finally
			{
				SideKickPlugin.finishParsingBuffer(buffer);
			}
		}
	} //}}}

	//{{{ BufferChangeHandler class
	class BufferChangeHandler extends BufferChangeAdapter
	{
		//{{{ parseOnKeyStroke() method
		private void parseOnKeyStroke(Buffer buffer)
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

		//{{{ contentInserted() method
		public void contentInserted(Buffer buffer, int startLine, int offset,
			int numLines, int length)
		{
			parseOnKeyStroke(buffer);
		} //}}}

		//{{{ contentRemoved() method
		public void contentRemoved(Buffer buffer, int startLine, int offset,
			int numLines, int length)
		{
			parseOnKeyStroke(buffer);
		} //}}}
	} //}}}

	//}}}
}
