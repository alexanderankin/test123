/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.jedit.projectviewer;

import java.util.Vector;


/**
Stores information about a Project mainly used as a tag and this.get()

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
*/
public class Project {

    private String      name;
    private int         key;
    private Directory   root = null;
    private Vector      directories = new Vector();
    private File        lastFile = null;
    private boolean     compiled = false;
    
    
    public Project(String name, Directory root) {
        this.name = name;
        this.setRoot(root);
    }

    Project(String name, Directory root, int key) {
        this.setRoot(root);
        this.name = name;
        this.key = key;
    }

    
    /**
    Projects can optionally have a "root" home directory.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public boolean isCompiled() {
        return this.compiled;
    }
    
    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }
    
    
    /**
    Projects can optionally have a "root" home directory.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public Directory getRoot() {
        return this.root;
    }

    /**
    Set the root directory
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public void setRoot(Directory root) {
        
        /*
        //trim the last char if it is file.separator
        if (root.length() > 1) {
            String last
        }
        */

        this.root = root;
    }

    

    /**
    Get the name of the project
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public String get() {
        return this.name;
    }

    /**
    Get a list of directories under this project

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public Directory[] getDirectories() {
        Directory[] directories = new Directory[this.directories.size()];
        this.directories.copyInto(directories);
        return directories;
    }
    
    public Directory getDirectory(Directory directory) {

        return (Directory)this.directories.elementAt( this.directories.indexOf(directory) );
    }
    

    /**
    Remove a directory from this project

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public void removeDirectory(Directory directory) {
        this.directories.removeElement(directory);
    }

    /**
    Add a directory to this project

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public void addDirectory(Directory directory) {
        this.directories.addElement(directory);
    }

    public void clearDirectories() {
        this.directories = new Vector();        
    }

    /**
    Returns true if this project has this directory.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public boolean hasDirectory(Directory directory) {
        return this.directories.contains(directory);
    }
    
    public String toString() {
        return this.name;
    }

    /**
    Used internally by Project to determine a unique identy in its property file

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    int getKey () {
        return this.key;
    }

    /**

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    void setKey(int key) {
        this.key = key;
    }

    /**
    Dump this projects information to stdout
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public void dump() {
        
        Logger.log("\tDUMP: ProjectName = " + this.get() );
        Logger.log("\tDUMP: ProjectKey = " + this.getKey() );
        
        Directory[] directories = this.getDirectories();
        
        for (int j = 0; j < directories.length; ++j) {
            Logger.log( "\t directory: + " + directories[j].get() , 200);
        }

        
    }


    /**
    Returns the last file edited within this project or null if none were
    ever selected.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public File getLastFile() {
        return this.lastFile;
    }

    /**
    Set the last edited file.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    void setLastFile(File lastFile) {
        this.lastFile = lastFile;
    }
    
    
}
