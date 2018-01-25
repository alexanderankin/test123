/*
* JDiffOverviewOptionPane.java
* Copyright (c) 2000, 2001, 2002 Andre Kaplan
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


package jdiff.options;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.gui.ColorWellButton;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.OptionPane;

// from CommonControls plugin
import ise.java.awt.KappaLayout;

/**
 * Handles color options for overviews, text area highlighting, and hunk cursors.
 */
public class JDiffOverviewOptionPane implements OptionPane {

    private JPanel panel = null;

    private ColorWellButton overviewChangedLineColor;
    private ColorWellButton overviewDeletedLineColor;
    private ColorWellButton overviewInsertedLineColor;
    private ColorWellButton overviewInvalidLineColor;

    private ColorWellButton leftCursorColor;
    private ColorWellButton rightCursorColor;


    /**
     * @return the name of this panel
     */
    public String getName() {
    	    return "jdiff.overview";
    }

    /**
     * @return the panel to display the options for JDiff
     */
    public Component getComponent() {
        if ( panel == null ) {
            createPanel();
        }
        return panel;
    }

    /**
     * Initialize the panel, create the components and apply current settings.
     */
    public void init() {
        if ( panel == null ) {
            createPanel();
        }
        overviewChangedLineColor.setSelectedColor( jEdit.getColorProperty( "jdiff.overview-changed-color" ) );
        overviewDeletedLineColor.setSelectedColor( jEdit.getColorProperty( "jdiff.overview-deleted-color" ) );
        overviewInsertedLineColor.setSelectedColor( jEdit.getColorProperty( "jdiff.overview-inserted-color" ) );
        overviewInvalidLineColor.setSelectedColor( jEdit.getColorProperty( "jdiff.overview-invalid-color" ) );
        leftCursorColor.setSelectedColor( jEdit.getColorProperty( "jdiff.left-cursor-color" ) );
        rightCursorColor.setSelectedColor( jEdit.getColorProperty( "jdiff.right-cursor-color" ) );
    }

    // actually create and layout the option panel here
    private void createPanel() {
        overviewChangedLineColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.overview-changed-color" ) );
        overviewDeletedLineColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.overview-deleted-color" ) );
        overviewInsertedLineColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.overview-inserted-color" ) );
        overviewInvalidLineColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.overview-invalid-color" ) );

        leftCursorColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.left-cursor-color" ) );
        rightCursorColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.right-cursor-color" ) );

        // Overview colors
        panel = new JPanel();
        panel.setLayout( new KappaLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 12, 11, 11, 12 ) );

        JLabel title = new JLabel( "<html><b>" + jEdit.getProperty( "options.jdiff.overview" ) + "</b>" );
        panel.add( "0, 0, 2, 1, W, w, 3", title );

        panel.add( "0, 1, 1, 1, W,  , 3", createLabel( "options.jdiff.overview-changed-color" ) );
        panel.add( "1, 1, 1, 1, 0, w, 3", overviewChangedLineColor );

        panel.add( "0, 2, 1, 1, W,  , 3", createLabel( "options.jdiff.overview-deleted-color" ) );
        panel.add( "1, 2, 1, 1, 0, w, 3", overviewDeletedLineColor );

        panel.add( "0, 3, 1, 1, W,  , 3", createLabel( "options.jdiff.overview-inserted-color" ) );
        panel.add( "1, 3, 1, 1, 0, w, 3", overviewInsertedLineColor );

        panel.add( "0, 4, 1, 1, W,  , 3", createLabel( "options.jdiff.overview-invalid-color" ) );
        panel.add( "1, 4, 1, 1, 0, w, 3", overviewInvalidLineColor );

        panel.add( "0, 5, 1, 1, W,  , 3", createLabel( "options.jdiff.left-cursor-color" ) );
        panel.add( "1, 5, 1, 1, 0, w, 3", leftCursorColor );

        panel.add( "0, 6, 1, 1, W,  , 3", createLabel( "options.jdiff.right-cursor-color" ) );
        panel.add( "1, 6, 1, 1, 0, w, 3", rightCursorColor );
    }


    /**
     * Save the user settings for the options.
     */
    public void save() {
        jEdit.setColorProperty( "jdiff.overview-changed-color",
                overviewChangedLineColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.overview-deleted-color",
                overviewDeletedLineColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.overview-inserted-color",
                overviewInsertedLineColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.overview-invalid-color",
                overviewInvalidLineColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.left-cursor-color",
                leftCursorColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.right-cursor-color",
                rightCursorColor.getSelectedColor()
                              );

        // set line highlight colors to be the same as the overview colors
        jEdit.setColorProperty( "jdiff.highlight-changed-color",
                overviewChangedLineColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.highlight-deleted-color",
                overviewDeletedLineColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.highlight-inserted-color",
                overviewInsertedLineColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.highlight-invalid-color",
                overviewInvalidLineColor.getSelectedColor()
                              );
    }


    private JLabel createLabel( String property ) {
        return new JLabel( jEdit.getProperty( property ) );
    }

}