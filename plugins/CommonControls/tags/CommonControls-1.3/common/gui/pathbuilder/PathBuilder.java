package common.gui.pathbuilder;

/*
 * PathBuilder.java
 * Part of the JSwat plugin for the jEdit text editor
 * Copyright (C) 2001 David Taylor
 * dtaylo11@bigpond.net.au
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.*;
import javax.swing.table.*;
import javax.swing.text.*;

import org.gjt.sp.jedit.jEdit;

/**
 * The PathBuilder is a component that allows a user to build a
 * classpath by selecting directories files using a filesystem
 * browser.<p>
 */
public class PathBuilder extends JPanel implements ActionListener, ListSelectionListener {
    /**
     * The table that displays the path elements.<p>
     */
    private JTable pathElementTable;

    /**
     * The table model that holds the path elements.<p>
     */
    private PathElementTableModel pathElementModel;

    /**
     * A panel to hold the buttons.<p>
     */
    private JPanel btnPanel;

    /**
     * The button to add an element.<p>
     */
    private JButton addElement;

    /**
     * The button to remove an element.<p>
     */
    private JButton removeElement;

    /**
     * The button to move an element towards the front of the path.<p>
     */
    private JButton moveUp;

    /**
     * The button to move an element towards the end of the path.<p>
     */
    private JButton moveDown;

    /**
     * Whether the move buttons are enabled or not.<p>
     */
    private boolean moveButtonsEnabled = true;

    /**
     * Whether to enabled multi-selection in the file chooser or not.<p>
     */
    private boolean multiSelectionEnabled = false;

    /**
     * The elements of the path.<p>
     */
    private Vector elements;

    /**
     * The initial directory to show in the file dialog.<p>
     */
    private String startDirectory;

    /**
     * The file selection mode. By default it is FILES_AND_DIRECTORIES.<p>
     */
    private int fileSelectionMode;
    
    /**
     * A list of external action listeners added by users of this class.    
     */
    private java.util.List<ActionListener> actionListeners = null;

    /**
     * A file filter to set on the file chooser.<p>
     */
    private FileFilter filter;

    private static final String PROPS_PREFIX = "common.gui.pathbuilder";

    private String addButtonText;
    private String removeButtonText;
    private String moveUpButtonText;
    private String moveDownButtonText;
    private String fileDialogTitle;
    private String fileDialogAction;

    /**
     * Creates a new PathBuilder.  Title at the top of the path table will be
     * "Classpath Elements".
     *
     */
    public PathBuilder() {
        this("Classpath Elements");
    }
    
    /**
     * Creates a new PathBuilder.<p>
     *
     * @param title The title to display at the top of the path table.
     */
    public PathBuilder(String title) {
        super(new BorderLayout());

        elements = new Vector();
        pathElementModel = new PathElementTableModel(title);

        addButtonText = jEdit.getProperty(PROPS_PREFIX + ".addButtonText");
        removeButtonText = jEdit.getProperty(PROPS_PREFIX + ".removeButtonText");
        moveUpButtonText = jEdit.getProperty(PROPS_PREFIX + ".moveUpButtonText");
        moveDownButtonText = jEdit.getProperty(PROPS_PREFIX + ".moveDownButtonText");
        fileDialogTitle = jEdit.getProperty(PROPS_PREFIX + ".fileDialogTitle");
        fileDialogAction = jEdit.getProperty(PROPS_PREFIX + ".fileDialogAction");

        addElement = new JButton(addButtonText);
        addElement.addActionListener(this);

        removeElement = new JButton(removeButtonText);
        removeElement.addActionListener(this);

        moveUp = new JButton(moveUpButtonText);
        moveUp.addActionListener(this);

        moveDown = new JButton(moveDownButtonText);
        moveDown.addActionListener(this);

        btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        btnPanel.add(addElement);
        btnPanel.add(removeElement);
        btnPanel.add(moveUp);
        btnPanel.add(moveDown);
        add(btnPanel, BorderLayout.SOUTH);

        removeElement.setEnabled(false);
        moveUp.setEnabled(false);
        moveDown.setEnabled(false);

        pathElementTable = new JTable(pathElementModel);
        JScrollPane tableScroller = new JScrollPane(pathElementTable);
        add(tableScroller, BorderLayout.CENTER);

        pathElementTable.getSelectionModel().addListSelectionListener(this);
        if(elements.size() > 0)
            pathElementTable.setRowSelectionInterval(0, 0);

        fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
    }

    /**
     * Set the text of the add element button.<p>
     *
     * @param text the String to display on the add element button.
     */
    public void setAddButtonText(String text) {
        //addButtonText = text;
        addElement.setText(text);
    }

    /**
     * Set the text of the remove element button.<p>
     *
     * @param text the String to display on the remove element button.
     */
    public void setRemoveButtonText(String text) {
        //removeButtonText = text;
        removeElement.setText(text);
    }

    /**
     * Set the text of the move up button.<p>
     *
     * @param text the String to display on the move up button.
     */
    public void setMoveUpButtonText(String text) {
        //moveUpButtonText = text;
        moveUp.setText(text);
    }

