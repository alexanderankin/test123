package tasklist.options;

import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import tasklist.*;
import ise.java.awt.*;

/**
 * Option pane to let the user adjust the types of files to be considered
 * as binary files.
 */
public class TaskListBinaryFilesOptionPane extends AbstractOptionPane {

    private JList list;
    private JButton removeBtn;
    private JButton resetBtn;
    private JButton addBtn;
    private JTextField ext;
    private JTextField desc;

    private static String SPACER = " - ";

    public TaskListBinaryFilesOptionPane() {
        super( "tasklist.binaryfiles" );
    }

    protected void _init() {
        installComponents();
        installListeners();
    }

    private void installComponents() {
        setLayout( new KappaLayout() );
        setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6 ) );

        // title and description
        JLabel title = new JLabel( "<html><b>Binary File Types" );
        JLabel description = new JLabel( "TaskList will ignore files of these types:" );

        // list of extensions and descriptions
        list = new JList( loadModel() );
        list.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        JScrollPane scroller = new JScrollPane( list ) {
            public Dimension getPreferredSize() {
                return new Dimension(600, 400 );
            }
        };

        // remove and reset buttons
        removeBtn = new JButton( "Remove" );
        removeBtn.setEnabled( false );
        resetBtn = new JButton( "Reset" );
        JPanel removeResetPanel = new JPanel();
        removeResetPanel.add( removeBtn );
        removeResetPanel.add( resetBtn );

        // panel to add a new extension
        JPanel addPanel = new JPanel();
        addPanel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(6, 6, 6, 6 ) ) );
        addPanel.setLayout( new KappaLayout() );
        ext = new JTextField(10 );
        desc = new JTextField(40 );
        addBtn = new JButton( "Add" );
        addBtn.setEnabled( false );
        addPanel.add( "0, 0, 1, 1, 0, w, 3", new JLabel( "Extension" ) );
        addPanel.add( "1, 0, 1, 1, 0, w, 3", new JLabel( "Description" ) );
        addPanel.add( "0, 1, 1, 1, 0, w, 3", ext );
        addPanel.add( "1, 1, 1, 1, 0, w, 3", desc );
        addPanel.add( "0, 2", KappaLayout.createVerticalStrut(6 ) );
        addPanel.add( "1, 3, 1, 1, E,, 3", addBtn );

        // add the components
        add( "0, 0, 1, 1, W,, 3", title );
        add( "0, 1, 1, 1, W,, 3", description );
        add( "0, 2, 1, 1, W,, 3", scroller );
        add( "0, 3, 1, 1, W,, 3", removeResetPanel );
        add( "0, 4", KappaLayout.createVerticalStrut(11 ) );
        add( "0, 5, 1, 1, W, w, 3", addPanel );
    }

    private void installListeners() {
        // remove one or more items from the list
        removeBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                int[] selectedRows = list.getSelectedIndices();
                DefaultListModel model = ( DefaultListModel ) list.getModel();
                for ( int row = selectedRows.length - 1; row >= 0; row-- ) {
                    model.remove( selectedRows[row] );
                }
                list.repaint();
            }
        }
        );

        // reset the list to the default list
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                Binary.resetBinaryTypes();
                list.setModel( loadModel() );
            }
        }
        );

        // enable the remove button only when items are selected in the list
        list.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent lse ) {
                removeBtn.setEnabled( list.getSelectedIndex() > -1 );
            }
        } );

        // enable the add button only when there is text in the extension box
        ext.getDocument().addDocumentListener( new DocumentListener() {
            public void changedUpdate( DocumentEvent e ) {
                toggleAddBtn();
            }
            public void insertUpdate( DocumentEvent e ) {
                toggleAddBtn();
            }
            public void removeUpdate( DocumentEvent e ) {
                toggleAddBtn();
            }

            void toggleAddBtn() {
                String text = ext.getText();
                addBtn.setEnabled( text != null && text.length() > 0 );
            }
        } );

        // add an extension and description to the list.
        // TODO: check for duplicates before adding?
        addBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                String extension = ext.getText();
                if ( extension == null || extension.length() == 0 ) {
                    return;
                }
                if ( !extension.startsWith( "." ) ) {
                    extension = "." + extension;
                }
                String description = desc.getText();
                if ( description == null ) {
                    description = "";
                }
                String toInsert = extension + SPACER + description;
                DefaultListModel model = ( DefaultListModel ) list.getModel();
                for ( int i = 0; i < model.getSize(); i++ ) {
                    String row = ( String ) model.get( i );
                    if ( row.compareTo( toInsert ) < 0 ) {
                        continue;
                    }
                    model.insertElementAt( toInsert, i );
                    break;
                }
                list.repaint();
            }
        }
        );
    }

    // get the current list of binary file types from Binary and return them
    // as a list model.
    private DefaultListModel loadModel() {
        Properties binary = Binary.getBinaryTypes();
        DefaultListModel model = new DefaultListModel();
        List<String> v = new ArrayList<String>();
        for ( Object key : binary.keySet() ) {
            v.add( key.toString() + SPACER + binary.getProperty( ( String ) key ) );
        }
        Collections.sort( v );
        for ( String value : v ) {
            model.addElement( value );
        }
        return model;
    }

    public void _save() {
        Properties props = new Properties();
        DefaultListModel model = ( DefaultListModel ) list.getModel();
        for ( int i = 0; i < model.getSize(); i++ ) {
            String row = ( String ) model.get( i );
            String[] values = row.split( SPACER );
            if ( values.length == 0 ) {
                continue;
            }
            String extension = values[0];
            if ( extension == null || extension.length() == 0 ) {
                continue;
            }
            String description = "";
            if ( values.length == 2 ) {
                description = values[1];
            }
            props.setProperty( extension, description );
        }
        Binary.setBinaryTypes( props );
    }
}
