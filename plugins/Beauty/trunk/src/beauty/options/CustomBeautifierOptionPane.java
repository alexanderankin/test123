package beauty.options;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import beauty.BeautyPlugin;

import ise.java.awt.*;


/**
 * An option pane to configure custom beautification rules for a mode.
 *
 */
public class CustomBeautifierOptionPane extends AbstractOptionPane {

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

    // the user can elect to use the jEdit indenter
    private JCheckBox usejEditIndenter;

    // textfields for indenting properties
    private JTextField indentOpenBrackets;
    private JTextField indentCloseBrackets;
    private JTextField unalignedOpenBrackets;
    private JTextField unalignedCloseBrackets;
    private JTextField indentNextLine;
    private JTextField unindentThisLine;
    private JTextField electricKeys;

    // checkboxes for indenting properties
    private JCheckBox lineUpClosingBracket;
    private JCheckBox doubleBracketIndent;

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
        JLabel description = new JLabel( jEdit.getProperty( "beauty.msg.<html><b>Create_a_custom_beautifier_for_a_mode", "<html><b>Create a custom beautifier for a mode" ) );

        JLabel mode_label = new JLabel( jEdit.getProperty( "beauty.msg.Mode>_", "Mode: " ) );
        Mode[] modes = jEdit.getModes();
        Arrays.sort( modes, new Comparator<Mode>() {
                    public int compare( Mode a, Mode b ) {
                        return a.getName().toLowerCase().compareTo( b.getName().toLowerCase() );
                    }
                }
                   );
        modeSelector = new JComboBox( modes );
        modeSelector.setSelectedItem( currentMode );
        JPanel modePanel = new JPanel( new KappaLayout() );
        modePanel.add( mode_label, "0, 0, 1, 1, W, w, 0" );
        modePanel.add( modeSelector, "1, 0, 1, 1, 0, w, 3" );

        // tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.add( "Padding", createPaddingPanel() );
        tabs.add( "Indenting", createIndentingPanel() );

        add( description, "0, 0, 1, 1, W, w, 2" );
        add( KappaLayout.createVerticalStrut( 6 ), "0, 1" );
        add( modePanel, "0, 2, 1, 1, W, w, 2" );
        add( KappaLayout.createVerticalStrut( 3 ), "0, 3" );
        add( tabs, "0, 4, 1, 1, 0, wh, 2" );

        // set the values for the components
        setComponentValues();
    }

    private JPanel createIndentingPanel() {
        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );

        usejEditIndenter = new JCheckBox( jEdit.getProperty( "beauty.msg.Use_jEdit_indenter_for_this_mode", "Use jEdit indenter for this mode" ) );
        indentOpenBrackets = new JTextField();
        indentCloseBrackets = new JTextField();
        unalignedOpenBrackets = new JTextField();
        unalignedCloseBrackets = new JTextField();
        indentNextLine = new JTextField();
        unindentThisLine = new JTextField();
        electricKeys = new JTextField();
        lineUpClosingBracket = new JCheckBox(jEdit.getProperty("beauty.msg.Line_up_closing_bracket", "Line up closing bracket"));
        doubleBracketIndent = new JCheckBox(jEdit.getProperty("beauty.msg.Double_bracket_indent", "Double bracket indent"));

        panel.add( usejEditIndenter, "0, 0, 1, 1, W, w, 2" );
        panel.add( new JLabel( jEdit.getProperty("beauty.msg.Indent_open_brackets>", "Indent open brackets:") ), "0, 1, 1, 1, W, w, 2" );
        panel.add( indentOpenBrackets, "0, 2, 1, 1, W, w, 2" );
        panel.add( new JLabel( jEdit.getProperty("beauty.msg.Indent_close_brackets>", "Indent close brackets:") ), "0, 3, 1, 1, W, w, 2" );
        panel.add( indentCloseBrackets, "0, 4, 1, 1, W, w, 2" );
        panel.add( new JLabel( jEdit.getProperty("beauty.msg.Unaligned_open_brackets>", "Unaligned open brackets:") ), "0, 5, 1, 1, W, w, 2" );
        panel.add( unalignedOpenBrackets, "0, 6, 1, 1, W, w, 2" );
        panel.add( new JLabel( jEdit.getProperty("beauty.msg.Unaligned_close_brackets>", "Unaligned close brackets:") ), "0, 7, 1, 1, W, w, 2" );
        panel.add( unalignedCloseBrackets, "0, 8, 1, 1, W, w, 2" );
        panel.add( new JLabel( jEdit.getProperty("beauty.msg.Indent_next_line>", "Indent next line:") ), "0, 9, 1, 1, W, w, 2" );
        panel.add( indentNextLine, "0, 10, 1, 1, W, w, 2" );
        panel.add( new JLabel( jEdit.getProperty("beauty.msg.Unindent_this_line>", "Unindent this line:") ), "0, 11, 1, 1, W, w, 2" );
        panel.add( unindentThisLine, "0, 12, 1, 1, W, w, 2" );
        panel.add( new JLabel( jEdit.getProperty("beauty.msg.Electric_keys>", "Electric keys:") ), "0, 13, 1, 1, W, w, 2" );
        panel.add( electricKeys, "0, 14, 1, 1, W, w, 2" );
        panel.add( lineUpClosingBracket, "0, 15, 1, 1, W, w, 2");
        panel.add( doubleBracketIndent, "0, 16, 1, 1, W, w, 2");

        return panel;
    }

    private JPanel createPaddingPanel() {
        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );

        prePadFunctions = new JCheckBox( jEdit.getProperty( "beauty.msg.before", "before" ) );
        prePadDigits = new JCheckBox( jEdit.getProperty( "beauty.msg.before", "before" ) );
        prePadOperators = new JCheckBox( jEdit.getProperty( "beauty.msg.before", "before" ) );
        prePadKeywords1 = new JCheckBox( jEdit.getProperty( "beauty.msg.before", "before" ) );
        prePadKeywords2 = new JCheckBox( jEdit.getProperty( "beauty.msg.before", "before" ) );
        prePadKeywords3 = new JCheckBox( jEdit.getProperty( "beauty.msg.before", "before" ) );
        prePadKeywords4 = new JCheckBox( jEdit.getProperty( "beauty.msg.before", "before" ) );
        postPadFunctions = new JCheckBox( jEdit.getProperty( "beauty.msg.after", "after" ) );
        postPadDigits = new JCheckBox( jEdit.getProperty( "beauty.msg.after", "after" ) );
        postPadOperators = new JCheckBox( jEdit.getProperty( "beauty.msg.after", "after" ) );
        postPadKeywords1 = new JCheckBox( jEdit.getProperty( "beauty.msg.after", "after" ) );
        postPadKeywords2 = new JCheckBox( jEdit.getProperty( "beauty.msg.after", "after" ) );
        postPadKeywords3 = new JCheckBox( jEdit.getProperty( "beauty.msg.after", "after" ) );
        postPadKeywords4 = new JCheckBox( jEdit.getProperty( "beauty.msg.after", "after" ) );

        labelOnSeparateLine = new JCheckBox( jEdit.getProperty( "beauty.msg.Label_on_separate_line", "Label on separate line" ) );

        prePadCharacters = new JTextField();
        postPadCharacters = new JTextField();
        dontPrePadCharacters = new JTextField();
        dontPostPadCharacters = new JTextField();
        preInsertLineCharacters = new JTextField();
        postInsertLineCharacters = new JTextField();

        collapseBlankLines = new JCheckBox( jEdit.getProperty( "beauty.msg.Collapse_multiple_blank_lines", "Collapse multiple blank lines" ) );

        // layout the components
        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Pad_functions", "Pad functions" ) ), "0, 4, 1, 1, W, w, 2" );
        panel.add( prePadFunctions, "1, 4, 1, 1, W, 0, 2" );
        panel.add( postPadFunctions, "2, 4, 1, 1, W, 0, 2" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Pad_operators", "Pad operators" ) ), "0, 5, 1, 1, W, w, 2" );
        panel.add( prePadOperators, "1, 5, 1, 1, W, 0, 2" );
        panel.add( postPadOperators, "2, 5, 1, 1, W, 0, 2" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Pad_digits", "Pad digits" ) ), "0, 6, 1, 1, W, w, 2" );
        panel.add( prePadDigits, "1, 6, 1, 1, W, 0, 2" );
        panel.add( postPadDigits, "2, 6, 1, 1, W, 0, 2" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Pad_keywords1", "Pad keywords1" ) ), "0, 7, 1, 1, W, w, 2" );
        panel.add( prePadKeywords1, "1, 7, 1, 1, W, 0, 2" );
        panel.add( postPadKeywords1, "2, 7, 1, 1, W, 0, 2" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Pad_keywords2", "Pad keywords2" ) ), "0, 8, 1, 1, W, w, 2" );
        panel.add( prePadKeywords2, "1, 8, 1, 1, W, 0, 2" );
        panel.add( postPadKeywords2, "2, 8, 1, 1, W, 0, 2" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Pad_keywords2", "Pad keywords2" ) ), "0, 9, 1, 1, W, w, 2" );
        panel.add( prePadKeywords3, "1, 9, 1, 1, W, 0, 2" );
        panel.add( postPadKeywords3, "2, 9, 1, 1, W, 0, 2" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Pad_keywords4", "Pad keywords4" ) ), "0, 10, 1, 1, W, w, 2" );
        panel.add( prePadKeywords4, "1, 10, 1, 1, W, 0, 2" );
        panel.add( postPadKeywords4, "2, 10, 1, 1, W, 0, 2" );

        panel.add( labelOnSeparateLine, "0, 11, R, 1, W, w, 2" );

        panel.add( KappaLayout.createVerticalStrut( 6 ), "0, 12" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Pad_before_these_characters>", "Pad before these characters:" ) ), "0, 13, R, 1, W, w, 2" );
        panel.add( prePadCharacters, "0, 14, R, 1, W, w, 2" );

        panel.add( KappaLayout.createVerticalStrut( 6 ), "0, 15" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Pad_after_these_characters>", "Pad after these characters:" ) ), "0, 16, R, 1, W, w, 2" );
        panel.add( postPadCharacters, "0, 17, R, 1, W, w, 2" );

        panel.add( KappaLayout.createVerticalStrut( 6 ), "0, 18" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Don't_pad_before_these_characters>", "Don't pad before these characters:" ) ), "0, 19, R, 1, W, w, 2" );
        panel.add( dontPrePadCharacters, "0, 20, R, 1, W, w, 2" );

        panel.add( KappaLayout.createVerticalStrut( 6 ), "0, 21" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Don't_pad_after_these_characters>", "Don't pad after these characters:" ) ), "0, 22, R, 1, W, w, 2" );
        panel.add( dontPostPadCharacters, "0, 23, R, 1, W, w, 2" );

        panel.add( KappaLayout.createVerticalStrut( 6 ), "0, 24" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Insert_line_separator_before_these_strings_(separate_with_comma)>", "Insert line separator before these strings (separate with comma):" ) ), "0, 25, R, 1, W, w, 2" );
        panel.add( preInsertLineCharacters, "0, 26, R, 1, W, w, 2" );

        panel.add( KappaLayout.createVerticalStrut( 6 ), "0, 27" );

        panel.add( new JLabel( jEdit.getProperty( "beauty.msg.Insert_line_separator_after_these_strings_(separate_with_comma)>", "Insert line separator after these strings (separate with comma):" ) ), "0, 28, R, 1, W, w, 2" );
        panel.add( postInsertLineCharacters, "0, 29, R, 1, W, w, 2" );

        panel.add( KappaLayout.createVerticalStrut( 6 ), "0, 30" );

        panel.add( collapseBlankLines, "0, 31, R, 1, W, w, 2" );

        return panel;
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

        usejEditIndenter.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    indentOpenBrackets.setEnabled( usejEditIndenter.isSelected() );
                    indentCloseBrackets.setEnabled( usejEditIndenter.isSelected() );
                    unalignedOpenBrackets.setEnabled( usejEditIndenter.isSelected() );
                    unalignedCloseBrackets.setEnabled( usejEditIndenter.isSelected() );
                    indentNextLine.setEnabled( usejEditIndenter.isSelected() );
                    unindentThisLine.setEnabled( usejEditIndenter.isSelected() );
                    electricKeys.setEnabled( usejEditIndenter.isSelected() );
                    lineUpClosingBracket.setEnabled( usejEditIndenter.isSelected());
                    doubleBracketIndent.setEnabled( usejEditIndenter.isSelected());
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

        modeProperties.setProperty( "usejEditIndenter", usejEditIndenter.isSelected() ? "true" : "false" );
        if ( usejEditIndenter.isSelected() ) {
            modeProperties.setProperty( "indentOpenBrackets", indentOpenBrackets.getText() );
            modeProperties.setProperty( "indentCloseBrackets", indentCloseBrackets.getText() );
            modeProperties.setProperty( "unalignedOpenBrackets", unalignedOpenBrackets.getText() );
            modeProperties.setProperty( "unalignedCloseBrackets", unalignedCloseBrackets.getText() );
            modeProperties.setProperty( "indentNextLine", indentNextLine.getText() );
            modeProperties.setProperty( "unindentThisLine", unindentThisLine.getText() );
            modeProperties.setProperty( "electricKeys", electricKeys.getText() );
            modeProperties.setProperty( "lineUpClosingBracket", lineUpClosingBracket.isSelected() ? "true" : "false");
            modeProperties.setProperty( "doubleBracketIndent", doubleBracketIndent.isSelected() ? "true" : "false");

            currentMode.setProperty( "indentOpenBrackets", indentOpenBrackets.getText() );
            currentMode.setProperty( "indentCloseBrackets", indentCloseBrackets.getText() );
            currentMode.setProperty( "unalignedOpenBrackets", unalignedOpenBrackets.getText() );
            currentMode.setProperty( "unalignedCloseBrackets", unalignedCloseBrackets.getText() );
            currentMode.setProperty( "indentNextLine", indentNextLine.getText() );
            currentMode.setProperty( "unindentThisLine", unindentThisLine.getText() );
            currentMode.setProperty( "electricKeys", electricKeys.getText() );
            currentMode.setProperty( "lineUpClosingBracket", lineUpClosingBracket.isSelected());
            currentMode.setProperty( "doubleBracketIndent", doubleBracketIndent.isSelected());
        }

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

        indentOpenBrackets.setText("");
        indentCloseBrackets.setText("");
        unalignedOpenBrackets.setText("");
        unalignedCloseBrackets.setText("");
        indentNextLine.setText("");
        unindentThisLine.setText("");
        electricKeys.setText("");
        lineUpClosingBracket.setSelected( false );
        doubleBracketIndent.setSelected( false );
    }

    private void setComponentValues() {
        // set the check box values
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
        collapseBlankLines.setSelected( getBoolean( "collapseBlankLines" ) );

        // set the text field values
        prePadCharacters.setText( getText( "prePadCharacters" ) );
        postPadCharacters.setText( getText( "postPadCharacters" ) );
        dontPrePadCharacters.setText( getText( "dontPrePadCharacters" ) );
        dontPostPadCharacters.setText( getText( "dontPostPadCharacters" ) );
        preInsertLineCharacters.setText( getText( "preInsertLineCharacters" ) );
        postInsertLineCharacters.setText( getText( "postInsertLineCharacters" ) );

        // indenter values
        usejEditIndenter.setSelected( getBoolean( "usejEditIndenter" ) );
        indentOpenBrackets.setEnabled( usejEditIndenter.isSelected() );
        indentCloseBrackets.setEnabled( usejEditIndenter.isSelected() );
        unalignedOpenBrackets.setEnabled( usejEditIndenter.isSelected() );
        unalignedCloseBrackets.setEnabled( usejEditIndenter.isSelected() );
        indentNextLine.setEnabled( usejEditIndenter.isSelected() );
        unindentThisLine.setEnabled( usejEditIndenter.isSelected() );
        electricKeys.setEnabled( usejEditIndenter.isSelected() );
        lineUpClosingBracket.setEnabled( usejEditIndenter.isSelected() );
        doubleBracketIndent.setEnabled( usejEditIndenter.isSelected() );

        indentOpenBrackets.setText( getText( "indentOpenBrackets" ) );
        indentCloseBrackets.setText( getText( "indentCloseBrackets" ) );
        unalignedOpenBrackets.setText( getText( "unalignedOpenBrackets" ) );
        unalignedCloseBrackets.setText( getText( "unalignedCloseBrackets" ) );
        indentNextLine.setText( getText( "indentNextLine" ) );
        unindentThisLine.setText( getText( "unindentThisLine" ) );
        electricKeys.setText( getText( "electricKeys" ) );
        lineUpClosingBracket.setSelected( getBoolean("lineUpClosingBracket") );
        doubleBracketIndent.setSelected( getBoolean("doubleBracketIndent") );
    }
}