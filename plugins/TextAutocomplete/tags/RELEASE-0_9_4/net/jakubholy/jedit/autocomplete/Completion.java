/*
 * Created on 9.2.2005
 * $id
 */
package net.jakubholy.jedit.autocomplete;

/**
 * Represents one completition = a word + perhaps some additional info.
 */
//{{{ Completion class
class Completion
implements Comparable
{
    private final String word;
    
    /** Create a new completion.
     * @param text At least one letter!
     * */
    Completion(String text )
    {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("Completion: the text" +
                    " must be not null and have at least 1 character");
        }
        this.word = text;           
    }

    public String toString()
    {
        return word;
    }

    public int hashCode()
    {
        return word.hashCode();
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof Completion)
            return ((Completion)obj).word.equals(word);
        else
            return false;
    }

    public String getWord() {
        return word;
    }
    
    /** Return true if this completion start with 
     * the given string.
     */
    public boolean hasPrefix(String prefix) {
        return word.startsWith(prefix);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return word.compareTo(((Completion)o).getWord());
    }
} //}}}