// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:

//{{{ imports
import java.util.*;
import ctags.bg.*;
import org.gjt.sp.util.Log;
//}}}

public class JumpHistory
{
    public Stack history;
    
//{{{ JumpHistory()
    public JumpHistory()
    {
        history = new Stack();   
    }//}}}
    
//{{{ add(CTAGS_Entry e)
    public void add(CTAGS_Entry e)
    {
        history.push(e); 
    }//}}}
    
//{{{ getPrevious()
    public CTAGS_Entry getPrevious()
    { 

       if (history.empty())
       {
           return null;
       }
       
       return (CTAGS_Entry)history.pop();
    }//}}}
    
//{{{ clear()
    public void clear()
    {
        history.clear();
        return;
    }//}}}

}
