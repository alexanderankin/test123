/*
 * TidyBuffer.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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
 */


package jtidy;


import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;


public class TidyBuffer
{
    public static void tidyBuffer(View view) {

        Buffer buffer = view.getBuffer();
        Mode mode     = buffer.getMode();

        // Setting tidy input
        JEditTextArea textArea = view.getTextArea();
        String text = textArea.getText();

        // Setting tidy standard output
        boolean noOutput = jEdit.getBooleanProperty("jtidy.no-output", false);
        boolean writeBack = jEdit.getBooleanProperty("jtidy.write-back", false);

        // Tidy
        JTidyBeautifier beautifier = new JTidyBeautifier();
        text = beautifier.beautify(text);
        
        if (noOutput) {
            // No output: simply returns
            return;
        }

        if (writeBack) {
            // Write back: we rewrite the content of the current buffer
            TidyBuffer.setBufferText(buffer, text);
            return;
        }

        // Create a new buffer and put the tidied content
        Buffer newBuffer = jEdit.newFile(view);

        if (newBuffer == null) {
            new WaitForBuffer(mode, text);
        } else {
            newBuffer.setMode(mode);
            TidyBuffer.setBufferText(newBuffer, text);
        }

    }


    private static void setBufferText(Buffer buffer, String text) {
        try {
            buffer.beginCompoundEdit();
            buffer.remove(0, buffer.getLength());
            buffer.insert(0, text);
        } finally {
            buffer.endCompoundEdit();
        }
    }


    private static class WaitForBuffer implements EBComponent {
        private Mode   mode;
        private String text;


        public WaitForBuffer(Mode mode, String text) {
            this.mode = mode;
            this.text = text;

            EditBus.addToBus(this);
        }


        public void handleMessage(EBMessage message) {
            if (message instanceof BufferUpdate) {
                BufferUpdate bu = (BufferUpdate) message;
                if (bu.getWhat() == BufferUpdate.CREATED) {
                    EditBus.removeFromBus(this);

                    Buffer buffer = bu.getBuffer();
                    Log.log(Log.DEBUG, this, "**** Buffer CREATED new file? [" + buffer.isNewFile() + "]");
                    Log.log(Log.DEBUG, this, "**** Buffer CREATED length:   [" + buffer.getLength() + "]");
                    buffer.setMode(this.mode);
                    TidyBuffer.setBufferText(buffer, this.text);
                }
            }
        }
    }
}

