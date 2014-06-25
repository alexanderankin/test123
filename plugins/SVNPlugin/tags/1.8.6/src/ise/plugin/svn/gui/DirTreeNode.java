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

package ise.plugin.svn.gui;

import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents a node in a repository tree.  Overrides <code>isLeaf</code> so
 * that if the node represents a directory that hasn't been populated yet, it
 * will still be displayed as a folder instead of a file.
 */
public class DirTreeNode extends DefaultMutableTreeNode implements Comparable<DirTreeNode> {

    private boolean isLeaf = true;
    private boolean external = false;
    private boolean hasProperties = false;
    private String repositoryLocation = null;

    public DirTreeNode( Object userObject, boolean isLeaf ) {
        super( userObject );
        if ( userObject == null ) {
            throw new IllegalArgumentException( "null user object not allowed" );
        }
        this.isLeaf = isLeaf;
    }

    @Override
    public boolean isLeaf() {
        return this.isLeaf;
    }

    /**
     * @return true if the node represents a url as specified in an
     * svn:externals property
     */
    public boolean isExternal() {
        return external;
    }

    public void setExternal( boolean b ) {
        external = b;
    }

    public boolean hasProperties() {
        return hasProperties;
    }

    public void setHasProperties(boolean b) {
        hasProperties = b;
    }

    /**
     * @return the url of the external location, only valid if <code>isExternal</code>
     * returns true.
     */
    public String getRepositoryLocation() {
        return repositoryLocation;
    }

    public void setRepositoryLocation( String s ) {
        repositoryLocation = s;
    }

    /**
     * For sorting.  Directories sort before files, otherwise, sort by name.
     */
    public int compareTo( DirTreeNode node ) {
        // sort directories first
        if ( !this.isLeaf() && node.isLeaf() ) {
            return -1;
        }
        if ( this.isLeaf() && !node.isLeaf() ) {
            return 1;
        }

        // otherwise, sort by name
        String a = this.getUserObject().toString().toLowerCase();
        String b = ( ( DirTreeNode ) node ).getUserObject().toString().toLowerCase();
        return a.compareTo( b );
    }

    public boolean equals( Object o ) {
        if ( o == null ) {
            return false;
        }
        if ( !( o instanceof DirTreeNode ) ) {
            return false;
        }
        return o.hashCode() == hashCode();
    }

    public int hashCode() {
        return this.getUserObject().toString().toLowerCase().hashCode();
    }
}
