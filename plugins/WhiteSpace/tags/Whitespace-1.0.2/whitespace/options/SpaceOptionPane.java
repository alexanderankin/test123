/*
 * SpaceOptionPane.java
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


public class SpaceOptionPane extends WhiteSpaceAbstractOptionPane
{
    private JCheckBox  showSpaceDefault;
    private JCheckBox  showLeadingSpaceDefault;
    private JCheckBox  showInnerSpaceDefault;
    private JCheckBox  showTrailingSpaceDefault;

    private JButton    spaceColor;


    public SpaceOptionPane() {
        super("whitespace.spaceOption");
    }


    public void _init() {
        this.showSpaceDefault         = this.createCheckBox(
            "white-space.show-space-default", true
        );
        this.showLeadingSpaceDefault  = this.createCheckBox(
            "white-space.show-leading-space-default", true
        );
        this.showInnerSpaceDefault    = this.createCheckBox(
            "white-space.show-inner-space-default", true
        );
        this.showTrailingSpaceDefault = this.createCheckBox(
            "white-space.show-trailing-space-default", true
        );

        this.spaceColor = this.createColorButton("white-space.space-color");

        addComponent(this.showSpaceDefault);
        addComponent(jEdit.getProperty("options.white-space.space-color"),
            this.spaceColor
        );
        addComponent(this.showLeadingSpaceDefault);
        addComponent(this.showInnerSpaceDefault);
        addComponent(this.showTrailingSpaceDefault);
    }


    public void _save() {
        jEdit.setBooleanProperty("white-space.show-space-default",
            this.showSpaceDefault.isSelected()
        );
        jEdit.setBooleanProperty("white-space.show-leading-space-default",
            this.showLeadingSpaceDefault.isSelected()
        );
        jEdit.setBooleanProperty("white-space.show-inner-space-default",
            this.showInnerSpaceDefault.isSelected()
        );
        jEdit.setBooleanProperty("white-space.show-trailing-space-default",
            this.showTrailingSpaceDefault.isSelected()
        );

        jEdit.setProperty("white-space.space-color",
            SyntaxUtilities.getColorHexString(this.spaceColor.getBackground())
        );
    }
}
