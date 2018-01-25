/*
* JDiffOptionPane.java
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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.jEdit;

// from CommonControls plugin
import ise.java.awt.KappaLayout;

/**
 * Handles the general options for JDiff -- diff whitespace options, restoring
 * the View, beeping on error, etc.
 */
public class JDiffOptionPane implements OptionPane {

    private JPanel panel = null;

    // diff options
    private JCheckBox ignoreCase;
    private JCheckBox trimWhitespace;
    private JCheckBox ignoreAmountOfWhitespace;
    private JCheckBox ignoreLineSeparators;
    private JCheckBox ignoreAllWhitespace;
    private JCheckBox heuristic;
    private JCheckBox no_discards;

    /**
     * @return the name of this panel
     */
    public String getName() {
        return "jdiff.general";
    }

    /**
     * @return the panel to display the general options for JDiff
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
        ignoreCase.setSelected( jEdit.getBooleanProperty( "jdiff.ignore-case", false ) );
        trimWhitespace.setSelected( jEdit.getBooleanProperty( "jdiff.trim-whitespace", false ) );
        ignoreAmountOfWhitespace.setSelected( jEdit.getBooleanProperty( "jdiff.ignore-amount-whitespace", false ) );
        ignoreLineSeparators.setSelected( jEdit.getBooleanProperty( "jdiff.ignore-line-separators", true ) );
        ignoreAllWhitespace.setSelected( jEdit.getBooleanProperty( "jdiff.ignore-all-whitespace", false ) );
        heuristic.setSelected( jEdit.getBooleanProperty( "jdiff.heuristic", false ) );
        no_discards.setSelected( jEdit.getBooleanProperty( "jdiff.no_discards", false ) );
    }

    // actually create and layout the option panel here
    private void createPanel() {
        // diff options
        JLabel diff_options_label = new JLabel( "<html><b>" + jEdit.getProperty( "options.diff-options.label", "Diff Options:" ) + "</b>");
        ignoreCase = createCheckBox( "jdiff.ignore-case", false );
        trimWhitespace = createCheckBox( "jdiff.trim-whitespace", false );
        ignoreAmountOfWhitespace = createCheckBox( "jdiff.ignore-amount-whitespace", false );
        ignoreLineSeparators = createCheckBox( "jdiff.ignore-line-separators", false );
        ignoreAllWhitespace = createCheckBox( "jdiff.ignore-all-whitespace", false );
        heuristic = createCheckBox( "jdiff.heuristic", false );
        no_discards = createCheckBox( "jdiff.no_discards", false );

        panel = new JPanel();
        panel.setLayout( new KappaLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 12, 11, 11, 12 ) );
        panel.add( "0,  0, 2, 1, W, 0, 2", diff_options_label );
        panel.add( "0,  1, 2, 1, W, 0, 2", ignoreCase );
        panel.add( "0,  2, 2, 1, W, 0, 2", trimWhitespace );
        panel.add( "0,  3, 2, 1, W, 0, 2", ignoreAmountOfWhitespace );
        panel.add( "0,  4, 2, 1, W, 0, 2", ignoreLineSeparators );
        panel.add( "0,  5, 2, 1, W, 0, 2", ignoreAllWhitespace );
        panel.add( "0,  6, 2, 1, W, 0, 2", heuristic );
        panel.add( "0,  7, 2, 1, W, 0, 2", no_discards );
    }

    /**
     * Save the user settings for the general options.
     */
    public void save() {
        jEdit.setBooleanProperty( "jdiff.ignore-case", ignoreCase.isSelected() );
        jEdit.setBooleanProperty( "jdiff.trim-whitespace", trimWhitespace.isSelected() );
        jEdit.setBooleanProperty( "jdiff.ignore-amount-whitespace", ignoreAmountOfWhitespace.isSelected() );
        jEdit.setBooleanProperty( "jdiff.ignore-line-separators", ignoreLineSeparators.isSelected() );
        jEdit.setBooleanProperty( "jdiff.ignore-all-whitespace", ignoreAllWhitespace.isSelected() );
        jEdit.setBooleanProperty( "jdiff.heuristic", heuristic.isSelected() );
        jEdit.setBooleanProperty( "jdiff.no_discards", no_discards.isSelected() );

        // virtual overview has been removed, since it hasn't worked since jEdit 4.2,
        // so make sure the property is false
        jEdit.setBooleanProperty( "jdiff.global-virtual-overview", false );
    }


    private JCheckBox createCheckBox( String property, boolean defaultValue ) {
        JCheckBox cb = new JCheckBox( jEdit.getProperty( "options." + property ) );
        cb.setSelected( jEdit.getBooleanProperty( property, defaultValue ) );
        return cb;
    }
}