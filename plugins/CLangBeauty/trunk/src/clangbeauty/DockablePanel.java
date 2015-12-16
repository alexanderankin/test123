
package clangbeauty;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.EditPaneUpdate;

/**
 * Dockable panel for CLangBeauty plugin
 * http://clang.llvm.org/docs/ClangFormatStyleOptions.html
 *
 */
public class DockablePanel extends JPanel implements EBComponent {

    private JLabel fileLabel;
    private StyleOptions styleOptions = new StyleOptions();
    private JPanel styleOptionsPanel;

    public DockablePanel() {
        init();
        EditBus.addToBus( this );
    }

    public void handleMessage( EBMessage message ) {
        if ( message instanceof EditPaneUpdate ) {
            EditPaneUpdate epu = ( EditPaneUpdate )message;
            if ( EditPaneUpdate.BUFFER_CHANGED.equals( epu.getWhat() ) ) {

                // TODO: look into improving performance here, this rebuilds the UI
                // every time the user switches to a new buffer, which is probably overkill
                loadStyleOptions( epu.getEditPane().getBuffer().getPath(), false );
            }
        }
    }

    protected void init() {
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
        setLayout( new BorderLayout() );

        fileLabel = new JLabel();

        JButton openButton = new JButton( jEdit.getProperty( "vfs.browser.dialog.open", "Open" ) );
        openButton.setToolTipText( "Select a .clang-format file to load." );
        openButton.addActionListener(
        new ActionListener(){

            public void actionPerformed( ActionEvent ae ) {
                DockablePanel.this.open();
            }
        }
        );
        JButton saveButton = new JButton( jEdit.getProperty( "vfs.browser.dialog.save", "Save" ) );
        saveButton.setToolTipText( "Select a directory to save the current configuration." );
        saveButton.addActionListener(
        new ActionListener(){

            public void actionPerformed( ActionEvent ae ) {
                DockablePanel.this.save();
            }
        }
        );
        styleOptionsPanel = new JPanel();
        JScrollPane scroller = new JScrollPane( styleOptionsPanel );

        JPanel topPanel = new JPanel();
        topPanel.setLayout( new BorderLayout() );
        topPanel.add( fileLabel, BorderLayout.WEST );

        JPanel buttonPanel = new JPanel();
        buttonPanel.add( openButton );
        buttonPanel.add( saveButton );

        topPanel.add( buttonPanel, BorderLayout.EAST );
        add( topPanel, BorderLayout.NORTH );
        add( scroller, BorderLayout.CENTER );

        loadStyleOptions( jEdit.getActiveView().getEditPane().getBuffer().getPath(), false );
    }

