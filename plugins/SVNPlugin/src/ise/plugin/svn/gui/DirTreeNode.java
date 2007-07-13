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
    private String repositoryLocation = null;
    private Map properties = null;

    public DirTreeNode( Object userObject, boolean isLeaf ) {
        super( userObject );
        if (userObject == null) {
            throw new IllegalArgumentException("null user object not allowed");
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
     * @return the svn properties, if any, associated with this node.
     */
    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map p) {
        properties = p;
    }

    /**
     * For sorting.  Directories sort before files, otherwise, sort by name.
     */
    public int compareTo( DirTreeNode node ) {
        // sort directories first
        if (!this.isLeaf() && node.isLeaf()) {
            return -1;
        }
        if (this.isLeaf() && !node.isLeaf()) {
            return 1;
        }

        // otherwise, sort by name
        String a = this.getUserObject().toString().toLowerCase();
        String b = ( ( DirTreeNode ) node ).getUserObject().toString().toLowerCase();
        return a.compareTo( b );
    }
}
