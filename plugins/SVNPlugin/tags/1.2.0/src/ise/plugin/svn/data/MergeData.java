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
import org.gjt.sp.jedit.jEdit;


public class MergeData extends SVNData implements Serializable {

    private static final long serialVersionUID = 42L;   // the answer to the universe and everything!

    private String fromPath = null;
    private File fromFile = null;
    private String toPath = null;
    private File toFile = null;
    private File destFile = null;
    private SVNRevision startRevision = null;
    private SVNRevision endRevision = null;
    private boolean recursive = false;
    private boolean force = false;
    private boolean dryRun = true;
    private boolean ignoreAncestry = true;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "MergeData[" );
        sb.append( "fromPath=" ).append( fromPath == null ? "null" : fromPath ).append( "," );
        sb.append( "fromFile=" ).append( fromFile == null ? "null" : fromFile.getAbsolutePath() ).append( "," );
        sb.append( "toPath=" ).append( toPath == null ? "null" : toPath ).append( "," );
        sb.append( "toFile=" ).append( toFile == null ? "null" : toFile.getAbsolutePath() ).append( "," );
        sb.append( "destFile=" ).append( destFile == null ? "null" : destFile.getAbsolutePath() ).append( "," );
        sb.append( "startRevision=" ).append( startRevision == null ? "null" : startRevision.toString() ).append( "," );
        sb.append( "endRevision=" ).append( endRevision == null ? "null" : endRevision.toString() ).append( "," );
        sb.append( "recursive=" ).append( recursive ).append( "," );
        sb.append( "force=" ).append( force ).append( "," );
        sb.append( "dryRun=" ).append( dryRun ).append( "," );
        sb.append( "ignoreAncestry=" ).append( ignoreAncestry ).append( "," );
        return sb.toString();
    }

    public String commandLineEquivalent() {
        if ( fromFile == null && fromPath == null ) {
            return jEdit.getProperty("ips.Invalid_merge_'from'.", "Invalid merge 'from'.");
        }
        if ( startRevision == null ) {
            return jEdit.getProperty("ips.Invalid_start_revision.", "Invalid start revision.");
        }

        StringBuffer sb = new StringBuffer();
        sb.append( "svn merge " );

        // flags
        if ( dryRun == true ) {
            sb.append( "--dry-run " );
        }
        if ( ignoreAncestry == true ) {
            sb.append( "--ignore-ancestry " );
        }
        if ( recursive == false ) {
            sb.append( "--non-recursive " );
        }
        if ( force == true ) {
            sb.append( "--force " );
        }

        // urls and revisions
        if ( ( toFile == null && toPath == null ) ||
                ( fromFile != null && fromFile.equals( toFile ) ) ||
                ( fromPath != null && fromPath.equals( toPath ) ) ) {
            // svn merge [-c M | -r N:M] SOURCE WCPATH
            if ( startRevision != null && startRevision.equals( endRevision ) ) {
                String sr = startRevision.getNumber() == -1 ? "HEAD" : String.valueOf( startRevision.getNumber() );
                sb.append( "-c " ).append( sr ).append( " " );
            }
            else if ( startRevision != null && endRevision != null ) {
                String sr = startRevision.getNumber() == -1 ? "HEAD" : String.valueOf( startRevision.getNumber() );
                String er = endRevision.getNumber() == -1 ? "HEAD" : String.valueOf( endRevision.getNumber() );
                sb.append( "-r " ).append( sr ).append( ":" ).append( er ).append( " " );
            }
            if ( toFile != null ) {
                sb.append( toFile );
            }
            else {
                sb.append( toPath );
            }
            sb.append( " " );
            if ( destFile == null ) {
                return jEdit.getProperty("ips.Invalid_working_copy.", "Invalid working copy.");
            }
            else {
                sb.append( destFile.getAbsolutePath() );
            }
            return sb.toString();
        }
        else if ( toFile != null || toPath != null ) {
            // svn merge sourceURL1@N sourceURL2@M WCPATH
            if ( fromFile == null && fromPath == null ) {
                return jEdit.getProperty("ips.Invalid_from_path.", "Invalid from path.");
            }
            if ( endRevision == null ) {
                return jEdit.getProperty("ips.Invalid_end_revision.", "Invalid end revision.");
            }
            sb.append( fromFile == null ? fromPath : fromFile.getAbsolutePath() ).append( "@" );
            sb.append( startRevision.getNumber() ).append( " " );
            sb.append( toFile == null ? toPath : toFile.getAbsolutePath() ).append( "@" );
            sb.append( endRevision.getNumber() ).append( " " );
            if ( destFile == null ) {
                return jEdit.getProperty("ips.Invalid_working_copy.", "Invalid working copy.");
            }
            else {
                sb.append( destFile.getAbsolutePath() );
            }
            return sb.toString();
        }
        else {
            return jEdit.getProperty("ips.Invalid_values_for_merge_request.", "Invalid values for merge request.");
        }
    }

    /**
     * @return null if valid, error string if not.
     */
    public String checkValid() {
        if ( fromPath == null && fromFile == null ) {
            return jEdit.getProperty( "ips.Merge_from_path_not_selected.", "Merge from path not selected." );
        }
        if ( toPath == null && toFile == null ) {
            return jEdit.getProperty( "ips.Merge_to_path_not_selected.", "Merge to path not selected." );
        }
        if ( destFile == null ) {
            return jEdit.getProperty( "ips.Merge_destination_not_selected.", "Merge destination not selected." );
        }
        if ( startRevision == null ) {
            return jEdit.getProperty( "ips.Start_revision_not_selected.", "Start revision not selected." );
        }
        if ( endRevision == null ) {
            return jEdit.getProperty( "ips.End_revision_not_selected.", "End revision not selected." );
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
     * Returns the value of toPath.
     */
    public String getToPath() {
        return toPath;
    }

    /**
     * Sets the value of toPath.
     * @param toPath The value to assign toPath.
     */
    public void setToPath( String toPath ) {
        this.toPath = toPath;
    }

    /**
     * Returns the value of toFile.
     */
    public File getToFile() {
        return toFile;
    }

    /**
     * Sets the value of toFile.
     * @param toFile The value to assign toFile.
     */
    public void setToFile( File toFile ) {
        this.toFile = toFile;
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

    /**
     * Returns the value of dryRun.
     */
    public boolean getDryRun() {
        return dryRun;
    }

    /**
     * Sets the value of dryRun.
     * @param dryRun The value to assign dryRun.
     */
    public void setDryRun( boolean dryRun ) {
        this.dryRun = dryRun;
    }

    /**
     * Returns the value of ignoreAncestry.
     */
    public boolean getIgnoreAncestry() {
        return ignoreAncestry;
    }

    /**
     * Sets the value of ignoreAncestry.
     * @param ignoreAncestry The value to assign ignoreAncestry.
     */
    public void setIgnoreAncestry( boolean ignoreAncestry ) {
        this.ignoreAncestry = ignoreAncestry;
    }
}