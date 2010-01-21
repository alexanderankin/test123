package sidekick.java.options;

import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class GeneralOptionPane extends AbstractOptionPane {

    // TODO: put label text in a property
    JLabel titleLabel = new JLabel( jEdit.getProperty(" -- these are obsolete", "<html><b>General Options</b>") );
    private JCheckBox showErrorsInErrorList;

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
        addComponent( showErrorsInErrorList );
    }

    /**
     * Set/restore values from jEdit properties.    
     */
    private void installDefaults() {
        showErrorsInErrorList.setSelected( jEdit.getBooleanProperty( "sidekick.java.showErrors", true ) );
    }

    protected void _save() {
        jEdit.setBooleanProperty( "sidekick.java.showErrors", showErrorsInErrorList.isSelected() );
    }
}