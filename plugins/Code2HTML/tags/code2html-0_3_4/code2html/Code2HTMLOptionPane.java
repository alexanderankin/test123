/*
 * Code2HTMLOptionPane.java
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


package code2html;


import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;


public class Code2HTMLOptionPane
    extends AbstractOptionPane
{
    private JCheckBox ckUseCSS;
    private JCheckBox ckShowGutter;
    private JTextField tfWrap;


    public Code2HTMLOptionPane() {
        super(jEdit.getProperty("code2html.label", "Code2HTML"));
    }


    public void _init() {
        this.ckUseCSS = new JCheckBox(
            jEdit.getProperty("options.code2html.use-css"),
            jEdit.getBooleanProperty("code2html.use-css", false)
        );
        addComponent(this.ckUseCSS);

        this.ckShowGutter = new JCheckBox(
            jEdit.getProperty("options.code2html.show-gutter"),
            jEdit.getBooleanProperty("code2html.show-gutter", false)
        );
        addComponent(this.ckShowGutter);

        this.tfWrap = new JTextField(4);
        int wrap = Code2HTMLUtilities.getIntegerProperty("code2html.wrap", 0);
        if (wrap < 0) { wrap = 0; }
        this.tfWrap.setText("" + wrap);
        addComponent(jEdit.getProperty("options.code2html.wrap"), this.tfWrap);
    }


    public void _save() {
        jEdit.setBooleanProperty("code2html.use-css",
            this.ckUseCSS.isSelected());

        jEdit.setBooleanProperty("code2html.show-gutter",
            this.ckShowGutter.isSelected());

        int wrap = Code2HTMLUtilities.getInteger(this.tfWrap.getText(), 0);
        if (wrap < 0) { wrap = 0; }
        jEdit.setProperty("code2html.wrap", "" + wrap);
    }
}
