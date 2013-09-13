package activator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import org.gjt.sp.jedit.jEdit;
import ise.java.awt.KappaLayout;

/**
 * Dialog to allow the user to select additional plugins to load.
 */
public class DependentSelectionDialog extends JDialog {

    private Set<JCheckBox> plugins;
    private Collection<Plugin> choices = new HashSet<Plugin>();
    private JButton okButton;
    private JButton cancelButton;

    public DependentSelectionDialog( Collection<Plugin> values ) {
        super( jEdit.getActiveView(), jEdit.getProperty( "activator.dependentpluginsdialog.title", "Select Plugins to Load" ), true );
        createUI( values );
        addActions();
        GUIUtils.center(jEdit.getActiveView(), this);
        setVisible( true );
        
    }

    private void createUI( Collection<Plugin> values ) {
        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JLabel title = new JLabel( jEdit.getProperty("activator.dependentpluginsdialog.heading", "You may want to also load these plugins:") );
        panel.add( title, "0, 0, 1, 1, W, wh, 0" );

        plugins = new HashSet<JCheckBox>();
        JPanel cbPanel = new JPanel( new KappaLayout() );
        cbPanel.setBorder(new EmptyBorder(6, 21, 6, 21));
        int row = 0;
        KappaLayout.Constraints con = KappaLayout.createConstraint();
        con.s = "wh";
        con.p = 3;
        for ( Plugin p : values ) {
            JCheckBox cb = new JCheckBox( p.toString() );
            cb.putClientProperty( "plugin", p );
            cb.setSelected(true);
            plugins.add( cb );
            con.y = row;
            cbPanel.add( cb, con );
            ++ row;
        }
        panel.add( cbPanel, "0, 1, 1, 1, , wh, 0" );

        KappaLayout buttonLayout = new KappaLayout();
        JPanel btnPanel = new JPanel( buttonLayout );
        btnPanel.setBorder(new EmptyBorder(6, 6, 11, 6));
        okButton = new JButton( jEdit.getProperty( "common.ok" ) );
        cancelButton = new JButton( jEdit.getProperty( "common.cancel" ) );
        btnPanel.add( okButton, "0, 0, 1, 1, E, , 3" );
        btnPanel.add( cancelButton, "1, 0, 1, 1, W, , 3" );
        buttonLayout.makeColumnsSameWidth( 0, 1 );
        panel.add( btnPanel, "0, 2, 1, 1, E, , 0" );

        setContentPane( panel );
        pack();
    }

    private void addActions() {
        okButton.addActionListener ( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                for ( JCheckBox cb : plugins ) {
                    if ( cb.isSelected() ) {
                        choices.add( ( Plugin ) cb.getClientProperty( "plugin" ) );
                    }
                }
                DependentSelectionDialog.this.close();
            }
        }
        );

        cancelButton.addActionListener ( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                DependentSelectionDialog.this.close();
            }
        }
        );
    }

    private void close() {
        setVisible( false );
        dispose();
    }

    public Collection<Plugin> getChoices() {
        return choices;
    }

}