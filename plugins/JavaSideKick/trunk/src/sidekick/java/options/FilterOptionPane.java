package sidekick.java;

import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


public class FilterOptionPane extends AbstractOptionPane {

    private JCheckBox attributesCheckBox;
    private JCheckBox includePrimitivesCheckBox;
    private JCheckBox showImportsCheckBox;
    private JCheckBox showLocalVariablesCheckBox;
    private JCheckBox showStaticInitializersCheckBox;
    private JCheckBox showExtendsImplementsCheckBox;
    private JCheckBox showThrowsCheckBox;

    private JComboBox topLevelVisibilityComboBox;
    private String[] topLevelVisibilityNames = { "package", "public" };

    private JComboBox memberVisibilityComboBox;
    private String[] memberVisibilityNames = { "private", "package", "protected", "public" };

    public FilterOptionPane() {
        super("Filter");   
    }

    protected void _init() {
        setBorder( BorderFactory.createEmptyBorder( 11, 11, 12, 12 ) );

        // TODO: put this in a property
        JLabel titleLabel = new JLabel( "<html><b>Filter Options:</b> What to include" );

        attributesCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showAttr", "Attributes" ) );
        includePrimitivesCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showPrimAttr", "include primitives" ) );
        showImportsCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showImports", "Show imports" ) );
        showLocalVariablesCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showVariables", "Show local variables" ) );
        showStaticInitializersCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showInitializers", "Show static initializers" ) );
        showExtendsImplementsCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showGeneralizations", "Show extends/implements" ) );
        showThrowsCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showThrows", "Show method throws types" ) );

        JLabel visibilityLevelLabel = new JLabel( jEdit.getProperty( "options.sidekick.java.visLevelLabel", "Lowest visibility level to show:" ) );
        topLevelVisibilityComboBox = new JComboBox( topLevelVisibilityNames );
        memberVisibilityComboBox = new JComboBox( memberVisibilityNames );

        addComponent( titleLabel );
        addComponent( Box.createVerticalStrut( 11 ) );
        addComponent( attributesCheckBox );
        addComponent( includePrimitivesCheckBox );
        addComponent( showImportsCheckBox );
        addComponent( showLocalVariablesCheckBox );
        addComponent( showStaticInitializersCheckBox );
        addComponent( showExtendsImplementsCheckBox );
        addComponent( showThrowsCheckBox );

        addComponent( Box.createVerticalStrut( 11 ) );
        addComponent( visibilityLevelLabel );
        addComponent( jEdit.getProperty( "options.sidekick.java.topLevelVis", "Top level:" ), topLevelVisibilityComboBox );
        addComponent( jEdit.getProperty( "options.sidekick.java.memberVis", "Member:" ), memberVisibilityComboBox );
    }


    protected void _save() {
        jEdit.setBooleanProperty( "options.sidekick.java.showAttr", attributesCheckBox.isSelected() );
        jEdit.setBooleanProperty( "options.sidekick.java.showPrimAttr", includePrimitivesCheckBox.isSelected() );
        jEdit.setBooleanProperty( "options.sidekick.java.showImports", showImportsCheckBox.isSelected() );
        jEdit.setBooleanProperty( "options.sidekick.java.showVariables", showLocalVariablesCheckBox.isSelected() );
        jEdit.setBooleanProperty( "options.sidekick.java.showInitializers", showStaticInitializersCheckBox.isSelected() );
        jEdit.setBooleanProperty( "options.sidekick.java.showGeneralizations", showExtendsImplementsCheckBox.isSelected() );
        jEdit.setBooleanProperty( "options.sidekick.java.showThrows", showThrowsCheckBox.isSelected() );
        jEdit.setProperty( "sidekick.java.topLevelVisIndex", ( String ) topLevelVisibilityComboBox.getSelectedItem() );
        jEdit.setProperty( "sidekick.java.memberVisIndex", ( String ) memberVisibilityComboBox.getSelectedItem() );
    }
}