    /**
     * Loads the style options from either a parent directory of the given path or from an
     * absolute file name.
     * @param The path to start the search for a .clang-format file.
     * @param isAbsolute If <code>false</code., the search for a .clang-format file starts at <code>path</code>,
     * if <code>true</code>, doesn't search for a .clang-format file. This is so the user can edit a
     * .clang-format file that is not in the current search path.
     */
    private void loadStyleOptions( String path, boolean isAbsolute ) {
        File file = isAbsolute ? new File( path ) : findClangFormat( path );
        if ( file != null ) {
            fileLabel.setText( file.getPath() );
            styleOptions = new StyleOptions();
            styleOptions.load( file );
        }
        else {
            fileLabel.setText( "< .clang-format not found >" );
        }

        // need a panel to hold sub-panels per language name
        String[] languageNames = styleOptions.getLanguageNames();
        if ( languageNames.length > 1 ) {
            fileLabel.setText( fileLabel.getText() + " (" + languageNames.length + " languages)" );
        }
        styleOptionsPanel.removeAll();
        styleOptionsPanel.setLayout( new BoxLayout( styleOptionsPanel, BoxLayout.Y_AXIS ) );

        // need a label and component per option name
        String[] optionNames = styleOptions.getOptionNames();

        for ( final String language : languageNames ) {
            JPanel subpanel = new JPanel( new GridLayout( optionNames.length + 1, 2 ) );
            subpanel.setBorder( BorderFactory.createEtchedBorder() );
            if ( StyleOptions.DEFAULT.equals( language ) ) {
                subpanel.add( new JLabel( "<html><b>Default settings" ) );
                subpanel.add( new JLabel( "" ) );
            }
            else {
                subpanel.add( new JLabel( "<html><b>" +language + " Settings" ) );
                subpanel.add( new JLabel( "" ) );
            }
            for ( final String name : optionNames ) {
                subpanel.add( new JLabel( name ) );
                
                String[] optionChoices = styleOptions.getOptionChoices( name );
                if ( optionChoices.length > 0 ) {
                    if ( optionChoices.length == 1 && "-1".equals( optionChoices[0] ) ) {

                        // use a NumberTextField
                        NumberTextField numberField = new NumberTextField();
                        String value = styleOptions.getOption( language, name );
                        int number = value == null || "".equals( value ) ? 0 : Integer.parseInt( value );
                        numberField.setValue( number );
                        subpanel.add( numberField );
                    }
                    else {

                        // otherwise, use a combo box
                        JComboBox choices = new JComboBox( optionChoices );
                        choices.addActionListener(
                        new ActionListener(){

                            public void actionPerformed( ActionEvent ae ) {
                                JComboBox source = ( JComboBox )ae.getSource();
                                styleOptions.setOption( language, name, ( String )source.getSelectedItem() );
                            }
                        }
                        );
                        String selected = styleOptions.getOption( language, name );
                        if ( selected != null ) {
                            choices.setSelectedItem( selected );
                        }

                        subpanel.add( choices );
                    }
                }
                else {

                    // use a plain text field
                    JTextField textField = new JTextField( styleOptions.getOption( language, name ) );
                    subpanel.add( textField );
                }
            }
            styleOptionsPanel.add( subpanel );
        }
        styleOptionsPanel.validate();
        validate();
    }

    /**
     * Searches in the directories above the directory containing the current buffer
     * for a file named ".clang-format".
     * @return A ".clang-format" file or null if none were found in the parent
     * directories containing the current buffer.
     */
    private File findClangFormat( String bufferPath ) {
        File parentDir = new File( bufferPath ).getParentFile();
        if ( parentDir == null ) {
            return null;
        }

        File clangformat = new File( parentDir, ".clang-format" );
        while ( !clangformat.exists() ) {
            parentDir = parentDir.getParentFile();
            if ( parentDir == null ) {
                break;
            }

            clangformat = new File( parentDir, ".clang-format" );
        }
        return clangformat != null && clangformat.exists() ? clangformat : null;
    }

    private void save() {
        String path = fileLabel.getText();
        if ( path.startsWith( "<" ) ) {
            path = jEdit.getActiveView().getEditPane().getBuffer().getPath();
        }

        String parentDirectory = new File( path ).getParent();
        String[] files = GUIUtilities.showVFSFileDialog( jEdit.getActiveView(), parentDirectory, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false );
        if ( files == null ) {
            return;
        }

        String filename = new File( files[0], ".clang-format" ).getAbsolutePath();

        try {
            String contents = styleOptions.toString();
            BufferedWriter writer = new BufferedWriter( new FileWriter( filename ) );
            writer.write( contents );
            writer.close();
        }
        catch ( Exception e ) {
            JOptionPane.showMessageDialog( jEdit.getActiveView(), e.getMessage(), "Error saving file", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void open() {
        String path = fileLabel.getText();
        if ( path.startsWith( "<" ) ) {
            path = jEdit.getActiveView().getEditPane().getBuffer().getPath();
        }

        String parentDirectory = new File( path ).getParent();
        String[] files = GUIUtilities.showVFSFileDialog( jEdit.getActiveView(), parentDirectory, VFSBrowser.OPEN_DIALOG, false );
        if ( files == null ) {
            return;
        }

        String filename = new File( files[0] ).getAbsolutePath();
        if ( !filename.endsWith( ".clang-format" ) ) {
            JOptionPane.showMessageDialog( jEdit.getActiveView(), "File name must be '.clang-format'.", "Invalid file name", JOptionPane.ERROR_MESSAGE );
            return;
        }

        loadStyleOptions( filename, true );
    }
}
