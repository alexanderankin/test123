package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Info;
import ise.plugin.svn.data.CommitData;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;

import org.tmatesoft.svn.core.wc.SVNInfo;

public class InfoActor extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null ) {
                    paths.add( node.getNodePath() );
                }
            }

            InfoAction la = new InfoAction(view, paths, username, password);
            la.actionPerformed(ae);
        }
    }
}
