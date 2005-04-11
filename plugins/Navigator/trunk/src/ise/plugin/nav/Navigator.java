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
   
   public Navigator(View view) {
        this(view, "");   
   }

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
      add( nav );

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
      NavPosition np = ( NavPosition ) o;
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

   /** go back one in the history list */
   public void goBack() {
      nav.goBack();
   }

   /** go forward one in the history list */
   public void goForward() {
      nav.goForward();
   }
   
}

