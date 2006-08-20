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

import p4plugin.Perforce;

/**
 *  Calls p4 fstat on a file.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4FileInfoAction extends P4FileAction {

    private String path;

    public P4FileInfoAction(String cmd) {
        super(cmd, true);
    }

    public void actionPerformed(ActionEvent ae) {
        path = viewer.getSelectedNode().getNodePath();
        invokePerforce(null, ae);
    }

    /** Shows the output in a dialog. */
    protected void postProcess(Perforce p4) {
        String title =
            jEdit.getProperty("p4plugin.action." + getCommand() + ".title",
                              new String[] { path });
        showOutputDialog(p4, title);
    }


}

