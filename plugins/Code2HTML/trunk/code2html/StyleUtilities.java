/*
 * StyleUtilities.java
 * Copyright (c) 1998, 1999, 2000, 2001, 2002 Slava Pestov
 * Copyright (c) 2002 Andre Kaplan
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

import java.awt.Font;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.util.Log;


public class StyleUtilities
{
    private StyleUtilities() {}


    //{{{ loadStyles() method
    /**
     * Loads the syntax styles from the properties, giving them the specified
     * base font family and size.
     * @param propertyAccessor The property accessor
     * @param family The font family
     * @param size The font size
     * @param color If false, the styles will be monochrome
     * @since jEdit 4.0pre4
     */
    public static SyntaxStyle[] loadStyles(
            PropertyAccessor propertyAccessor, String family, int size, boolean color
    ) {
        String[] names = new String[Token.ID_COUNT];
        SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];

        try
        {
            styles[Token.NULL] = new SyntaxStyle(
                  GUIUtilities.parseColor(propertyAccessor.getProperty("view.fgColor"))
                , null
                , new Font(family, Font.PLAIN, size)
            );

            names[Token.COMMENT1] = "view.style.comment1";
            names[Token.COMMENT2] = "view.style.comment2";
            names[Token.LITERAL1] = "view.style.literal1";
            names[Token.LITERAL2] = "view.style.literal2";
            names[Token.LABEL]    = "view.style.label";
            names[Token.KEYWORD1] = "view.style.keyword1";
            names[Token.KEYWORD2] = "view.style.keyword2";
            names[Token.KEYWORD3] = "view.style.keyword3";
            names[Token.FUNCTION] = "view.style.function";
            names[Token.MARKUP]   = "view.style.markup";
            names[Token.OPERATOR] = "view.style.operator";
            names[Token.DIGIT]    = "view.style.digit";
            names[Token.INVALID]  = "view.style.invalid";

            for (int i = 1; i < Token.ID_COUNT; i++) {
                String style = propertyAccessor.getProperty(names[i]);
                styles[i] = GUIUtilities.parseStyle(
                    style, family, size, color
                );
            }
        }
        catch(Exception e)
        {
            Log.log(Log.ERROR, StyleUtilities.class, e);
        }

        return styles;
    } //}}}
}

