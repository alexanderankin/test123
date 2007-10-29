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
import java.util.Date;

/**
 * Data transfer object to mimic org.tmatesoft.core.wc.SVNCommitInfo.
 */
public class CommitInfo implements Serializable {
    private String author = null;
    private Date date = null;
    private String exception = null;
    private long revision = 0L;

    /**
     * Returns the value of author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the value of author.
     * @param author The value to assign author.
     */
    public void setAuthor( String author ) {
        this.author = author;
    }

    /**
     * Returns the value of date.
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /**
     * Sets the value of date.
     * @param date The value to assign date.
     */
    public void setDate( Date date ) {
        this.date = new Date(date.getTime());
    }

    /**
     * Returns the value of exception.
     */
    public String getException() {
        return exception;
    }

    /**
     * Sets the value of exception.
     * @param exception The value to assign exception.
     */
    public void setException( String exception ) {
        this.exception = exception;
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

}
