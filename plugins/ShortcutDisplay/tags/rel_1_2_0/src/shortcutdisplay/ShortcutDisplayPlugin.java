package shortcutdisplay;

import javax.swing.event.*;
import java.awt.Component;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.gui.*;


/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 18, 2005
 */
public class ShortcutDisplayPlugin extends EBPlugin
{

    private SDChangeListener listener = new SDChangeListener();
    /**
     *  Description of the Method new shortcutdisplay.ShortcutDisplayPlugin().start()
     */
    public void start()
    {
        ShortcutPrefixActiveEvent.addChangeEventListener(listener);
    }


    /**
     *  Description of the Method
     */
    public void stop()
    {
        ShortcutPrefixActiveEvent.removeChangeEventListener(listener);
    }


    /**
     *  Description of the Method
     *
     *@param  msg  Description of the Parameter
     */
    public void handleMessage( EBMessage msg )
    {
        return;
    }
}


