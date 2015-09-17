package ise.plugin.bmp;


import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;

//import org.gjt.sp.util.Log;

/**
 * This plugin stores buffer-local properties in a file and restores those
 * setting when the file is next opened.
 *
 * This plugin provides a WindowListener that is attached to each view so that
 * the auto-close timer only applies when the window is actually active.  If
 * the window is deactivated or iconified, the timer is stopped.
 *
 * @author    Dale Anson
 * @version   $Revision$
 * @since     Oct 1, 2003
 */
public class BufferLocalPlugin extends EBPlugin {
    
    private BufferMinder bufferMinder;
    private BufferLocal bufferLocal;


    public static String NAME = "bufferlocal";


    /**
     * Load the stored buffer local properties. The properties are stored in a
     * file named .bufferlocalplugin.cfg in either the jEdit settings directory
     * (if writable) otherwise, in $user.home.
     */
    public void start() {
        bufferMinder = new BufferMinder();
        bufferLocal = new BufferLocal();
    }


    /**
     * Save the buffer local properties to disk.
     *
     * @see   start
     */
    public void stop() {
        bufferMinder.stop();
        bufferLocal.stop();
    }

    /**
     * Check for BufferUpdate messages. Save properties on CLOSED, restore
     * properties on LOADED.
     *
     * @param message
     */
    public void handleMessage( EBMessage message ) {
        bufferMinder.handleMessage(message);
        bufferLocal.handleMessage(message);
    }

}