/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2007 Marcelo Vanzin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package p4plugin.action;

import java.awt.event.ActionEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import projectviewer.PVActions;

import p4plugin.Perforce;
import p4plugin.P4ClientInfo;
import p4plugin.config.P4Config;

/**
 *  Opens all the files that are part of a changelist in jEdit.
 *	Optionally, close all other files that are currently opened.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.2.3
 */
public class P4OpenFiles extends AsyncP4Action
                         implements Perforce.Visitor

{

    private boolean         closeOthers;
    private List<String>    files;
    private Pattern         fspec;
    private P4ClientInfo    cinfo;

    /** This submits a single file (the selected node in the PV tree). */
    public P4OpenFiles(boolean closeOthers) {
        super(closeOthers ? "p4plugin_open_files_ex" : "p4plugin_open_files", true);
        this.closeOthers = closeOthers;
        this.fspec = Pattern.compile("(.*)#[0-9]+ - (.*?) (default|change) .*");
        setVisitor(this);
    }

    public String getCommand() {
        return "opened";
    }

    public String[] getArgs(ActionEvent ae) {
        return null;
    }

    public void run(ActionEvent ae) {
        cinfo = new P4ClientInfo();
        if (!cinfo.fetch()) {
            return;
        }
        super.run(ae);
    }

    public boolean process(String line) {
        Matcher m = fspec.matcher(line);
        if (m.matches()) {
            String path = m.group(1);
            String action = m.group(2);
            if ("edit".equals(action) || "add".equals(action)) {
                if (files == null) {
                    files = new LinkedList<String>();
                }
                files.add(path);
            }
        }
        return true;
    }

    protected void postProcess(Perforce p4) {
        if (files != null) {
            if (closeOthers) {
                for (Buffer b : jEdit.getBuffers()) {
                    jEdit.closeBuffer(jEdit.getActiveView(), b);
                }
            }
            for (String path : files) {
                jEdit.openFile(jEdit.getActiveView(), cinfo.translate(path));
            }
        } else {
            // nothing to open
            PVActions.swingInvoke(
                new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(
                            jEdit.getActiveView(),
                            jEdit.getProperty("p4plugin.action.openfiles.no_files"),
                            jEdit.getProperty("p4plugin.action.openfiles.no_files.title"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            );
        }
        // free memory.
        cinfo = null;
        files = null;
    }

}

