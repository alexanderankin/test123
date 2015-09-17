package ise.plugin.bmp;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * @author Dale Anson
 */
public class OptionPanel extends AbstractOptionPane {

    private JCheckBox removeStale = null;
    private JSpinner spinner = null;
    private JCheckBox whileActive = null;

    public OptionPanel( String name ) {
        super( name );
    }

    public void _init() {
        setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6 ) );

        // setName("BufferLocalOptions");
        addSeparator("bufferlocal.options.title");
        removeStale = new JCheckBox( jEdit.getProperty( "bufferlocal.options.removeStale" ) );
        removeStale.setSelected( jEdit.getBooleanProperty( "bufferlocal.removeStale", false ) );
        addComponent( removeStale );
        int stale_value = jEdit.getIntegerProperty( "bufferlocal.staleTime", 30 );
        if ( stale_value < 1 ) {
            stale_value = 30;
            jEdit.setIntegerProperty( "bufferlocal.staleTime", 30 );
        }
        spinner = new JSpinner( new SpinnerNumberModel( stale_value, 1, Integer.MAX_VALUE, 1 ) );
        addComponent( jEdit.getProperty( "bufferlocal.options.time" ), spinner );

        whileActive = new JCheckBox( jEdit.getProperty( "bufferlocal.options.whileActive" ) );
        whileActive.setSelected( jEdit.getBooleanProperty( "bufferlocal.whileActive", false ) );
        addComponent( whileActive );

    }

    public void _save() {
        jEdit.setBooleanProperty( "bufferlocal.removeStale", removeStale.isSelected() );
        jEdit.setIntegerProperty( "bufferlocal.staleTime", ( ( Number ) spinner.getValue() ).intValue() );
        jEdit.setBooleanProperty( "bufferlocal.whileActive", whileActive.isSelected() );
    }
}