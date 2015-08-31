/*
* LookAndFeelOptionPane.java - plugin options pane for LookAndFeel plugin
* (c) 2001, 2002 Dirk Moebius
*
* :mode=java:tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package lookandfeel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;

/**
 * This is the option pane that jEdit displays for the options of
 * the LookAndFeel plugin.
 */
public class LookAndFeelOptionPane extends AbstractOptionPane implements ItemListener {

    private JComboBox lookAndFeels;
    private JPanel lnfOptionPanel;
    private AbstractOptionPane configComponent;
    private JCheckBox useFont;
    private JCheckBox allowBeep = null;

    public LookAndFeelOptionPane() {
        super( "lookandfeel" );
    }

    public void _init() {
        setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6 ) );
        String[] lnfs = LookAndFeelPlugin.getAvailableLookAndFeels();
        lookAndFeels = new JComboBox( lnfs );
        addComponent( useFont = new JCheckBox( jEdit.getProperty( "lookandfeel.usejeditfont.label" ), jEdit.getBooleanProperty( "lookandfeel.usejeditfont", false ) ) );
        addComponent( Box.createVerticalStrut(6 ) );

        if ( "true".equals( System.getProperty( "LNFAgentInstalled" ) ) ) {
            addComponent( allowBeep = new JCheckBox( jEdit.getProperty( "lookandfeel.allowBeep.label" ), "true".equals(System.getProperty("allowBeep") ) ) );
            addComponent( Box.createVerticalStrut(6 ) );
        }

        addComponent( jEdit.getProperty( "lookandfeel.lookandfeel.label" ), lookAndFeels );
        addComponent( Box.createVerticalStrut(6 ) );
        addComponent( lnfOptionPanel = new JPanel( new BorderLayout() ) );
        addComponent( Box.createVerticalStrut(6 ) );
        JButton button = new JButton( jEdit.getProperty( "lookandfeel.chooseeditorscheme.label" ) );
        button.setToolTipText( jEdit.getProperty( "lookandfeel.chooseeditorschemetooltip.label" ) );
        addComponent( button );
        button.addActionListener ( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                new editorscheme.EditorSchemeSelectorDialog( jEdit.getActiveView() );
            }
        }
        );

        addComponent( Box.createVerticalStrut(11 ) );
        addSeparator();
        addComponent( Box.createVerticalStrut(11 ) );
        addComponent( new JLabel( jEdit.getProperty( "lookandfeel.message.restart.message" ) ) );

        int idx = indexOf( lnfs, jEdit.getProperty( "lookandfeel.lookandfeel" ) );
        lookAndFeels.setSelectedIndex( idx < 0 ? 0 : idx );
        itemStateChanged( null );
        lookAndFeels.addItemListener( this );

    }

    public void _save() {
        try {
            if ( configComponent != null ) {
                configComponent.save();
            }
            jEdit.setProperty( "lookandfeel.lookandfeel", lookAndFeels.getSelectedItem().toString() );
            jEdit.setBooleanProperty( "lookandfeel.usejeditfont", useFont.isSelected() );
            if (allowBeep != null) {
            	System.setProperty("allowBeep", allowBeep.isSelected() ? "true" : "false");
            }
            LookAndFeelInstaller installer = LookAndFeelPlugin.getInstaller( lookAndFeels.getSelectedItem().toString() );
            if ( installer != null ) {
                LookAndFeelPlugin.installLookAndFeel( installer );
            }
        } catch ( Exception e ) {
            Log.log( Log.ERROR, this, e );
            GUIUtilities.error( this, "lookandfeel.error.installer", null );
        }
    }

    /**
     * Handle a change in the combo box.
     */
    public final void itemStateChanged( ItemEvent evt ) {
        try {
            LookAndFeelInstaller installer = LookAndFeelPlugin.getInstaller( lookAndFeels.getSelectedItem().toString() );
            if ( installer == null ) {
                return;
            }
            if ( configComponent != null ) {
                lnfOptionPanel.remove( configComponent );
            }
            configComponent = installer.getOptionPane();
            if ( configComponent != null ) {
                lnfOptionPanel.add( configComponent );
            }
            invalidate();
            revalidate();
            repaint();
        } catch ( Exception e ) {
            Log.log( Log.ERROR, this, e );
            GUIUtilities.error( this, "lookandfeel.error.installer", null );
        }
    }

    /**
     * Returns the index of a string in the given string array.
     */
    private static int indexOf( String[] arr, String s ) {
        for ( int i = 0; i < arr.length; i++ ) {
            if ( arr[ i].equals( s ) ) {
                return i;
            }
        }
        return -1;
    }

}