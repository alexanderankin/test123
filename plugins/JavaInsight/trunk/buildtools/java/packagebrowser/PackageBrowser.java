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

package buildtools.java.packagebrowser;


import buildtools.FileUtils;
import buildtools.JavaUtils;
import buildtools.MiscUtils;
import buildtools.StaticLogger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

public class PackageBrowser {
    
    public static final String PACKAGE_SEPARATOR = ".";

    private static boolean      parsed      = false;
    private static Hashtable    packages    = new Hashtable();
    private static Hashtable    classpath   = new Hashtable();
    
    
    /**
     * Return all known packages
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public static JavaPackage[] getPackages() {

        parse();
        JavaPackage[] pkgs = new JavaPackage[packages.size()];
        
        Enumeration enum = packages.elements();
        int element = 0;
        while(enum.hasMoreElements() ) {
            pkgs[element] = (JavaPackage)enum.nextElement();
            ++element;
        }

        return pkgs;
    }

    public static ClasspathEntry[] getPackagesAsClasspath() {
        
        JavaPackage[] packages = getPackages();
        
        

        for (int i = 0; i < packages.length; ++i) {
            
            String source = packages[i].getSource();


            //require that this source has a classpath entry.
            if ( ! classpath.containsKey( source ) ) {
                classpath.put( source, new ClasspathEntry( source ) );
            }


            ClasspathEntry entry = (ClasspathEntry)classpath.get( source );
            
            if ( ! entry.containsJavaPackage( packages[i] ) ) {
                entry.addJavaPackage( packages[i] );
            }



        }
        
        
        //now convert the hashtable into an array 


        ClasspathEntry[] classpathEntries = new ClasspathEntry[classpath.size()];
        
        Enumeration enum = classpath.elements();
        int element = 0;
        while(enum.hasMoreElements() ) {
            classpathEntries[element] = (ClasspathEntry)enum.nextElement();
            ++element;
        }

        return classpathEntries;


        
    }
    
    /**
     * Parse out the current classpath and get all packages and classes.
     * 
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public static void parse() {
        
        if (parsed == false) {
        
            String[] classpath = JavaUtils.getClasspath();
            for (int i = 0; i < classpath.length; ++i) {
                
                //StaticLogger.log( classpath[i] );
                File entry = new File( classpath[i] );
                if ( entry.isFile() ) {
                    parseJAR(entry);
                }
                
            }
        
            parsed = true;
        }
    }


    /**
     * Parse out a JAR file and added it to the known packages
     * 
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    private static void parseJAR(File jar) {


        try {
            //StaticLogger.log( "archive: " + jar.getAbsolutePath() );
            ZipFile archive = new ZipFile( jar.getAbsolutePath() );

            Enumeration elements = archive.entries();
    
			String fileSep = "/";
            while( elements.hasMoreElements() ) {
                ZipEntry entry = (ZipEntry)elements.nextElement();


                /*
                if (jar.getName().equals("classes.zip") ) {
                    StaticLogger.log( entry.getName() );
                }
                */


                
                String ext = FileUtils.getExtension( entry.getName() );
    
                if ( ext != null && ext.equals("class") ) {
    
                    //if the entry is an inner class... ignore it.
                    if (entry.getName().indexOf("$") != -1) {
                        continue;
                    }
                    
                    String pkg = getPackage( entry.getName(), fileSep );
    
                    if (pkg != null) {
                        String className = getFullyQualifiedClassName( entry.getName(), fileSep );
    
                        getJavaPackage( pkg, jar.getAbsolutePath() ).addClass( new JavaClass( className, jar.getAbsolutePath() ) );
                    }
    
                    //StaticLogger.log( getPackage( entry.getName() ) );
                }
                


            }


            
            

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


    }

    /**
     * Given a package name, return its JavaPackage from the stack or create a 
     * new one if necessary.
     * 
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    private static JavaPackage getJavaPackage(String packageName, String source) {
        JavaPackage entry = (JavaPackage)packages.get(packageName);
        
        if (entry == null) {
            entry = new JavaPackage(packageName, source);
            packages.put(packageName, entry);
        }
        
        return entry;
    }
    
    /**
     * Given a classname... get its package. or null if it is not part of a package
     * 
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    private static String getPackage(String classname, String fileSep) {
        
        String ext = FileUtils.getExtension(classname);
        if ( ext != null && ext.equals("class") ) {

            int start = 0;
            int end = classname.lastIndexOf(fileSep);

            if (end == -1) {
                return null;
            }

            String rawpackage = classname.substring( start, end );
            
            
            return MiscUtils.globalStringReplace( rawpackage, fileSep, PACKAGE_SEPARATOR );
            
        } else {
            throw new IllegalArgumentException("this method only excepts .class files");
        }
        
    }

    /**
     * Given a file, return the full class path.
     *
     * EX:  org/apache/jetspeed/Test.class == org.apache.jetspeed.Test
     * 
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    private static String getFullyQualifiedClassName(String file, String fileSep) {

        int start = 0;
        int end = file.lastIndexOf(".class");
        
        if (end != -1) {
            file = file.substring( start, end );
        }
        return MiscUtils.globalStringReplace( file, 
                                              fileSep, 
                                              PACKAGE_SEPARATOR );
    }
    
    /**
     * dump all packages and classes
     * 
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public static void dump() {

        JavaPackage[] packages = getPackages();
        for (int i = 0;i < packages.length; ++i) {
            StaticLogger.log("PACKAGE -> " + packages[i].getName());
            packages[i].dump();

        }
        
    }
    
}

