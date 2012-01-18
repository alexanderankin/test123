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

import java.util.List;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Data object to represent 2 revisions used for a diff on a single file.
 * Revision 1 is always for a remote file, revision 2 may be a local file
 * or a remote file.
 *
 * Can also be used to represent 2 remote files for diff.  Revision 1 corresponds
 * to getPaths().get(0), revision 2 corresponds to getPaths().get(1).
 */
public class DiffData extends SVNData {

    private static final long serialVersionUID = 42L;

    private String repositoryUrl = null;
    private SVNRevision revision1 = SVNRevision.HEAD;
    private SVNRevision revision2 = null;
    private boolean svnDiff = false;

    public void setURL( String url ) {
        repositoryUrl = url;
    }

    public String getURL() {
        return repositoryUrl;
    }

    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setRevision1( SVNRevision revision ) {
        this.revision1 = revision;
    }

    public SVNRevision getRevision1() {
        return revision1;
    }

    public void setRevision2( SVNRevision revision ) {
        this.revision2 = revision;
    }

    public SVNRevision getRevision2() {
        return revision2;
    }

    /**
     * @return true if the user is requesting an svn diff rather than a jdiff diff.
     */
    public boolean getSvnDiff() {
        return svnDiff;
    }

    public void setSvnDiff( boolean svnDiff ) {
        this.svnDiff = svnDiff;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "DiffData[\n" );
        List paths = getPaths();
        if ( paths != null && paths.size() > 0 ) {
            sb.append( "\n\tpath1=" );
            sb.append( getPaths().get( 0 ) );
        }
        if ( paths != null && paths.size() > 1 ) {
            sb.append( "\n\tpath2=" );
            sb.append( getPaths().get( 1 ) );
        }
        sb.append( "\n\turl=" );
        sb.append( repositoryUrl );
        sb.append( "\n\trev1=" );
        sb.append( revision1 );
        sb.append( "\n\trev2=" );
        sb.append( revision2 );
        sb.append( "\n]" );
        return sb.toString();
    }
}