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
import org.tmatesoft.svn.core.wc.SVNStatus;

public class StatusData {
    private long revision;
    private List<SVNStatus> modified;
    private List<SVNStatus> conflicted;
    private List<SVNStatus> deleted;
    private List<SVNStatus> added;
    private List<SVNStatus> unversioned;
    private List<SVNStatus> missing;
    private List<SVNStatus> outOfDate;
    private List<SVNStatus> locked;
    private List<SVNStatus> normal;
    private List<String> errorFiles;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "StatusData[" );
        if ( added != null ) {
            sb.append( "Added:\n" );
            for ( SVNStatus status : added ) {
                sb.append( '\t' ).append( status.getFile() ).append( '=' ).append( status.getContentsStatus() ).append( '\n' );
            }
            sb.append( '\n' );
        }
        else if ( getConflicted() != null ) {
            sb.append( "Conflicted:\n" );
            for ( SVNStatus status : conflicted ) {
                sb.append( '\t' ).append( status.getFile() ).append( '=' ).append( status.getContentsStatus() ).append( '\n' );
            }
            sb.append( '\n' );
        }
        else if ( getDeleted() != null ) {
            sb.append( "Deleted:\n" );
            for ( SVNStatus status : deleted ) {
                sb.append( '\t' ).append( status.getFile() ).append( '=' ).append( status.getContentsStatus() ).append( '\n' );
            }
            sb.append( '\n' );
        }
        else if ( getLocked() != null ) {
            sb.append( "Locked:\n" );
            for ( SVNStatus status : locked ) {
                sb.append( '\t' ).append( status.getFile() ).append( '=' ).append( status.getContentsStatus() ).append( '\n' );
            }
            sb.append( '\n' );
        }
        else if ( getMissing() != null ) {
            sb.append( "Missing:\n" );
            for ( SVNStatus status : missing ) {
                sb.append( '\t' ).append( status.getFile() ).append( '=' ).append( status.getContentsStatus() ).append( '\n' );
            }
            sb.append( '\n' );
        }
        else if ( getModified() != null ) {
            sb.append( "Modified:\n" );
            for ( SVNStatus status : modified ) {
                sb.append( '\t' ).append( status.getFile() ).append( '=' ).append( status.getContentsStatus() ).append( '\n' );
            }
            sb.append( '\n' );
        }
        else if ( getOutOfDate() != null ) {
            sb.append( "Out of date:\n" );
            for ( SVNStatus status : outOfDate ) {
                sb.append( '\t' ).append( status.getFile() ).append( '=' ).append( status.getContentsStatus() ).append( '\n' );
            }
            sb.append( '\n' );
        }
        else if ( getUnversioned() != null ) {
            sb.append( "Unversioned:\n" );
            for ( SVNStatus status : unversioned ) {
                sb.append( '\t' ).append( status.getFile() ).append( '=' ).append( status.getContentsStatus() ).append( '\n' );
            }
            sb.append( '\n' );
        }
        else if ( getNormal() != null ) {
            sb.append( "Normal:\n" );
            for ( SVNStatus status : normal ) {
                sb.append( '\t' ).append( status.getFile() ).append( '=' ).append( status.getContentsStatus() ).append( '\n' );
            }
            sb.append( '\n' );
        }
        else if ( getErrorFiles() != null ) {
            sb.append( "Error files:\n" );
            for ( String status : errorFiles ) {
                sb.append( '\t' ).append( status ).append( '\n' );
            }
            sb.append( '\n' );
        }
        sb.append( "]\n" );
        return sb.toString();
    }

    /**
     * Returns the value of revision.
     */
    public long getRevision() {
        return revision;
    }

    /**
     * Sets the value of revision.
     * @param revision The value to assign revision.
     */
    public void setRevision( long revision ) {
        this.revision = revision;
    }

    /**
     * Returns the value of modified.
     */
    public List<SVNStatus> getModified() {
        return modified;
    }

    /**
     * Sets the value of modified.
     * @param modified The value to assign modified.
     */
    public void setModified( List<SVNStatus> modified ) {
        this.modified = modified;
    }

    /**
     * Returns the value of conflicted.
     */
    public List<SVNStatus> getConflicted() {
        return conflicted;
    }

    /**
     * Sets the value of conflicted.
     * @param conflicted The value to assign conflicted.
     */
    public void setConflicted( List<SVNStatus> conflicted ) {
        this.conflicted = conflicted;
    }

    /**
     * Returns the value of deleted.
     */
    public List<SVNStatus> getDeleted() {
        return deleted;
    }

    /**
     * Sets the value of deleted.
     * @param deleted The value to assign deleted.
     */
    public void setDeleted( List<SVNStatus> deleted ) {
        this.deleted = deleted;
    }

    /**
     * Returns the value of added.
     */
    public List<SVNStatus> getAdded() {
        return added;
    }

    /**
     * Sets the value of added.
     * @param added The value to assign added.
     */
    public void setAdded( List<SVNStatus> added ) {
        this.added = added;
    }

    /**
     * Returns the value of unversioned.
     */
    public List<SVNStatus> getUnversioned() {
        return unversioned;
    }

    /**
     * Sets the value of unversioned.
     * @param unversioned The value to assign unversioned.
     */
    public void setUnversioned( List<SVNStatus> unversioned ) {
        this.unversioned = unversioned;
    }

    /**
     * Returns the value of missing.
     */
    public List<SVNStatus> getMissing() {
        return missing;
    }

    /**
     * Sets the value of missing.
     * @param missing The value to assign missing.
     */
    public void setMissing( List<SVNStatus> missing ) {
        this.missing = missing;
    }

    /**
     * Returns the value of outOfDate.
     */
    public List<SVNStatus> getOutOfDate() {
        return outOfDate;
    }

    /**
     * Sets the value of outOfDate.
     * @param outOfDate The value to assign outOfDate.
     */
    public void setOutOfDate( List<SVNStatus> outOfDate ) {
        this.outOfDate = outOfDate;
    }

    public List<SVNStatus> getLocked() {
        return locked;
    }

    public void setLocked( List<SVNStatus> Locked ) {
        this.locked = Locked;
    }

    public List<SVNStatus> getNormal() {
        return normal;
    }

    public void setNormal( List<SVNStatus> normal ) {
        this.normal = normal;
    }

    public List<String> getErrorFiles() {
        return errorFiles;
    }

    public void setErrorFiles( List<String> errorFiles ) {
        this.errorFiles = errorFiles;
    }
}