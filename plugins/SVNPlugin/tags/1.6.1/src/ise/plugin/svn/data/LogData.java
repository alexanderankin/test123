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

import org.tmatesoft.svn.core.wc.SVNRevision;

public class LogData extends CheckoutData {

    private static final long serialVersionUID = 42L;

    private transient SVNRevision startRevision = SVNRevision.create( 0L );
    private transient SVNRevision endRevision = SVNRevision.HEAD;
    private transient SVNRevision pegRevision = SVNRevision.UNDEFINED;
    private int maxLogs = 1000;
    private boolean stopOnCopy = true;
    private boolean showPaths = false;

    /**
     * Returns the value of stopOnCopy.
     */
    public boolean getStopOnCopy() {
        return stopOnCopy;
    }

    /**
     * Sets the value of stopOnCopy.
     * @param stopOnCopy The value to assign stopOnCopy.
     */
    public void setStopOnCopy( boolean stopOnCopy ) {
        this.stopOnCopy = stopOnCopy;
    }

    /**
     * Returns the value of showPaths.
     */
    public boolean getShowPaths() {
        return showPaths;
    }

    /**
     * Sets the value of showPaths.
     * @param showPaths The value to assign showPaths.
     */
    public void setShowPaths( boolean showPaths ) {
        this.showPaths = showPaths;
    }


    public String toString() {
        return "LogData[startRevision=" + startRevision + ", endRevision=" + endRevision + "]" + super.toString();
    }

    public void setMaxLogs( int n ) {
        maxLogs = Math.max( 0, n );
    }

    public int getMaxLogs() {
        return maxLogs;
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
     * Returns the value of pegRevision.
     */
    public SVNRevision getPegRevision() {
        return pegRevision;
    }

    /**
     * Sets the value of pegRevision.
     * @param pegRevision The value to assign pegRevision.
     */
    public void setPegRevision( SVNRevision pegRevision ) {
        this.pegRevision = pegRevision;
    }
}
