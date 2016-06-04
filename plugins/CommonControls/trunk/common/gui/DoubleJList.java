
package common.gui;


import ise.java.awt.KappaLayout;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.jEdit;


/**
 * A double list with buttons to move items between the lists and the ability
 * to delete things from the list. ActionListeners may be added to the buttons
 * and ListSelectionListeners may be added to the lists.
 * <br>
 * Usage notes:<br>
 * Create the double list first: DoubleJList dl = new DoubleJList();<br>
 * Call the various "set" methods: dl.setLeftLabel("Available");, etc<br>
 * Call the init() method, which will use the set values to display the panel.<br>
 *<br>
 * Example:<br>
 * DoubleJList<String> dlist = new DoubleJList<String>();<br>
 * dlist.setLeftLabel( "Available" );<br>
 * dlist.setRightLabel( "Selected" );<br>
 * dlist.setLeftListData( new String[] {"one", "two", "three", "four", "five", "six", "seven"} );<br>
 * dlist.setShowAdd( true );<br>
 * dlist.setShowDelete( true );<br>
 * dlist.setLeftToolTipText("Right click for Add and Delete");<br>
 * dlist.setRightToolTipText("Right click for Add and Delete");<br>
 * dlist.init();<br>
 *<br>
 * A popup may be displayed that shows "Add" and "Delete" menu items. The "Add"
 * item does nothing on it's own, it needs to have an action listener added to
 * do anything. The "Delete" item simply removes the item from the list. Other
 * actions can be added to the "Delete" item by adding an action listener. It
 * is possible to only show the "Add" or only show the "Delete".<br>
 *<br>
 * For the "Delete" action, this component shows the user a confirmation dialog.
 * If the user confirms the delete, other delete action listeners should check
 * the value returned by the <code>canDelete()</code> method and respond accordingly.<br>
 *<br>
 * Items in the lists must implement Comparable so the lists can remain sorted.<br>
 */
public class DoubleJList <E extends Comparable> extends JPanel {

    private JLabel leftLabel;
    private JLabel rightLabel;
    private JList<E> leftList;
    private JList<E> rightList;
    private JButton leftButton;
    private JButton rightButton;
    private JPopupMenu popup;
    private JMenuItem add;
    private JMenuItem delete;
    private boolean showAdd = false;
    private boolean showDelete = false;
    private List<E> deleteSelectedList = null;
    private boolean canDelete = false;


    /**
     * Create the double JList.
     */
    public DoubleJList() {
        leftLabel = new JLabel();
        rightLabel = new JLabel();
        leftList = new JList();
        rightList = new JList();
        leftList.setModel( new DefaultListModel() );
        rightList.setModel( new DefaultListModel() );
        leftButton = new JButton( "<" );
        leftButton.setToolTipText( jEdit.getProperty( "common.gui.dlist.moveLeft.tooltip", "Move selected items from right to left" ) );
        rightButton = new JButton( ">" );
        rightButton.setToolTipText( jEdit.getProperty( "common.gui.dlist.moveRight.tooltip", "Move selected items from left to right" ) );
        leftButton.addActionListener( leftDefaultAction );
        rightButton.addActionListener( rightDefaultAction );
        add = new JMenuItem( jEdit.getProperty( "common.gui.dlist.Add", "Add" ) );
        delete = new JMenuItem( jEdit.getProperty( "common.gui.dlist.Delete", "Delete" ) );
        popup = new JPopupMenu();
    }


    /**
     * Construct the double JList using the values set via the <code>set*</code>
     * methods.
     */
    public void init() {
        KappaLayout kl = new KappaLayout();
        setLayout( kl );
        add( "0, 0, ,1, 7, w,  2", leftLabel );
        add( "2, 0, ,1, 7, w,  2", rightLabel );
        add( "0, 1, ,6, ,  wh, 5", new JScrollPane( leftList ) );
        add( "2, 1, ,6, ,  wh, 5", new JScrollPane( rightList ) );
        add( "1, 3, , , ,  w", leftButton );
        add( "1, 4, , , ,  w", rightButton );
        kl.makeColumnsSameWidth( 0, 2 );

        if ( showAdd ) {
            popup.add( add );
        }


        if ( showDelete ) {
            popup.add( delete );
            delete.addActionListener( deleteDefaultAction );
        }


        if ( showAdd || showDelete ) {
            leftList.setComponentPopupMenu( popup );
            rightList.setComponentPopupMenu( popup );
        }
    }


    /**
     * Set the heading for the left JList.
     * @param text The text for the heading.
     */
    public void setLeftLabel( String text ) {
        leftLabel.setText( text );
    }


    /**
     * Set the heading for the right JList.
     * @param text The text for the heading.
     */
    public void setRightLabel( String text ) {
        rightLabel.setText( text );
    }


    /**
     * Set the data to display in the left JList.
     * @param data The data to display.
     */
    public void setLeftListData( E[] data ) {
        DefaultListModel<E> model = ( DefaultListModel <E> )leftList.getModel();
        model.clear();
        if ( data != null ) {
            for ( E item : data ) {
                model.addElement( item );
            }
        }


        sortLists();
    }


