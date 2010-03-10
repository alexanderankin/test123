package beauty;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;

import ise.java.awt.*;


/**
 * An option pane to configure the mode to beautifier associations.
 * TODO: put strings in properties file
 *
 */
public class CustomBeautifierOptionPane extends AbstractOptionPane {

    private JCheckBox usejEditIndenter;
    private JComboBox modeSelector;
    private JCheckBox prePadFunctions;
    private JCheckBox prePadDigits;
    private JCheckBox prePadOperators;
    private JCheckBox postPadFunctions;
    private JCheckBox postPadDigits;
    private JCheckBox postPadOperators;
    private JCheckBox labelOnSeparateLine;
    private JCheckBox prePadKeywords1;
    private JCheckBox postPadKeywords1;
    private JCheckBox prePadKeywords2;
    private JCheckBox postPadKeywords2;
    private JCheckBox prePadKeywords3;
    private JCheckBox postPadKeywords3;
    private JCheckBox prePadKeywords4;
    private JCheckBox postPadKeywords4;

    private Properties modeProperties;

    private Mode currentMode = null;

    public CustomBeautifierOptionPane() {
        super( "beauty.custom" );
    }

    // _init() method
    public void _init() {
        loadProperties();
        installComponents();
        installListeners();

    }

    private void loadProperties() {
        modeProperties = BeautyPlugin.getProperties();
    }

    private void installComponents() {
        setLayout( new KappaLayout() );
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );

        JLabel description = new JLabel( "<html><b>Create a custom beautifier for a mode" );

        Mode[] modes = jEdit.getModes();
        modeSelector = new JComboBox( modes );
        currentMode = modes[0];

        usejEditIndenter = new JCheckBox( "Use jEdit indenter for this mode" );
        usejEditIndenter.setSelected( true );

        prePadFunctions = new JCheckBox( "before" );
        prePadDigits = new JCheckBox( "before" );
        prePadOperators = new JCheckBox( "before" );
        prePadKeywords1 = new JCheckBox( "before" );
        prePadKeywords2 = new JCheckBox( "before" );
        prePadKeywords3 = new JCheckBox( "before" );
        prePadKeywords4 = new JCheckBox( "before" );
        postPadFunctions = new JCheckBox( "after" );
        postPadDigits = new JCheckBox( "after" );
        postPadOperators = new JCheckBox( "after" );
        postPadKeywords1 = new JCheckBox( "after" );
        postPadKeywords2 = new JCheckBox( "after" );
        postPadKeywords3 = new JCheckBox( "after" );
        postPadKeywords4 = new JCheckBox( "after" );

        labelOnSeparateLine = new JCheckBox( "Label on separate line" );

        add( description, "0, 0, R, 1, W, w, 3" );

        add( KappaLayout.createVerticalStrut( 11 ), "0, 1" );

        add( new JLabel( "Mode: " ), "0, 2, 1, 1, W, w, 3" );
        add( modeSelector, "1, 2, R, 1, W, w, 3" );

        add( usejEditIndenter, "0, 3, R, 1, W, w, 3" );

        add( new JLabel( "Pad functions" ), "0, 4, 1, 1, W, w, 3" );
        add( prePadFunctions, "1, 4, 1, 1, W, 0, 3" );
        add( postPadFunctions, "2, 4, 1, 1, W, 0, 3" );

        add( new JLabel( "Pad operators" ), "0, 5, 1, 1, W, w, 3" );
        add( prePadOperators, "1, 5, 1, 1, W, 0, 3" );
        add( postPadOperators, "2, 5, 1, 1, W, 0, 3" );

        add( new JLabel( "Pad digits" ), "0, 6, 1, 1, W, w, 3" );
        add( prePadDigits, "1, 6, 1, 1, W, 0, 3" );
        add( postPadDigits, "2, 6, 1, 1, W, 0, 3" );

        add( new JLabel( "Pad keywords1" ), "0, 7, 1, 1, W, w, 3" );
        add( prePadKeywords1, "1, 7, 1, 1, W, 0, 3" );
        add( postPadKeywords1, "2, 7, 1, 1, W, 0, 3" );

        add( new JLabel( "Pad keywords2" ), "0, 8, 1, 1, W, w, 3" );
        add( prePadKeywords2, "1, 8, 1, 1, W, 0, 3" );
        add( postPadKeywords2, "2, 8, 1, 1, W, 0, 3" );

        add( new JLabel( "Pad keywords3" ), "0, 9, 1, 1, W, w, 3" );
        add( prePadKeywords3, "1, 9, 1, 1, W, 0, 3" );
        add( postPadKeywords3, "2, 9, 1, 1, W, 0, 3" );

        add( new JLabel( "Pad keywords4" ), "0, 10, 1, 1, W, w, 3" );
        add( prePadKeywords4, "1, 10, 1, 1, W, 0, 3" );
        add( postPadKeywords4, "2, 10, 1, 1, W, 0, 3" );

