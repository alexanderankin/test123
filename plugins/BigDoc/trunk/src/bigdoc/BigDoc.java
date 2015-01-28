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
import javax.swing.KeyStroke;

import javax.swing.event.*;
import javax.swing.text.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;

public class BigDoc extends JPanel {

    private View view;
    private String filename;
    private JEditTextArea editor;
    private StatusBar status;

    private JMenuItem open_mi = new JMenuItem( "Open File" );
    private JMenuItem save_mi = new JMenuItem( "Save File" );

    private JMenuItem undo_mi = new JMenuItem( "Undo" );
    private JMenuItem redo_mi = new JMenuItem( "Redo" );
    private JMenuItem cut_mi = new JMenuItem( "Cut" );
    private JMenuItem copy_mi = new JMenuItem( "Copy" );
    private JMenuItem paste_mi = new JMenuItem( "Paste" );
    private JMenuItem find_mi = new JMenuItem( "Find" );
    private JMenuItem replace_mi = new JMenuItem( "Replace" );

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
        file_menu.setMnemonic( KeyEvent.VK_F );

        JMenu edit_menu = new JMenu( "Edit" );
        edit_menu.setMnemonic( KeyEvent.VK_E );

        open_mi.setMnemonic( KeyEvent.VK_O );
        save_mi.setMnemonic( KeyEvent.VK_S );

        undo_mi.setMnemonic( KeyEvent.VK_Z );
        redo_mi.setMnemonic( KeyEvent.VK_Y );
        cut_mi.setMnemonic( KeyEvent.VK_X );
        copy_mi.setMnemonic( KeyEvent.VK_C );
        paste_mi.setMnemonic( KeyEvent.VK_V );
        find_mi.setMnemonic( KeyEvent.VK_F );
        replace_mi.setMnemonic( KeyEvent.VK_R );
        open_mi.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.CTRL_MASK ) );
        save_mi.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK ) );
        undo_mi.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Z, ActionEvent.CTRL_MASK ) );
        redo_mi.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Y, ActionEvent.CTRL_MASK ) );
        cut_mi.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, ActionEvent.CTRL_MASK ) );
        copy_mi.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, ActionEvent.CTRL_MASK ) );
        paste_mi.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, ActionEvent.CTRL_MASK ) );
        find_mi.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F, ActionEvent.CTRL_MASK ) );
        replace_mi.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, ActionEvent.CTRL_MASK ) );

        file_menu.add( open_mi );
        file_menu.add( save_mi );

        edit_menu.add( undo_mi );
        edit_menu.add( redo_mi );
        edit_menu.addSeparator();
        edit_menu.add( cut_mi );
        edit_menu.add( copy_mi );
        edit_menu.add( paste_mi );
        edit_menu.addSeparator();
        edit_menu.add( find_mi );
        edit_menu.add( replace_mi );
        replace_mi.setVisible( false );
        bar.add( file_menu );
        bar.add( edit_menu );

        editor = new JEditTextArea();
        editor.setTokenMarker( new XMLTokenMarker() );
        Font font = new Font( "dialog", Font.PLAIN, 12 );
        editor.getPainter().setFont( font );
        editor.setCaretPosition( 0 );
        editor.scrollToCaret();
        add( editor, BorderLayout.CENTER );

        status = new StatusBar();
        add( status, BorderLayout.SOUTH );
    }

    private void initActions() {
        open_mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                String[] files = GUIUtilities.showVFSFileDialog( view, view.getBuffer().getPath(), VFSBrowser.OPEN_DIALOG, false );
                if ( files != null && files.length == 1 ) {
                    // open( "/home/danson/docs/software_manuals/jdk1.7_docs/docs/api/index-files/index-7.html" );
                    BigDoc.this.filename = files[0];
                    open( filename );
                }
            }
        }
        );

        save_mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                save();
            }
        }
        );

        find_mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                FindAndReplace find = new FindAndReplace( view, FindAndReplace.FIND, editor );
                GUIUtils.center( view, find );
                find.setVisible( true );
            }
        }
        );

        replace_mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                FindAndReplace find = new FindAndReplace( view, FindAndReplace.REPLACE, editor );
                GUIUtils.center( view, find );
                find.setVisible( true );
            }
        }
        );

        editor.addCaretListener( new CaretListener() {
            public void caretUpdate( CaretEvent ce ) {
                status.setLine( editor.getCaretLine(), editor.getLineCount() );
            }
        }
        );

        undo_mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                editor.undo();
                undo_mi.setEnabled( editor.canUndo() );
                redo_mi.setEnabled( editor.canRedo() );
            }
        }
        );
        redo_mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                editor.redo();
                undo_mi.setEnabled( editor.canUndo() );
                redo_mi.setEnabled( editor.canRedo() );
            }
        }
        );
        cut_mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                editor.cut();
            }
        }
        );
        copy_mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                editor.copy();
            }
        }
        );
        paste_mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                editor.paste();
            }
        }
        );

        undo_mi.setEnabled( editor.canUndo() );
        redo_mi.setEnabled( editor.canRedo() );
    }

    /**
     * Loads the editor with the contents of the given file.
     */
    public void open( String filename ) {
        final File file = new File( filename );
        if ( file == null ) {
            return;
        }
        if ( file.isDirectory() ) {
            return;
        }
        try {
            FileMap fileMap = new FileMap( filename );

            TextAreaDefaults defaults = TextAreaDefaults.getDefaults();
            SyntaxDocument doc = new SyntaxDocument( fileMap );
            defaults.document = doc;
            doc.putProperty( SyntaxDocument.FILE, file );
            editor.setDocument( doc );
            editor.setCaretPosition( 0 );
            editor.scrollToCaret();

            // TODO: can I use the jEdit token marker classes instead?
            editor.setTokenMarker( new XMLTokenMarker() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the contents of the editor to the current build file from the
     * AntelopePanel. Whoa, Nelly! It is possible that the build file in the
     * editor is NOT the build file open in the AntelopePanel!
     */
    private void save() {
        // TODO: update this so it works with the FileMap.
        /*
        SyntaxDocument doc = (SyntaxDocument)editor.getDocument();
        File file = (File)doc.getProperty(SyntaxDocument.FILE);
        if ( file == null )
            return ;

        int caret_position = editor.getCaretPosition();
        try {
            StringReader reader = new StringReader( editor.getText() );
            FileWriter writer = new FileWriter( file );
            FileUtilities.copyToWriter( reader, writer );
///            _antelope_panel.reload();
            setTitle( "BigDoc: " + file.getAbsolutePath() );
            editor.setCaretPosition(caret_position);
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        */
    }
}

