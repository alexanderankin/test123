/*
 *  UnusedImportFinder.java  
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
package jimporter.utilization;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;
import gnu.regexp.RESyntax;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import jimporter.ImportItem;
import jimporter.ImportList;
import org.gjt.sp.jedit.Buffer;


public class UnusedImportFinder {
    public StreamTokenizer createClassTokenizer(Buffer buffer) {
        StreamTokenizer toReturn = new StreamTokenizer(new StringReader(buffer.getText(0, buffer.getLength())));
        toReturn.eolIsSignificant(true);
        toReturn.slashSlashComments(true);
        toReturn.slashStarComments(true);
        toReturn.wordChars('_', '_');
        toReturn.ordinaryChar('.');
        toReturn.quoteChar('\'');
        toReturn.quoteChar('\"');
        
        return toReturn;
    }
    
    /**
     * Find the package name in a given buffer.
     *
     * @param buffer a <code>Buffer</code> object that we are going to find a 
     * package declaration in.
     * @return a <code>String</code> value containing the package name of this
     * package.
     */
    private String findPackage(Buffer buffer) {
        String toReturn = "";
        
        try {
            RE packageRE = new RE("[^][[:space:]]*package[[:space:]]+([[:alnum:].$_*]*);", RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
            REMatch packageLocation = packageRE.getMatch(buffer.getText(0, buffer.getLength()));
            toReturn = packageLocation.toString(1);
        } catch (REException ree) {
            JOptionPane.showMessageDialog(null, "Unexpected error found in JImporter.  Please send a copy of your Activity Log to MattFlower@yahoo.com");            
        }
        
        return toReturn;
    }
    
    /**
     * Find all of the imports that are in a specified package.
     *
     * @param package a <code>String</code> value that is the package we are going
     * to search in.
     * @param imports a <code>ArrayList</code> value containing all of the current
     * imports.
     * @param remove a <code>boolean</code> value indicating that any import 
     * statements found should be removed from the "imports" arraylist variable.
     * @return a <code>List</code> of imports that are in the specified package.
     */
    private List findImportsInPackage(String packageName, ArrayList imports, boolean remove) {
        if (packageName.length() == 0) {
            return Collections.EMPTY_LIST;
        }
        
        ArrayList inPackage = new ArrayList();
        
        //Iterate through all of the import statements looking for any that are
        //in the current package
        Iterator it = imports.iterator();
        while (it.hasNext()) {
            ImportItem ii = (ImportItem)it.next();
            if (ii.getPackageName().equals(packageName)) {
                inPackage.add(ii);
                
                if (remove) {
                    it.remove();
                }
            }
        }
        
        return inPackage;
    }
    
    private void findAndRemove(String token, ArrayList imports) {
        Iterator it = imports.iterator();
        
        while (it.hasNext()) {
            ImportItem ii = (ImportItem)it.next();
            if (ii.getShortClassName().equals(token)) {
                it.remove();
            }
        }
    }
    
    public List findUnusedImports(Buffer buffer) {
        ArrayList unusedImports = new ArrayList();
        
        //Load a list of all of the existing import statements into the toReturn list
        ArrayList imports = new ImportList(buffer).getImportList();
        
        //Add any imports that are in the current package to the unused list
        unusedImports.addAll(findImportsInPackage(findPackage(buffer), imports, true));
        
        //Remove any blank items from the import list
        Iterator it = imports.iterator();
        while (it.hasNext()) {
            ImportItem ii = (ImportItem)it.next();
            if (ii.getShortClassName().equals("")) {
                it.remove();
            }
        }
        
        //Go through each token in the file after the import statements and find
        //any class that matches the imports        
        StreamTokenizer classTokenizer = createClassTokenizer(buffer);
        
        try {
            int lastTokenType = -1;
            while (classTokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                if (classTokenizer.ttype == StreamTokenizer.TT_WORD) {
                    String token = classTokenizer.sval;
                    System.out.println("Token - " + token);
                    
                    //Skip any import lines
                    if ((token.equals("import")) && (lastTokenType == StreamTokenizer.TT_EOL)) {
                        while (classTokenizer.nextToken() != StreamTokenizer.TT_EOL) {}
                        continue;
                    }
                    
                    findAndRemove(token, imports);
                }
                
                //If there aren't any imports left in the import list, it isn't worth
                //continuing.
                if (imports.isEmpty()) {
                    break;
                }
                
                lastTokenType = classTokenizer.ttype;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An unexpected error has occurred in JImporter.  Please send a copy of your Activity Log to MattFlower@yahoo.com.");
        }                  
        
        //Now add any left over imports to the unused imports list
        unusedImports.addAll(imports);
        
        return unusedImports;
    }
}

