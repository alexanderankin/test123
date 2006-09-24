
package beauty;


import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

import java.util.*;
import java.util.zip.*;
import javax.swing.*;

import beauty.beautifiers.*;


public class BeautyPlugin extends EBPlugin {

    /**
     * Beautify the current buffer using Beauty.
     *
     * @param buffer  The buffer to be beautified.
     * @param view  The view; may be null, if there is no current view.
     * @param showErrorDialogs  If true, modal error dialogs will be shown
     *        on error. Otherwise, the errors are silently logged.
     */
    public static void beautify( Buffer buffer, View view, boolean showErrorDialogs ) {
        if ( buffer.isReadOnly() ) {
            Log.log( Log.NOTICE, BeautyPlugin.class, jEdit.getProperty( "beauty.error.isReadOnly.message" ) );
            if ( showErrorDialogs )
                GUIUtilities.error( view, "beauty.error.isReadOnly", null );
            return ;
        }

        // load beautifier
        String mode = buffer.getStringProperty("beauty.beautifier");
        if (mode == null)
            mode = buffer.getMode().getName();
        Beautifier beautifier = (Beautifier)ServiceManager.getService(Beautifier.SERVICE_NAME, mode);
        if ( beautifier == null ) {
            if ( showErrorDialogs ) {
                JOptionPane.showMessageDialog(view, "Error: can't beautify this buffer because I don't know how to handle this mode.", 
                    "Beauty Error", JOptionPane.ERROR_MESSAGE); 
                return ;
            }
            else {
                Log.log( Log.NOTICE, BeautyPlugin.class, "buffer " + buffer.getName()
                        + " not beautified, because mode is not supported." );
                return ;
            }
        }

        // run the format routine synchronously on the AWT thread
        VFSManager.runInAWTThread( new BeautyThread( buffer, view, showErrorDialogs, beautifier ) );
    }

}

