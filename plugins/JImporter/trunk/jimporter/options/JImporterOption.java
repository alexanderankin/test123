/*
 *  JimporterOption.java -   
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

import org.gjt.sp.jedit.AbstractOptionPane;

/**
 * This abstract class represent an option that will be displayed in the 
 * JImporter "options" dialog.  It defines classes that allow the option panes
 * to class these options so they can create themselves.
 */
public abstract class JImporterOption {
    /** The jEdit property where the human-readable name of the option is stored. */
    protected String labelProperty;
    
    /**
     * Standard constructor.
     *
     * @param labelProperty a <code>String</code> containing the jEdit property
     * name of the location where the human readable name of this option will be
     * stored.
     */
    public JImporterOption(String labelProperty) {
        this.labelProperty = labelProperty;
    }
    
    /**
     * Create the current option on in the JImporter option panes.
     *
     * @param jiop a <code>JImporterOptionPane</code> value which the option
     * will add itself to.
     */
    public abstract void createVisualPresentation(JImporterOptionPane jiop);
}

