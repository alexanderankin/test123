/*
 * LineWrapper.java
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


package code2html.line;


public class LineWrapper
{
    private int wrapSize = 72;


    public LineWrapper() {
        this(72);
    }


    public LineWrapper(int wrapSize) {
        if (wrapSize > 0) {
            this.wrapSize = wrapSize;
        }
    }


    public int getWrapSize() {
        return this.wrapSize;
    }


    /**
     * Returns an array of all indexes at which a string of length len at
     * position pos wraps. The referential for the indexes is the string.
     * Also, the wrap indexes are surrounded by 0 and len so that it is
     * very easy to compute the wrapped substrings:
     * <pre>
     * int[] wraps = wrapper.wrap(pos, len);
     * if (wraps == null) { return; }
     * for (int i = 0; i &lt; wraps.length - 1; i++) {
     *    System.out.println("Piece " + (i + 1) + ": " + s.substring(wraps[i], wraps[i + 1]));
     * }
     * </pre>
     * Returns null if the string does not wrap.
     * @requires pos >= 0
     * @requires len >= 0
     */
    public int[] wrap(int pos, int len) {
        // Log.log(Log.DEBUG, this, "#### pos-len " + pos + "," + len);

        int min = pos / this.wrapSize + 1;

        int max = (pos + len) / this.wrapSize;
        if ((pos + len) % this.wrapSize == 0) {
            max--;
        }
        // Log.log(Log.DEBUG, this, "#### min-max " + min + "," + max);

        if (min <= max) {
            int[] wrapArr = new int[max - min + 1 + 2];
            int wrapIdx = 0;
            int i   = min;
            int idx = i * this.wrapSize - pos;
            wrapArr[wrapIdx++] = 0;
            for (; i <= max; i++, idx += this.wrapSize) {
                wrapArr[wrapIdx++] = idx;
            }
            wrapArr[wrapIdx++] = len;
            return wrapArr;
        }

        return null;
    }
}

