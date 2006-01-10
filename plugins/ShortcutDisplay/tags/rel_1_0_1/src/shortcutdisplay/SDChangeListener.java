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
 *@created    December 17, 2005
 */
public class SDChangeListener implements ChangeListener
{

    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    public void stateChanged( ChangeEvent e )
    {
        Log.log(Log.DEBUG,this,"got stateChanged event: " + e);
        ShortcutPrefixActiveEvent evt = ( ShortcutPrefixActiveEvent ) e;
        if ( evt.getActive() == true )
        {
            ShortcutDisplay.displayShortcuts( evt.getBindings() );
        }
        else
        {
            ShortcutDisplay.disposeShortcuts();
        }
    }
}

