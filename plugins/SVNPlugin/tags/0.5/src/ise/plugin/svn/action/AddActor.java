package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;
import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Add;
import ise.plugin.svn.command.Info;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.gui.AddDialog;
import ise.plugin.svn.gui.AddResultsPanel;
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

/**
 * Action for ProjectViewer's context menu to execute an svn add.
 */
public class AddActor extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null ) {
                    paths.add( node.getNodePath() );
                }
            }

            AddAction action = new AddAction(view, paths, username, password);
            action.actionPerformed(ae);
        }
    }
}
