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
import org.tmatesoft.svn.core.SVNURL;

public class UpdateData extends SVNData {

    private static final long serialVersionUID = 42L;

    private List<String> conflictedFiles = null;
    private List<String> addedFiles = null;
    private List<String> deletedFiles = null;
    private List<String> updatedFiles = null;
    private transient SVNRevision revision = SVNRevision.HEAD;
    private SVNURL url = null;


    /**
     * @return the numeric value of the revision
     */
    public long getRevision() {
        return revision.getNumber();
    }

    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setRevision( long number ) {
        this.revision = SVNRevision.create(number);
    }

    /**
     * Returns the value of revision.
     */
    public SVNRevision getSVNRevision() {
        return revision;
    }

    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setSVNRevision( SVNRevision revision ) {
        this.revision = revision;
    }

    public void setURL(SVNURL url) {
        this.url = url;
    }

    public SVNURL getURL() {
        return url;
    }

    /**
     * Returns the value of conflictedFiles.
     */
    public List<String> getConflictedFiles() {
        return conflictedFiles;
    }

    /**
     * Sets the value of conflictedFiles.
     * @param conflictedFiles The value to assign conflictedFiles.
     */
    public void setConflictedFiles( List<String> conflictedFiles ) {
        this.conflictedFiles = conflictedFiles;
    }

    /**
     * Returns the value of addedFiles.
     */
    public List<String> getAddedFiles() {
        return addedFiles;
    }

    /**
     * Sets the value of addedFiles.
     * @param addedFiles The value to assign addedFiles.
     */
    public void setAddedFiles( List<String> addedFiles ) {
        this.addedFiles = addedFiles;
    }

    /**
     * Returns the value of deletedFiles.
     */
    public List<String> getDeletedFiles() {
        return deletedFiles;
    }

    /**
     * Sets the value of deletedFiles.
     * @param deletedFiles The value to assign deletedFiles.
     */
    public void setDeletedFiles( List<String> deletedFiles ) {
        this.deletedFiles = deletedFiles;
    }

    /**
     * Returns the value of updatedFiles.
     */
    public List<String> getUpdatedFiles() {
        return updatedFiles;
    }

    /**
     * Sets the value of updatedFiles.
     * @param updatedFiles The value to assign updatedFiles.
     */
    public void setUpdatedFiles( List<String> updatedFiles ) {
        this.updatedFiles = updatedFiles;
    }


}
