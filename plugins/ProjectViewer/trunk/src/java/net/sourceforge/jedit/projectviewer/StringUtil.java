/*
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

package net.sourceforge.jedit.projectviewer;

/**

A class that provides a replace(int start, int end, String str) method similar
to StringBuffer in JDK 1.2.2

*/
public final class StringUtil {

    private char value[];
    private int count;
    private boolean shared;

    public StringUtil() {
        this(16);
    }

    public StringUtil(int length) {
        value = new char[length];
        shared = false;
    }

    public StringUtil(String str) {
        this(str.length() + 16);
        append(str);
    }

    public int length() {
        return count;
    }

    public int capacity() {
        return value.length;
    }

    private final void copy() {
        char newValue[] = new char[value.length];
        System.arraycopy(value, 0, newValue, 0, count);
        value = newValue;
        shared = false;
    }

    private void expandCapacity(int minimumCapacity) {
        int newCapacity = (value.length + 1) * 2;
        if (minimumCapacity > newCapacity) {
            newCapacity = minimumCapacity;
        }
	
        char newValue[] = new char[newCapacity];
        System.arraycopy(value, 0, newValue, 0, count);
        value = newValue;
        shared = false;
    }




    public synchronized StringUtil append(String str) {
        if (str == null) {
            str = String.valueOf(str);
        }

        int len = str.length();
        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        str.getChars(0, len, value, count);
        count = newcount;
        return this;
    }



    public synchronized StringUtil replace(int start, int end, String str) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }

        if (end > count) {
            end = count;
        }

        if (start > end) {
            throw new StringIndexOutOfBoundsException();
        }

        int len = str.length();
        int newCount = count + len - (end - start);

        if (newCount > value.length) {
            expandCapacity(newCount);
        } else if (shared) {
            copy();
        }

        System.arraycopy(value, end, value, start + len, count - end);
        str.getChars(0, len, value, start);
        count = newCount;
        return this;
    }

    public String substring(int start) {
        return substring(start, count);
    }

    public synchronized String substring(int start, int end) {

        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > count)
            throw new StringIndexOutOfBoundsException(end);
        if (start > end)
    	    throw new StringIndexOutOfBoundsException(end - start);

        return new String(value, start, end - start);

    }

    public String toString() {
        return this.substring(0);
    }

    final void setShared() { 
        shared = true; 
    } 

    final char[] getValue() { 
        return value; 
    }

}
