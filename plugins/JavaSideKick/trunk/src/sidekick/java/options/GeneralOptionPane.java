package sidekick.java.options;

import javax.swing.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class GeneralOptionPane extends AbstractOptionPane {

    JLabel titleLabel = new JLabel( jEdit.getProperty( "options.sidekick.java.generalOptions", "<html><b>General Options</b>" ) );
    private JCheckBox showErrorsInErrorList;
    private JCheckBox ignoreDirtyBuffers;
    private JCheckBox parseOnCodeComplete;
    private JCheckBox importPackage;
    private JCheckBox useAntlrParser;   // TODO: remove this and references below when the antlr parser is complete

    public GeneralOptionPane() {
        super( "sidekick.java.general" );
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
        ignoreDirtyBuffers = new JCheckBox( jEdit.getProperty( "options.sidekick.java.ignoreDirtyBuffers", "Ignore dirty buffers" ) );
        parseOnCodeComplete = new JCheckBox( jEdit.getProperty( "options.sidekick.java.parseOnComplete", "Parse buffer on code completion" ) );
        importPackage = new JCheckBox( jEdit.getProperty( "options.sidekick.java.importPackage", "Insert import statement on package completion" ) );
        useAntlrParser = new JCheckBox( "Use Antlr parser" );
        JPanel errorPanel = new JPanel();
        errorPanel.setLayout( new BoxLayout( errorPanel, BoxLayout.X_AXIS ) );
        errorPanel.add( showErrorsInErrorList );
        errorPanel.add( ignoreDirtyBuffers );
        errorPanel.add( Box.createHorizontalGlue() );
        addComponent( errorPanel );
        addComponent( parseOnCodeComplete );
        addComponent( importPackage );
        addComponent( useAntlrParser );
    }

    /**
     * Set/restore values from jEdit properties.
     */
    private void installDefaults() {
        showErrorsInErrorList.setSelected( jEdit.getBooleanProperty( "sidekick.java.showErrors", true ) );
        ignoreDirtyBuffers.setSelected( jEdit.getBooleanProperty( "sidekick.java.ignoreDirtyBuffers", true ) );
        parseOnCodeComplete.setSelected( jEdit.getBooleanProperty( "sidekick.java.parseOnComplete", true ) );
        importPackage.setSelected( jEdit.getBooleanProperty( "sidekick.java.importPackage", false ) );
        useAntlrParser.setSelected( jEdit.getBooleanProperty( "sidekick.java.useAntlrParser", false ) );
    }

    protected void _save() {
        jEdit.setBooleanProperty( "sidekick.java.showErrors", showErrorsInErrorList.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.ignoreDirtyBuffers", ignoreDirtyBuffers.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.parseOnComplete", parseOnCodeComplete.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.importPackage", importPackage.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.useAntlrParser", useAntlrParser.isSelected() );
    }
}
