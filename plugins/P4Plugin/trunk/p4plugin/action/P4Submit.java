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
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;

import p4plugin.Perforce;
import p4plugin.config.P4Config;

/**
 *  Submits a changes list, or a file.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4Submit extends AsyncP4Action {

    private final boolean fileOnly;

	public P4Submit() {
		super(true);
        this.fileOnly = false;
	}

    /**
     *  The argument is actually ignored and is, internally,
     *  always "true". This is to differentiate the super class
     *  constructor that is called, since it has to be the
     *  first statement in the constructor...
     */
    public P4Submit(boolean fileOnly) {
        super(getActionName("submit", false), false);
        this.fileOnly = true;
    }

    public String getCommand() {
        return "submit";
    }

    public String[] getArgs(ActionEvent ae) {
        if (fileOnly) {
            VPTNode node = viewer.getSelectedNode();
            return new String[] { node.getNodePath() };
        } else {
            return null;
        }
    }

    public void prepareForNode(VPTNode node) {
        if (fileOnly) {
            getMenuItem().setVisible(node != null && node.isFile());
        }
    }

    protected void postProcess(Perforce p4) {
        showInShell(p4);
        if (fileOnly) {
            VPTNode node = viewer.getSelectedNode();
            if (node.isOpened()) {
                jEdit.getBuffer(node.getNodePath()).checkFileStatus(viewer.getView());
            }
            ProjectViewer.nodeChanged(viewer.getSelectedNode());
        } else {
            viewer.repaint();
        }
    }

}

