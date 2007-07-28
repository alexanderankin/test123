package com.illengineer.jcc.jedit;

import com.illengineer.jcc.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.BeanShellErrorDialog;

public class BufferWordsProvider implements IdentifierProvider, Serializable
{
    private String regex;
    private Pattern pattern;
    private TreeSet<String> wordsSet;
    private boolean searchAllBuffers;
    
    public BufferWordsProvider(String regex, boolean searchAllBuffers) {
	this.regex = regex;
	this.searchAllBuffers = searchAllBuffers;
	wordsSet = new TreeSet<String>();
	try {
	    pattern = Pattern.compile(regex);
	} catch (PatternSyntaxException ex) {
	    pattern = null;
	    View v = jEdit.getActiveView();
	    if (v != null)
		new BeanShellErrorDialog(v, ex);
	}
    }
    
    public void process() {
	if (pattern == null) return;

	Buffer [] buffers = null;
	if (searchAllBuffers)
	    buffers = jEdit.getBuffers();
	else {
	    ArrayList<Buffer> l = new ArrayList<Buffer>();
	    View v = jEdit.getFirstView();
	    while (v != null) {
		for (EditPane pane : v.getEditPanes()) {
		    Buffer b = pane.getBuffer();
		    if (!l.contains(b))
			l.add(b);
		}
		v = v.getNext();
	    }
	    buffers = l.toArray(new Buffer[0]);
	}
	for (Buffer buffer : buffers) {
	    int numlines = buffer.getLineCount();
	    for (int i = 0; i < numlines; i++) {
		String line = buffer.getLineText(i);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
		    String word = matcher.group();
		    wordsSet.add(word);
		}
	    }
	}
    }
    
    public void forget() {
	wordsSet = new TreeSet<String>();
    }

    public Iterator<String> iterator() {
	return wordsSet.iterator();
    }

    public String toString() {
	return "Buffer Words, " + regex;
    }
}
