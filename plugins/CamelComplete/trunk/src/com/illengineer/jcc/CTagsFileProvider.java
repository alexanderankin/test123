package com.illengineer.jcc;

import java.util.*;
import java.io.*;

/*
    This provider takes a File indicating a tags file as output by ctags,
    and loads identifier names from it.
*/
public class CTagsFileProvider implements IdentifierProvider, Serializable
{
    private ArrayList<String> identifierList;
    private String fileName;
    private File ctagsFile;
    
    public CTagsFileProvider(File f) {
	ctagsFile = f;
	fileName = ctagsFile.getName();
    }
    
    public void process() {
	identifierList = new ArrayList<String>();
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(ctagsFile));
	    String line;
	    int tabloc;
	    
	    while(true) {
		line = reader.readLine();
		if (line == null) break;
		if (line.length() == 0) continue;
		if (line.charAt(0) == '!') continue;  // a ctags comment line
		tabloc = line.indexOf('\t');
		if (tabloc == -1)	// malformed line?
		    continue;
		identifierList.add(line.substring(0, tabloc));
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
    
    public String toString() {
	return "CTags, " + fileName;
    }
}
