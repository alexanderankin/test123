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

package net.sourceforge.jedit.jcompiler;

//jedit support 
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;



//awt
import java.awt.*;
import java.awt.event.*;

//java stuff
import java.io.*;
import java.util.*;
import java.lang.*;

//build support
import net.sourceforge.jedit.buildtools.*;
import net.sourceforge.jedit.buildtools.msg.*;

import net.sourceforge.jedit.pluginholder.*;


/**
@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
public class JCompilerPlugin extends EBPlugin {

    //constants for JCompiler actions
    
    public static final String JCOMPILER_BUILD_FILE_LABEL_KEY 
        = "jcompiler.build.file.label";

    public static final String JCOMPILER_BUILD_PACKAGE_LABEL_KEY 
        = "jcompiler.build.package.label";

    public static final String JCOMPILER_BUILD_EVERYTHING_LABEL_KEY 
        = "jcompiler.build.everything.label";

    

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void start() {
        try {
            NoExitSecurityManager sm = NoExitSecurityManager.getNoExitSM();
            java.lang.System.setSecurityManager(sm);


            
            //register the plugin
            PluginHolderEntry entry = new PluginHolderEntry( "net.sourceforge.jedit.jcompiler.JCompiler", "jcompiler-menu" );
            entry.setType( PluginHolderEntry.TYPE_MENU );
            entry.setRequiredStandalone( true );
            PluginHolder.registerPlugin( entry );

            //FIX ME:  Need to add actions for starting JCompiler
            //jEdit.addAction( new JCompiler(sm, "jcompiler", false) );
            //jEdit.addAction( new JCompiler(sm, "jpkgcompiler", true) );
            //jEdit.addAction( new JCompiler(sm, "jpkgrebuild", true, true) );


            /*            
            HoldablePlugin.addPluginHolderAction( 
                new PluginHolderAction( "net.sourceforge.jedit.jcompiler.JCompiler", "jcompiler.open" ) );
            
            HoldablePlugin.addPluginHolderAction( 
                new PluginHolderAction( "net.sourceforge.jedit.jcompiler.JCompiler", "jpkgcompiler.open" ) );

            HoldablePlugin.addPluginHolderAction( 
                new PluginHolderAction( "net.sourceforge.jedit.jcompiler.JCompiler", "jpkgrebuild.open" ) );
            */
            
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    //FIX ME:  this plugin should not create menu items.
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$

    public void createMenuItems(View view, Vector menus, Vector menuItems) {
        menus.addElement(GUIUtilities.loadMenu(view, "jcompiler-menu"));

    }
    */    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void createOptionPanes(OptionsDialog od) {
        od.addOptionPane( new JCompilerPane()  );
    }
    



}
