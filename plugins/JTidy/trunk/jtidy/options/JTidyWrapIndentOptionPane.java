/*
 * JTidyWrapIndentOptionPane.java
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


package jtidy.options;


public class JTidyWrapIndentOptionPane
    extends JTidyAbstractOptionPane
{
    {
        components = new OptionPaneComponent[] {
              new TextComponent("wrap", "66", 3)
            , new CheckBoxComponent("wrap-attributes", false)
            , new CheckBoxComponent("wrap-script-literals", false)
            , new CheckBoxComponent("wrap-asp", true)
            , new CheckBoxComponent("wrap-jste", true)
            , new CheckBoxComponent("wrap-php", true)
            , new TextComponent("tab-size", "4", 3)
            , new ComboBoxComponent("indent", 0)
            , new TextComponent("indent-spaces", "2", 3)
            , new CheckBoxComponent("indent-attributes", false)
            , new CheckBoxComponent("literal-attributes", false)
        };
    }


    public JTidyWrapIndentOptionPane() {
        super("jtidy.wrap-indent");
    }
}

