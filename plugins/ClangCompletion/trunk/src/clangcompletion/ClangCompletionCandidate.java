package clangcompletion;

import static completion.util.CompletionUtil.createAbbrev;

import javax.swing.ListCellRenderer;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;

import superabbrevs.SuperAbbrevs;

import completion.util.*;

// import ctagsinterface.main.KindIconProvider;
// import ctagsinterface.main.Tag;

public class ClangCompletionCandidate  extends BaseCompletionCandidate
{
	private String labelText;  
	
	private String description;
	
	private static int lengthOfClangOuputHeader = new String("COMPLETION: ").length();
	
	protected ListCellRenderer renderer;
	
    private ClangCompletionCandidate()
    {
    	super("");
    	renderer = new CompletionListCellRenderer();
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
    	StringBuilder label = new StringBuilder(clangOutput);
    	int indexOfDesc = label.indexOf(":", lengthOfClangOuputHeader) + 1;
    	if(indexOfDesc > 0)
    	{
    		label.delete(0, indexOfDesc);
    	}
    	
    	int indexOfSep = 0;
    	while((indexOfSep = label.indexOf("<#")) >= 0)
    	{
    		label.delete(indexOfSep, indexOfSep + 2);
    	}
    	
    	indexOfSep = 0;
    	while((indexOfSep = label.indexOf("#>")) >= 0)
    	{
    		label.delete(indexOfSep, indexOfSep + 2);
    	}
    	
    	while((indexOfSep = label.indexOf("{#")) >= 0)
    	{
    		label.delete(indexOfSep, indexOfSep + 2);
    	}
    	
    	indexOfSep = 0;
    	while((indexOfSep = label.indexOf("#}")) >= 0)
    	{
    		label.delete(indexOfSep, indexOfSep + 2);
    	}
    	
    	StringBuilder desc = new StringBuilder(label);
    	indexOfSep = 0;
    	while((indexOfSep = desc.indexOf("[#")) >= 0)
    	{
    		int indexOfSepend = desc.indexOf("#]", indexOfSep);
    		if(indexOfSepend > indexOfSep)
    		{
    			desc.delete(indexOfSep, indexOfSepend + 2);
    		}
    	}
    	
    	indexOfSep = 0;
    	while((indexOfSep = label.indexOf("#]")) >= 0)
    	{
    		label.replace(indexOfSep, indexOfSep + 2, " ");
    	}
    	
    	indexOfSep = 0;
    	while((indexOfSep = label.indexOf("[#")) >= 0)
    	{
    		label.delete(indexOfSep, indexOfSep + 2);
    	}
    	
    	while(desc.charAt(0) == ' ')
    	{
    		desc.delete(0, 1);
    	}
    	
    	ClangCompletionCandidate candidate = new ClangCompletionCandidate();
    	candidate.labelText = label.toString().trim();
    	candidate.description = desc.toString().trim();
    	
    	return candidate;
    }
    
    @Override
    public String getDescription()
    {
    	/* String result = labelText;
    	int i;
    	i = labelText.indexOf(description);
    	if(i >= 0)
    	{
    		//delete return value;
    		result = result.substring(i);
    	}
    	
    	i = result.indexOf("[");
    	if(i > 0)
    	{
    		//delete const in the end of func
    		result = result.substring(0, i);
    	}
    	
    	// : text inside [#...#] should be displayed but not isnert into text area
    	//COMPLETION: setText : [#void#][#CommonDialogLayer::#]setText(<#const char *text#>{#, <#int text_id#>#})
    	//COMPLETION: getString : [#const std::string &#]getString()[# const#]
    	result = result.replace("<#","");
    	result = result.replace("#>","");
    	result = result.replace("{#","");
    	result = result.replace("#}","");
        return result; */
        return description;
    }
    
    @Override
    public boolean isValid (View view)
    {
    	//return true;
    	
        String prefix = CompletionUtil.getCompletionPrefix(view);
        if (prefix == null /* || prefix.length() == 0 */) 
        {
            return false;
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