/*
 *  JavaClassImporter.java - Plugin for add java imports to the top of a java file.
 *  Copyright (C) 2002 Matthew Flower (MattFlower@yahoo.com)
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

import gnu.regexp.*;
import gnu.regexp.REException;
import java.util.ArrayList;
import java.util.Iterator;
import jimporter.sorting.ImportSorter;
import jimporter.sorting.SortOnImportOption;
import jimporter.MissingParameterException;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;

/**
 * Given a Java source file and a class to import, generate the appropriate
 * import statement and insert it into the code.
 *
 * @author    Matthew Flower
 * @since   August 25, 2002
 */
public class JavaClassImporter extends ClassImporter {

    /**
     * Find the location at which we should insert the class identified by {@link #setImportClass(java.lang.String)}.
     *
     * @param importList Description of Parameter
     * @return Description of the Returned Value
     */
    public int findInsertLocation(ImportList importList) {
        int insertLocation = 0;
        
        if (importList.size() == 0) {
            //There isn't already an import statement in there.  Let's try to
            //find a good place to put it in anyhow.
            
            //Find all of the different places we might insert a import
            try {
                RE packageRE = new RE("[^][[:space:]]*package[[:space:]]+[[:alnum:].$_*]*;", RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
                REMatch packageLocation = packageRE.getMatch(sourceBuffer.getText(0, sourceBuffer.getLength()));
                RE classRE = new RE("[^][[:space:]]*(public|private)?[[:space:]](abstract|final)?class[[:space:]]+.*$", RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
                REMatch classLocation = classRE.getMatch(sourceBuffer.getText(0, sourceBuffer.getLength()));
                
                if (packageLocation != null) {
                    //Find the first line after the package
                    insertLocation = sourceBuffer.getLineStartOffset(sourceBuffer.getLineOfOffset(packageLocation.getStartIndex())+2);
                    
                    //Make sure we aren't overflowing
                    insertLocation = Math.min(insertLocation, sourceBuffer.getLength());
                    
                    //We found the package, add a blank line before where the import
                    //is going to be inserted.
                    sourceBuffer.insert(insertLocation, "\n");
                } else if (classLocation != null) {
                    //Find the first character of the class
                    insertLocation = sourceBuffer.getLineStartOffset(sourceBuffer.getLineOfOffset(classLocation.getStartIndex()));
                    
                    //Add a space before the class
                    sourceBuffer.insert(insertLocation, "\n");
                    
                    //Adjust the insert location for the new entries we just
                    //added, being careful to not go before the beginning of the
                    //buffer.
                    insertLocation = Math.max(insertLocation-2, 0);
                } else {
                    //Can't think of a good place to put it -- put it at the beginning
                    insertLocation = 0;
                }
            } catch (gnu.regexp.REException e) {
                throw new RuntimeException("Unexpected error while creating regular expression: " + e);
            }
        } else {
            int lineOfLastImport = sourceBuffer.getLineOfOffset(importList.getLast().getStartLocation());
            insertLocation = sourceBuffer.getLineStartOffset(lineOfLastImport + 1);
        }
        
        return insertLocation;
    }
    
    /**
     * This method combines "import" with the class name to generate the source
     * that we are going to insert into the buffer.
     *
     * @return The importSource value
     * @since empty
     */
    private String getImportSource() {
        //Check prerequisite
        if (importClass == null) {
            throw new MissingParameterException("importClass", "getImportSource()");
        }

        return "import " + importClass + ";\n";
    }

}


