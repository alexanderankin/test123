/*
 * JTidyGeneralOptionPane.java
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

import java.util.Properties;


public class JTidyGeneralOptionPane
    extends JTidyAbstractOptionPane
{
    {
        components = new OptionPaneComponent[] {
              new ComboBoxComponent("char-encoding", 1)
            , new CheckBoxComponent("tidy-mark", true)
            , new CheckBoxComponent("markup", true)
            , new CheckBoxComponent("no-output", false) {
                public void saveTo(Properties props) {}
              }
            , new CheckBoxComponent("show-warnings", true)
            , new CheckBoxComponent("quiet", false)
            , new CheckBoxComponent("gnu-emacs", false)
            , new CheckBoxComponent("write-back", false)
            , new CheckBoxComponent("keep-time", true)
            // Not implemented yet
            // , new TextComponent("error-file", "", 15)
            // Not implemented yet
            // , new CheckBoxComponent("split", false)
        };
    }


    public JTidyGeneralOptionPane() {
        super("jtidy.general");
    }
}

