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

package javainsight;


import java.util.Vector;

//required for jEdit use
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindow;
import org.gjt.sp.jedit.msg.CreateDockableWindow;

import org.gjt.sp.util.Log;

import buildtools.msg.DecompileClassMessage;


/**
 * Main JavaInsight plugin class
 *
 * @version $Id$
**/
public class JavaInsightPlugin extends EBPlugin
{
    /**
     * The 'name' of the dockable window.
     *
     * @author <A HREF="mailto:akaplan@users.sourceforge.net">Andre Kaplan</A>
    **/
    public static final String DOCKABLE_NAME = "javainsight-dockable";


    /**
     * Start the plugin
     * 
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @author <A HREF="mailto:akaplan@users.sourceforge.net">Andre Kaplan</A>
    **/
    public void start() {
        jEdit.addAction(new JavaInsightDockAction());

        EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST, DOCKABLE_NAME);

        //parse out the resources as a thread so that when the plugin is 
        //requested there is nothing to do.
        new ThreadedParser().start();
    }


    /**
     * Create the menus
     * 
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @author <A HREF="mailto:akaplan@users.sourceforge.net">Andre Kaplan</A>
    **/
    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenuItem("javainsight.toggle-dockable"));
    }


    /**
     * Handle message for decompile requests..
     *
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @author <A HREF="mailto:akaplan@users.sourceforge.net">Andre Kaplan</A>
    **/
    public void handleMessage(EBMessage message) {
        if (message instanceof CreateDockableWindow) {
            CreateDockableWindow cmsg = (CreateDockableWindow) message;
            if (cmsg.getDockableWindowName().equals(DOCKABLE_NAME)) {
                cmsg.setDockableWindow(new JavaInsightDockable(cmsg.getView()));
            }
        } else if (message instanceof DecompileClassMessage) {
            DecompileClassMessage decompile = (DecompileClassMessage)message;
            decompile.setFileName( JavaInsight.decompileClass( decompile.getClassName() , true ) );
            Log.log(Log.MESSAGE, this, "Decompiling the class: " + decompile.getClassName() );
        }
    }
}

