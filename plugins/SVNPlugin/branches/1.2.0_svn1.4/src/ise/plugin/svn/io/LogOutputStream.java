/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.io;

import java.io.*;
import java.util.logging.*;

import org.gjt.sp.jedit.View;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;

public class LogOutputStream extends OutputStream {

    private Logger logger = null;
    private Level level = Level.INFO;
    private String LS = System.getProperty("line.separator");

    public LogOutputStream( View view ) {
        OutputPanel panel = SVNPlugin.getOutputPanel( view );
        logger = panel.getLogger();
        logger.setUseParentHandlers(false);
    }

    public void setLevel( Level el ) {
        level = el;
    }

    public void write( int b ) {
        byte[] bytes = {( byte ) b};
        write( bytes, 0, 1 );
    }

    public void write( byte[] bytes, int offset, int length ) {
        String s = new String( bytes, offset, length );
        if (s.endsWith(LS)) {
            // logger.log will do a println, so remove the last line separator
            // to avoid double spacing
            s = s.substring(0, s.length() - LS.length());
        }
        logger.log( level, s );
    }
    public void close() {
        logger.fine( "\n---------------------------------------\n" );
    }
}
