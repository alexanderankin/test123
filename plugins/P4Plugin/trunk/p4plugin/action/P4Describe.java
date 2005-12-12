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

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import p4plugin.Perforce;

/**
 *  Shows a change list in a jEdit buffer.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4Describe extends AbstractP4Action {

    private String changelist;

    public P4Describe() {
        super(true);
    }

    public String getText() {
        return jEdit.getProperty("p4plugin.action.describe");
    }

	protected String getCommand() {
        return "describe";
    }

    /**
     *  Implementations should return a string array with the arguments
     *  to the p4 command according to the event (or the current state
     *  of the class).
     */
    protected String[] getArgs(ActionEvent ae) {
        return new String[] { changelist };
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            changelist = new ChangeListDialog(jEdit.getActiveView(), false, true)
                                .getChangeList(jEdit.getActiveView().getContentPane());
            if (changelist != null) {
                invokePerforce(null, ae);
            }
        } catch (IllegalArgumentException iae) {
            // dialog was canceled.
        }
    }

    protected void postProcess(Perforce p4) {
        String title = jEdit.getProperty("p4plugin.action.describe.title",
                                         new Object[] { changelist });
        showOutputDialog(p4, title);
    }

}

