
/* 
Copyright (C) 2009 Matthew Gilbert 

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package vimage;

/* Class to hold a single command. This class knows how to trigger execution and
   what order to apply the jEdit snippets. It's also responsible for recording
   commands for macros.
 */

 
import java.lang.Class;
import java.lang.String;
 
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.TextArea;

public class VimageCommand
{
    protected Vector<String> prologue;
    protected Vector<String> epilogue;
    
    protected Vector<String> edit;
    protected Vector<String> movement;
    protected int count;
    
    protected View view;
    
    protected void run_actions(Vector<String> action_names)
    {
        for (String s : action_names) {
            EditAction ea = jEdit.getAction(s);
            if (ea == null) {
                Log.log(Log.ERROR, this, "Could not find action for string \"" + s + "\"");
                continue;
            }
            ea.invoke(view);
        }
    }
    
    public VimageCommand(View view_)
    {
        prologue = new Vector<String>();
        epilogue = new Vector<String>();
        edit = new Vector<String>();
        movement = new Vector<String>();
        count = 0;
        view = view_;
    }
    
    public void addEdit(String edit_)
    {
        String[] edit = {edit_};
        addEdit(edit);
    }
    public void addEdit(String[] edits_)
    {
        edit.addAll(java.util.Arrays.asList(edits_));
        eval();
    }
    
    public void addMovement(String movement_)
    {
        String[] movement = {movement_};
        addMovement(movement);
    }
    public void addMovement(String[] movements_)
    {
        movement.addAll(java.util.Arrays.asList(movements_));
        eval();
    }
    
    public void setCount(int count_)
    {
        count = count_;
    }
    
    public void eval()
    {
        if (movement.isEmpty())
            return;
        
        run_actions(prologue);
        for (int i = 0; i < count; ++i)
            run_actions(movement);
        run_actions(edit);
        run_actions(epilogue);
    }
}