    /**
     * Set the data to display in the right JList.
     * @param data The data to display.
     */
    public void setRightListData( E[] data ) {
        DefaultListModel<E> model = ( DefaultListModel <E> )rightList.getModel();
        model.clear();
        if ( data != null ) {
            for ( E item : data ) {
                model.addElement( item );
            }
        }


        sortLists();
    }


    /**
     * Set the data to display in the left JList.
     * @param model The data to display.
     */
    public void setLeftModel( DefaultListModel<E> model ) {
        leftList.setModel( model );
        sortLists();
    }


    /**
     * Set the data to display in the right JList.
     * @param model The data to display.
     */
    public void setRightModel( DefaultListModel<E> model ) {
        rightList.setModel( model );
        sortLists();
    }


    /**
     * Add a list selection listener to the left JList.
     * @param listener The listener.
     */
    public void addLeftListSelectionListener( ListSelectionListener listener ) {
        leftList.addListSelectionListener( listener );
    }


    /**
     * Add a list selection listener to the right JList.
     * @param listener The listener.
     */
    public void addRightListSelectionListener( ListSelectionListener listener ) {
        rightList.addListSelectionListener( listener );
    }


    /**
     * Set a tooltip for the left JList. Default tool tip says, "Right click for
     * Add and Delete".
     * @param tip The text for the tool tip.
     */
    public void setLeftToolTipText( String tip ) {
        leftList.setToolTipText( tip );
    }


    /**
     * Set a tooltip for the right JList. Default tool tip says, "Right click for
     * Add and Delete".
     * @param tip The text for the tool tip.
     */
    public void setRightToolTipText( String tip ) {
        rightList.setToolTipText( tip );
    }


    /**
     * Set a cell renderer for the left JList.
     * @param cellRender The cell renderer.
     */
    public void setLeftCellRenderer( ListCellRenderer<? super E> cellRenderer ) {
        leftList.setCellRenderer( cellRenderer );
    }


    /**
     * Set a cell renderer for the right JList.
     * @param cellRender The cell renderer.
     */
    public void setRightCellRenderer( ListCellRenderer<? super E> cellRenderer ) {
        rightList.setCellRenderer( cellRenderer );
    }


    /**
     * @return The selected values in the left JList.
     */
    public List<E> getLeftSelectedValuesList() {
        return leftList.getSelectedValuesList();
    }


    /**
     * @return The selected values in the right JList.
     */
    public List<E> getRightSelectedValuesList() {
        return rightList.getSelectedValuesList();
    }


    /**
     * @return All the values in the left JList.
     */
    public List<E> getLeftValuesList() {
        DefaultListModel model = ( DefaultListModel )leftList.getModel();
        ArrayList<E> list = new ArrayList<E>();
        for ( int i = 0; i < model.getSize(); i++ ) {
            list.add( ( E )model.get( i ) );    // ??? the cast should not be necessary, 'get' returns an E
        }
        return list;
    }


    /**
     * @return All the values in the right JList.
     */
    public List<E> getRightValuesList() {
        DefaultListModel model = ( DefaultListModel )rightList.getModel();
        ArrayList<E> list = new ArrayList<E>();
        for ( int i = 0; i < model.getSize(); i++ ) {
            list.add( ( E )model.get( i ) );    // ??? the cast should not be necessary, 'get' returns an E
        }
        return list;
    }


    /**
     * Add an action listener to the "move items left" button.
     * @param listener The listener.
     */
    public void addLeftButtonActionListener( ActionListener listener ) {
        leftButton.addActionListener( listener );
    }


    /**
     * Add an action listener to the "move items right" button.
     * @param listener The listener.
     */
    public void addRightButtonActionListener( ActionListener listener ) {
        rightButton.addActionListener( listener );
    }


    /**
     * Should the popup menu show an "Add" item?
     * @param b Use true to show "Add" in the popup.
     */
    public void setShowAdd( boolean b ) {
        showAdd = b;
    }


    /**
     * Should the popup menu show a "Delete" item?
     * @param b Use true to show "Delete" in the popup.
     */
    public void setShowDelete( boolean b ) {
        showDelete = b;
    }


    public List<E> getDeleteSelectedValues() {
        return deleteSelectedList;
    }
    
    /**
     * Delete action listeners should check this value before doing anything. 
     * This component will show the user a confirmation dialog and this method
     * will return <code>true</code> if the user has confirmed the delete.
     * @return true if the user has elected to delete some items from the lists.    
     */
    public boolean canDelete() {
        return canDelete;   
    }


    /**
     * Add an action listener to the "Add" menu item in the popup.
     * @param listener The listener.
     */
    public void addAddListener( ActionListener listener ) {
        add.addActionListener( listener );
    }


    /**
     * Add an action listener to the "Delete" menu item in the popup.
     * @param listener The listener.
     */
    public void addDeleteListener( ActionListener listener ) {
        delete.addActionListener( listener );
    }


