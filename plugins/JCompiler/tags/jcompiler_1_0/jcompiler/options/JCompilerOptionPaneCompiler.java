/*
 * JCompilerOptionPaneCompiler.java - plugin options pane for JCompiler - compiler options
 * (c) 1999, 2000 Kevin A. Burton and Aziz Sharif
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */

package jcompiler.options;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;


/**
 * This is the option pane that jEdit displays for JCompiler's
 * compiler plugin options.
 */
public class JCompilerOptionPaneCompiler
         extends AbstractOptionPane
         implements ActionListener, ChangeListener
{
    private JCheckBox genDebug;
    private JCheckBox genOptimized;
    private JCheckBox showDeprecation;
    private JCheckBox specifyOutputDirectory;
    private JCheckBox useJavaCP;
    private JCheckBox addPkg2CP;
    private JLabel cpLabel;
    private JTextField newCP;
    private JTextField outputDirectory;
    private JTextField otherOptions;
    private JButton pickDirectory;


    public JCompilerOptionPaneCompiler() {
        super("jcompiler.compiler");
    }


    public void _init() {
        // "Generate debug info (-g)"
        genDebug = new JCheckBox(jEdit.getProperty(
            "options.jcompiler.gendebug"));
        genDebug.setSelected(jEdit.getBooleanProperty(
            "jcompiler.gendebug", true));
        addComponent(genDebug);

        // "Generate optimized code (-O)"
        genOptimized = new JCheckBox(jEdit.getProperty(
            "options.jcompiler.genoptimized"));
        genOptimized.setSelected(jEdit.getBooleanProperty(
            "jcompiler.genoptimized", false));
        addComponent(genOptimized);

        // "Warn about use of deprecated API (-deprecation)"
        showDeprecation = new JCheckBox(jEdit.getProperty(
            "options.jcompiler.showdeprecated"));
        showDeprecation.setSelected(jEdit.getBooleanProperty(
            "jcompiler.showdeprecated", true));
        addComponent(showDeprecation);

        // "Use CLASSPATH defined when running jEdit"
        useJavaCP = new JCheckBox(jEdit.getProperty(
            "options.jcompiler.usejavacp"));
        useJavaCP.setSelected(jEdit.getBooleanProperty(
            "jcompiler.usejavacp", true));
        useJavaCP.addActionListener(this);
        addComponent(useJavaCP);

        // "Java System CLASSPATH"/"User defined CLASSPATH"
        cpLabel = new JLabel();
        addComponent(cpLabel);

        // CLASSPATH text field
        newCP = new JTextField();
        newCP.setPreferredSize(new Dimension(270, newCP.getPreferredSize().height));
        addComponent("", newCP);

        // "Add package of compiled sourcefile to CLASSPATH"
        addPkg2CP = new JCheckBox(jEdit.getProperty(
            "options.jcompiler.addpkg2cp"));
        addPkg2CP.setSelected(jEdit.getBooleanProperty(
            "jcompiler.addpkg2cp", true));
        addComponent(addPkg2CP);

        // "Use different output directory (-d)"
        specifyOutputDirectory = new JCheckBox(jEdit.getProperty(
            "options.jcompiler.specifyoutputdirectory"));
        specifyOutputDirectory.setSelected(jEdit.getBooleanProperty(
            "jcompiler.specifyoutputdirectory", false));
        specifyOutputDirectory.addChangeListener(this);
        addComponent(specifyOutputDirectory);

        // Output directory text field (+ select button)
        outputDirectory = new JTextField();
        String output = jEdit.getProperty("jcompiler.outputdirectory");
        outputDirectory.setText(output == null ? "" : output);
        pickDirectory = new JButton(GUIUtilities.loadIcon("Open24.gif"));
        pickDirectory.setMargin(new Insets(0,0,0,0));
        pickDirectory.addActionListener(this);
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(outputDirectory, BorderLayout.CENTER);
        outputPanel.add(pickDirectory, BorderLayout.EAST);
        addComponent(jEdit.getProperty("options.jcompiler.outputDirectory"),
            outputPanel);

        // "Other options:"
        otherOptions = new JTextField();
        String options = jEdit.getProperty("jcompiler.otheroptions");
        otherOptions.setText(options == null ? "" : options);
        addComponent(jEdit.getProperty("options.jcompiler.otheroptions"), otherOptions);

        // ========== misc setup ==========
        enableOutputDirectory(jEdit.getBooleanProperty(
            "jcompiler.specifyoutputdirectory", false));
        adjustCPSettings();
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pickDirectory) {
            // the "choose dir" button was pressed:
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //chooser.setTitle("Select output directory");
            int retVal = chooser.showOpenDialog(this);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (file != null) {
                    try {
                        String dirName = file.getCanonicalPath();
                        outputDirectory.setText(dirName);
                    }
                    catch(IOException donothing) {
                        // shouldn't happen
                    }
                }
            }
        } else {
            adjustCPSettings();
        }
    }


    public void stateChanged(ChangeEvent e) {
        if (specifyOutputDirectory.isSelected()) {
            enableOutputDirectory(true);
        } else {
            enableOutputDirectory(false);
        }
    }


    private void enableOutputDirectory(boolean enable) {
        outputDirectory.setEnabled(enable);
        pickDirectory.setEnabled(enable);
    }


    private void adjustCPSettings() {
        if (useJavaCP.isSelected()) {
            newCP.setEnabled(false);
            newCP.setText(System.getProperty("java.class.path"));
            cpLabel.setText(jEdit.getProperty(
                "options.jcompiler.usejavacp.true"));
        } else {
            newCP.setEnabled(true);
            newCP.setText(jEdit.getProperty("jcompiler.classpath"));
            cpLabel.setText(jEdit.getProperty(
                "options.jcompiler.usejavacp.false"));
        }
    }


    public void _save() {
        jEdit.setBooleanProperty("jcompiler.genDebug", genDebug.isSelected());
        jEdit.setBooleanProperty("jcompiler.genOptimized", genOptimized.isSelected());
        jEdit.setBooleanProperty("jcompiler.showdeprecated", showDeprecation.isSelected());
        jEdit.setBooleanProperty("jcompiler.specifyoutputdirectory", specifyOutputDirectory.isSelected());
        jEdit.setBooleanProperty("jcompiler.usejavacp", useJavaCP.isSelected());
        jEdit.setBooleanProperty("jcompiler.addpkg2cp", addPkg2CP.isSelected());
        jEdit.setProperty("jcompiler.outputdirectory", outputDirectory.getText().trim());
        jEdit.setProperty("jcompiler.otheroptions", otherOptions.getText().trim());
        if (!useJavaCP.isSelected()) {
            jEdit.setProperty("jcompiler.classpath", newCP.getText().trim());
        }
    }

}

