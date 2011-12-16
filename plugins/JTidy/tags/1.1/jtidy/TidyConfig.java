/*
 * TidyConfig.java
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import java.util.Properties;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.msg.BufferUpdate;

import org.gjt.sp.util.Log;


public class TidyConfig
{
    public static void generateTidyConfig(View view) {
        String res = "";

        try {
            Properties props = JTidyPlugin.getProperties();

            ByteArrayOutputStream baOut = new ByteArrayOutputStream();
            OutputStream out = new NewlineOutputFilter(new BufferedOutputStream(baOut));

            props.store(out, "Generated Tidy configuration file");

            out.flush();
            out.close();

            res = new String(baOut.toByteArray());
        } catch (IOException ioe) {
            Log.log(Log.ERROR, TidyConfig.class, ioe);
            return;
        }

        Buffer buffer = jEdit.newFile(view);

        if (buffer == null) {
            new WaitForBuffer(res);
        } else {
            TidyConfig.setBufferText(buffer, res);
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
        private String text;


        public WaitForBuffer(String text) {
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
                    TidyConfig.setBufferText(buffer, this.text);
                }
            }
        }
    }
}

