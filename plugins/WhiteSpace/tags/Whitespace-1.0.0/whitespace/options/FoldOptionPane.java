/*
 * FoldOptionPane.java
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


public class FoldOptionPane extends WhiteSpaceAbstractOptionPane
{
    private JCheckBox showFoldDefault;
    private JCheckBox showFoldTooltipDefault;
    private JButton   foldColor;


    public FoldOptionPane() {
        super("white-space.fold");
    }


    public void _init() {
        this.showFoldDefault        = this.createCheckBox(
            "white-space.show-fold-default", false
        );
        this.showFoldTooltipDefault = this.createCheckBox(
            "white-space.show-fold-tooltip-default", false
        );

        this.foldColor = this.createColorButton("white-space.fold-color");

        addComponent(this.showFoldDefault);
        addComponent(this.showFoldTooltipDefault);
        addComponent(jEdit.getProperty("options.white-space.fold-color"),
            this.foldColor
        );
    }


    public void _save() {
        jEdit.setBooleanProperty("white-space.show-fold-default",
            this.showFoldDefault.isSelected()
        );
        jEdit.setBooleanProperty("white-space.show-fold-tooltip-default",
            this.showFoldTooltipDefault.isSelected()
        );
        jEdit.setProperty("white-space.fold-color",
            SyntaxUtilities.getColorHexString(this.foldColor.getBackground())
        );
    }
}
