/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.library;

import java.util.*;

/**
 * <b>NOTE:  I've genericized this version of ListOps.  This means it is NOT
 * backward compatible with other versions of this class.</b>
 * <p>
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
    * Performs a union of two collections. The elements of the returned collection will be
    * ordered with elements of <code>a</code> first (in their original order),
    * followed by elements of <code>b</code>, in their original order.
    * @param a one collection
    * @param b the other collection
    * @return a collection containing the elements of both collections with no duplicates.
    */
   public static <T> Collection<T> union( Collection<T> a, Collection<T> b ) {
      LinkedHashSet<T> union = new LinkedHashSet<T>( a );
      union.addAll( b );
      return union;
   }

   /**
    * Finds the intersection of two sets. Ordering is the order that the elements
    * are in in set <code>a</code>.
    * @param a one set
    * @param b the other set
    * @return the intersection of the two sets, may be empty, will not be null.
    */
   public static <T> Collection<T> intersection( Collection<T> a, Collection<T> b ) {
      LinkedHashSet<T> intersection = new LinkedHashSet<T>();
      for (T o : a) {
        if (b.contains(o))
            intersection.add(o);
      }
      return intersection;
   }

   /**
    * Finds the difference of set <code>a</code> and set <code>b</code>.
    * @param a the first set
    * @param b the other set
    * @return a set containing the elements of set <code>a</code> that are NOT also
    * in set <code>b</code>.
    */
   public static <T> Collection<T> difference( Collection<T> a, Collection<T> b ) {
      LinkedHashSet<T> difference = new LinkedHashSet<T>();
      for (T o : a) {
        if (!b.contains(o))
            difference.add(o);
      }
      return difference;
   }

   /**
    * Finds the symmetric difference of set <code>a</code> and set <code>b</code>.
    * @param a the first set
    * @param b the other set
    * @return a set containing the elements of set <code>a</code> that are NOT also
    * in set <code>b</code> unioned with the elements of set <code>b</code> that
    * are NOT also in set <code>a</code>.
    */
   public static <T> Collection<T> symmetricDifference( Collection<T> a, Collection<T> b ) {
      return union( difference( a, b ), difference( b, a ) );
   }

   /**
    * @return true if all elements of <code>b</code> are also in <code>a</code>.
    */
   public static <T> boolean isSubset( Collection<T> a, Collection<T> b ) {
      return a.containsAll( b );
   }

   /**
    * @return true if both sets contain the same elements.
    */
   public static <T> boolean equals( Collection<T> a, Collection<T> b ) {
      return isSubset( a, b ) && isSubset( b, a );
   }

   /**
    * Converts a List to a Set.  This has the effect of removing all duplicates
    * from the list.
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

}
