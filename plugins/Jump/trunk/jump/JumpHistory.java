/*
 *  Jump plugin for jEdit
 *  Copyright (c) 2003 Pavlikus
 *
 *  :tabSize=4:indentSize=4:
 *  :folding=explicit:collapseFolds=1:
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

    //{{{ imports
package jump;
    import java.util.Stack;

import jump.ctags.CTAGS_Entry;


public class JumpHistory
{
    private Stack history;

    //{{{ JumpHistory()
    public JumpHistory()
    {
        history = new Stack();
    } //}}}

    //{{{ add(CTAGS_Entry e)
    public void add(CTAGS_Entry e)
    {
        history.push(e);
    } //}}}

    //{{{ getPrevious()
    public CTAGS_Entry getPrevious()
    {
       if (history.empty())
       {
           return null;
       }
       return (CTAGS_Entry)history.pop();
    } //}}}

    //{{{ clear()
    public void clear()
    {
        history.clear();
        return;
    } //}}}
}
