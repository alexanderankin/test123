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

//standard java stuff
import java.io.*;

//jedit integrations.
import org.gjt.sp.jedit.*;

/**

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Revision$
*/

public class File {

    private String          name;
    private int             key;
    private Project         project     = null;
    private Directory       directory   = null;
    private String          fileName    = null;
    private boolean         subscribed  = false;
    private java.io.File    file        = null;
    //private Buffer          buffer      = null;
    
    public File(Project project, String name) {
        this.project = project;


        this.file = new java.io.File( name );

        //use getAbsolutePath so that symbolic links are stored as their full 
        //path.  If this is not done then some file names won't match up.

        try {
            this.name = new java.io.File( name ).getCanonicalPath();
        } catch (IOException e) {
            this.name = name;
        }



    }

    File(Project project, String name, int key) {
        this( project, name );

        this.key = key;
    }

    /**
    Returns true if this file is subscribed to.  A subscribed file is one that 
    is launched when you start Project Viewer=.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    public boolean isSubscribed() {
        return this.subscribed;
    }

    /**
    If this file has been subscribed to, returns its jEdit buffer.  If not then
    it returns null
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    public Buffer getBuffer() {
        
        return jEdit.getBuffer( this.get() );
            
    }

    /*
    Set this files jEdit buffer.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>

    void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }
    */    
   
    /**
    Set the status as to whether this file is subscribed or not.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public void setSubscribed(boolean subscribed) {


        if (subscribed == true) {
            ProjectResources.addSubscribedFile( this );
        } 

        this.subscribed = subscribed;

        if (subscribed == true) {
            ProjectResources.reportFileSubscribed(this);
        } else {
            ProjectResources.reportFileUnSubscribed(this);            
        }
        
    }
    
    
    /**
    <p>
    Used to determine the directory a file is store in.
    </p>
    
    <p>So for example if the filename were "/tmp/text.txt" the directory would
    be "/tmp/" and the file would be "text.txt".</p>

    <p>Also for files like /test.txt note that the directory would be "/" or
    file.separator</p>
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public Directory getDirectory() {

        if (this.directory == null) {
        
            int start = 0;
            int end = this.get().lastIndexOf( System.getProperty("file.separator") ) + 1;
            Directory directory = new Directory( this.get().substring(start, end), this.getProject() );


            this.directory = directory;
            return directory;

        } else {

            return this.directory;

        }

    }

    /**
    <p>
    Used to determine the filename of a file
    </p>
    
    <p>So for example if the filename were "/tmp/text.txt" the directory would
    be "/tmp/" and the file would be "text.txt".</p>

    <p>Also for files like /test.txt note that the directory would be "/" or
    file.separator</p>
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public String getFileName() {

        if (this.fileName == null) {
        
            int start = this.get().lastIndexOf( System.getProperty("file.separator") ) + 1;
            int end = this.get().length();
            String fileName = this.get().substring(start, end);


            this.fileName = fileName;
            return this.fileName;
        } else {
            return this.fileName;   
        }

    }
    

    
    public String get() {
        return this.name;
    }

    public String toString() {
        return this.getFileName();
    }

    /**
    Used internally by Project to determine a unique identy in its property file
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

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public Project getProject() {
        return this.project;
    }

    /**
    Dump this files information to stdout
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public void dump() {
        
        Logger.log("\tDUMP: FileName = " + this.get() , 200);
        Logger.log("\tDUMP: FileKey = " + this.getKey(), 200 );
        if ( this.isSubscribed() ) {
            Logger.log("\t\tSUBSCRIBED", 200 );
            Logger.log("\t\tBUFFER = " + this.getBuffer(), 200);   
        }
    }
    

    /**
    Provide comparison between two Files
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public boolean equals(Object obj) {
        if (obj instanceof File == false) 
            return false;
            
        //ok it is a file... compare the two..
        File file = (File)obj;
        //FIX ME:  also compare the Project
        return  (
                    ( this.get().equals( file.get() ) ) &&
                    ( this.getKey() == file.getKey() )
                );

    }
    
    

    
    /**
    Return an java.io.File object which allows you to perform normal IO 
    operations.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    */
    public java.io.File getFile() {

        return this.file;
    }
    
}
