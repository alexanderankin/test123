/*
 * JTidyBeautifier.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
 * Copyright (c) 2011 Dale Anson
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

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.Log;

import org.w3c.tidy.Node;
import org.w3c.tidy.Tidy;

import beauty.beautifiers.Beautifier;

public class JTidyBeautifier extends Beautifier {
    public String beautify(String text) {
        Tidy jtidy = new Tidy();
        jtidy.setConfigurationFromProps(JTidyPlugin.getProperties());

        // Setting tidy input
        InputStream in = new BufferedInputStream(
            new ByteArrayInputStream(text.getBytes())
        );
        
        // Setting tidy output
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        OutputStream out = new NewlineOutputFilter(new BufferedOutputStream(baOut));

        // Setting tidy error output
        StringWriter sw = new StringWriter();
        JTidyErrorSourceWriter esw = new JTidyErrorSourceWriter(jEdit.getActiveView().getBuffer(), sw);
        PrintWriter jtidyErr = new PrintWriter(esw);
        jtidy.setErrout(jtidyErr);

        // Tidy buffer
        Node node = jtidy.parse(in, out);
        jtidyErr.close();
        
        // Tidy logo, errors, advice to activity log
        String notice = sw.toString();
        Log.log(Log.NOTICE, TidyBuffer.class, notice);

        if (node == null || esw.getErrorCount() > 0) {
            return null;
        }
        
        return baOut.toString();
    }
}