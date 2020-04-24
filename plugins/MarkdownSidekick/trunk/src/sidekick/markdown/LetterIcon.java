
/*
 * Copyright (c) 2017, Dale Anson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package sidekick.markdown;


import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.Icon;


/**
 * A class to make icons out of letters. The original intent was for icons with a
 * single letter or number, but longer strings can be used. All characters will be
 * converted to upper case.
 */
public class LetterIcon implements Icon {

    private String s;
    private Color bg;
    private Color fg;
    private int w;
    private int h = 16;

    /**
     * Create an icon out of a string. By default, icons are 16 pixels tall. Strings
     * containing a single letter are shown in a circle, longer string are a rounded
     * rectangle. Background color is cyan, foreground color is black.
     * @param component A valid component with a valid graphics.
     * @param letters The string to show in the icon. The string will be converted to upper case.
     */
    public LetterIcon( Component component, String letters ) {
        this(component, letters, Color.CYAN, Color.BLACK, 16);
    }

    /**
     * Create an icon out of a string. By default, icons are 16 pixels tall. Strings
     * containing a single letter are shown in a circle, longer string are a rounded
     * rectangle.
     * @param component A valid component with a valid graphics.
     * @param letters The string to show in the icon. The string will be converted to upper case.
     * @param background The background color to use for the icon. If null, default color is cyan.
     * @param foreground The color for the letters. If null, default color is black.
     */
    public LetterIcon( Component component, String letters, Color background, Color foreground ) {
        this(component, letters, background, foreground, 16);
    }
    
    /**
     * Create an icon out of a string. By default, icons are 16 pixels tall. Strings
     * containing a single letter are shown in a circle, longer string are a rounded
     * rectangle.
     * @param component A valid component with a valid graphics.
     * @param letters The string to show in the icon. The string will be converted to upper case.
     * @param background The background color to use for the icon. If null, default color is cyan.
     * @param foreground The color for the letters. If null, default color is black.
     * @param height The height of the icon. If there is a single letter, this will be the diameter
     * of the icon, otherwise, this is the diameter of the ends of the rounded rectangle.
     */
    public LetterIcon( Component component, String letters, Color background, Color foreground, int height ) {
        if ( component == null ) {
            throw new IllegalArgumentException( "component may not be null" );
        }
        if ( letters == null || letters.isEmpty() ) {
            throw new IllegalArgumentException( "letters may not be empty" );
        }
        s = letters.toUpperCase();
        bg = background == null ? Color.CYAN : background;
        fg = foreground == null ? Color.BLACK : foreground;
        h = height;
        paintIcon( component, component.getGraphics(), 0, 0 );
    }

    public void paintIcon( Component c, Graphics graphics, int x, int y ) {
        FontMetrics fm = graphics.getFontMetrics();
        int lineHeight = fm.getAscent();
        int lineWidth = fm.stringWidth( s );
        Graphics g;

        switch ( s.length() ) {
            case 1:     // draw a filled circle
                w = h;
                g = graphics.create( 0, 0, w, h );
                g.setColor( bg );
                g.fillArc( 0, 0, w, h, 0, 360 );
                break;
            default:    // draw a filled round rectangle
                w = h + lineWidth;
                g = graphics.create( 0, 0, h + lineWidth, h );
                g.setColor( bg );
                g.fillRoundRect( 0, 0, h + lineWidth, h, h, h );
        }

        int offset_x = ( w - lineWidth ) / 2;
        int offset_y = ( ( h - lineHeight ) / 2 ) + lineHeight;
        g.setColor( fg );
        g.drawString( s, offset_x, offset_y );

        g.dispose();
    }

    public int getIconWidth() {
        return w;
    }

    public int getIconHeight() {
        return h;
    }
}
