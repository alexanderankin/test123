package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import projectviewer.config.ProjectOptions;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.action.CheckoutAction;
import ise.plugin.svn.gui.CheckoutDialog;
import ise.plugin.svn.gui.OutputPanel;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.io.*;
import ise.plugin.svn.data.*;
import ise.plugin.svn.*;
import java.util.logging.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.swingworker.*;

public class CheckoutActor extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        CheckoutAction la = new CheckoutAction( view, username, password );
        la.actionPerformed( ae );
    }
}
