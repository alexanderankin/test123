/*
 * JTidyXmlOptionPane.java
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
import org.gjt.sp.jedit.jEdit;


public class JTidyXmlOptionPane
    extends JTidyAbstractOptionPane
{
    {
        components = new OptionPaneComponent[] {
            new ComboBoxComponent("doctype", 1) {
                public void saveTo(Properties props) {
                    String tidyProp = this.getTidyProp();
                    int idx = this.defaultIndex;

                    try {
                        idx = Integer.parseInt(jEdit.getProperty(this.getJEditProp()));
                    } catch (NumberFormatException nfe) {}

                    // Special case for fpi
                    String value =
                        jEdit.getProperty(this.getJEditProp() + "." + idx);
                    if (value.toLowerCase().indexOf("fpi") != -1) {
                        value =
                            jEdit.getProperty(this.getJEditProp() + "-fpi", "");
                    }
                    props.put(tidyProp, value);
                }
            }
            , new TextComponent("doctype-fpi", "", 15) {
                public void saveTo(Properties props) {
                }
            }
            , new CheckBoxComponent("input-xml", false)
            , new CheckBoxComponent("output-xml", false)
            , new CheckBoxComponent("output-xhtml", false)
            , new CheckBoxComponent("add-xml-pi", false)
            , new CheckBoxComponent("add-xml-decl", false)
            , new CheckBoxComponent("add-xml-space", false)
            , new CheckBoxComponent("assume-xml-procins", false)
        };
    }


    public JTidyXmlOptionPane() {
        super("jtidy.xml");
    }
}

