/*
 *  GroupingSpacer.java  
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
package jimporter.grouping;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.RESyntax;
import java.util.Iterator;
import jimporter.grouping.ImportGroupItem;
import jimporter.grouping.ImportGroupOption;
import jimporter.grouping.WhiteSpaceGroupItem;
import jimporter.importer.JavaImportList;
import org.gjt.sp.jedit.Buffer;

/**
 * Add spaces between import groups in the import statement list.
 *
 * @author Matthew Flower
 */
public class GroupingSpacer {
    /**
     *
     */        
    private void createSpacerList() {
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
                } catch (REException ree) {
                    System.out.println(ree);
                }
            } else if (igi instanceof AllOtherImportsItem) {
                
            }
        }
    }
    
    public static void addGroupingSpaces(Buffer buffer, JavaImportList list) {
        Iterator importStatements = new JavaImportList().getImportList().iterator();
    }
    
}

class GroupingItem {
    private RE re;
    private boolean spaceBefore;
    
    public GroupingItem(RE re, boolean spaceBefore) {
        this.re = re;
        this.spaceBefore = spaceBefore;
    }
    
    public boolean isMatch(String statement) {
        return re.isMatch(statement); 
    }
    
    public boolean isSpaceBefore() {
        return spaceBefore;
    }
}
