/*
 * ModeOptionsDialog.java 
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Alan Ezust
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

// {{{ imports
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;
// }}}

/** {{{ ModeOptionsDialog class 
 * A customized OptionDialog for
 * SideKick, which includes a shared ComboBox for the current edit
 * mode.
 * 
 * It creates an OptionPane for each plugin that defines the proper
 * service, and is currently loaded.
 * 
 * @see ModeOptionsPane 
 * @author Alan Ezust
 *
 */

public class ModeOptionsDialog extends OptionsDialog
{
	// {{{ data members
	public static final String SERVICECLASS="org.gjt.sp.jedit.options.ModeOptionPane";
	public static final String ALL="ALL";

	OptionTreeModel paneTreeModel;
	StringList modes;
	JComboBox modeCombo;	
	JButton useDefaultsCheck;
	// }}}
	
	// {{{ ModeOptionsDialog ctor
	public ModeOptionsDialog(View v) {
		super(v, "options.mode.settings", "sidekick.mode");
	} // }}}
	
	// {{{ getMode()
	public String getMode() {
		return modeCombo.getSelectedItem().toString();
	} // }}}
	
	// {{{ createOptionTreeModel method
	protected OptionTreeModel createOptionTreeModel()
	{
		modes = new StringList(jEdit.getModes());
		
		Collections.sort(modes,new MiscUtilities.StringICaseCompare());
		modes.add(0, ALL);
		modeCombo = new JComboBox(modes.toArray());
		useDefaultsCheck = new JButton(jEdit.getProperty("options.editing.useDefaults"));
		JLabel editModeLabel = new JLabel(jEdit.getProperty("buffer-options.mode"));
		
		GridLayout gl = new GridLayout(1, 3);
		JPanel editModePanel = new JPanel(gl);
		// JLabel spacer = new JLabel(" ");
		//for (int i=0; i<3; ++i) editModePanel.add(spacer);

		editModePanel.add(useDefaultsCheck);
		editModePanel.add(editModeLabel);
		editModePanel.add(modeCombo);
		
		
		JPanel content = (JPanel) getContentPane();
		content.add(editModePanel, BorderLayout.NORTH);
				
		paneTreeModel = new OptionTreeModel();
		OptionGroup root = (OptionGroup) (paneTreeModel.getRoot());
		
		// iterate through all parsers and get their name, attempt to get an option pane. 
		for (String service: ServiceManager.getServiceNames(SERVICECLASS)) 
		{
			ModeOptionsPane mop = (ModeOptionsPane) ServiceManager.getService(SERVICECLASS, service);
			modeCombo.addItemListener(mop);
			root.addOptionPane(mop);	
		}
		ActionHandler actionListener = new ActionHandler();

		modeCombo.addActionListener(actionListener);
		useDefaultsCheck.addActionListener(actionListener);

		String currentMode = jEdit.getActiveView().getBuffer().getMode().getName();
		modeCombo.setSelectedItem(currentMode);

		return paneTreeModel;
	} // }}}

	// {{{ load() method
	private void load(Object obj)
	{
		if(obj instanceof OptionGroup)
		{
			OptionGroup grp = (OptionGroup)obj;
			Enumeration members = grp.getMembers();
			while(members.hasMoreElements())
			{
				load(members.nextElement());
			}
		}
		else if(obj instanceof ModeOptionsPane)
		{
			try
			{
				((ModeOptionsPane)obj)._load();
			}
			catch(Throwable t)
			{
				Log.log(Log.ERROR,this,"Error loading options:");
				Log.log(Log.ERROR,this,t);
			}
		}
		
	} //}}}

	
	// {{{ load method
	protected void load() {
		load(getDefaultGroup());
	} // }}}
	
	// {{{ getDefaultGroup() method
	protected OptionGroup getDefaultGroup()
	{
		return (OptionGroup) paneTreeModel.getRoot();
	} // }}}

	// {{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		//{{{ actionPerformed() method
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == modeCombo)
			{
				String m = getMode();
				useDefaultsCheck.setEnabled(!m.equals(ALL));			
			}
			else if (source == useDefaultsCheck) try 
			{
				ModeOptionsPane spp = ((ModeOptionsPane)currentPane);
				spp._reset();
				spp._load();
			}
			catch (ClassCastException cce) 
			{
					Log.log(Log.NOTICE, this, "Wrong kind of pane?", cce);
			}
				
		}
	} //}}}

} // }}}

