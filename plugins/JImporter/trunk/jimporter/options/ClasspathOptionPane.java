/*
 *  ClasspathOptionPane.java -   
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

/**
 * Create the option pane (actually, the tab) that will contain information about
 * the classpath the user is going to traverse to find a class to import.
 *
 * @author Matthew Flower
 */
public class ClasspathOptionPane extends JImporterOptionPane {
    /**
     * Standard constructor.
     */
    public ClasspathOptionPane() {
        super("jimporter.classpath");
    }
    
    /**
     * An implemented version of the _init abstract method of AbstractOptionPane
     * which will kick off the visual creation of the classpath option pane.
     */
    public void _init() {
        new ClasspathOption().createVisualPresentation(this);
    }
}

