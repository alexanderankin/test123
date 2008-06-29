
/*
Copyright (C) 2008 Matthew Gilbert

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

package commentcontinuator;

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.buffer.JEditBuffer;

public class CommentContinuatorPlugin extends EBPlugin
{
    protected CommentContinuator continuator;
    
    public void handleMessage(EBMessage msg)
    {
        if (msg instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate)msg;
            View view = bu.getView();
            if (view == null)
                return;
        } else if (msg instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate)msg;
            EditPane edit_pane = epu.getEditPane();
            Buffer buffer = edit_pane.getBuffer();
            if ((epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED) ||
                (epu.getWhat() == EditPaneUpdate.CREATED))
            {
                if (buffer != null) {
                    if (!buffer.getBooleanProperty("commentcontinuator.enabled")) {
                        buffer.setBooleanProperty("commentcontinuator.enabled", true);
                        buffer.addBufferListener(continuator);
                    }
                }
            }
        }
    }
    
    public void start()
    {
        // Enumerate edit panes and add listener
        continuator = new CommentContinuator();
        for (View v : jEdit.getViews()) {
            JEditBuffer buffer = v.getEditPane().getBuffer();
            buffer.setBooleanProperty("commentcontinuator.enabled", true);
            buffer.addBufferListener(continuator);
        }
    }
    
    public void stop()
    {
        for (View v : jEdit.getViews()) {
            JEditBuffer buffer = v.getEditPane().getBuffer();
            buffer.setBooleanProperty("commentcontinuator.enabled", false);
            buffer.removeBufferListener(continuator);
        }
    }
}
