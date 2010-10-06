package sidekick.java.util;

import java.lang.ref.SoftReference;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.*;
import java.util.jar.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.gjt.sp.jedit.jEdit;

import projectviewer.vpt.VPTProject;

import sidekick.java.PVHelper;
import sidekick.java.classloader.AntClassLoader;

/**
 * The Locator is a utility class which is used to find classes loaded
 * in the runtime environment.
 *
 */
public final class Locator {

    // list of class names in classpathJars
    private List<String> classpathClassNames = null;
    
    // list jars and/or directories in the java runtime
    private File[] runtimeJars = null;
    
    // list of class names in runtimeJars
    private List<String> runtimeClassNames = null;
    
    // list of jars and/or directories defined per project
    //private File[] projectJars = null;
    
    // list of class names in projectJars
    private List<String> projectClassNames = null;

	// list of class names in global classpath
	// this may also include the system classpath
	private List<String> globalClassNames = null;
    
    // the current project
    private VPTProject project = null;
    
    // a classloader to load classes found in projectJars
    private AntClassLoader projectClassloader = null;
	private AntClassLoader globalClassloader = null;

    private static SoftReference<Locator> cachedSingleton;

    /**
     * Not instantiable
     */
    private Locator() {
        runtimeJars = getRuntimeJars();
        runtimeClassNames = getRuntimeClassNames();
        project = PVHelper.getProject( jEdit.getActiveView() );
        projectClassNames = reloadProjectClassNames( project );
		globalClassNames = reloadGlobalClassNames();
    }

    public static Locator getInstance() {
        if ( cachedSingleton != null ) {
            Locator cached = cachedSingleton.get();
            if ( cached != null ) {
                return cached;
            }
        }
        Locator newOne = new Locator();
        cachedSingleton = new SoftReference<Locator>( newOne );
        return newOne;
    }

    public ClassLoader getGlobalClassLoader() {
		return globalClassloader;
    }

    public File[] getClassPathJars() {
        String classpath = System.getProperty( "java.class.path" );
        if ( classpath == null || classpath.length() == 0 )
            return null;
        String path_sep = File.pathSeparator;
        String[] path_jars = classpath.split( path_sep );
        File[] jars = new File[ path_jars.length ];
        for ( int i = 0; i < path_jars.length; i++ ) {
            jars[ i ] = new File( path_jars[ i ] );
        }
        return jars;
    }

    /**
	 * @Deprecated
     * @return a list of class names of all classes in all jars in the classpath.
     */
    public List<String> getClassPathClassNames() {
        if ( classpathClassNames != null ) {
            return classpathClassNames;
        }
        File[] jars = getClassPathJars();
        List<String> allnames = new ArrayList<String>();
        for ( int i = 0; i < jars.length; i++ ) {
            File jar = jars[ i ];
            List<String> names = getJarClassNames( jar );
            if ( names != null ) {
                allnames.addAll( names );
            }
        }
        return allnames;
    }

