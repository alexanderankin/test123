/*
 * SessionManager.java
 * Copyright (c) 2001 Dirk Moebius, Sergey V. Udaltsov
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


package sessions;


import java.awt.Component;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;


/**
 * A singleton class that holds a current session and has methods to switch
 * between sessions.
 */
public class SessionManager implements EBComponent
{

	public final static String SESSION_PROPERTY = "sessions.currentSession";


	/** The name of the current session. */
	private String currentSession;


	/** The singleton instance. */
	private static SessionManager instance;


	/** Returns the singleton instance */
	public static SessionManager getInstance()
	{
		if (instance == null)
			instance = new SessionManager();
		return instance;
	}


	/**
	 * Initialization
	 */
	private SessionManager()
	{
		currentSession = jEdit.getProperty(SESSION_PROPERTY, "default");

		// create directory <jedithome>/sessions if it not yet exists
		String sessionDir = MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "sessions");
		File dir = new File(sessionDir);
		if (!dir.exists())
			dir.mkdirs();

		// create default session file if it not yet exists
		File defaultSessionFile = new File(createSessionFileName("default"));
		if (!defaultSessionFile.exists())
			saveSession(null, "default");
	}


	/**
	 * Switch to a new session.
	 * This sends out a SessionChanged message on EditBus, if the
	 * session could be changed successfully.
	 *
	 * @param view  view for displaying error messages
	 * @param newSession  the new session name
	 * @return false, if the new session could not be set.
	 */
	public void setCurrentSession(View view, String newSession)
	{
		Log.log(Log.DEBUG, this, "setCurrentSession:"
			+ " currentSession=" + currentSession
			+ " newSession=" + newSession);

		if (newSession.equals(currentSession))
			return;

		if (jEdit.getBooleanProperty("sessions.switcher.autoSave", true))
		{
			File currentSessionFile = new File(createSessionFileName(currentSession));
			if (currentSessionFile.exists())
				saveSession(view, currentSession);
			else
			{
				// The current session file has been deleted, probably by the SessionManagerDialog.
				// Do nothing, because save would recreate it.
			}
		}

		// close all open buffers, if closeAll option is set:
		if (jEdit.getBooleanProperty("sessions.switcher.closeAll", true))
		{
			if (!jEdit.closeAllBuffers(view))
				return;  // jEdit should have shown an error
			VFSManager.waitForRequests();
			if (VFSManager.errorOccurred())
				return; // some dirty files couldn't be saved
		}

		EditBus.send(new SessionChanging(this, newSession));

		// load new session:
		Buffer buffer = loadSession(newSession);
		VFSManager.waitForRequests();
		if (VFSManager.errorOccurred())
			return;

		// set new session:
		String oldSession = currentSession;
		currentSession = newSession;
		saveCurrentSessionProperty();
		EditBus.send(new SessionChanged(this, oldSession));

		// open session's recent buffer:
		if (buffer != null)
			view.setBuffer(buffer);
	}


	/** Return the current session name. */
	public String getCurrentSession()
	{
		return currentSession;
	}


	/**
	 * Save current session and show a dialog that it has been saved.
	 *
	 * @param view  view for displaying error messages
	 */
	public void saveCurrentSession(View view)
	{
		saveCurrentSession(view, false);
	}


	/**
	 * Save current session.
	 *
	 * @param view  view for displaying error messages
	 * @param silently  if false, show a dialog that the current session has been saved.
	 */
	public void saveCurrentSession(View view, boolean silently)
	{
		saveSession(view, currentSession);
		saveCurrentSessionProperty();
		if (!silently)
			GUIUtilities.message(view, "sessions.switcher.save.saved", new Object[] { currentSession });
	}


	/**
	 * Save current session under a different name and switch to it.
 	 * This sends out SessionListChanged and SessionChanged messages
	 * on EditBus, if the session could be changed successfully, in this
	 * order.
	 *
	 * @param view  view for displaying error messages
	 */
	public void saveCurrentSessionAs(View view)
	{
		String name = inputSessionName(view, currentSession);

		if (name == null)
			return;

		File file = new File(createSessionFileName(name));
		if (file.exists())
		{
			int answer = JOptionPane.showConfirmDialog(view,
				jEdit.getProperty("sessions.switcher.saveAs.exists.message", new Object[] { name }),
				jEdit.getProperty("sessions.switcher.saveAs.exists.title"),
				JOptionPane.YES_NO_OPTION
			);

			if (answer != JOptionPane.YES_OPTION)
				return;
		}

		saveSession(view, name);
		EditBus.send(new SessionChanging(this, name));

		// set new session:
		String oldSession = currentSession;
		currentSession = name;
		saveCurrentSessionProperty();
		EditBus.send(new SessionListChanged(this));
		EditBus.send(new SessionChanged(this, oldSession));
	}


	/**
	 * Reload current session.
	 *
	 * @param view  view for displaying error messages
	 */
	public void reloadCurrentSession(View view)
	{
		Log.log(Log.DEBUG, this, "reloadCurrentSession: currentSession=" + currentSession);

		// close all open buffers
		if (!jEdit.closeAllBuffers(view))
			return; // user cancelled

		VFSManager.waitForRequests();
		if (VFSManager.errorOccurred())
			return;

		Buffer buffer = loadSession(currentSession);
		VFSManager.waitForRequests();
		if (buffer != null)
			view.setBuffer(buffer);
	}


	/**
	 * Show the Session Manager dialog.
	 * If the user changes the current session or modifies the
	 * list of sessions, SessionChanged and SessionListChanged
	 * messages are sent on EditBus.
	 *
	 * @param  view  center dialog on this View.
	 */
	public void showSessionManagerDialog(View view)
	{
		SessionManagerDialog dlg = new SessionManagerDialog(view, currentSession);
		String newSession = dlg.getSelectedSession();

		if(dlg.isListModified())
		{
			EditBus.send(new SessionListChanged(this));
			if(newSession == null)
			{
				EditBus.send(new SessionChanging(this, currentSession));
				EditBus.send(new SessionChanged(this, currentSession));
			}
		}

		if(newSession != null)
			setCurrentSession(view, dlg.getSelectedSession());
	}


	public String[] getSessionNames() {
		String path = MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "sessions");

		String[] files = new File(path).list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".session");
			}
		});

		MiscUtilities.quicksort(files, new MiscUtilities.StringICaseCompare());

		Vector v = new Vector();
		boolean foundDefault = false;
		for (int i=0; i < files.length; i++) {
			String name = files[i].substring(0, files[i].length() - 8);
			if (name.equalsIgnoreCase("default")) {
				// default session always first
				v.insertElementAt(name, 0);
				foundDefault = true;
			} else
				v.addElement(name);
		}

		if (!foundDefault)
			v.insertElementAt("default", 0);

		String[] result = new String[v.size()];
		v.copyInto(result);
		return result;
	}


	/** EBComponent implementation; does nothing */
	public void handleMessage(EBMessage msg) {}


	/**
	 * Converts a session name (eg, default) to a full path name
	 * (eg, /home/slava/.jedit/sessions/default.session).
	 */
	public static String createSessionFileName(String session) {
		String filename = MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "sessions", session);

		if (!filename.toLowerCase().endsWith(".session"))
			filename = filename + ".session";

		return filename;
	}


	/**
	 * Shows an input dialog asking for a session name as long as a valid
	 * session name is entered or the dialog is cancelled.
	 * A session name is valid if it doesn't contains the following characters:
	 * File.separatorChar, File.pathSeparatorChar and ':'.
	 *
	 * @param relativeTo  the component where the dialog is centered on.
	 * @param defaultName  a default session name to display in the input dialog; may be null.
	 * @return the new session name without trailing ".session", or null, if
	 *     the dialog was cancelled.
	 */
	public static String inputSessionName(Component relativeTo, String defaultName) {
		String name = defaultName;

		do {
			name = GUIUtilities.input(relativeTo, "sessions.switcher.saveAs.input", name);
			if (name != null) {
				name = name.trim();
				if (name.length() == 0)
					GUIUtilities.error(relativeTo, "sessions.switcher.saveAs.error.empty", null);
				if (name.indexOf('/') >= 0 || name.indexOf('\\') >= 0
					|| name.indexOf(';') >= 0 || name.indexOf(':') >= 0) {
					GUIUtilities.error(relativeTo, "sessions.switcher.saveAs.error.illegalChars", new Object[] { "/  \\  ;  :" });
					name = "";
				}
			}
		} while (name != null && name.length() == 0);

		if (name != null && name.toLowerCase().endsWith(".session"))
			name = name.substring(0, name.length() - 8);

		return name;
	}


	/**
	 * Save current session property only, without saving the current list of
	 * open files and without firing any events or messages.
	 */
	void saveCurrentSessionProperty() {
		jEdit.setProperty(SESSION_PROPERTY, currentSession);
	}


	/**
	 * Loads a session.
	 * Does nothing, if the a session file with the specified name does not exist.
	 * @param session The file name, relative to $HOME/.jedit/sessions
	 *     (.session suffix not required)
	 * @return the buffer that was used at last in this session, or null if
	 *     an error occured or there is no last session.
	 */
	private Buffer loadSession(String session) {
		String filename = createSessionFileName(session);
		if (!new File(filename).exists())
			return null;

		Buffer buffer = null;

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line;

			while ((line = in.readLine()) != null) {
				Buffer _buffer = readSessionCommand(line);
				if (_buffer != null)
					buffer = _buffer;
			}

			in.close();
		}
		catch (FileNotFoundException fnf) {
			Log.log(Log.NOTICE, this, fnf);
			String[] args = { filename };
			GUIUtilities.error(null, "filenotfound", args);
		}
		catch (IOException io) {
			Log.log(Log.ERROR, this, io);
			String[] args = { io.getMessage() };
			GUIUtilities.error(null, "ioerror", args);
		}

		return buffer;
	}


	/**
	 * Parse one line from a session file.
	 */
	private Buffer readSessionCommand(String line) {
		// handle path:XXX for backwards compatibility
		// with jEdit 2.2 sessions
		if (line.startsWith("path:"))
			line = line.substring(5);

		boolean current = false;
		StringTokenizer st = new StringTokenizer(line, "\t");
		String path = st.nextToken();

		// ignore all tokens except for 'current' to maintain
		// compatibility with jEdit 2.2 sessions
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.equals("current"))
				current = true;
		}

		if (path == null)
			return null;

		Buffer buffer = jEdit.openFile(null, path);
		if (buffer == null)
			return null;

		return (current ? buffer : null);
	}


	/**
	 * Saves the session
	 * @param view The view this is being saved from.
	 *     If non-null, the caret information and current buffer
	 *     is taken from this view, otherwise this information
	 *     won't be saved into the session file.
	 * @param session The file name, relative to $HOME/.jedit/sessions
	 *     (.session suffix not required)
	 */
	private void saveSession(View view, String session) {
		if (view != null)
			view.getEditPane().saveCaretInfo();

		String lineSep = System.getProperty("line.separator");
		String filename = createSessionFileName(session);
		Buffer viewBuffer = view != null ? view.getBuffer() : null;
		Buffer buffer = jEdit.getFirstBuffer();

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));

			while (buffer != null) {
				if (!buffer.isUntitled()) {
					out.write(buffer.getPath());
					if (buffer == viewBuffer)
						out.write("\tcurrent");
					out.write(lineSep);
				}
				buffer = buffer.getNext();
			}

			out.close();
		}
		catch (IOException io) {
			Log.log(Log.ERROR, this, io);
			String[] args = { io.getMessage() };
			GUIUtilities.error(view, "ioerror", args);
		}
	}

}

