/*
 * SideKick.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2005 Slava Pestov 
 * Copyright (c) 2006, 2009 by the jEdit developer team
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.BufferChanging;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.EditorExiting;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
//}}}
/** This is an EBComponent that manages a SideKick parser.
 * Each jEdit View needs its own parser.  
 * 
 * SideKick is not a visible component itself, but rather, serves as the underlying 
 * data model for a tree in a dockable Component, as determined by dockables.xml.
 * It happens to be  sidekick.enhanced.SourceTree. 
 *  
 */
public class SideKick
{
	//{{{ static members
	public static final String BUFFER_CHANGE = "sidekick.buffer-change-parse";
	public static final String BUFFER_SAVE = "sidekick.buffer-save-parse";
	public static final String FOLLOW_CARET = "sidekick-tree.follows-caret";
	public static final String AUTO_EXPAND_DEPTH = "sidekick-tree.auto-expand-tree-depth";
	public static final String SHOW_STATUS= "sidekick.showStatusWindow.label";
	public static final String FILTER_VISIBLE = "sidekick.filter-visible-assets";
	public static final String SHOW_FILTER = "sidekick.showFilter";
	static private boolean exiting = false;
	// }}}

	//{{{ Instance variables
	private View view;
	private EditPane editPane;
	private Buffer buffer;
	private SideKickParser parser;
	private DefaultErrorSource errorSource;
	private boolean showParsingMessage;
	private int delay;
	private Timer keystrokeTimer;
	private BufferChangeListener bufferListener;
	private boolean addedBufferListener;
	//}}}


