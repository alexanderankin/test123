/*
 * NavigationOptionPane.java - Navigation options.
 *
 * Copyright (C) 2002 Anthony Roy
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
package uk.co.antroy.latextools.options;

import java.awt.Toolkit;

import java.text.NumberFormat;
import java.text.ParseException;

import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import uk.co.antroy.latextools.parsers.NavigationList;


public class NavigationOptionPane
    extends AbstractOptionPane {

    //~ Instance/static variables .............................................

	
    private JCheckBox inserttags;

    private JTextField userDir;
    private JCheckBox insertcitetags;
    private WholeNumberField wordlength;
    private WholeNumberField wordcount;
    private JComboBox nav_list;
    
    //~ Constructors ..........................................................

    /**
     * Creates a new NavigationOptionPane object.
     */
    public NavigationOptionPane() {
        super("navigation");
    }

    //~ Methods ...............................................................

    protected void _init() 
    {
        initStructureNav();
        initBibNav();
    }

    protected void _save() {
        saveStructureNav();
        saveBibNav();
    }

    private void initBibNav() 
    {
	    
        //addComponent(new JLabel("<html><h3>Bibliography Navigation"));
	addSeparator("options.latextools.navigation.bibliography");
        wordlength = new WholeNumberField(jEdit.getIntegerProperty(
                                                      "bibtex.bibtitle.wordlength", 
                                                      0), 4);
        wordcount = new WholeNumberField(jEdit.getIntegerProperty(
                                                     "bibtex.bibtitle.wordcount", 
                                                     0), 4);
        addComponent(jEdit.getProperty("options.bibtex.wordlength"), wordlength);
        addComponent(jEdit.getProperty("options.bibtex.wordcount"), wordcount);

        addComponent(insertcitetags = new JCheckBox(jEdit.getProperty(
        "options.bibtex.inserttags")));
        addComponent(inserttags = new JCheckBox(jEdit.getProperty(
        "options.reference.inserttags")));

        inserttags.getModel().setSelected(jEdit.getBooleanProperty(
        "reference.inserttags"));
        insertcitetags.getModel().setSelected(jEdit.getBooleanProperty(
        "bibtex.inserttags"));

    }



    public static String getUserDir() 
    {
	    String settingsLoc = "";
	    try {
		    settingsLoc = MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "navigation");
	    }
	    finally {} 
	    return jEdit.getProperty("options.navigation.userdir", settingsLoc);
    }
    
    public static void setUserDir(String newDir) 
    {
	    jEdit.setProperty("options.navigation.userdir", newDir);
    }
    private void initStructureNav() 
    {
        
        addSeparator("options.latextools.navigation.structure");
        nav_list = new JComboBox(NavigationList.getNavigationData().toArray());
        
        addComponent(jEdit.getProperty("options.latextools.navigation.listchoose"), nav_list, 0);

        NavigationList nl = NavigationList.getDefaultGroup();
        nav_list.setSelectedItem(nl);


        userDir = new JTextField(getUserDir());
        addComponent(jEdit.getProperty("options.latextools.navigation.userdir.label"), userDir);
    }

    private void saveBibNav() {
        jEdit.setBooleanProperty("bibtex.inserttags", 
                                 insertcitetags.getModel().isSelected());
        jEdit.setIntegerProperty("bibtex.bibtitle.wordlength", 
                                 wordlength.getValue());
        jEdit.setIntegerProperty("bibtex.bibtitle.wordcount", 
                                 wordcount.getValue());
    }


    private void saveStructureNav() {
	    setUserDir(userDir.getText());
	    NavigationList nl = (NavigationList)nav_list.getSelectedItem();
	    NavigationList.setDefaultGroup(nl);
	    
	    jEdit.setBooleanProperty("reference.inserttags", 
                                 inserttags.getModel().isSelected());

    }

    //~ Inner classes .........................................................

    //   }}}
    protected class WholeNumberField
        extends JTextField {

        //~ Instance/static variables .........................................

        private Toolkit toolkit;
        private NumberFormat integerFormatter;

        //~ Constructors ......................................................

        public WholeNumberField(int value, int columns) {
            super(columns);
            toolkit = Toolkit.getDefaultToolkit();
            integerFormatter = NumberFormat.getNumberInstance(Locale.US);
            integerFormatter.setParseIntegerOnly(true);
            setValue(value);
        }

        //~ Methods ...........................................................

        public void setValue(int value) {
            setText(integerFormatter.format(value));
        }

        public int getValue() {

            int retVal = 0;

            try {
                retVal = integerFormatter.parse(getText()).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
                Log.log(Log.ERROR, this, 
                        "Value should be an integer value." + 
                        " This error should have been prevented by the UI");
            }

            return retVal;
        }

        protected Document createDefaultModel() {

            return new WholeNumberDocument();
        }

        //~ Inner classes .....................................................

        protected class WholeNumberDocument
            extends PlainDocument {

            //~ Methods .......................................................

            public void insertString(int offs, String str, AttributeSet a)
                              throws BadLocationException {

                char[] source = str.toCharArray();
                char[] result = new char[source.length];
                int j = 0;

                for (int i = 0; i < result.length; i++) {

                    if (Character.isDigit(source[i])) {
                        result[j++] = source[i];
                    } else {
                        System.err.println("insertString: " + source[i]);
                    }
                }

                super.insertString(offs, new String(result, 0, j), a);
            }
        }
    }
}
