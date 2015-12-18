
package clangbeauty;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
    private File currentFile = null;
    private long lastModified = 0l;
    
    private List<StylePanel> stylePanels = new ArrayList<StylePanel>();

    public DockablePanel() {
        init();
        EditBus.addToBus( this );
    }
    
    /**
     * Listens for EditBus messages indicating the EditPane is showing a different
     * buffer.
     */
    public void handleMessage( EBMessage message ) {
        if ( message instanceof EditPaneUpdate ) {
            EditPaneUpdate epu = ( EditPaneUpdate )message;
            if ( EditPaneUpdate.BUFFER_CHANGED.equals( epu.getWhat() ) ) {
                File formatFile = findClangFormat( epu.getEditPane().getBuffer().getPath() );
                if ( formatFile != null ) {
                    if ( currentFile == null ) {
                        currentFile = formatFile;
                        lastModified = formatFile.lastModified();
                    }
                    else {
                        if ( currentFile.equals( formatFile ) && lastModified == formatFile.lastModified() ) {

                            // file is already loaded, no need to reload
                            return;
                        }
                    }
                }
                loadStyleOptions( epu.getEditPane().getBuffer().getPath(), false );
            }
        }
    }

    // install and layout components this dockable panel
    protected void init() {
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
        setLayout( new BorderLayout() );

        fileLabel = new JLabel();

        // the 'open' button lets the user select a .clang-format file from any directory
        // to load into this panel
        JButton openButton = new JButton( jEdit.getProperty( "vfs.browser.dialog.open", "Open" ) );
        openButton.setToolTipText( jEdit.getProperty("clangbeauty.Select_a_.clang-format_file_to_load.", "Select a .clang-format file to load.") );
        openButton.addActionListener(
        new ActionListener(){

            public void actionPerformed( ActionEvent ae ) {
                DockablePanel.this.open();
            }
        }
        );
        
        // the 'save' button lets the user save the current settings as shown in this panel
        // to a .clang-format file in any directory
        JButton saveButton = new JButton( jEdit.getProperty( "vfs.browser.dialog.save", "Save" ) );
        saveButton.setToolTipText( jEdit.getProperty("clangbeauty.Select_a_directory_to_save_the_current_configuration.", "Select a directory to save the current configuration.") );
        saveButton.addActionListener(
        new ActionListener(){

            public void actionPerformed( ActionEvent ae ) {
                DockablePanel.this.save();
            }
        }
        );
        
        // this is the main panel to hold the various option settings
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
            fileLabel.setText( jEdit.getProperty("clangbeauty.<_.clang-format_not_found_>", "< .clang-format not found >") );
        }

        // need a panel to hold sub-panels per language name
        String[] languageNames = styleOptions.getLanguageNames();
        if ( languageNames.length > 1 ) {
            fileLabel.setText( fileLabel.getText() + " (" + languageNames.length + " " + jEdit.getProperty("clangbeauty.languages", "languages") + ")" );
        }
        styleOptionsPanel.removeAll();
        styleOptionsPanel.setLayout( new BoxLayout( styleOptionsPanel, BoxLayout.Y_AXIS ) );

        // add a sub-panel per language
        for ( int i = 0; i < languageNames.length; i++ ) {
            String language = languageNames[i];
            String title = "";
            if ( StyleOptions.DEFAULT.equals( language ) ) {
                title = "<html><b>" + jEdit.getProperty("clangbeauty.Default_settings", "Default settings");
            }
            else {
                title = "<html><b>" + language + " " + jEdit.getProperty("clangbeauty.Settings", "Settings");
            }
            StylePanel subpanel;
            if (stylePanels.size() <= i){ 
                subpanel = new StylePanel(language, title, styleOptions);
                stylePanels.add(subpanel);
            }
            else {
                subpanel = stylePanels.get(i);
                subpanel.load(language, title, styleOptions);
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
    
    /**
     * Shows a VFSFileDialog to let the user select a directory in which to save the
     * style settings. The file name is always '.clang-format'.
     */
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
            JOptionPane.showMessageDialog( jEdit.getActiveView(), e.getMessage(), jEdit.getProperty("clangbeauty.Error_saving_file", "Error saving file"), JOptionPane.ERROR_MESSAGE );
        }
    }
    
    /**
     * Shows a VFSFileDialog to allow the user to select a .clang-format file to load
     * into this dockable panel.
     * TODO: can the VFSFileDialog be set to show hidden files programatically?
     */
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
            JOptionPane.showMessageDialog( jEdit.getActiveView(), jEdit.getProperty("clangbeauty.File_name_must_be", "File name must be") + " '.clang-format'.", jEdit.getProperty("clangbeauty.Invalid_file_name", "Invalid file name"), JOptionPane.ERROR_MESSAGE );
            return;
        }

        loadStyleOptions( filename, true );
    }
}
