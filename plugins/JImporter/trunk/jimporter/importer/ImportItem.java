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
package jimporter.importer;

import gnu.regexp.REMatch;
import java.lang.ClassCastException;
import java.lang.Comparable;

/**
 * An individual import item that has been harvested from a source file.
 *
 * @author    Matthew Flower
 */
public class ImportItem implements Comparable {
    private int startLocation;
    private int endLocation;
    private String importStatement;
    private String packageName;
    private String className;

    /**
     * Constructor for the ImportItem object.
     *
     * @param match a <code>REMatch</code> value that contains information as to
     * where the import item was found.  This is used to determine the full import
     * source.
     */
    public ImportItem(REMatch match) {
        startLocation = match.getStartIndex();
        endLocation = match.getEndIndex();
        importStatement = match.toString();
        
        String fqClassName = match.toString(1);
        int lastPeriodPosition = fqClassName.lastIndexOf(".");
        
        if (lastPeriodPosition > -1) {
            packageName = fqClassName.substring(0, lastPeriodPosition);
        } else { 
            packageName = "";
        }
        
        if (fqClassName.length() > 0) {
            className = fqClassName.substring(lastPeriodPosition+1);
        } else {
            className = "";
        }
    }

    public ImportItem(int startLocation, int endLocation, String importStatement, String packageName, String className) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.importStatement = importStatement;
        this.packageName = packageName;
        this.className = className;
    }

    /**
     * Gets the offset of where the import statement begins.
     *
     * @return The buffer offset where the import begins.
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
     * Return the name of the class only, without any package names or an import
     * statement.
     *
     * @return a <code>String</code> value without any package name attached.
     */
    public String getShortClassName() {
        return className;
    }
    
    /**
     * Return the name of the package that this class resides in.
     *
     * @return a <code>String</code> value containing the package name where this
     * class resides.    
     */
    public String getPackageName() {
        return this.packageName;
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


