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
import org.gjt.sp.jedit.textarea.*;


//java stuff
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.text.Element;
import java.lang.*;


//build support
import net.sourceforge.jedit.buildtools.*;
import net.sourceforge.jedit.buildtools.msg.*;

/**
@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
public class MouseHandler extends MouseAdapter implements ActionListener {

    JMenuItem stackTrace = null;
    JMenuItem one = null;
    JMenuItem two = null;
    JMenuItem three = null;

    JPopupMenu menu = null;

    private JCompiler compiler = null;
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public MouseHandler(JCompiler compiler) {
        this.compiler = compiler;
    }
    
    
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void mousePressed(MouseEvent evt) {

        if((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
            showFullMenu( evt.getX(), evt.getY() );

    }


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    private void showFullMenu(int x, int y)	{

        this.stackTrace = new JMenuItem("Load stack trace...");
        this.one = new JMenuItem( jEdit.getProperty( "jcompiler.label" ) );
        this.two = new JMenuItem( jEdit.getProperty( "jpkgcompiler.label") );
        this.three = new JMenuItem( jEdit.getProperty( "jpkgrebuild.label" ) );


        this.menu = new JPopupMenu();




        stackTrace.addActionListener(this);
        one.addActionListener(this);
        two.addActionListener(this);
        three.addActionListener(this);            
        
        menu.add(one);
        menu.add(two);
        menu.add(three);

        menu.addSeparator();

        menu.add(stackTrace);
        menu.show( this.compiler.getErrorList(), x, y);
        
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void actionPerformed(ActionEvent e) {
        


       if ( e.getSource() == this.stackTrace ) {

            new StackTraceDialog( this.compiler.getView() );

        } else if ( e.getSource() == this.one) {
            
            //FIX ME
            //new JCompiler( NoExitSecurityManager.getNoExitSM(), "jcompiler", false).actionPerformed(e);

        } else if ( e.getSource() == this.two) {

            //FIX ME
            //new JCompiler(NoExitSecurityManager.getNoExitSM(), "jpkgcompiler", true).actionPerformed(e);
            
        } else if ( e.getSource() == this.three) {

            //FIX ME
            //new JCompiler(NoExitSecurityManager.getNoExitSM(), "jpkgrebuild", true, true).actionPerformed(e);
            
        }
    }

}
