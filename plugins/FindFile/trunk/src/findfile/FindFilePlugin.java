package findfile;

/*
* TODO: Delete + Rename
*/

//{{{ imports
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;
//}}}

/**
 * A jEdit plugin that provides file search capabilities.
 * @author Nicholas O'Leary
 * @version $Id: FindFilePlugin.java,v 1.1.1.1 2003-11-20 17:19:14 olearyni Exp $
 */
public class FindFilePlugin extends EBPlugin
{
   public void handleMessage(EBMessage msg)
   {
      if (msg instanceof BufferUpdate)
      {
         BufferUpdate bu = (BufferUpdate)msg;
         if (bu.getWhat().equals(BufferUpdate.LOADED) ||
             bu.getWhat().equals(BufferUpdate.CLOSED))
         {
            FindFileResults results = (FindFileResults)bu.getView().getDockableWindowManager().getDockable("FindFilePlugin");
            if (results != null)
               results.repaint();
         } else if(bu.getWhat().equals(BufferUpdate.PROPERTIES_CHANGED)) {
            //Nothing at the moment.
         }
      }
   }
}

