package beauty;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import ise.java.awt.*;


/**
 * An option pane to configure custom beautification rules for a mode.
 *
 */
public class CustomBeautifierOptionPane extends AbstractOptionPane {

    // the user can elect to use the jEdit indenter
    private JCheckBox usejEditIndenter;

    // selector to spefify which mode this beautifier is associated with
    private JComboBox modeSelector;

    // check boxes for padding based on jEdit text area tokenization.  There
    // are other tokens, but these are the ones that users might want to
    // apply padding to.
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

    // the user can specify a list of characters to be padded, one list is to
    // pad before, the other is to pad after
    private JTextField prePadCharacters;
    private JTextField postPadCharacters;
    private JTextField dontPrePadCharacters;
    private JTextField dontPostPadCharacters;

    // the user can specify a list of characters after which a line
    // separator will be inserted
    private JTextField preInsertLineCharacters;
    private JTextField postInsertLineCharacters;

    // multiple blank lines can be collapsed to a single blank line
    private JCheckBox collapseBlankLines;

    // a reference to the properties maintained by BeautyPlugin
    private Properties modeProperties;

    // the currently selected mode from the modeSelector combo box
    private Mode currentMode = null;


    public CustomBeautifierOptionPane() {
        super( "beauty.custom" );
    }

    // called when this class is first accessed
    public void _init() {
        currentMode = jEdit.getActiveView().getBuffer().getMode();
        loadProperties( currentMode.getName() );
        installComponents();
        installListeners();
    }

    // fetch a reference to the mode properties maintained by the BeautyPlugin
    private void loadProperties( String modeName ) {
        modeProperties = BeautyPlugin.getCustomModeProperties( modeName );
    }

    // create the user interface components and do the layout
    private void installComponents() {
        setLayout( new KappaLayout() );
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );

        // create the components
        JLabel description = new JLabel( jEdit.getProperty("beauty.msg.<html><b>Create_a_custom_beautifier_for_a_mode", "<html><b>Create a custom beautifier for a mode") );

        Mode[] modes = jEdit.getModes();
        modeSelector = new JComboBox( modes );
        modeSelector.setSelectedItem( currentMode );

        usejEditIndenter = new JCheckBox( jEdit.getProperty("beauty.msg.Use_jEdit_indenter_for_this_mode", "Use jEdit indenter for this mode") );

        prePadFunctions = new JCheckBox( jEdit.getProperty("beauty.msg.before", "before") );
        prePadDigits = new JCheckBox( jEdit.getProperty("beauty.msg.before", "before") );
        prePadOperators = new JCheckBox( jEdit.getProperty("beauty.msg.before", "before") );
        prePadKeywords1 = new JCheckBox( jEdit.getProperty("beauty.msg.before", "before") );
        prePadKeywords2 = new JCheckBox( jEdit.getProperty("beauty.msg.before", "before") );
        prePadKeywords3 = new JCheckBox( jEdit.getProperty("beauty.msg.before", "before") );
        prePadKeywords4 = new JCheckBox( jEdit.getProperty("beauty.msg.before", "before") );
        postPadFunctions = new JCheckBox( jEdit.getProperty("beauty.msg.after", "after") );
        postPadDigits = new JCheckBox( jEdit.getProperty("beauty.msg.after", "after") );
        postPadOperators = new JCheckBox( jEdit.getProperty("beauty.msg.after", "after") );
        postPadKeywords1 = new JCheckBox( jEdit.getProperty("beauty.msg.after", "after") );
        postPadKeywords2 = new JCheckBox( jEdit.getProperty("beauty.msg.after", "after") );
        postPadKeywords3 = new JCheckBox( jEdit.getProperty("beauty.msg.after", "after") );
        postPadKeywords4 = new JCheckBox( jEdit.getProperty("beauty.msg.after", "after") );
        
        
        labelOnSeparateLine = new JCheckBox( jEdit.getProperty("beauty.msg.Label_on_separate_line", "Label on separate line") );

        prePadCharacters = new JTextField();
        postPadCharacters = new JTextField();
        dontPrePadCharacters = new JTextField();
        dontPostPadCharacters = new JTextField();
        preInsertLineCharacters = new JTextField();
        postInsertLineCharacters = new JTextField();

        collapseBlankLines = new JCheckBox( jEdit.getProperty("beauty.msg.Collapse_multiple_blank_lines", "Collapse multiple blank lines") );
        
        // set the values for the components
        setComponentValues();

        // layout the components
        add( description, "0, 0, R, 1, W, w, 2" );

