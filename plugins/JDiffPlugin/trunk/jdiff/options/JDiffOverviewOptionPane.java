/*
 * JDiffOverviewOptionPane.java
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


public class JDiffOverviewOptionPane extends AbstractOptionPane
{
    private JButton overviewChangedLineColor;
    private JButton overviewDeletedLineColor;
    private JButton overviewInsertedLineColor;
    private JButton overviewInvalidLineColor;

    private JButton leftCursorColor;
    private JButton rightCursorColor;


    public JDiffOverviewOptionPane() {
        super("jdiff-overview");
    }


    public void _init() {
        this.overviewChangedLineColor  = this.createColorButton("jdiff.overview-changed-color");
        this.overviewDeletedLineColor  = this.createColorButton("jdiff.overview-deleted-color");
        this.overviewInsertedLineColor = this.createColorButton("jdiff.overview-inserted-color");
        this.overviewInvalidLineColor  = this.createColorButton("jdiff.overview-invalid-color");

        this.leftCursorColor   = this.createColorButton("jdiff.left-cursor-color");
        this.rightCursorColor  = this.createColorButton("jdiff.right-cursor-color");

        // Overview colors
        addComponent(this.createLabel("options.jdiff.overview"));
        addComponent(
            jEdit.getProperty("options.jdiff.overview-changed-color"),
            this.overviewChangedLineColor
        );
        addComponent(
            jEdit.getProperty("options.jdiff.overview-deleted-color"),
            this.overviewDeletedLineColor
        );
        addComponent(
            jEdit.getProperty("options.jdiff.overview-inserted-color"),
            this.overviewInsertedLineColor
        );
        addComponent(
            jEdit.getProperty("options.jdiff.overview-invalid-color"),
            this.overviewInvalidLineColor
        );
        addComponent(
            jEdit.getProperty("options.jdiff.left-cursor-color"),
            this.leftCursorColor
        );
        addComponent(
            jEdit.getProperty("options.jdiff.right-cursor-color"),
            this.rightCursorColor
        );
    }


    public void _save() {
        jEdit.setProperty("jdiff.overview-changed-color",
            GUIUtilities.getColorHexString(this.overviewChangedLineColor.getBackground())
        );
        jEdit.setProperty("jdiff.overview-deleted-color",
            GUIUtilities.getColorHexString(this.overviewDeletedLineColor.getBackground())
        );
        jEdit.setProperty("jdiff.overview-inserted-color",
            GUIUtilities.getColorHexString(this.overviewInsertedLineColor.getBackground())
        );
        jEdit.setProperty("jdiff.overview-invalid-color",
            GUIUtilities.getColorHexString(this.overviewInvalidLineColor.getBackground())
        );
        jEdit.setProperty("jdiff.left-cursor-color",
            GUIUtilities.getColorHexString(this.leftCursorColor.getBackground())
        );
        jEdit.setProperty("jdiff.right-cursor-color",
            GUIUtilities.getColorHexString(this.rightCursorColor.getBackground())
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
                JDiffOverviewOptionPane.this,
                jEdit.getProperty("colorChooser.title"),
                button.getBackground()
            );
            if (c != null) {
                button.setBackground(c);
            }
        }
    }
}
