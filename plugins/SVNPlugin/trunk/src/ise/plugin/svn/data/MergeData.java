/*
Copyright (c) 2008, Dale Anson
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
import org.tmatesoft.svn.core.wc.SVNRevision;


public class MergeData extends SVNData implements Serializable {

    private static final long serialVersionUID = 42L;   // the answer to the universe and everything!

    private String fromPath = null;
    private File fromFile = null;
    private String intoPath = null;
    private File intoFile = null;
    private String destPath = null;
    private File destFile = null;
    private SVNRevision startRevision = null;
    private SVNRevision endRevision = null;
    private boolean recursive = false;
    private boolean force = false;


    /**
     * @return null if valid, error string if not.
     */
    public String checkValid() {
        if ( fromPath == null && fromFile == null ) {
            return "Merge from path not selected.";
        }
        if ( intoPath == null && intoFile == null ) {
            return "Merge to path not selected.";
        }
        if ( destPath == null && destFile == null ) {
            return "Merge destination not selected.";
        }
        if ( startRevision == null ) {
            return "Start revision not selected.";
        }
        if ( endRevision == null ) {
            return "End revision not selected.";
        }
        return null;
    }


    /**
     * Returns the value of fromFile.
     */
    public File getFromFile() {
        return fromFile;
    }

    /**
     * Sets the value of fromFile.
     * @param fromFile The value to assign fromFile.
     */
    public void setFromFile( File fromFile ) {
        this.fromFile = fromFile;
    }

    /**
     * Returns the value of intoPath.
     */
    public String getIntoPath() {
        return intoPath;
    }

    /**
     * Sets the value of intoPath.
     * @param intoPath The value to assign intoPath.
     */
    public void setIntoPath( String intoPath ) {
        this.intoPath = intoPath;
    }

    /**
     * Returns the value of intoFile.
     */
    public File getIntoFile() {
        return intoFile;
    }

    /**
     * Sets the value of intoFile.
     * @param intoFile The value to assign intoFile.
     */
    public void setIntoFile( File intoFile ) {
        this.intoFile = intoFile;
    }

    /**
     * Returns the value of destPath.
     */
    public String getDestPath() {
        return destPath;
    }

    /**
     * Sets the value of destPath.
     * @param destPath The value to assign destPath.
     */
    public void setDestPath( String destPath ) {
        this.destPath = destPath;
    }

    /**
     * Returns the value of destFile.
     */
    public File getDestinationFile() {
        return destFile;
    }

    /**
     * Sets the value of destFile.
     * @param destFile The value to assign destFile.
     */
    public void setDestinationFile( File destFile ) {
        this.destFile = destFile;
    }

    /**
     * Returns the value of startRevision.
     */
    public SVNRevision getStartRevision() {
        return startRevision;
    }

    /**
     * Sets the value of startRevision.
     * @param startRevision The value to assign startRevision.
     */
    public void setStartRevision( SVNRevision startRevision ) {
        this.startRevision = startRevision;
    }

    /**
     * Returns the value of fromPath.
     */
    public String getFromPath() {
        return fromPath;
    }

    /**
     * Sets the value of fromPath.
     * @param fromPath The value to assign fromPath.
     */
    public void setFromPath( String fromPath ) {
        this.fromPath = fromPath;
    }

    /**
     * Returns the value of destPath.
     */
    public String getDestinationPath() {
        return destPath;
    }

    /**
     * Sets the value of destPath.
     * @param destPath The value to assign destPath.
     */
    public void setDestinationPath( String destPath ) {
        this.destPath = destPath;
    }

    /**
     * Returns the value of endRevision.
     */
    public SVNRevision getEndRevision() {
        return endRevision;
    }

    /**
     * Sets the value of endRevision.
     * @param endRevision The value to assign endRevision.
     */
    public void setEndRevision( SVNRevision endRevision ) {
        this.endRevision = endRevision;
    }

    /**
     * Returns the value of recursive.
     */
    public boolean getRecursive() {
        return recursive;
    }

    /**
     * Sets the value of recursive.
     * @param recursive The value to assign recursive.
     */
    public void setRecursive( boolean recursive ) {
        this.recursive = recursive;
    }

    /**
     * Returns the value of force.
     */
    public boolean getForce() {
        return force;
    }

    /**
     * Sets the value of force.
     * @param force The value to assign force.
     */
    public void setForce( boolean force ) {
        this.force = force;
    }


}