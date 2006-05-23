/*
 * LaTeXOptionPane.java - General options.
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


//{{{ Imports
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


//}}}
public class LaTeXOptionPane
    extends AbstractOptionPane {

    //~ Instance/static variables .............................................

    private JTextField compileCommandField;
    private JTextField compileExtField;
    private JTextField compileShowErrorsSwitch;
    private JTextField bibtexCommandField;
    private JTextField viewerCommandField;
    private JTextField viewerExtField;
    private JTextField classpathField;
    private JCheckBox compileDetach;
    private JCheckBox cStyleErrors;
    private JCheckBox bibtexDetach;
    private JCheckBox viewerDetach;

    //~ Constructors ..........................................................

    public LaTeXOptionPane() {
        super("general");
    }

    //~ Methods ...............................................................

    protected void _init() {
        initGeneral();
    }

    protected void _save() {
        saveGeneral();
    }

    private void initGeneral() {
        addComponent(new JLabel("<html><h3>Compilation Options"));
        compileCommandField = new JTextField(30);
        compileCommandField.setText(jEdit.getProperty("latex.compile.command"));

        JPanel p1 = new JPanel();
        p1.add(new JLabel("Default Compile Command"));
        p1.add(compileCommandField);
        compileExtField = new JTextField(8);
        compileExtField.setText(jEdit.getProperty("latex.compile.ext"));

        JPanel p2 = new JPanel();
        p2.add(new JLabel("Default Compile Extension"));
        p2.add(compileExtField);
        compileDetach = new JCheckBox("Detach Compilation Process?");
        compileDetach.getModel().setSelected(jEdit.getBooleanProperty(
                                                         "latex.compile.detach"));
        cStyleErrors = new JCheckBox("Show Errors in Error List? (Passes a switch to latex - see the Latex error style switch)");
        cStyleErrors.getModel().setSelected(jEdit.getBooleanProperty(
                                                        "latex.compile.parse-errors"));
        compileShowErrorsSwitch = new JTextField(30);
        compileShowErrorsSwitch.setText(jEdit.getProperty("latex.compile.c-errors"));
        String showErrTooltip = "Passed to latex; use -c-style-errors for MiKTeX"
    		+" and -file-line-error-style for linux distributions";
        compileShowErrorsSwitch.setToolTipText(showErrTooltip);
        JPanel pShowErr1 = new JPanel();
        pShowErr1.add(new JLabel("Latex error style switch"));
        pShowErr1.add(compileShowErrorsSwitch);
        JLabel showErrDetailLabel = new JLabel("\t("+showErrTooltip+")");
        /*JPanel pShowErr2 = new JPanel();
        pShowErr2.setLayout(new BoxLayout(pShowErr2,BoxLayout.Y_AXIS));
        pShowErr2.add(pShowErr1);
        pShowErr2.add(showErrDetailLabel);*/
        
        addComponent(p1);
        addComponent(p2);
        addComponent(compileDetach);
        addComponent(cStyleErrors);
        addComponent(pShowErr1);
        addComponent(showErrDetailLabel);
        bibtexCommandField = new JTextField(30);
        bibtexCommandField.setText(jEdit.getProperty("latex.bibtex.command"));

        JPanel pbib = new JPanel();
        pbib.add(new JLabel("Default BibTeX Command"));
        pbib.add(bibtexCommandField);
        bibtexDetach = new JCheckBox("Detach BibTeX Process?");
        bibtexDetach.getModel().setSelected(jEdit.getBooleanProperty(
                                                        "latex.bibtex.detach"));
        addComponent(pbib);
        addComponent(bibtexDetach);
        addComponent(new JLabel("<html><h3>Viewer Options"));
        viewerCommandField = new JTextField(30);
        viewerCommandField.setText(jEdit.getProperty(
                                               "latex.viewoutput.command"));

        JPanel p3 = new JPanel();
        p3.add(new JLabel("Default Viewer Command"));
        p3.add(viewerCommandField);
        viewerExtField = new JTextField(30);
        viewerExtField.setText(jEdit.getProperty("latex.viewoutput.ext"));

        JPanel p4 = new JPanel();
        p4.add(new JLabel("Default Viewer Extension"));
        p4.add(viewerExtField);
        viewerDetach = new JCheckBox("Detach Viewer Process?");
        viewerDetach.getModel().setSelected(jEdit.getBooleanProperty(
                                                        "latex.viewoutput.detach"));
        addComponent(p3);
        addComponent(p4);
        addComponent(viewerDetach);
        addComponent(new JLabel("<html><h3>Classpath Options"));
        addComponent(new JLabel("Provide a semi-colon delimited list of directories to search for imports:"));
        classpathField = new JTextField(30);
        classpathField.setText(jEdit.getProperty("latex.classpath.dirs"));
        addComponent(classpathField);
    }

    private void saveGeneral() {
        jEdit.setProperty("latex.compile.command", 
                          compileCommandField.getText());
        jEdit.setProperty("latex.compile.ext", compileExtField.getText());
        jEdit.setBooleanProperty("latex.compile.detach", 
                                 compileDetach.getModel().isSelected());
        jEdit.setProperty("latex.bibtex.command", bibtexCommandField.getText());
        jEdit.setBooleanProperty("latex.bibtex.detach", 
                                 bibtexDetach.getModel().isSelected());
        jEdit.setBooleanProperty("latex.compile.parse-errors", 
                                 cStyleErrors.getModel().isSelected());
        jEdit.setProperty("latex.viewoutput.command", 
                          viewerCommandField.getText());
        jEdit.setProperty("latex.viewoutput.ext", viewerExtField.getText());
        jEdit.setBooleanProperty("latex.viewoutput.detach", 
                                 viewerDetach.getModel().isSelected());
        jEdit.setProperty("latex.classpath.dirs", classpathField.getText());
        jEdit.setProperty("latex.compile.c-errors", compileShowErrorsSwitch.getText());
    }
}
