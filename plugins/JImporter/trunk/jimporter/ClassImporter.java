/*
 *  ClassImporter.java - Plugin for add java imports to the top of a java file.
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
package jimporter;

import gnu.regexp.*;
import gnu.regexp.REException;
import java.util.ArrayList;
import java.util.Iterator;
import jimporter.sorting.ImportSorter;
import jimporter.sorting.SortOnImportOption;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;

/**
 * Given a Java source file and a class to import, generate the appropriate
 * import statement and insert it into the code.
 *
 * @author    Matthew Flower
 * @since   August 25, 2002
 */
public class ClassImporter {
    private String importClass;
    private Buffer sourceBuffer;
    
    private boolean sortImportStatements = false;
    
    /** 
     * Set the buffer that we are going to add the import to.
     * 
     * @param source The source buffer we are going to add the import to.
     * @see #getSourceBuffer
     */
    public void setSourceBuffer(Buffer source) {
        sourceBuffer = source;
    }
    
    /**
     * Get the buffer we are going to add the import to.
     *
     * @return A <code>Buffer</code> that is the buffer we are going to add the
     * import into.
     * @see #setSourceBuffer
     */
    public Buffer getSourceBuffer() {
        return sourceBuffer;
    }
    
    /**
     * Sets the fully qualified name of the class we are going to import.
     *
     * @param fqClassName The new importClass value
     * @see #getImportClass
     */
    public void setImportClass(String fqClassName) {
        importClass = fqClassName;
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
    
    /**
     * Gets the name of the class we are going to import.
     *
     * @return A <code>String</code> value containing the fq class name of the
     * class we are going to import.
     * @see #setImportClass
     */
    public String getImportClass() {
        return importClass;
    }
    
    /**
     * Find the location at which we should insert the class identified by {@link #setImportClass(String)}.
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
     * This method will find the cursor location that corresponds to the first
     * row after the last import row.
     *
     * @return a <code>boolean</code> value indicating whether the class was
     * successfully imported.
     */
    public boolean addImportToBuffer() {
        //Check prerequisites
        if (importClass == null) {
            throw new MissingParameterException("importClass", "findInsertLocation()");
        }
        if (sourceBuffer == null) {
            throw new MissingParameterException("sourceBuffer", "findInsertLocation()");
        }
        
        ImportList importList = new ImportList();
        importList.setSourceBuffer(sourceBuffer);
        
        //Make sure that the class hasn't already been imported
        if (checkForDuplicateImport(importClass, importList)) {
            jEdit.getActiveView().getStatus().setMessageAndClear(importClass + " has already been imported.  Your request to import again has been ignored.");
            return false;
        }
        
        //Get the location we are going to do our insert at
        int insertLocation = findInsertLocation(importList);
        
        //Add the import to the source buffer
        sourceBuffer.insert(insertLocation, importClass);
        
        //See if we are supposed to sort on import.  If so, do it.
        if (new SortOnImportOption().state()) {
            new ImportSorter(jEdit.getActiveView()).sort();
        }
        
        //Everything seems to have gone well, indicate success to the caller
        return true;
    }
    
    /**
     * See if the import listed in "className" has already been imported.
     *
     * @param className The name of the class we are going to check the presence
     * of in the import list.
     * @param importList The list of imports that currently exists in the file.
     * @return a <code>boolean</code> value indicating whether the class was
     * already in the import list.
     */
    private boolean checkForDuplicateImport(String className, ImportList importList) {
        //Strip out the "import " part of the classname
        className = className.substring(7);
        //Strip out anything unusual from the end of the import
        className = className.substring(0, className.lastIndexOf(";"));
        
        System.out.println("Base className="+className);
        
        boolean duplicateFound = false;
        String importPrefix = ".*[[:space:]]*import[[:space:]]+";
        
        //First, we need to find the package.* version of the classname.
        int lastPeriod = className.lastIndexOf(".");
        boolean searchCombinedImport = (lastPeriod == -1);
        String combinedImport = className.substring(0, lastPeriod) + ".*;";
        
        //Set up the regex for the uncombined import
        String uncombinedImport = className + ";";
        
        //Do a little bit of regex cleanup before we create the regex's
        combinedImport = combinedImport.replaceAll("\\.", "[.]");
        combinedImport = combinedImport.replaceAll("\\*", "[*]");
        uncombinedImport = uncombinedImport.replaceAll("\\.", "[.]");
        uncombinedImport = uncombinedImport.replaceAll("\\*", "[*]");
        
        //Now that we have done our replacements, we won't screw up the premade regex
        combinedImport = importPrefix + combinedImport + ".*";
        uncombinedImport = importPrefix + uncombinedImport + ".*";
        
        System.out.println("combinedImport = " + combinedImport);
        System.out.println("uncombinedImport = " + uncombinedImport);
        
        //Set up the regular expressions that we will use to see if we have a match
        try {
            RE singleView = new RE(uncombinedImport, RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
            RE combinedView = new RE(combinedImport, RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
            
            //Iterate through the importlist looking for a match
            Iterator it = importList.iterator();
            while (it.hasNext()) {
                ImportItem ii = (ImportItem)it.next();
                
                if ((singleView.isMatch(ii.getImportStatement())) ||
                (combinedView.isMatch(ii.getImportStatement()))) {
                    duplicateFound = true;
                    break;
                }
            }
        } catch (REException ree) {
            System.out.println("Unexpected REException found while trying to check for " +
            "a duplicate import statement.  (Highly irregular!)" + ree);
        }
        
        return duplicateFound;
    }
}