    /**
     * Set the text of the move down button.<p>
     *
     * @param text the String to display on the move down button.
     */
    public void setMoveDownButtonText(String text) {
        //moveDownButtonText = text;
        moveDown.setText(text);
    }

    /**
     * Set a file selection mode to customise type of files can be selected.<p>
     *
     * @param filter the filter to use.
     */
    public void setFileSelectionMode(int fsm) {
        this.fileSelectionMode = fsm;
    }

    /**
     * Set a filter to customise what files are displayed.<p>
     *
     * @param filter the filter to use.
     */
    public void setFileFilter(FileFilter filter) {
        this.filter = filter;
    }

    /**
     * Sets the initial directory to be displayed by the file dialog.<p>
     *
     * @param startDirectory the initial directory to be displayed by the
     * file dialog.
     */
    public void setStartDirectory(String startDirectory) {
        this.startDirectory = startDirectory;
    }

    /**
     * Sets the title of the file dialog.<p>
     *
     * @param fileDialogTitle the title of the file dialog.
     */
    public void setFileDialogTitle(String fileDialogTitle) {
        this.fileDialogTitle = fileDialogTitle;
    }

    /**
     * Sets the label of the file dialog "approve" button.<p>
     *
     * @param fileDialogAction the label of the file dialog "approve" button.
     */
    public void setFileDialogAction(String fileDialogAction) {
        this.fileDialogAction = fileDialogAction;
    }

