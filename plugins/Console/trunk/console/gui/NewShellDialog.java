
package console.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.StringList;
import org.gjt.sp.util.GenericGUIUtilities;

import console.ConsolePlugin;
import console.Shell;

public class NewShellDialog extends JDialog {

    JComboBox<String> shellNames;
    JTextField shellName;


    public NewShellDialog() {
        super( jEdit.getActiveView(), jEdit.getProperty( "console.newshell.createNewShell", "Create New Shell" ), true );
        /*
         * It would be nice to be able to list all existing shell names, but
         * the ServiceManager does not expose the code used to create the
         * other shells. The only code known, then, is the code to create a
         * new System shell or new BeanShell shell, so these choices are
         * hard coded here. Note that the shell names "BeanShell" and "System"
         * are also hard coded into their respective constructors, so no need
         * to load these names from properties.
         */
        shellNames = new JComboBox<String>( Shell.getBaseShellNames());//new String[] {"System", "BeanShell"} );
        shellName = new JTextField();
        JButton okButton = new JButton( jEdit.getProperty( "common.ok" ) );
        okButton.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    // check the name, it must not be empty, must not already exist,
                    // and cannot contain a comma
                    String newName = getShellName();
                    if (newName == null || newName.isEmpty()) {
                        JOptionPane.showMessageDialog(NewShellDialog.this, 
                            jEdit.getProperty("console.newshell.emptyName.msg", "Name must not be empty."), 
                            jEdit.getProperty("console.newshell.emptyName.title", "Missing Name"), 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (newName.indexOf(',') > -1) {
                        JOptionPane.showMessageDialog(NewShellDialog.this, 
                            jEdit.getProperty("console.newshell.hasComma.msg", "Name must not contain a comma."), 
                            jEdit.getProperty("console.newshell.hasComma.title", "Illegal Name"), 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String[] existingNames = Shell.getShellNames();
                    for ( String name : existingNames ) {
                        if ( newName.equals( name ) ) {
                            JOptionPane.showMessageDialog( NewShellDialog.this,
                                jEdit.getProperty( "console.newshell.shellExists.msg", new String[] {name} ),
                                jEdit.getProperty( "console.newshell.shellExists.title", "Shell already exists" ),
                                JOptionPane.ERROR_MESSAGE );
                            return;
                        }
                    }
                    
                    // create the shell
					String name = getShellName();
					String type = getShellType();
					if (name == null || type == null)
						return;
					String code = ConsolePlugin.getBaseShellCode(type);
					if (code == null) {
						ConsolePlugin.getBaseShellPluginJAR(type).activatePlugin();
						ConsolePlugin.loadBaseShells();
						code = ConsolePlugin.getBaseShellCode(type);
						if (code == null) {
							JOptionPane.showMessageDialog(NewShellDialog.this,
							    jEdit.getProperty("console.newshell.unableToCreateShell.msg", new String[] {name} ),
							    jEdit.getProperty("console.newshell.unableToCreateShell.title", "Unable to create shell"),
							    JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					// possibly replace the name in the beanshell code, 
					// e.g. change: new javascriptshell.JavaScriptShell("JavaScript");
					// to:          new javascriptshell.JavaScriptShell("name");
					if (code.indexOf('\"') > 0) {
						code = code.replaceAll("\".*?\"", '\"' + name + '\"');		
					}
					
					// create a new shell service
					ServiceManager.registerService(Shell.SERVICE, name, code, ConsolePlugin.getBaseShellPluginJAR(type));
					Shell shell = (Shell)ServiceManager.getService(Shell.SERVICE, name);
					shell.setName(name);
					shell.setIsUserShell(true);
					StringList userShells = StringList.split(jEdit.getProperty("console.userShells"), ",");
					userShells.add(name);
					jEdit.setProperty("console.userShells", userShells.join(","));
					jEdit.setProperty("console.userShells." + name + ".code", code);
                    
                    setVisible( false );
                    dispose();
                }
            }
        );
        JButton cancelButton = new JButton( jEdit.getProperty( "common.cancel" ) );
        cancelButton.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    NewShellDialog.this.setVisible( false );
                    NewShellDialog.this.dispose();
                }
            }
        );
        GenericGUIUtilities.makeSameSize( okButton, cancelButton );
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 11, 11, 12, 12 ) );
        AbstractOptionPane dialogPanel = new AbstractOptionPane( jEdit.getProperty( "console.newshell.createNewShell", "Create New Shell" ) );
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );

        dialogPanel.addComponent( jEdit.getProperty( "console.newshell.selectShellType", "Select Shell Type:" ), shellNames );
        dialogPanel.addComponent( jEdit.getProperty( "console.newshell.shellName", "Enter Shell Name:" ), shellName );

        buttonPanel.add( okButton );
        buttonPanel.add( cancelButton );

        panel.add( dialogPanel, BorderLayout.CENTER );
        panel.add( buttonPanel, BorderLayout.SOUTH );
        setContentPane( panel );

        // pack and center on view
        pack();
        Rectangle v = jEdit.getActiveView().getBounds();
        Dimension size = getSize();
        int x = v.x + ( v.width - size.width ) / 2;
        if ( x < 0 ) {
            x = 0;
        }

        int y = v.y + ( v.height - size.height ) / 2;
        if ( y < 0 ) {
            y = 0;
        }

        setLocation( x, y );
    }


    public String getShellType() {
        return ( String )shellNames.getSelectedItem();
    }


    public String getShellName() {
        return shellName.getText();
    }
}
