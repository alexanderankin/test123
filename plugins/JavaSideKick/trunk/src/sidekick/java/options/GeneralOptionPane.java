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
    private JRadioButton java7;
    private JRadioButton java8;

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
        JPanel errorPanel = new JPanel();
        errorPanel.setLayout( new BoxLayout( errorPanel, BoxLayout.X_AXIS ) );
        errorPanel.add( showErrorsInErrorList );
        errorPanel.add( ignoreDirtyBuffers );
        errorPanel.add( Box.createHorizontalGlue() );
        addComponent( errorPanel );
        addComponent( parseOnCodeComplete );
        addComponent( importPackage );
        java7 = new JRadioButton( "Java 7" );
        java8 = new JRadioButton( "Java 8" );
        ButtonGroup bg = new ButtonGroup();
        bg.add( java7 );
        bg.add( java8 );
        JPanel javaPanel = new JPanel();
        javaPanel.setLayout( new BoxLayout( javaPanel, BoxLayout.X_AXIS ) );
        javaPanel.add( new JLabel( jEdit.getProperty( "options.sidekick.java.javaParser", "Java parser: " ) ) );
        javaPanel.add( Box.createHorizontalStrut( 11 ) );
        javaPanel.add( java7 );
        javaPanel.add( Box.createHorizontalStrut( 6 ) );
        javaPanel.add( java8 );
        addComponent( javaPanel );
    }

    /**
     * Set/restore values from jEdit properties.
     */
    private void installDefaults() {
        showErrorsInErrorList.setSelected( jEdit.getBooleanProperty( "sidekick.java.showErrors", true ) );
        ignoreDirtyBuffers.setSelected( jEdit.getBooleanProperty( "sidekick.java.ignoreDirtyBuffers", true ) );
        parseOnCodeComplete.setSelected( jEdit.getBooleanProperty( "sidekick.java.parseOnComplete", true ) );
        importPackage.setSelected( jEdit.getBooleanProperty( "sidekick.java.importPackage", false ) );
        java7.setSelected( jEdit.getBooleanProperty( "sidekick.java.useJava7Parser", true ) );
        java8.setSelected( !jEdit.getBooleanProperty( "sidekick.java.useJava7Parser", false ) );
    }

    protected void _save() {
        jEdit.setBooleanProperty( "sidekick.java.showErrors", showErrorsInErrorList.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.ignoreDirtyBuffers", ignoreDirtyBuffers.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.parseOnComplete", parseOnCodeComplete.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.importPackage", importPackage.isSelected() );
        jEdit.setBooleanProperty( "sidekick.java.useJava7Parser", java7.isSelected() );
    }
}
