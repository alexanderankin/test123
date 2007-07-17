package com.illengineer.jcc;

import java.util.Iterator;

public interface IdentifierProvider
    extends Iterable<String>
{
    /** Processes the data source known by this IdentifierProvider, to prepare
	it for availability through iterator(). */
    public void process();
    
    /** In the course of process(), the IdentifierProvider may build up a large amount
	of data. This method makes it discard this cached data. process() will need
	to be called again before iterator() is valid. */
    public void forget();
    
    /** Returns an Iterator that will enumerate all the identifiers that
        this IdentifierProvider knows about. */
    public Iterator<String> iterator();

}
