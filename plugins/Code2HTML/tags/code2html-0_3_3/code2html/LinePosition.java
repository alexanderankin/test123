/*
 * LinePosition.java
 * Copyright (c) 2000 Andre Kaplan
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


import java.util.Vector;

import org.gjt.sp.util.Log;


public class LinePosition {
    private int pos = 0;


    public LinePosition() {}


    public int getPos() {
        return this.pos;
    }


    public void setPos(int pos) {
        this.pos = pos;
    }


    public void incPos(int inc) {
        this.pos += inc;
    }
}
