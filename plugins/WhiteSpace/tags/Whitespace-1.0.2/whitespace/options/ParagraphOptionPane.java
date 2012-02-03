/*
 * ParagraphOptionPane.java
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


public class ParagraphOptionPane extends WhiteSpaceAbstractOptionPane
{
    private JCheckBox showBlockDefault;
    private JCheckBox indentBlock;
    private JButton   blockColor;


    public ParagraphOptionPane() {
        super("whitespace.paragraphOption");
    }


    public void _init() {
        this.showBlockDefault = this.createCheckBox(
            "white-space.show-block-default", false
        );

        this.indentBlock      = this.createCheckBox(
            "white-space.indent-block", true
        );

        this.blockColor       = this.createColorButton("white-space.block-color");

        addComponent(this.showBlockDefault);
        addComponent(this.indentBlock);
        addComponent(jEdit.getProperty("options.white-space.block-color"),
            this.blockColor
        );
    }


    public void _save() {
        jEdit.setBooleanProperty("white-space.show-block-default",
            this.showBlockDefault.isSelected()
        );
        jEdit.setBooleanProperty("white-space.indent-block",
            this.indentBlock.isSelected()
        );
        jEdit.setProperty("white-space.block-color",
            SyntaxUtilities.getColorHexString(this.blockColor.getBackground())
        );
    }
}
