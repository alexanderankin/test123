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
import org.gjt.sp.jedit.gui.ColorWellButton;
import org.gjt.sp.jedit.jEdit;


public class JDiffHighlightOptionPane extends AbstractOptionPane
{
    private ColorWellButton highlightChangedLineColor;
    private ColorWellButton highlightDeletedLineColor;
    private ColorWellButton highlightInsertedLineColor;

    private ColorWellButton highlightInvalidLineColor;


    public JDiffHighlightOptionPane() {
        super("jdiff-highlight");
    }


    public void _init() {
        this.highlightChangedLineColor  = new ColorWellButton(jEdit.getColorProperty("jdiff.highlight-changed-color"));
        this.highlightDeletedLineColor  = new ColorWellButton(jEdit.getColorProperty("jdiff.highlight-deleted-color"));
        this.highlightInsertedLineColor = new ColorWellButton(jEdit.getColorProperty("jdiff.highlight-inserted-color"));
        
	this.highlightInvalidLineColor  = new ColorWellButton(jEdit.getColorProperty("jdiff.highlight-invalid-color"));

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
        jEdit.setColorProperty("jdiff.highlight-changed-color",
            this.highlightChangedLineColor.getSelectedColor()
        );
        jEdit.setColorProperty("jdiff.highlight-deleted-color",
            this.highlightDeletedLineColor.getSelectedColor()
        );
        jEdit.setColorProperty("jdiff.highlight-inserted-color",
            this.highlightInsertedLineColor.getSelectedColor()
        );

        jEdit.setColorProperty("jdiff.highlight-invalid-color",
            this.highlightInvalidLineColor.getSelectedColor()
        );
    }

    
    private JLabel createLabel(String property) {
        return new JLabel(jEdit.getProperty(property));
    }

}

