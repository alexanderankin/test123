package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.SwingUtilities;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.command.Info;
import ise.plugin.svn.command.CommitData;
import ise.plugin.svn.library.GUIUtils;

public class InfoAction extends NodeActor {


    public void actionPerformed( ActionEvent ae ) {
        if ( node != null ) {
            final CommitData cd = new CommitData();
            List<String> paths = new ArrayList<String>();
            String path = node.getNodePath();
            paths.add( path );
            cd.setPaths( paths );
            if ( username != null && password != null ) {
                cd.setUsername( username );
                cd.setPassword( password );
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cd.setOut( new PrintStream( new BufferedOutputStream( baos ) ) );

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            view.getDockableWindowManager().showDockableWindow( "console" );
                            try {
                                Info info = new Info();
                                info.info( cd );
                                print( baos.toString() );
                            }
                            catch ( Exception e ) {
                                printError( e.getMessage() );
                            }
                        }
                    }
                                      );
        }
    }

}
