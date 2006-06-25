package sidekick.java.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.*;
import java.util.jar.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.FilenameFilter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;


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
        if ( classpathJars != null )
            return classpathJars;

        String classpath = System.getProperty( "java.class.path" );
        if ( classpath == null || classpath.length() == 0 )
            return null;
        String path_sep = System.getProperty( "path.separator" );
        String[] path_jars = classpath.split( path_sep );
        File[] jars = new File[ path_jars.length ];
        for ( int i = 0; i < path_jars.length; i++ ) {
            jars[ i ] = new File( path_jars[ i ] );
        }
        return jars;
    }

    /**
     * @return a list of class names of all classes in all jars in the classpath.
     */
    public static List getClassPathClassNames() {
        File[] jars = getClassPathJars();
        List allnames = new ArrayList();
        for ( int i = 0; i < jars.length; i++ ) {
            File jar = jars[ i ];
            List names = getJarClassNames( jar );
            if ( names != null ) {
                allnames.addAll( names );
            }
        }
        return allnames;
    }

    /**
     * @return a list of classnames contained in the given jar.    
     */
    private static List getJarClassNames( File jar ) {
        List names = new ArrayList();
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
        File java_ext = new File( System.getProperty( "java.ext.dirs" ) );
        File java_endorsed = new File( System.getProperty( "java.endorsed.dirs" ) );
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
        if ( endorsed != null && endorsed.length > 0 )
            list.addAll( Arrays.asList( endorsed ) );
        if ( libs != null && libs.length > 0 )
            list.addAll( Arrays.asList( libs ) );
        if ( exts != null && exts.length > 0 )
            list.addAll( Arrays.asList( exts ) );
        runtimeJars = ( File[] ) list.toArray( new File[] {} );
        return runtimeJars;
    }

    /**
     * @return a list of class names of public classes provided by the java runtime.
     */
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
                fullClassName = fullClassName.replaceAll( "/", "." );
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

    public static List getRuntimeClasses( String packageName ) {
        List list = new ArrayList();
        if ( packageName == null || packageName.length() == 0 )
            return list;
        String name = packageName.replaceAll( "[.]", "/" );
        for ( Iterator it = runtimeClassNames.iterator(); it.hasNext(); ) {
            String fullClassName = ( String ) it.next();
            if ( fullClassName.startsWith( name ) && fullClassName.indexOf( "$" ) < 0 ) {
                list.add( fullClassName.substring( fullClassName.lastIndexOf( "/" ) + 1 ) );
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
                fullClassName = fullClassName.replaceAll( "/", "." );
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
    public static List getClassPathClasses( String packageName ) {
        List list = new ArrayList();
        if ( packageName == null || packageName.length() == 0 )
            return list;
        String name = packageName.replaceAll( "[.]", "/" );
        for ( Iterator it = runtimeClassNames.iterator(); it.hasNext(); ) {
            String fullClassName = ( String ) it.next();
            if ( fullClassName.startsWith( name ) && fullClassName.indexOf( "$" ) < 0 ) {
                list.add( fullClassName.substring( fullClassName.lastIndexOf( "/" ) + 1 ) );
            }
        }
        return list;
    }

    /**
     * Get a list of class files from the given paths.  
     * @param path a list of file paths separated by the platform path separator,
     * that is, a path that could be used as a classpath.
     * @return a consolidated List<String> of classes found in the given paths.
     */
    public static List getClassesForPath( String path ) {
        String pathSep = System.getProperty( "path.separator" );
        String[] paths = path.split( pathSep );
        // paths can be either jars, zips, directories, or individual classes
        // directories can contain individual classes.
        List allnames = new ArrayList();
        for (int i = 0; i < paths.length; i++) {
            path = paths[i];
            File f = new File(path);
            // check for jar or zip
            if (path.toLowerCase().endsWith(".jar") || path.toLowerCase().endsWith(".zip")){
                List names = getJarClassNames(f);
                if (names != null) {
                    allnames.addAll(names);   
                }
            }
            // check for individual class
            else if (path.toLowerCase().endsWith(".class")) {
                allnames.add(f.getName().substring(0, f.getName().lastIndexOf(".")));   
            }
            // check for directories
            else if (f.isDirectory()) {
                allnames.addAll(getDirClassNames(f, path));          
            }
        }
        return allnames;    
    }
    
    private static List getDirClassNames(File directory, String base) {
        List allclasses = new ArrayList();
        File[] classes = directory.listFiles( new FileFilter() {
                    public boolean accept( File pathname ) {
                        return pathname.getName().endsWith( ".class" );
                    }
                });
        if (classes != null) {
            for (int i = 0; i < classes.length; i++) {
                String name = classes[i].getAbsolutePath();
                name = name.substring(base.length(), name.lastIndexOf("."));
                name = name.replaceAll("/", ".");
                allclasses.add(name);
            }
        }
        File[] directories = directory.listFiles( new FileFilter() {
                    public boolean accept( File pathname ) {
                        return pathname.isDirectory();
                    }
                });
        for (int i = 0; i < directories.length; i++) {
            allclasses.addAll(getDirClassNames(directories[i], base));   
        }
        return allclasses;
    }
    
    public static String getPathClassName(String path, String name) {
        List classes = getClassesForPath(path);
        for ( Iterator it = classes.iterator(); it.hasNext(); ) {
            String className = ( String ) it.next();
            if ( className.substring(className.lastIndexOf(".")).equals( name ) ) {
                return className;
            }
        }
        return null;
    }
    

    /***********
        The following methods were borrowed from Ant from a class with the same
        name.
    ***********/    
    /**
     * Find the directory or jar file the class has been loaded from.
     *
     * @param c the class whose location is required.
     * @return the file or jar with the class or null if we cannot
     *         determine the location.
     *
     * @since Ant 1.6
     */
    public static File getClassSource(Class c) {
        String classResource = c.getName().replace('.', '/') + ".class";
        return getResourceSource(c.getClassLoader(), classResource);
    }

    /**
     * Find the directory or jar a give resource has been loaded from.
     *
     * @param c the classloader to be consulted for the source
     * @param resource the resource whose location is required.
     *
     * @return the file with the resource source or null if
     *         we cannot determine the location.
     *
     * @since Ant 1.6
     */
    public static File getResourceSource(ClassLoader c, String resource) {
        if (c == null) {
            c = Locator.class.getClassLoader();
        }

        URL url = null;
        if (c == null) {
            url = ClassLoader.getSystemResource(resource);
        } else {
            url = c.getResource(resource);
        }
        if (url != null) {
            String u = url.toString();
            if (u.startsWith("jar:file:")) {
                int pling = u.indexOf("!");
                String jarName = u.substring(4, pling);
                return new File(fromURI(jarName));
            } else if (u.startsWith("file:")) {
                int tail = u.indexOf(resource);
                String dirName = u.substring(0, tail);
                return new File(fromURI(dirName));
            }
        }
        return null;
    }

    /**
     * Constructs a file path from a <code>file:</code> URI.
     *
     * <p>Will be an absolute path if the given URI is absolute.</p>
     *
     * <p>Swallows '%' that are not followed by two characters,
     * doesn't deal with non-ASCII characters.</p>
     *
     * @param uri the URI designating a file in the local filesystem.
     * @return the local file system path for the file.
     * @since Ant 1.6
     */
    public static String fromURI(String uri) {
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException emYouEarlEx) {
        }
        if (url == null || !("file".equals(url.getProtocol()))) {
            throw new IllegalArgumentException("Can only handle valid file: URIs");
        }
        StringBuffer buf = new StringBuffer(url.getHost());
        if (buf.length() > 0) {
            buf.insert(0, File.separatorChar).insert(0, File.separatorChar);
        }

        String file = url.getFile();
        int queryPos = file.indexOf('?');
        buf.append((queryPos < 0) ? file : file.substring(0, queryPos));

        uri = buf.toString().replace('/', File.separatorChar);

        if (File.pathSeparatorChar == ';' && uri.startsWith("\\") && uri.length() > 2
            && Character.isLetter(uri.charAt(1)) && uri.lastIndexOf(':') > -1) {
            uri = uri.substring(1);
        }

        String path = decodeUri(uri);
        return path;
    }

    /**
     * Decodes an Uri with % characters.
     * @param uri String with the uri possibly containing % characters.
     * @return The decoded Uri
     */
    private static String decodeUri(String uri) {
        if (uri.indexOf('%') == -1)
        {
            return uri;
        }
        StringBuffer sb = new StringBuffer();
        CharacterIterator iter = new StringCharacterIterator(uri);
        for (char c = iter.first(); c != CharacterIterator.DONE;
             c = iter.next()) {
            if (c == '%') {
                char c1 = iter.next();
                if (c1 != CharacterIterator.DONE) {
                    int i1 = Character.digit(c1, 16);
                    char c2 = iter.next();
                    if (c2 != CharacterIterator.DONE) {
                        int i2 = Character.digit(c2, 16);
                        sb.append((char) ((i1 << 4) + i2));
                    }
                }
            } else {
                sb.append(c);
            }
        }
        String path = sb.toString();
        return path;
    }
    
    /**
     * Get the File necessary to load the Sun compiler tools. If the classes
     * are available to this class, then no additional URL is required and
     * null is returned. This may be because the classes are explicitly in the
     * class path or provided by the JVM directly
     *
     * @return the tools jar as a File if required, null otherwise
     */
    public static File getToolsJar() {
        // firstly check if the tools jar is already in the classpath
        boolean toolsJarAvailable = false;

        try {
            // just check whether this throws an exception
            Class.forName("com.sun.tools.javac.Main");
            toolsJarAvailable = true;
        } catch (Exception e) {
            try {
                Class.forName("sun.tools.javac.Main");
                toolsJarAvailable = true;
            } catch (Exception e2) {
                // ignore
            }
        }

        if (toolsJarAvailable) {
            return null;
        }

        // couldn't find compiler - try to find tools.jar
        // based on java.home setting
        String javaHome = System.getProperty("java.home");
        if (javaHome.toLowerCase(Locale.US).endsWith("jre")) {
            javaHome = javaHome.substring(0, javaHome.length() - 4);
        }
        File toolsJar = new File(javaHome + "/lib/tools.jar");
        if (!toolsJar.exists()) {
            System.out.println("Unable to locate tools.jar. "
                 + "Expected to find it in " + toolsJar.getPath());
            return null;
        }
        return toolsJar;
    }

    /**
     * Get an array or URLs representing all of the jar files in the
     * given location. If the location is a file, it is returned as the only
     * element of the array. If the location is a directory, it is scanned for
     * jar files
     *
     * @param location the location to scan for Jars
     *
     * @return an array of URLs for all jars in the given location.
     *
     * @exception MalformedURLException if the URLs for the jars cannot be
     *            formed
     */
    public static URL[] getLocationURLs(File location)
         throws MalformedURLException {
        return getLocationURLs(location, new String[]{".jar"});
    }

    /**
     * Get an array or URLs representing all of the files of a given set of
     * extensions in the given location. If the location is a file, it is
     * returned as the only element of the array. If the location is a
     * directory, it is scanned for matching files
     *
     * @param location the location to scan for files
     * @param extensions an array of extension that are to match in the
     *        directory search
     *
     * @return an array of URLs of matching files
     * @exception MalformedURLException if the URLs for the files cannot be
     *            formed
     */
    public static URL[] getLocationURLs(File location,
                                        final String[] extensions)
         throws MalformedURLException {
        URL[] urls = new URL[0];

        if (!location.exists()) {
            return urls;
        }

        if (!location.isDirectory()) {
            urls = new URL[1];
            String path = location.getPath();
            for (int i = 0; i < extensions.length; ++i) {
                if (path.toLowerCase().endsWith(extensions[i])) {
                    urls[0] = location.toURL();
                    break;
                }
            }
            return urls;
        }

        File[] matches = location.listFiles(
            new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    for (int i = 0; i < extensions.length; ++i) {
                        if (name.toLowerCase().endsWith(extensions[i])) {
                            return true;
                        }
                    }
                    return false;
                }
            });

        urls = new URL[matches.length];
        for (int i = 0; i < matches.length; ++i) {
            urls[i] = matches[i].toURL();
        }
        return urls;
    }
    
}
