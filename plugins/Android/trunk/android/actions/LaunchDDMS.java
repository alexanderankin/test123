package android.actions;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import ise.java.awt.KappaLayout;

public class LaunchDDMS implements Command {

    private View view;

    public void execute( View view ) {
        this.view = view;
        startDDMS();
    }

    void startDDMS() {
        try {
            Runtime.getRuntime().exec( "ddms" );
        } catch ( Exception e ) {
            e.printStackTrace();
            Util.showError( view, jEdit.getProperty( "android.Error", "Error" ), e.getMessage() );
        }
    }
}

