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


import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


/**
 * An option panel for the Java Insight plugin.
 *
 * @author Dirk Moebius
 * @version $Id$
 */
public class JavaInsightOptionPane extends AbstractOptionPane
{

	private static final long serialVersionUID = 5947532463024083203L;

	public JavaInsightOptionPane() {
        super("javainsight");
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void _init() {
    	addSeparator("options.javainsight.jode.decompiler");

        cStyle = new JComboBox(new String[] { "sun", "gnu", "pascal" });
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

        addSeparator("options.javainsight.procyon.decompiler");
        
        JPanel grid = new JPanel(new GridLayout(6,2));
         
        grid.add(cMergeVariables =
                new JCheckBox("Attempt to merge variables",
                        jEdit.getBooleanProperty("javainsight.procyon.merge", false)));

        grid.add(cExplicitImports =
                new JCheckBox("Force explicit imports",
                    jEdit.getBooleanProperty("javainsight.procyon.explicitimport", false)));

        grid.add(cForceExplicitTypes =
                new JCheckBox("Always print type arguments to generic methods",
                    jEdit.getBooleanProperty("javainsight.procyon.explicittypes", false)));

        grid.add(cRetainRedundantCasts =
                new JCheckBox("Do not remove redundant explicit casts.",
                    jEdit.getBooleanProperty("javainsight.procyon.retaincasts", false)));

        grid.add(cFlattenSwitchBlocks =
                new JCheckBox("Remove Braces in Switch Blocks",
                    jEdit.getBooleanProperty("javainsight.procyon.flattenswitch", false)));
        
        grid.add(cShowSynthenticMembers =
                new JCheckBox("Show compiler generated members",
                    jEdit.getBooleanProperty("javainsight.procyon.showsynth", false)));

        grid.add(cUnoptomisedAstByteCode =
                new JCheckBox("Show unoptimized AST byte-code",
                    jEdit.getBooleanProperty("javainsight.procyon.unoptast", false)));

        grid.add(cExcludeNestedTypes =
                new JCheckBox("Exclude nested types",
                    jEdit.getBooleanProperty("javainsight.procyon.excludenested", false)));

        grid.add(cRetainPointlessSwitches =
                new JCheckBox("Do not lift the contents of switches",
                    jEdit.getBooleanProperty("javainsight.procyon.retainswitches", false)));

        grid.add(cEnableEagerMethodLoading =
                new JCheckBox("Enable eager loading of method bodies",
                    jEdit.getBooleanProperty("javainsight.procyon.eagermethodloading", false)));

        grid.add(cSimplifyMemberReferences =
                new JCheckBox("Simplify type-qualified member references",
                    jEdit.getBooleanProperty("javainsight.procyon.simplifymember", false)));

        grid.add(cDisableForEachTransforms =
                new JCheckBox("Disable 'for each' loop transforms",
                    jEdit.getBooleanProperty("javainsight.procyon.disableforeach", false)));

        addComponent(grid);
        
    }


    /**
     * Called when the options dialog's `OK' button is pressed.
     * This saves any properties saved in this option pane.
     */
    @Override
    public void _save() {
        jEdit.setProperty("javainsight.jode.style", cStyle.getSelectedItem().toString());
        jEdit.setBooleanProperty("javainsight.jode.pretty", cPretty.isSelected());
        jEdit.setBooleanProperty("javainsight.jode.onetime", cOnetime.isSelected());
        jEdit.setBooleanProperty("javainsight.jode.decrypt", cDecrypt.isSelected());
        jEdit.setProperty("javainsight.jode.pkglimit", cImportPkgLimit.getText());
        jEdit.setProperty("javainsight.jode.clslimit", cImportClassLimit.getText());

        jEdit.setBooleanProperty("javainsight.procyon.merge", cMergeVariables.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.explicitimport", cExplicitImports.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.explicittypes", cForceExplicitTypes.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.retaincasts", cRetainRedundantCasts.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.flattenswitch", cFlattenSwitchBlocks.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.showsynth", cShowSynthenticMembers.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.unoptast", cUnoptomisedAstByteCode.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.excludenested", cExcludeNestedTypes.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.retainswitches", cRetainPointlessSwitches.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.eagermethodloading", cEnableEagerMethodLoading.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.simplifymember", cSimplifyMemberReferences.isSelected());
        jEdit.setBooleanProperty("javainsight.procyon.disableforeach", cDisableForEachTransforms.isSelected());
        
    }

    //Jode Decompiler options
    @SuppressWarnings("rawtypes")
	private JComboBox cStyle;
    private JCheckBox cPretty;
    private JCheckBox cOnetime;
    private JCheckBox cDecrypt;
    private JTextField cImportPkgLimit;
    private JTextField cImportClassLimit;

    //Procyon Decompiler options
    private JCheckBox cMergeVariables;
    private JCheckBox cExplicitImports;
    private JCheckBox cForceExplicitTypes;
    private JCheckBox cRetainRedundantCasts;
    private JCheckBox cFlattenSwitchBlocks;
    private JCheckBox cShowSynthenticMembers;
    private JCheckBox cUnoptomisedAstByteCode;
    private JCheckBox cExcludeNestedTypes;
    private JCheckBox cRetainPointlessSwitches;
    private JCheckBox cEnableEagerMethodLoading;
    private JCheckBox cSimplifyMemberReferences;
    private JCheckBox cDisableForEachTransforms;

}
