/*
 * Created on 9.2.2005
 * $id
 */
package net.jakubholy.jedit.autocomplete;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

/**
 * A WordList implementation based on
 * java.util.TreeSet (which implements
 * OrderedSet). It is synchronized.
 *
 */
public class WordListTreeSet implements WordList {
    
    protected java.util.TreeSet treeSet = new java.util.TreeSet();
    private Vector tmpVector = new Vector( 10 );

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#getCompletions(java.lang.String)
     * TODO: use  TreeSet.tailSet instead
     * tailSet(Object fromElement)
     *     Returns a view of the portion of this set whose elements are greater than or equal to fromElement.
     */
    public synchronized Completion[] getCompletions(String prefix) {
        
        tmpVector.clear();
        
        // Find the first matching element
        //
        Iterator iter  = treeSet.iterator();
        Completion completion = null;
        boolean found = false;
        while (iter.hasNext()) {
            completion = (Completion) iter.next();
            if ( completion.hasPrefix(prefix) ) {
                found = true;
                break;
            }
        } // while more elements
        
        // Get the consecutive matching elements & return them
        //
        if ( found == true )
        {
            // Find all consecutive matching elements
            tmpVector.add( completion );
            while (iter.hasNext()) {
                completion = (Completion) iter.next();
                
                if ( completion.hasPrefix(prefix) ) {
                    tmpVector.add( completion );
                } else {
                    break;
                }
                
            } // while more completions with the same prefix
            
            // Return the result
            Completion[] words = new Completion[ tmpVector.size() ];
            words = (Completion[]) tmpVector.toArray( words );
            return words;
        } 
        else 
        {
            return new Completion[0];
        } // if-else a match found
    } // getCompletions

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#getAllWords()
     */
    public synchronized Completion[] getAllWords() {
        Completion[] words = new Completion[ treeSet.size() ];
        Iterator     iter  = treeSet.iterator();
        int i = 0;
        
        while (iter.hasNext()) {
            words[ i++ ] = (Completion) iter.next();
        }
        return words;
    } // getAllWords

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#put(net.jakubholy.jedit.autocomplete.Completion)
     */
    public synchronized boolean add(Completion completion) {
        return treeSet.add(completion);
    }

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#remove(net.jakubholy.jedit.autocomplete.Completion)
     */
    public synchronized boolean remove(Completion completion) {
        return treeSet.remove(completion);
    }

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#clear()
     */
    public synchronized void clear() {
        treeSet.clear();
    }

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#addAll(net.jakubholy.jedit.autocomplete.Completion[])
     */
    public void addAll(Completion[] completions) {
        treeSet.addAll( Arrays.asList(completions) );
    }

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#size()
     */
    public int size() {
        return treeSet.size();
    }

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#containes(net.jakubholy.jedit.autocomplete.Completion)
     */
    public boolean containes(Completion completion) {
        return treeSet.contains(completion);
    }

}
