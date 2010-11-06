/*
 * WhiteSpaceOptionPane.java
 * Copyright (c) 2000-2001 Andre Kaplan
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


public class WhiteSpaceOptionPane extends WhiteSpaceAbstractOptionPane
{
    private JCheckBox showWhitespaceDefault;

    private JCheckBox displayControlChars;
    private JButton   whitespaceColor;


    public WhiteSpaceOptionPane() {
        super("white-space.other");
    }


    public void _init() {
        this.showWhitespaceDefault = this.createCheckBox(
            "white-space.show-whitespace-default", true
        );

        this.displayControlChars   = this.createCheckBox(
            "white-space.display-control-chars", false
        );

        this.whitespaceColor = this.createColorButton("white-space.whitespace-color");

        addComponent(this.showWhitespaceDefault);
        addComponent(this.displayControlChars);
        addComponent(
            jEdit.getProperty("options.white-space.whitespace-color"),
            this.whitespaceColor
        );
    }


    public void _save() {
        jEdit.setBooleanProperty("white-space.show-whitespace-default",
            this.showWhitespaceDefault.isSelected()
        );
        jEdit.setBooleanProperty("white-space.display-control-chars",
            this.displayControlChars.isSelected()
        );
        jEdit.setProperty("white-space.whitespace-color",
            SyntaxUtilities.getColorHexString(this.whitespaceColor.getBackground())
        );
    }
}
