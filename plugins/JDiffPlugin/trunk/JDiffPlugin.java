/*
 * JDiffPlugin.java
 * Copyright (c) 2000 Andre Kaplan
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


import java.util.Vector;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import org.gjt.sp.util.Log;


public class JDiffPlugin
    extends EBPlugin
{
    public JDiffPlugin() {
        super();
    }


    public void start() {
        jEdit.addAction(new toggle_dual_diff());
    }


    public void stop() {}


    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu("jdiff-menu"));
    }


    public void createOptionPanes(OptionsDialog dialog) {
        dialog.addOptionPane(new JDiffOptionPane());
    }


    public void handleMessage(EBMessage message) {
        if (message instanceof PropertiesChanged) {
            DiffOverview.propertiesChanged();
            DualDiff.propertiesChanged();
        } else if (message instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate) message;
            EditPane editPane = epu.getEditPane();
            View view = editPane.getView();
            if (!DualDiff.isEnabledFor(view)) {
                return;
            }
            if (epu.getWhat() == EditPaneUpdate.CREATED) {
                DualDiff.removeFrom(view);
            } else if (epu.getWhat() == EditPaneUpdate.DESTROYED) {
                DualDiff.removeFrom(view);
            } else if (epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED) {
                DualDiff.removeFrom(view);
                DualDiff.addTo(view);
            } else {
            }
        }
    }
}
