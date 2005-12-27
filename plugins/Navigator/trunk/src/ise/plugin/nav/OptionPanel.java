package ise.plugin.nav;

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

   private JCheckBox showOnToolbar = null;

   private JComboBox onClose = null;
   public OptionPanel( String name ) {
      super( name );
   }

   public void _init() {
	   String[] stackChoices = new String[] {
		     jEdit.getProperty("navigator.back.label"),
		     jEdit.getProperty("navigator.forward.label") };

	      addComponent( new JLabel( "<html><h3>Navigator</h3>" ) );
	      showOnToolbar = new JCheckBox( "Show Navigator on toolbar" );
	      
	      onClose= new JComboBox(stackChoices); 
	      
	      onClose.setSelectedIndex(jEdit.getIntegerProperty("navigator.onclose", 0));
	      
	      showOnToolbar.setSelected( jEdit.getBooleanProperty( getName() + ".showOnToolbar", false ) );
	      addComponent( showOnToolbar );
	      addComponent( "On buffer close, push position: ", onClose);
   }
   

   public void _save() {
	   boolean useToolBars = showOnToolbar.isSelected();
	   jEdit.setBooleanProperty( getName() + ".showOnToolbar", useToolBars  );
	   int pushForward = onClose.getSelectedIndex();
	   NavigatorPlugin.pushForward = (pushForward>0);
	   jEdit.setIntegerProperty("navigator.onclose", pushForward);
	   NavigatorPlugin.setToolBars();
   }

   public String getName() {
      return "navigator";
   }
}
