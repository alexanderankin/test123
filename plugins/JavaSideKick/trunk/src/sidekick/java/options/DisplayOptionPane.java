package sidekick.java.options;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


public class DisplayOptionPane extends AbstractOptionPane {

    private JLabel titleLabel = new JLabel( jEdit.getProperty("options.sidekick.java.displayOptions", "<html><b>Display Options:</b> How to display") );
    private JCheckBox argumentsCheckBox;
    private JCheckBox formalNamesCheckBox;
    private JCheckBox showGenericsCheckBox;
    private JCheckBox qualifyNamesCheckBox;
    private JCheckBox keywordsCheckBox;
    private JCheckBox showModifiersCheckBox;
    private JCheckBox showIconsCheckBox;
    private JCheckBox likeEclipseCheckBox;
    private JCheckBox lineNumbersCheckBox;
    private JCheckBox expandClassesCheckBox;

    private JLabel sortByLabel = new JLabel( jEdit.getProperty("options.sidekick.java.sortBy", "Sort by:") );
    private JRadioButton lineRB;
    private JRadioButton nameRB;
    private JRadioButton visibilityRB;

    private JLabel displayStyleLabel = new JLabel( jEdit.getProperty("options.sidekick.java.displayStyle", "Display style:") );
    private JComboBox displayStyleComboBox;
    private String[] displayStyleNames = {
                jEdit.getProperty( "options.sidekick.java.umlStyle", "UML" ),
                jEdit.getProperty( "options.sidekick.java.javaStyle", "Java" ),
                jEdit.getProperty( "options.sidekick.java.customStyle", "Custom" ) };


    private JLabel customDisplayLabel = new JLabel( jEdit.getProperty("options.sidekick.java.customOptions", "Custom Display Options:") );
    private JLabel useVisibilityLabel = new JLabel( jEdit.getProperty("options.sidekick.java.useVisibility", "Use Visibility:") );
    private JRadioButton symbolsRB;
    private JRadioButton wordsRB;
    private JRadioButton noneRB;
    private JCheckBox abstractCheckBox;
    private JCheckBox staticCheckBox;
    private JCheckBox typeIdentifierCheckBox;


    public DisplayOptionPane() {
        super( jEdit.getProperty("options.sidekick.java.display", "Display") );
    }

    protected void _init() {
        installComponents();
        installDefaults();
        installListeners();
    }

    /**
     * Create and add all GUI components.    
     */
    private void installComponents() {
        createComponents();
        addComponents();
    }

    private void createComponents() {
        argumentsCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showArgs", "Arguments" ) );
        formalNamesCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showArgNames", "formal names" ) );
        showGenericsCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showTypeArgs", "Show Generics type arguments" ) );
        qualifyNamesCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showNestedName" ) );
        keywordsCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showIconKeywords" ) );
        showModifiersCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showMiscMod" ) );
        showIconsCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showIcons" ) + " " );
        likeEclipseCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showIconsLikeEclipse" ) );
        lineNumbersCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showLineNums" ) );
        expandClassesCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.expandClasses", "Expand inner classes"));

        // sort by
        lineRB = new JRadioButton( jEdit.getProperty( "options.sidekick.java.sortByLine" ) );
        nameRB = new JRadioButton( jEdit.getProperty( "options.sidekick.java.sortByName" ) );
        visibilityRB = new JRadioButton( jEdit.getProperty( "options.sidekick.java.sortByVisibility" ) );

        // display style
        displayStyleComboBox = new JComboBox( displayStyleNames );
        symbolsRB = new JRadioButton( jEdit.getProperty( "options.sidekick.java.custVisAsSymbol" ) );
        wordsRB = new JRadioButton( jEdit.getProperty( "options.sidekick.java.custVisAsWord" ) );
        noneRB = new JRadioButton( jEdit.getProperty( "options.sidekick.java.custVisAsNone" ) );
        abstractCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.custAbsAsItalic" ) );
        staticCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.custStaAsUlined" ) );
        typeIdentifierCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.custTypeIsSuffixed" ) );
    }

    private void addComponents() {
        setBorder( BorderFactory.createEmptyBorder( 11, 11, 12, 12 ) );
        addComponent( titleLabel );

        addComponent( Box.createVerticalStrut( 11 ) );
        JPanel argumentsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
        argumentsPanel.add( argumentsCheckBox );
        argumentsPanel.add( Box.createHorizontalStrut( 6 ) );
        argumentsPanel.add( formalNamesCheckBox );
        addComponent( argumentsPanel );

        addComponent( showGenericsCheckBox );
        addComponent( qualifyNamesCheckBox );
        addComponent( keywordsCheckBox );
        addComponent( showModifiersCheckBox );

        JPanel iconsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
        iconsPanel.add( showIconsCheckBox );
        iconsPanel.add( Box.createHorizontalStrut( 6 ) );
        iconsPanel.add( likeEclipseCheckBox );
        addComponent( iconsPanel );

        addComponent( lineNumbersCheckBox );
        addComponent( expandClassesCheckBox );

        addComponent( Box.createVerticalStrut( 11 ) );

        JPanel sortByPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
        sortByPanel.add( sortByLabel );
        sortByPanel.add( Box.createHorizontalStrut( 6 ) );
        sortByPanel.add( lineRB );
        sortByPanel.add( Box.createHorizontalStrut( 6 ) );
        sortByPanel.add( nameRB );
        sortByPanel.add( Box.createHorizontalStrut( 6 ) );
        sortByPanel.add( visibilityRB );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( lineRB );
        buttonGroup.add( nameRB );
        buttonGroup.add( visibilityRB );
        addComponent( sortByPanel );

        addComponent( Box.createVerticalStrut( 11 ) );

        addComponent( displayStyleLabel, displayStyleComboBox );
        JPanel visibilityPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
        visibilityPanel.add( useVisibilityLabel );
        visibilityPanel.add( symbolsRB );
        visibilityPanel.add( Box.createHorizontalStrut( 6 ) );
        visibilityPanel.add( wordsRB );
        visibilityPanel.add( Box.createHorizontalStrut( 6 ) );
        visibilityPanel.add( noneRB );
        addComponent( visibilityPanel );

        addComponent( abstractCheckBox );
        addComponent( staticCheckBox );
        addComponent( typeIdentifierCheckBox );
    }

