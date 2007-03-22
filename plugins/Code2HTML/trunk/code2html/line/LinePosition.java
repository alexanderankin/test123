/*
 * LinePosition.java
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


/**
 *  Represents the position of an imaginary 'cursor' in a line
 *
 * @author     Andre Kaplan
 * @version    0.5
 */
public class LinePosition {
    private int pos = 0;


    /**
     *  LinePosition Constructor
     */
    public LinePosition() { }


    /**
     *  Sets the pos of the object
     *
     * @param  pos  The new pos value
     */
    public void setPos(int pos) {
        this.pos = pos;
    }


    /**
     *  Gets the pos of the object
     *
     * @return    The pos value
     */
    public int getPos() {
        return this.pos;
    }


    /**
     *  increase the position by an ammount
     *
     * @param  inc  The ammount to increase the position by
     */
    public void incPos(int inc) {
        this.pos += inc;
    }
}

