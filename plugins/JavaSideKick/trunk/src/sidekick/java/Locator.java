package sidekick.java;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.*;
import java.util.jar.*;


/**
 * The Locator is a utility class which is used to find classes loaded
 * in the runtime environment.
 *
 */
public final class Locator {

    private static File[] classpathJars = getClassPathJars();
    private static List classpathClassNames = getClassPathClassNames();
    private static File[] runtimeJars = getRuntimeJars();
    private static List runtimeClassNames = getRuntimeClassNames();

    /**
     * Not instantiable
     */
    private Locator() {}

        
    public static File[] getClassPathJars() {
        if (classpathJars != null)
            return classpathJars;
        
        String classpath = System.getProperty("java.class.path");
        if (classpath == null || classpath.length() == 0)
            return null;
        String path_sep = System.getProperty("path.separator");
        String[] path_jars = classpath.split(path_sep);
        File[] jars = new File[path_jars.length];
        for(int i = 0; i < path_jars.length; i++) {
            jars[i] = new File(path_jars[i]);
        }
        return jars;       
    }
    
    
    public static List getClassPathClassNames() {
        File[] jars = getClassPathJars();
        List names = new ArrayList();
        for ( int i = 0; i < jars.length; i++ ) {
            File jar = jars[ i ];
            try {
                JarFile jar_file = new JarFile( jar );
                Enumeration entries = jar_file.entries();
                while ( entries.hasMoreElements() ) {
                    JarEntry entry = ( JarEntry ) entries.nextElement();
                    String classname = entry.getName();
                    if ( classname.endsWith( ".class" ) )
                        classname = classname.substring( 0, classname.lastIndexOf( "." ) );
                    names.add( classname );
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return names;
    }

        
    /**
     * Returns a list of all jar files in the classpath, java.home/lib, 
     * java.ext.dirs and java.endorsed.dirs    
     */
    public static File[] getRuntimeJars() {
        if ( runtimeJars != null )
            return runtimeJars;
        
        // get runtime jars based on java.home setting
        String javaHome = System.getProperty( "java.home" );
        File java_lib = new File( javaHome + "/lib" );
        File java_ext = new File( System.getProperty("java.ext.dirs") );
        File java_endorsed = new File(System.getProperty("java.endorsed.dirs"));
        File[] libs = java_lib.listFiles( new FileFilter() {
                    public boolean accept( File pathname ) {
                        return pathname.getName().endsWith( ".jar" );
                    }
                }
                                        );
        File[] endorsed = java_endorsed.listFiles( new FileFilter() {
                    public boolean accept( File pathname ) {
                        return pathname.getName().endsWith( ".jar" );
                    }
                }
                                        );
        File[] exts = java_lib.listFiles( new FileFilter() {
                    public boolean accept( File pathname ) {
                        return pathname.getName().endsWith( ".jar" );
                    }
                }
                                        );
        // add endorsed jars first, they should override standard jars
        List list = new ArrayList();
        if (endorsed != null && endorsed.length > 0)
            list.addAll( Arrays.asList( endorsed ) );
        if (libs != null && libs.length > 0)
            list.addAll( Arrays.asList( libs ) );
        if (exts != null && exts.length > 0)
            list.addAll( Arrays.asList( exts ) );
        runtimeJars = ( File[] ) list.toArray( new File[] {} );
        return runtimeJars;
    }

    public static List getRuntimeClassNames() {
        File[] jars = getRuntimeJars();
        List names = new ArrayList();
        for ( int i = 0; i < jars.length; i++ ) {
            File jar = jars[ i ];
            try {
                JarFile jar_file = new JarFile( jar );
                Enumeration entries = jar_file.entries();
                while ( entries.hasMoreElements() ) {
                    JarEntry entry = ( JarEntry ) entries.nextElement();
                    String classname = entry.getName();
                    if ( classname.startsWith( "java/" ) ||
                            classname.startsWith( "javax/" ) ||
                            classname.startsWith( "org/omg/" ) ||
                            classname.startsWith( "org/ietf/" ) ||
                            classname.startsWith( "org/w3c/" ) ||
                            classname.startsWith( "org/xml/" ) ) {
                        if ( classname.endsWith( ".class" ) )
                            classname = classname.substring( 0, classname.lastIndexOf( "." ) );
                        names.add( classname );
                    }
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return names;
    }

    /**
     * @param name class name minus the package part, e.g. "String" in "java.lang.String".    
     */
    public static String getRuntimeClassName( String name ) {
        for ( Iterator it = runtimeClassNames.iterator(); it.hasNext(); ) {
            String fullClassName = ( String ) it.next();
            int index = fullClassName.lastIndexOf( "/" ) + 1;
            String className = fullClassName.substring( index );
            if ( className.equals( name ) ) {
                fullClassName = fullClassName.replaceAll("/", ".");
                return fullClassName;
            }
        }
        return null;
    }


	/**

	 * @param packageName package name with possibly a part of a classname, e.g.

	 * "javax.swing.tree.DefaultMut"

     * @return a list of all class names that match.  The list may be empty, but 
     * won't be null.
	 */

    public static List getRuntimeClasses(String packageName) {
        List list = new ArrayList();
        if (packageName == null || packageName.length() == 0)
            return list;
        String name = packageName.replaceAll("[.]", "/");
        for ( Iterator it = runtimeClassNames.iterator(); it.hasNext(); ) {
            String fullClassName = ( String ) it.next();
            if (fullClassName.startsWith(name) && fullClassName.indexOf("$") < 0) {
                list.add(fullClassName.substring(fullClassName.lastIndexOf("/") + 1));
            }
        }
        return list;
    }

    
    /**
     * @param name class name minus the package part, e.g. "String" in "java.lang.String".    
     */
    public static String getClassPathClassName( String name ) {
        for ( Iterator it = runtimeClassNames.iterator(); it.hasNext(); ) {
            String fullClassName = ( String ) it.next();
            int index = fullClassName.lastIndexOf( "/" ) + 1;
            String className = fullClassName.substring( index );
            if ( className.equals( name ) ) {
                fullClassName = fullClassName.replaceAll("/", ".");
                return fullClassName;
            }
        }
        return null;
    }


	/**

	 * @param packageName package name with possibly a part of a classname, e.g.

	 * "javax.swing.tree.DefaultMut"

     * @return a list of all class names that match.  The list may be empty, but 
     * won't be null.
	 */

    public static List getClassPathClasses(String packageName) {
        List list = new ArrayList();
        if (packageName == null || packageName.length() == 0)
            return list;
        String name = packageName.replaceAll("[.]", "/");
        for ( Iterator it = runtimeClassNames.iterator(); it.hasNext(); ) {
            String fullClassName = ( String ) it.next();
            if (fullClassName.startsWith(name) && fullClassName.indexOf("$") < 0) {
                list.add(fullClassName.substring(fullClassName.lastIndexOf("/") + 1));
            }
        }
        return list;
    }
}

