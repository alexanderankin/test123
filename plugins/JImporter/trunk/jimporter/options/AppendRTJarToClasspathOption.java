/*
 *  AppendRTJarToClasspathOption.java -   
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

import jimporter.options.JImporterBooleanOption;

/**
 * This option class identifies whether or not the rt.jar package of java should
 * automatically be added to the search path that is searched when looking for
 * imports.
 *
 * @author Matthew Flower
 */
public class AppendRTJarToClasspathOption extends JImporterBooleanOption {
    private static String LABEL_PROPERTY = "options.jimporter.classpath.appendrtjar.label";
    private static String VALUE_PROPERTY = "jimporter.classpath.appendrtjar";
    private static boolean DEFAULT_VALUE = true;
    
    /**
     * Standard constructor.
     */
    public AppendRTJarToClasspathOption() {
        super(LABEL_PROPERTY, VALUE_PROPERTY, DEFAULT_VALUE);
    }
}