	//{{{ SideKick constructor
	SideKick(View view)
	{
		this.view = view;
		editPane = view.getEditPane();
		bufferListener = new BufferChangeListener();

		propertiesChanged();

		keystrokeTimer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				parse(false);
			}
		});

		buffer = view.getBuffer();
		EditBus.addToBus(this);
	} //}}}

	// {{{ property getters/setters
	public static boolean isFollowCaret() {
		try {
			String currentMode = jEdit.getActiveView().getBuffer().getMode().getName();
			return AbstractModeOptionPane.getBooleanProperty(currentMode, SideKick.FOLLOW_CARET);
		}
		catch (NullPointerException npe) {
			return jEdit.getBooleanProperty(SideKick.FOLLOW_CARET) ;
		}
	}
	public static void setFollowCaret(boolean fc) {
		jEdit.setBooleanProperty( SideKick.FOLLOW_CARET, fc);
	}

	public static void setParseOnSave(boolean val) {
		jEdit.setBooleanProperty(BUFFER_SAVE, val);
	}

	public static void setParseOnChange(boolean val) {
		jEdit.setBooleanProperty(BUFFER_CHANGE, val);
	}

	public static boolean isParseOnSave() {
		return jEdit.getBooleanProperty(BUFFER_SAVE);
	}

	public static boolean isParseOnChange() {
		return jEdit.getBooleanProperty(BUFFER_CHANGE);
	} // }}}

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


		// unconditionally get the right parser
		parser = SideKickPlugin.getParserForBuffer(buffer);
		//{{{ check for unknown file
		if (parser == null) {
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

		///DefaultErrorSource errorSource = new DefaultErrorSource("SideKick");	/// why is this here? Why not have ParseRequest create it?
		///SideKickParsedData[] data = new SideKickParsedData[1];		/// why is this an array? Why is this even here?

		///ParseRequest parseRequest = new ParseRequest(
		///	parser, buffer, errorSource, data);
		ParseRequestWorker parseRequest = new ParseRequestWorker(parser, buffer);
		SideKickPlugin.execute(view, parseRequest);
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
	void setParser(Buffer newBuffer)
	{
		deactivateParser();
		if (newBuffer != null) buffer = newBuffer;
		parser = SideKickPlugin.getParserForBuffer(buffer);
		activateParser();
//		autoParse();
	} //}}}

	//{{{ handlePropertiesChanged() method
	@EBHandler
	public void handlePropertiesChanged(PropertiesChanged msg)
	{
		propertiesChanged();
	} //}}}

	//{{{ handleEditorExiting() method
	@EBHandler
	public void handleEditorExiting(EditorExiting msg)
	{
		exiting = true;
	} //}}}

	//{{{ handleBufferUpdate() method
	@EBHandler
	public void handleBufferUpdate(BufferUpdate bmsg)
	{
		if (bmsg.getWhat() == BufferUpdate.PROPERTIES_CHANGED &&
			view.getBuffer() == bmsg.getBuffer()) {
			String prevMode =
				buffer.getStringProperty(SideKickPlugin.PARSER_MODE_PROPERTY);
			String currMode = (buffer.getMode() != null) ? buffer.getMode().getName() : "";
			if (! currMode.equals(prevMode)) {
				buffer.unsetProperty(SideKickPlugin.PARSER_PROPERTY);
				setParser(view.getBuffer());
			}
		}

		if (bmsg.getView() != view &&
			(bmsg.getView() != null || bmsg.getBuffer() != view.getBuffer()))
			return;

		if (bmsg.getWhat() == BufferUpdate.SAVED && isParseOnSave()) 
			setParser(buffer);
		else if (bmsg.getWhat() == BufferUpdate.LOADED && isParseOnChange())
			parse(true);
		else if(bmsg.getWhat() == BufferUpdate.CLOSED)
			setErrorSource(null);

	} //}}}
	
	//{{{ handleBufferChange() method
	@EBHandler
	public void handleBufferChange(BufferChanging bmsg) {
		buffer = bmsg.getBuffer();
		buffer.setProperty(SideKickPlugin.PARSER_PROPERTY, null);
	} //}}}
	

	//{{{ handleEditPaneUpdate() method
	@EBHandler
	public void handleEditPaneUpdate(EditPaneUpdate epu)
	{
		editPane = epu.getEditPane();
		View v = editPane.getView();
		if (v == null) v=jEdit.getActiveView();
		if (v == null || v != view )
			return;

		if(epu.getWhat() == EditPaneUpdate.DESTROYED)
		{
			// check if this is the currently focused edit pane
			if(editPane == v.getEditPane())
			{
				removeBufferChangeListener(this.buffer);
				deactivateParser();
			}
		}
		else if(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
		{
			if (!isParseOnChange()) {
				SideKickTree tree = (SideKickTree) view.getDockableWindowManager().getDockable("sidekick");
				if (tree != null) tree.reloadParserCombo();
				return;
			}
			// check if this is the currently focused edit pane

			if(editPane == view.getEditPane())
			{
				removeBufferChangeListener(this.buffer);
				deactivateParser();
				buffer = editPane.getBuffer();
				if (! buffer.isLoaded())
					return;
				parser = SideKickPlugin.getParserForBuffer(buffer);
				activateParser();

				parse(true);
			}
		}
	} //}}}

	//{{{ handleViewUpdate() method
	@EBHandler
	public void handleViewUpdate(ViewUpdate vu)
	{
		if(vu.getView() == view && buffer != view.getBuffer()
			&& vu.getWhat() == ViewUpdate.EDIT_PANE_CHANGED)
		{
			if (!isParseOnChange()) return;

			removeBufferChangeListener(this.buffer);
			deactivateParser();

			buffer = view.getBuffer();
			this.editPane = view.getEditPane();

			parser = SideKickPlugin.getParserForBuffer(buffer);
			activateParser();

			parse(true);
		}
	} //}}}

	//{{{ handlePluginUpdate() method
	@EBHandler
	public void handlePluginUpdate(PluginUpdate pmsg)
	{
		if(!exiting)
		{
			if(pmsg.getWhat() == PluginUpdate.UNLOADED
				|| pmsg.getWhat() == PluginUpdate.LOADED)
			{
				/* Pick a parser again in case our parser
				plugin was loaded or unloaded. */
				setParser(null);
			}
		}
	} //}}}

	/*
	//{{{ autoParse() method
	private void autoParse()
	{
		if(buffer.getBooleanProperty(
			"sidekick.buffer-change-parse") ||
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
	*/
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
		if(!addedBufferListener)
		{
			buffer.addBufferListener(bufferListener = new BufferChangeListener());
			addedBufferListener = true;
		}
	} //}}}

	//{{{ removeBufferChangeListener() method
	private void removeBufferChangeListener(Buffer buffer)
	{
		if(addedBufferListener)
		{
			buffer.removeBufferListener(bufferListener);
			addedBufferListener = false;
		}
	} //}}}

	//{{{ propertiesChanged() method
	/** called when the sidekick's properties are changed */
	@EBHandler
	private void propertiesChanged()
	{
		if (!isParseOnChange()) return;
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
	} //}}}

	//{{{ parseWithDelay() method
	private void parseWithDelay()
	{
		if(parser != null)
		{
			parser.stop();

			if(keystrokeTimer.isRunning())
				keystrokeTimer.stop();

			keystrokeTimer.setInitialDelay(delay);
			keystrokeTimer.setRepeats(false);
			keystrokeTimer.start();
		}
	} //}}}

	//{{{ sendUpdate() method
	private void sendUpdate()
	{
		if(view.isClosed())
			return;

		EditBus.send(new SideKickUpdate(view));
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
//			this.editPane = null;
			SideKickTree tree = (SideKickTree) view.getDockableWindowManager().getDockable("sidekick-tree");
			if (tree == null) return;
			tree.removeParserPanel();
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

		SideKickTree tree = (SideKickTree) view.getDockableWindowManager().getDockable("sidekick-tree");
		if (tree == null) return;
		tree.reloadParserCombo();
		if (parser != null)
			tree.addParserPanel(parser);
		parse(true);
	} //}}}
	
	
	//{{{ Inner classes
	
	class ParseRequestWorker extends SwingWorker<SideKickParsedData, Object>
	{
		SideKickParser parser;
		Buffer buffer;
		DefaultErrorSource errorSource;

		ParseRequestWorker(SideKickParser parser, Buffer buffer)
		{
			this.parser = parser;
			this.buffer = buffer;
			errorSource = new DefaultErrorSource("SideKick");
		}
		
		@Override
		public SideKickParsedData doInBackground()
		{
			SideKickTree tree = (SideKickTree) view.getDockableWindowManager().getDockable("sidekick-tree");
			if (tree != null)
			{
				tree.showStopButton(true);	
			}
			try
			{
				buffer.readLock();
				SideKickParsedData data = parser.parse(buffer, errorSource);
				return data;
			}
			finally
			{
				buffer.readUnlock();
			}
		}
		
		@Override
		public void done() 
		{
			try
			{
				if (isCancelled())
				{
					parser.stop();
					return;
				}
				SideKickParsedData data = get();
				
				setErrorSource(errorSource);

				int errorCount = errorSource.getErrorCount();

				if(showParsingMessage || errorCount != 0)
				{
					String label = jEdit.getProperty("sidekick.parser."
						+ parser.getName() + ".label");
					Object[] pp = { label, Integer.valueOf(errorCount)};
					view.getStatus().setMessageAndClear(jEdit.getProperty(
						"sidekick.parsing-complete",pp));
				}

				buffer.setProperty(SideKickPlugin.PARSED_DATA_PROPERTY, data);
				if(buffer.getProperty("folding").equals("sidekick"))
					buffer.invalidateCachedFoldLevels();

				View _view = jEdit.getFirstView();
				while(_view != null)
				{
					if(_view.getBuffer() == buffer)
						SideKickParsedData.setParsedData(_view, data);
					_view = _view.getNext();
				}

				sendUpdate();
			}
			catch(Exception ie) 
			{
				showNotParsedMessage();	
			}
			finally
			{
				SideKickPlugin.finishParsingBuffer(buffer);
				SideKickTree tree = (SideKickTree) view.getDockableWindowManager().getDockable("sidekick-tree");
				if (tree != null)
				{
					tree.showStopButton(false);	
				}
			}
		}
	}

	// {{{ BufferChangeListener class
	/**
	 * @since jedit 4.3pre2
	 */
	class BufferChangeListener extends BufferAdapter {

		private void parseOnKeyStroke(JEditBuffer buffer)
		{
			if(buffer != SideKick.this.buffer)
			{
				return;
			}

			if(buffer.getBooleanProperty("sidekick.keystroke-parse"))
				parseWithDelay();
		}


		public void contentInserted(JEditBuffer buffer, int startLine, int offset, int numLines, int length)
		{
			parseOnKeyStroke(buffer);
		}

		public void contentRemoved(JEditBuffer buffer, int startLine, int offset, int numLines, int length)
		{
			parseOnKeyStroke(buffer);
		}


	} //}}}
}
