package android.actions;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

public class LaunchDDMS implements Command {

    private View view;

    public void execute( View view ) {
        this.view = view;
        startDDMS();
    }

    void startDDMS() {
        try {
            boolean hasMonitor = false;
            String sdkPath = jEdit.getProperty( "android.sdk.path", "" );
            
            // check for which debug monitor to use, the new one is "monitor, the old one is "ddms".
            if ( !sdkPath.isEmpty() ) {
                sdkPath += "/tools/";
                File monitorFile = new File(sdkPath, "monitor");
                hasMonitor = monitorFile.exists();
            } else {
                String envPath = System.getenv("PATH");
                String[] paths = envPath.split(System.getProperty("path.separator"));
                for (String path : paths) {
                    File monitorFile = new File(path, "monitor");
                    hasMonitor = monitorFile.exists();
                    if (hasMonitor) {
                        break;   
                    }
                }
            }
            
            Runtime.getRuntime().exec( sdkPath + (hasMonitor ? "monitor" : "ddms") );
        } catch ( Exception e ) {
            e.printStackTrace();
            Util.showError( view, jEdit.getProperty( "android.Error", "Error" ), e.getMessage() );
        }
    }
}

