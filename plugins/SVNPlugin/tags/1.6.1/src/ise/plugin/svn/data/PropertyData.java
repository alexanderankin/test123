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

import java.util.Properties;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class PropertyData extends CheckoutData {

    private static final long serialVersionUID = 42L;

    private transient SVNRevision pegRevision = SVNRevision.UNDEFINED;
    private transient SVNRevision revision = SVNRevision.HEAD;
    private transient boolean recursive = false;
    private transient boolean hasDirectory = false;
    private Properties properties = null;
    private String name = null;
    private String value = null;
    private boolean askRecursive = true;

    public String toString() {
        return "PropertyData[pegRevision=" + pegRevision + ", revision=" + revision + "]";
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

    public void setAskRecursive(boolean b) {
        askRecursive = b;
    }

    public boolean askRecursive() {
        return askRecursive;
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

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive( boolean recursive ) {
        this.recursive = recursive;
    }

    public void setHasDirectory( boolean b ) {
        hasDirectory = b;
    }

    public boolean hasDirectory() {
        return hasDirectory;
    }

    public void setProperties( Properties p ) {
        properties = p;
    }

    public Properties getProperties() {
        return properties == null ? null : new Properties( properties );
    }

    public void setName( String n ) {
        name = n;
    }
    public String getName() {
        return name;
    }

    public void setValue( String v ) {
        value = v;
    }

    public String getValue() {
        return value == null ? null : new String( value );
    }
}
