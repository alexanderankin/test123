package com.illengineer.jcc;

import java.util.*;
import java.io.*;

public class TextFileProvider implements IdentifierProvider, Serializable
{
    private ReaderProvider readerProvider;
    private String fileName;
    private File f;
    private FileReader fr;

    public TextFileProvider(File f) {
	this.f = f;
	fileName = f.getName();
    }
    
    public void process() {
	try {
	    fr = new FileReader(f);
	    readerProvider = new ReaderProvider(fr);
	    readerProvider.process();
	} catch (IOException ex) {
	    readerProvider = null;
	}
    }

    public void forget() {
	if (readerProvider != null) {
	    readerProvider.forget();
	    readerProvider = null;
	    try {
		fr.close();
	    } catch (IOException ex) {}
	    fr = null;
	}
    }

    public Iterator<String> iterator() {
	if (readerProvider != null)
	    return readerProvider.iterator();
	else
	    return (new NullProvider()).iterator();
    }
    
    public String toString() {
	return "Text, " + fileName;
    }
}