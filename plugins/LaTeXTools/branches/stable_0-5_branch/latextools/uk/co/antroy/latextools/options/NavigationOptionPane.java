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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;


public class NavigationOptionPane
    extends AbstractOptionPane {

    //~ Instance/static variables .............................................

    private JCheckBox inserttags;
    private JTextField userDir;
    private JCheckBox insertcitetags;
    private JPanel wordlengthPan;
    private JPanel wordcountPan;
    private WholeNumberField wordlength;
    private WholeNumberField wordcount;

    //~ Constructors ..........................................................

    /**
     * Creates a new NavigationOptionPane object.
     */
    public NavigationOptionPane() {
        super("navigation");
    }

    //~ Methods ...............................................................

    protected void _init() {
        initStructureNav();
        initLabelNav();
        initBibNav();
    }

    protected void _save() {
        saveStructureNav();
        saveLabelNav();
        saveBibNav();
    }

    private void initBibNav() {
        addComponent(new JLabel("<html><h3>Bibliography Navigation"));
        wordlength = new WholeNumberField(jEdit.getIntegerProperty(
                                                      "bibtex.bibtitle.wordlength", 
                                                      0), 4);
        wordcount = new WholeNumberField(jEdit.getIntegerProperty(
                                                     "bibtex.bibtitle.wordcount", 
                                                     0), 4);
        wordlengthPan = new JPanel();
        wordlengthPan.add(new JLabel(jEdit.getProperty(
                                                 "options.bibtex.wordlength")));
        wordlengthPan.add(wordlength);
        wordcountPan = new JPanel();
        wordcountPan.add(new JLabel(jEdit.getProperty(
                                                "options.bibtex.wordcount")));
        wordcountPan.add(wordcount);
        addComponent(insertcitetags = new JCheckBox(jEdit.getProperty(
                                                                "options.bibtex.inserttags")));
        insertcitetags.getModel().setSelected(jEdit.getBooleanProperty(
                                                          "bibtex.inserttags"));
        addComponent(wordlengthPan);
        addComponent(wordcountPan);
    }

    private void initLabelNav() {
        addComponent(new JLabel("<html><h3>Label Navigation"));
        addComponent(inserttags = new JCheckBox(jEdit.getProperty(
                                                            "options.reference.inserttags")));
        inserttags.getModel().setSelected(jEdit.getBooleanProperty(
                                                      "reference.inserttags"));
    }

    private void initStructureNav() {
        addComponent(new JLabel("<html><h3>Structure Navigation"));
        userDir = new JTextField(30);
        userDir.setText(jEdit.getProperty("options.navigation.userdir"));

        JLabel userDirLab = new JLabel(jEdit.getProperty(
                                                   "options.navigation.userdir.label"));
        JPanel p = new JPanel();
        p.add(userDirLab);
        p.add(userDir);
        addComponent(p);
    }

    private void saveBibNav() {
        jEdit.setBooleanProperty("bibtex.inserttags", 
                                 insertcitetags.getModel().isSelected());
        jEdit.setIntegerProperty("bibtex.bibtitle.wordlength", 
                                 wordlength.getValue());
        jEdit.setIntegerProperty("bibtex.bibtitle.wordcount", 
                                 wordcount.getValue());
    }

    private void saveLabelNav() {
        jEdit.setBooleanProperty("reference.inserttags", 
                                 inserttags.getModel().isSelected());
    }

    private void saveStructureNav() {
        jEdit.setProperty("options.navigation.userdir", userDir.getText());
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
