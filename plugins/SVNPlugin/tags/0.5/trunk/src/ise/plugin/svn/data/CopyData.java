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
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;


public class CopyData extends SVNData {

    private File sourceFile = null;
    private SVNURL sourceURL = null;
    private File destinationFile = null;
    private SVNURL destinationURL = null;
    private SVNRevision revision = null;
    private boolean isMove = false;
    private String message = null;

    public void setMessage(String m) {
        message = m;
    }

    public String getMessage() {
        return message == null || message.length() == 0 ? "copying" : message;
    }


    /**
     * Returns the value of revision.
     */
    public SVNRevision getRevision() {
        return revision;
    }

    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setRevision( SVNRevision revision ) {
        this.revision = revision;
    }


    /**
     * Returns the value of sourceFile.
     */
    public File getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the value of sourceFile.
     * @param sourceFile The value to assign sourceFile.
     */
    public void setSourceFile( File sourceFile ) {
        this.sourceFile = sourceFile;
    }

    /**
     * Returns the value of sourceURL.
     */
    public SVNURL getSourceURL() {
        return sourceURL;
    }

    /**
     * Sets the value of sourceURL.
     * @param sourceURL The value to assign sourceURL.
     */
    public void setSourceURL( SVNURL sourceURL ) {
        this.sourceURL = sourceURL;
    }

    /**
     * Returns the value of destinationFile.
     */
    public File getDestinationFile() {
        return destinationFile;
    }

    /**
     * Sets the value of destinationFile.
     * @param destinationFile The value to assign destinationFile.
     */
    public void setDestinationFile( File destinationFile ) {
        this.destinationFile = destinationFile;
    }

    /**
     * Returns the value of destinationURL.
     */
    public SVNURL getDestinationURL() {
        return destinationURL;
    }

    /**
     * Sets the value of destinationURL.
     * @param destinationURL The value to assign destinationURL.
     */
    public void setDestinationURL( SVNURL destinationURL ) {
        this.destinationURL = destinationURL;
    }

    public void setIsMove(boolean b) {
        isMove = b;
    }

    public boolean getIsMove() {
        return isMove;
    }

}
