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

import java.util.*;
import java.io.*;

import org.gjt.sp.jedit.*;

/**
Contains a set of resources that JProject uses.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Revision$
*/

public class ProjectResources {

   
    /**
    Stores a Project -> Hashtable association
    the vector contains multiple File(s).
    */
    static private Hashtable   files                = new Hashtable();

    static private Hashtable   directories          = new Hashtable();  //stores a Project -> Directory association
                                                    
    static private Hashtable   subscribedFiles      = new Hashtable();
                                                                                                                                
    static private Vector      projects             = new Vector();

    static private String      projectDir           = "/tmp";
    
    static private String      projectResources     = ProjectViewer.NAME + ".projects.properties";
    static private String      fileResources        = ProjectViewer.NAME + ".files.properties";    

    static private boolean     useMemory            = false;

    static private Vector      listeners            = new Vector();

    /**
    When the constructor is called loadLock prevents save() from modifying the data
    files.  This is necessary because the internal addX() methods call save and if they
    are called from the constructor we will run into a race state.
    */
    static private boolean     loadLock            = true;



    /**
    Since everything is now static.  We should reparse everything if any methods
    are called on ProjectResources when isParsed() returns false
    
    */
    static private boolean     parsed              = false;
    
    
    /**
    Returns false if resources haven't been parsed out yet.


    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized public boolean isParsed() {
        return ProjectResources.parsed;
    }


    /**
    Set the resources parsing status.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized void setParsed(boolean parsed) {
        ProjectResources.parsed = parsed;
    }
    
    
    /**
    Normally, for every resource addition, the configuration file is written.  When
    you are trying to add a lot of resources performance drops because the configuration
    file is continually being re-written.
    
    Calling this with "true" tells save() to not do anything.  Calling this with "false"
    save()s the current resource state and then goes back to using disk.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized void setUseMemory(boolean useMemory) {
        
        ProjectResources.useMemory = useMemory;
        if (useMemory == false) {
            ProjectResources.save();
        }
        
    }
    
    /**
    Return whether or not the current resources are using in-memory structures

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized public boolean getUseMemory() {
        return ProjectResources.useMemory;
    }
    
    /**
    Return all projects that JProject knows about.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized public Project[] getProjects() {

        
        
        Project[] projects = new Project[ ProjectResources.projects.size() ];
        ProjectResources.projects.copyInto( projects ); 

        //MiscUtilities.quicksort( projects, new ProjectComparator() );

        return projects;
        
    }


    /**
    this is bad form but just look through project list and if you hit the key
    return it... else null

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    static synchronized private Project getProject(int key) {

        Project[] projects = ProjectResources.getProjects();
        
        for (int i = 0; i < projects.length; ++i) {
            if (projects[i].getKey() == key) 
                return projects[i];
        }
        
        return null;
    }
    
    /**
    If you have an object this will find the "in-memory" object and restore it.
    This is convenient for object comparison.
    
    returns null if it can't find it.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    static synchronized File getFile(File file) {

        File[] files = ProjectResources.getFiles( file.getProject() );

        for(int i = 0; i < files.length; ++i ) {
            File currentFile = files[i];
            if (
                    (currentFile.get().equals(file.get()) ) &&
                    (currentFile.getProject().get().equals( file.getProject().get()) )
               ) {
                   
                return currentFile;
            }
        }


        return null;
    }

    /**
    Get a file from the current file table or null it it doesn't exist.  Note 
    that this may cause problems if multiple files with the same name are 
    opened.
    
    */
    static synchronized File[] getFile(String file) {

        /*
        Enumeration projects = this.files.elements();
        while( projects.hasMoreElements() ) {
            Vector v = (Vector)projects.nextElement();



        }

        return (File)files.get(file);

        */
        return null;

    }
    
    
    
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    static synchronized private File getInitializedFile(File file) {

        
        //only do work if the given file is not initialized.
        if (file.getKey() == 0) {
            file.setKey( ProjectResources.getFiles( file.getProject() ).length + 1);
        }
        
        return file;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    static synchronized public Project getProject(String project) {

        Project[] projects = ProjectResources.getProjects();
        

        for (int i = 0; i < projects.length; ++i) {
            if (projects[i].get().equals(project)) 
                return projects[i];
        }


        return null;

    }
    
    /**
    Given a project, return an array of its file names

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    static synchronized public File[] getFiles(Project project) {

        

        //since I am using a Key with and sometimes not Vector.get won't work.

        Project[] projects = ProjectResources.getProjects();        
        
        Vector fileVector = new Vector();

        for (int i = 0; i < projects.length; ++i) {
            if (projects[i].get().equals( project.get() )) 
                fileVector = (Vector)ProjectResources.files.get(projects[i]);
        }
        
        int numFiles = 0;
        if (fileVector != null) {
            numFiles = fileVector.size();   
        } else {
            return new File[0];   
        }
        
        File[] files = new File[numFiles];
        fileVector.copyInto(files);

        
        //ok... now qsort it...
        //MiscUtilities.quicksort( files, new FileComparator() );
     
     
        return files;
    }


    /**
    Given a file, return an array of files that are stored in the 
    ProjectResources.  This is useful to determine all instances of a file 
    across all projects.
    
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$

    static synchronized public File[] getFiles(String filename) {
        
        Enumeration elements = this.files.elements();
        
        
        
    }
    */    
    
    /**
    Given a project, return an array of its file names that belong under
    the specified directory.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    static synchronized public File[] getFiles(Project project, Directory directory) {

        

        //since I am using a Key with and sometimes not Vector.get won't work.

        Project[] projects = ProjectResources.getProjects();        
        
        Vector fileVector = new Vector();

        for (int i = 0; i < projects.length; ++i) {
            if (projects[i].get().equals( project.get() )) {

                File[] files = ProjectResources.getFiles(projects[i]);

                for ( int j = 0; j < files.length; ++j ) {
                    //make sure it is the right directory
                    if ( files[j].getDirectory().equals(directory) ) 
                        fileVector.addElement(files[j]);

                }

                break;
            }
        }
        


        int numFiles = 0;
        if (fileVector != null) {
            numFiles = fileVector.size();   
        } else {
            return new File[0];   
        }
        
        File[] files = new File[numFiles];
        fileVector.copyInto(files);

        //MiscUtilities.quicksort( files, new FileComparator() );

        return files;

        
    }

    
    /**
    Populate the project/file information.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public static synchronized  void parse() {


        if (isParsed() == false) {

    
            try {
    
                //set projects and files back to their original values...
                ProjectResources.files       = new Hashtable();
                ProjectResources.projects    = new Vector();
            
                Properties projectProps = new Properties();
                Properties fileProps = new Properties();



                projectProps.load(  new FileInputStream(ProjectResources.getProjectsFileName()  ) );
                fileProps.load(     new FileInputStream(ProjectResources.getFilesFileName()     ) );
                


    
                Enumeration projectKeys = projectProps.propertyNames();
        
                
        
                //now get projects..
                
                int counter = 1;
                while ( projectKeys.hasMoreElements() ) {
                    String propertyName = (String)projectKeys.nextElement();
        
                    String value = projectProps.getProperty("project." + counter);
                    
                    if (value != null) {
        
                        //Logger.log("Found project: " +  value, 9);
                        
                        //int key = ProjectResources.getProjectKey( propertyName );
                        String root = projectProps.getProperty("project." + counter + ".root");
                        
                        Project project = new Project(value, new Directory(root), counter); 
        
                        ProjectResources.addProject( project );
                    } else {
                        //no need to continue if you have hit the end of the files list...
                        break;
                    }
        
                    ++counter;
                }
        
                //ProjectResources.dumpProjects();
        
        
                Vector v = new Vector();
        
                //ok... now just go through every known project.. and get their files...
                Project[] projects = ProjectResources.getProjects();
                for (int j = 0; j < projects.length; ++j) {
        
                    int curProject = j + 1;
                    int fileCounter = 1;
                    
                    String value;
                    do {
                        String fileEntry = "file." + fileCounter + ".project." + curProject;
                        value = fileProps.getProperty( fileEntry );
        
                        if (value == null)
                            break;
        
        
                        Project project = ProjectResources.getProject( curProject );
                        File file = new File(project, value, fileCounter);
                        
                        /*
                        String subscribed = fileProps.getProperty("file." + fileCounter + ".project." + curProject + ".subscribed");
                        
                        if (subscribed != null &&  subscribed.equals("TRUE") ) 
                            file.setSubscribed(true);
                        */
        
                        if (file.getFile().exists()) {
                            v.addElement(file);
                        } else {
                            Logger.log("WARNING:  File does not exist: " + file.get(), 200);
                        }
        
                        ++fileCounter;
                        
                    } while(value != null);
        
                    
                    
                }
                
        
                File[] files = new File[ v.size() ];
                v.copyInto(files);
        
                ProjectResources.addFiles( files ); 
    


            } catch  (Exception e) {

                //e.printStackTrace();

            } finally {    
    
    
                //FIX ME:  instantly set this to true so that  parse can internally
                //add resources.  but write some type of lock

                ProjectResources.setParsed(true);
                ProjectResources.loadLock = false;
                Logger.log("loadlock == false", 9);            
                
            }
           
        } 
        
    }
    
    
    /**
    Take ths JProject and save it to disk.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized public void save() {


        if (ProjectResources.getUseMemory() == false) {
            //ok.. using persistent disk storage... write the config file...
            
            if (ProjectResources.loadLock == false) {

                ProjectResources.saveProjects();
                ProjectResources.saveFiles();
                
            } 
            
           
        } 
         
        


    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized private void saveProjects() {

        //write projects in this format
        /*
        */
        
        try {

            FileOutputStream stream = new FileOutputStream( ProjectResources.getProjectsFileName() );

            //tell the user not to modify this file.
                
            stream.write("#DO NOT MODIFY THIS FILE:\n\n".getBytes());
            stream.write("#IF YOU DO WANT TO MODIFY IT... HERE IS THE SYNTAX\n".getBytes());
            stream.write("#project.1=projectName\n".getBytes());
            stream.write("#project.2=projectName\n".getBytes());
            
            Project[] projects = ProjectResources.getProjects();
        
            for ( int i = 0; i < projects.length; ++i ) {

                String entry;
                byte[] record;
                entry = "project." + projects[i].getKey() + "=" + projects[i].get() + "\n";
                record = entry.getBytes();
                stream.write(record);

                //add the root path entry..
                String root = projects[i].getRoot().get();

                root = ProjectResources.globalStringReplace(root, "\\", "\\\\");
               
                entry = "project." + projects[i].getKey() + ".root=" + root + "\n";
                record = entry.getBytes();
                stream.write(record);


            }

            stream.close();

        } catch (IOException e) {

        }

    }
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized private void saveFiles() {
        
        //write files in this format
        
        try {

            FileOutputStream stream = new FileOutputStream( ProjectResources.getFilesFileName() );

            stream.write("#DO NOT MODIFY THIS FILE:\n\n".getBytes());

            stream.write("#IF YOU DO WANT TO MODIFY IT... HERE IS THE SYNTAX\n".getBytes());
            stream.write("#file.1.project.1=filename\n".getBytes());
            stream.write("#file.2.project.1=filename\n".getBytes());
            stream.write("#file.1.project.2=filename\n".getBytes());
            stream.write("#file.2.project.2=filename\n".getBytes());
            //stream.write("#file.2.project.2.subscribed=TRUE\n".getBytes());

            Project[] projects = ProjectResources.getProjects();
        
            for ( int i = 0; i < projects.length; ++i ) {

                //now loop through the files...
                
                File[] files = ProjectResources.getFiles( projects[i] );
                
                for (int j = 0; j < files.length; ++j) {

                    String filename = files[j].get();
                    //under NT filenames with "\" in their name need to be escaped to "\\"
                    filename = ProjectResources.globalStringReplace(filename, "\\", "\\\\");
                    
                    
                    String entry = "file." + files[j].getKey() + ".project." + projects[i].getKey() + "=" + filename + "\n";
                    byte[] record = entry.getBytes();
                    stream.write(record);

                    /*
                    if ( files[j].isSubscribed() ) {
                        entry = "file." + files[j].getKey() + ".project." + projects[i].getKey() + ".subscribed=TRUE\n";
                        record = entry.getBytes();
                        stream.write(record);
                    }
                    */
                    
                }

            }

            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized private String getSettingsDirectory() {

        String dir = jEdit.getSettingsDirectory();
        
        if (dir == null) {
            dir =  ProjectResources.projectDir;
        }
        
        return dir;
        
    }
    
    /**
    Returns the name of the property file to store Project ProjectResources in

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
     static synchronized private String getProjectsFileName() {

        String file = ProjectResources.getSettingsDirectory() + System.getProperty("file.separator") + ProjectResources.projectResources;


        return file;
        
    }

    /**
    Returns the name of the property file to store File ProjectResources in

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
     static synchronized private String getFilesFileName() {

        String file = ProjectResources.getSettingsDirectory() + System.getProperty("file.separator") + ProjectResources.fileResources;
        return file;
        
    }
    
    
    /**
    Add a project..

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
     static synchronized public Project addProject(Project project) {


         
         
        if (project.getKey() == 0) {
            project.setKey( ProjectResources.projects.size() + 1 );
        }
       
        ProjectResources.projects.addElement(project);

        MiscUtilities.quicksort( ProjectResources.projects, new ProjectComparator() );        
        
        ProjectResources.save();

        return project;
    }

    
    
    
    
    /**
    Add a file to it (project is stored internally)

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
     static synchronized public void addFile(File file) {

        file = ProjectResources.getInitializedFile( file );
        
        Project project = file.getProject();

        Vector files = (Vector)ProjectResources.files.get( project );

        if (files == null) {
            files = new Vector();
            ProjectResources.files.put(project, files);
        }

        files.addElement( file );

        if (getUseMemory() == false) {
            MiscUtilities.quicksort( files, new FileComparator() );
        }

        //nothing I can do to speed up this.
        if (project.hasDirectory( file.getDirectory() ) == false) {
            project.addDirectory( file.getDirectory() );
        }


        //ok... now increase the file  count within that dir...
        Directory dir = project.getDirectory( file.getDirectory() );
        dir.setFileCount( dir.getFileCount() + 1 );

        ProjectResources.save();
        
        
    }


    /**
    Add a group of files to a project.. since this is done in-memory performance
    is improved.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
     static synchronized public void addFiles( File[] files ) {

        MiscUtilities.quicksort( files, new FileComparator() );
        
        ProjectResources.setUseMemory(true);

        //
        for (int i = 0; i < files.length; ++i) {
            ProjectResources.addFile(files[i]);
        }

        
        ProjectResources.setUseMemory(false);

    }
    
    
    /**

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
     static synchronized public void removeFile(File file) {

        
        
        file = ProjectResources.getFile(file);

        Vector files = (Vector)ProjectResources.files.get(file.getProject());
        
        if (files == null) 
            return;
        
        files.removeElement(file);

        Project project = file.getProject();
        Directory dir = project.getDirectory(file.getDirectory());
        dir.setFileCount( dir.getFileCount() - 1 );
        
        //ok.. now if there aren't any more files in this directory you should remove it.
        if (dir.getFileCount() == 0) {
            project.removeDirectory(dir);
        }

        ProjectResources.save();
        
    }


    /**
    Given a project... remove all its files.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
     static synchronized public void removeFiles(Project project) {
        
        

        //remove all the directories underneath the project..
        project.clearDirectories();
        
        ProjectResources.files.remove(project);
        ProjectResources.save();

    }

    /**

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
     static synchronized public void removeProject(Project project) {

        
        
        ProjectResources.files.remove(project);
        
        //remove 
        ProjectResources.projects.removeElement(project);
        
        ProjectResources.save();
        
    }
    
    
    /**
    Print out the current project resources to a PrintStream

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */    
     static synchronized void dump() {

                
        
        Project[] projects = ProjectResources.getProjects();
        
        for(int i = 0; i < projects.length;++i) {
            Logger.log( "DUMP: Project -> " + projects[i].toString(), 200 );

            Directory[] directories = projects[i].getDirectories();
            
            for (int j = 0; j < directories.length; ++j) {
                Logger.log( "\t directory: + " + directories[j].toString() + " has a file count of: " + directories[j].getFileCount(), 200);
            }

            File[] files = ProjectResources.getFiles( projects[i] );

            for(int j = 0; j < files.length;++j) {
                files[j].dump();
            }
            

        }


    }

    /**
    Print out the current known projects

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */    
     static synchronized void dumpProjects() {

        

        Project[] projects = ProjectResources.getProjects();
        
        for(int i = 0; i < projects.length;++i) {
            Logger.log( "ProjectResources.dumpProjects(): Project -> " + projects[i].toString(), 200 );

            Logger.log( "ProjectResources.dumpProjects(): Project ROOT -> " + projects[i].getRoot().toString(), 200 );

            
            Directory[] directories = projects[i].getDirectories();
            
            for (int j = 0; j < directories.length; ++j) {
                Logger.log( "\t ProjectResources.dumpProjects() directory: + " + directories[j].toString() + " has a file count of: " + directories[j].getFileCount(), 200);
            }

        }
        
    }

    /**
    Prints out the current known directories
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */    
     static synchronized void dumpDirectories() {


        
        
        Project[] projects = ProjectResources.getProjects();
        
        for(int i = 0; i < projects.length;++i) {
            
            Directory[] directories = projects[i].getDirectories();
            
            for (int j = 0; j < directories.length; ++j) {
                Logger.log( "\t ProjectResources.dumpProjects() directory: + " + directories[j].toString() + " has a file count of: " + directories[j].getFileCount(), 200);
            }

        }

        
    }

    
    /**
    Given a prototype of <project>.<key> or <file>.<key>.<project>.<key>
    this will return the project key

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
     static synchronized private int getProjectKey(String prototype) {

        return ProjectResources.getKeyFromID("project", prototype);

    }

    
    /**
    Given a prototype of <file>.<key>.<project>.<key>
    this will return the file key

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
     static synchronized private int getFileKey(String prototype) {
        
        return ProjectResources.getKeyFromID("file", prototype);
    }

    
    /**
    Used by getProjectKey and getFileKey.  Returns -1 on failure.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
     static synchronized private int getKeyFromID(String id, String prototype) {
        
        int start = prototype.indexOf(id) + id.length() + 1;
        int end = prototype.indexOf(".", start);
        
        //eol.
        if (end < start)
            end = prototype.length();

        String key = prototype.substring(start, end);  

        return Integer.parseInt(key);
        
    }

    /**
    Given a string... replaces all occurences of "find" with "replacement" in 
    "original"

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized String globalStringReplace(String original, String find, String replacement) {


        StringUtil buffer = new StringUtil( original );
        
        
        int space_location = buffer.toString().indexOf(find);

        while(space_location != -1) {

            int start = space_location;
            int end = space_location + find.length();

            buffer.replace(start, end, replacement);

            //this speed could be improved by starting off where the last string was found...
            //this is why it starts off from space_location.. the length of the string you are finding.. plus 1 

            //space_location = buffer.toString().indexOf(find, space_location + find.length() + 1);
            space_location = buffer.toString().indexOf(find, space_location + find.length() + 1);
      }

      return buffer.toString();

    }


    //bootstrap for testing...
    public static void main(String args[]) {
        
        parse();
        
    }

    /** 
    Add a file to the subscribed base.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public static synchronized void addSubscribedFile( File file ) {

        subscribedFiles.put( file.get(), file );
    }
    

    /** 
    Remove a file to the subscribed base.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public static synchronized void removeSubscribedFile( String file ) {
        //set the subscribed statius to false.

        ((File)subscribedFiles.get( file )).setSubscribed( false );

        subscribedFiles.remove( file );
    }
    
    /** 
    Tests if the files is subscribed
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public static synchronized boolean isSubscribedFile( String file ) {
        return subscribedFiles.containsKey( file );
    }


    /** 
    Dump the subscribed file list.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public static void dumpSubscribedFiles() {

        System.err.println( "-------  Subscribed Files:  -------" );
        
        Enumeration enum = ProjectResources.subscribedFiles.elements();
        while(enum.hasMoreElements() ) {
            System.err.println( ((File)enum.nextElement()).get() );
        }
    }
    
    /**
    Add a listener for notification of ProjectResource events
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public static synchronized void addResourceListener( ResourceListener listener ) {
        listeners.addElement( listener );
    }

    /**
    Remove a listener for notification of ProjectResource events
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public static synchronized void removeResourceListener( ResourceListener listener ) {
        listeners.removeElement( listener );        
    }
    

    /**
    Report to all listeners that a file has been subscribed...

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized void reportFileSubscribed(File file) {
        
        ResourceListener[] listeners = getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            listeners[i].fileSubscribed( file );
        }
        
    }

    /**
    Report to all listeners that a file has been unsubscribed...

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized void reportFileUnSubscribed(File file) {

        ResourceListener[] listeners = getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            listeners[i].fileUnSubscribed( file );
        }
        
    }

    /**
    Get all ResourceListeners
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    private static synchronized ResourceListener[] getListeners() {

        ResourceListener[] listeners = new ResourceListener[ ProjectResources.listeners.size() ];
        ProjectResources.listeners.copyInto(listeners);
        return listeners;
        
    }

}
