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


import java.awt.Color;

import java.util.Vector;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import org.gjt.sp.util.Log;


public class JDiffPlugin
    extends EBPlugin
{

    public static Color changedLineColor;
    public static Color deletedLineColor;
    public static Color insertedLineColor;
    public static Color invalidLineColor;

    public static Color changedHunkColor;
    public static Color deletedHunkColor;
    public static Color insertedHunkColor;
    public static Color invalidHunkColor;

    public static Color leftCursorColor;
    public static Color rightCursorColor;

    static {
        propertiesChanged();
    }


    public JDiffPlugin() {
        super();
    }


    public void start() {}


    public void stop() {}


    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu("jdiff-menu"));
    }


    public void createOptionPanes(OptionsDialog dialog) {
        dialog.addOptionPane(new JDiffOptionPane());
    }


    public void handleMessage(EBMessage message) {
        if (message instanceof PropertiesChanged) {
            DualDiff.propertiesChanged();
            JDiffPlugin.propertiesChanged();
        } else if (message instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate) message;
            EditPane editPane = epu.getEditPane();
            View view = editPane.getView();
            if (!DualDiff.isEnabledFor(view)) {
                return;
            }
            if (epu.getWhat() == EditPaneUpdate.CREATED) {
                DualDiff.editPaneCreated(view, editPane);
            } else if (epu.getWhat() == EditPaneUpdate.DESTROYED) {
                DualDiff.editPaneDestroyed(view, editPane);
            } else if (epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED) {
                Log.log(Log.DEBUG, this, "Buffer loaded? " + editPane.getBuffer().isLoaded());
                if (editPane.getBuffer().isLoaded()) {
                    DualDiff.editPaneBufferChanged(view, editPane);
                } else {
                    new WaitForBuffer(view, editPane);
                }
            } else {
            }
        }
    }


    public static void propertiesChanged() {
        Color changedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.changed-color", "#FFCC66")
        );
        Color deletedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.deleted-color", "#FF6666")
        );
        Color insertedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.inserted-color", "#99CC66")
        );
        Color invalidColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.invalid-color", "#CCCCCC")
        );

        if (jEdit.getBooleanProperty("jdiff.brighter-highlight", true)) {
            changedLineColor  = changedColor.brighter();
            deletedLineColor  = deletedColor.brighter();
            insertedLineColor = insertedColor.brighter();
            invalidLineColor  = invalidColor.brighter();
        } else {
            changedLineColor  = changedColor;
            deletedLineColor  = deletedColor;
            insertedLineColor = insertedColor;
            invalidLineColor  = invalidColor;
        }

        if (jEdit.getBooleanProperty("jdiff.darker-overview", false)) {
            changedHunkColor  = changedColor.darker();
            deletedHunkColor  = deletedColor.darker();
            insertedHunkColor = insertedColor.darker();
            invalidHunkColor  = invalidColor.darker();
        } else {
            changedHunkColor  = changedColor;
            deletedHunkColor  = deletedColor;
            insertedHunkColor = insertedColor;
            invalidHunkColor  = invalidColor;
        }

        leftCursorColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.left-cursor-color", "#000000")
        );
        rightCursorColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.right-cursor-color", "#000000")
        );
    }


    private static class WaitForBuffer implements EBComponent {
        private View view;
        private EditPane editPane;


        public WaitForBuffer(View view, EditPane editPane) {
            this.view = view;
            this.editPane = editPane;
            EditBus.addToBus(this);
        }


        public void handleMessage(EBMessage message) {
            if (message instanceof BufferUpdate) {
                BufferUpdate bu = (BufferUpdate) message;
                if (bu.getWhat() == BufferUpdate.LOADED) {
                    if (bu.getBuffer() == editPane.getBuffer()) {
                        EditBus.removeFromBus(this);
                        DualDiff.editPaneBufferChanged(view, editPane);
                    }
                }
            }
        }
    }
}
