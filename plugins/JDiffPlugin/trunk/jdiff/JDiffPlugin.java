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


package jdiff;


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
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import org.gjt.sp.util.Log;


public class JDiffPlugin extends EBPlugin
{
    public static Color overviewChangedColor;
    public static Color overviewDeletedColor;
    public static Color overviewInsertedColor;
    public static Color overviewInvalidColor;

    public static Color leftCursorColor;
    public static Color rightCursorColor;

    public static Color highlightChangedColor;
    public static Color highlightDeletedColor;
    public static Color highlightInsertedColor;
    public static Color highlightInvalidColor;

    public static Color selectedHighlightChangedColor;
    public static Color selectedHighlightDeletedColor;
    public static Color selectedHighlightInsertedColor;

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
        OptionGroup jdiffGroup = new OptionGroup(
            jEdit.getProperty("options.jdiff.label")
        );

        jdiffGroup.addOptionPane(new JDiffOptionPane());
        jdiffGroup.addOptionPane(new JDiffOverviewOptionPane());
        jdiffGroup.addOptionPane(new JDiffHighlightOptionPane());

        dialog.addOptionGroup(jdiffGroup);
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
        // Overview colors
        overviewChangedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.overview-changed-color", "#FFCC66")
        );
        overviewDeletedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.overview-deleted-color", "#FF6666")
        );
        overviewInsertedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.overview-inserted-color", "#99CC66")
        );
        overviewInvalidColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.overview-invalid-color", "#CCCCCC")
        );

        leftCursorColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.left-cursor-color", "#000000")
        );
        rightCursorColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.right-cursor-color", "#000000")
        );

        // Highlight colors
        highlightChangedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.highlight-changed-color", "#FFFF90")
        );
        highlightDeletedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.highlight-deleted-color", "#FF9090")
        );
        highlightInsertedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.highlight-inserted-color", "#D9FF90")
        );

        highlightInvalidColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.highlight-invalid-color", "#909090")
        );

        // Selected highlight colors
        selectedHighlightChangedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.selected-highlight-changed-color", "#FFCC66")
        );
        selectedHighlightDeletedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.selected-highlight-deleted-color", "#FF6666")
        );
        selectedHighlightInsertedColor = GUIUtilities.parseColor(
            jEdit.getProperty("jdiff.selected-highlight-inserted-color", "#99CC66")
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
