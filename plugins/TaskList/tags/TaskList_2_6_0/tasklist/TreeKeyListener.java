/*
* Copyright (C) 2009, Dale Anson
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
*/

package tasklist;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;


// key listener for the tree so typing "Enter" on a tree node shows the 
// corresponding line in the edit pane
public class TreeKeyListener extends KeyAdapter {

    private View view = null;
    private JTree tree = null;

    public TreeKeyListener( View view, JTree tree ) {
        this.view = view;
        this.tree = tree;
    }
    
    public void keyPressed( KeyEvent ke ) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            ke.consume();
            TreePath path = tree.getSelectionPath();
            Task task = null;
            if ( path.getPathCount() > 2 ) {
                task = ( Task ) ( ( DefaultMutableTreeNode ) path.getLastPathComponent() ).getUserObject();
                Buffer buffer = jEdit.getBuffer( task.getBufferPath() );
                if ( buffer == null ) {
                    buffer = jEdit.openFile( view, task.getBufferPath() );
                }

                int line_number = task.getLineNumber();
                int start_offset = task.getStartOffset();
                try {
                    EditPane edit_pane = jEdit.getActiveView().showBuffer( buffer );
                    edit_pane.getTextArea().scrollTo( line_number, start_offset, true );
                    edit_pane.getTextArea().setCaretPosition( task.getStartPosition().getOffset() );
                }
                catch(Exception e) {
                    // occasionally, there is an ArrayIndexOutOfBounds exception thrown from
                    // the 'scrollTo' line above, so this try/catch is to help track down the
                    // problem.
                    System.err.println("Error scrolling to line " + line_number + ", offset " + start_offset + " in file " + task.getBufferPath());
                    e.printStackTrace();
                }
            }
        }
    }
}