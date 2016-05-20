
/*
 * Code2HTMLPlugin.java
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
package code2html;


import java.awt.Color;

import org.gjt.sp.jedit.EditPlugin;


/**
 *  Code2HTML plugin
 *
 * @author     Andr&eacute; Kaplan
 * @version    0.5
 */
public class Code2HTMLPlugin extends EditPlugin {

    /**
     *  Code2HTMLPlugin Constructor
     */
    public Code2HTMLPlugin() {
        super();
    }


    /**
     *  Start the plugin
     */
    public void start() {
    }


    /**
     *  Stop the plugin
     */
    public void stop() {
    }


    /*
     *public void createMenuItems(Vector menuItems) {
     *menuItems.addElement(GUIUtilities.loadMenu("code2html"));
     *}
     *public void createOptionPanes(OptionsDialog dialog) {
     *dialog.addOptionPane(new Code2HTMLOptionPane());
     *}
     */

    /**
     * Assumes a 6 or 8 digit hex value preceded by a # for a color value,
     * for example, #ffdcdcc or #f0f0f0
     * @return the corresponding Color, minus any alpha value
     */
    public static Color decode( String colorString ) {
        if ( colorString == null || colorString.length() == 0 || colorString.charAt( 0 ) != '#' ) {
            throw new IllegalArgumentException( "Invalid color string: " + colorString );
        }
        colorString = colorString.substring(1);     // remove leading #
        try
        {
            if ( colorString.length() == 6 ) {
                colorString = new StringBuilder("ff").append(colorString).toString();
            }
            //int alpha = Integer.parseInt( colorString.substring( 0, 2 ), 16 );
            int red = Integer.parseInt( colorString.substring( 2, 4 ), 16 );
            int green = Integer.parseInt( colorString.substring( 4, 6 ), 16 );
            int blue = Integer.parseInt( colorString.substring( 6, 8 ), 16 );
            Color color = new Color( red, green, blue );
            return color;
        }
        catch ( NumberFormatException nf ) {
            throw new IllegalArgumentException( "Invalid color string: " + colorString );
        }
    }
    
	/**
	 * Converts a color object to its hex value, minus any alpha value. The returned
	 * value is prefixed with `#', for example `#ff0088'.
	 */
    public static String encode(Color color) {
		String colString = Integer.toHexString(color.getRGB());
		return "#" + colString.substring(2);
    }
}
