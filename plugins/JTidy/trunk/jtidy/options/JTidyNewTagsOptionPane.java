/*
 * JTidyNewTagsOptionPane.java
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


public class JTidyNewTagsOptionPane
    extends JTidyAbstractOptionPane
{
    {
        components = new OptionPaneComponent[] {
              new TextComponent("new-empty-tags", "", 15)
            , new TextComponent("new-inline-tags", "", 15)
            , new TextComponent("new-blocklevel-tags", "", 15)
            , new TextComponent("new-pre-tags", "", 15)
        };
    }


    public JTidyNewTagsOptionPane() {
        super("jtidy.new-tags");
    }
}