    /**
     * Set/restore values from jEdit properties.    
     */
    private void installDefaults() {
        argumentsCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showArgs", true ) );
        formalNamesCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showArgNames", false ) );
        showGenericsCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showTypeArgs", false ) );
        qualifyNamesCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showNestedName", false ) );
        keywordsCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showIconKeywords", false ) );
        showModifiersCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showMiscMod", false ) );
        showIconsCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showIcons", true ) );
        likeEclipseCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showIconsLikeEclipse", false ) );
        lineNumbersCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showLineNums", false ) );
        expandClassesCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.expandClasses", true ) );

        int sortBy = jEdit.getIntegerProperty( "sidekick.java.sortBy", OptionValues.SORT_BY_NAME );
        switch ( sortBy ) {
            case OptionValues.SORT_BY_LINE:
                lineRB.setSelected( true );
                nameRB.setSelected( false );
                visibilityRB.setSelected( false );
                break;
            case OptionValues.SORT_BY_VISIBILITY:
                lineRB.setSelected( false );
                nameRB.setSelected( false );
                visibilityRB.setSelected( true );
                break;
            case OptionValues.SORT_BY_NAME:
            default:
                lineRB.setSelected( false );
                nameRB.setSelected( true );
                visibilityRB.setSelected( false );
                break;
        }
        displayStyleComboBox.setSelectedIndex( jEdit.getIntegerProperty( "sidekick.java.displayStyle", OptionValues.STYLE_UML ) );

        symbolsRB.setSelected( jEdit.getBooleanProperty( "sidekick.java.custVisAsSymbol", true ) );
        wordsRB.setSelected( jEdit.getBooleanProperty( "sidekick.java.custVisAsWord", false ) );
        noneRB.setSelected( jEdit.getBooleanProperty( "sidekick.java.custVisAsNone", false ) );
        abstractCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.custAbsAsItalic", true ) );
        staticCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.custStaAsUlined", true ) );
        typeIdentifierCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.custTypeIsSuffixed", true ) );
        enableCustomOptions( displayStyleComboBox.getSelectedIndex() == OptionValues.STYLE_CUSTOM );
    }

    private void installListeners() {
        // enable/disable the custom display options based on the currently
        // selected item in the displayStyleComboBox.
        displayStyleComboBox.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    switch ( displayStyleComboBox.getSelectedIndex() ) {
                        case OptionValues.STYLE_CUSTOM:
                            enableCustomOptions( true );
                            break;
                        case OptionValues.STYLE_UML:
                        case OptionValues.STYLE_JAVA:
                        default:
                            enableCustomOptions( false );
                            break;
                    }
                }
            }
        );
    }

    private void enableCustomOptions( boolean enabled ) {
        customDisplayLabel.setEnabled( enabled );
        useVisibilityLabel.setEnabled( enabled );
        symbolsRB.setEnabled( enabled );
        wordsRB.setEnabled( enabled );
        noneRB.setEnabled( enabled );
        abstractCheckBox.setEnabled( enabled );
        staticCheckBox.setEnabled( enabled );
        typeIdentifierCheckBox.setEnabled( enabled );
    }

    protected void _save() {
        jEdit.setBooleanProperty( "sidekick.java.showArgs", argumentsCheckBox.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.showArgNames", formalNamesCheckBox.isSelected() ) ;
        jEdit.setBooleanProperty( "sidekick.java.showTypeArgs", showGenericsCheckBox.isSelected() ) ;
        jEdit.setBooleanProperty( "sidekick.java.showNestedName", qualifyNamesCheckBox.isSelected() ) ;
        jEdit.setBooleanProperty( "sidekick.java.showIconKeywords", keywordsCheckBox.isSelected() ) ;
        jEdit.setBooleanProperty( "sidekick.java.showMiscMod", showModifiersCheckBox.isSelected() ) ;
        jEdit.setBooleanProperty( "sidekick.java.showIcons", showIconsCheckBox.isSelected() ) ;
        jEdit.setBooleanProperty( "sidekick.java.showIconsLikeEclipse", likeEclipseCheckBox.isSelected() ) ;
        jEdit.setBooleanProperty( "sidekick.java.showLineNums", lineNumbersCheckBox.isSelected() ) ;
        jEdit.setBooleanProperty( "sidekick.java.expandClasses", expandClassesCheckBox.isSelected() );

        int sortBy = OptionValues.SORT_BY_NAME;
        if ( lineRB.isSelected() ) {
            sortBy = OptionValues.SORT_BY_LINE;
        }
        else if ( visibilityRB.isSelected() ) {
            sortBy = OptionValues.SORT_BY_VISIBILITY;
        }
        jEdit.setIntegerProperty( "sidekick.java.sortBy", sortBy );

        jEdit.getIntegerProperty( "sidekick.java.displayStyle", displayStyleComboBox.getSelectedIndex() ) ;

        jEdit.setBooleanProperty( "sidekick.java.custVisAsSymbol", symbolsRB.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.custVisAsWord", wordsRB.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.custVisAsNone", noneRB.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.custAbsAsItalic", abstractCheckBox.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.custStaAsUlined", staticCheckBox.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.custTypeIsSuffixed", typeIdentifierCheckBox.isSelected() );
    }
}