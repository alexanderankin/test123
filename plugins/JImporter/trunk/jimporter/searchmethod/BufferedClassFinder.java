/*
 *  BufferedClassFinder.java  
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.searchmethod;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;

// This class is not in use yet.  Currently it is fatally flawed.  The use of
// a Hashmap is not going to work, because we need to be able to do partial 
// matches of strings in an efficient fashion.  Not being able to do partial 
// matches means that the only way classes will match is if you put in their
// entire name.  This isn't going to work too well.
// 
// I've started to think about how to solve the problem.  I don't have a handy 
// algorithm laying around ready for this one, so my first bet will probably be
// to look for a partial matching algorithm within jEdit.  From there I will look
// around in other GPL projects to see what I can lift.
// 
// If all of that fails, I'll have to write one myself.  (Sigh)  I was thinking a
// nice naive algorithm would be to have a sorted ArrayList with an external index into 
// it.  You'll still have to traverse it linearly, but the external index will
// definitely speed up the process.

import jimporter.classpath.Classpath;
import jimporter.classpath.ClasspathChangeListener;

/**
 * The BufferedClassFinder attempts to find a class by looking in a cache of 
 * classes that was constructed when the class is constructed.
 *
 * The benefit of this class over using the "Brute Force" method of finding
 * classes is that we can eliminate the "Search" button from the dialog box.
 * instead we can have the names complete as the user types.  The downside to 
 * this method is that we need time to cache all of the class names and that it
 * takes memory to do so.
 *
 * @author Matthew Flower
 */
public class BufferedClassFinder extends TraverseSearchMethod implements ClasspathChangeListener {
    private HashMap classBuffer;    
    
    /**
     * Constructor for the BufferedClassFinder object
     */
    BufferedClassFinder() {
        super("usebuffered", "options.jimporter.searchmethod.usebuffered.label");
        Classpath.addClasspathChangeListener(this);
    }
    
    /**
     * Notification that the classpath has changed, be sure to clear out the cache.
     */
    public void classpathChanged(Classpath oldClasspath, Classpath newClasspath) {
        //The classpath has changed, null it out.
        classBuffer = null;
    }

    /**
     * Traverse all of the files in the path indicated by the currentRoot parameter.
     *
     * @param classFiles a <code>HashMap</code> containing all of the class files
     * that we've already found.
     * @param currentRoot the current root directory we are going to standard searching
     * at.
     * @param locationPrefix a <code>String</code> value that we are going to 
     * prepend to any filenames we find.
     */
    public void traverseFileSystem(HashMap classFiles, File currentRoot, String locationPrefix) {
        File[] fileList = currentRoot.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                traverseFileSystem(classFiles, fileList[i], locationPrefix);
            } else if (fileList[i].getAbsolutePath().endsWith(".class")) {
                String className = fileList[i].getName();
                className = className.substring(0, className.length()-6);
                addFile(normalizeFSClassName(fileList[i].getAbsolutePath(), locationPrefix), className);                 
            }
        }
    }
    
    /**
     * Given a classpath element, delegate traversal to the appropriate method
     * and add any found files to the <code>classFiles</code> hashmap.
     *
     * @param classFiles a <code>HashMap</code> object which contains any classes 
     * that have been found.
     * @param jarOrZip a <code>String</code> containing a classpath element.
     */
    public void traverseJarOrZip(HashMap classFiles, String jarOrZip) {
        ZipFile zipFile;
        
        //Try to open the jar or zip
        try {
            zipFile = new ZipFile(jarOrZip);
        } catch (java.io.IOException e) {
            return;
        }
        
        Enumeration zipEntries = zipFile.entries();
        
        while (zipEntries.hasMoreElements()) {
            ZipEntry currentZipEntry = (ZipEntry)zipEntries.nextElement();
            
            String className = currentZipEntry.getName().substring(
                currentZipEntry.getName().lastIndexOf(jarPathSeparator)+1);
                
            //Check to make sure this is a class file
            if (!className.endsWith(".class")) {
                continue;
            }
            
            //Finish cleaning up the name
            className = className.substring(0, className.length()-6);
            
            //Add the classname to the cache
            classFiles.put(className, normalizeJarClassName(currentZipEntry.toString()));
        }
    }

    /**
     * Add a file to the class buffer.
     *
     * @param className    The feature to be added to the File attribute
     * @param fqClassName  The feature to be added to the File attribute
     */
    public void addFile(String className, String fqClassName) {
        if (classBuffer.containsKey(className)) {
            Object currentValue = classBuffer.get(className);
            if (currentValue instanceof ArrayList) {
                //There is already a value, but it is an array.  Add to the array
                ((ArrayList)currentValue).add(className);
            } else {
                //Create a new array so we can hold two values
                ArrayList newValue = new ArrayList();
                newValue.add(currentValue);
                newValue.add(fqClassName);
            
                //Remove the old single value and replace with our ArrayList
                classBuffer.remove(className);
                classBuffer.put(className, newValue);
            }
        } else {
            classBuffer.put(className, fqClassName);
        }
    }

    /**
     * Find all fully qualified classnames matching the short name supplied in
     * the parameter.
     *
     *@param className  Description of Parameter
     *@return           Description of the Returned Value
     *@since            empty
     */
    public List findFullyQualifiedClassName(String className) {
        HashMap hashMap = getClassBuffer();

        Object matches = hashMap.get(className);
        if (matches == null) {
            return new ArrayList();
        } else if (matches instanceof ArrayList) {
            return (ArrayList) matches;
        } else {
            ArrayList matchList = new ArrayList();
            matchList.add(matches);
            return matchList;
        }
    }

    /**
     * Get the class buffer.
     *
     * @return The class buffer
     */
    private HashMap getClassBuffer() {
        if (classBuffer == null) {
            createClassBuffer();
        }

        return classBuffer;
    }

    /**
     * Create a class buffer
     */
    private void createClassBuffer() {
        HashMap classList = new HashMap();

        Iterator it = classPath.iterator();
        while (it.hasNext()) {
            String currentPathItem = (String) it.next();
            File currentPathFile = new File(currentPathItem);

            if (currentPathFile.isDirectory()) {
                traverseFileSystem(classList, currentPathFile, currentPathItem);
            } else {
                traverseJarOrZip(classList, currentPathItem);
            }

        }

        this.classBuffer = classList;
    }
}
