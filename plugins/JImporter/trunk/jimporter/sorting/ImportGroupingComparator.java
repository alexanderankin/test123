/*
 *  ImportGroupingComparator.java -   
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
package jimporter.sorting;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.RESyntax;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import jimporter.ImportItem;
import jimporter.grouping.ImportGroupItem;
import jimporter.grouping.ImportGroupOption;
import jimporter.grouping.PackageGroupItem;
import java.text.Collator;

/**
 * Compares the ordering of two objects with the knowledge of how the user has
 * set up Import Grouping in the JImporter option pane.
 */
public class ImportGroupingComparator implements Comparator {
    ArrayList importGroupRegex = new ArrayList();
    Comparator alphabeticComparator;
    
    public ImportGroupingComparator() {
        loadGrouping();
        
        SortCaseInsensitiveOption scio = new SortCaseInsensitiveOption();
        if (scio.state()) {
            alphabeticComparator = String.CASE_INSENSITIVE_ORDER;
        } else {
            alphabeticComparator = Collator.getInstance();
        }
        
    }
    
    /**
     * Load the list of all import groupings from the jEdit properties.
     */
    public void loadGrouping() {
        Iterator importGroups = ImportGroupOption.load().iterator();
        
        while (importGroups.hasNext()) {
            ImportGroupItem igi = (ImportGroupItem)importGroups.next();
            
            if (igi instanceof PackageGroupItem) {
                String regexString = ((PackageGroupItem)igi).getPackagePattern();
                
                //"Fix" periods or asterisks so the regular expressions match them correctly
                regexString.replaceAll("\\.", "[.]");
                regexString.replaceAll("\\*", "[^.;]*");
                regexString = "[[:space:]]*import[[:space:]]+" + regexString + ";";
                
                try {
                    RE re = new RE(regexString, RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
                    
                    importGroupRegex.add(re);
                } catch (REException ree) {
                    System.out.println(ree);
                }
            }
        }
    }
    
    public int compare(Object o1, Object o2) {
        int toReturn;
        String o1Statement = ((ImportItem)o1).getImportStatement();
        String o2Statement = ((ImportItem)o2).getImportStatement();
        
        //First, determine what group the objects are in
        int o1Group = whichGroup(o1Statement);
        int o2Group = whichGroup(o2Statement);         
        
        if (o1Group < o2Group) {
            toReturn = -1;
        } else if (o1Group > o2Group) {
            toReturn = 1;
        } else {
            toReturn = alphabeticComparator.compare(o1Statement, o2Statement);
        }
        
        return toReturn;
    }
    
    /**
     * Determine which group an import statement belongs to.
     *
     * @param o an import statement that we are going to find a grouping for.
     * @return a <code>int</code> value indicating the import grouping that the
     * import statement belongs to.
     */
    public int whichGroup(Object o) {
        int toReturn = 0;
        Iterator it = importGroupRegex.iterator();
        
        int i = 1;
        
        while (it.hasNext()) {
            RE re = (RE)it.next();
            
            if (re.isMatch(o)) {
                toReturn = i;
            }
            
            i++;
        }
        
        return toReturn;
    }
    
    public boolean equals(Object o) {
        return (o instanceof ImportGroupingComparator);
    }            
}

