/*
 * CheckBoxComponent.java
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Properties;

import javax.swing.JCheckBox;

import org.gjt.sp.jedit.jEdit;


public class CheckBoxComponent
    extends OptionPaneComponent
    implements MouseListener
{
    private boolean defaultValue;
    private JCheckBox checkBox;
    private Helper helper;


    public CheckBoxComponent(String prop, boolean defaultValue) {
        super(prop);
        this.defaultValue = defaultValue;
    }


    public void init() {
        this.checkBox = new JCheckBox(
            this.getLabel(),
            jEdit.getBooleanProperty(this.getJEditProp(), this.defaultValue)
        );

        this.checkBox.addMouseListener(this);
    }


    public void save() {
        jEdit.setBooleanProperty(this.getJEditProp(), this.checkBox.isSelected());
    }


    public boolean isSingle() {
        return true;
    }


    public void saveTo(Properties props) {
        props.put(
            this.getTidyProp(),
            jEdit.getBooleanProperty(this.getJEditProp(), this.checkBox.isSelected())
                ? "yes"
                : "no"
        );
    }


    public Component getComponent() {
        return this.checkBox;
    }


    void setHelper(Helper helper) {
        this.helper = helper;
    }


    // MouseListener implementation
    public void mouseClicked(MouseEvent evt) {}


    public void mouseEntered(MouseEvent evt) {
        if (this.helper != null) {
            this.helper.showHelp(this.getHelpText());
        }
    }


    public void mouseExited(MouseEvent evt) {
        if (this.helper != null) {
            this.helper.hideHelp();
        }
    }


    public void mousePressed(MouseEvent evt) {}


    public void mouseReleased(MouseEvent evt) {}
}

