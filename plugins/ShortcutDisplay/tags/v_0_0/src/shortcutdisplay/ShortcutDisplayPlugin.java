package shortcutdisplay;

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
    /**
     *  Description of the Method new shortcutdisplay.ShortcutDisplayPlugin().start()
     */
    public void startup()
    {
    }


    /**
     *  Description of the Method
     */
    public void stop()
    {
    }


    /**
     *  Description of the Method
     *
     *@param  msg  Description of the Parameter
     */
    public void handleMessage( EBMessage msg )
    {
        Log.log( Log.NOTICE, this, msg.toString() );
        if ( msg instanceof ShortcutPrefixActive )
        {
            ShortcutPrefixActive prefixMsg = ( ShortcutPrefixActive ) msg;
            if ( prefixMsg.getActive() == true )
            {
                ShortcutDisplay.displayShortcuts( prefixMsg.getBindings() );
            }
            else
            {
                ShortcutDisplay.disposeShortcuts();
            }
        }

    }
}

