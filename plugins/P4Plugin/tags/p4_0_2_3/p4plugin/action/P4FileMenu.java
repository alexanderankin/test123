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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;

import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectViewer;
import projectviewer.action.Action;
import projectviewer.vpt.VPTNode;

import p4plugin.config.P4Config;

/**
 *  The sub-menu to be shown in the project viewer context menu.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4FileMenu extends Action {

    private JMenu           fileMenu;
    private List<Action>    actions;

    public String getText() {
        return jEdit.getProperty("p4plugin.action.file-menu");
    }

    public JComponent getMenuItem() {
        if (fileMenu == null) {
            fileMenu = new JMenu(getText());

            addAction(new P4FileAction("edit",   true));
            addAction(new P4FileAction("add",    true));
            addAction(new P4FileAction("delete", true));
            addAction(new P4FileAction("revert", true));
            addAction(new P4FileAction("edit",   false));
            addAction(new P4FileAction("add",    false));
            addAction(new P4FileAction("delete", false));
            addAction(new P4FileAction("reopen", false, false));
            addAction(new P4FileInfoAction("diff"));
            addAction(new P4FileInfoAction("fstat"));
            addAction(new P4FileInfoAction("filelog"));
            addAction(new P4Submit(true));
            addAction(new P4SyncAction());
        }
        return fileMenu;
    }

    public void prepareForNode(VPTNode node) {
        P4Config cfg = P4Config.getProjectConfig(jEdit.getActiveView());
        if (cfg != null) {
            boolean show = false;
            for (Action a : actions) {
                a.prepareForNode(node);
                if (a.getMenuItem().isVisible()) {
                    show = true;
                }
            }
            getMenuItem().setVisible(show);
        } else {
            getMenuItem().setVisible(false);
        }
    }

    public void actionPerformed(ActionEvent ae) {
        // no-op.
    }

    public void setViewer(ProjectViewer viewer) {
        if (actions != null)
            for (Iterator i = actions.iterator(); i.hasNext(); )
                ((Action)i.next()).setViewer(viewer);
        super.setViewer(viewer);
    }

    private void addAction(Action a) {
        a.setViewer(viewer);
        if (actions == null)
            actions = new LinkedList<Action>();
        actions.add(a);
        fileMenu.add(a.getMenuItem());
    }
}

