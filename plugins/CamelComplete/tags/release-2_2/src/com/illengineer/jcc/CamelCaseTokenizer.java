package com.illengineer.jcc;

/*
    This tokenizer will work for identifiers written in the so-called "CamelCase" style.
    Some examples, along with their abbreviations, are:
    
	SwingUtilities 	-> SU
	GtkMessageBox 	-> GMB
	IOException	-> IOE
	G_ModelScan	-> GMS
	G_modelScan	-> GmS
	G3openFile	-> GoF
	_kml_openFile	-> koF
	
    So the rule can basically be summarized as
	*All capital letters will be counted as part of the abbreviation
	*Any letter after a non-letter character will be counted as well.

*/
public class CamelCaseTokenizer implements Tokenizer
{
    private StringBuilder buffer;
    
    private enum ParserState { FIRST, NORMAL, NONLETTER };

    public CamelCaseTokenizer() {
	buffer = new StringBuilder();
    }
    
    public char[] splitIdentifer(String identifier) {
	buffer.delete(0, buffer.length());
	
	ParserState state = ParserState.FIRST;
	for (int i = 0; i < identifier.length(); i++) {
	    char c = identifier.charAt(i);
	    switch (state) {
	      case FIRST:
	        if (Character.isLetter(c)) {
		    buffer.append(c);
		    state = ParserState.NORMAL;
		}
		break;
	      case NORMAL:
	        if (Character.isUpperCase(c))
		    buffer.append(c);
		else if (!Character.isLetter(c))
		    state = ParserState.NONLETTER;
		break;
	      case NONLETTER:
	        if (Character.isLetter(c)) {
		    buffer.append(c);
		    state = ParserState.NORMAL;
		}
		break;
	    }
	        
	}
	char[] retval = new char[buffer.length()];
	buffer.getChars(0, retval.length, retval, 0);
	return retval;
    }
    
    public String toString() {
	return "CamelCase";
    }
}