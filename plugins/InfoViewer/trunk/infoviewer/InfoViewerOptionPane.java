/*
 * InfoViewerOptionPane.java - InfoViewer options panel
 * Copyright (C) 1999-2001 Dirk Moebius
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

package infoviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;


public class InfoViewerOptionPane extends AbstractOptionPane implements ActionListener {

    public InfoViewerOptionPane() {
        super("infoviewer");
    }


    public void _init() {
        addSeparator("options.infoviewer.browser.label");

        rbInternal = new JRadioButton(jEdit.getProperty("options.infoviewer.browser.internal"));
        rbInternal.addActionListener(this);
        addComponent(rbInternal);

        rbClass = new JRadioButton(jEdit.getProperty("options.infoviewer.browser.class"));
        rbClass.addActionListener(this);
        addComponent(rbClass);

        rbNetscape = new JRadioButton(jEdit.getProperty("options.infoviewer.browser.netscape"));
        rbNetscape.addActionListener(this);
        addComponent(rbNetscape);

        rbOther = new JRadioButton(jEdit.getProperty("options.infoviewer.browser.other"));
        rbOther.addActionListener(this);
        addComponent(rbOther);

        ButtonGroup browserGroup = new ButtonGroup();
        browserGroup.add(rbInternal);
        browserGroup.add(rbClass);
        browserGroup.add(rbNetscape);
        browserGroup.add(rbOther);

        String browserType = jEdit.getProperty("infoviewer.browsertype");
        if ("netscape".equals(browserType))
            rbNetscape.setSelected(true);
        else if ("class".equals(browserType))
            rbClass.setSelected(true);
        else if ("external".equals(browserType))
            rbOther.setSelected(true);
        else
            rbInternal.setSelected(true);

        // "Browser settings:"
        addComponent(Box.createVerticalStrut(20));
        addSeparator("options.infoviewer.browser.settings.label");

        // "Class:"
        String classname = jEdit.getProperty("infoviewer.class");
        if (classname == null)
            classname = jEdit.getProperty("options.infoviewer.browser.class.default");
        tClass = new JTextField(classname, 15);
        addComponent(jEdit.getProperty("options.infoviewer.browser.class.label"), tClass);

        // "Method:"
        String methodname = jEdit.getProperty("infoviewer.method");
        if (methodname == null)
            methodname = jEdit.getProperty("options.infoviewer.browser.method.default");
        tMethod = new JTextField(methodname, 15);
        addComponent(jEdit.getProperty("options.infoviewer.browser.method.label"), tMethod);

        // "External browser command:"
        String otherBrowser = jEdit.getProperty("infoviewer.otherBrowser");
        if (otherBrowser == null)
            otherBrowser = jEdit.getProperty("options.infoviewer.browser.other.default");
        tBrowser = new JTextField(otherBrowser, 15);
        addComponent(jEdit.getProperty("options.infoviewer.browser.other.label"), tBrowser);

        // "Homepage:"
        String homepage = jEdit.getProperty("infoviewer.homepage");
        if (homepage == null) {
            String jEditHome = MiscUtilities.constructPath(jEdit.getJEditHome(), "doc");
            jEditHome = jEditHome.replace(File.separatorChar, '/');
            homepage = "file:" + jEditHome + "/jeditdocs/index.html";
            jEdit.setProperty("infoviewer.homepage", homepage);
        }
        tHome = new JTextField(homepage, 15);
        addComponent(jEdit.getProperty("options.infoviewer.homepage"), tHome);

        // "Max. number of menu entries in "Go" menu:"
        String max_go_menu = jEdit.getProperty("infoviewer.max_go_menu");
        if (max_go_menu == null) {
            max_go_menu = "20";
            jEdit.setProperty("infoviewer.max_go_menu", max_go_menu);
        }
        tMaxGoMenu = new JTextField(max_go_menu, 15);
        addComponent(jEdit.getProperty("options.infoviewer.max_go_menu"), tMaxGoMenu);

        // init:
        actionPerformed(null);
    }


    /**
     * Called when the options dialog's `OK' button is pressed.
     * This saves any properties saved in this option pane.
     */
    public void _save() {
        jEdit.setProperty("infoviewer.browsertype",
            rbInternal.isSelected() ? "internal" :
            rbClass.isSelected() ? "class" :
            rbNetscape.isSelected() ? "netscape" :
            "external");

        jEdit.setProperty("infoviewer.otherBrowser", tBrowser.getText());
        jEdit.setProperty("infoviewer.class", tClass.getText());
        jEdit.setProperty("infoviewer.method", tMethod.getText());
        jEdit.setProperty("infoviewer.homepage", tHome.getText());
        jEdit.setProperty("infoviewer.max_go_menu", tMaxGoMenu.getText());
    }


    /**
     * Called when one of the radio buttons is clicked.
     */
    public void actionPerformed(ActionEvent e) {
        tClass.setEnabled(rbClass.isSelected());
        tMethod.setEnabled(rbClass.isSelected());
        tBrowser.setEnabled(rbOther.isSelected());
        tHome.setEnabled(rbInternal.isSelected());
        tMaxGoMenu.setEnabled(rbInternal.isSelected());
    }


    private JRadioButton rbInternal;
    private JRadioButton rbOther;
    private JRadioButton rbClass;
    private JRadioButton rbNetscape;
    private JTextField tBrowser;
    private JTextField tClass;
    private JTextField tMethod;
    private JTextField tHome;
    private JTextField tMaxGoMenu;

}
