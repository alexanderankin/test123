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

package net.sourceforge.jedit.javainsight;

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


import net.sourceforge.jedit.buildtools.msg.*;

import net.sourceforge.jedit.pluginholder.*;

public class JavaInsightPlugin extends EBPlugin {
    
    
    /**
    Start the plugin

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void start() {

        PluginHolder.registerPlugin( "net.sourceforge.jedit.javainsight.JavaInsight", "javainsight.open");

        //parse out the resources as a thread so that when the plugin is 
        //requested there is nothing to do.
        new ThreadedParser().start();
        

    }

    /**
    Handle message for decompile requests..

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public void handleMessage(EBMessage message) {

        if (message instanceof DecompileClassMessage) {

            DecompileClassMessage decompile = (DecompileClassMessage)message;
            
            decompile.setFileName( JavaInsight.decompileClass( decompile.getClassName() , true ) );
            
            System.out.println( "Decompiling the class: " + decompile.getClassName() );
        }
    }


}

