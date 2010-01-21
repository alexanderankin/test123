
package sidekick.java.options;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.msg.PropertiesChanged;

/**
 * This is a toolbar to be added to SideKick. It provides the ability to sort,
 * change visibility, and toggle line numbers on and off without having to open
 * the plugin option panes.
 */
public class JavaModeToolBar extends JPanel {

    private JMenuItem byLine;
    private JMenuItem byName;
    private JMenuItem byVisibility;
    private JMenuItem privateMI;
    private JMenuItem packageMI;
    private JMenuItem protectedMI;
    private JMenuItem publicMI;
    private JCheckBoxMenuItem toggleLineNumbers;
    
    private EBComponent parent;
    private OptionValues optionValues;
    
    /**
     * @param parent An EBComponent to include with PropertyChanged messages.
     * This will be a JavaParser.
     */
    public JavaModeToolBar( EBComponent parent ) {
        this.parent = parent;
        optionValues = new OptionValues();
        installComponents();
        installListeners();
    }

    private void installComponents() {
        JMenuBar menuBar = new JMenuBar();

        JMenu sortMenu = new JMenu( jEdit.getProperty("options.sidekick.java.sortBy", "Sorting") );
        byLine = new JMenuItem( jEdit.getProperty("options.sidekick.java.sortByLine", "Line") );
        byName = new JMenuItem( jEdit.getProperty("options.sidekick.java.sortByName", "Name") );
        byVisibility = new JMenuItem( jEdit.getProperty("options.sidekick.java.sortByVisibility", "Visibility") );

        JMenu visibilityMenu = new JMenu( jEdit.getProperty("options.sidekick.java.sortByVisibility", "Visibility") );
        privateMI = new JMenuItem( "private" );
        packageMI = new JMenuItem( "package" );
        protectedMI = new JMenuItem( "protected" );
        publicMI = new JMenuItem( "public" );

        toggleLineNumbers = new JCheckBoxMenuItem( jEdit.getProperty("options.sidekick.java.showLineNums", "Line Numbers") );
        toggleLineNumbers.setSelected( optionValues.getShowLineNum() );

        add( menuBar );
        menuBar.add( sortMenu );
        menuBar.add( visibilityMenu );

        sortMenu.add( byLine );
        sortMenu.add( byName );
        sortMenu.add( byVisibility );

        visibilityMenu.add( privateMI );
        visibilityMenu.add( packageMI );
        visibilityMenu.add( protectedMI );
        visibilityMenu.add( publicMI );

        menuBar.add( toggleLineNumbers );
    }

    private void installListeners() {
        byLine.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    jEdit.setIntegerProperty( "sidekick.java.sortBy", OptionValues.SORT_BY_LINE );
                    EditBus.send( new PropertiesChanged( parent ) );
                }
            }
        );

        byName.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    jEdit.setIntegerProperty( "sidekick.java.sortBy", OptionValues.SORT_BY_NAME );
                    EditBus.send( new PropertiesChanged( parent ) );
                }
            }
        );

        byVisibility.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    jEdit.setIntegerProperty( "sidekick.java.sortBy", OptionValues.SORT_BY_VISIBILITY );
                    EditBus.send( new PropertiesChanged( parent ) );
                }
            }
        );

        privateMI.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    jEdit.setIntegerProperty( "sidekick.java.memberVisIndex", OptionValues.PRIVATE );
                    EditBus.send( new PropertiesChanged( parent ) );
                }
            }
        );

        packageMI.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    jEdit.setIntegerProperty( "sidekick.java.memberVisIndex", OptionValues.PROTECTED );
                    EditBus.send( new PropertiesChanged( parent ) );
                }
            }
        );

        protectedMI.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    jEdit.setIntegerProperty( "sidekick.java.memberVisIndex", OptionValues.PROTECTED );
                    EditBus.send( new PropertiesChanged( parent ) );
                }
            }
        );

        publicMI.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    jEdit.setIntegerProperty( "sidekick.java.memberVisIndex", OptionValues.PUBLIC );
                    EditBus.send( new PropertiesChanged( parent ) );
                }
            }
        );

        toggleLineNumbers.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    jEdit.setBooleanProperty( "sidekick.java.showLineNums", toggleLineNumbers.isSelected() );
                    EditBus.send( new PropertiesChanged( parent ) );
                }
            }
        );
    }
}