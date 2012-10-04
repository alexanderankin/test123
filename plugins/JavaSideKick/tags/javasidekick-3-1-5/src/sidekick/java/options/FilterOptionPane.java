package sidekick.java.options;

import java.awt.FlowLayout;
import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * Option pane to let the user choose what to display in the sidekick tree.
 */
public class FilterOptionPane extends AbstractOptionPane {

    private JLabel titleLabel = new JLabel( jEdit.getProperty("options.sidekick.java.filterOptions", "<html><b>Filter Options:</b> What to include") );

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
        super( "sidekick.java.filter" );
    }

    protected void _init() {
        installComponents();
        installDefaults();
    }

    /**
     * Create and add all GUI components.
     */
    private void installComponents() {
        setBorder( BorderFactory.createEmptyBorder( 11, 11, 12, 12 ) );

        attributesCheckBox = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showAttr", "Fields" ) );
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
        addComponent( showExtendsImplementsCheckBox );
        addComponent( showImportsCheckBox );
        JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
        panel.add( attributesCheckBox );
        panel.add( Box.createHorizontalStrut( 6 ) );
        panel.add( includePrimitivesCheckBox );
        addComponent( panel );
        addComponent( showStaticInitializersCheckBox );
        addComponent( showThrowsCheckBox );
        addComponent( showLocalVariablesCheckBox );

        addComponent( Box.createVerticalStrut( 11 ) );
        addComponent( visibilityLevelLabel );
        addComponent( Box.createVerticalStrut( 6 ) );
        addComponent( jEdit.getProperty( "options.sidekick.java.topLevelVis", "Top level:" ), topLevelVisibilityComboBox );
        addComponent( jEdit.getProperty( "options.sidekick.java.memberVis", "Member:" ), memberVisibilityComboBox );
    }

    /**
     * Set/restore values from jEdit properties.
     */
    private void installDefaults() {
        attributesCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showAttr", true ) );
        includePrimitivesCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showPrimAttr", true ) );
        showImportsCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showImports", true ) );
        showLocalVariablesCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showVariables", false ) );
        showStaticInitializersCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showInitializers", false ) );
        showExtendsImplementsCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showGeneralizations", false ) );
        showThrowsCheckBox.setSelected( jEdit.getBooleanProperty( "sidekick.java.showThrows", false ) );
        topLevelVisibilityComboBox.setSelectedIndex(jEdit.getIntegerProperty("sidekick.java.topLevelVisIndex", 1));
        memberVisibilityComboBox.setSelectedIndex(jEdit.getIntegerProperty("sidekick.java.memberVisIndex", OptionValues.PRIVATE));
    }


    protected void _save() {
        jEdit.setBooleanProperty( "sidekick.java.showAttr", attributesCheckBox.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.showPrimAttr", includePrimitivesCheckBox.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.showImports", showImportsCheckBox.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.showVariables", showLocalVariablesCheckBox.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.showInitializers", showStaticInitializersCheckBox.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.showGeneralizations", showExtendsImplementsCheckBox.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.showThrows", showThrowsCheckBox.isSelected() );
        jEdit.setIntegerProperty( "sidekick.java.topLevelVisIndex", topLevelVisibilityComboBox.getSelectedIndex() );
        jEdit.setIntegerProperty( "sidekick.java.memberVisIndex", memberVisibilityComboBox.getSelectedIndex() );
    }
}