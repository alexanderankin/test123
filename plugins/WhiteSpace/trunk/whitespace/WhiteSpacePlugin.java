/*
 * WhiteSpacePlugin.java
 * Copyright (c) 2000-2001 Andre Kaplan
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


package whitespace;


import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

import whitespace.WhiteSpaceModel;


public class WhiteSpacePlugin
    extends EBPlugin
{
    public void start() {
        // init buffer properties & models
        WhiteSpaceDefaults.editorStarted();
        // add highlights to editpanes
        View view = jEdit.getFirstView();
        while(view != null) {
            EditPane[] panes = view.getEditPanes();
            for(int i=0; i < panes.length; i++) {
                addHighlightsTo(panes[i]);
            }
            view = view.getNext();
        }
    }


    public void stop() {
        Log.log(Log.DEBUG, this, "removing all highlights");
        View view = jEdit.getFirstView();
        while(view != null) {
            EditPane[] panes = view.getEditPanes();
            for(int i=0; i < panes.length; i++) {
                BlockHighlight.removeHighlightFrom(panes[i]);
                WhiteSpaceHighlight.removeHighlightFrom(panes[i]);
                FoldHighlight.removeHighlightFrom(panes[i]);
            }
            view = view.getNext();
        }
        Log.log(Log.DEBUG, this, "removing WhiteSpaceModel properties");
        Buffer[] buffers = jEdit.getBuffers();
        for(int i=0; i < buffers.length; i++) {
            buffers[i].setProperty(WhiteSpaceModel.MODEL_PROPERTY,null);
        }
    }


    // EditBus
    public void handleMessage(EBMessage message) {
        if (message instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate) message;
            EditPane editPane = (EditPane) epu.getSource();
            if (epu.getWhat() == EditPaneUpdate.CREATED) {
                this.addHighlightsTo(editPane);
            } else if (epu.getWhat() == EditPaneUpdate.DESTROYED) {
                BlockHighlight.removeHighlightFrom(editPane);
                WhiteSpaceHighlight.removeHighlightFrom(editPane);
                FoldHighlight.removeHighlightFrom(editPane);
            }
        } else if (message instanceof PropertiesChanged) {
            BlockHighlight.propertiesChanged();
            FoldHighlight.propertiesChanged();
            WhiteSpaceHighlight.propertiesChanged();
        } else if (message instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate) message;
            if (bu.getWhat() == BufferUpdate.SAVING) {
                this.bufferSaving(bu.getBuffer());
            } else if (
                       (bu.getWhat() == BufferUpdate.CREATED)
                    || (bu.getWhat() == BufferUpdate.DIRTY_CHANGED)
            ) {
                WhiteSpaceDefaults.bufferCreated(bu.getBuffer());
            }
        }
    }


    private void addHighlightsTo(EditPane editPane) {
        // drawn third
        BlockHighlight.addHighlightTo(editPane);
        // drawn second
        WhiteSpaceHighlight.addHighlightTo(editPane);
        // drawn first
        FoldHighlight.addHighlightTo(editPane);
    }


    private void bufferSaving(JEditBuffer buffer) {
        WhiteSpaceModel model = (WhiteSpaceModel) buffer.getProperty(
            WhiteSpaceModel.MODEL_PROPERTY
        );
        if (model == null) { return; }

        boolean removeTrailingWhitespace    =
            model.getRemoveTrailingWhitespace().isEnabled();
        boolean softTabifyLeadingWhitespace =
            model.getSoftTabifyLeadingWhitespace().isEnabled();

        boolean tabifyLeadingWhitespace   = false;
        boolean untabifyLeadingWhitespace = false;
        if (softTabifyLeadingWhitespace) {
            boolean noTabs = buffer.getBooleanProperty("noTabs");
            tabifyLeadingWhitespace   = !noTabs;
            untabifyLeadingWhitespace = noTabs;
        } else {
            tabifyLeadingWhitespace   =
                model.getTabifyLeadingWhitespace().isEnabled();
            untabifyLeadingWhitespace =
                model.getUntabifyLeadingWhitespace().isEnabled();
        }

        if (    !removeTrailingWhitespace
             && !tabifyLeadingWhitespace
             && !untabifyLeadingWhitespace
        ) {
            return;
        }

        long start = System.currentTimeMillis();
        buffer.beginCompoundEdit();

        if (removeTrailingWhitespace) {
            // Escape characters
            String escapeChars = jEdit.getProperty("white-space.escape-chars", "");
            DocumentUtilities.removeTrailingWhiteSpace(buffer, escapeChars);
        }

        if (tabifyLeadingWhitespace != untabifyLeadingWhitespace) {
            if (tabifyLeadingWhitespace) {
                DocumentUtilities.tabifyLeading(buffer, buffer.getTabSize());
            }

            if (untabifyLeadingWhitespace) {
                DocumentUtilities.untabifyLeading(buffer, buffer.getTabSize());
            }
        }

        buffer.endCompoundEdit();
        long end = System.currentTimeMillis();
        Log.log(Log.DEBUG, this, "bufferSaving: " + (end - start) + " ms");
    }
}

