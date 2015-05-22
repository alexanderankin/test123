package android.actions;

import org.gjt.sp.jedit.*;

import console.ConsolePlugin;
import console.Console;
import console.Shell;

import java.awt.Component;
import javax.swing.*;

public class Util {
    
    static final ImageIcon ICON = new ImageIcon(Util.class.getClassLoader().getResource("android/android_icon.png"));   
    static final ImageIcon ERROR_ICON = new ImageIcon(Util.class.getClassLoader().getResource("android/android_error_icon.png"));
    
    static boolean isProjectViewerAvailable() {
        EditPlugin pv = jEdit.getPlugin( "projectviewer.ProjectPlugin", false );
        return pv != null;
    }

    static void runInSystemShell( View view, String command ) {
        view.getDockableWindowManager().showDockableWindow( "console" );
        Console console = ConsolePlugin.getConsole( view );
        Shell shell = Shell.getShell( "System" );
        shell.waitFor( console );
        console.run( shell, command );
    }

    static void showError( Component parent, String title, String msg ) {
        displayMessage( parent, title, msg, JOptionPane.ERROR_MESSAGE, ERROR_ICON );
    }

    static void showMessage( Component parent, String title, String msg ) {
        displayMessage( parent, title, msg, JOptionPane.INFORMATION_MESSAGE, ICON );
    }

    private static void displayMessage( final Component parent, final String title, final String message, final int messageType, final Icon icon) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog( parent, message, title, messageType, icon );
            }
        } );
    }

}