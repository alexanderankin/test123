/*
 *  JImporterBooleanOption.java -   
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jimporter.options;

import org.gjt.sp.jedit.jEdit;
import javax.swing.JCheckBox;

/**
 * This abstract class implements the functionality that is required to save a
 * simple "yes/no" question in the jEdit properties.  It is designed to make it
 * easy to add a simple "yes/no" option.  Also included is the ability to look
 * up this option from other parts of the code.
 *
 * @author Matthew Flower
 */
public abstract class JImporterBooleanOption extends JImporterOption implements OptionSaveListener {
    /** 
     * A <code>String</code> value indicating what jEdit property will store this
     * option's value.
     */
    protected String valueProperty;
    /** The value of this property if one is not found in the jEdit property db. */
    protected boolean defaultValue;
    JCheckBox checkBox; 
    
    /**
     * Standard constructor.
     *
     * @param labelProperty a <code>String</code> value containing a jEdit property
     * name that points to a <code>String</code> that users will recognize to 
     * identify this option.
     * @param valueProperty a <code>String</code> value containing a jEdit property
     * where this boolean value will be stored.
     * @param defaultValue a <code>boolean</code> value indicating what this 
     * property should be set to if it hasn't been set previously.
     */
    public JImporterBooleanOption(String labelProperty, String valueProperty, boolean defaultValue) {
        super(labelProperty);
        this.valueProperty = valueProperty;
        this.defaultValue = defaultValue; 
    }
    
    /**
     * Indicates what the current state of this boolean value is, true or false.
     *
     * @return a <code>boolean</code> value indicating whether this option is 
     * currently true or false.
     */
    public boolean state() {
        boolean toReturn = defaultValue;
        String propertyValue = jEdit.getProperty(this.valueProperty);
        
        if (propertyValue != null) {
            toReturn = new Boolean(propertyValue).booleanValue();
        }
        
        return toReturn;
    }
    
    /**
     * Set the current value of this option - true or false.
     *
     * @param state a <code>boolean</code> value indicating the new value for
     * this property.
     */
    public void setState(boolean state) {
        jEdit.setProperty(valueProperty, new Boolean(state).toString());
    }
    
    /**
     * This is called by the option pane builders so an option has the opportunity
     * to add itself to the interface.
     *
     * @param jiop a <code>JImporterOptionPane</code> value that this boolean 
     * option is going to add itself to.
     */
    public void createVisualPresentation(JImporterOptionPane jiop) {
        checkBox = new JCheckBox(jEdit.getProperty(labelProperty));
        checkBox.setSelected(state());
        jiop.addComponent(checkBox);
        
        jiop.addSaveListener(this);
    }
    
    /**
     * This method, which implements OptionSaveListener, save any values that were
     * modified in the option panes.
     */
    public void saveChanges() {
        setState(checkBox.getModel().isSelected());
    }
}

