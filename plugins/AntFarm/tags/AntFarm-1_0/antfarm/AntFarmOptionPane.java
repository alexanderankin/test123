/*
 *  AntFarmOptionPane.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 Brian Knowles
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package antfarm;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class AntFarmOptionPane
	 extends AbstractOptionPane
	 implements ActionListener
{

	private JTextField _command;
	private JRadioButton _useSameJvm;
	private JRadioButton _useExternalScript;
	private JCheckBox _useProjectViewerIntegration;
	private JCheckBox _useEmacsOutput;


	public AntFarmOptionPane()
	{
		super( AntFarmPlugin.NAME );
	}


	static String promptForAntScript( Component component )
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle( jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "location" ) );
		int result = chooser.showDialog(
			component,
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "file-dialog-approve" )
			 );
		if ( result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null ) {
			return chooser.getSelectedFile().toString();
		}
		return "";
	}


	public void _init()
	{
		_useSameJvm = new JRadioButton(
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "use-same-jvm-button" )
			 );
		_useExternalScript = new JRadioButton(
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "use-external-script-button" )
			 );
		_useSameJvm.addActionListener(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
					_command.setEnabled( false );
				}
			} );
		_useExternalScript.addActionListener(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
					_command.setEnabled( true );
				}
			} );
		_useProjectViewerIntegration = new JCheckBox(
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "use-project-bridge-label" )
			 );
		_useProjectViewerIntegration.setSelected(
			jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "use-project-bridge" )
			 );
		_useEmacsOutput = new JCheckBox(
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "output-emacs-label" )
			 );
		_useEmacsOutput.setSelected(
			jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "output-emacs" )
			 );

		boolean useSameJvmSelected = jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "use-same-jvm" );
		if ( useSameJvmSelected )
			_useSameJvm.setSelected( true );
		else
			_useExternalScript.setSelected( true );

		ButtonGroup group = new ButtonGroup();
		group.add( _useSameJvm );
		group.add( _useExternalScript );

		_command = new JTextField(
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "command" ),
			40
			 );
		_command.setEnabled( !useSameJvmSelected );

		JButton pickPath = new JButton( jEdit.getProperty(
			AntFarmPlugin.OPTION_PREFIX + "choose-antpath" ) );
		pickPath.addActionListener( this );

		JPanel pathPanel = new JPanel();
		pathPanel.add( _command );
		pathPanel.add( pickPath );

		addComponent( _useSameJvm );
		addComponent( _useExternalScript );
		addComponent(
			new JLabel( jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "location" ) )
			 );
		addComponent( pathPanel );
		addComponent( _useProjectViewerIntegration );
		addComponent( _useEmacsOutput );
	}


	public void _save()
	{
		jEdit.setProperty( AntFarmPlugin.OPTION_PREFIX + "command", _command.getText() );
		jEdit.setBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "use-same-jvm", _useSameJvm.isSelected() );
		jEdit.setBooleanProperty(
			AntFarmPlugin.OPTION_PREFIX + "use-project-bridge", _useProjectViewerIntegration.isSelected()
			 );
		jEdit.setBooleanProperty(
			AntFarmPlugin.OPTION_PREFIX + "output-emacs", _useEmacsOutput.isSelected()
			 );

		jEdit.propertiesChanged();
	}


	public void actionPerformed( ActionEvent e )
	{
		String scriptPath = AntFarmOptionPane.promptForAntScript( this );
		if ( scriptPath != "" )
			_command.setText( scriptPath );
	}


	private boolean isUseInternalJvmSelected()
	{
		return jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "use-same-jvm" );
	}
}

