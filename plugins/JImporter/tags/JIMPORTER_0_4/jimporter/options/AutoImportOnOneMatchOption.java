/*
 *  AutoImportOnOneMatchCheckbox.java -   
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
 * Option call that indicates whether an import statement should automatically
 * be generated if there is only one matching import statement found in the
 * classpath.
 *
 * @author Matthew Flower
 */
public class AutoImportOnOneMatchOption extends JImporterBooleanOption {
    private static String LABEL_PROPERTY = "options.jimporter.autoimportonmatch.label";
    private static String VALUE_PROPERTY = "jimporter.autoimportonmatch.enabled";
    private static boolean DEFAULT_VALUE = false;
   
    /**
     * Standard constructor.
     */
    public AutoImportOnOneMatchOption() {
        super(LABEL_PROPERTY, VALUE_PROPERTY, DEFAULT_VALUE);
    }
    
}

