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
import jimporter.JImporterOptionPane;

public abstract class JImporterBooleanOption extends JImporterOption implements OptionSaveListener {
    protected String valueProperty;
    protected boolean defaultValue;
    JCheckBox checkBox; 
    
    public JImporterBooleanOption(String labelProperty, String valueProperty, boolean defaultValue) {
        super(labelProperty);
        this.valueProperty = valueProperty;
        this.defaultValue = defaultValue; 
    }
    
    public boolean state() {
        boolean toReturn = defaultValue;
        String propertyValue = jEdit.getProperty(this.valueProperty);
        
        if (propertyValue != null) {
            toReturn = new Boolean(propertyValue).booleanValue();
        }
        
        return toReturn;
    }
    
    public void setState(boolean state) {
        jEdit.setProperty(valueProperty, new Boolean(state).toString());
    }
    
    public void createVisualPresentation(JImporterOptionPane jiop) {
        checkBox = new JCheckBox(jEdit.getProperty(labelProperty));
        checkBox.setSelected(state());
        jiop.addComponent(checkBox);
        
        jiop.addSaveListener(this);
    }
    
    public void saveChanges() {
        setState(checkBox.getModel().isSelected());
    }
}

