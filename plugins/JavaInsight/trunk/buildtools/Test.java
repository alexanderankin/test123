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


package buildtools;


import buildtools.java.packagebrowser.PackageBrowser;
import java.io.File;
import java.lang.StringBuffer;


/**
A test suite for build tools.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
public class Test{

    static String file1 = "/projects/jakarta/jakarta-tomcat/src/share/org/apache/tomcat/core/ServletConfigImpl.java";
    static String file2 = "/usr/local/space/projects/BuildTools/buildtools/Test.java";
    static String file3 = "/usr/local/space/projects/ViewJavadoc/viewjavadoc/ViewJavadoc.java";
    
    

    public static void main(String[] args) {

        testGlobalStringReplace();
        //testClasspathEntry();
        //testPackageBrowser();
        //localTest();
        //testUtils(); 
        //testChangeMonitor();
        //testCacheTools();
    }

    public static void testClasspathEntry() {
        //PackageBrowser.getPackagesAsClasspath();
    }


    public static void testGlobalStringReplace() {
        
        
        String before = "org/test/";
        
        StaticLogger.log("before == " + before);


        String after = MiscUtils.globalStringReplace( before, "/", "." );
        
        StaticLogger.log("after == " + after);
        

    }
    
    public static void testPackageBrowser() {
        PackageBrowser.parse();
        PackageBrowser.dump();
    }


    //perform a debug of current code
    public static void localTest() {
        String extension = FileUtils.getExtension("test");
        
        StaticLogger.log(extension);
    }
    
    public static void testCacheTools() {
        
        CacheStore.getCacheDirectory( new File("/tmp/ViewJavadoc") , new File(file1) );
        
    }

    public static void testChangeMonitor() {
        
        String[] exts = { "java" };

        FileChangeController controller = new FileChangeController();



        FileChangeMonitor monitor = controller.getMonitor( "/projects/test", exts );


        try {
            while(true) {
            
                String[] changed = monitor.getChangedFiles();
    
                System.out.println("Total number of files changed: " + changed.length);
                for (int i = 0; i < changed.length; ++i) {
                    System.out.println("\t" + changed[i]);
                }
                
                monitor.check();
                
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void testUtils() {

 
        try {

            String packagename = JavaUtils.getPackageName( file1 );
            System.out.println("package is: " + packagename + " should be \"org.apache.tomcat.core\"" );

            packagename = JavaUtils.getPackageName( file2 );
            System.out.println("package is: " + packagename + " should be null");

            packagename = JavaUtils.getPackageName( file3 );
            System.out.println("package for " + file3 + ":  " + packagename + " should be \"viewjavadoc\"");



            //find the root dirs of these files.
            System.out.println("root dir of " + file1);
            String dir = JavaUtils.getBaseDirectory(file1);

            System.out.println("\t" + dir );
            
            System.out.println("root dir of " + file2);
            System.out.println("\t" + JavaUtils.getBaseDirectory(file2) );

            String exts[] = { "java" };
            
            String files[] = FileUtils.getFilesFromExtension(dir, exts );
            
            System.out.println("Found " + files.length + " java files found under " + dir);


            
            String packages[] = JavaUtils.getPackageNames( file3 );
            System.out.println("Found " + packages.length + " java packages found under " + dir);
            for (int i = 0; i < packages.length; ++i) {
                System.out.println("\tpackage: " + packages[i]);
            }

            String classname = JavaUtils.getFullClassname( file3 );            
            System.out.println("classname for: " + file3);
            System.out.println("\t" + classname);
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }


        
    }

}
