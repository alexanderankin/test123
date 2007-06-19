package ise.plugin.svn.io;

import java.io.*;
import java.util.logging.*;

import org.gjt.sp.jedit.View;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.action.NodeActor;

public class LogOutputStream extends OutputStream {

    private Logger logger = null;
    private Level level = Level.INFO;

    public LogOutputStream( NodeActor actor ) {
        OutputPanel panel = SVNPlugin.getOutputPanel( actor.getView() );
        logger = panel.getLogger();
    }

    public void setLevel( Level el ) {
        level = el;
    }

    public void write( int b ) {
        byte[] bytes = {( byte ) b};
        write( bytes, 0, 1 );
    }

    public void write( byte[] bytes, int offset, int length ) {
        String s = new String( bytes, offset, length );
        logger.log( level, s );
    }
    public void close() {
        logger.fine( "\n---------------------------------------\n" );
    }
}
