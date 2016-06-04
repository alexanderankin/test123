
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
        shellNames = new JComboBox<String>( new String[] {"System", "BeanShell"} );
        shellName = new JTextField();
        JButton okButton = new JButton( jEdit.getProperty( "common.ok" ) );
        okButton.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    String newName = getShellName();
                    String[] existingNames = Shell.getShellNames();
                    for ( String name : existingNames ) {
                        if ( newName.equals( name ) ) {
                            JOptionPane.showMessageDialog( NewShellDialog.this,
                            jEdit.getProperty( "console.userShells.shellExistsMsg", new String[] {name} ),
                            jEdit.getProperty( "console.userShells.shellExistsTitle", "Shell already exists" ),
                            JOptionPane.WARNING_MESSAGE );
                            return;
                        }
                    }
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
        GUIUtilities.makeSameSize( okButton, cancelButton );
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
