package ise.plugin.nav;

import javax.swing.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * @author Dale Anson
 */
public class OptionPanel extends AbstractOptionPane {

   private JCheckBox showOnToolbar = null;

   public OptionPanel( String name ) {
      super( name );
   }

   public void _init() {
	   String[] stackChoices = new String[] {
		     jEdit.getProperty("navigator.back.label"),
		     jEdit.getProperty("navigator.forward.label") };

	      addComponent( new JLabel( "<html><h3>Navigator</h3>" ) );
	      showOnToolbar = new JCheckBox( "Show Navigator on toolbar (buggy)" );
	      
   }
   

   public void _save() {
	   boolean useToolBars = showOnToolbar.isSelected();
	   jEdit.setBooleanProperty( getName() + ".showOnToolbar", useToolBars  );
   }

   public String getName() {
      return "navigator";
   }
}
