/*
 * OptionPaneComponent.java
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

import java.awt.Component;
import java.util.Properties;

import org.gjt.sp.jedit.jEdit;


abstract public class OptionPaneComponent
{
    private String prop;


    abstract void init();


    abstract void save();


    abstract boolean isSingle();


    public abstract void saveTo(Properties props);


    private OptionPaneComponent() {}


    protected OptionPaneComponent(String prop) {
        this.prop = prop;
    }


    final String getTidyProp() {
        return this.prop;
    }


    final String getJEditProp() {
        return "jtidy." + this.prop;
    }


    final String getLabel() {
        return jEdit.getProperty("options.jtidy." + this.prop, "");
    }


    final String getHelpText() {
        return jEdit.getProperty(
            "options.jtidy." + this.prop + ".text", "");
    }


    final String getHelpText(int index) {
        return jEdit.getProperty(
            "options.jtidy." + this.prop + "." + index + ".text", ""
        );
    }


    void setHelper(Helper helper) {}


    abstract Component getComponent();
}

