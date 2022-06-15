// Unbounded Wildcards
import java.util.Collection;
import java.util.ArrayList;

class Test {
    // failed on this line, encountered void
    static void printCollection( Collection<?> c ) {
        // a wildcard collection
        for ( Object o : c ) {
            System.out.println( o );
        }
    }

    public static void main( String[] args ) {
        Collection<String> cs = new ArrayList<String>();
        cs.add( "hello" );
        cs.add( "world" );
        printCollection( cs );
    }
}