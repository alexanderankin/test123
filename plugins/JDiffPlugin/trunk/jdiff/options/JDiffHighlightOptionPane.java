/*
 * JDiffHighlightOptionPane.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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


package jdiff.options;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JColorChooser;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;


public class JDiffHighlightOptionPane extends AbstractOptionPane
{
    private JButton highlightChangedLineColor;
    private JButton highlightDeletedLineColor;
    private JButton highlightInsertedLineColor;

    private JButton highlightInvalidLineColor;


    public JDiffHighlightOptionPane() {
        super("jdiff-highlight");
    }


    public void _init() {
        this.highlightChangedLineColor  = this.createColorButton("jdiff.highlight-changed-color");
        this.highlightDeletedLineColor  = this.createColorButton("jdiff.highlight-deleted-color");
        this.highlightInsertedLineColor = this.createColorButton("jdiff.highlight-inserted-color");

        this.highlightInvalidLineColor  = this.createColorButton("jdiff.highlight-invalid-color");

        // Highlight colors
        addComponent(this.createLabel("options.jdiff.highlight"));
        addComponent(
            jEdit.getProperty("options.jdiff.highlight-changed-color"),
            this.highlightChangedLineColor
        );
        addComponent(
            jEdit.getProperty("options.jdiff.highlight-deleted-color"),
            this.highlightDeletedLineColor
        );
        addComponent(
            jEdit.getProperty("options.jdiff.highlight-inserted-color"),
            this.highlightInsertedLineColor
        );

        addComponent(
            jEdit.getProperty("options.jdiff.highlight-invalid-color"),
            this.highlightInvalidLineColor
        );
    }


    public void _save() {
        jEdit.setProperty("jdiff.highlight-changed-color",
            GUIUtilities.getColorHexString(this.highlightChangedLineColor.getBackground())
        );
        jEdit.setProperty("jdiff.highlight-deleted-color",
            GUIUtilities.getColorHexString(this.highlightDeletedLineColor.getBackground())
        );
        jEdit.setProperty("jdiff.highlight-inserted-color",
            GUIUtilities.getColorHexString(this.highlightInsertedLineColor.getBackground())
        );

        jEdit.setProperty("jdiff.highlight-invalid-color",
            GUIUtilities.getColorHexString(this.highlightInvalidLineColor.getBackground())
        );
    }


    private JButton createColorButton(String property) {
        JButton b = new JButton(" ");
        b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
        b.addActionListener(new ActionHandler(b));
        b.setRequestFocusEnabled(false);
        return b;
    }


    private JLabel createLabel(String property) {
        return new JLabel(jEdit.getProperty(property));
    }


    private class ActionHandler implements ActionListener {
        private JButton button;


        ActionHandler(JButton button) {
            this.button = button;
        }


        public void actionPerformed(ActionEvent evt) {
            JButton button = (JButton)evt.getSource();
            Color c = JColorChooser.showDialog(
                JDiffHighlightOptionPane.this,
                jEdit.getProperty("colorChooser.title"),
                button.getBackground()
            );
            if (c != null) {
                button.setBackground(c);
            }
        }
    }
}

