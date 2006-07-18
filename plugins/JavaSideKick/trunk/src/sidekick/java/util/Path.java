package sidekick.java.util;

import java.util.*;

/**
 * Ant has a class named the same, but it is much more involved.  I just wanted
 * a nice way to represent a set of paths for a classpath.
 * @author Dale Anson
 */
public class Path {

    private List paths;

    /** The system classspath as a Path object */
    public static Path systemClassPath = new Path( System.getProperty( "java.class.path" ) );

    public Path( String path ) {
        paths = new ArrayList();
        if ( path != null ) {
            String[] parts = path.split( System.getProperty( "path.separator" ) );
            paths.addAll( Arrays.asList( parts ) );
        }
    }

    public void concatSystemClassPath() {
        concatSystemClassPath( true );
    }

    public Path concatSystemClassPath( boolean before ) {
        if ( paths == null )
            paths = new ArrayList();
        if ( before )
            paths.addAll( 0, systemClassPath.getPaths() );
        else
            paths.addAll( systemClassPath.getPaths() );
        compact();
        //System.out.println("%%%%% " + toString());
        return this;
    }

    public List getPaths() {
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
