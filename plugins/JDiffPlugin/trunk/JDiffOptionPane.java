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


import javax.swing.JCheckBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


public class JDiffOptionPane extends AbstractOptionPane
{
    private JCheckBox ignoreCase;
    private JCheckBox trimWhitespace;
    private JCheckBox ignoreWhitespace;


    public JDiffOptionPane() {
        super("jdiff-general");
    }


    public void _init() {
        this.ignoreCase        = this.createCheckBox("jdiff.ignore-case", false);
        this.trimWhitespace    = this.createCheckBox("jdiff.trim-whitespace", false);
        this.ignoreWhitespace  = this.createCheckBox("jdiff.ignore-whitespace", false);

        addComponent(this.ignoreCase);
        addComponent(this.trimWhitespace);
        addComponent(this.ignoreWhitespace);
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
    }


    private JCheckBox createCheckBox(String property, boolean defaultValue) {
        JCheckBox cb = new JCheckBox(jEdit.getProperty("options." + property));
        cb.setSelected(jEdit.getBooleanProperty(property, defaultValue));
        return cb;
    }
}

