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

//the standard swing stuff
import javax.swing.*;
import javax.swing.tree.*;

//awt stuff for swing support
import java.awt.*;


import java.awt.event.*;  // required for KeyListener and ActionListener
import javax.swing.event.*;

//used for File manipulation
import java.util.Vector;




/**

Prompts the user with a dialog asking what directory, files types they want 
to import.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>

*/

public class ImportFiles {
    
 



    /**
    A list of file extensions to import.  These should always be included and if jEdit knows of
    any more include those as well.
    */
    private final static String[] FILES_EXT = { "java",
                                                "xml",
                                                "dtd",
                                                "txt",
                                                "c++",
                                                "c",
                                                "cpp",
                                                "h",
                                                "pl",
                                                "pm",
                                                "sh",
                                                "asm",
                                                "asp",
                                                "jsp",
                                                "jsc",
                                                "html",
                                                "htm",
                                                "properties",
                                                "props",
                                                "sql",
                                                "conf",
                                                "css",
                                                "js",
                                                "vbs",
                                                "bat" };

    /**
    A list of files to import.
    */                                                
    private final static String[] FILES = {
                                          "README",
                                          "LICENSE",
                                          "INSTALL",
                                          "CHANGELOG",
                                          "Makefile",
                                          "makefile.jmk"
                                          };
                                              
    JButton importFiles = new JButton("Import");
    JButton cancel      = new JButton("Cancel");
                                              
                                              

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>

    @param project The name of the project.  This is needed when creating any 
                   files.
    */
    public static File[] getFiles(Project project, Container owner) {
        //ImportFiles importDialog = new ImportFiles(owner, "Import files...");
        //ImportFiles.addActionListener(importDialog);
        //importDialog.show();


        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = chooser.showOpenDialog(owner);

        
        if (returnVal != JFileChooser.APPROVE_OPTION) 
            return new File[0];


        //it appears that multiple selection mode doesn't work right.
        //come back to this in the future.

        //java.io.File[] files = new java.io.File[1];
        //files[0] = chooser.getSelectedFile();
        java.io.File file = chooser.getSelectedFile();
        if (file.isDirectory() == false) 
            return new File[0];
            
        String directory = file.getPath();
        


        //now recurse through "directory" and get all files...

        owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // Set Default Cursor
        owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        

        return getFiles(project, directory);
    }

    
    /**
    Given a directory, will recursive into all subdirectories and return
    all known files.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    public static File[] getFiles(Project project, String directory) {

        
        //
        Vector files = new Vector();
        
        //get the 
        java.io.File currentDir = new java.io.File(directory);
        String[] unknownFiles = currentDir.list();
        if (unknownFiles == null) {
            return new File[0];
        }
        
        for (int i = 0;i < unknownFiles.length;++i) {
            String currentFileName = directory + System.getProperty("file.separator") + unknownFiles[i];
            java.io.File currentFile = new java.io.File(currentFileName);

            if (currentFile.isDirectory()) {


                //ignore all CVS directories...
                if ( currentFile.getName().equals("CVS") ) {
                    continue;
                }

                
                //ok... transverse into this directory and get all the files... then combine
                //them with the current list.

                File[] fetchFiles = getFiles(project, currentFileName);
                files = blendFilesToVector( files, fetchFiles);
                
            } else {
                //ok... add the file
    
                File add = new File(project, currentFile.getAbsolutePath());

                if ( isValidFile( add ) )
                    files.addElement( add );

            }
        }

        //ok... move the Vector into the files list...

        File[] foundFiles = new File[files.size()];
        files.copyInto(foundFiles);
        
        
        
        return foundFiles;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    private static boolean isValidFile(File file) {
        String fileName = file.get();
        int start = fileName.lastIndexOf(".") + 1;
        int end = fileName.length();
        

        //make sure that it actually has an extension
        if (end <= start) 
            return false;
            
        String extension = fileName.substring(start,end);
        
        //ok.. now that we have the "extension" go through the current know
        //excepted extensions and determine if this one is OK.
        
        for (int i = 0; i < FILES_EXT.length; ++i) {
            if (FILES_EXT[i].equals(extension)) 
                return true;
        }

        for (int i = 0; i < FILES.length; ++i) {
            if (FILES[i].equals(file.getFileName())) 
                return true;
        }


        return false;
        
    }
    

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    private static Vector blendFilesToVector(Vector v, File[] files) {
        
        for (int i = 0; i < files.length; ++i) {
            v.addElement(files[i]);
        }
        
        return v;
    }
    
    /**
    Given a path, return an array of directory names.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    private static String[] getDirectories(String directory) {
        
        java.io.File file = new java.io.File(directory);
        
        //if you pass it something that isn't a 
        if (file.isDirectory() == false)
            return new String[0];
            
            
        return new String[0];
    }
    

    
}


