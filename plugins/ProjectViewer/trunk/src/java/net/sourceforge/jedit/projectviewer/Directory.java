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
public class Directory {

    private String      name;
    private Project     project = null;
    private Directory   parent = null;
    private Directory[] children = null;
    
    /**
    The number of files this directory has underneath it.
    */
    private int         fileCount = 0;


    /**
    Default constructor.  This should be used almost always.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public Directory(String name, Project project) {

        name = this.getCorrectName(name);

        this.name = name;
        this.project = project;
    }


    /**
    Only call this constructor when creating a new Project.  setProject should
    be called right after.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public Directory(String name) {

        name = this.getCorrectName(name);

        this.name = name;
        this.project = project;
    }

    /**
    Sometimes a directory with a trailing file.separator should chopped.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    private String getCorrectName(String name) {

        if (name.substring(name.length() - 1, name.length() ).equals(System.getProperty("file.separator")) ) {
            name = name.substring( 0, name.length() - 1);
        }

        return name;
    }


    /**
    Returns the parent directory for this directory or null if it doesnt have one (ie root)

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public Directory getParent() {
        if (this.parent == null) {

            int start = 0;
            int end = this.get().lastIndexOf(System.getProperty("file.separator"));
            
            if (end == -1) 
                return null;

            this.parent = new Directory(
                                        this.get().substring( start, end ),
                                        this.getProject() );
        }

        return this.parent;
    }

    /**
    Set the project for this directory.  Should not be called if the current
    project is already set.  If it is set then setProject makes not changes.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    void setProject(Project project) {
        if (this.project == null)
            this.project = project;
    }


    /**
    @return Project the project that this Directory is part of.
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public Project getProject() {
        return this.project;
    }

    /**
    Returns the number of files within this directory.
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public int getFileCount() {
        return this.fileCount;
    }


    /**
    Returns the number of files within this directory.
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public String get() {
        return this.name;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public String toString() {
        return name.substring( name.lastIndexOf(java.io.File.separator) + 1, name.length() );
    }


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public Directory[] getChildren(Directory[] dirs) {

        if (this.children == null) {
    
            //Logger.log("searching for subs in " + this.get(), 9);

            Vector v = new Vector();
    
            for (int i = 0;i < dirs.length; ++i) {
                if ( dirs[i].get().indexOf( this.get() ) >= 0 ) {
    
                    //Logger.log("contains " + dirs[i].get(), 9);
                    
                    int start = 0;
                    int offset =  this.get().length() + 1;
    
                    int end = dirs[i].get().indexOf( java.io.File.separator, offset);
    
    
                    //in this situation... this directory is in the form of /parent/child 
                    //so there is no trailing File.separator
                    if (end == -1) {
                        //start = dirs[i].get().lastIndexOf(java.io.File.separator) + 1;
                        end = dirs[i].get().length();
                        //continue;
                    }
    
                    
                    Directory newdir = new Directory ( dirs[i].get().substring(start, end), this.getProject() ) ;

                    //if the newdir is equal to the current dir then obviosly we are wrong here.
                    
                    if (newdir.equals(this))
                        continue;
                    
                    
                    if (v.contains( newdir ) == false) {
                        //Logger.log("start: " + start + " offset: " + offset + " end: " + end , 9);
                        //Logger.log("newdir is  " + newdir.get(), 9);                    


                        //Logger.log("adding dir: " + newdir.get(),9 );
                        v.addElement( newdir );
                        //Logger.log(newdir.toString(),9);
                    }
    
    
                }
    
            }
    
    
    
            Directory[] children = new Directory[v.size()];
            v.copyInto(children);
    
            this.children = children;
    
        } 
        /*
        else {
            Logger.log("PULLED FROM CACHE", 9);
        }
        */
        
        return children;
    }

    /**
    Dump this directories information to stdout

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void dump() {
        
        Logger.log("\tDUMP: DirectoryName = " + this.get() );
        
    }

    /**
    Compare two directories..
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public boolean equals(Object obj) {
        if (obj instanceof Directory == false) {
            Logger.log("WARNING:  object is not a Directory", 200);
            return false;
        }

        Directory dir = (Directory)obj;
        return this.get().equals( dir.get() );
    }

}
