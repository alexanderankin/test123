/*
 * JavaUtils.java - miscelleanous utilities for java files
 * (c) 1999, 2000 Kevin A. Burton
 *
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

package buildtools;

import java.io.*;
import java.util.*;

/**
 *  Description of the Class 
 *
 *  @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 */
public class JavaUtils {
    /**
     *  The constructor is private. Only static methods are used in this
     *  class.
     */
    private JavaUtils() { }


    /**
     *  Given a filename, finds the root folder, and then determines what 
     *  java packages exists throughout the hierarchy.
     *  
     *  A package is identified by determining files with a .java file in 
     *  them and if they have one, makes sure its "package" reference is 
     *  correct.
     *
     *  @exception  IOException  if the file is not readable or does not exist 
     */
    public static String[] getPackageNames(String javafilename)
            throws IOException
    {
        Vector packages = new Vector();
        File basedir = new File(getBaseDirectory(javafilename));
        return getPackageNames(basedir);
    }


    /**
     *  Given a directory, finds the root folder, and then determines what 
     *  java packages exists throughout the hierarchy.
     *  
     *  A package is identified by determining files with a .java file in 
     *  them and if they have one, makes sure its "package" reference is 
     *  correct. 
     *
     *  @exception  IOException  if the file is not readable or does not exist 
     */
    public static String[] getPackageNames(File directory) throws IOException {
        Vector packages = new Vector();
        boolean processed = false;
        String[] unknownFiles = directory.list();

        if (unknownFiles == null) {
            return new String[0];
        }

        for (int i = 0; i < unknownFiles.length; ++i) {
            String currentFileName = directory + System.getProperty("file.separator") + unknownFiles[i];
            java.io.File currentFile = new java.io.File(currentFileName);
            if (currentFile.isDirectory()) {
                // ignore all CVS directories...
                if (currentFile.getName().equals("CVS")) {
                    continue;
                }
                // ok... transverse into this directory and get all the files...
                // then combine them with the current list.
                String[] morepackages = getPackageNames(currentFile);
                packages = blendFilesToVector(packages, morepackages);
            }
            else if (!processed) {
                // ok... add the file
                String add = currentFile.getAbsolutePath();
                if (add.indexOf(".java") > 0) {
                    // TODO: debug messages
                    System.out.println(add);
                    String packagename = getPackageName(add);
                    if (packagename != null) {
                        packages.addElement(packagename);
                        processed = true;
                    }
                }
            }
        }

        // ok... move the Vector into the files list...
        String[] found = new String[packages.size()];
        packages.copyInto(found);
        return found;
    }


    /**
     *  Get the current classpath as an array of strings. 
     */
    public static String[] getClasspath() {
        Vector v = new Vector();
        StringTokenizer tokenizer = 
            new StringTokenizer(System.getProperty("java.class.path"), 
            System.getProperty("path.separator"));

        while (tokenizer.hasMoreElements()) {
            v.addElement(tokenizer.nextElement());
        }

        String[] classpath = new String[v.size()];
        v.copyInto(classpath);
        return classpath;
    }


    /**
     *  Given a filename return its package name. This is used when 
     *  compiling a package. Basically it looks for the first instance of 
     *  "package" in the file and returns this. The provided file must be a 
     *  java src file. This is not checked!
     *
     *  @return     null, if no package or the package name
     *  @exception  IOException  if the file is not readable or does not exist 
     */
    public static String getPackageName(String fileName) throws IOException {
        FileReader fileRdr = new FileReader(fileName);
        try {
            StreamTokenizer stok = new StreamTokenizer(fileRdr);
            // set tokenizer to skip comments
            stok.commentChar('*');
            stok.slashStarComments(true);
            stok.slashSlashComments(true);

            while (stok.nextToken() != StreamTokenizer.TT_EOF) {
                if (stok.sval == null) {
                    continue;
                }
                if (stok.sval.equals("package")) {
                    stok.nextToken();
                    fileRdr.close();
                    return stok.sval;
                }
            }
            fileRdr.close();
            return null;
        }
        finally {
            fileRdr.close();
        }
    }


    /**
     *  Given a java file name, finds its package name and then returns its 
     *  base directory.
     *
     *  @exception  IOException  if the file is not readable or does not exist 
     */
    public static String getBaseDirectory(String filename) throws IOException {
        String packagename = getPackageName(filename);
        String dirname = filename.substring(0, filename.lastIndexOf(
            System.getProperty("file.separator")));

        if (packagename == null) {
            return dirname;
        }

        String javadir = replaceAll(packagename, ".",
            System.getProperty("file.separator"));

        return dirname.substring(0, dirname.lastIndexOf(javadir) - 1);
    }

    
    /**
     *  replaces all occurences of "find" with "replacement" in "original".
     */
    public static String replaceAll(String original, 
                                    String find, 
                                    String replacement)
    {
        StringBuffer buffer = new StringBuffer(original);
        int location = buffer.toString().indexOf(find);
        while (location != -1) {
            buffer.replace(location, location + find.length(), replacement);
            location = buffer.toString().indexOf(find, location + find.length() + 1);
        }
        return buffer.toString();
    }
    

    /**
     *  Returns the location of "tools.jar".
     */
    public static String getTools() {
        String separator = System.getProperty("file.separator");
        return System.getProperty("java.home") + separator + "lib" 
            + separator + "tools.jar";
    }


    public static String getFullClassname(String javafile) throws Exception {
        String packagename = getPackageName(javafile);
        String classname = javafile.substring(javafile.lastIndexOf(
            System.getProperty("file.separator")) + 1, 
            javafile.lastIndexOf("."));
        return packagename + "." + classname;
    }


    /**
     *  Given a string entry, make sure it is in the classpath. If it isn't 
     *  then add it. 
     *
     *  @return The updated classpath 
     */
    public static String requireEntryInClasspath(String entry) {
        StringBuffer classpath = new StringBuffer(
            System.getProperty("java.class.path"));
        if (classpath.toString().indexOf(entry) == -1) {
            classpath.append(System.getProperty("path.separator") + entry);
            // FIXME:  This doesn't work under JDK 1.1 because
            // System.setProperty isn't available
            //System.setProperty( "java.class.path", classpath.toString() );
        }
        return classpath.toString();
    }


    /**
     *  Given a Vector, make sure that it has all the entries in the string 
     *  array but do not add duplicate entries. 
     */
    private static Vector blendFilesToVector(Vector v, String[] files) {
        for (int i = 0; i < files.length; ++i) {
            v.addElement(files[i]);
        }
        return v;
    }
}