    /**
     * Add a menu item to the popup menu.
     * @param menuItem The menu item.
     */
    public void addMenuItem( JMenuItem menuItem ) {
        popup.add( menuItem );
    }

    // Action listener to move items from the right list to the left list.
    ActionListener leftDefaultAction = new ActionListener(){

        public void actionPerformed( ActionEvent ae ) {
            List<E> selected = rightList.getSelectedValuesList();
            DefaultListModel<E> leftModel = ( DefaultListModel <E> )leftList.getModel();
            if ( selected != null ) {
                for ( E item : selected ) {
                    leftModel.addElement( item );
                }
                DefaultListModel<E> rightModel = ( DefaultListModel <E> )rightList.getModel();
                int[] indices = rightList.getSelectedIndices();
                for ( int i = indices.length - 1; i >= 0; i-- ) {
                    rightModel.remove( indices[i] );
                }
                sortLists();
            }
        }
    };
    // Action listener to move items from the left list to the right list.
    ActionListener rightDefaultAction = new ActionListener(){

        public void actionPerformed( ActionEvent ae ) {
            List<E> selected = leftList.getSelectedValuesList();
            DefaultListModel<E> rightModel = ( DefaultListModel <E> )rightList.getModel();
            if ( selected != null ) {
                for ( E item : selected ) {
                    rightModel.addElement( item );
                }
                DefaultListModel<E> leftModel = ( DefaultListModel <E> )leftList.getModel();
                int[] indices = leftList.getSelectedIndices();
                for ( int i = indices.length - 1; i >= 0; i-- ) {
                    leftModel.remove( indices[i] );
                }
                sortLists();
            }
        }
    };
    // Default action for the delete menu item, this shows a confirmation dialog
    // before removing the selected items from the list.
    ActionListener deleteDefaultAction = new ActionListener(){

        public void actionPerformed( ActionEvent ae ) {
            JList parentList = ( JList )popup.getInvoker();
            deleteSelectedList = parentList.getSelectedValuesList();
            if ( deleteSelectedList != null && !deleteSelectedList.isEmpty() ) {

                List<E> items = getDeleteSelectedValues();
                if ( items != null ) {
                    // build list of items
                    StringBuilder sb = new StringBuilder( 128 );
                    for ( E item : items ) {
                        sb.append( item.toString() ).append( '\n' );
                    }
                    String message = items.size() > 1 ? jEdit.getProperty("common.gui.dlist.deleteSeveralItems") : jEdit.getProperty("common.gui.dlist.deleteOneItem");
                    sb.insert(0, message + '\n');
                    
                    // confirm the delete
                    int answer = JOptionPane.showConfirmDialog( DoubleJList.this, sb.toString(), jEdit.getProperty("common.gui.dlist.confirmDelete", "Confirm Delete"), JOptionPane.WARNING_MESSAGE );
                    if ( answer == JOptionPane.YES_OPTION ) {
                        DefaultListModel<E> parentModel = ( DefaultListModel <E> )parentList.getModel();
                        int[] indices = parentList.getSelectedIndices();
                        for ( int i = indices.length - 1; i >= 0; i-- ) {
                            parentModel.remove( indices[i] );
                        }
                        sortLists();
                        canDelete = true;
                    }
                    else {
                        deleteSelectedList = null;
                        canDelete = false;    
                    }
                }
            }
        }
    };


    // Sorts both of the lists using the natural ordering of the items.
    private void sortLists() {
        ArrayList<E> list = new ArrayList<E>();
        DefaultListModel<E> model = ( DefaultListModel <E> )leftList.getModel();
        if ( model.getSize() > 1 ) {
            for ( int i = 0; i < model.getSize(); i++ ) {
                list.add( model.get( i ) );
            }
            Collections.sort( list );
            for ( int i = 0; i < list.size(); i++ ) {
                model.set( i, list.get( i ) );
            }
        }


        list.clear();
        model = ( DefaultListModel <E> )rightList.getModel();
        if ( model.getSize() > 1 ) {
            for ( int i = 0; i < model.getSize(); i++ ) {
                list.add( model.get( i ) );
            }
            Collections.sort( list );
            for ( int i = 0; i < list.size(); i++ ) {
                model.set( i, list.get( i ) );
            }
        }


        leftList.revalidate();
        rightList.revalidate();
    }


    // for testing
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test" );
        DoubleJList<String> dlist = new DoubleJList<String>();
        dlist.setLeftLabel( "Available" );
        dlist.setRightLabel( "Selected" );
        dlist.setLeftListData( new String[] {"one", "two", "three", "four", "five", "six", "seven"} );
        dlist.setShowAdd( true );
        dlist.setShowDelete( true );
        dlist.setLeftToolTipText( "Right click for Add and Delete" );
        dlist.setRightToolTipText( "Right click for Add and Delete" );
        dlist.init();
        frame.setContentPane( dlist );
        frame.pack();
        frame.setVisible( true );
    }
}
