package jimporter.searchmethod;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.text.Document;

/**
 * BruteForceClassFinder will search a classpath to find every instance of a
 * named class. The general usage of this class is to set the classpath using
 * the <code>setClassPath</code> method, then use findFullyQualifiedClassName to
 * search for the file. In this version, I'm not using any tricks to find the
 * file more quickly, i.e. there isn't any caching or anything else exciting
 * going on. This isn't for lack of skill - it just isn't really a priority. If
 * those things are really necessary, there isn't a reason why the class
 * couldn't be subclassed to do just that.
 *
 * @author    Matthew Flower
 */
public class BruteForceClassFinder extends TraverseSearchMethod {
    /**
     * Constructor for the BruteForceClassFinder object
     */
    BruteForceClassFinder() {
        super("usebruteforce", "options.jimporter.searchmethod.usebruteforce.label");
    }

    /**
     * The main program for the ClassFinder class. This is used primarily for
     * testing purposes.
     *
     * @param args  The command line arguments
     */
    public static void main(String[] args) {
        BruteForceClassFinder cu = new BruteForceClassFinder();
        cu.setClassPath(System.getProperty("java.class.path", "."));

        if (args.length > 0) {
            System.out.println("Finding " + args[0] + "...");
            List classes = cu.findFullyQualifiedClassName(args[0]);

            Iterator it = classes.iterator();
            while (it.hasNext()) {
                System.out.println(it.next());
            }
        } else {
            System.out.println("Specify a class as a parameter");
        }
    }

    /**
     * Find all of the class names (in fully-qualified form) on the classpath
     * that match the classname indicated in the <code>className</code>
     * parameter.
     *
     *@param className  a <code>String</code> variable indicating the class name
     *      that we are trying to match.
     *@return           A <code>List</code> value containing strings which
     *      contain the fully-qualified form of all the class names that match
     *      the className parameter.
     *@since            empty
     */
    public List findFullyQualifiedClassName(String className) {
        ArrayList matchingClasses = new ArrayList();

        Iterator it = classPath.iterator();
        while (it.hasNext()) {
            String currentPathItem = (String) it.next();
            File currentPathFile = new File(currentPathItem);

            if (currentPathFile.isDirectory()) {
                List classFiles = findClassInFileSystem(className, currentPathFile, currentPathItem);

                if (classFiles != null) {
                    matchingClasses.addAll(classFiles);
                }
            } else {
                List jarClassFiles = findClassInJarOrZip(className, currentPathItem);

                if (jarClassFiles != null) {
                    matchingClasses.addAll(jarClassFiles);
                }
            }
        }
        
        //Remove any duplicate elements
        matchingClasses = new ArrayList(new HashSet(matchingClasses));
        
        //Sort all of the items in the list
        Collections.sort(matchingClasses);

        return matchingClasses;
    }

    /**
     * Given the path of a zip or jar file in the FileSystem, traverse the
     * contents of that file, looking for any file that appears to match the
     * classname of the className parameter. Return a list of all classes that
     * match the name.
     *
     *@param className  a <code>String</code> value containing the className of
     *      the class we'd like to find in the zip file somewhere.
     *@param jarOrZip   a <code>String</code> value containing the absolute path
     *      to the zip file we are going to search through.
     *@return           a <code>List</code> structure containing the absolute
     *      classname (the fully-qualified java version of the class name) of
     *      any class found in the jar file that matches the className
     *      parameter.
     *@since            empty
     *@see              #findClassInFileSystem
     */
    private List findClassInJarOrZip(String className, String jarOrZip) {
        ArrayList classList = new ArrayList();
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(jarOrZip);
        } catch (java.io.IOException e) {
            return null;
        }
        Enumeration zipEntries = zipFile.entries();

        while (zipEntries.hasMoreElements()) {
            ZipEntry currentZipEntry = (ZipEntry) zipEntries.nextElement();

            if (currentZipEntry.getName().endsWith(jarPathSeparator + className + ".class")) {
                classList.add(normalizeJarClassName(currentZipEntry.toString()));
            }
        }

        return classList;
    }

    /**
     * Search for the class in the filesystem.
     *
     *@param className       a <code>String</code> value containing the name of
     *      the class we are looking for.
     *@param currentRoot     The directory we are looking in.
     *@param locationPrefix
     *@return                The list of classes with any new found classes
     *      added on.
     *@since                 empty
     */
    private List findClassInFileSystem(String className, File currentRoot, String locationPrefix) {
        ArrayList classList = new ArrayList();
        File[] fileList = currentRoot.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                classList.addAll(findClassInFileSystem(className, fileList[i], locationPrefix));
            } else if (fileList[i].getAbsolutePath().endsWith(File.separator + className + ".class")) {
                classList.add(normalizeFSClassName(fileList[i].getAbsolutePath(), locationPrefix));
            }
        }
        return classList;
    }
}
