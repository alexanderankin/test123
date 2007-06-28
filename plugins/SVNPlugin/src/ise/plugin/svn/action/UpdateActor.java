package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Update;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.UpdateData;
import ise.plugin.svn.gui.UpdateResultsPanel;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import projectviewer.vpt.VPTNode;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.gjt.sp.jedit.View;

/**
 * Action for ProjectViewer's context menu to execute an svn update.
 */
public class UpdateActor extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null ) {
                    paths.add( node.getNodePath() );
                }
            }

            UpdateAction action = new UpdateAction(view, paths, username, password);
            action.actionPerformed(ae);
        }
    }
}
