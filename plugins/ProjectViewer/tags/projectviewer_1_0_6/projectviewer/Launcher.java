/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

import java.util.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import projectviewer.config.ProjectViewerConfig;

/**
 * Provides a neutral way to launch projects and files.
 *
 * @author	<A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 * @version   $Revision$
 */
public final class Launcher {

   private final static ProjectViewerConfig config = ProjectViewerConfig.getInstance();

   private View view = null;
   private ProjectViewer viewer = null;

   /**
	* Create a new <code>Launcher</code>.
	*
	* @param view	Description of Parameter
	* @param viewer  Description of Parameter
	* @parameter	 view		 The view that Launcher should open files with.
	* @parameter	 viewer	   An instance of ProjectViewer
	* @author		<A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
	* @version	   $Id$
	*/
   public Launcher( View view, ProjectViewer viewer ) {
	  this.view = view;
	  this.viewer = viewer;
   }

   /**
	* Takes a file and opens it up in jEdit.
	*
	* @param file  Description of Parameter
	* @author	  <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
	*/
   public void launchFile( ProjectFile file ) {
	  if ( this.view != null ) {
		 Buffer buffer = jEdit.openFile( this.view, null, file.getPath(), false, null );
		 showFile( file );
	  }
   }

   /**
	* Takes a given file and highlights it in the current view.
	*
	* @param file  Description of Parameter
	* @author	  <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
	* @version	 $Id$
	*/
   public void showFile( ProjectFile file ) {
	  Buffer buffer = file.getBuffer();
	  if ( buffer != null ) {
		 view.setBuffer( buffer );
	  }
   }

   /**
	* Close the specified project file.
	*
	* @param file  Description of Parameter
	*/
   public void closeFile( ProjectFile file ) {
	  if ( this.view == null )
		 return;
	  jEdit.closeBuffer( view, file.getBuffer() );
   }

   /**
	* Close all project resources.
	*
	* @param project  Description of Parameter
	*/
   public void closeProject( Project project ) {
		if ( project == null ) return;
		if ( viewer.getCurrentProject() == null ) return;

		project.clearOpenFiles();
		if ( config.getCloseFiles() || config.getRememberOpen() ) {
			Buffer[] bufs = jEdit.getBuffers();
			if ( bufs == null ) return;
			Buffer lastBuffer = view.getBuffer();
			for ( int i = 0; i < bufs.length; i++ ) {
				if ( project.isProjectFile( bufs[i].getPath() ) ) {
					if ( config.getCloseFiles() ) {
						jEdit.closeBuffer( view, bufs[i] );
					}
					if ( config.getRememberOpen() ) {
						if (bufs[i] == lastBuffer) {
							project.setLastFile(bufs[i].getPath());
						} else {
							project.addOpenFile( bufs[i].getPath() );
						}
					}
				}
			}
	  	}
	}

   /**
	* Opens the files remembered by the provided project.
	*
	* @param project  Description of Parameter
	*/
	public void openProject( Project project ) {
		if ( view == null || project == null )
			return;

		// open files previously open in the provided project. The
		// files listed in the iterator are in order, the last file
		// in the iterator is the last file open in the view.
		Iterator it = project.getOpenFiles();
		while ( it.hasNext() ) {
			jEdit.openFile( view, (String)it.next() );
		}
	}

}

