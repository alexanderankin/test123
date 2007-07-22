/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.action;

import ise.plugin.svn.action.NodeActor;
import ise.plugin.svn.gui.OutputPanel;
import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.BrowseRepository;
import ise.plugin.svn.data.CheckoutData;
import ise.plugin.svn.gui.DirTreeNode;
import ise.plugin.svn.gui.LogResultsPanel;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JTree;
import javax.swing.tree.*;
import org.gjt.sp.jedit.View;

public class BrowseRepositoryAction implements ActionListener {

    private View view = null;
    private JTree tree = null;
    private DirTreeNode node = null;
    private CheckoutData data = null;

    public BrowseRepositoryAction( View view, JTree tree, DirTreeNode node, CheckoutData data ) {
        this.view = view;
        this.tree = tree;
        this.node = node;
        this.data = data;
        if (view == null || tree == null || node == null || data == null || data.getURL() == null) {
            throw new IllegalArgumentException("neither view, tree, node, nor url can be null");
        }
        NodeActor.setupLibrary();
    }

    public void actionPerformed( ActionEvent ae ) {
        data.setOut( new ConsolePrintStream( view ) );
        view.getDockableWindowManager().showDockableWindow( "subversion" );
        view.getDockableWindowManager().showDockableWindow( "subversion.browser" );
        final OutputPanel panel = SVNPlugin.getOutputPanel( view );
        panel.showConsole();
        Logger logger = panel.getLogger();
        logger.log( Level.INFO, "Fetching repository info for\n" + data.getURL() + "..." );
        for ( Handler handler : logger.getHandlers() ) {
            handler.flush();
        }

        class Runner extends SwingWorker < List<DirTreeNode>, Object> {

            @Override
            public List<DirTreeNode> doInBackground() {
                tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                tree.setEditable(false);
                try {
                    BrowseRepository br = new BrowseRepository( );
                    return node.isExternal() ? br.getRepository(node, data) : br.getRepository( data );
                }
                catch ( Exception e ) {
                    data.getOut().printError( e.getMessage() );
                }
                finally {
                    data.getOut().close();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    List<DirTreeNode> children = get();
                    for (DirTreeNode child : children) {
                        node.add(child);
                    }
                    DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                    model.nodeStructureChanged(node);
                    TreePath path = new TreePath(node.getPath());
                    tree.invalidate();
                    tree.repaint();
                    tree.expandPath(path);
                }
                catch ( Exception e ) {
                    // ignored
                }
                finally {
                    tree.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    tree.setEditable(true);
                }
            }
        }
        ( new Runner() ).execute();
    }
}
