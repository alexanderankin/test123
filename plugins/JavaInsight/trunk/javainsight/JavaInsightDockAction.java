/*
 * JavaInsightDockAction.java - JavaInsight main action
 *
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


package javainsight;


import java.awt.Component;
import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.EditAction;

import org.gjt.sp.util.Log;


/**
 * jEdit action: dock JavaInsight in jEdit
 * @author Andre Kaplan
 * @version $Id$
**/
public class JavaInsightDockAction extends EditAction
{
    public JavaInsightDockAction() {
        super("javainsight.toggle-dockable");
    }

    public void actionPerformed(ActionEvent evt) {
        EditAction.getView(evt).getDockableWindowManager()
            .toggleDockableWindow(JavaInsightPlugin.DOCKABLE_NAME);
    }

    public boolean isToggle() {
        return true;
    }

    public boolean isSelected(Component comp) {
        return EditAction.getView(comp).getDockableWindowManager()
            .isDockableWindowVisible(JavaInsightPlugin.DOCKABLE_NAME);
    }
}

