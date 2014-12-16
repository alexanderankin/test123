package sidekick.java.util;

import java.util.*;

/**
 * Ant has a class named the same, but it is much more involved.  I just wanted
 * a nice way to represent a set of paths for a classpath.
 * @author Dale Anson
 */
public class Path {

    private List<String> paths;

    /** The system classspath as a Path object */
    public static Path systemClassPath = new Path( System.getProperty( "java.class.path" ) );

    public Path( String path ) {
        paths = new ArrayList<String>();
        if ( path != null ) {
            String[] parts = path.split( System.getProperty( "path.separator" ) );
            paths.addAll( Arrays.asList( parts ) );
        }
    }
    
    public void concat( String path ) {
    	concat( path, false );
    }
    
    public void concat( String path, boolean before ) {
    	if ( path != null ) {
    		if ( paths == null )
				paths = new ArrayList<String>();
    		String[] parts = path.split( System.getProperty( "path.separator" ) );
            if ( before )
            	paths.addAll( 0, Arrays.asList( parts ) );
            else
            	paths.addAll( Arrays.asList( parts ) );
        }
    }

    public void concatSystemClassPath() {
        concatSystemClassPath( true );
    }

    public Path concatSystemClassPath( boolean before ) {
        if ( paths == null )
            paths = new ArrayList<String>();
        if ( before )
            paths.addAll( 0, systemClassPath.getPaths() );
        else
            paths.addAll( systemClassPath.getPaths() );
        compact();
        return this;
    }

    public List<String> getPaths() {
        compact();
        return paths;
    }

    public Iterator iterator() {
        compact();
        return paths.iterator();
    }

    /**
     * Removes duplicates while retaining order.
     */
    private void compact() {
        if ( paths != null ) {
            paths = ListOps.toList( ListOps.toSet( paths ) );
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        String pathSep = System.getProperty("path.separator");
        for (Iterator it = iterator(); it.hasNext(); ) {
            sb.append((String)it.next());
            if (it.hasNext())
                sb.append(pathSep);
        }
        return sb.toString();
    }
}
