/*
 * JIndexOptionPane.java - JIndex options panel
 * Copyright (C) 1999 Dirk Moebius
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

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import org.gjt.sp.jedit.*;


public class InfoViewerOptionPane 
        extends AbstractOptionPane implements ActionListener {

    // private members
    private JRadioButton  rbInternal;
    private JRadioButton  rbOther;
    private JRadioButton  rbClass;
    private JRadioButton  rbNetscape;
    private JTextField    tBrowser;
    private JTextField    tClass;
    private JTextField    tMethod;
    private JTextField    tHome;

    public InfoViewerOptionPane() {
        super("infoviewer");
		setBorder(new EmptyBorder(5,5,5,5));

        Dimension space = new Dimension(0, 30);

        // create the dialog:

        // create the dialog: 1. select browser
        
        addComponent(new JLabel(jEdit.getProperty(
            "options.infoviewer.browser.label")));
        addComponent(rbInternal = new JRadioButton(jEdit.getProperty(
            "options.infoviewer.browser.internal")));            
        addComponent(rbClass = new JRadioButton(jEdit.getProperty(
            "options.infoviewer.browser.class")));
        addComponent(rbNetscape = new JRadioButton(jEdit.getProperty(
            "options.infoviewer.browser.netscape")));
        addComponent(rbOther = new JRadioButton(jEdit.getProperty(
            "options.infoviewer.browser.other")));

        ButtonGroup browserGroup = new ButtonGroup();
        browserGroup.add(rbInternal);
        browserGroup.add(rbClass);
        browserGroup.add(rbNetscape);
        browserGroup.add(rbOther);
        
        rbInternal.addActionListener(this);
        rbClass.addActionListener(this);
        rbNetscape.addActionListener(this);
        rbOther.addActionListener(this);        
        
        // create the dialog: 2. configuring browser
        
        addComponent(new Box.Filler(space, space, space));
        
        addComponent(new JLabel(jEdit.getProperty(
            "options.infoviewer.browser.settings.label")));
        
        addComponent(
            jEdit.getProperty("options.infoviewer.browser.class.label"),
            tClass = new JTextField(jEdit.getProperty(
                "options.infoviewer.browser.class.default"), 15)
        );
        addComponent(
            jEdit.getProperty("options.infoviewer.browser.method.label"),
            tMethod = new JTextField(jEdit.getProperty(
                "options.infoviewer.browser.method.default"), 15)
        );
        addComponent(
            jEdit.getProperty("options.infoviewer.browser.other.label"),
            tBrowser = new JTextField(jEdit.getProperty(
                "options.infoviewer.browser.other.default"), 15)
        );

        // create the dialog: 3. misc settings
        
        addComponent(new Box.Filler(space, space, space));
        
        addComponent(new JLabel(jEdit.getProperty(
            "options.infoviewer.misc.label")));
        
        String homepage = jEdit.getProperty("infoviewer.homepage");
        if (homepage == null) {
            String jEditHome = MiscUtilities.constructPath(
                                   jEdit.getJEditHome(), "doc");
            jEditHome = jEditHome.replace(File.separatorChar, '/');
            homepage = "file:" + jEditHome + "/jeditdocs/index.html";
            jEdit.setProperty("infoviewer.homepage", homepage);
        }
        
        addComponent(jEdit.getProperty("options.infoviewer.misc.homepage"),
            tHome = new JTextField(homepage, 15));
            
        // configure the dialog:
        
        String classname = jEdit.getProperty("infoviewer.class");
        if (classname != null && classname.length() > 0) {
            tClass.setText(classname);
        }

        String methodname = jEdit.getProperty("infoviewer.method");
        if (methodname != null && methodname.length() > 0) {
            tMethod.setText(methodname);
        }

        String otherBrowser = jEdit.getProperty("infoviewer.otherBrowser");
        if (otherBrowser != null && otherBrowser.length() > 0) {
            tBrowser.setText(otherBrowser);
        }

        String intBrowser = jEdit.getProperty("infoviewer.browsertype");
        if ("netscape".equals(intBrowser))
            rbNetscape.setSelected(true);
        else if ("class".equals(intBrowser))
            rbClass.setSelected(true);
        else if ("external".equals(intBrowser))
            rbOther.setSelected(true);
        else
            rbInternal.setSelected(true);
            
        enableItems();
    }


    /**
     * Called when the options dialog's `OK' button is pressed.
     * This should save any properties saved in this option pane.
     */
    public void save() {
        jEdit.setProperty("infoviewer.browsertype",
            rbInternal.isSelected() ? "internal" : 
            rbClass.isSelected() ? "class" : 
            rbNetscape.isSelected() ? "netscape" : "external");
            
        jEdit.setProperty("infoviewer.otherBrowser", tBrowser.getText());            
        jEdit.setProperty("infoviewer.class", tClass.getText());
        jEdit.setProperty("infoviewer.method", tMethod.getText());
        jEdit.setProperty("infoviewer.homepage", tHome.getText());
        
         // only used in old version 0.1:
        jEdit.unsetProperty("infoviewer.internalBrowser");
    }
    
    
    /**
     * called when one of the radio buttons is clicked
     */
    public void actionPerformed(ActionEvent e) {
        enableItems();
    }
    
    
    private void enableItems() {
        tClass.setEnabled(rbClass.isSelected());
        tMethod.setEnabled(rbClass.isSelected());
        tBrowser.setEnabled(rbOther.isSelected());
    }
}
