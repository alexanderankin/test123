package com.illengineer.jcc;

import java.util.regex.*;

public class RegexpTokenizer implements Tokenizer
{
    private Pattern pattern;
    private StringBuilder buffer;
    
    public RegexpTokenizer(String regexp, boolean ignoreCase) {
	try {
	    pattern = Pattern.compile(regexp, (ignoreCase ? Pattern.CASE_INSENSITIVE : 0));
	} catch (Exception ex) {
	    // In case of a bad regexp, we'll use a default value that works for C and Lisp code.
	    pattern = Pattern.compile("[_-]");
	}
	buffer = new StringBuilder();
    }
    
    // NOTE!! Only word constituents that begin with LETTERS will be considered
    // for code completion.
    public char[] splitIdentifer(String identifier) {
	String[] parts = pattern.split(identifier);
	char c;
	
	buffer.delete(0, buffer.length());
	
	for (String part : parts) {
	    if (part.length() > 0) {
		c = part.charAt(0);
		if (Character.isLetter(c))
		    buffer.append(c);
	    }
	}
	
	char[] retval = new char[buffer.length()];
	buffer.getChars(0, retval.length, retval, 0);
	return retval;
    }
    
    public String toString() {
	return "Regex, " + pattern.toString();
    }
}
