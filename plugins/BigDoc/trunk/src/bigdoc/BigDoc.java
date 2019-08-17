
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;


// TODO: figure out why this locks up jEdit when attempting to open an mkv file.
public class BigDoc extends JPanel implements PropertyChangeListener, CaretListener {

    private View view;
    private JTextArea textArea;
    private BigTextDocument doc;
    private JMenuItem open_mi = new JMenuItem( jEdit.getProperty( "bigdoc.Open_File", "Open File" ) );
    private JMenuItem find_mi = new JMenuItem( jEdit.getProperty( "bigdoc.Find", "Find" ) );
    private JLabel statusLabel;
    private File file;    // current file being displayed

    public BigDoc( View view ) {
        super();
        this.view = view;
        init();
    }

    private void init() {
        initGUI();
        initActions();
    }

    private void initGUI() {
        setLayout( new BorderLayout() );

        // top panel holds menu and status
        JPanel topPanel = new JPanel();
        topPanel.setLayout( new LambdaLayout() );

        // menu
        JMenuBar bar = new JMenuBar();
        topPanel.add( bar, "0, 0, 1, 1, W, , 3" );
        JMenu file_menu = new JMenu( jEdit.getProperty( "bigdoc.File", "File" ) );
        file_menu.add( open_mi );
        file_menu.add( find_mi );
        bar.add( file_menu );
        topPanel.add( LambdaLayout.createHorizontalStrut( 30 ), "1, 0, 1, 1, 0, w" );

        // status
        statusLabel = new JLabel();
        topPanel.add( statusLabel, "3, 0, 1, 1, E, w, 3" );
        add( topPanel, BorderLayout.NORTH );

        // main text area
        textArea = new JTextArea();
        textArea.setEditable( true );    // true even though document is read only, this allows the caret to be visible and move
        textArea.addCaretListener( this );
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
                    center( view, find );
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
                        textArea.setText( jEdit.getProperty( "bigdoc.Loading...", "Loading..." ) );
                        statusLabel.setText( jEdit.getProperty( "bigdoc.Loading", "Loading:" ) + ' ' + file.getName() );
                        doc = new BigTextDocument( filename, BigDoc.this );
                    }
                    catch ( Exception e ) {

                        JOptionPane.showMessageDialog( view, e.getMessage(), jEdit.getProperty( "bigdoc.Error_loading_file", "Error loading file" ), JOptionPane.ERROR_MESSAGE );
                        textArea.setText( "" );
                        statusLabel.setText( "" );
                    }
                }
            } );
    }

    public void propertyChange( PropertyChangeEvent event ) {
        if ( "state".equals( event.getPropertyName() ) && SwingWorker.StateValue.DONE.equals( event.getNewValue() ) ) {
            SwingUtilities.invokeLater( new Runnable(){

                    public void run() {
                        textArea.setDocument( doc );
                        textArea.setCaretPosition( 0 );
                        statusLabel.setText( file.getName() + " 0:" + file.length() );
                        textArea.requestFocusInWindow();
                    }
                } );
        }
    }

    public void caretUpdate( CaretEvent event ) {
        statusLabel.setText( file.getName() + ' ' + event.getDot() + ':' + file.length() );
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
        if ( x < 0 ) {
            x = 0;
        }
        int y = my.y + ( my.height - your.height ) / 2;
        if ( y < 0 ) {
            y = 0;
        }
        you.setLocation( x, y );
    }
}
