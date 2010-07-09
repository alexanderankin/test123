package sidekick.java.options;

import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class GeneralOptionPane extends AbstractOptionPane {

    JLabel titleLabel = new JLabel( jEdit.getProperty("options.sidekick.java.generalOptions", "<html><b>General Options</b>") );
    private JCheckBox showErrorsInErrorList;
    private JCheckBox parseOnCodeComplete;
    private JCheckBox importPackage;

    public GeneralOptionPane() {
        super( jEdit.getProperty("options.sidekick.java.general.label", "General") );
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
        addComponent( titleLabel );
        addComponent( Box.createVerticalStrut( 11 ) );
        showErrorsInErrorList = new JCheckBox( jEdit.getProperty( "options.sidekick.java.showErrors", "Show parse errors in ErrorList" ) );
        parseOnCodeComplete = new JCheckBox( jEdit.getProperty( "options.sidekick.java.parseOnComplete", "Parse buffer on code completion" ) );
        importPackage = new JCheckBox( jEdit.getProperty( "options.sidekick.java.importPackage", "Insert import statement on package completion") );
        addComponent( showErrorsInErrorList );
        addComponent( parseOnCodeComplete );
        addComponent( importPackage );
    }

    /**
     * Set/restore values from jEdit properties.    
     */
    private void installDefaults() {
        showErrorsInErrorList.setSelected( jEdit.getBooleanProperty( "sidekick.java.showErrors", true ) );
        parseOnCodeComplete.setSelected( jEdit.getBooleanProperty( "sidekick.java.parseOnComplete", true) );
        importPackage.setSelected( jEdit.getBooleanProperty( "sidekick.java.importPackage", false) );
    }

    protected void _save() {
        jEdit.setBooleanProperty( "sidekick.java.showErrors", showErrorsInErrorList.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.parseOnComplete", parseOnCodeComplete.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.importPackage", importPackage.isSelected() );
    }
}