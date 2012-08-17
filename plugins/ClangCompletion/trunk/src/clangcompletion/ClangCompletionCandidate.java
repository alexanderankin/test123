package clangcompletion;

import static completion.util.CompletionUtil.createAbbrev;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;

import superabbrevs.SuperAbbrevs;

import completion.service.CompletionCandidate;
import completion.util.*;

// import ctagsinterface.main.KindIconProvider;
// import ctagsinterface.main.Tag;

public class ClangCompletionCandidate  extends BaseCompletionCandidate
{
	private String labelText;
	protected ListCellRenderer renderer;
	
    private ClangCompletionCandidate()
    {
    	super("");
    	renderer = new ClangCompletionRenderer();
    }
    
    @Override
    public ListCellRenderer getCellRenderer()
    {
        return renderer;
    }
 
    @Override
    public void complete (View view)
    {
        TextArea textArea = view.getTextArea();
        String prefix = CompletionUtil.getCompletionPrefix(view);
        int caret = textArea.getCaretPosition();
        JEditBuffer buffer = textArea.getBuffer();
        if (prefix.length() > 0)
        {
            buffer.remove(caret - prefix.length(), prefix.length());
        }
        
        // Check if a parametrized abbreviation is needed
        String sig = getDescription();
        System.out.println(sig);
        if (sig != null && sig.length() != 0)
        {
        	String abbrev = createAbbrev(sig);
        	SuperAbbrevs.expandAbbrev(view, abbrev, null);
        }
    }
    
    public static ClangCompletionCandidate parse(String clangOutput)
    {
    	ClangCompletionCandidate candidate = new ClangCompletionCandidate();
    	String[] splits = clangOutput.split(":", 3);
    	if(splits.length < 3)
    	{
    		return null;
    	}
    	
    	/*
    	//delete regexp to speed up
    	if(!splits[0].matches("\\s*COMPLETION\\s*"))
    	{
    		return null;
    	}
    	*/
    	
    	candidate.description = splits[1].trim();
    	candidate.labelText =   splits[2].trim();
    	//System.out.println(splits[1].trim());
    	/*  ignore labeltext for now 
    	if(splits.length>2)
    	{
    		//System.out.println(splits[2]);
    		candidate.labelText = splits[2];
    	}
    	*/
    	return candidate;
    }
    
    @Override
    public String getDescription()
    {
    	String result = labelText;
    	int i;
    	i = labelText.lastIndexOf("]");
    	if(i > 0)
    	{
    		//delete retturn value;
    		result = result.substring(i+1);
    	}
    	
    	result = result.replace("<#","");
    	result = result.replace("#>","");
        return result;
    }
    
    @Override
    public boolean isValid (View view)
    {
    	return true;
    	/*
        String prefix = CompletionUtil.getCompletionPrefix(view);
        if (prefix == null || prefix.length() == 0) 
        {
            return true;
        }
        
        // System.out.println(description.startsWith("mai"));
        
        if( description.toLowerCase().startsWith(prefix.toLowerCase()))
        {
        	return true;
        }else
        {
        	return false;
        }
        */
    }
    
    @Override
    public String getLabelText()
    {
        return labelText;
    }
    /*
    @Override
    public boolean equals(Object obj)
    {
    	if(ClangCompletionCandidate.class.isInstance(obj))
    	{
    		ClangCompletionCandidate candidate = (ClangCompletionCandidate)obj;
    		return getDescription().equals(candidate.getDescription()) && 
    		getLabelText().equals(candidate.getLabelText());
    	}
    	return false;
    }
    */
}