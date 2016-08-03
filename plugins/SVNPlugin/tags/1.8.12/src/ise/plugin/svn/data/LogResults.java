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

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNInfo;

public class LogResults implements Serializable {

    private static final long serialVersionUID = 42L;

    private transient SVNInfo info;
    private transient TreeMap < String, List < SVNLogEntry >> entries;
    /**
     * Returns the value of info.
     */
    public SVNInfo getInfo() {
        return info;
    }

    /**
     * Sets the value of info.
     * @param info The value to assign info.
     */
    public void setInfo( SVNInfo info ) {
        this.info = info;
    }

    /**
     * @return map with the path of a file as the key and a list of associated
     * log entries as the value (in Perforce terms, the rest of the changelist).
     */
    public TreeMap < String, List < SVNLogEntry >> getEntries() {
        return entries;
    }

    /**
     * Sets the value of entries.
     * @param entries The value to assign entries.
     */
    public void setEntries( TreeMap < String, List < SVNLogEntry >> entries ) {
        this.entries = entries;
    }
}