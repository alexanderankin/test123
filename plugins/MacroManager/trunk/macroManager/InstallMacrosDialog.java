/*
 * InstallMacrosDialog.java - macro install dialog box
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Carmine Lucarelli
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

package macroManager;

//{{{ Imports
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
//}}}

public class InstallMacrosDialog extends EnhancedDialog
{
	static final int INSTALL = 0;
	static final int UPDATE = 1;

	//{{{ InstallPluginsDialog constructor
/*	InstallPluginsDialog(JDialog dialog, Vector model, int mode)
	{
		super(JOptionPane.getFrameForComponent(dialog),
			(mode == INSTALL
			? jEdit.getProperty("install-macros.title")
			: jEdit.getProperty("update-macros.title")),true);  */

	public InstallMacrosDialog(Frame frame)
	{
		super(frame, jEdit.getProperty("install-macros.title"), true);

		JPanel content = new JPanel(new BorderLayout(12,12));
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		JLabel label = new JLabel(jEdit.getProperty("install-macros.caption"));
		content.add(BorderLayout.NORTH,label);

		MacroList list = null;
		try
		{
			list = new MacroListDownloadProgress(InstallMacrosDialog.this)
				.getMacroList();
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR, this, "An error occurred " + e.getMessage());
		}
		
		if(list == null)
			return;
		
		macros = new JCheckBoxList(list.macros);
		macros.getSelectionModel().addListSelectionListener(new ListHandler());
		macros.getModel().addTableModelListener(new TableModelHandler());
		JScrollPane scroller = new JScrollPane(macros);
		scroller.setPreferredSize(new Dimension(200,0));
		content.add(BorderLayout.WEST,scroller);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder(jEdit.getProperty("install-macros"
			+ ".macro-info")));

		JPanel labelAndValueBox = new JPanel(new BorderLayout());

		JPanel labelBox = new JPanel(new GridLayout(6,1,0,3));
		labelBox.setBorder(new EmptyBorder(0,0,3,12));
		labelBox.add(new JLabel(jEdit.getProperty("install-macros"
			+ ".info.name"),SwingConstants.RIGHT));
		labelBox.add(new JLabel(jEdit.getProperty("install-macros"
			+ ".info.author"),SwingConstants.RIGHT));
		labelBox.add(new JLabel(jEdit.getProperty("install-macros"
			+ ".info.size"),SwingConstants.RIGHT));
		labelBox.add(new JLabel(jEdit.getProperty("install-macros"
			+ ".info.latest-version"),SwingConstants.RIGHT));
		labelBox.add(new JLabel(jEdit.getProperty("install-macros"
			+ ".info.description"),SwingConstants.RIGHT));
		labelAndValueBox.add(BorderLayout.WEST,labelBox);

		JPanel valueBox = new JPanel(new GridLayout(6,1,0,3));
		valueBox.setBorder(new EmptyBorder(0,0,3,0));
		valueBox.add(name = new JLabel());
		valueBox.add(author = new JLabel());
		valueBox.add(size = new JLabel());
		valueBox.add(latestVersion = new JLabel());
		valueBox.add(Box.createGlue());
		labelAndValueBox.add(BorderLayout.CENTER,valueBox);

		panel.add(BorderLayout.NORTH,labelAndValueBox);

		description = new JTextArea(6,50);
		description.setEditable(false);
		description.setLineWrap(true);
		description.setWrapStyleWord(true);

		panel.add(BorderLayout.CENTER,new JScrollPane(description));

		content.add(BorderLayout.CENTER,panel);

		panel = new JPanel(new BorderLayout(12,0));

		JPanel panel2 = new JPanel(new GridLayout(4,1));

		Box totalSizeBox = new Box(BoxLayout.X_AXIS);
		totalSizeBox.add(new JLabel(jEdit.getProperty("install-macros.totalSize")));
		totalSizeBox.add(Box.createHorizontalStrut(12));
		totalSizeBox.add(totalSize = new JLabel());
		panel2.add(totalSizeBox);

		ButtonGroup grp = new ButtonGroup();
		installUser = new JRadioButton();
		String settings = jEdit.getSettingsDirectory();
		if(settings == null)
		{
			settings = jEdit.getProperty("install-macros.none");
			installUser.setEnabled(false);
		}
		else
		{
			settings = MiscUtilities.constructPath(settings,"macros");
			installUser.setEnabled(true);
		}
		String[] args = { settings };
		installUser.setText(jEdit.getProperty("install-macros.user",args));
		grp.add(installUser);
		panel2.add(installUser);

		installSystem = new JRadioButton();
		String jEditHome = jEdit.getJEditHome();
		if(jEditHome == null)
		{
			jEditHome = jEdit.getProperty("install-macros.none");
			installSystem.setEnabled(false);
		}
		else
		{
			jEditHome = MiscUtilities.constructPath(jEditHome,"macros");
			installSystem.setEnabled(true);
		}
		args[0] = jEditHome;
		installSystem.setText(jEdit.getProperty("install-macros.system",args));
		grp.add(installSystem);
		panel2.add(installSystem);

		if(installUser.isEnabled())
			installUser.setSelected(true);
		else
			installSystem.setSelected(true);

		panel.add(BorderLayout.NORTH,panel2);

		Box box = new Box(BoxLayout.X_AXIS);

		box.add(Box.createGlue());
		install = new JButton(jEdit.getProperty("install-macros.install"));
		install.setEnabled(false);
		getRootPane().setDefaultButton(install);
		install.addActionListener(new ActionHandler());
		box.add(install);
		box.add(Box.createHorizontalStrut(6));

		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(new ActionHandler());
		box.add(cancel);
		box.add(Box.createHorizontalStrut(6));
		box.add(Box.createGlue());

		panel.add(BorderLayout.SOUTH,box);

		content.add(BorderLayout.SOUTH,panel);

		updateTotalSize();

		pack();
		setLocationRelativeTo(frame);
		show();
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		macroManager.Roster roster = new macroManager.Roster();
		installMacros(roster);
		if(roster.isEmpty())
			return;
		new MacroManagerProgress(InstallMacrosDialog.this, roster);
		
		// rescan the macros to update the menu
		Macros.loadMacros();
		dispose();
	} //}}}

	//{{{ cancel() method
	public void cancel()
	{
		cancelled = true;

		dispose();
	} //}}}

	//{{{ installPlugins() method
	void installMacros(macroManager.Roster roster)
	{
		if(cancelled)
			return;

		String installDirectory;
		if(installUser == null || installUser.isSelected())
		{
			installDirectory = MiscUtilities.constructPath(
				jEdit.getSettingsDirectory(),"macros");
		}
		else
		{
			installDirectory = MiscUtilities.constructPath(
				jEdit.getJEditHome(),"macros");
		}

		Object[] selected = macros.getCheckedValues();
		for(int i = 0; i < selected.length; i++)
		{
			((MacroList.Macro)selected[i]).install(roster, installDirectory);
		}
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private JCheckBoxList macros;
	private JLabel name;
	private JLabel author;
	private JLabel size;
	private JLabel latestVersion;
	private JTextArea description;
	private JLabel totalSize;
	private JRadioButton installUser;
	private JRadioButton installSystem;

	private JButton install;
	private JButton cancel;

	private boolean cancelled;
	private Thread thread;
	//}}}

	//{{{ updateInfo() method
	private void updateInfo()
	{
		Object selected = macros.getSelectedValue();
		if(selected instanceof MacroList.Macro)
		{
			MacroList.Macro macro = (MacroList.Macro)selected;
			name.setText(macro.name);
			author.setText(macro.author);
			size.setText(String.valueOf((macro.size / 1024)) + " Kb");
			latestVersion.setText(macro.version);
			description.setText(macro.description);
			description.setCaretPosition(0);
		}
		else
		{
			name.setText(null);
			author.setText(null);
			size.setText(null);
			latestVersion.setText(null);
			description.setText(null);
		}
	} //}}}

	//{{{ updateTotalSize() method
	private void updateTotalSize()
	{
		ArrayList selectedMacros = new ArrayList();

		Object[] selected = macros.getCheckedValues();
		install.setEnabled(selected.length != 0);

		for(int i = 0; i < selected.length; i++)
		{
			MacroList.Macro macro = (MacroList.Macro)selected[i];
			if(!selectedMacros.contains(macro))
				selectedMacros.add(macro);
		}

		int _totalSize = 0;
		for(int i = 0; i < selectedMacros.size(); i++)
		{
			_totalSize += ((MacroList.Macro)selectedMacros.get(i)).size;
		}

		totalSize.setText(String.valueOf(_totalSize / 1024) + " Kb");
	} //}}}

	//}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == install)
				ok();
			else if(source == cancel)
				cancel();
		}
	} //}}}

	//{{{ ListHandler class
	class ListHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			updateInfo();
		}
	} //}}}

	//{{{ TableModelHandler class
	class TableModelHandler implements TableModelListener
	{
		public void tableChanged(TableModelEvent e)
		{
			updateTotalSize();
		}
	} //}}}
}
