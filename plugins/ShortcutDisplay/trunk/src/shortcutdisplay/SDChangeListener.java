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
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 17, 2005
 */
public class SDChangeListener implements ChangeListener
{

    private Timer popupTimer;
    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    public void stateChanged( ChangeEvent e )
    {
        // Log.log(Log.DEBUG,this,"got stateChanged event: " + e);
        final ShortcutPrefixActiveEvent evt = ( ShortcutPrefixActiveEvent ) e;
        if ( evt.getActive() == true )
        {
            popupTimer = new Timer(jEdit.getIntegerProperty("options.shortcut-display.popup.delay", 500), new ActionListener()  //2 second delay for testing
                {
                    public void actionPerformed(ActionEvent dontcare)
                    {
                        ShortcutDisplay.displayShortcuts( evt.getBindings() );
                    }
                }
            );
            popupTimer.setRepeats(false);
            popupTimer.start();
        }
        else
        {
            if(popupTimer==null || popupTimer.isRunning())
                popupTimer.stop();
            ShortcutDisplay.disposeShortcuts();
        }
    }
}

