/*
 *  AutoSaveAtPointOption.java -   
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
 * Option class indicating if Jimporter should automatically run a search when
 * the user invokes "import class at point".
 *
 * As of 0.4, this option has been changed to be true by default -- it isn't
 * really clear why someone wouldn't want to do this, unless they had a really
 * slow system.
 *
 * @author Matthew Flower
 */ 
public class AutoSearchAtPointOption extends JImporterBooleanOption {
    private static String LABEL_PROPERTY = "options.jimporter.autosearchatpoint.label";
    private static String VALUE_PROPERTY = "jimporter.autosearchatpoint.enabled";
    private static boolean DEFAULT_VALUE = true;
    
    public AutoSearchAtPointOption() {
        super(LABEL_PROPERTY, VALUE_PROPERTY, DEFAULT_VALUE);
    }
}

