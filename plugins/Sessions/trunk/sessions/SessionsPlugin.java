/*
 * SessionsPlugin.java
 * Copyright (c) 2000,2001 Dirk Moebius
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


import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;


/**
 * The Sessions plugin.
 *
 * @author Dirk Moebius
 */
public class SessionsPlugin extends EBPlugin
{

	public void start()
	{
		boolean restore = jEdit.getBooleanProperty("restore");
		boolean restore_cli =  jEdit.getBooleanProperty("restore.cli");
		boolean autosave = jEdit.getBooleanProperty("sessions.switcher.autoSave", true);

		if ((!restore || !restore_cli) && autosave)
		{
			// Show a warning if either "Restore previously open files on startup"
			// or "Restore even if file names were specified on the command line"
			// is off and Session Autosave is on:
			showInfoMessage("sessions.manager.info.restore_autosave");
		}
		else if (!restore)
		{
			// "Restore previously open files on startup" is off.
			// The last open session won't be restored.
			// Show an information dialog.
			showInfoMessage("sessions.manager.info.restore");
		}
	}


	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("sessions.menu"));
	}


	public void createOptionPanes(OptionsDialog od)
	{
		od.addOptionPane(new SessionsOptionPane());
	}


	public void handleMessage(EBMessage message)
	{
		if (message instanceof ViewUpdate)
		{
			ViewUpdate vu = (ViewUpdate) message;
			if (vu.getWhat() == ViewUpdate.CREATED)
			{
				if(jEdit.getBooleanProperty("sessions.switcher.showToolBar", false))
					addSessionSwitcher(vu.getView());
			}
			else if (vu.getWhat() == ViewUpdate.CLOSED)
			{
				viewSessionSwitchers.remove(vu.getView());
			}
		}
		else if (message instanceof EditorExitRequested)
		{
			EditorExitRequested eer = (EditorExitRequested) message;
			handleEditorExit(eer.getView());
		}
		else if (message instanceof PropertiesChanged) {
			handlePropertiesChanged();
		}
	}


	private void handleEditorExit(View view)
	{
		// remember the last open session:
		SessionManager mgr = SessionManager.getInstance();
		mgr.saveCurrentSessionProperty();

		// if autosave sessions is on, save current session silently:
		if (jEdit.getBooleanProperty("sessions.switcher.autoSave", true))
		{
			Log.log(Log.DEBUG, this, "autosaving current session...");
			mgr.saveCurrentSession(view, true);
		}
	}


	private void handlePropertiesChanged()
	{
		boolean show = jEdit.getBooleanProperty("sessions.switcher.showToolBar", false);
		View view = jEdit.getFirstView();

		while (view != null)
		{
			if (show)
				addSessionSwitcher(view);
			else
				removeSessionSwitcher(view);
			view = view.getNext();
		}
	}


	private void addSessionSwitcher(final View view)
	{
		removeSessionSwitcher(view);

		if(jEdit.getBooleanProperty("sessions.switcher.showJEditToolBar", false))
		{
			if(view.getToolBar() != null)
			{
				// Add to jEdit's default toolbar:
				final SessionSwitcher switcher = new SessionSwitcher(view, true);
				viewSessionSwitchers.put(view, switcher);

				// We need to add it later. Cannot add it right now, because if the View
				// receives the PropertiesChanged message, it removes and recreates
				// the toolbar. If it receives the message _after_ we received it, then
				// our switcher would be gone.
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						view.getToolBar().add(switcher);
						view.getRootPane().revalidate();
					}
				});
			}
		}
		else
		{
			// Add a _new_ toolbar to the View:
			SessionSwitcher switcher = new SessionSwitcher(view, false);
			view.addToolBar(switcher);
			viewSessionSwitchers.put(view, switcher);
		}
	}


	private void removeSessionSwitcher(View view)
	{
		SessionSwitcher switcher = (SessionSwitcher) viewSessionSwitchers.get(view);
		if (switcher != null)
		{
			// try to remove toolbar (does nothing if it is not there)
			view.removeToolBar(switcher);

			if(view.getToolBar() != null)
			{
				// try to remove from jEdit's default toolbar (does nothing if it is not there)
				view.getToolBar().remove(switcher);
				view.getRootPane().revalidate();
			}

			viewSessionSwitchers.remove(view);
		}
	}


	private void showInfoMessage(String key)
	{
		if (!jEdit.getBooleanProperty(key + ".notAgain"))
		{
			String title = jEdit.getProperty(key + ".title");
			String msg = jEdit.getProperty(key + ".message");
			String msg2 = jEdit.getProperty("sessions.manager.info.dontShowAgain");
			JCheckBox notAgain = new JCheckBox(msg2, false);
			JOptionPane.showMessageDialog(null, new Object[] { msg, notAgain },
				title, JOptionPane.INFORMATION_MESSAGE);
			jEdit.setBooleanProperty(key + ".notAgain", notAgain.isSelected());
		}
	}


	private Hashtable viewSessionSwitchers = new Hashtable();

}