    /**
     * Set the path to be displayed in the list box.<p>
     *
     * @param path the current path elements, separated by
     * File.pathSeparator.
     */
    public void setPath(String path) {
        int size = elements.size();
        if(size > 0)
        {
            elements.clear();
            pathElementModel.fireTableRowsDeleted(0, size - 1);
        }

        StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
        while(st.hasMoreTokens()) {
            elements.addElement(st.nextToken());
        }

        if(elements.size() > 0) {
            pathElementModel.fireTableRowsInserted(0, elements.size() - 1);
            pathElementTable.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * Set the path to be displayed in the list box.<p>
     *
     * @param path an array of the current path elements.
     */
    public void setPathArray(String[] path) {
        int size = elements.size();
        if ( size > 0 ) {
            elements.clear();
            pathElementModel.fireTableRowsDeleted(0, size - 1);
        }

        for(int i = 0; i < path.length; i++)
            elements.addElement(path[i]);

        if(elements.size() > 0) {
            pathElementModel.fireTableRowsInserted(0, elements.size() - 1);
            pathElementTable.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * Returns the path built using this PathBuilder as a single String,
     * with the elements of the path separated by File.pathSeparator.<p>
     *
     * @return the path built using this PathBuilder.
     */
    public String getPath() {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < elements.size(); i++) {
            sb.append((String)elements.elementAt(i));
            if(i < (elements.size() - 1))
                sb.append(File.pathSeparator);
        }

        return sb.toString();
    }

    /**
     * Returns the path built using this PathBuilder as an array of
     * Strings.<p>
     *
     * @return the path built using this PathBuilder.
     */
    public String[] getPathArray() {
        String[] pathArray = new String[elements.size()];
        for(int i = 0; i < elements.size(); i++)
            pathArray[i] = (String)elements.elementAt(i);

        return pathArray;
    }

    /**
     * Returns the last directory selected in the file chooser dialog
     *
     * @return the last selected directory
     */
    public String getStartDirectory() {
        return startDirectory;
    }

    /**
     * Enable or disable the move buttons.<p>
     *
     * @param enabled true to enabled the move up and move down buttons,
     * false to hide them.
     */
    public void setMoveButtonsEnabled(boolean enabled) {
        if(enabled == true && moveButtonsEnabled == false) {
            moveButtonsEnabled = true;
            btnPanel.add(moveUp);
            btnPanel.add(moveDown);
        }
        else if(enabled == false && moveButtonsEnabled == true) {
            moveButtonsEnabled = false;
            btnPanel.remove(moveDown);
            btnPanel.remove(moveUp);
        }
    }

    /**
     * Enable or disable multiple file selection in the file chooser.<p>
     *
     * @param multiSelectionEnabled true to enable multiple file selection,
     * false to disable it.
     */
    public void setMultiSelectionEnabled(boolean multiSelectionEnabled) {
        this.multiSelectionEnabled = multiSelectionEnabled;
    }

    /**
     * Listen to specific GUI events.
     *
     * @param evt the GUI event.
     */
    public void actionPerformed(ActionEvent evt) {
        int row;
        Object source = evt.getSource();
        if(source.equals(addElement)) {
            JFileChooser chooser;
            if(startDirectory != null)
                chooser = new JFileChooser(startDirectory);
            else
                chooser = new JFileChooser();

            chooser.setFileSelectionMode(fileSelectionMode);
            if(multiSelectionEnabled == true)
                chooser.setMultiSelectionEnabled(true);

            if(filter != null)
                chooser.addChoosableFileFilter(filter);

            chooser.setDialogTitle(fileDialogTitle);
            int returnVal = chooser.showDialog(null, fileDialogAction);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
		    if(multiSelectionEnabled == true) {
		        File[] files = chooser.getSelectedFiles();
		        for(int i = 0; i < files.length; i++)
		            pathElementModel.add(files[i].getPath());
		    } else
		        pathElementModel.add(chooser.getSelectedFile().getPath());
		
		    if(elements.size() == 1)
		        pathElementTable.setRowSelectionInterval(0, 0);               

		    startDirectory = chooser.getCurrentDirectory().getPath();
            }
        }
        else if(source.equals(removeElement)) {
            row = pathElementTable.getSelectedRow();
            if(row >= 0) {
                pathElementModel.remove(row);
            }
        }
        else if(source.equals(moveUp)) {
            row = pathElementTable.getSelectedRow();
            if(row >= 1) {
                pathElementModel.moveUp(row);
            }
        }
        else if(source.equals(moveDown)) {
            row = pathElementTable.getSelectedRow();
            if(row < (elements.size() - 1)) {
                pathElementModel.moveDown(row);
            }
        }

        int tableSize = elements.size();
        if(tableSize < 1 && removeElement.isEnabled())
            removeElement.setEnabled(false);
        else if(tableSize > 0 && removeElement.isEnabled() != true)
            removeElement.setEnabled(true);

        // update the move up/down buttons
        valueChanged(null);
        
        // notify external action listeners
        if (actionListeners != null) {
            for (ActionListener actionListener : actionListeners) {
                actionListener.actionPerformed(new ActionEvent(this, 0, ""));   
            }
        }
    }

    /**
     * Handle list selection events.<p>
     *
     * @param evt the list selection event.
     */
    public void valueChanged(ListSelectionEvent evt) {
        int row = pathElementTable.getSelectedRow();
        int tableSize = elements.size();

        if(tableSize < 1 && removeElement.isEnabled())
            removeElement.setEnabled(false);
        else if(tableSize > 0 && removeElement.isEnabled() != true)
            removeElement.setEnabled(true);

        if(tableSize < 1) {
            moveUp.setEnabled(false);
            moveDown.setEnabled(false);
            return;
        }

        if(row < 1) {
            moveUp.setEnabled(false);
            if(tableSize > 1 && moveDown.isEnabled() != true)
                moveDown.setEnabled(true);
        }
        else if(row == (tableSize - 1)) {
            moveDown.setEnabled(false);
            if(moveUp.isEnabled() != true)
                moveUp.setEnabled(true);
        }
        else {
            moveUp.setEnabled(true);
            moveDown.setEnabled(true);
        }
    }
    
    /**
     * Users of this class may add action listeners to be notified when
     * paths are changed.  Each action listener is called when ever the 
     * 'add', 'remove', or 'move' buttons are pressed.
     */
    public void addActionListener(ActionListener listener) {
        if (actionListeners == null) {
            actionListeners = new ArrayList<ActionListener>();   
        }
        actionListeners.add(listener);
    }

    /**
     * A simple table model of the classpathElementTable.<p>
     */
    class PathElementTableModel extends AbstractTableModel {
        private String title = "";
        public PathElementTableModel(String title) {
            this.title = title;
        }
        
        public int getRowCount() {
            return elements.size();
        }

        public int getColumnCount() {
            return 1;
        }

        public String getColumnName(int column) {
            return title;
        }

        public Object getValueAt(int row, int column) {
            return elements.elementAt(row);
        }

        /**
         * Add an element to the path model.<p>
         *
         * @param value the path element to be added.
         */
        protected void add(String value) {
            int rows = elements.size();
            elements.addElement(value);
            fireTableRowsInserted(rows, rows);
        }

        /**
         * Remove an element from the path model.<p>
         *
         * @param row the index of the element to remove.
         */
        protected void remove(int row) {
            elements.removeElementAt(row);
            fireTableRowsDeleted(row, row);
            if(elements.size() > 0) {
                if(elements.size() > row) {
                    pathElementTable.setRowSelectionInterval(row, row);
                }
                else {
                    row = elements.size() - 1;
                    pathElementTable.setRowSelectionInterval(row, row);
                }
            }
        }

        /**
         * Move an element up (towards the front of) the path.<p>
         *
         * @param row the element to be moved.
         */
        protected void moveUp(int row) {
            Object a = elements.elementAt(row);
            Object b = elements.elementAt(row - 1);
            elements.setElementAt(a, row - 1);
            elements.setElementAt(b, row);
            fireTableRowsUpdated(row - 1, row);
            pathElementTable.setRowSelectionInterval(row - 1, row - 1);
        }

        /**
         * Move an element down (towards the end of) the path.<p>
         *
         * @param row the element to be moved.
         */
        protected void moveDown(int row) {
            Object a = elements.elementAt(row);
            Object b = elements.elementAt(row + 1);
            elements.setElementAt(a, row + 1);
            elements.setElementAt(b, row);
            fireTableRowsUpdated(row, row + 1);
            pathElementTable.setRowSelectionInterval(row + 1, row + 1);
        }
    }
}

