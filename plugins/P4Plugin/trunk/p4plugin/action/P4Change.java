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

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import projectviewer.action.Action;

import p4plugin.Perforce;
import p4plugin.config.P4Config;

/**
 *  Calls "p4 user" and hopes for the best.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4Change extends AsyncP4Action {

    private String changelist;

    public String getCommand() {
        return "change";
    }

    public String[] getArgs(ActionEvent ae) {
        if (changelist != null)
            return new String[] { changelist };
        return null;
    }

    protected void run(ActionEvent ae) {
        CListChooser chooser = new CListChooser();
        try {
            SwingUtilities.invokeAndWait(chooser);
        } catch (Exception e) {
            Log.log(Log.ERROR, this, e);
            return;
        }
        this.changelist = chooser.change;
        invokePerforce(null, ae);
        this.changelist = null;
    }

    protected void postProcess(Perforce p4) {
        showInShell(p4);
    }

}

