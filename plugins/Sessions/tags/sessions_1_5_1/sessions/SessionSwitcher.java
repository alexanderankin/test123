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
import org.gjt.sp.jedit.io.VFSManager;
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

	public SessionSwitcher(View view, boolean isInDefaultToolBar)
	{
		super();
		this.view = view;

		Insets nullInsets = new Insets(0,0,0,0);

		combo = new JComboBox(SessionManager.getInstance().getSessionNames());
		int maxEntries = jEdit.getIntegerProperty(
			"options.sessions.switcher.maxListSize", 8);
		combo.setMaximumRowCount(maxEntries);
		combo.setSelectedItem(SessionManager.getInstance().getCurrentSession());
		combo.setEditable(false);
		combo.addItemListener(this);

		save = new JButton(new ImageIcon(getClass().getResource("Save24.gif")));
		save.setMargin(nullInsets);
		save.setToolTipText(jEdit.getProperty("sessions.switcher.save.tooltip"));
		save.setFocusPainted(false);
		save.addActionListener(this);

		saveAs = new JButton(new ImageIcon(getClass().getResource("SaveAs24.gif")));
		saveAs.setMargin(nullInsets);
		saveAs.setToolTipText(jEdit.getProperty("sessions.switcher.saveAs.tooltip"));
		saveAs.setFocusPainted(false);
		saveAs.addActionListener(this);

		reload = new JButton(new ImageIcon(getClass().getResource("Redo24.gif")));
		reload.setMargin(nullInsets);
		reload.setToolTipText(jEdit.getProperty("sessions.switcher.reload.tooltip"));
		reload.setFocusPainted(false);
		reload.addActionListener(this);

		props = new JButton(new ImageIcon(getClass().getResource("History24.gif")));
		props.setMargin(nullInsets);
		props.setToolTipText(jEdit.getProperty("sessions.switcher.props.tooltip"));
		props.setFocusPainted(false);
		props.addActionListener(this);

		prefs = new JButton(new ImageIcon(getClass().getResource("Preferences24.gif")));
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
		//add(props);  // FIXME: not yet - EXPERIMENTAL
		add(prefs);

		// if we're not added to jEdit's default toolbar, then add some glue at
		// the end of the toolbar, so that we're left aligned and the combo box
		// doesn't get too long:
		if(!isInDefaultToolBar)
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
		else if (evt.getSource() == props)
			SessionManager.getInstance().showSessionPropertiesDialog(view);
		else if (evt.getSource() == prefs)
			SessionManager.getInstance().showSessionManagerDialog(view);
	}

	// END ActionListener implementation


	// BEGIN ItemListener implementation

	public void itemStateChanged(ItemEvent e)
	{
		if(e.getStateChange() != ItemEvent.SELECTED || e.getItem() == null)
			return;

		String currentSession = SessionManager.getInstance().getCurrentSession();
		final String selectedSession = e.getItem().toString();

		if (selectedSession.equals(currentSession)) return;
		
		SessionManager.getInstance().setCurrentSession(view, selectedSession);
		// The session may not have been changed (eg. if the session change 
		// was cancelled by the user while closing all open buffers).
		// Make sure the combo box is correct by calling ...
		updateSessionComboBox();
	}  

	// END ItemListener implementation


	private void handleSessionChanged(SessionChanged msg) {
		updateSessionComboBox();
	}
	
	private void updateSessionComboBox()
	{
		final String newSession = SessionManager.getInstance().getCurrentSession();

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
	private JButton props;
	private JButton prefs;
	private JLabel title;

}

