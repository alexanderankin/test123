/*
 *  ImportItem.java - One import line from a java file.  
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
package jimporter;

import gnu.regexp.REMatch;

import java.lang.ClassCastException;

import java.lang.Comparable;

/**
 * An individual import item that has been harvested from a source file.
 *
 * @author    Matthew Flower
 * @created   August 25, 2002
 */
public class ImportItem implements Comparable {
    private int startLocation;
    private int endLocation;
    private String importStatement;

    /**
     * Constructor for the ImportItem object
     *
     * @param match a <code>REMatch</code> value that contains information as to
     * where the import item was found.  This is used to determine the full import
     * source.
     */
    public ImportItem(REMatch match) {
        startLocation = match.getStartIndex();
        endLocation = match.getEndIndex();
        importStatement = match.toString();
    }

    /**
     * Gets the offset of where the import statement begins.
     *
     * @return   The buffer offset where the import begins.
     */
    public int getStartLocation() {
        return startLocation;
    }
    
    /**
     * Get the offset of the last character in the import statement.
     * 
     * @return a <code>int</code> value which indicates where the last character
     * in the import statement was found.
     */
    public int getEndLocation() {
        return endLocation;
    }
    
    /** 
     * Get the text of the import statement that this import item represents.
     * 
     * @return a <code>String</code> value that contains the original text of the
     * import statement.
     */
    public String getImportStatement() {
        return importStatement;
    }    
    
    /** 
     * This method allows two <code>ImportItem</code> items to be compared so that
     * natural order can be established.
     *
     * @return A <CODE>int</CODE> value which will be negative if the current object is less
     * than the current parameter, 0 if they are equal, or positive if it is greater.
     * @param o An <code>object<code> that we are going to compare to the current 
     * instance to establish natural ordering.
     */    
    public int compareTo(Object o) {
        if (!(o instanceof ImportItem)) {
            throw new ClassCastException("Class being compared is not a ImportItem");
        } else {
            return this.getImportStatement().compareTo(((ImportItem)o).getImportStatement());
        }
    }
}    

