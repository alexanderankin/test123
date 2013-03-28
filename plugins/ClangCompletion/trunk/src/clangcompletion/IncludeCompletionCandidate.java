package clangcompletion;

import java.io.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;

import superabbrevs.SuperAbbrevs;

import completion.util.*;

class IncludeCompletionCandidate extends BaseCompletionCandidate
{
	// private int start;
	
	public IncludeCompletionCandidate(/* int start, */ String desc)
	{
		super(desc);
		// this.start = start;
		description = desc;
	}
	
    @Override
    public boolean isValid (View view)
    {
    	
    	String path = getCompletionPrefix(view);
    	
    	if(description != null && path != null)
    	{
    		return description.toLowerCase().startsWith(path.toLowerCase());
    	}
    	return false;
    }
    
    @Override
    public void complete (View view)
    {
        String path = getCompletionPrefix(view);
    	if( path != null)
    	{
    		Buffer buffer = view.getBuffer();
    		int caret = view.getTextArea().getCaretPosition();
    		buffer.remove(caret - path.length(), path.length());
    	}
        
        // Check if a parametrized abbreviation is needed
        String sig = getDescription();
        if (sig == null || sig.length() == 0)
        {
            return;
        }
        String abbrev = CompletionUtil.createAbbrev(sig);
        SuperAbbrevs.expandAbbrev(view, abbrev, null);
    }
    
    @Override
    public String getDescription()
    {
    	return description;
    }
    
    public static String getCompletionPrefix(View view)
    {
    	TextArea textArea = view.getTextArea();
    	String prefix = "";
    	int caret = textArea.getCaretPosition() - 1;
    	if (caret <= 0) {
    		return "";
    	}
    	String token = textArea.getText(caret, 1);
    	while (caret > -1 && isIncludeFilePart(token.charAt(0))) {
    		prefix = token + prefix;
    		caret--;
    		if (caret < 0) {
    			break;
    		}
    		token = textArea.getText(caret, 1);
    	}
    	return prefix;
    }
    
    private static boolean isIncludeFilePart(char c)
    {
    	return c != '\\' && c != '/' && c != '<' && c != '"' && c != '\r' && c != '\n';
    }
}
