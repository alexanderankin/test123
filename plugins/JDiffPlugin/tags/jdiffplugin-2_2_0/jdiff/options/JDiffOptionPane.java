/*
 * JDiffOptionPane.java
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

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import jdiff.component.MergeToolBar;


public class JDiffOptionPane extends AbstractOptionPane
{
    private JCheckBox ignoreCase;
    private JCheckBox trimWhitespace;
    private JCheckBox ignoreAmountOfWhitespace;
    private JCheckBox ignoreAllWhitespace;
    private JCheckBox autoShowDockable;
    private JCheckBox beepOnError;
    private JRadioButton horizontal;
    private JRadioButton vertical;
    private JRadioButton compact;
    private JCheckBox showLineDiff;

    public JDiffOptionPane() {
        super("jdiff-general");
    }


    public void _init() {
        ignoreCase        = createCheckBox("jdiff.ignore-case", false);
        trimWhitespace    = createCheckBox("jdiff.trim-whitespace", false);
        ignoreAmountOfWhitespace  = createCheckBox("jdiff.ignore-amount-whitespace", false);
        ignoreAllWhitespace  = createCheckBox("jdiff.ignore-all-whitespace", false);
        autoShowDockable = createCheckBox("jdiff.auto-show-dockable", false);
        showLineDiff = createCheckBox("jdiff.show-line-diff", true );
        beepOnError = createCheckBox("jdiff.beep-on-error", true);

        int orientation = jEdit.getIntegerProperty("jdiff.toolbar-orientation", MergeToolBar.HORIZONTAL);
        horizontal = new JRadioButton(jEdit.getProperty("options.jdiff.toolbar-horizontal"));
        vertical = new JRadioButton(jEdit.getProperty("options.jdiff.toolbar-vertical"));
        compact = new JRadioButton(jEdit.getProperty("options.jdiff.toolbar-compact"));

        ButtonGroup button_group = new ButtonGroup();
        button_group.add(horizontal);
        button_group.add(vertical);
        button_group.add(compact);
        switch(orientation) {
        case MergeToolBar.VERTICAL:
            horizontal.setSelected(false);
            vertical.setSelected(true);
            compact.setSelected(false);
            break;
        case MergeToolBar.COMPACT:
            horizontal.setSelected(false);
            vertical.setSelected(false);
            compact.setSelected(true);
            break;
        default:
            horizontal.setSelected(true);
            vertical.setSelected(false);
            compact.setSelected(false);
            break;
        }
        JLabel orientation_label = new JLabel(jEdit.getProperty("options.toolbar-orientation.label", "Merge Toolbar Orientation:"));

        addComponent(ignoreCase);
        addComponent(trimWhitespace);
        addComponent(ignoreAmountOfWhitespace);
        addComponent(ignoreAllWhitespace);
        addComponent(autoShowDockable);
        addComponent(showLineDiff);
        addComponent(beepOnError);
        addComponent(orientation_label);
        addComponent(horizontal);
        addComponent(vertical);
        addComponent(compact);
    }


    public void _save() {
        if ( vertical.isSelected() ) {
            jEdit.setIntegerProperty("jdiff.toolbar-orientation", MergeToolBar.VERTICAL);
        }
        else if ( compact.isSelected() ) {
            jEdit.setIntegerProperty("jdiff.toolbar-orientation", MergeToolBar.COMPACT);
        }
        else{
            jEdit.setIntegerProperty("jdiff.toolbar-orientation", MergeToolBar.HORIZONTAL);

        }

        jEdit.setBooleanProperty("jdiff.ignore-case",
            ignoreCase.isSelected()
        );
        jEdit.setBooleanProperty("jdiff.trim-whitespace",
            trimWhitespace.isSelected()
        );
        jEdit.setBooleanProperty("jdiff.ignore-amount-whitespace",
            ignoreAmountOfWhitespace.isSelected()
        );
        jEdit.setBooleanProperty("jdiff.ignore-all-whitespace",
            ignoreAllWhitespace.isSelected()
        );
        jEdit.setBooleanProperty("jdiff.auto-show-dockable",
            autoShowDockable.isSelected()
        );
        jEdit.setBooleanProperty("jdiff.show-line-diff",
            showLineDiff.isSelected()
        );
        jEdit.setBooleanProperty("jdiff.beep-on-error",
            beepOnError.isSelected()
        );



        // virtual overview has been removed, it hasn't worked since jEdit 4.2,
        // so make sure the property is false
        jEdit.setBooleanProperty("jdiff.global-virtual-overview", false);
    }


    private JCheckBox createCheckBox(String property, boolean defaultValue) {
        JCheckBox cb = new JCheckBox(jEdit.getProperty("options." + property));
        cb.setSelected(jEdit.getBooleanProperty(property, defaultValue));
        return cb;
    }
}

