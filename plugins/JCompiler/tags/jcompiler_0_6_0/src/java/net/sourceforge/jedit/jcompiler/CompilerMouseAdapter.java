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


import java.util.*;
import java.awt.event.*;

import org.gjt.sp.jedit.*;

//build support
import net.sourceforge.jedit.buildtools.*;
import net.sourceforge.jedit.buildtools.msg.*;

public class CompilerMouseAdapter extends MouseAdapter {

    private JCompiler compiler = null;

    public CompilerMouseAdapter(JCompiler compiler) {
        this.compiler = compiler;
    }
    
    public void mouseClicked(MouseEvent e) {


        if (e.getClickCount() == 2) {

            
            int index = this.compiler.getErrorList().locationToIndex(e.getPoint());

            Vector vec = this.compiler.getModel();
            
            try {

                //if the location that they clicked on was larger than
                //the registered number of lines

                String err;
                if ( index < vec.size() && index != -1) {
                    err = (String) vec.elementAt(index);
                } else {
                    return;
                }

                BuildMessage message = BuildMessage.getBuildMessage( err );


                //only continue if this is a supported BuildMessage type
                if ( message.getType() == BuildMessage.TYPE_UNKNOWN ) {
                    return;
                }

                if ( message.getType() == BuildMessage.TYPE_EXCEPTION ) {
                    
                    //FIX ME
                    //this.getParent().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                    //type to decompile this class...
                    EditBus.send( new DecompileClassMessage( null, message.getTarget(), message ) );

                    return;
                }
                
                
                this.compiler.setEditorFile( message.getTarget(), message.getLineNumber() );


            } catch (Exception exp) { 
                exp.printStackTrace();
            }
            
        }
    }

}
