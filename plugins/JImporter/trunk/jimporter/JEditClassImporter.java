/*
 *  JEditClassImporter.java - Plugin for add java imports to the top of a java file.
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
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;

/**
 * This class is the interface between jEdit and the importing utilities. It is
 * the only class that should know about the jEdit classes.
 *
 * @author  Matthew Flower
 */
public class JEditClassImporter {
    /** 
     * The jEdit property that identifies the text of the "Auto Search at Point" option
     * checkbox.
     */    
    public static final String AUTO_SEARCH_AT_POINT_LABEL = "options.jimporter.autosearchatpoint.label";
    private static final String AUTO_SEARCH_AT_POINT_VALUE = "jimporter.autosearchatpoint.enabled";
    private static final boolean AUTO_SEARCH_AT_POINT_DEFAULT = false;
    
    /** 
     * Determine whether JImporter should automatically run a search when a user
     * imports the class at point.
     *
     * @return A <CODE>boolean</CODE> value indicating whether JImporter should run a search
     * automatically when a user chooses to "Import class at Point".
     */
    public static boolean isAutoSearchAtPoint() {
        boolean toReturn = AUTO_SEARCH_AT_POINT_DEFAULT;
        String isAutoSearchProperty = jEdit.getProperty(AUTO_SEARCH_AT_POINT_VALUE);
        
        if (isAutoSearchProperty != null) {
            toReturn = Boolean.valueOf(isAutoSearchProperty).booleanValue();
        }
        
        return toReturn;
    }
    
    /** 
     * Indicate whether JImporter should automatically run a search when a user
     * imports the class at point.
     *
     * @param state Indicates whether JImporter should automatically run a search when a user
     * imports the class at point.
     */
    public static void setAutoSearchAtPoint(boolean state) {
        jEdit.setProperty(AUTO_SEARCH_AT_POINT_VALUE, new Boolean(state).toString());
    }

    /** The property that identifies the text of the "Auto Import on Match" checkbox. */    
    public static final String AUTO_IMPORT_ON_MATCH_LABEL = "options.jimporter.autoimportonmatch.label";
    private static final String AUTO_IMPORT_ON_MATCH_VALUE = "jimporter.autoimportonmatch.enabled";
    private static final boolean AUTO_IMPORT_ON_MATCH_DEFAULT = false;

    /** 
     * Determine whether JImporter should automatically import a class when there
     * is only one class that matches.  (This really only applies if you are
     * doing "Auto Search at Point".)
     *
     * @return A <CODE>boolean</CODE> value indicating whether JImporter should automatically
     * import a class if only one class is found when "Importing class at point."
     */
    public static boolean isAutoImportOnOneMatch() {
        boolean toReturn = AUTO_IMPORT_ON_MATCH_DEFAULT;
        String isAutoImportOnOneMatchProperty = jEdit.getProperty(AUTO_IMPORT_ON_MATCH_VALUE);
        
        if (isAutoImportOnOneMatchProperty != null) {
            toReturn = Boolean.valueOf(isAutoImportOnOneMatchProperty).booleanValue();
        }
        
        return toReturn;
    }
     
    /** 
     * Indicate whether JImporter should automatically import a class when there
     * is only one class that matches the search string.
     *
     * @param state A <CODE>boolean</CODE> value indicating whether JImporter should automatically
     * import a class when there is only one class that matches the search string.
     */
    public static void setAutoImportOnOneMatch(boolean state) {
        jEdit.setProperty(AUTO_IMPORT_ON_MATCH_VALUE, new Boolean(state).toString());
    }
    
    /** 
     * Import a class that the user will specify. As opposed to the
     * importClassAtPoint() method, this method does not attempt to determine
     * the class that the user is currently pointing at.
     *
     * @param currentView A <code>View</code> value used to determine what we 
     * are going to import and where we are going to import it into.
     * @see #importClassAtPoint
     */
    public static void importClass(View currentView) {
        //Raise the dialog box to do the search
        JavaImportClassForm importClassForm = new JavaImportClassForm(currentView, "");
        importClassForm.setLocationRelativeTo(currentView);
        importClassForm.show();
        String foundClass = importClassForm.getImportedClass();

        //Import the class
        if (foundClass != null) {
            if (importClass(foundClass, currentView)) {
                currentView.getStatus().setMessageAndClear(foundClass + " imported.");
            }
        }
    }        

