/*
* TaskList.java - TaskList plugin
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

package tasklist;

//{{{ imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
//}}}

/**
 * A dockable component contaning a scrollable table; the table contains
 * data on task items found by parsing one or more buffers.
 *
 * @author Oliver Rutherfurd
 */
public class TaskList extends JPanel {
	private JTabbedPane tabs = null;

	//{{{ constructor
	/**
	 * Constructor
	 *
	 * @param view The view in which the TaskList component will appear
	 */
	public TaskList( View view ) {
		super( new BorderLayout() );

		tabs = new JTabbedPane();

		tabs.add( "Current File", new CurrentBufferTaskList( view ) );
		tabs.add( "Open Files", new JScrollPane( new OpenBufferTaskList( view ) ) );
		if ( PVHelper.isProjectViewerAvailable() ) {
			tabs.add( "Project Files", new ProjectTaskList(view) );
		}

		add( BorderLayout.CENTER, tabs );
	} //}}}

	//{{{ getName() method
	/**
	 * Property accessor required by jEdit Plugin API
	 * @return The plugin's name property
	 */
	public String getName() {
		return "tasklist";
	} //}}}

	//{{{ getComponent() method
	/**
	 * Property accessor required by jEdit Plugin API
	 * @return A reference to the TaskList object
	 */
	public Component getComponent() {
		return this;
	} //}}}

}

// :collapseFolds=1:folding=explicit:indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4: