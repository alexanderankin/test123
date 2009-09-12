
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
import java.lang.StringBuffer;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.gui.KeyEventTranslator;

class VimageOperation
{
    public int count;
    public String exec;
    public String mode;
    // Save the following for playback
    KeyEventTranslator.Key key;
    public String overlay_mode;

    public VimageOperation(String mode_) 
    {
        count = 0;
        exec = null;
        mode = mode_;
        key = null;
        overlay_mode = null;
    }

    public VimageOperation(VimageOperation other)
    {
        count = other.count;
        exec = other.exec;
        mode = other.mode;
        if (other.key == null) {
            Log.log(Log.ERROR, this, "other has no key!");
            Exception ex = new Exception("other has no key");
            ex.printStackTrace();
        }
        key = other.key;
        overlay_mode = other.overlay_mode;
    }
    
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append("[").append(count).append(", ").append(exec).append(", ");
        b.append(key).append(", ").append(mode).append(", ").append(overlay_mode).append("]");
        return b.toString();
    }
}