    /** 
     * Import the class that the cursor is currently pointing at. Currently,
     * this method only grabs the current text, it does not try to determine the
     * class type of a variable.
     *
     * @param currentView A <code>View</code> value used to determine what we are 
     * going to import and where we are going to import it into.
     * @see #importClass
     */
    public static void importClassAtPoint(View currentView) {
        //Get the class name at the current point
        String classToSearchFor = getWordAtPoint(currentView);

        //Raise the dialog box to do the search
        JavaImportClassForm importClassForm = new JavaImportClassForm(currentView, classToSearchFor);
        importClassForm.setLocationRelativeTo(currentView);
        
        //Run the search automatically if the user has selected that option
        if ((isAutoSearchAtPoint()) && (classToSearchFor.length() > 0)) {
            importClassForm.generateImportModel(classToSearchFor);
        }
        
        if (! (isAutoImportOnOneMatch() && (importClassForm.getMatchCount() == 1))) {
            importClassForm.show();
        }
        
        String foundClass = importClassForm.getImportedClass();

        //Import the class
        if (foundClass != null) {
            if (importClass(foundClass, currentView)) {
                currentView.getStatus().setMessageAndClear(foundClass + " imported.");
            }
        }
        
        //Finally, make sure that the focus ends up in the text window
        currentView.getTextArea().requestFocus();
    }
    
    /** 
     * Insert the fully qualified name of a class that the user selects into the
     * current buffer at the current caret position.
     *
     * @param currentView A <code>View</code> value used to determine what we 
     * are going to import and where we are going to import it into.
     */
    public static void insertClassAtPoint(View currentView) {
        //Raise the dialog box to do the search
        JavaImportClassForm importClassForm = new JavaImportClassForm(currentView, "");
        importClassForm.setLocationRelativeTo(currentView);
        importClassForm.show();
        String foundClass = importClassForm.getImportedClass();

        //Insert the class at the point
        Buffer buffer = currentView.getBuffer();
        buffer.insert(currentView.getTextArea().getCaretPosition(), foundClass);
    }

    /**
     * This method grabs the word that the point is currently pointing at.
     *
     * @param currentView  The view which contains the cursor position that we 
     * are going to grab the word for.
     * @return a <code>String</code> value containing the word that the cursor is
     * currently placed on.
     */
    private static String getWordAtPoint(View currentView) {
        JEditTextArea textArea = currentView.getTextArea();
        String currentWord;

        //Find numerous variables from this class that we will feed to
        //the findWordStart and findWordEnd helper functions.
        int currentLine = textArea.getCaretLine();
        int lineStart = textArea.getLineStartOffset(currentLine);
        int lineEnd = textArea.getLineEndOffset(currentLine);
        int offset = textArea.getCaretPosition() - lineStart;
        
        String lineText = textArea.getLineText(currentLine);
        String noWordSep = currentView.getBuffer().getStringProperty("noWordSep");
        
        if ((lineEnd-lineStart) > 1) {        
            //Find the start and end of the current word
            int wordStart = TextUtilities.findWordStart(lineText, 
                Math.min(offset, lineText.length()-1)
                , noWordSep);
            int wordEnd = TextUtilities.findWordEnd(lineText, 
                Math.min(offset, lineText.length()-1)
                , noWordSep);
    
            //Extract the text of the current word
            currentWord = currentView.getBuffer().getText(lineStart + wordStart, wordEnd - wordStart);
        } else {
            //This is a blank line, there isn't going to be a "current word"
            currentWord = "";
        }

        //Cut off any spaces, which you'll get if you are on some contiguous
        //whitespace.
        return currentWord.trim();
    }

    /**
     * This method adds an import statement for the class passed as a parameter.
     *
     * @param fqClassName The fully-qualified class name of that class we would
     * like to import.
     * @param currentView A view that will allow use to get the buffer where
     * we are going to do the import.
     */
    private static boolean importClass(String fqClassName, View currentView) {
        //Construct the full String that we are going to add to the file.
        fqClassName = "import " + fqClassName + ";\n";

        //Find the location where you are going to insert the line
        ClassImporter classImporter = new ClassImporter();
        classImporter.setSourceBuffer(currentView.getBuffer());
        classImporter.setImportClass(fqClassName);
        return classImporter.addImportToBuffer();

        //JOptionPane.showMessageDialog(null, fqClassName);
    }
}
