/*
* TaskListGeneralOptionPane.java - TaskList plugin
* Copyright (C) 2001,2002 Oliver Rutherfurd
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
*
* $Id$
*/

package tasklist.options;

//{{{ imports
import java.awt.event.*;
import java.awt.Color;
import javax.swing.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.SyntaxUtilities;
import tasklist.*;
//}}}

public class TaskListGeneralOptionPane extends AbstractOptionPane {
	public TaskListGeneralOptionPane() {
		super( "tasklist.general" );
	}

	//{{{ _init() method
	protected void _init() {
		setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );

		addComponent( jEdit.getProperty( "options.tasklist.general.buffer.display" ),
		        bufferDisplay = new JComboBox(
		                    new String[]
		                    {
		                        jEdit.getProperty( "options.tasklist.general.buffer.display.fullpath" ),
		                        jEdit.getProperty( "options.tasklist.general.buffer.display.namedir" ),
		                        jEdit.getProperty( "options.tasklist.general.buffer.display.nameonly" )
		                    } ) );

		addComponent( Box.createVerticalStrut( 3 ) );

		addComponent( jEdit.getProperty( "options.tasklist.general.sort.choice" ),
		        sortCriteria = new JComboBox(
		                    new String[]
		                    {
		                        jEdit.getProperty( "options.tasklist.general.sort.choice.0" ),
		                        jEdit.getProperty( "options.tasklist.general.sort.choice.1" ),
		                    } ) );
		// need the Math.min here because I removed one of the choices and it's possible for a user
		// to have a previous value of 2, which would now give an out of bounds exception.
		sortCriteria.setSelectedIndex( Math.min(jEdit.getIntegerProperty( "tasklist.table.sort-column", 1 ) - 1, 1) );

		addComponent( Box.createVerticalStrut( 3 ) );

		addComponent( jEdit.getProperty( "options.tasklist.general.sort.direction" ),
		        sortDirection = new JComboBox(
		                    new String[]
		                    {
		                        jEdit.getProperty( "options.tasklist.general.sort.direction.0" ),
		                        jEdit.getProperty( "options.tasklist.general.sort.direction.1" ),
		                    } ) );
		sortDirection.setSelectedIndex(
		    jEdit.getBooleanProperty( "tasklist.table.sort-ascending", true ) ? 0 : 1 );

		addComponent( Box.createVerticalStrut( 3 ) );

		addComponent( showOpenFiles = new JCheckBox(
		            jEdit.getProperty( "options.tasklist.general.show-open-files" ),
		            jEdit.getBooleanProperty( "tasklist.show-open-files", true )
		        ) );

		if ( PVHelper.isProjectViewerAvailable() ) {
			addComponent( Box.createVerticalStrut( 3 ) );

			addComponent( showProjectFiles = new JCheckBox(
			            jEdit.getProperty( "options.tasklist.general.show-project-files" ),
			            jEdit.getBooleanProperty( "tasklist.show-project-files", true )
			        ) );
		}

		addComponent( Box.createVerticalStrut( 3 ) );

		addComponent( allowSingleClick = new JCheckBox(
		            jEdit.getProperty( "options.tasklist.single-click-selection" ),
		            jEdit.getBooleanProperty( "tasklist.single-click-selection", false ) ) );

		addComponent( Box.createVerticalStrut( 3 ) );


		// default to false, until we get it working well
		addComponent( highlightTasks = new JCheckBox(
		            jEdit.getProperty( "options.tasklist.general.highlight.tasks" ),
		            jEdit.getBooleanProperty( "tasklist.highlight.tasks", false ) ) );

		addComponent( Box.createVerticalStrut( 3 ) );

		addComponent(
		    jEdit.getProperty( "options.tasklist.general.highlight.color" ),
		    highlightColor = createColorButton( "tasklist.highlight.color" ) );

		// toggle whether the color button is enabled
		highlightTasks.addActionListener( new HighlightColorHandler() );
		highlightColor.setEnabled( highlightTasks.isSelected() );

		// set current value (default to name (dir))
		String _bufferDisplay = jEdit.getProperty( "tasklist.buffer.display" );
		if ( _bufferDisplay == null || _bufferDisplay.equals( "" ) ) {
			bufferDisplay.setSelectedItem( jEdit.getProperty(
			            "options.tasklist.general.buffer.display.namedir" ) );
		}
		else {
			bufferDisplay.setSelectedItem( _bufferDisplay );
		}
	} //}}}

	//{{{ _save() method
	public void _save() {
		jEdit.setProperty( "tasklist.buffer.display",
		        bufferDisplay.getSelectedItem().toString() );

		jEdit.setProperty( "tasklist.highlight.color",
		        SyntaxUtilities.getColorHexString( highlightColor.getBackground() ) );

		jEdit.setBooleanProperty( "tasklist.show-open-files",
		        showOpenFiles.isSelected() );

		jEdit.setBooleanProperty( "tasklist.show-project-files",
		        showProjectFiles.isSelected() );

		jEdit.setBooleanProperty( "tasklist.single-click-selection",
		        allowSingleClick.isSelected() );

		jEdit.setBooleanProperty( "tasklist.highlight.tasks",
		        highlightTasks.isSelected() );

		jEdit.setIntegerProperty( "tasklist.table.sort-column",
		        sortCriteria.getSelectedIndex() + 1 );

		jEdit.setBooleanProperty( "tasklist.table.sort-ascending",
		        ( sortDirection.getSelectedIndex() == 0 ) );
	} //}}}

	//{{{ createColorButton() method
	private JButton createColorButton( String property ) {
		JButton b = new JButton( " " );
		b.setBackground( GUIUtilities.parseColor( jEdit.getProperty( property ) ) );
		b.addActionListener( new ColorButtonHandler() );
		b.setRequestFocusEnabled( false );
		return b;
	} //}}}

	//{{{ ColorButtonHandler class
	private class ColorButtonHandler implements ActionListener {
		public void actionPerformed( ActionEvent evt ) {
			JButton button = ( JButton ) evt.getSource();
			Color c = JColorChooser.showDialog( TaskListGeneralOptionPane.this,
			        jEdit.getProperty( "colorChooser.title" ),
			        button.getBackground()
			                                  );
			if ( c != null ) {
				button.setBackground( c );
			}
		}
	} //}}}

	//{{{ HighlightColorHandler class
	private class HighlightColorHandler implements ActionListener {
		public void actionPerformed( ActionEvent evt ) {
			TaskListGeneralOptionPane.this.highlightColor.setEnabled(
			    TaskListGeneralOptionPane.this.highlightTasks.isSelected() );
		}
	} //}}}

	//{{{ private members
	private JComboBox bufferDisplay;
	private JComboBox sortCriteria;
	private JComboBox sortDirection;
	private JCheckBox showOpenFiles;
	private JCheckBox showProjectFiles;
	private JCheckBox allowSingleClick;
	private JCheckBox highlightTasks;
	private JButton highlightColor;
	//}}}
}

// :collapseFolds=1:folding=explicit:indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4: