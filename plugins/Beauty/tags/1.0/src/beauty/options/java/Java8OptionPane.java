
package beauty.options.java;

import beauty.options.NumberTextField;

import ise.java.awt.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;

public class Java8OptionPane extends JPanel {

    private NumberTextField blankLinesBeforePackage;
    private NumberTextField blankLinesAfterPackage;
    private NumberTextField blankLinesAfterImports;
    private JCheckBox sortImports;
    private JCheckBox groupImports;
    private NumberTextField blankLinesBetweenImportGroups;
    private NumberTextField blankLinesAfterClassDeclaration;
    private NumberTextField blankLinesAfterClassBody;
    private NumberTextField blankLinesBeforeMethods;
    private NumberTextField blankLinesAfterMethods;
    private JCheckBox sortModifiers;
    private NumberTextField collapseMultipleBlankLinesTo;

    public Java8OptionPane() {
        super( new KappaLayout() );
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
    }

    public void _init() {
        JLabel description = new JLabel( "<html><b>" + jEdit.getProperty( "beauty.java8.Blank_Lines", "Blank Lines" ) );
        JLabel blankLinesBeforePackageLabel = new JLabel( jEdit.getProperty( "beauty.java8.Blank_Lines_Before_Package", "Blank Lines Before Package" ) );
        blankLinesBeforePackage = new NumberTextField( 0, 100 );
        blankLinesBeforePackage.setValue( jEdit.getIntegerProperty( "beauty.java8.blankLinesBeforePackage", 0 ) );

        JLabel blankLinesAfterPackageLabel = new JLabel( jEdit.getProperty( "beauty.java8.Blank_Lines_After_Package", "Blank Lines After Package" ) );
        blankLinesAfterPackage = new NumberTextField( 0, 100 );
        blankLinesAfterPackage.setValue( jEdit.getIntegerProperty( "beauty.java8.blankLinesAfterPackage", 2 ) );

        JLabel blankLinesAfterImportsLabel = new JLabel( jEdit.getProperty( "beauty.java8.Blank_Lines_After_Imports", "Blank Lines After Imports" ) );
        blankLinesAfterImports = new NumberTextField( 0, 100 );
        blankLinesAfterImports.setValue( jEdit.getIntegerProperty( "beauty.java8.blankLinesAfterImports", 2 ) );

        sortImports = new JCheckBox( jEdit.getProperty( "beauty.java8.Sort_Imports", "Sort Imports" ) );
        sortImports.setSelected( jEdit.getBooleanProperty( "beauty.java8.sortImports", true ) );

        groupImports = new JCheckBox( jEdit.getProperty( "beauty.java8.Group_Imports", "Group Imports" ) );
        groupImports.setSelected( jEdit.getBooleanProperty( "beauty.java8.groupImports", true ) );

        JLabel blankLinesBetweenImportGroupsLabel = new JLabel( jEdit.getProperty( "beauty.java8.Blank_Lines_Between_Import_Groups", "Blank Lines Between Import Groups" ) );
        blankLinesBetweenImportGroups = new NumberTextField( 0, 100 );
        blankLinesBetweenImportGroups.setValue( jEdit.getIntegerProperty( "beauty.java8.blankLinesBetweenImportGroups", 1 ) );

        JLabel blankLinesAfterClassDeclarationLabel = new JLabel( jEdit.getProperty( "beauty.java8.Blank_Lines_After_Class_Declaration", "Blank Lines After Class Declaration" ) );
        blankLinesAfterClassDeclaration = new NumberTextField( 0, 100 );
        blankLinesAfterClassDeclaration.setValue( jEdit.getIntegerProperty( "beauty.java8.blankLinesAfterClassDeclaration", 1 ) );

        JLabel blankLinesAfterClassBodyLabel = new JLabel( jEdit.getProperty( "beauty.java8.Blank_Lines_After_Class_Body", "Blank Lines After Class Body" ) );
        blankLinesAfterClassBody = new NumberTextField( 0, 100 );
        blankLinesAfterClassBody.setValue( jEdit.getIntegerProperty( "beauty.java8.blankLinesAfterClassBody", 1 ) );

        JLabel blankLinesBeforeMethodsLabel = new JLabel( jEdit.getProperty( "beauty.java8.Blank_Lines_Before_Methods", "Blank Lines Before Methods" ) );
        blankLinesBeforeMethods = new NumberTextField( 0, 100 );
        blankLinesBeforeMethods.setValue( jEdit.getIntegerProperty( "beauty.java8.blankLinesBeforeMethods", 2 ) );

        JLabel blankLinesAfterMethodsLabel = new JLabel( jEdit.getProperty( "beauty.java8.Blank_Lines_After_Methods", "Blank Lines After Methods" ) );
        blankLinesAfterMethods = new NumberTextField( 0, 100 );
        blankLinesAfterMethods.setValue( jEdit.getIntegerProperty( "beauty.java8.blankLinesAfterMethods", 2 ) );

        sortModifiers = new JCheckBox( jEdit.getProperty( "beauty.java8.Sort_Modifiers", "Sort Modifiers" ) );
        sortModifiers.setSelected( jEdit.getBooleanProperty( "beauty.java8.sortModifiers", true ) );

        JLabel collapseMultipleBlankLinesToLabel = new JLabel( jEdit.getProperty( "beauty.java8.Collapse_Multiple_Blank_Lines_To", "Collapse Multiple Blank Lines To" ) );
        collapseMultipleBlankLinesTo = new NumberTextField( 0, 100 );
        collapseMultipleBlankLinesTo.setValue( jEdit.getIntegerProperty( "beauty.java8.collapseMultipleBlankLinesTo", 2 ) );

        add( "0, 0, 1, 1, W, w, 3", description );
        add( "0, 1, 1, 1, W, w, 3", blankLinesBeforePackageLabel );
        add( "1, 1, 1, 1, W, w, 3", blankLinesBeforePackage );
        add( "0, 2, 1, 1, W, w, 3", blankLinesAfterPackageLabel );
        add( "1, 2, 1, 1, W, w, 3", blankLinesAfterPackage );
        add( "0, 3, 1, 1, W, w, 3", blankLinesAfterImportsLabel );
        add( "1, 3, 1, 1, W, w, 3", blankLinesAfterImports );
        add( "0, 4, 1, 1, W, w, 3", sortImports );
        add( "0, 5, 1, 1, W, w, 3", groupImports );
        add( "0, 6, 1, 1, W, w, 3", blankLinesBetweenImportGroupsLabel );
        add( "1, 6, 1, 1, W, w, 3", blankLinesBetweenImportGroups );
        add( "0, 7, 1, 1, W, w, 3", blankLinesAfterClassDeclarationLabel );
        add( "1, 7, 1, 1, W, w, 3", blankLinesAfterClassDeclaration );
        add( "0, 8, 1, 1, W, w, 3", blankLinesAfterClassBodyLabel );
        add( "1, 8, 1, 1, W, w, 3", blankLinesAfterClassBody );
        add( "0, 9, 1, 1, W, w, 3", blankLinesBeforeMethodsLabel );
        add( "1, 9, 1, 1, W, w, 3", blankLinesBeforeMethods );
        add( "0, 10,1, 1, W, w, 3", blankLinesAfterMethodsLabel );
        add( "1, 10,1, 1, W, w, 3", blankLinesAfterMethods );
        add( "0, 11,1, 1, W, w, 3", sortModifiers );
        add( "0, 12,1, 1, W, w, 3", collapseMultipleBlankLinesToLabel );
        add( "1, 12,1, 1, W, w, 3", collapseMultipleBlankLinesTo );
    }

    public void _save() {
        jEdit.setIntegerProperty( "beauty.java8.blankLinesBeforePackage", blankLinesBeforePackage.getValue() );
        jEdit.setIntegerProperty( "beauty.java8.blankLinesAfterPackage", blankLinesAfterPackage.getValue() );
        jEdit.setIntegerProperty( "beauty.java8.blankLinesAfterImports", blankLinesAfterImports.getValue() );
        jEdit.setBooleanProperty( "beauty.java8.sortImports", sortImports.isSelected() );
        jEdit.setBooleanProperty( "beauty.java8.groupImports", groupImports.isSelected() );
        jEdit.setIntegerProperty( "beauty.java8.blankLinesBetweenImportGroups", blankLinesBetweenImportGroups.getValue() );
        jEdit.setIntegerProperty( "beauty.java8.blankLinesAfterClassDeclaration", blankLinesAfterClassDeclaration.getValue() );
        jEdit.setIntegerProperty( "beauty.java8.blankLinesAfterClassBody", blankLinesAfterClassBody.getValue() );
        jEdit.setIntegerProperty( "beauty.java8.blankLinesBeforeMethods", blankLinesBeforeMethods.getValue() );
        jEdit.setIntegerProperty( "beauty.java8.blankLinesAfterMethods", blankLinesAfterMethods.getValue() );
        jEdit.setBooleanProperty( "beauty.java8.sortModifiers", sortModifiers.isSelected() );
        jEdit.setIntegerProperty( "beauty.java8.collapseMultipleBlankLinesTo", collapseMultipleBlankLinesTo.getValue() );
    }
}
