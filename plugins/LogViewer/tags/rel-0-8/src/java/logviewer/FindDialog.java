package logviewer;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;

import ghm.follow.OutputDestinationComponent;

/**
 * A dialog for searching in the output panel. Dialog is automatically centered
 * on the screen.
 *
 * @author    Dale Anson, danson@germane-software.com
 * @version   $Revision$
 */
public class FindDialog extends JDialog {

   private OutputDestinationComponent textarea = null;


   /**
    * Constructor for FindDialog
    *
    * @param ta      JTextComponent to search in.
    */
   public FindDialog( OutputDestinationComponent ta ) {
      super( new JFrame(), "Find in LogViewer", true );
      this.textarea = ta;
      textarea.requestFocus();

      JPanel panel = new JPanel();
      KappaLayout layout = new KappaLayout();
      panel.setLayout( layout );
      panel.setBorder( new javax.swing.border.EmptyBorder( 11, 11, 11, 11 ) );
      setContentPane( panel );

      JLabel find_label = new JLabel( "Find:" );
      final JTextField to_find = new JTextField( 20 );
      JButton find_btn = new JButton( "Find" );
      JButton find_next_btn = new JButton( "Find Next" );
      JButton cancel_btn = new JButton( "Close" );
      final JCheckBox wrap_cb = new JCheckBox( "Wrap search" );
      
      panel.add( find_label, "0, 0, 1, 1, W, w, 3" );
      panel.add( to_find, "0, 1, 1, 1, 0, w, 3" );
      panel.add( wrap_cb, "0, 2, 1, 1, 0, w, 3" );

      JPanel btn_panel = new JPanel( new KappaLayout() );
      btn_panel.add( find_btn, "0, 0, 1, 1, 0, w, 3" );
      btn_panel.add( find_next_btn, "0, 1, 1, 1, 0, w, 3" );
      btn_panel.add( cancel_btn, "0, 2, 1, 1, 0, w, 3" );
      panel.add( btn_panel, "1, 0, 1, 3, 0, h, 5" );

      find_btn.addActionListener(
         new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
               String text_to_find = to_find.getText();
               if ( text_to_find == null || text_to_find.length() == 0 ) {
                  return;
               }
               textarea.find(text_to_find);
            }
         } );
      find_next_btn.addActionListener(
         new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
               String text_to_find = to_find.getText();
               if ( text_to_find == null || text_to_find.length() == 0 ) {
                  return;
               }
               textarea.findNext(text_to_find);
            }
         } );

      wrap_cb.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent ae) {
            textarea.setWrapFind(wrap_cb.isSelected());   
          }
      });
      wrap_cb.setSelected(true);
      textarea.setWrapFind(true);

      cancel_btn.addActionListener(
         new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
               setVisible( false );
               dispose();
            }
         } );
      pack();
      Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension window_size = getSize();
      setBounds( ( screen_size.width - window_size.width ) / 2,
            ( screen_size.height - window_size.height ) / 2,
            window_size.width,
            window_size.height );
      to_find.requestFocus();
   }
}

