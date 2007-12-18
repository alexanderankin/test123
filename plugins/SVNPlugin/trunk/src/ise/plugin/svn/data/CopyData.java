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


import java.io.File;
import java.io.Serializable;
import java.util.*;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;


public class CopyData extends SVNData implements Serializable {

    private static final long serialVersionUID = 42L;

    // single source file
    private File sourceFile = null;

    // multiple files
    private List<File> sourceFiles = null;

    // single source URL
    private transient SVNURL sourceURL = null;

    // copy destination if on local filesystem
    private File destinationFile = null;

    // copy destination if on remote repository
    private transient SVNURL destinationURL = null;

    // revision to copy from
    private transient SVNRevision revision = null;

    // true if this class represents a move rather than a copy
    private boolean isMove = false;

    // commit message
    private String message = null;


    // set/get commit message
    public void setMessage(String m) {
        message = m;
    }
    public String getMessage() {
        return message == null || message.length() == 0 ? "copying" : message;
    }

    // set/get revision to copy from
    public SVNRevision getRevision() {
        return revision;
    }
    public void setRevision( SVNRevision revision ) {
        this.revision = revision;
    }

    // set/get single source file to copy
    public File getSourceFile() {
        if (sourceFiles != null) {
            return sourceFiles.get(0);
        }
        return sourceFile;
    }
    public void setSourceFile( File sourceFile ) {
        this.sourceFile = sourceFile;
    }

    // Set/get filenames for copying multiple files to the destination.
    // @param files Map of filename to recursive, recursive is a boolean, always
    // false for files, could be true for directories.
    public void setSourceFiles(List<File> files) {
        sourceFiles = files;
    }
    public List<File> getSourceFiles() {
        return sourceFiles;
    }

    // set/get source url for copying remote url
    public SVNURL getSourceURL() {
        return sourceURL;
    }
    public void setSourceURL( SVNURL sourceURL ) {
        this.sourceURL = sourceURL;
    }

    // set/get the local filesystem destination for the copy
    // @return the value of destinationFile, should be a directory if there is
    // more than one source file.
    public File getDestinationFile() {
        return destinationFile;
    }
    public void setDestinationFile( File destinationFile ) {
        this.destinationFile = destinationFile;
    }

    // set/get the remote repository destination url for the copy
    // @return the value of destinationURL.
    public SVNURL getDestinationURL() {
        return destinationURL;
    }
    public void setDestinationURL( SVNURL destinationURL ) {
        this.destinationURL = destinationURL;
    }

    // set/get is this a move rather than a copy
    public void setIsMove(boolean b) {
        isMove = b;
    }
    public boolean getIsMove() {
        return isMove;
    }

}