    /**
     * @return a list of classnames contained in the given jar.
     */
    private List<String> getJarClassNames( File jar ) {
        List<String> names = new ArrayList<String>();
        try {
        	if (jar.isDirectory()) {
        		return names;
        	}
            JarFile jar_file = new JarFile( jar );
            Enumeration entries = jar_file.entries();
            while ( entries.hasMoreElements() ) {
                JarEntry entry = ( JarEntry ) entries.nextElement();
                String classname = entry.getName();
                if ( classname.endsWith( ".class" ) ) {
                    classname = classname.substring( 0, classname.lastIndexOf( '.' ) );
					names.add( classname );
				}
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
    public File[] getRuntimeJars() {
        if ( runtimeJars != null ) {
            return copyOf( runtimeJars );
        }

        // get runtime jars based on java.home setting
        File java_lib = null;             // location of $JAVA_HOME/lib
        File[] libs = null;               // actual files from lib dir
        File[] java_ext = null;           // location of ext dirs
        List<File> exts = null;           // actual files from ext dirs
        File[] java_endorsed = null;      // location of endorsed dirs
        List<File> endorsed = null;       // actual files from endorsed dirs

        String javaHome = System.getProperty( "java.home" );
        if ( javaHome != null ) {
            java_lib = new File( javaHome + "/lib" );
        }

        FileFilter ff = new FileFilter() {
                    public boolean accept( File pathname ) {
                        return pathname.getName().endsWith( ".jar" );
                    }
                };
        String ps = System.getProperty( "path.separator" );

        libs = java_lib.listFiles( ff );


        String extDirs = System.getProperty( "java.ext.dirs" );
        if ( extDirs != null ) {
            String[] filenames = extDirs.split( ps );
            exts = new ArrayList<File>();
            for ( String filename : filenames ) {
                File dir = new File( filename );
                if ( dir.exists() ) {
                    File[] filelist = dir.listFiles( ff );
                    exts.addAll( Arrays.asList( filelist ) );
                }
            }
        }

        String endorsedDirs = System.getProperty( "java.endorsed.dirs" );
        if ( endorsedDirs != null ) {
            String[] filenames = endorsedDirs.split( ps );
            endorsed = new ArrayList<File>();
            for ( String filename : filenames ) {
                File dir = new File( filename );
                if ( dir.exists() ) {
                    File[] filelist = dir.listFiles( ff );
                    endorsed.addAll( Arrays.asList( filelist ) );
                }
            }
        }

        // add endorsed jars first, they should override standard jars
        List<File> list = new ArrayList<File>();
        if ( endorsed != null && !endorsed.isEmpty() )
            list.addAll( endorsed );
        if ( libs != null && libs.length > 0 )
            list.addAll( Arrays.asList( libs ) );
        if ( exts != null && !exts.isEmpty() )
            list.addAll( exts );
        runtimeJars = ( File[] ) list.toArray( new File[] {} );
        return copyOf( runtimeJars );
    }

    /**
     * @return a list of class names of public classes provided by the java runtime.
     */
    public List<String> getRuntimeClassNames() {
        if ( runtimeClassNames != null ) {
            return runtimeClassNames;
        }
        File[] jars = getRuntimeJars();
        List<String> names = new ArrayList<String>();
        for ( int i = 0; i < jars.length; i++ ) {
            File jar = jars[ i ];
			/*
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
                            classname = classname.substring( 0, classname.lastIndexOf( '.' ) );
                        names.add( classname );
                    }
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
			*/
			List<String> classes = getJarClassNames(jar);
			names.addAll(classes);
        }
        return names;
    }

    /**
     * @param name class name minus the package part, e.g. "String" in "java.lang.String".
     */
    public String[] getRuntimeClassName( String name ) {
        return getClassName( runtimeClassNames, name );
    }


    /**
     * @param packageName package name with possibly a part of a classname, e.g.
     * "javax.swing.tree.DefaultMut"
     * @return a list of all class names that match.  The list may be empty, but
     * won't be null.
     */
    public List<String> getRuntimeClasses( String packageName ) {
        return getClasses( runtimeClassNames, packageName );
    }

    public AntClassLoader getProjectClassLoader() {
        return projectClassloader;   
    }
    
    private File[] getProjectJars( VPTProject proj ) {
        if ( proj == null ) {
            return null;
        }
        String classpath = PVHelper.getClassPathForProject(proj).toString();
        if ( classpath == null || classpath.length() == 0 ) {
            return null;
        }
		StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
		int count = tokenizer.countTokens();
        File[] jars = new File[ count ];
		for (int i = 0; i < count; i++) {
            jars[ i ] = new File( tokenizer.nextToken() );
        }
        return jars;
    }

	private File[] getGlobalJars() {
		String classpath = jEdit.getProperty("sidekick.java.classpath");
		if (classpath == null || classpath.length() == 0) {
			return null;
		}
		StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
		int count = tokenizer.countTokens();
        File[] jars = new File[ count ];
		for (int i = 0; i < count; i++) {
            jars[ i ] = new File( tokenizer.nextToken() );
        }
        return jars;
	}

    /**
     * @return a list of class names of all classes in all jars in the classpath.
	 * This method should be the only one used publicly
	 * It will automatically reload class names if needed
     */
    public List<String> getProjectClassNames( VPTProject proj ) {
        if ( proj == null ) {
            return null;
        }

		if ( projectClassNames == null || project != proj ) {
			this.project = proj;
			projectClassNames = reloadProjectClassNames( proj );
		}

        return projectClassNames;
    }

	public List<String> getGlobalClassNames() {
		if (globalClassNames == null) {
			globalClassNames = reloadGlobalClassNames();
		}
		return globalClassNames;
	}

    private List<String> reloadProjectClassNames( VPTProject proj ) {
        // need both a list of names and a classloader for these classes
        File[] jars = getProjectJars( proj );
        if (jars == null) {
            return null;   
        }
        projectClassloader = new AntClassLoader();
        List<String> allnames = new ArrayList<String>();
        for ( File jar : jars ) {
            projectClassloader.addPathFile( jar );
            List<String> names = null;
            if (!jar.isDirectory()) names = getJarClassNames( jar );
            else names = getDirClassNames(jar, jar.getPath());
            if ( names != null ) {
                allnames.addAll( names );
            }
        }
        return allnames;
    }

	private List<String> reloadGlobalClassNames() {
		globalClassloader = new AntClassLoader();
		File[] jars = getGlobalJars();
		List<String> allnames = new ArrayList<String>();
		if (jars != null) {
			for (File jar : jars) {
				globalClassloader.addPathFile(jar);
				List<String> names = null;
				if (!jar.isDirectory()) names = getJarClassNames(jar);
				else names = getDirClassNames(jar, jar.getPath());
				if (names != null) {
					allnames.addAll(names);
				}
			}
		}
		// Load system classpath
		if (jEdit.getBooleanProperty("sidekick.java.classpathIncludeSystem")) {
			jars = getClassPathJars();
			for (File jar : jars) {
				globalClassloader.addPathFile(jar);
				List<String> names = null;
				if (!jar.isDirectory()) names = getJarClassNames(jar);
				else names = getDirClassNames(jar, jar.getPath());
				if (names != null) {
					allnames.addAll(names);
				}
			}
		}	
		// Load jedit libraries and plugins
		if (jEdit.getBooleanProperty("sidekick.java.classpathIncludePlugins")) {
			// Load jars from settings
			jars = new File(jEdit.getSettingsDirectory(), "jars").listFiles();
			for (File jar : jars) {
				globalClassloader.addPathFile(jar);
				List<String> names = null;
				if (!jar.isDirectory()) names = getJarClassNames(jar);
				else names = getDirClassNames(jar, jar.getPath());
				if (names != null) {
					allnames.addAll(names);
				}
			}
			// Load jars from application
			jars = new File(jEdit.getJEditHome(), "jars").listFiles();
			for (File jar : jars) {
				globalClassloader.addPathFile(jar);
				List<String> names = null;
				if (!jar.isDirectory()) names = getJarClassNames(jar);
				else names = getDirClassNames(jar, jar.getPath());
				if (names != null) {
					allnames.addAll(names);
				}
			}
		}
		return allnames;
	}

	/**
	 * @Deprecated
	 */
    public void reloadProjectJars( VPTProject proj ) {
        String classpath = PVHelper.getClassPathForProject(proj).toString();
        if ( classpath == null || classpath.length() == 0 )
            return;
        String path_sep = File.pathSeparator;
        String[] path_jars = classpath.split( path_sep );
        File[] jars = new File[ path_jars.length ];
        for ( int i = 0; i < path_jars.length; i++ ) {
            jars[ i ] = new File( path_jars[ i ] );
        }
        //projectJars = jars;
    }
    
    /**
     * @param packageName package name with possibly a part of a classname, e.g.
     * "javax.swing.tree.DefaultMut"
     * @return a list of all class names that match.  The list may be empty, but
     * won't be null.
     */
    public List<String> getProjectClasses( VPTProject proj, String packageName ) {
        if ( proj == null ) {
            return null;
        }
        return getClasses( getProjectClassNames(proj), packageName );
    }

	public List<String> getGlobalClasses( String packageName ) {
		return getClasses( getGlobalClassNames(), packageName );
	}

    /**
     * @param name class name minus the package part, e.g. "String" in "java.lang.String".
     */
    public String[] getProjectClassName( VPTProject proj, String name ) {
        if ( proj == null ) {
            return null;
        }
        if ( projectClassNames == null || project != proj ) {
            // need to load jars and class names for the project
            projectClassNames = getProjectClassNames( proj );
        }
        return getClassName( projectClassNames, name );
    }

    public String[] getGlobalClassName( String name ) {
        return getClassName( globalClassNames, name );
    }


    /**
	 * @Deprecated. The system classpath is now included in global
     * @param name class name minus the package part, e.g. "String" in "java.lang.String".
     */
    public String[] getClassPathClassName( String name ) {
        return getClassName( classpathClassNames, name );
    }

    public String[] getClassName(String name) {
		// Replace dots with dollar signs to support inner classes
		name = name.replace(".", "$");
    	String[] runtime = getRuntimeClassName(name);
		String[] project = null;
    	String[] classpath = null;

    	ArrayList<String> all = new ArrayList<String>();
    	
    	for (int i = 0; i<runtime.length; i++) {
    		all.add(runtime[i]);
    	}

		Object _proj = PVHelper.getProject(jEdit.getActiveView());
		if (_proj != null) {
			// If a project is active, load its classes
			projectviewer.vpt.VPTProject proj = (projectviewer.vpt.VPTProject) _proj;
			project = getProjectClassName(proj, name);
			// Load the global classpath if it hasn't been disabled for this project
			if (!"false".equals(proj.getProperty("java.useJavaClasspath"))) {
				classpath = getGlobalClassName(name);
			}
		} else {
			// No project open, so just load the global classpath
			classpath = getGlobalClassName(name);
		}

		if (project != null) {
			for (int i = 0; i<project.length; i++) {
				all.add(project[i]);
			}
		}
		if (classpath != null) {
			for (int i = 0; i<classpath.length; i++) {
				all.add(classpath[i]);
			}
		}
    	return all.toArray(new String[] {});
    }
    
    private String[] getClassName( List<String> classNames, String name ) {
        if ( classNames == null ) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();
        for ( String fullClassName : classNames ) {
            int index = fullClassName.lastIndexOf( "/" ) + 1;
            String className = fullClassName.substring( index );
            if ( className.equals( name ) ) {
                fullClassName = fullClassName.replaceAll( "/", "." );
                list.add(fullClassName);
            }
        }
        if (list.size() > 0) {
            Collections.sort(list);
            return list.toArray(new String[] {});
        }
        //list.add("");
        return list.toArray(new String[] {});
    }


    /**
     * @param packageName package name with possibly a part of a classname, e.g.
     * "javax.swing.tree.DefaultMut"
     * @return a list of all class names that match.  The list may be empty, but
     * won't be null.
     */
    public List<String> getClassPathClasses( String packageName ) {
        return getClasses( classpathClassNames, packageName );
    }

    private List<String> getClasses( List<String> classNames, String packageName ) {
        List<String> list = new ArrayList<String>();
        if ( packageName == null || packageName.length() == 0 || classNames == null ) {
            return list;
        }
        String name = packageName.replaceAll( "[.]", "/" );
        for ( String fullClassName : classNames ) {
            if ( fullClassName.startsWith( name ) && fullClassName.indexOf("/", name.length()) < 0 && fullClassName.indexOf( "$" ) < 0 ) {
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
    public List<String> getClassesForPath( String path ) {
        String pathSep = System.getProperty( "path.separator" );
        String[] paths = path.split( pathSep );
        // paths can be either jars, zips, directories, or individual classes
        // directories can contain individual classes.
        List<String> allnames = new ArrayList<String>();
        for ( int i = 0; i < paths.length; i++ ) {
            path = paths[ i ];
            File f = new File( path );
            // check for jar or zip
            if ( path.toLowerCase().endsWith( ".jar" ) || path.toLowerCase().endsWith( ".zip" ) ) {
                List<String> names = getJarClassNames( f );
                if ( names != null ) {
                    allnames.addAll( names );
                }
            }
            // check for individual class
            else if ( path.toLowerCase().endsWith( ".class" ) ) {
                allnames.add( f.getName().substring( 0, f.getName().lastIndexOf( "." ) ) );
            }
            // check for directories
            else if ( f.isDirectory() ) {
                allnames.addAll( getDirClassNames( f, path ) );
            }
        }
        return allnames;
    }

    private List<String> getDirClassNames( File directory, String base ) {
        List<String> allclasses = new ArrayList<String>();
        File[] classes = directory.listFiles( new FileFilter() {
                    public boolean accept( File pathname ) {
                        return pathname.getName().endsWith( ".class" );
                    }
                }
                                            );
        if ( classes != null ) {
            for ( int i = 0; i < classes.length; i++ ) {
                String name = classes[ i ].getAbsolutePath();
                name = name.substring( base.length()+1, name.lastIndexOf( '.' ) );
                name = name.replace(File.separator, "/");
                //name = name.replaceAll( "/", "." );
                allclasses.add( name );
            }
        }
        File[] directories = directory.listFiles( new FileFilter() {
                    public boolean accept( File pathname ) {
                        return pathname.isDirectory();
                    }
                }
                                                );
        for ( int i = 0; i < directories.length; i++ ) {
            allclasses.addAll( getDirClassNames( directories[ i ], base ) );
        }
        return allclasses;
    }

    public String getPathClassName( String path, String name ) {
        List classes = getClassesForPath( path );
        for ( Iterator it = classes.iterator(); it.hasNext(); ) {
            String className = ( String ) it.next();
            if ( className.substring( className.lastIndexOf( '.' ) ).equals( name ) ) {
                return className;
            }
        }
        return null;
    }

    public File[] getAllJars() {
    	File[] runtime = getRuntimeJars();
    	File[] project = getProjectJars(PVHelper.getProject(jEdit.getActiveView()));
    	File[] all = new File[runtime.length+project.length];
    	for (int i = 0; i<all.length; i++) {
    		if (i<runtime.length) {
				all[i] = runtime[i];
			} else {
				all[i] = project[i-runtime.length];
			}
    	}
    	return all;
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
    public File getClassSource( Class c ) {
        String classResource = c.getName().replace( '.', '/' ) + ".class";
        return getResourceSource( c.getClassLoader(), classResource );
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
    public File getResourceSource( ClassLoader c, String resource ) {
        if ( c == null ) {
            c = Locator.class.getClassLoader();
        }

        URL url = null;
        if ( c == null ) {
            url = ClassLoader.getSystemResource( resource );
        }
        else {
            url = c.getResource( resource );
        }
        if ( url != null ) {
            String u = url.toString();
            if ( u.startsWith( "jar:file:" ) ) {
                int pling = u.indexOf( '!' );
                String jarName = u.substring( 4, pling );
                return new File( fromURI( jarName ) );
            }
            else if ( u.startsWith( "file:" ) ) {
                int tail = u.indexOf( resource );
                String dirName = u.substring( 0, tail );
                return new File( fromURI( dirName ) );
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
    public String fromURI( String uri ) {
        URL url = null;
        try {
            url = new URL( uri );
        }
        catch ( MalformedURLException emYouEarlEx ) {}  // NOPMD
        if ( url == null || !( "file".equals( url.getProtocol() ) ) ) {
            throw new IllegalArgumentException( "Can only handle valid file: URIs" );
        }
        StringBuffer buf = new StringBuffer( url.getHost() );
        if ( buf.length() > 0 ) {
            buf.insert( 0, File.separatorChar ).insert( 0, File.separatorChar );
        }

        String file = url.getFile();
        int queryPos = file.indexOf( '?' );
        buf.append( ( queryPos < 0 ) ? file : file.substring( 0, queryPos ) );

        uri = buf.toString().replace( '/', File.separatorChar );

        if ( File.pathSeparatorChar == ';' && uri.startsWith( "\\" ) && uri.length() > 2
                && Character.isLetter( uri.charAt( 1 ) ) && uri.lastIndexOf( ':' ) > -1 ) {
            uri = uri.substring( 1 );
        }

        String path = decodeUri( uri );
        return path;
    }

    /**
     * Decodes an Uri with % characters.
     * @param uri String with the uri possibly containing % characters.
     * @return The decoded Uri
     */
    private String decodeUri( String uri ) {
        if ( uri.indexOf( '%' ) == -1 ) {
            return uri;
        }
        StringBuffer sb = new StringBuffer();
        CharacterIterator iter = new StringCharacterIterator( uri );
        for ( char c = iter.first(); c != CharacterIterator.DONE;
                c = iter.next() ) {
            if ( c == '%' ) {
                char c1 = iter.next();
                if ( c1 != CharacterIterator.DONE ) {
                    int i1 = Character.digit( c1, 16 );
                    char c2 = iter.next();
                    if ( c2 != CharacterIterator.DONE ) {
                        int i2 = Character.digit( c2, 16 );
                        sb.append( ( char ) ( ( i1 << 4 ) + i2 ) );
                    }
                }
            }
            else {
                sb.append( c );
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
    public File getToolsJar() {
        // firstly check if the tools jar is already in the classpath
        boolean toolsJarAvailable = false;

        try {
            // just check whether this throws an exception
            Class.forName( "com.sun.tools.javac.Main" );
            toolsJarAvailable = true;
        }
        catch ( Exception e ) {
            try {
                Class.forName( "sun.tools.javac.Main" );
                toolsJarAvailable = true;
            }
            catch ( Exception e2 ) {    // NOPMD
                // ignore
            }
        }

        if ( toolsJarAvailable ) {
            return null;
        }

        // couldn't find compiler - try to find tools.jar
        // based on java.home setting
        String javaHome = System.getProperty( "java.home" );
        if ( javaHome.toLowerCase( Locale.US ).endsWith( "jre" ) ) {
            javaHome = javaHome.substring( 0, javaHome.length() - 4 );
        }
        File toolsJar = new File( javaHome + "/lib/tools.jar" );
        if ( !toolsJar.exists() ) {
            System.out.println( "Unable to locate tools.jar. "
                    + "Expected to find it in " + toolsJar.getPath() );
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
    public URL[] getLocationURLs( File location )
    throws MalformedURLException {
        return getLocationURLs( location, new String[] {".jar"} );
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
    public URL[] getLocationURLs( File location,
            final String[] extensions )
    throws MalformedURLException {
        URL[] urls = new URL[ 0 ];

        if ( !location.exists() ) {
            return urls;
        }

        if ( !location.isDirectory() ) {
            urls = new URL[ 1 ];
            String path = location.getPath();
            for ( int i = 0; i < extensions.length; ++i ) {
                if ( path.toLowerCase().endsWith( extensions[ i ] ) ) {
                    urls[ 0 ] = location.toURI().toURL();
                    break;
                }
            }
            return urls;
        }

        File[] matches = location.listFiles(
                    new FilenameFilter() {
                        public boolean accept( File dir, String name ) {
                            for ( int i = 0; i < extensions.length; ++i ) {
                                if ( name.toLowerCase().endsWith( extensions[ i ] ) ) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    }
                );

        urls = new URL[ matches.length ];
        for ( int i = 0; i < matches.length; ++i ) {
            urls[ i ] = matches[ i ].toURI().toURL();
        }
        return urls;
    }

    private File[] copyOf( File[] array ) {
        File[] rtn = new File[ array.length ];
        System.arraycopy( array, 0, rtn, 0, array.length );
        return rtn;
    }

	public void refreshProject( VPTProject proj ) {
		if (project == proj) {
			projectClassNames = reloadProjectClassNames( proj );
		}
	}

	public void refreshGlobal() {
		globalClassNames = reloadGlobalClassNames();
	}

}
