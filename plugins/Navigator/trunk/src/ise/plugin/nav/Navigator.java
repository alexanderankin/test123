package ise.plugin.nav;

import java.awt.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

/**
 * @version   $Revision$
 */
public class Navigator extends JPanel implements Navable {
   /** Description of the Field */
   private Nav nav = null;

   /** Description of the Field */
   private boolean on_toolbar = false;

   /** Description of the Field */
   private View view;

   /** Description of the Field */
   private Box toolbar;

   /**
    * Constructor for Navigator
    *
    * @param vw
    * @param position
    */
   public Navigator( View vw, String position ) {
      this.view = vw;

      // create a Nav and make sure the plugin knows about it
      nav = new Nav( this );
      NavigatorPlugin.addNavigator( view, this );

      // should the Nav be put on the view's toolbar?
      toolbar = view.getToolBar();
      on_toolbar = jEdit.getBooleanProperty( "ise.plugin.nav.Navigator.showOnToolbar", false );

      // control display of the Nav on the view's toolbar
      final JCheckBox cb = new JCheckBox( "Show on toolbar", on_toolbar );
      cb.addActionListener(
         new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
               on_toolbar = cb.isSelected();
               jEdit.setBooleanProperty( "ise.plugin.nav.Navigator.showOnToolbar", on_toolbar );
               Navigator.this.handleToolbar();
            }
         }
             );
      add( cb );

      // add a mouse listener to the view. Each mouse click on a text area in
      // the view is stored for the Nav.
      view.getTextArea().getPainter().addMouseListener(
         new MouseAdapter() {
            public void mouseClicked( MouseEvent ce ) {
               Buffer b = view.getTextArea().getBuffer();
               int cp = view.getTextArea().getCaretPosition();
               nav.update( new NavPosition( b, cp ) );
            }
         }
             );
      nav.update( new NavPosition( view.getTextArea().getBuffer(), view.getTextArea().getCaretPosition() ) );

   }

   /**
    * Sets the position of the cursor to the given NavPosition.
    *
    * @param o  The new NavPosition value
    */
   public void setPosition( Object o ) {
      NavPosition np = ( NavPosition )o;
      Buffer buffer = np.buffer;
      int caret = np.caret;
      if ( !buffer.equals( view.getTextArea().getBuffer() ) ) {
         try {
            buffer = jEdit.openFile( view, buffer.getFile().getAbsolutePath() );
         }
         catch ( Exception e ) {
            //e.printStackTrace();
            // return?
         }
      }
      if ( caret >= buffer.getLength() ) {
         caret = buffer.getLength() - 1;
      }
      if ( caret < 0 ) {
         caret = 0;
      }
      view.getTextArea().setCaretPosition( caret, true );
      view.getTextArea().requestFocus();
   }


   /**
    * Gets the Nav attribute of the Navigator object
    *
    * @return   The Nav value
    */
   public Nav getNav() {
      return nav;
   }

   /** Handles the toolbar on first loading of this panel. */
   public void addNotify() {
      super.addNotify();
      handleToolbar();
   }

   /** go back one in the history list */
   public void goBack() {
      nav.goBack();
   }

   /** go forward one in the history list */
   public void goForward() {
      nav.goForward();
   }

   /** Description of the Method */
   public void handleToolbar() {
      if ( on_toolbar ) {
         remove( nav );
         toolbar.remove( nav );
         toolbar.add( nav );
      }
      else {
         toolbar.remove( nav );
         remove( nav );
         add( nav );
      }
      SwingUtilities.invokeLater(
         new Runnable() {
            public void run() {
               Navigator.this.repaint();
               view.repaint();
            }
         } );
   }
}

