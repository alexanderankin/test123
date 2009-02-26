/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package browser;

//{{{ imports
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import options.GlobalOptionPane;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

public class GlobalPlugin extends EBPlugin
{
	public static final String OPTION_PREFIX = "options.GlobalPlugin.";

	//{{{ EBPlugin methods

	public static final String CALL_TREE_BROWSER = "call-tree-browser";
	public static final String REFERENCE_BROWSER = "reference-browser";
	public static final String DEFINITION_BROWSER = "definition-browser";
	public static final String PATTERN_BROWSER = "pattern-browser";
	
	private static Pattern identifierRegexp;
	private Timer autoUpdateTimer = null;
	
	//{{{ start() method
	public void start()
	{
		propertiesChanged();
	} //}}}

	//{{{ stop()
	public void stop()
	{
	} //}}}

	private void propertiesChanged()
	{
		identifierRegexp = Pattern.compile(
				jEdit.getProperty(GlobalOptionPane.IDENTIFIER_REGEXP_OPTION));
		if (GlobalOptionPane.isAutoUpdateDB() &&
			GlobalOptionPane.isAutoUpdatePeriodically())
		{
			int seconds = GlobalOptionPane.getAutoUpdateSeconds();
			if (autoUpdateTimer == null)
				autoUpdateTimer = new Timer();
			autoUpdateTimer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					String dir = jEdit.getActiveView().getBuffer().getDirectory();
					updateDatabase(dir);
				}
			}, seconds * 1000, seconds * 1000);
			
		}
		else if (autoUpdateTimer != null) {
			autoUpdateTimer.cancel();
			autoUpdateTimer = null;
		}
	}
	
	static public String getIdentifierUnderCaret(View view)
	{
		JEditTextArea textArea = view.getTextArea();
		int offset = textArea.getCaretPosition(); 
		int line = textArea.getLineOfOffset(offset);
		int index = offset - textArea.getLineStartOffset(line);
		String text = textArea.getLineText(line);
		Matcher m = identifierRegexp.matcher(text);
		while (m.find())
		{
			if (m.start() <= index && m.end() > index)
				return m.group();
		}
		return null;
	}
	
	static public String getIdentifierForQuery(View view)
	{
		String selected = view.getTextArea().getSelectedText();
		if (selected == null)
			selected = getIdentifierUnderCaret(view);
		return selected;
	}
	
	static public void invokeDockable(View view, String name)
	{
		String identifier = getIdentifierForQuery(view);
		if (identifier == null)
		{
			Log.log(Log.ERROR, GlobalPlugin.class, "No identifier selected for " + name);
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		DockableWindowManager dwm = view.getDockableWindowManager(); 
		dwm.showDockableWindow(name);
		GlobalDockableInterface dockable = (GlobalDockableInterface) dwm.getDockable(name);
		dockable.show(view, identifier);
	}
	
	static public void jump(final View view, final String file, final int line)
	{
		if (file == null)
			return;
		final Buffer buffer = jEdit.openFile(view, file);
		if(buffer == null) {
			view.getStatus().setMessage("Unable to open: " + file);
			return;
		}
		final Runnable moveCaret = new Runnable() {
			public void run() {
				JEditTextArea ta = view.getTextArea();
				ta.setCaretPosition(ta.getLineStartOffset(line - 1));
			}
		};
		if (buffer.isLoaded())
		{
			moveCaret.run();
		}
		else
		{
			buffer.addBufferListener(new BufferAdapter() {
				@Override
				public void bufferLoaded(JEditBuffer buffer) {
					moveCaret.run();
				}
			});
		}
	}

	private void runInBackground(Runnable runnable) {
		Thread task = new Thread(runnable);
		task.start();
	}
	
	private void updateDatabase(final String workingDirectory) {
		runInBackground(new Runnable() {
			public void run() {
				GlobalLauncher.instance().run("-u", workingDirectory);
			}
		});
	}
	
	private void handleBufferUpdate(BufferUpdate bu) {
		// Auto-update database when files from the database are saved
		if (GlobalOptionPane.isAutoUpdateDB() &&
			GlobalOptionPane.isAutoUpdateOnSave() &&
		    bu.getWhat().equals(BufferUpdate.SAVED))
		{
			// Check if the saved buffer is in the database
			Buffer buffer = bu.getBuffer();
			String path = buffer.getPath();
			String dir = buffer.getDirectory();
			if (GlobalLauncher.instance().isFileInDatabase(path, dir))
				updateDatabase(dir);
		}
	}
	
	@Override
	public void handleMessage(EBMessage message) {
		if (message instanceof PropertiesChanged)
			propertiesChanged();
		else if (message instanceof BufferUpdate)
			handleBufferUpdate((BufferUpdate) message);
	}
}

// :collapseFolds=1:noTabs=false:lineSeparator=\r\n:tabSize=4:indentSize=4:deepIndent=false:folding=explicit:
