package com.illengineer.jcc;

import java.util.*;
import java.util.jar.*;
import java.io.*;

/*
    This provider takes a File indicating a JAR file
    and loads identifier names from it.
*/
public class JarFileProvider implements IdentifierProvider, Serializable
{
    private ArrayList<String> identifierList;
    private String fileName;
    private File jarFile;
    
    public JarFileProvider(File f) {
	jarFile = f;
	fileName = f.getName();
    }
    
    public void process() {
	identifierList = new ArrayList<String>();
	
	try {
	    JarFile jar = new JarFile(jarFile, false, JarFile.OPEN_READ);
	    String name;
	    int i,j,k;
	    JarEntry entry;
	    
	    for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements(); ) {
		entry = e.nextElement();
		name = entry.getName();
		i = name.lastIndexOf('/');
		i++;
		j = name.indexOf(".class", i);
		if (j == -1) continue;	// We only want class files
		k = name.indexOf('$', i);
		if (k != -1) {
		    i = k+1;  // inner class names
		    // I'm only going to take inner classes one level deep!
		    k = name.indexOf('$', i);
		    if (k != -1)
			j = k;
		}
		if (j - i < 2) continue;  // No silly little names, as for anonymous inner classes
		    
		identifierList.add(name.substring(i,j));
	    }
	    jar.close();
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
