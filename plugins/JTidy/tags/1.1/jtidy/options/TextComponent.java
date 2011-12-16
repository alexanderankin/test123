/*
 * TextComponent.java
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

import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;


public class TextComponent extends OptionPaneComponent
    implements MouseListener
{
    private String defaultValue;
    private int columns;
    private JTextField textField;
    private Helper helper;


    public TextComponent(String prop, String defaultValue) {
        this(prop, defaultValue, -1);
    }


    public TextComponent(String prop, String defaultValue, int columns) {
        super(prop);
        this.defaultValue = defaultValue;
        this.columns = columns;
    }


    public void init() {
        if (this.columns > 0) {
            this.textField = new JTextField(
                jEdit.getProperty(this.getJEditProp(), this.defaultValue),
                this.columns
            );
        } else {
            this.textField = new JTextField(
                jEdit.getProperty(this.getJEditProp(), this.defaultValue)
            );
        }

        this.textField.addMouseListener(this);
    }


    public void save() {
        jEdit.setProperty(this.getJEditProp(), this.textField.getText());
    }


    public boolean isSingle() {
        return false;
    }


    public void saveTo(Properties props) {
        props.put(
            this.getTidyProp(),
            jEdit.getProperty(this.getJEditProp(), this.defaultValue)
        );
    }


    public Component getComponent() {
        return this.textField;
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

