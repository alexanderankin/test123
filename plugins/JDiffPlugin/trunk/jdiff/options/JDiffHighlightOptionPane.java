/*
* JDiffHighlightOptionPane.java
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

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.gui.ColorWellButton;
import org.gjt.sp.jedit.jEdit;

// from CommonControls
import ise.java.awt.KappaLayout;

/**
 * @deprecated The functionality of this class has been combined with
 * JDiffOverviewOptionPane so colors are managed in one place.
 */
@Deprecated
public class JDiffHighlightOptionPane implements OptionPane {

    private JPanel panel = null;

    private ColorWellButton highlightChangedLineColor;
    private ColorWellButton highlightDeletedLineColor;
    private ColorWellButton highlightInsertedLineColor;
    private ColorWellButton highlightInvalidLineColor;

    /**
     * @return the name of this panel
     */
    public String getName() {
        return jEdit.getProperty( "jdiff.highlight" );
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
        highlightChangedLineColor.setSelectedColor( jEdit.getColorProperty( "jdiff.highlight-changed-color" ) );
        highlightDeletedLineColor.setSelectedColor( jEdit.getColorProperty( "jdiff.highlight-deleted-color" ) );
        highlightInsertedLineColor.setSelectedColor( jEdit.getColorProperty( "jdiff.highlight-inserted-color" ) );
        highlightInvalidLineColor.setSelectedColor( jEdit.getColorProperty( "jdiff.highlight-invalid-color" ) );
    }

    // actually create and layout the option panel here
    private void createPanel() {
        highlightChangedLineColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.highlight-changed-color" ) );
        highlightDeletedLineColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.highlight-deleted-color" ) );
        highlightInsertedLineColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.highlight-inserted-color" ) );
        highlightInvalidLineColor = new ColorWellButton( jEdit.getColorProperty( "jdiff.highlight-invalid-color" ) );

        // Highlight colors
        panel = new JPanel();
        panel.setLayout( new KappaLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 12, 11, 11, 12 ) );

        panel.add( "0, 0, 2, 1, W, w, 3", createLabel( "options.jdiff.highlight" ) );

        panel.add( "0, 1, 1, 1, W,  , 3", createLabel( "options.jdiff.overview-changed-color" ) );
        panel.add( "1, 1, 1, 1, 0, w, 3", highlightChangedLineColor );

        panel.add( "0, 2, 1, 1, W,  , 3", createLabel( "options.jdiff.highlight-deleted-color" ) );
        panel.add( "1, 2, 1, 1, 0, w, 3", highlightDeletedLineColor );

        panel.add( "0, 3, 1, 1, W,  , 3", createLabel( "options.jdiff.highlight-inserted-color" ) );
        panel.add( "1, 3, 1, 1, 0, w, 3", highlightInsertedLineColor );

        panel.add( "0, 4, 1, 1, W,  , 3", createLabel( "options.jdiff.highlight-invalid-color" ) );
        panel.add( "1, 4, 1, 1, 0, w, 3", highlightInvalidLineColor );
    }


    public void save() {
        jEdit.setColorProperty( "jdiff.highlight-changed-color",
                this.highlightChangedLineColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.highlight-deleted-color",
                this.highlightDeletedLineColor.getSelectedColor()
                              );
        jEdit.setColorProperty( "jdiff.highlight-inserted-color",
                this.highlightInsertedLineColor.getSelectedColor()
                              );

        jEdit.setColorProperty( "jdiff.highlight-invalid-color",
                this.highlightInvalidLineColor.getSelectedColor()
                              );
    }


    private JLabel createLabel( String property ) {
        return new JLabel( jEdit.getProperty( property ) );
    }

}