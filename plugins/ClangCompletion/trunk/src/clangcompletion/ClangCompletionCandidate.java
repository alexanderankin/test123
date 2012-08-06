package clangcompletion;

import static completion.util.CompletionUtil.createAbbrev;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;


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
    
    public static ClangCompletionCandidate parse(String clangOutput)
    {
    	ClangCompletionCandidate candidate = new ClangCompletionCandidate();
    	String[] splits = clangOutput.split(":");
    	if(splits.length < 2)
    	{
    		return null;
    	}
    	
    	if(!splits[0].matches("\\s*COMPLETION\\s*"))
    	{
    		return null;
    	}
    	
    	candidate.description = splits[1].trim();
    	//System.out.println(splits[1].trim());
    	
    	if(splits.length>2)
    	{
    		//System.out.println(splits[2]);
    		candidate.labelText = splits[2];
    	}
    	
    	return candidate;
    }
    
    @Override
    public String getDescription ()
    {
        return description;
    }
    
    @Override
    public boolean isValid (View view)
    {
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
    }
    
    @Override
    public String getLabelText()
    {
        return labelText;
    }
    
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
}