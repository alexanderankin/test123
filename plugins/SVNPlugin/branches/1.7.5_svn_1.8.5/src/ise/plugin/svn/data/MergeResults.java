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

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Data transfer object for the results of a merge.
 */
public class MergeResults implements Serializable {

    // if there is any error during the merge, this field will contain a
    // non-null message for display
    private String errorMessage = null;

    // lists of files that fall into particular merge categories
    private List<String> conflicted = null;
    private List<String> added = null;
    private List<String> deleted = null;
    private List<String> merged = null;
    private List<String> skipped = null;
    private List<String> updated = null;

    private boolean dryRun = true;
    private boolean cancelled = false;

    private String commandLineEquivalent;
    /**
     * Returns the value of commandLineEquivalent.
     */
    public String getCommandLineEquivalent() {
        return commandLineEquivalent;
    }

    /**
     * Sets the value of commandLineEquivalent.
     * @param commandLineEquivalent The value to assign commandLineEquivalent.
     */
    public void setCommandLineEquivalent(String commandLineEquivalent) {
        this.commandLineEquivalent = commandLineEquivalent;
    }

    /**
     * Returns the value of dryRun.
     */
    public boolean isDryRun() {
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
     * Sets the value of conflicted.
     * @param conflicted The value to assign conflicted.
     */
    public void addConflicted( String filename ) {
        if ( conflicted == null ) {
            conflicted = new ArrayList<String>();
        }
        conflicted.add( filename );
    }

    /**
     * Sets the value of added.
     * @param added The value to assign added.
     */
    public void addAdded( String filename ) {
        if ( added == null ) {
            added = new ArrayList<String>();
        }
        added.add( filename );
    }

    /**
     * Sets the value of deleted.
     * @param deleted The value to assign deleted.
     */
    public void addDeleted( String filename ) {
        if ( deleted == null ) {
            deleted = new ArrayList<String>();
        }
        deleted.add( filename );
    }

    /**
     * Sets the value of merged.
     * @param merged The value to assign merged.
     */
    public void addMerged( String filename ) {
        if ( merged == null ) {
            merged = new ArrayList<String>();
        }
        merged.add( filename );
    }

    /**
     * Sets the value of skipped.
     * @param skipped The value to assign skipped.
     */
    public void addSkipped( String filename ) {
        if ( skipped == null ) {
            skipped = new ArrayList<String>();
        }
        skipped.add( filename );
    }

    /**
     * Sets the value of updated.
     * @param updated The value to assign updated.
     */
    public void addUpdated( String filename ) {
        if ( updated == null ) {
            updated = new ArrayList<String>();
        }
        updated.add( filename );
    }


    /**
     * Returns the value of conflicted.
     */
    public List<String> getConflicted() {
        return conflicted;
    }


    /**
     * Returns the value of added.
     */
    public List<String> getAdded() {
        return added;
    }


    /**
     * Returns the value of deleted.
     */
    public List<String> getDeleted() {
        return deleted;
    }


    /**
     * Returns the value of merged.
     */
    public List<String> getMerged() {
        return merged;
    }


    /**
     * Returns the value of skipped.
     */
    public List<String> getSkipped() {
        return skipped;
    }

    /**
     * Returns the value of updated.
     */
    public List<String> getUpdated() {
        return updated;
    }

    /**
     * Returns the value of errorMessage.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of errorMessage.
     * @param errorMessage The value to assign errorMessage.
     */
    public void setErrorMessage( String errorMessage ) {
        this.errorMessage = errorMessage;
    }


    /**
     * Returns the value of cancelled.
     */
    public boolean getCancelled() {
        return cancelled;
    }

    /**
     * Sets the value of cancelled.
     * @param cancelled The value to assign cancelled.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}