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
    
    protected java.util.TreeSet<Completion> treeSet =
    	new java.util.TreeSet<Completion>();
    private Vector<Completion> tmpVector = new Vector<Completion>( 10 );

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
        Iterator<Completion> iter  = treeSet.iterator();
        Completion completion = null;
        boolean found = false;
        while (iter.hasNext()) {
            completion = iter.next();
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
                completion = iter.next();
                
                if ( completion.hasPrefix(prefix) ) {
                    tmpVector.add( completion );
                } else {
                    break;
                }
                
            } // while more completions with the same prefix
            
            // Return the result
            Completion[] words = new Completion[ tmpVector.size() ];
            words = tmpVector.toArray( words );
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
        Iterator<Completion>     iter  = treeSet.iterator();
        int i = 0;
        
        while (iter.hasNext()) {
            words[ i++ ] = iter.next();
        }
        return words;
    } // getAllWords

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#put(net.jakubholy.jedit.autocomplete.Completion)
     */
    public synchronized boolean add(Completion completion) {
        boolean result = treeSet.add(completion);
        notifier.notifyObservers( new WordListEvent(this) );
        return result;
    }

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#remove(net.jakubholy.jedit.autocomplete.Completion)
     */
    public synchronized boolean remove(Completion completion) {
    	boolean result =  treeSet.remove(completion);
        notifier.notifyObservers( new WordListEvent(this) );
        return result;
    }

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#clear()
     */
    public synchronized void clear() {
        treeSet.clear();
        notifier.notifyObservers( new WordListEvent(this) );
    }

    /* (non-Javadoc)
     * @see net.jakubholy.jedit.autocomplete.WordList#addAll(net.jakubholy.jedit.autocomplete.Completion[])
     */
    public void addAll(Completion[] completions) {
        treeSet.addAll( Arrays.asList(completions) );
        notifier.notifyObservers( new WordListEvent(this) );
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
    

    
	////////////////////////////////////////////////////////////////////////////////////////
	//					OBSERVABLE STUFF
	////////////////////////////////////////////////////////////////////////////////////////
	/** The object that to make us observable. */
	java.util.Observable notifier = new java.util.Observable(){
		public void notifyObservers( Object wordListEvent )
	    {
			super.setChanged();
	        super.notifyObservers( wordListEvent );
	    }// letObserversKnow
		
	};
	
	public void addObserver(java.util.Observer o){ notifier.addObserver(o); }
    public void deleteObserver(java.util.Observer o){ notifier.deleteObserver(o); }

}
