package sidekick.java.util;

import java.util.*;

/**
 * Set operations for Lists and Sets:
 * <ul>
 * <li>union: a combined set consisting of the unique elements of two sets</li>
 * <li>intersection: a set containing elements that are members of both sets</li>
 * <li>symmetric difference: a set containing elements that are in one or the other of, but not both, sets</li>
 * <li>difference: a set containing the elements of one set minus any elements in the second set</li>
 * <li>cartesian product: the set of all ordered pairs with the first element of each pair selected from one set and the second element selected from the other.
 * </ul>
 * and some utility functions:
 * <ul>
 * <li>isSubset: returns true if all elements of the second set are elements of the first set</li>
 * <li>equals: returns true if both sets contain the same elements</li>
 * <li>disjoint: returns true if no elements of either set are contained in the other</li>
 * <li>toSet: converts a list to a set</li>
 * <li>toList: converts a set to a list</li>
 * </ul>
 * There are some interesting side effects: Suppose you have an ArrayList, <code>a</code>, that
 * may contain duplicates. Calling <code>toList(toSet(a))</code> return a new ArrayList sorted
 * in the same order as <code>a</code> but containing no duplicates.
 * @author Dale Anson
 */
public class ListOps {

    /**
     * Performs a union of two sets. The elements of the returned set will be
     * ordered with elements of <code>a</code> first (in their original order),
     * followed by elements of <code>b</code>, in their original order.
     * @param a one set
     * @param b the other set
     * @return a List containing the elements of both lists with no duplicates.
     */
    public static <T> Set<T> union( Set<T> a, Set<T> b ) {
        LinkedHashSet<T> union = new LinkedHashSet<T>( a );
        union.addAll( b );
        return union;
    }

    /**
     * Performs a union of two lists. The elements of the returned list will be
     * ordered with elements of <code>a</code> first (in their original order),
     * followed by elements of <code>b</code>, in their original order.
     * @param a one list
     * @param b the other list
     * @return a List containing the elements of both lists with no duplicates.
     */
    public static <T> List<T> union( List<T> a, List<T> b ) {
        Set<T> union = union( toSet( a ), toSet( b ) );
        return new ArrayList<T>( union );
    }

    /**
     * Finds the intersection of two sets. Ordering is the order that the elements
     * are in in set <code>a</code>.
     * @param one set
     * @param the other set
     * @return the intersection of the two sets, may be empty, will not be null.
     */
    public static <T> Set<T> intersection( Set<T> a, Set<T> b ) {
        LinkedHashSet<T> intersection = new LinkedHashSet<T>();
        for ( T o : a ) {
            if (b.contains( o )) {
                intersection.add(o);
            }
        }
        return intersection;
    }

    /**
     * Finds the intersection of two Lists. Ordering is the order that the elements
     * are in in List <code>a</code>.
     * @param one List
     * @param the other List
     * @return the intersection of the two List, may be empty, will not be null.
     */
    public static <T> List<T> intersection( List<T> a, List<T> b ) {
        return toList( intersection( toSet( a ), toSet( b ) ) );
    }

    /**
     * Finds the difference of set <code>a</code> and set <code>b</code>.
     * @param a the first set
     * @param b the other set
     * @return a set containing the elements of set <code>a</code> that are NOT also
     * in set <code>b</code>.
     */
    public static <T> Set<T> difference( Set<T> a, Set<T> b ) {
        LinkedHashSet<T> difference = new LinkedHashSet<T>();
        for ( T o : a ) {
            if ( !b.contains( o ) ) {
                difference.add( o );
            }
        }
        return difference;
    }

    /**
     * Finds the difference of list <code>a</code> and list <code>b</code>.
     * @param a the first list
     * @param b the other list
     * @return a set containing the elements of list <code>a</code> that are NOT also
     * in list <code>b</code>.
     */
    public static <T> List<T> difference( List<T> a, List<T> b ) {
        return toList( difference( toSet( a ), toSet( b ) ) );
    }

    /**
     * Finds the symmetric difference of set <code>a</code> and set <code>b</code>.
     * @param a the first set
     * @param b the other set
     * @return a set containing the elements of set <code>a</code> that are NOT also
     * in set <code>b</code> unioned with the elements of set <code>b</code> that
     * are NOT also in set <code>a</code>.
     */
    public static <T> Set<T> symmetricDifference( Set<T> a, Set<T> b ) {
        return union( difference( a, b ), difference( b, a ) );
    }

    /**
     * Finds the symmetric difference of list <code>a</code> and list <code>b</code>.
     * @param a the first list
     * @param b the other list
     * @return a list containing the elements of list <code>a</code> that are NOT also
     * in list <code>b</code> unioned with the elements of list <code>b</code> that
     * are NOT also in list <code>a</code>.
     */
    public static <T> List<T> symmetricDifference( List<T> a, List<T> b ) {
        return toList( symmetricDifference( toSet( a ), toSet( b ) ) );
    }

    /**
     * @return true if all elements of <code>b</code> are also in <code>a</code>.
     */
    public static <T> boolean isSubset( Set<T> a, Set<T> b ) {
        return a.containsAll( b );
    }

    /**
     * @return true if all elements of <code>b</code> are also in <code>a</code>.
     */
    public static <T> boolean isSubset( List<T> a, List<T> b ) {
        return isSubset( toSet( a ), toSet( b ) );
    }

    /**
     * @return true if both sets contain the same elements.
     */
    public static <T> boolean equals( Set<T> a, Set<T> b ) {
        return isSubset( a, b ) && isSubset( b, a );
    }

    /**
     * @return true if both Lists contain the same elements.
     */
    public static <T> boolean equals( List<T> a, List<T> b ) {
        return equals( toSet( a ), toSet( b ) );
    }

    /**
     * Converts a List to a Set.
     */
    public static <T> Set<T> toSet( List<T> a ) {
        return new LinkedHashSet<T>( a );
    }

    /**
     * Converts a Set to a List.
     */
    public static <T> List<T> toList( Set<T> a ) {
        return new ArrayList<T>( a );
    }


    /**
     * Used by <code>product</code>, represents an ordered pair.
     */
    public static class Pair<T> {

        public T x = null;
        public T y = null;

        public Pair() {}

        public Pair( T a, T b ) {
            x = a;
            y = b;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append( "(" );
            sb.append( x == null ? "null" : x.toString() );
            sb.append( "," );
            sb.append( y == null ? "null" : y.toString() );
            sb.append( ")" );
            return sb.toString();
        }
    }

}
