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
import org.gjt.sp.jedit.gui.ColorWellButton;
import org.gjt.sp.jedit.jEdit;


public class JDiffOverviewOptionPane extends AbstractOptionPane
{
    private ColorWellButton overviewChangedLineColor;
    private ColorWellButton overviewDeletedLineColor;
    private ColorWellButton overviewInsertedLineColor;
    private ColorWellButton overviewInvalidLineColor;

    private ColorWellButton leftCursorColor;
    private ColorWellButton rightCursorColor;


    public JDiffOverviewOptionPane() {
        super("jdiff-overview");
    }


    public void _init() {
        this.overviewChangedLineColor  = new ColorWellButton(jEdit.getColorProperty("jdiff.overview-changed-color"));
        this.overviewDeletedLineColor  = new ColorWellButton(jEdit.getColorProperty("jdiff.overview-deleted-color"));
        this.overviewInsertedLineColor = new ColorWellButton(jEdit.getColorProperty("jdiff.overview-inserted-color"));
        this.overviewInvalidLineColor  = new ColorWellButton(jEdit.getColorProperty("jdiff.overview-invalid-color"));

        this.leftCursorColor   = new ColorWellButton(jEdit.getColorProperty("jdiff.left-cursor-color"));
        this.rightCursorColor  = new ColorWellButton(jEdit.getColorProperty("jdiff.right-cursor-color"));

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
        jEdit.setColorProperty("jdiff.overview-changed-color",
            this.overviewChangedLineColor.getSelectedColor()
        );
        jEdit.setColorProperty("jdiff.overview-deleted-color",
            this.overviewDeletedLineColor.getSelectedColor()
        );
        jEdit.setColorProperty("jdiff.overview-inserted-color",
            this.overviewInsertedLineColor.getSelectedColor()
        );
        jEdit.setColorProperty("jdiff.overview-invalid-color",
            this.overviewInvalidLineColor.getSelectedColor()
        );
        jEdit.setColorProperty("jdiff.left-cursor-color",
            this.leftCursorColor.getSelectedColor()
        );
        jEdit.setColorProperty("jdiff.right-cursor-color",
            this.rightCursorColor.getSelectedColor()
        );
    }


    private JLabel createLabel(String property) {
        return new JLabel(jEdit.getProperty(property));
    }

}
