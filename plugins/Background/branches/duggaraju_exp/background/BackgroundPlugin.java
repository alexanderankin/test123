/*
 * BackgroundPlugin.java
 * Copyright (c) 2002 Andre Kaplan
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


package background;

import java.awt.*;
import java.util.Vector;

import javax.swing.*;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.util.Log;


public class BackgroundPlugin extends EBPlugin
{
    public void start() {}


    public void stop() {}


    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenuItem("background.toggle-background"));
    }


    public void createOptionPanes(OptionsDialog optionsDialog) {
        optionsDialog.addOptionPane(new BackgroundOptionPane());
    }


    public void handleMessage(EBMessage message) {
        if (message instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate) message;
            EditPane editPane = epu.getEditPane();

            if (epu.getWhat() == EditPaneUpdate.CREATED) {
                TextAreaPainter textAreaPainter = editPane.getTextArea().getPainter();

                BackgroundHighlight backgroundHighlight =
                    (BackgroundHighlight) BackgroundHighlight.addHighlightTo(editPane);

                textAreaPainter.addExtension(TextAreaPainter.BACKGROUND_LAYER, backgroundHighlight);
            } else if (epu.getWhat() == EditPaneUpdate.DESTROYED) {
                BackgroundHighlight.removeHighlightFrom(editPane);
            }
        } else if (message instanceof PropertiesChanged) {
            BackgroundHighlight.propertiesChanged();
        }
    }
}