        add( KappaLayout.createVerticalStrut( 6 ), "0, 1" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Mode>_", "Mode: ") ), "0, 2, 1, 1, W, w, 2" );
        add( modeSelector, "1, 2, R, 1, W, w, 2" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Pad_functions", "Pad functions") ), "0, 4, 1, 1, W, w, 2" );
        add( prePadFunctions, "1, 4, 1, 1, W, 0, 2" );
        add( postPadFunctions, "2, 4, 1, 1, W, 0, 2" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Pad_operators", "Pad operators") ), "0, 5, 1, 1, W, w, 2" );
        add( prePadOperators, "1, 5, 1, 1, W, 0, 2" );
        add( postPadOperators, "2, 5, 1, 1, W, 0, 2" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Pad_digits", "Pad digits") ), "0, 6, 1, 1, W, w, 2" );
        add( prePadDigits, "1, 6, 1, 1, W, 0, 2" );
        add( postPadDigits, "2, 6, 1, 1, W, 0, 2" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Pad_keywords1", "Pad keywords1") ), "0, 7, 1, 1, W, w, 2" );
        add( prePadKeywords1, "1, 7, 1, 1, W, 0, 2" );
        add( postPadKeywords1, "2, 7, 1, 1, W, 0, 2" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Pad_keywords2", "Pad keywords2") ), "0, 8, 1, 1, W, w, 2" );
        add( prePadKeywords2, "1, 8, 1, 1, W, 0, 2" );
        add( postPadKeywords2, "2, 8, 1, 1, W, 0, 2" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Pad_keywords2", "Pad keywords2") ), "0, 9, 1, 1, W, w, 2" );
        add( prePadKeywords3, "1, 9, 1, 1, W, 0, 2" );
        add( postPadKeywords3, "2, 9, 1, 1, W, 0, 2" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Pad_keywords4", "Pad keywords4") ), "0, 10, 1, 1, W, w, 2" );
        add( prePadKeywords4, "1, 10, 1, 1, W, 0, 2" );
        add( postPadKeywords4, "2, 10, 1, 1, W, 0, 2" );

        add( labelOnSeparateLine, "0, 11, R, 1, W, w, 2" );

        add( KappaLayout.createVerticalStrut( 6 ), "0, 12" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Pad_before_these_characters>", "Pad before these characters:") ), "0, 13, R, 1, W, w, 2" );
        add( prePadCharacters, "0, 14, R, 1, W, w, 2" );

        add( KappaLayout.createVerticalStrut( 6 ), "0, 15" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Pad_after_these_characters>", "Pad after these characters:") ), "0, 16, R, 1, W, w, 2" );
        add( postPadCharacters, "0, 17, R, 1, W, w, 2" );

        add( KappaLayout.createVerticalStrut( 6 ), "0, 18" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Don't_pad_before_these_characters>", "Don't pad before these characters:") ), "0, 19, R, 1, W, w, 2" );
        add( dontPrePadCharacters, "0, 20, R, 1, W, w, 2" );

        add( KappaLayout.createVerticalStrut( 6 ), "0, 21" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Don't_pad_after_these_characters>", "Don't pad after these characters:") ), "0, 22, R, 1, W, w, 2" );
        add( dontPostPadCharacters, "0, 23, R, 1, W, w, 2" );

        add( KappaLayout.createVerticalStrut( 6 ), "0, 24" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Insert_line_separator_before_these_strings_(separate_with_comma)>", "Insert line separator before these strings (separate with comma):") ), "0, 25, R, 1, W, w, 2" );
        add( preInsertLineCharacters, "0, 26, R, 1, W, w, 2" );

        add( KappaLayout.createVerticalStrut( 6 ), "0, 27" );

        add( new JLabel( jEdit.getProperty("beauty.msg.Insert_line_separator_after_these_strings_(separate_with_comma)>", "Insert line separator after these strings (separate with comma):") ), "0, 28, R, 1, W, w, 2" );
        add( postInsertLineCharacters, "0, 29, R, 1, W, w, 2" );

        add( KappaLayout.createVerticalStrut( 6 ), "0, 30" );

        add( collapseBlankLines, "0, 31, R, 1, W, w, 2" );
        
        add( usejEditIndenter, "0, 32, R, 1, W, w, 2" );
    }

    // install listeners for the various gui components
    private void installListeners() {
        modeSelector.addItemListener(
            new ItemListener() {
                public void itemStateChanged( ItemEvent ie ) {
                    updateComponents( ie );
                }
            }
        );
    }

    private boolean getBoolean( String key ) {
        String value = modeProperties.getProperty( key );
        return "true".equals( value );
    }

    private String getText( String key ) {
        String value = modeProperties.getProperty( key );
        return value == null ? "" : value;
    }

    public void _save() {
        String name = currentMode.getName();
        modeProperties.setProperty( "usejEditIndenter", usejEditIndenter.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "prePadFunctions", prePadFunctions.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "prePadDigits", prePadDigits.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "prePadOperators", prePadOperators.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "prePadKeywords1", prePadKeywords1.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "prePadKeywords2", prePadKeywords2.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "prePadKeywords3", prePadKeywords3.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "prePadKeywords4", prePadKeywords4.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "postPadFunctions", postPadFunctions.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "postPadDigits", postPadDigits.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "postPadOperators", postPadOperators.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "postPadKeywords1", postPadKeywords1.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "postPadKeywords2", postPadKeywords2.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "postPadKeywords3", postPadKeywords3.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "postPadKeywords4", postPadKeywords4.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "labelOnSeparateLine", labelOnSeparateLine.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "collapseBlankLines", collapseBlankLines.isSelected() ? "true" : "false" );
        modeProperties.setProperty( "prePadCharacters", prePadCharacters.getText() );
        modeProperties.setProperty( "postPadCharacters", postPadCharacters.getText() );
        modeProperties.setProperty( "dontPrePadCharacters", dontPrePadCharacters.getText() );
        modeProperties.setProperty( "dontPostPadCharacters", dontPostPadCharacters.getText() );
        modeProperties.setProperty( "preInsertLineCharacters", preInsertLineCharacters.getText() );
        modeProperties.setProperty( "postInsertLineCharacters", postInsertLineCharacters.getText() );

        BeautyPlugin.saveProperties( name, modeProperties );
        BeautyPlugin.registerServices();
    }

    // update the components to display the settings from the current mode properties
    private void updateComponents( final ItemEvent ie ) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    clearComponentValues();

                    Mode selectedMode = ( Mode ) ie.getItem();
                    currentMode = selectedMode;
                    loadProperties( currentMode.getName() );

                    setComponentValues();
                }
            }
        );
    }

    // uncheck all checkboxes and clear the text fields
    private void clearComponentValues() {
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
        collapseBlankLines.setSelected( false );

        prePadCharacters.setText( "" );
        postPadCharacters.setText( "" );
        dontPrePadCharacters.setText( "" );
        dontPostPadCharacters.setText( "" );
        preInsertLineCharacters.setText( "" );
        postInsertLineCharacters.setText( "" );
    }

    private void setComponentValues() {
        // set the check box values
        usejEditIndenter.setSelected( getBoolean( "usejEditIndenter" ) );
        prePadFunctions.setSelected( getBoolean( "prePadFunctions" ) );
        prePadDigits.setSelected( getBoolean( "prePadDigits" ) );
        prePadOperators.setSelected( getBoolean( "prePadOperators" ) );
        prePadKeywords1.setSelected( getBoolean( "prePadKeywords1" ) );
        prePadKeywords2.setSelected( getBoolean( "prePadKeywords2" ) );
        prePadKeywords3.setSelected( getBoolean( "prePadKeywords3" ) );
        prePadKeywords4.setSelected( getBoolean( "prePadKeywords4" ) );
        postPadFunctions.setSelected( getBoolean( "postPadFunctions" ) );
        postPadDigits.setSelected( getBoolean( "postPadDigits" ) );
        postPadOperators.setSelected( getBoolean( "postPadOperators" ) );
        postPadKeywords1.setSelected( getBoolean( "postPadKeywords1" ) );
        postPadKeywords2.setSelected( getBoolean( "postPadKeywords2" ) );
        postPadKeywords3.setSelected( getBoolean( "postPadKeywords3" ) );
        postPadKeywords4.setSelected( getBoolean( "postPadKeywords4" ) );
        labelOnSeparateLine.setSelected( getBoolean( "labelOnSeparateLine" ) );
        collapseBlankLines.setSelected(getBoolean("collapseBlankLines"));
        
        // set the text field values
        prePadCharacters.setText( getText( "prePadCharacters" ) );
        postPadCharacters.setText( getText( "postPadCharacters" ) );
        dontPrePadCharacters.setText( getText( "dontPrePadCharacters" ) );
        dontPostPadCharacters.setText( getText( "dontPostPadCharacters" ) );
        preInsertLineCharacters.setText( getText( "preInsertLineCharacters" ) );
        postInsertLineCharacters.setText( getText( "postInsertLineCharacters" ) );
    }
}