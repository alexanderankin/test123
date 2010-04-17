/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2005 Marcelo Vanzin
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

import org.gjt.sp.jedit.jEdit;

import projectviewer.vpt.VPTNode;

import p4plugin.Perforce;
import p4plugin.config.P4GlobalConfig;

/**
 *  Calls a customizable p4 command to show information about a file.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4FileInfoAction extends AsyncP4Action {

    private String path;
    private final String cmd;
    private final VPTNode node;

    public P4FileInfoAction(String cmd) {
        this(null, cmd);
    }

    public P4FileInfoAction(VPTNode node,
                            String cmd)
    {
        super(getActionName(cmd, false), false);
        this.cmd = cmd;
        this.node = node;
    }

    protected String getCommand() {
        return cmd;
    }

	protected String[] getArgs(ActionEvent ae) {
        return new String[] { path };
    }

    public void actionPerformed(ActionEvent ae) {
        VPTNode n = (node != null) ? node : viewer.getSelectedNode();
        path = n.getNodePath();
        super.actionPerformed(ae);
    }

    public void prepareForNode(VPTNode node) {
        getMenuItem().setVisible(node != null && node.isFile());
    }

    /** Shows the output in a dialog. */
    protected void postProcess(Perforce p4) {
        if ("diff".equals(getCommand()) &&
            P4GlobalConfig.getInstance().getIgnoreDiffOutput())
        {
            return;
        }
        String title =
            jEdit.getProperty("p4plugin.action." + getCommand() + ".title",
                              new String[] { path });
        showOutputDialog(p4, title);
    }

}

