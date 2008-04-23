package com.illengineer.jcc;

import java.util.*;
import java.io.Serializable;

public class NullProvider implements IdentifierProvider, Serializable, Iterator<String>
{
    public NullProvider() {
    }
    
    public void process() {
    }
    
    public void forget() {
    }

    public Iterator<String> iterator() {
	return this;
    }
    
    // {{{ Interface Iterator
    public boolean hasNext() { return false; }
    public String next() { throw new NoSuchElementException(); }
    public void remove() { throw new UnsupportedOperationException(); }
    // }}}
}
