/*
 * JavaInsightOptionPane.java - options panel for JavaInsight
 * Copyright (C) 2001 Dirk Moebius
 *
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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

package javainsight;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


/**
 * An option panel for the Java Insight plugin.
 *
 * @author Dirk Moebius
 * @version $Id$
 */
public class JavaInsightOptionPane extends AbstractOptionPane implements ActionListener {

    public JavaInsightOptionPane() {
        super("javainsight");
    }


    public void _init() {
        addSeparator("options.javainsight.decompiler");

        cStyle = new JComboBox(new String[] { "sun", "gnu" });
        cStyle.setSelectedItem(jEdit.getProperty("javainsight.jode.style", "sun"));
        addComponent("Coding Style:", cStyle);

        addComponent(Box.createVerticalStrut(15));

        addComponent(cPretty =
            new JCheckBox("Use \"pretty\" names for local variables",
                jEdit.getBooleanProperty("javainsight.jode.pretty", true)));

        addComponent(cOnetime =
            new JCheckBox("Remove locals, that are used only one time",
                jEdit.getBooleanProperty("javainsight.jode.onetime", false)));

        addComponent(cDecrypt =
            new JCheckBox("Decrypt encrypted strings",
                jEdit.getBooleanProperty("javainsight.jode.decrypt", true)));

        addComponent(Box.createVerticalStrut(15));
        addComponent(new JLabel("Generate imports..."));

        cImportPkgLimit = new JTextField(jEdit.getProperty("javainsight.jode.pkglimit", "0"));
        Box b1 = Box.createHorizontalBox();
        b1.add(cImportPkgLimit);
        b1.add(new JLabel(" classes"));
        addComponent("...for packages with more than", b1);

        cImportClassLimit = new JTextField(jEdit.getProperty("javainsight.jode.clslimit", "1"));
        Box b2 = Box.createHorizontalBox();
        b2.add(cImportClassLimit);
        b2.add(new JLabel(" times"));
        addComponent("...for classes used more than", b2);

        addComponent(Box.createVerticalStrut(5));
        addComponent(new JLabel("(0 means generate no imports)"));

        addComponent(Box.createVerticalStrut(15));

        addSeparator("options.javainsight.general");

        addComponent(cDecompileToBuffer =
            new JCheckBox("Decompile to jEdit buffer only (w/o storing on filesystem)",
                jEdit.getBooleanProperty("javainsight.decompileToBuffer", true)));
        cDecompileToBuffer.addActionListener(this);

        addComponent(cOverwrite =
            new JCheckBox("Overwrite existing files",
                jEdit.getBooleanProperty("javainsight.overwrite", true)));

        addComponent(cClearDirty =
            new JCheckBox("Set status of buffer to \"saved\" after decompile",
                jEdit.getBooleanProperty("javainsight.clearDirty", false)));

        enableComponents();
    }


    /**
     * Called when the options dialog's `OK' button is pressed.
     * This saves any properties saved in this option pane.
     */
    public void _save() {
        jEdit.setProperty("javainsight.jode.style", cStyle.getSelectedItem().toString());
        jEdit.setBooleanProperty("javainsight.jode.pretty", cPretty.isSelected());
        jEdit.setBooleanProperty("javainsight.jode.onetime", cOnetime.isSelected());
        jEdit.setBooleanProperty("javainsight.jode.decrypt", cDecrypt.isSelected());
        jEdit.setProperty("javainsight.jode.pkglimit", cImportPkgLimit.getText());
        jEdit.setProperty("javainsight.jode.clslimit", cImportClassLimit.getText());
        jEdit.setBooleanProperty("javainsight.decompileToBuffer", cDecompileToBuffer.isSelected());
        jEdit.setBooleanProperty("javainsight.overwrite", cOverwrite.isSelected());
        jEdit.setBooleanProperty("javainsight.clearDirty", cClearDirty.isSelected());
    }


    public void actionPerformed(ActionEvent evt) {
        enableComponents();
    }


    private void enableComponents() {
        cOverwrite.setEnabled(!cDecompileToBuffer.isSelected());
        cClearDirty.setEnabled(cDecompileToBuffer.isSelected());
    }


    private JComboBox cStyle;
    private JCheckBox cPretty;
    private JCheckBox cOnetime;
    private JCheckBox cDecrypt;
    private JCheckBox cDecompileToBuffer;
    private JCheckBox cOverwrite;
    private JCheckBox cClearDirty;
    private JTextField cImportPkgLimit;
    private JTextField cImportClassLimit;

}
