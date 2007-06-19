package ise.plugin.svn.io;

import java.awt.Color;
import java.io.*;

import java.util.logging.*;

import org.gjt.sp.jedit.View;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.action.NodeActor;

public class ConsolePrintStream extends PrintStream {

    public static String LS = System.getProperty( "line.separator" );

    public ConsolePrintStream(OutputStream os) {
        super(os, true);
    }

    public ConsolePrintStream( NodeActor na ) {
        super( new LogOutputStream( na ), true );
    }

    // print a message to the system shell in the Console plugin.  This is an
    // easy way to display output without a lot of work.
    public void print( String msg ) {
        print( msg, Level.INFO );
    }

    public void print( String msg, Level level ) {
        if ( msg == null || msg.length() == 0 ) {
            return ;
        }
        if ( level == null ) {
            level = Level.INFO;
        }
        LogOutputStream los = (LogOutputStream)out;
        los.setLevel(level);
        super.print(msg);
    }

    public void println( String msg ) {
        print( msg + LS );
    }

    public void println( String msg, Level level ) {
        print( msg + LS, level );
    }


    public void printError( String msg ) {
        print( msg + LS, Level.SEVERE );
    }

}
