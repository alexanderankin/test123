
package bigdoc;


import ise.java.awt.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import java.util.prefs.*;
import java.util.regex.*;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.text.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;

// TODO: figure out why this locks up jEdit when attempting to open an mkv file.
public class BigDoc extends JPanel implements PropertyChangeListener, CaretListener {

    private View view;
    private JTextArea textArea;
    private BigTextDocument doc;
    private JMenuItem open_mi = new JMenuItem( "Open File" );
    private JMenuItem find_mi = new JMenuItem( "Find" );
    private JLabel statusLabel;
    private File file;  // current file being displayed

    public BigDoc( View view ) {
        super();
        this.view = view;
        init();
    }

    public void init() {
        initGUI();
        initActions();
    }

    private void initGUI() {
        setLayout( new BorderLayout() );

        JPanel topPanel = new JPanel();
        topPanel.setLayout( new KappaLayout() );

        JMenuBar bar = new JMenuBar();
        topPanel.add( bar, "0, 0, 1, 1, W, w, 3" );
        JMenu file_menu = new JMenu( "File" );
        file_menu.add( open_mi );
        file_menu.add( find_mi );
        bar.add( file_menu );

        statusLabel = new JLabel();
        topPanel.add( statusLabel, "1, 0, 6, 1, E, w, 3" );
        add( topPanel, BorderLayout.NORTH );

        textArea = new JTextArea();
        textArea.setEditable( true );    // true even though document is read only, this allows the caret to be visible and move
        textArea.addCaretListener(this);
        add( new JScrollPane( textArea ), BorderLayout.CENTER );
    }

    private void initActions() {
        open_mi.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    String[] files = GUIUtilities.showVFSFileDialog( view, null, VFSBrowser.OPEN_DIALOG, false );
                    if ( files != null && files.length == 1 ) {
                        load( files[0] );
                    }
                }
            }
        );

        find_mi.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    FindAndReplace find = new FindAndReplace( view, FindAndReplace.FIND, textArea );

                    center( BigDoc.this, find );
                    find.setVisible( true );
                }
            }
        );
    }

    /**
     * Loads the editor with the contents of the given file.
     */
    public void load( String filename ) {
        final File file = new File( filename );
        if ( file == null ) {
            return;
        }
        if ( file.isDirectory() ) {
            return;
        }
        this.file = file;
        SwingUtilities.invokeLater( new Runnable(){

                public void run() {
                    try {

                        textArea.setText( "Loading..." );
                        statusLabel.setText( "Loading " + file.getName() );
                        doc = new BigTextDocument( filename, BigDoc.this );
                    }
                    catch ( Exception e ) {
                        JOptionPane.showMessageDialog( view, e.getMessage(), "Error loading file", JOptionPane.ERROR_MESSAGE );
                        textArea.setText( "" );
                    }
                }
            } );
    }

    public void propertyChange( PropertyChangeEvent event ) {
        if ( "lineLoader".equals( event.getPropertyName() ) && "done".equals( event.getNewValue() ) ) {
            SwingUtilities.invokeLater( new Runnable(){

                    public void run() {
                        textArea.setDocument( doc );
                        textArea.setCaretPosition(0);
                    }
                } );
        }
    }
    
    public void caretUpdate(CaretEvent event) {
        statusLabel.setText(file.getName() + ' ' + event.getDot() + ':' + file.length());    
    }

   /**
    * Centers <code>you</code> on <code>me</code>. Useful for centering
    * dialogs on their parent frames.
    *
    * @param me   Component to use as basis for centering.
    * @param you  Component to center on <code>me</code>.
    */
   public void center( Component me, Component you ) {
      Rectangle my = me.getBounds();
      Dimension your = you.getSize();
      int x = my.x + ( my.width - your.width ) / 2;
      if ( x < 0 )
         x = 0;
      int y = my.y + ( my.height - your.height ) / 2;
      if ( y < 0 )
         y = 0;
      you.setLocation( x, y );
   }
}
