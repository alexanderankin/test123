package ise.plugin.bmp;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EditPlugin;


/**
 * @author Dale Anson
 */
public class OptionPanel extends AbstractOptionPane {

   private JCheckBox removeStale = null;
   private JSpinner spinner = null;

   public OptionPanel( String name ) {
      super( name );
   }

   public void _init() {
      addComponent( new JLabel( "<html><h3>BufferLocal</h3>" ) );
      removeStale = new JCheckBox( "Close files not used" );
      removeStale.setSelected( jEdit.getBooleanProperty( getName() + ".removeStale", false ) );
      addComponent( removeStale );
      int stale_value = jEdit.getIntegerProperty( getName() + ".staleTime", 30 );
      spinner = new JSpinner( new SpinnerNumberModel( stale_value, 1, Integer.MAX_VALUE, 1 ) );
      addComponent( "for this many minutes", spinner );
   }

   public void _save() {
      jEdit.setBooleanProperty( getName() + ".removeStale", removeStale.isSelected() );
      jEdit.setIntegerProperty( getName() + ".staleTime", ( ( Number ) spinner.getValue() ).intValue() );
      EditPlugin plugin = jEdit.getPlugin( "ise.plugin.bmp.BufferLocal" );
      plugin.stop();
      plugin.start();
   }

   public String getName() {
      return "bufferlocal";
   }
}
