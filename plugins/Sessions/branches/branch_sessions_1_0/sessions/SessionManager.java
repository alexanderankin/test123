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
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;


/**
 * A singleton class that holds a current session and has methods to switch
 * between sessions.
 */
public class SessionManager implements EBComponent
{

	public final static String SESSION_PROPERTY = "sessions.currentSession";


	/** The current session instance. */
	private Session currentSession;


	/** The singleton SessionManager instance. */
	private static SessionManager instance;


	/** Returns the singleton SessionManager instance */
	public static SessionManager getInstance()
	{
		if(instance == null)
			instance = new SessionManager();
		return instance;
	}


	/**
	 * Initialization
	 */
	private SessionManager()
	{
		currentSession = new Session(jEdit.getProperty(SESSION_PROPERTY, "default"));

		// create directory <jedithome>/sessions if it not yet exists
		File dir = new File(getSessionsDir());
		if(!dir.exists())
			dir.mkdirs();

		// convert old format session files, if necessary
		new SessionFileConverter().run();

		// create default session file if it not yet exists
		File defaultSessionFile = new File(createSessionFileName("default"));
		if(!defaultSessionFile.exists())
			new Session("default").save(null);
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
	public void setCurrentSession(final View view, final String newSessionName)
	{
		Log.log(Log.DEBUG, this, "setCurrentSession:"
			+ " currentSession=" + currentSession.getName()
			+ " newSessionName=" + newSessionName);

		if(newSessionName.equals(currentSession.getName()))
			return;

		if(jEdit.getBooleanProperty("sessions.switcher.autoSave", true))
		{
			File currentSessionFile = new File(currentSession.getFilename());
			if(currentSessionFile.exists())
				currentSession.save(view);
			else
			{
				// The current session file has been deleted, probably by the SessionManagerDialog.
				// Do nothing, because save would recreate it.
			}
		}

		// close all open buffers, if closeAll option is set:
		if (jEdit.getBooleanProperty("sessions.switcher.closeAll", true))
			if (!jEdit.closeAllBuffers(view))
				return;  // jEdit should have shown an error

		final String oldSessionName = currentSession.getName();
		EditBus.send(new SessionChanging(this, oldSessionName, newSessionName));

		// load new session
		// make sure this is not done from the AWT thread
		new Thread()
		{
			public void run()
			{
				currentSession = new Session(newSessionName);
				saveCurrentSessionProperty();
				currentSession.open(view);
				EditBus.send(new SessionChanged(
					SessionManager.this, oldSessionName, newSessionName, currentSession));
			}
		}.start();
	}


	/** Return the current session name. */
	public String getCurrentSession()
	{
		return currentSession.getName();
	}


	/** Return the current session. */
	public Session getCurrentSessionInstance()
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
		currentSession.save(view);
		saveCurrentSessionProperty();
		if (!silently)
			GUIUtilities.message(view, "sessions.switcher.save.saved", new Object[] { currentSession });
		Log.log(Log.DEBUG, this, "session saved: " + currentSession.getName());
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
		String newName = inputSessionName(view, currentSession.getName());

		if (newName == null)
			return;

		File file = new File(createSessionFileName(newName));
		if (file.exists())
		{
			int answer = GUIUtilities.confirm(view,
				"sessions.switcher.saveAs.exists",
				new Object[] { newName },
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE
			);
			if (answer != JOptionPane.YES_OPTION)
				return;
		}

		// create new session:
		Session newSession = currentSession.getClone();
		newSession.setName(newName);
		newSession.save(view);
		EditBus.send(new SessionChanging(this, currentSession.getName(), newName));

		// set new session:
		String oldSessionName = currentSession.getName();
		currentSession = newSession;
		saveCurrentSessionProperty();
		EditBus.send(new SessionListChanged(this));
		EditBus.send(new SessionChanged(this, oldSessionName, newName, currentSession));
	}


