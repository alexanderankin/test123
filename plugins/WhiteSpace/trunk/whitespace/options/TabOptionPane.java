/*
 * TabOptionPane.java
 * Copyright (c) 2001 Andre Kaplan
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


package whitespace.options;


import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.SyntaxUtilities;


public class TabOptionPane extends WhiteSpaceAbstractOptionPane
{
    private JCheckBox  showTabDefault;
    private JCheckBox  showLeadingTabDefault;
    private JCheckBox  showInnerTabDefault;
    private JCheckBox  showTrailingTabDefault;

    private JButton    tabColor;


    public TabOptionPane() {
        super("whitespace.tabOption");
    }


    public void _init() {
        this.showTabDefault         = this.createCheckBox(
            "white-space.show-tab-default", true
        );
        this.showLeadingTabDefault  = this.createCheckBox(
            "white-space.show-leading-tab-default", true
        );
        this.showInnerTabDefault    = this.createCheckBox(
            "white-space.show-inner-tab-default", true
        );
        this.showTrailingTabDefault = this.createCheckBox(
            "white-space.show-trailing-tab-default", true
        );

        this.tabColor = this.createColorButton("white-space.tab-color");

        addComponent(this.showTabDefault);
        addComponent(jEdit.getProperty("options.white-space.tab-color"),
            this.tabColor
        );
        addComponent(this.showLeadingTabDefault);
        addComponent(this.showInnerTabDefault);
        addComponent(this.showTrailingTabDefault);
    }


    public void _save() {
        jEdit.setBooleanProperty("white-space.show-tab-default",
            this.showTabDefault.isSelected()
        );
        jEdit.setBooleanProperty("white-space.show-leading-tab-default",
            this.showLeadingTabDefault.isSelected()
        );
        jEdit.setBooleanProperty("white-space.show-inner-tab-default",
            this.showInnerTabDefault.isSelected()
        );
        jEdit.setBooleanProperty("white-space.show-trailing-tab-default",
            this.showTrailingTabDefault.isSelected()
        );

        jEdit.setProperty("white-space.tab-color",
            SyntaxUtilities.getColorHexString(this.tabColor.getBackground())
        );
    }
}
