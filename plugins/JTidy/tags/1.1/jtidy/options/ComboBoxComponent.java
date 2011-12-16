/*
 * ComboBoxComponent.java
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

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.Log;


public class ComboBoxComponent
    extends OptionPaneComponent
{
    protected int defaultIndex;
    private JComboBox comboBox;
    private Helper helper;


    public ComboBoxComponent(String prop, int defaultIndex) {
        super(prop);
        this.defaultIndex = defaultIndex;
    }


    public void init() {
        this.comboBox = new JComboBox();
        this.comboBox.setRenderer(
            new ListCellRendererAdapter(this.comboBox.getRenderer())
        );
        this.comboBox.setEditable(false);

        String item = null;
        for (
            int i = 0;
            (item = jEdit.getProperty(this.getJEditProp() + "." + i)) != null;
            i++
        ) {
            this.comboBox.addItem(item);
        }

        int selectedItem = this.defaultIndex;
        try {
            selectedItem = Integer.parseInt(jEdit.getProperty(this.getJEditProp()));
        } catch (NumberFormatException nfe) {}

        this.comboBox.setSelectedIndex(selectedItem);
    }


    public void save() {
        jEdit.setProperty(this.getJEditProp(), Integer.toString(
            this.comboBox.getSelectedIndex()));
    }


    public boolean isSingle() {
        return false;
    }


    public void saveTo(Properties props) {
        String tidyProp = this.getTidyProp();
        int idx = this.defaultIndex;

        try {
            idx = Integer.parseInt(jEdit.getProperty(this.getJEditProp()));
        } catch (NumberFormatException nfe) {}

        props.put(tidyProp, jEdit.getProperty(this.getJEditProp() + "." + idx));
    }


    public Component getComponent() {
        return this.comboBox;
    }


    void setHelper(Helper helper) {
        this.helper = helper;
    }


    private class ListCellRendererAdapter implements ListCellRenderer
    {
        private ListCellRenderer renderer;


        ListCellRendererAdapter(ListCellRenderer renderer) {
            this.renderer = renderer;
        }


        public Component getListCellRendererComponent(JList list,
            Object value, int index, boolean isSelected,
            boolean cellHasFocus
        ) {
            if (isSelected) {
                if (ComboBoxComponent.this.helper != null) {
                    String text = ComboBoxComponent.this.getHelpText(index);
                    if (text == null) {
                        text = ComboBoxComponent.this.getHelpText();
                    }
                    ComboBoxComponent.this.helper.showHelp(text);
                }
            }

            return this.renderer.getListCellRendererComponent(list,
                value, index, isSelected, cellHasFocus
            );
        }
    }
}

