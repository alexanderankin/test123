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

package ise.plugin.svn.data;

import java.io.*;
import java.util.*;
import ise.plugin.svn.io.ConsolePrintStream;


public class CommitData extends SVNData {
    private boolean keepLocks = true;
    private String commitMessage = "";
    private CommitInfo info = null;

    public CommitData(){}

    public CommitData( ConsolePrintStream out,
            ConsolePrintStream err,
            List<String> paths,
            boolean keepLocks,
            String commitMessage,
            boolean recursive,
            String username,
            String password ) {
        super(out, err, paths, username, password, recursive);
        this.keepLocks = keepLocks;
        this.commitMessage = commitMessage;
    }

    /**
     * Returns the value of keepLocks.
     */
    public boolean getKeepLocks() {
        return keepLocks;
    }

    /**
     * Sets the value of keepLocks.
     * @param keepLocks The value to assign keepLocks.
     */
    public void setKeepLocks( boolean keepLocks ) {
        this.keepLocks = keepLocks;
    }

    /**
     * Returns the value of commitMessage.
     */
    public String getCommitMessage() {
        return commitMessage;
    }

    /**
     * Sets the value of commitMessage.
     * @param commitMessage The value to assign commitMessage.
     */
    public void setCommitMessage( String commitMessage ) {
        this.commitMessage = commitMessage;
    }

    public CommitInfo getInfo() {
        return info;
    }

    public void setInfo(CommitInfo info) {
        this.info = info;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(getClass().getName()).append('[');
            java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                sb.append(field.getName()).append('=').append(ise.plugin.svn.library.PrivilegedAccessor.getValue(this, field.getName())).append(',');
            }
            sb.append(']');
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
