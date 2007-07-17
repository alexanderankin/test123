package com.illengineer.jcc;

import java.util.*;
import java.io.*;

/*
    This provider simply reads one line after another from its Reader input,
    and uses them as identifier names. This could be useful when reading from
    a plain text file, or over a network stream.
*/
class ReaderProvider implements IdentifierProvider, Serializable
{
    private ArrayList<String> identifierList;
    private Reader inputReader;
    
    ReaderProvider(Reader r) {
	inputReader = r;
    }
    
    public void process() {
	identifierList = new ArrayList<String>();
	try {
	    BufferedReader reader = new BufferedReader(inputReader);
	    String line;
	    
	    while(true) {
		line = reader.readLine();
		if (line == null) break;
		if (line.length() == 0) continue;
		identifierList.add(line);
	    }
	    
	    reader.close();
	} catch (IOException ex) {
	    // We'll just keep the identifiers we read before the error.
	}
    }
    
    public void forget() {
	identifierList = new ArrayList<String>();
    }

    public Iterator<String> iterator() {
	return identifierList.iterator();
    }
}