	/**
	 * Reload current session.
	 *
	 * @param view  view for displaying error messages
	 */
	public void reloadCurrentSession(final View view)
	{
		Log.log(Log.DEBUG, this, "reloadCurrentSession: currentSession=" + currentSession);

		// close all open buffers
		if(!jEdit.closeAllBuffers(view))
			return; // user cancelled

		// FIXME: do we need to make sure this is not the AWT thread?!?
		currentSession.open(view); // ignore any errors and return value
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
		SessionManagerDialog dlg = new SessionManagerDialog(view, currentSession.getName());
		String newSession = dlg.getSelectedSession();

		if(dlg.isListModified())
		{
			EditBus.send(new SessionListChanged(this));
			if(newSession == null)
			{
				// Session list has been modified, but dialog has been cancelled.
				// Send out session changed events, just in case...
				String name = currentSession.getName();
				EditBus.send(new SessionChanging(this, name, name));
				EditBus.send(new SessionChanged(this, name, name, currentSession));
			}
		}

		if(newSession != null)
			setCurrentSession(view, newSession);
	}


	public void showSessionPropertiesDialog(View view)
	{
		SessionPropertiesShowing message = new SessionPropertiesShowing(this, currentSession);
		EditBus.send(message);
		new SessionPropertiesDialog(view, currentSession.getName(), message.getRootGroup());
	}


	public static String[] getSessionNames()
	{
		String[] files = new File(getSessionsDir()).list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".xml");
			}
		});

		MiscUtilities.quicksort(files, new MiscUtilities.StringICaseCompare());

		Vector v = new Vector();
		boolean foundDefault = false;
		for (int i=0; i < files.length; i++)
		{
			String name = files[i].substring(0, files[i].length() - 4); // cut off ".xml"
			if (name.equalsIgnoreCase("default"))
			{
				// default session always first
				v.insertElementAt(name, 0);
				foundDefault = true;
			}
			else
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
	 * Converts a session name (eg, "default") to a full path name
	 * (eg, "/home/slava/.jedit/sessions/default.xml").
	 */
	public static String createSessionFileName(String session)
	{
		String filename = MiscUtilities.constructPath(getSessionsDir(), session);
		if (!filename.toLowerCase().endsWith(".xml"))
			filename = filename + ".xml";
		return filename;
	}


	/**
	 * Return the directory where the session files are stored,
	 * usually $HOME/.jedit/sessions.
	 */
	public static String getSessionsDir()
	{
		return MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "sessions");
	}


	/**
	 * Shows an input dialog asking for a session name as long as a valid
	 * session name is entered or the dialog is cancelled.
	 * A session name is valid if it doesn't contains the following characters:
	 * File.separatorChar, File.pathSeparatorChar and ':'.
	 *
	 * @param relativeTo  the component where the dialog is centered on.
	 * @param defaultName  a default session name to display in the input dialog; may be null.
	 * @return the new session name, or null if the dialog was cancelled.
	 */
	public static String inputSessionName(Component relativeTo, String defaultName)
	{
		String name = defaultName;

		do
		{
			name = GUIUtilities.input(relativeTo, "sessions.switcher.saveAs.input", name);
			if (name != null)
			{
				name = name.trim();
				if (name.length() == 0)
					GUIUtilities.error(relativeTo, "sessions.switcher.saveAs.error.empty", null);
				if (name.indexOf('/') >= 0 || name.indexOf('\\') >= 0
					|| name.indexOf(';') >= 0 || name.indexOf(':') >= 0)
				{
					GUIUtilities.error(relativeTo, "sessions.switcher.saveAs.error.illegalChars", new Object[] { "/  \\  ;  :" });
					name = "";
				}
			}
		} while (name != null && name.length() == 0);

		return name;
	}


	/**
	 * Show an error message dialog (using GUIUtilities.error())
	 * after the GUI has been updated, in the AWT thread.
	 */
	public static final void showErrorLater(final View view, final String messageProperty, final Object[] args)
	{
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				GUIUtilities.error(view, messageProperty, args);
			}
		});
	}


	/**
	 * Save current session property.
	 */
	void saveCurrentSessionProperty()
	{
		Log.log(Log.DEBUG, this, "saveCurrentSessionProperty: currentSession=" + currentSession.getName());
		jEdit.setProperty(SESSION_PROPERTY, currentSession.getName());
		jEdit.saveSettings();
	}


}

