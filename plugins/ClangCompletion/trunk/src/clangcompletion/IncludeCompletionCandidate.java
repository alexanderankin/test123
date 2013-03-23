package clangcompletion;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;

import superabbrevs.SuperAbbrevs;

import completion.util.*;

class IncludeCompletionCandidate extends BaseCompletionCandidate
{
	private String prefix;
	
	public IncludeCompletionCandidate(String prefix, String desc)
	{
		super(desc);
		this.prefix = prefix;
		description = desc;
	}
	
    @Override
    public boolean isValid (View view)
    {
    	return (description != null && description.startsWith(prefix));
    }
    
    @Override
    public void complete (View view)
    {
        TextArea textArea = view.getTextArea();
        int caret = textArea.getCaretPosition();
        JEditBuffer buffer = textArea.getBuffer();
        if (prefix.length() > 0)
        {
            buffer.remove(caret - prefix.length(), prefix.length());
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
}
