/*
 * LineTabExpander.java
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

import org.gjt.sp.util.Log;


public class LineTabExpander
{
    private int tabSize;
    private char[] spacer;


    public LineTabExpander() {
        this(4);
    }


    public LineTabExpander(int tabSize) {
        if (tabSize > 0) {
            this.tabSize = tabSize;
        }

        this.spacer = new char[this.tabSize];
        for (int i = 0; i < this.tabSize; i++) {
            this.spacer[i] = ' ';
        }
    }


    public String expand(int pos, char[] str, int strOff, int strLen) {
        StringBuffer buf = new StringBuffer();

        int off = strOff;
        int len = 0;
        char c;

        for (int i = 0; i < strLen; i++) {
            c = str[strOff + i];

            if (c != '\t') {
                len++;
                pos++;
            } else {
                int rem = this.tabSize - (pos % this.tabSize);
                buf.append(str, off, len).append(this.spacer, 0, rem);
                off += len + 1;
                len = 0;
                pos += rem;
            }
        }

        buf.append(str, off, len);
        return buf.toString();
    }


    public String expand(int pos, String s) {
        return this.expand(pos, s.toCharArray(), 0, s.length());
    }
}

