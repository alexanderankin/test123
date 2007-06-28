package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Log;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.gui.LogResultsPanel;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;
import projectviewer.vpt.VPTNode;
import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * Action for ProjectViewer's context menu to execute an svn log.
 */
public class LogActor extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null ) {
                    paths.add( node.getNodePath() );
                }
            }

            LogAction la = new LogAction(view, paths, username, password);
            la.actionPerformed(ae);
        }
    }
}
