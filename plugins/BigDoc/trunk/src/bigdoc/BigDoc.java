
package bigdoc;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.prefs.*;
import java.util.regex.*;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.event.*;
import javax.swing.text.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// TODO: should show the filename, file size, current caret position
public class BigDoc extends JPanel implements PropertyChangeListener {

    private View view;
    private JTextArea textArea;
    private BigTextDocument doc;
    private JMenuItem open_mi = new JMenuItem( "Open File" );
    private JMenuItem find_mi = new JMenuItem( "Find" );

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

        JMenuBar bar = new JMenuBar();
        add( bar, BorderLayout.NORTH );
        JMenu file_menu = new JMenu( "File" );
        file_menu.add( open_mi );
        file_menu.add( find_mi );
        bar.add( file_menu );

        textArea = new JTextArea( );
        textArea.setEditable( true );   // true even though document is read only, this allows the caret to be visible and move
        add( new JScrollPane( textArea ), BorderLayout.CENTER );
    }

    private void initActions() {
        open_mi.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    String[] files = GUIUtilities.showVFSFileDialog( view, view.getBuffer().getPath(), VFSBrowser.OPEN_DIALOG, false );
                    if ( files != null && files.length == 1 ) {

                        // open( "/home/danson/tmp/bigfile7.html" );
                        load(files[0]);
                    }
                }
            }
        );

        find_mi.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    FindAndReplace find = new FindAndReplace( view, FindAndReplace.FIND, textArea );
                    //GUIUtils.center( view, find );
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
        try {
            textArea.setText("Loading...");
            doc = new BigTextDocument( filename, this );
        }
        catch(Exception e) {
            // TODO: show exception message in JOptionPane
            e.printStackTrace();
        }
    }
    
    public void propertyChange( PropertyChangeEvent event ) {
        if ( "lineLoader".equals( event.getPropertyName() ) && "done".equals( event.getNewValue() ) ) {
            textArea.setDocument( doc );
        }
    }
}
