/*
 * JTidyFormatOptionPane.java
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


public class JTidyFormatOptionPane
    extends JTidyAbstractOptionPane
{
    {
        components = new OptionPaneComponent[] {
              new CheckBoxComponent("hide-endtags", false)
            , new CheckBoxComponent("numeric-entities", false)
            , new CheckBoxComponent("quote-marks", false)
            , new CheckBoxComponent("quote-nbsp", true)
            , new CheckBoxComponent("quote-ampersand", true)
            , new CheckBoxComponent("break-before-br", false)
            , new CheckBoxComponent("uppercase-tags", false)
            , new CheckBoxComponent("uppercase-attributes", false)
            , new TextComponent("alt-text", "", 15)
        };
    }


    public JTidyFormatOptionPane() {
        super("jtidy-format");
    }
}

