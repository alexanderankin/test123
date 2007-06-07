package ise.plugin.svn.action;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import projectviewer.vpt.VPTNode;
import console.ConsolePlugin;
import console.Console;
import console.Output;
import org.gjt.sp.jedit.View;

import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

/**
 * The various action classes in this package extend this class.  The action
 * classes are added to JMenuItems for inclusion in the ProjectViewer context
 * menu.
 */
public abstract class NodeActor implements ActionListener {

    // this is the currently selected node in ProjectViewer
    protected VPTNode node = null;

    // this is the current view containing the ProjectViewer
    protected View view = null;

    // this is the root directory of the project
    protected String projectRoot = null;

    // the username as set for the project
    protected String username = null;

    // the password for the user
    protected String password = null;

    // subclasses need to implement this to provide the appropriate
    // parameters to their subversion command
    public abstract void actionPerformed(ActionEvent ae);

    // called by SVNAction to set the ProjectViewer node and the View.  This
    // is called each time the user raises the PV context menu, which will be
    // prior to the actionPerformed method being called.
    public void prepareForNode( VPTNode n, View v, String project_root, String username, String password ) {
        node = n;
        view = v;
        projectRoot = project_root;
        this.username = username;
        this.password = password;
        setupLibrary();
    }

    // print a message to the system shell in the Console plugin.  This is an
    // easy way to display output without a lot of work.
    public void print(String msg) {
        print(msg, null);
    }

    public void print(String msg, Color color) {
        if (msg == null || msg.length() == 0) {
            return;
        }
        if (color == null) {
            color = Color.BLUE;
        }
        Console console = ConsolePlugin.getConsole(view);
        console.setShell(ConsolePlugin.getSystemShell());
        Output output = console.getOutput();
        output.print(color, msg);
        output.print(Color.BLACK, "\n-------------------------------------------\n");
    }

    public void printError(String msg) {
        print(msg, Color.RED);
    }

    /*
     * Initializes the svnkit library to work with a repository via
     * different protocols.
     */
    private static void setupLibrary() {
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();

        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();
    }

}
