/*
 * HtmlUtilities.java
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
package code2html.html;


/**
 *  Utilities for HTML handling
 *
 * @author     Andre Kaplan
 * @version    0.5
 */
public class HtmlUtilities {
    /**
     *  converts a string to valid HTML using entities
     *
     * @param  s  The string
     * @return    A new string in HTML
     */
    public static String toHTML(String s) {
        return HtmlUtilities.toHTML(s.toCharArray(), 0, s.length());
    }


    /**
     *  converts a string to valid HTML using entities
     *
     * @param  str     The string as an array of char
     * @param  strOff  The offset (initially 0)
     * @param  strLen  The length of the string
     * @return         A string with entities where there were non-printable
     *      chars
     * @todo           Whty do we have to pass a length arg here?
     */
    public static String toHTML(char[] str, int strOff, int strLen) {
        StringBuffer buf = new StringBuffer();
        char c;
        int len = 0;
        int off = strOff;
        for (int i = 0; i < strLen; strOff++, i++) {
            c = str[strOff];

            String entity = HtmlEntity.lookupEntity((short) c);
            if (entity != null) {
                buf.append(str, off, len).append("&").append(entity).append(";");
                off += len + 1;
                len = 0;
            } else if (((short) c) > 255) {
                buf.append(str, off, len).append("&#").append((short) c).append(";");
                off += len + 1;
                len = 0;
            } else {
                len++;
            }
        }

        buf.append(str, off, len);
        return buf.toString();
    }
}

