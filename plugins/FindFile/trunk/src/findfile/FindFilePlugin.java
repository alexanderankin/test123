/**
 * FindFilePlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * A jEdit plugin that provides file search capabilities.
 * @author Nicholas O'Leary
 * @version $Id: FindFilePlugin.java,v 1.2 2004/04/01 21:26:19 olearyni Exp $
 */

package findfile;

/*
 * TODO: Delete + Rename??
 */

//{{{ imports
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
//}}}

public class FindFilePlugin extends EBPlugin {

    @Override public void handleMessage(EBMessage msg) {
	if (msg instanceof BufferUpdate) {
	    BufferUpdate bu = (BufferUpdate) msg;
	    if (bu.getWhat().equals(BufferUpdate.LOADED) || bu.getWhat().equals(BufferUpdate.CLOSED)) {
		// Thanks to Ollie Rutherfurd for the NPE fix.
		View view = bu.getView();
		if (view == null) {
		    view = jEdit.getActiveView();
		}

		FindFileResults results = (FindFileResults) view.getDockableWindowManager().getDockable("FindFilePlugin");
		if (results != null) {
		    results.repaint();
		}

	    } else{
		if (bu.getWhat().equals(BufferUpdate.PROPERTIES_CHANGED)) {
		    //Nothing at the moment.
		}

	    }
	}
    }
}
