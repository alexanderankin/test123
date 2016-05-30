/*
 * GeneralOptionPane.java - General settings
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2004 Slava Pestov
 * parts Copyright (C) 2010 Eric Le Lay
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

package console.options;

//{{{ Imports
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.event.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.FontSelector;
import org.gjt.sp.util.StandardUtilities.StringCompare;
import org.gjt.sp.jedit.gui.PingPongList;
import org.gjt.sp.util.StringList;
import org.gjt.sp.jedit.gui.HistoryModel ;
import org.gjt.sp.jedit.ServiceManager;

import console.Shell;
import console.gui.Label;
//}}}

public class GeneralOptionPane extends AbstractOptionPane
{

	// {{{ data members

	private FontSelector font;
	private JComboBox encoding;
	private JComboBox defaultShell;
	private PingPongList<String> pingPongList;
	private JButton deleteSelectedButton;
	private JButton bgColor;
	private JButton plainColor;
	private JButton caretColor;
	private JButton infoColor;
	private JButton warningColor;
	private JButton errorColor;
	private JCheckBox showWelcomeMessage;
	private JTextField limit ;
	private JTextField limitHistory ;
	private JComboBox ansiBehaviour;
	private JComboBox ansiMode;
	// }}}

	//{{{ GeneralOptionPane constructor
	public GeneralOptionPane()
	{
		super("console.general");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		defaultShell = new JComboBox();
		reloadDefaultList();
		addComponent(jEdit.getProperty("options.console.general.defaultshell", "Default Shell:"),
			defaultShell);

		StringList userShells = StringList.split(jEdit.getProperty("console.userShells", ""), "[,]");
		StringList activeShells = new StringList();
		StringList inactiveShells = StringList.split(jEdit.getProperty("console.userShells.optOut", ""), "[,]");
		for ( String name : userShells ) 
		{
			if (!inactiveShells.contains(name))
			{
				activeShells.add(name);
			}
		}
		pingPongList = new PingPongList<String>( inactiveShells, activeShells );
		pingPongList.setLeftTitle( "Available User Shells" );
		pingPongList.setRightTitle( "Active User Shells" );
		pingPongList.setLeftTooltip( "Drag to the right to make active" );
		pingPongList.setRightTooltip( "Drag to the left to make inactive" );
		pingPongList.addLeftListSelectionListener(new LeftListSelectionListener());
		pingPongList.addRightListSelectionListener(new RightListSelectionListener());
		deleteSelectedButton = new JButton( "Delete Selected");	
		deleteSelectedButton.setEnabled(true);
		pingPongList.addButton( deleteSelectedButton );
		deleteSelectedButton.addActionListener( new ActionListener(){

				public void actionPerformed( ActionEvent ae )
				{
					List<String> names = pingPongList.getRightSelectedValues();
					StringBuilder sb = new StringBuilder(128);
					sb.append("Delete these shells?").append('\n');
					for (String name : names) 
					{
						sb.append(name).append('\n');	
					}
					int answer = JOptionPane.showConfirmDialog(jEdit.getActiveView(), sb.toString(), "Confirm Shell Delete", JOptionPane.WARNING_MESSAGE );
					if (answer == JOptionPane.YES_OPTION) {
						for (String name : names)
						{
							ServiceManager.unregisterService(Shell.SERVICE, name);
						}
						reloadLists();
						reloadDefaultList();
					}
				}
			}
		);
		addComponent( pingPongList, GridBagConstraints.BOTH );
		
		showWelcomeMessage = new JCheckBox();
		showWelcomeMessage.setText(jEdit.getProperty("options.console.general.welcome"));
		showWelcomeMessage.setSelected(jEdit.getBooleanProperty("console.shell.info.toggle"));
		addComponent(showWelcomeMessage);

		font = new FontSelector(jEdit.getFontProperty("console.font"));
		addComponent(jEdit.getProperty("options.console.general.font"), font);

		String[] encodings = MiscUtilities.getEncodings(true);
		// Arrays.sort(encodings,new MiscUtilities.StringICaseCompare());
		Arrays.sort(encodings, new StringCompare<String>(true));
		encoding = new JComboBox(encodings);
		encoding.setEditable(true);
		encoding.setSelectedItem(jEdit.getProperty("console.encoding"));
		addComponent(jEdit.getProperty("options.console.general.encoding"),
			encoding);


		Label limitLabel = new Label("options.console.general.charlimit");
		limit = new JTextField(jEdit.getProperty("console.outputLimit"));
		addComponent(limitLabel, limit);

		Label limitHistoryLabel = new Label("options.console.general.historylimit");
		limitHistory = new JTextField(jEdit.getProperty("console.historyLimit",
			String.valueOf(HistoryModel.getDefaultMax())));
		addComponent(limitHistoryLabel, limitHistory);



		addComponent(jEdit.getProperty("options.console.general.bgColor"),
			bgColor = createColorButton("console.bgColor"));
		bgColor.setToolTipText(jEdit.getProperty("options.console.general.bgColor.tooltip"));
		addComponent(jEdit.getProperty("options.console.general.plainColor"),
			plainColor = createColorButton("console.plainColor"));
		plainColor.setToolTipText(jEdit.getProperty("options.console.general.bgColor.tooltip"));
		addComponent(jEdit.getProperty("options.console.general.caretColor"),
			caretColor = createColorButton("console.caretColor"));
		caretColor.setToolTipText(jEdit.getProperty("options.console.general.bgColor.tooltip"));
		addComponent(jEdit.getProperty("options.console.general.infoColor"),
			infoColor = createColorButton("console.infoColor"));
		addComponent(jEdit.getProperty("options.console.general.warningColor"),
			warningColor = createColorButton("console.warningColor"));
		addComponent(jEdit.getProperty("options.console.general.errorColor"),
			errorColor = createColorButton("console.errorColor"));
		
		addComponent( Box.createVerticalStrut(15) );
		
		JPanel ansiEscape = new JPanel();
		ansiEscape.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				jEdit.getProperty("options.ansi-escape.title")
			)
		);
		ansiEscape.setLayout(new BoxLayout(ansiEscape, BoxLayout.Y_AXIS) );
			
			ArrayList<String> al = new ArrayList<String>(
				StringList.split( jEdit.getProperty("options.ansi-escape.behaviour-names"), ";")
			);
			
			ansiBehaviour = new JComboBox( al.toArray() );
			int index = jEdit.getIntegerProperty("ansi-escape.behaviour", 2);
			index = Math.min(
						Math.max( 0, index),
						al.size()-1
			);
			ansiBehaviour.setSelectedIndex(index);
			
			al.clear();
			al.addAll(
				StringList.split( jEdit.getProperty("options.ansi-escape.mode-names"), ";")
			);
			
			ansiMode = new JComboBox( al.toArray() );
			index = Math.min(
						Math.max( 0, jEdit.getIntegerProperty("ansi-escape.mode", 0) ),
						al.size()-1
			);
			ansiMode.setSelectedIndex(index);
			
			JPanel UpperPanel = new JPanel();
			UpperPanel.setLayout(new GridLayout(2, 2, 2, 2) );
			
				UpperPanel.add(new JLabel( jEdit.getProperty("options.ansi-escape.behaviour-caption") ));
				UpperPanel.add( ansiBehaviour );
				UpperPanel.add(new JLabel( jEdit.getProperty("options.ansi-escape.mode-caption") ));
				UpperPanel.add( ansiMode );
				
			ansiEscape.add( UpperPanel );
			
			ansiEscape.add( Box.createVerticalStrut(15) );
			
		addComponent( ansiEscape );
		
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		jEdit.setBooleanProperty("console.shell.info.toggle", showWelcomeMessage.isSelected());

		jEdit.setFontProperty("console.font",font.getFont());

		String limitstr = limit.getText();
		if(limitstr != null && limitstr.length() > 0)
			jEdit.setProperty("console.outputLimit", limitstr);
		else
			jEdit.unsetProperty("console.outputLimit");

		String limithist = limitHistory.getText();
		if(limithist != null && limithist.length() > 0
			&& !String.valueOf(HistoryModel.getDefaultMax()).equals(limithist))
			jEdit.setProperty("console.historyLimit", limithist);
		else
			jEdit.unsetProperty("console.historyLimit");

 		jEdit.setProperty("console.encoding",
 			(String)encoding.getSelectedItem());

 		jEdit.setProperty("console.shell.default",
 			(String)defaultShell.getSelectedItem());

		jEdit.setColorProperty("console.bgColor",
			bgColor.getBackground());
		jEdit.setColorProperty("console.plainColor",
			plainColor.getBackground());
		jEdit.setColorProperty("console.caretColor",
			caretColor.getBackground());
		jEdit.setColorProperty("console.infoColor",
			infoColor.getBackground());
		jEdit.setColorProperty("console.warningColor",
			warningColor.getBackground());
		jEdit.setColorProperty("console.errorColor",
			errorColor.getBackground());

 		jEdit.setProperty("ansi-escape.behaviour", String.valueOf( ansiBehaviour.getSelectedIndex() ) );
 		jEdit.setProperty("ansi-escape.mode"     , String.valueOf( ansiMode.getSelectedIndex() ) );
 		
 		// register/unregister shells with ServiceManager
 		Iterator<String> iter = pingPongList.getLeftDataIterator();
 		StringBuilder sb = new StringBuilder();
 		while(iter.hasNext())
 		{
 			String name = iter.next();
 			ServiceManager.unregisterService(Shell.SERVICE, name);
 			sb.append(name).append(',');
 		}
 		jEdit.unsetProperty("console.userShells.optOut");
 		jEdit.setProperty("console.userShells.optOut", sb.toString());
 		
 		sb = new StringBuilder();
 		iter = pingPongList.getRightDataIterator();
 		while(iter.hasNext())
 		{
 			String name = iter.next();
 			String code = jEdit.getProperty("console.userShells." + name + ".code");
 			ServiceManager.registerService(Shell.SERVICE, name, code, null);
 			sb.append(name).append(',');
 		}
 		jEdit.unsetProperty("console.userShells");
 		jEdit.setProperty("console.userShells", sb.toString());
	}
	//}}}

	//{{{ createColorButton() method
	private JButton createColorButton(String property)
	{
		final JButton b = new JButton(" ");
		b.setBackground(jEdit.getColorProperty(property));
		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Color c = JColorChooser.showDialog(
					GeneralOptionPane.this,
					jEdit.getProperty("colorChooser.title"),
					b.getBackground());
				if(c != null)
					b.setBackground(c);
			}
		});

		b.setRequestFocusEnabled(false);
		return b;
	} //}}}
	
	//{{{ reloadLists() method
	private void reloadLists()
	{
		// load the ping pong lists
		StringList allShells = new StringList(ServiceManager.getServiceNames(Shell.SERVICE));
		StringList availableShells = new StringList();
		StringList selectedShells = new StringList(Shell.getShellNames());
		for ( String name : allShells ) 
		{
			if (!selectedShells.contains(name))
			{
				availableShells.add(name);
			}
		}
		pingPongList.setLeftData( availableShells );
		pingPongList.setRightData( selectedShells );
	} //}}}
	
	// reload the default shell combo box
	private void reloadDefaultList() 
	{
		StringList sl = new StringList(Shell.getShellNames());
		int idx = sl.indexOf("System");
		if (idx != 0) {
			String other = sl.get(0);
			sl.set(idx, other);
			sl.set(0, "System");
		}
		sl.add(jEdit.getProperty("options.last-selected"));
		
		defaultShell.setModel( new DefaultComboBoxModel<String>( sl.toArray() ) );
		String ds = jEdit.getProperty("console.shell.default", "System");
		defaultShell.setSelectedItem(ds);
	}
	
	class LeftListSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e) 
		{
			HashSet<String> names = new HashSet<String>(pingPongList.getLeftSelectedValues());
			for (String name : names) 
			{
				ServiceManager.unregisterService(Shell.SERVICE, name);	
				String optOut = jEdit.getProperty("console.userShells.optOut", "");
				StringList out = StringList.split(optOut, "[,]");
				out.add(name);
				jEdit.setProperty("console.userShells.optOut", out.join(out, ","));
			}
			reloadDefaultList();
		}
	}
	
	class RightListSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e) 
		{
			HashSet<String> names = new HashSet<String>(pingPongList.getRightSelectedValues());
			for (String name : names)
			{
				String code = jEdit.getProperty("console.userShells." + name + ".code");
				ServiceManager.registerService(Shell.SERVICE, name, code, null);
				String optOut = jEdit.getProperty("console.userShells.optOut", "");
				StringList out = StringList.split(optOut, "[,]");
				out.remove(name);
				jEdit.setProperty("console.userShells.optOut", out.join(out, ","));
 			}
 			reloadDefaultList();
		}
	}
	
}
