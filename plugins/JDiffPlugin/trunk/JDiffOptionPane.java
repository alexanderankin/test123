/*
 * JDiffOptionPane.java
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;


public class JDiffOptionPane extends AbstractOptionPane
{
    private JCheckBox ignoreCase;
    private JCheckBox trimWhitespace;
    private JCheckBox ignoreWhitespace;
    private JButton   changedLineColor;
    private JButton   deletedLineColor;
    private JButton   insertedLineColor;
    private JButton   invalidLineColor;
    private JCheckBox brighterHighlight;
    private JCheckBox darkerOverview;
    private JButton   leftCursorColor;
    private JButton   rightCursorColor;


    public JDiffOptionPane() {
        super("jdiff");
    }


    public void _init() {
        this.ignoreCase        = this.createCheckBox("jdiff.ignore-case", false);
        this.trimWhitespace    = this.createCheckBox("jdiff.trim-whitespace", false);
        this.ignoreWhitespace  = this.createCheckBox("jdiff.ignore-whitespace", false);

        this.changedLineColor  = this.createColorButton("jdiff.changed-color");
        this.deletedLineColor  = this.createColorButton("jdiff.deleted-color");
        this.insertedLineColor = this.createColorButton("jdiff.inserted-color");
        this.invalidLineColor  = this.createColorButton("jdiff.invalid-color");
        this.brighterHighlight = this.createCheckBox("jdiff.brighter-highlight", true);
        this.darkerOverview    = this.createCheckBox("jdiff.darker-overview", false);
        this.leftCursorColor   = this.createColorButton("jdiff.left-cursor-color");
        this.rightCursorColor  = this.createColorButton("jdiff.right-cursor-color");

        addComponent(this.ignoreCase);
        addComponent(this.trimWhitespace);
        addComponent(this.ignoreWhitespace);

        addComponent(
            jEdit.getProperty("options.jdiff.changed-color"),
            this.changedLineColor
        );

        addComponent(
            jEdit.getProperty("options.jdiff.deleted-color"),
            this.deletedLineColor
        );

        addComponent(
            jEdit.getProperty("options.jdiff.inserted-color"),
            this.insertedLineColor
        );

        addComponent(
            jEdit.getProperty("options.jdiff.invalid-color"),
            this.invalidLineColor
        );

        addComponent(this.brighterHighlight);
        addComponent(this.darkerOverview);

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
        jEdit.setBooleanProperty("jdiff.ignore-case",
            this.ignoreCase.isSelected()
        );
        jEdit.setBooleanProperty("jdiff.trim-whitespace",
            this.trimWhitespace.isSelected()
        );
        jEdit.setBooleanProperty("jdiff.ignore-whitespace",
            this.ignoreWhitespace.isSelected()
        );
        jEdit.setProperty("jdiff.changed-color",
            GUIUtilities.getColorHexString(this.changedLineColor.getBackground())
        );
        jEdit.setProperty("jdiff.deleted-color",
            GUIUtilities.getColorHexString(this.deletedLineColor.getBackground())
        );
        jEdit.setProperty("jdiff.inserted-color",
            GUIUtilities.getColorHexString(this.insertedLineColor.getBackground())
        );
        jEdit.setProperty("jdiff.invalid-color",
            GUIUtilities.getColorHexString(this.invalidLineColor.getBackground())
        );
        jEdit.setBooleanProperty("jdiff.brighter-highlight",
            this.brighterHighlight.isSelected()
        );
        jEdit.setBooleanProperty("jdiff.darker-overview",
            this.darkerOverview.isSelected()
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


    private JCheckBox createCheckBox(String property, boolean defaultValue) {
        JCheckBox cb = new JCheckBox(jEdit.getProperty("options." + property));
        cb.setSelected(jEdit.getBooleanProperty(property, defaultValue));
        return cb;
    }


    private class ActionHandler implements ActionListener {
        private JButton button;


        ActionHandler(JButton button) {
            this.button = button;
        }


        public void actionPerformed(ActionEvent evt) {
            JButton button = (JButton)evt.getSource();
            Color c = JColorChooser.showDialog(
                JDiffOptionPane.this,
                jEdit.getProperty("colorChooser.title"),
                button.getBackground()
            );
            if (c != null) {
                button.setBackground(c);
            }
        }
    }
}
