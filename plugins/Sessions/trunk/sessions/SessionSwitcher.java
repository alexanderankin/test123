/*
 * SessionSwitcher.java - toolbar for switching between jEdit sessions
 * Copyright (c) 2001 Dirk Moebius
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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;


/**
 * A control panel for switching between jEdit sessions.
 *
 * @author Dirk Moebius
 */
public class SessionSwitcher
		extends JToolBar
		implements ActionListener, ItemListener, EBComponent
{

	public SessionSwitcher(View view)
	{
		super();
		this.view = view;

		Insets nullInsets = new Insets(0,0,0,0);

		combo = new JComboBox(SessionManager.getInstance().getSessionNames());
		combo.setSelectedItem(SessionManager.getInstance().getCurrentSession());
		combo.setEditable(false);
		combo.addItemListener(this);

		save = new JButton(GUIUtilities.loadIcon("Save24.gif"));
		save.setMargin(nullInsets);
		save.setToolTipText(jEdit.getProperty("sessions.switcher.save.tooltip"));
		save.setFocusPainted(false);
		save.addActionListener(this);

		saveAs = new JButton(GUIUtilities.loadIcon("SaveAs24.gif"));
		saveAs.setMargin(nullInsets);
		saveAs.setToolTipText(jEdit.getProperty("sessions.switcher.saveAs.tooltip"));
		saveAs.setFocusPainted(false);
		saveAs.addActionListener(this);

		reload = new JButton(GUIUtilities.loadIcon("Redo24.gif"));
		reload.setMargin(nullInsets);
		reload.setToolTipText(jEdit.getProperty("sessions.switcher.reload.tooltip"));
		reload.setFocusPainted(false);
		reload.addActionListener(this);

		prefs = new JButton(GUIUtilities.loadIcon("Preferences24.gif"));
		prefs.setMargin(nullInsets);
		prefs.setToolTipText(jEdit.getProperty("sessions.switcher.prefs.tooltip"));
		prefs.setFocusPainted(false);
		prefs.addActionListener(this);

		setFloatable(false);
		putClientProperty("JToolBar.isRollover", Boolean.TRUE);

		addSeparator(new Dimension(5,5));  // just add a little space at begin, looks better

		add(combo);
		addSeparator();
		add(save);
		add(saveAs);
		add(reload);
		add(prefs);

		// add some glue at the end of the toolbar,
		//so that the combo box doesn't get too long:
		add(Box.createGlue());

		updateTitle();
	}


	/** adds itself to EditBus on display */
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	}


	/** removes itself from EditBus on undisplay */
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	}


	// BEGIN EBComponent implementation

	public void handleMessage(EBMessage message)
	{
		if (message instanceof PropertiesChanged)
			updateTitle();
		else if (message instanceof SessionChanged)
			handleSessionChanged((SessionChanged)message);
		else if (message instanceof SessionListChanged)
			handleSessionListChanged((SessionListChanged)message);
	}

	// END EBComponent implementation


	// BEGIN ActionListener implementation

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == save)
			SessionManager.getInstance().saveCurrentSession(view);
		else if (evt.getSource() == saveAs)
			SessionManager.getInstance().saveCurrentSessionAs(view);
		else if (evt.getSource() == reload)
			SessionManager.getInstance().reloadCurrentSession(view);
		else if (evt.getSource() == prefs)
			SessionManager.getInstance().showSessionManagerDialog(view);
	}

	// END ActionListener implementation


	// BEGIN ItemListener implementation

	public void itemStateChanged(ItemEvent e)
	{
		Object selectedItem = combo.getSelectedItem();
		if (selectedItem == null)
			return;

		String selectedSession = selectedItem.toString();
		String currentSession = SessionManager.getInstance().getCurrentSession();
		if (!selectedSession.equals(currentSession))
			SessionManager.getInstance().setCurrentSession(view, selectedSession);
	}

	// END ItemListener implementation


	private void handleSessionChanged(SessionChanged msg)
	{
		final String newSession = msg.getSessionManager().getCurrentSession();

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				combo.setSelectedItem(newSession);
			}
		});
	}


	private void handleSessionListChanged(SessionListChanged msg)
	{
		final String[] sessions = msg.getSessionManager().getSessionNames();

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				combo.setModel(new DefaultComboBoxModel(sessions));
			}
		});
	}


	private void updateTitle()
	{
		if (jEdit.getBooleanProperty("sessions.switcher.showTitle", true))
			addTitle();
		else
			removeTitle();
	}


	private void addTitle()
	{
		if (title != null)
			return; // already added

		title = new JLabel(jEdit.getProperty("sessions.switcher.title"));
		add(title, 0);
		revalidate();
	}


	private void removeTitle()
	{
		if(title == null)
			return; // already removed

		remove(title);
		revalidate();
		title = null;
	}


	private View view;
	private JComboBox combo;
	private JButton save;
	private JButton saveAs;
	private JButton reload;
	private JButton prefs;
	private JLabel title;

}

