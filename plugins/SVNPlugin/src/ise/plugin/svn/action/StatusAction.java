package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.SwingUtilities;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.command.Status;
import ise.plugin.svn.command.CommitData;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.io.ConsolePrintStream;

import org.tmatesoft.svn.core.wc.SVNStatus;

public class StatusAction extends NodeActor {


    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            final CommitData cd = new CommitData();
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null && node.getNodePath() != null ) {
                    paths.add( node.getNodePath() );
                }
            }
            cd.setPaths( paths );
            if ( username != null && password != null ) {
                cd.setUsername( username );
                cd.setPassword( password );
            }

            cd.setOut( new ConsolePrintStream(this));

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            view.getDockableWindowManager().showDockableWindow( "console" );
                            try {
                                Status status = new Status();
                                List<SVNStatus> results = status.getStatus( cd );
                            }
                            catch ( Exception e ) {
                                cd.getOut().printError( e.getMessage() );
                            }
                        }
                    }
                                      );
        }
    }

}
