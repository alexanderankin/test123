/*
 * toggle_dual_diff.java
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


import java.awt.Component;
import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;

import org.gjt.sp.util.Log;


public class toggle_dual_diff extends EditAction {
    public toggle_dual_diff() {
        super("toggle-dual-diff");
    }


    public boolean isToggle() {
        return true;
    }


    public boolean isSelected(Component comp) {
        View view = EditAction.getView(comp);
        return DualDiff.isEnabledFor(view);
    }


    public void actionPerformed(ActionEvent evt) {
        View view = EditAction.getView(evt);
        EditPane[] editPanes = view.getEditPanes();
        if (editPanes.length != 2) {
            Log.log(Log.DEBUG, this, "Splitting The view has to be split in two");
            if (editPanes.length > 2) {
                view.unsplit();
            }
            view.splitVertically();
        }

        if (DualDiff.isEnabledFor(view)) {
            DualDiff.removeFrom(view);
        } else {
            DualDiff.addTo(view);
        }
    }
}
