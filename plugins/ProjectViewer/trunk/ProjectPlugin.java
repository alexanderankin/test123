/*
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



//the standard swing stuff
import javax.swing.*;
import javax.swing.tree.*;

//awt stuff for swing support
import java.awt.*;


import java.awt.event.*;  // required for KeyListener and ActionListener
import javax.swing.event.*;


//required for jEdit use
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;

import java.net.*;
import java.util.Vector;

//used so that Project Viewer can be swallowed by jEdit
//import org.yi.relativity.plugholder.*;

import projectviewer.*;

import pluginholder.*;

/**

A Project Viewer plugin for jEdit.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
*/

public class ProjectPlugin extends EditPlugin {
    
    //extends HoldablePlugin


    /**
    Stop the plugin and save the project resources.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */    
    public void stop() {
        ProjectResources.save();
    }

    /**
    Start the plugin

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void start() {

        PluginHolder.registerPlugin( "projectviewer.ProjectViewer", "projectviewer.open" );

        //parse out the resources as a thread so that when the plugin is 
        //requested there is nothing to do.
        new ThreadedParser().start();

    }


}

