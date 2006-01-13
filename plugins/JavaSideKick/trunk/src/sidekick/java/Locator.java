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

    private static File[] runtimeJars = getRuntimeJars();
    private static List runtimeClassNames = getRuntimeClassNames();

    /**
     * Not instantiable
     */
    private Locator() {}

    
    /**
     * Returns a list of all jar files in java.home/lib and java.home/ext.    
     */
    public static File[] getRuntimeJars() {
        if ( runtimeJars != null )
            return runtimeJars;

        // get runtime jars based on java.home setting
        String javaHome = System.getProperty( "java.home" );
        File java_lib = new File( javaHome + "/lib" );
        File java_ext = new File( javaHome + "/lib/ext" );
        File[] libs = java_lib.listFiles( new FileFilter() {
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
        // add extension jars first, they should override standard jars
        List list = new ArrayList();
        list.addAll( Arrays.asList( exts ) );
        list.addAll( Arrays.asList( libs ) );
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

}

