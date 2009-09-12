
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

import java.lang.String;
import java.lang.Character;

import java.util.HashMap;
import java.util.Vector;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

import org.gjt.sp.util.Log;

class VimageRecorder
{
    boolean in_playback;
    protected HashMap<Character, Collection<VimageOperation>> record_map;
    protected Vector<VimageOperation> current;
    
    public VimageRecorder()
    {
        in_playback = false;
        record_map = new HashMap<Character, Collection<VimageOperation>>();
        current = new Vector<VimageOperation>();
    }
    
    public Collection<VimageOperation> get(char c)
    {
        if (in_playback)
            return null;
        
        Collection<VimageOperation> col = record_map.get(c);
        if (col == null)
            return col;
        
        if (col.isEmpty())
            return col;
        
        // Last op in collection should be what ended the playback. This is a
        // hack, but seems cleanest here.
        Vector<VimageOperation> vec = new Vector<VimageOperation>();
        Iterator<VimageOperation> iter = col.iterator();
        for (int i = 0; i < (col.size() - 1); ++i) {
            vec.add(iter.next());
        }
        // Make sure iter.next is "nmap" and q.
        VimageOperation last_op = iter.next();
        if (last_op.key.input != 'q' || last_op.mode != "nmap") {
            vec.add(last_op);
        }
        return vec;
    }
    
    public void put(char c, Collection<VimageOperation> ops)
    {
        if (in_playback)
            return;
        record_map.put(c, ops);
    }
    
    public void clear(char c)
    {
        if (in_playback)
            return;
        if (record_map.get(c) != null)
            record_map.get(c).clear();
    }
    
    public void add(char c, VimageOperation op)
    {
        if (in_playback)
            return;
        if (record_map.get(c) == null)
            record_map.put(c, new Vector<VimageOperation>());
        record_map.get(c).add(new VimageOperation(op));
    }
    
    public void setPlayback(boolean in_playback_)
    {
        in_playback = in_playback_;
    }
    
    public boolean inPlayback()
    {
        return in_playback;
    }
}

