import java.util.*;
import ctags.bg.*;
import org.gjt.sp.util.Log;

public class JumpHistory
{
    public Stack history;
    
    public JumpHistory()
    {
        history = new Stack();   
    }
    
    public void add(CTAGS_Entry e)
    {
        history.push(e); 
    }
    
    // public void add(int e)
    // {
        // history.push(e);       
    // }   
    
    public CTAGS_Entry getPrevious()
    { 

       if (history.empty() == true)
       {
           return null;
       }
       
       return (CTAGS_Entry)history.pop();
    }
    
    public void clear()
    {
        history.clear();
        return;
    }
}
