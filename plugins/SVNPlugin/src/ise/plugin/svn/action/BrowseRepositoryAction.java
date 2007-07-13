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

            private Cursor cursor = null;

            @Override
            public List<DirTreeNode> doInBackground() {
                cursor = tree.getCursor();
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
                    tree.setCursor(cursor);
                    tree.setEditable(true);
                }
            }
        }
        ( new Runner() ).execute();
    }
}