        add( labelOnSeparateLine, "0, 11, R, 1, W, w, 3" );
    }

    private void installListeners() {
        modeSelector.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    Mode selectedMode = ( Mode ) modeSelector.getSelectedItem();
                    if ( selectedMode.equals( currentMode ) ) {
                        return ;
                    }
                    currentMode = selectedMode;

                    // uncheck all checkboxes
                    usejEditIndenter.setSelected( false );
                    prePadFunctions.setSelected( false );
                    prePadDigits.setSelected( false );
                    prePadOperators.setSelected( false );
                    prePadKeywords1.setSelected( false );
                    prePadKeywords2.setSelected( false );
                    prePadKeywords3.setSelected( false );
                    prePadKeywords4.setSelected( false );
                    postPadFunctions.setSelected( false );
                    postPadDigits.setSelected( false );
                    postPadOperators.setSelected( false );
                    postPadKeywords1.setSelected( false );
                    postPadKeywords2.setSelected( false );
                    postPadKeywords3.setSelected( false );
                    postPadKeywords4.setSelected( false );
                    labelOnSeparateLine.setSelected( false );

                    // check the checkboxes based on the stored properties
                    String name = selectedMode.getName();
                    for ( Object k : modeProperties.keySet() ) {
                        String key = ( String ) k;
                        boolean value = "false".equals( modeProperties.getProperty( key ) ) ? false : true;
                        if ( key.startsWith( name ) ) {
                            key = key.substring( key.indexOf( '.' ) + 1 );
                            if ( "usejEditIndenter".equals( key ) ) {
                                usejEditIndenter.setSelected( value );
                            }
                            else if ( "prePadFunctions".equals( key ) ) {
                                prePadFunctions.setSelected( value );
                            }
                            else if ( "postPadFunctions".equals( key ) ) {
                                postPadFunctions.setSelected( value );
                            }
                            else if ( "prePadDigits".equals( key ) ) {
                                prePadDigits.setSelected( value );
                            }
                            else if ( "postPadDigits".equals( key ) ) {
                                postPadDigits.setSelected( value );
                            }
                            else if ( "prePadOperators".equals( key ) ) {
                                prePadOperators.setSelected( value );
                            }
                            else if ( "postPadOperators".equals( key ) ) {
                                postPadOperators.setSelected( value );
                            }
                            else if ( "prePadKeywords1".equals( key ) ) {
                                prePadKeywords1.setSelected( value );
                            }
                            else if ( "prePadKeywords2".equals( key ) ) {
                                prePadKeywords2.setSelected( value );
                            }
                            else if ( "prePadKeywords3".equals( key ) ) {
                                prePadKeywords3.setSelected( value );
                            }
                            else if ( "prePadKeywords4".equals( key ) ) {
                                prePadKeywords4.setSelected( value );
                            }
                            else if ( "postPadKeywords1".equals( key ) ) {
                                postPadKeywords1.setSelected( value );
                            }
                            else if ( "postPadKeywords2".equals( key ) ) {
                                postPadKeywords2.setSelected( value );
                            }
                            else if ( "postPadKeywords3".equals( key ) ) {
                                postPadKeywords3.setSelected( value );
                            }
                            else if ( "postPadKeywords4".equals( key ) ) {
                                postPadKeywords4.setSelected( value );
                            }
                            else if ( "labelOnSeparateLine".equals( key ) ) {
                                labelOnSeparateLine.setSelected( value );
                            }
                        }
                    }

                }
            }
        );
        usejEditIndenter.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".usejEditIndenter", usejEditIndenter.isSelected() ? "true" : "false" );
                }
            }
        );
        prePadFunctions.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".prePadFunctions", prePadFunctions.isSelected() ? "true" : "false" );
                }
            }
        );
        prePadDigits.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".prePadDigits", prePadDigits.isSelected() ? "true" : "false" );
                }
            }
        );
        prePadOperators.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".prePadOperators", prePadOperators.isSelected() ? "true" : "false" );
                }
            }
        );
        prePadKeywords1.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".prePadKeywords1", prePadKeywords1.isSelected() ? "true" : "false" );
                }
            }
        );
        prePadKeywords2.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".prePadKeywords2", prePadKeywords2.isSelected() ? "true" : "false" );
                }
            }
        );
        prePadKeywords3.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".prePadKeywords3", prePadKeywords3.isSelected() ? "true" : "false" );
                }
            }
        );
        prePadKeywords4.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".prePadKeywords4", prePadKeywords4.isSelected() ? "true" : "false" );
                }
            }
        );
        postPadFunctions.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".postPadFunctions", postPadFunctions.isSelected() ? "true" : "false" );
                }
            }
        );
        postPadDigits.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".postPadDigits", postPadDigits.isSelected() ? "true" : "false" );
                }
            }
        );
        postPadOperators.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".postPadOperators", postPadOperators.isSelected() ? "true" : "false" );
                }
            }
        );
        postPadKeywords1.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".postPadKeywords1", postPadKeywords1.isSelected() ? "true" : "false" );
                }
            }
        );
        postPadKeywords2.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".postPadKeywords2", postPadKeywords2.isSelected() ? "true" : "false" );
                }
            }
        );
        postPadKeywords3.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".postPadKeywords3", postPadKeywords3.isSelected() ? "true" : "false" );
                }
            }
        );
        postPadKeywords4.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".postPadKeywords4", postPadKeywords4.isSelected() ? "true" : "false" );
                }
            }
        );
        labelOnSeparateLine.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String name = currentMode.getName();
                    modeProperties.setProperty( name + ".labelOnSeparateLine", labelOnSeparateLine.isSelected() ? "true" : "false" );
                }
            }
        );
    }

    // _save() method
    public void _save() {
        BeautyPlugin.saveProperties();
        BeautyPlugin.registerServices();
    }
}