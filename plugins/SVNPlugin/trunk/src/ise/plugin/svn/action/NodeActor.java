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

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
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

    // this is a list of the currently selected nodes in ProjectViewer
    protected List<VPTNode> nodes = null;

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
    public void prepareForNode( List<VPTNode>n, View v, String project_root, String username, String password ) {
        nodes = n;
        view = v;
        projectRoot = project_root;
        this.username = username;
        this.password = password;
        setupLibrary();
    }

    public View getView() {
        return view;
    }

    // print a message to the system shell in the Console plugin.  This is an
    // easy way to display output without a lot of work.
    /**
     * @deprecated
     */
    public void print(String msg) {
        print(msg, null);
    }

    /**
     * @deprecated
     */
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
    }

    /**
     * @deprecated
     */
    public void close() {
        Console console = ConsolePlugin.getConsole(view);
        console.setShell(ConsolePlugin.getSystemShell());
        Output output = console.getOutput();
        output.print(Color.BLACK, "\n-------------------------------------------\n");
    }

    /**
     * @deprecated
     */
    public void printError(String msg) {
        print(msg, Color.RED);
    }

    /*
     * Initializes the svnkit library to work with a repository via
     * different protocols.
     */
    public static void setupLibrary() {
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
