/*
 * OpenIt jEdit Plugin (FindFileWindow.java) 
 *  
 * Copyright (C) 2003 Matt Etheridge (matt@etheridge.org)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
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

package org.etheridge.openit.gui;

import gnu.regexp.REException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.border.BevelBorder;
import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.ToolTipManager;

import org.etheridge.openit.OpenItProperties;
import org.etheridge.openit.sourcepath.QuickAccessSourcePath;
import org.etheridge.openit.sourcepath.SourcePathFile;
import org.etheridge.openit.SourcePathManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * Popup window that allows users to search for files
 */
public class FindFileWindow extends JFrame
{
  // source file list GUI components 
  private JWindow mSourceFileListWindow;
  private JList mSourceFileList;
  private SourceFileListModel mSourceFileListModel;
  private JScrollPane mScrollPane;
  private List mFileSelectionListeners;
  
  // source file name and filter GUI components
  private JTextField mSourceFileNameField;
  private JButton mFilterButton;
  private String mShowFilterString;
  private String mHideFilterString;
  private SourceFileFilter mSourceFileFilter;
  private FilterDialog mFilterDialog;
  
  // Loading label
  private JLabel mLoadingLabel;
  
  // create a file window that contains a default filter
  public FindFileWindow()
  {
    init();
  }
   
  //
  // Import Selection Listener Methods
  //
  
  public void addFileSelectionListener(FileSelectionListener listener)
  {
    if (mFileSelectionListeners == null) {
      mFileSelectionListeners = new ArrayList();
    }
    mFileSelectionListeners.add(listener);
  }
  
  public void removeFileSelectionListener(FileSelectionListener listener)
  {
    if (mFileSelectionListeners != null) {
      mFileSelectionListeners.remove(listener);
    }
  }
  
  /**
   * Overridden to get around focusing problem!
   * NOTE: if anyone knows how to get around this bloody problem, let me know! ;)
   */
  public void setVisible(boolean visible)
  {
    super.setVisible(visible);
    
    // after the top level JFrame has been made visible, we can then set the 
    // location of the SourceFileListWindow (JWindow) underneath the JFrame.
    Rectangle findFileWindowBounds = getBounds();
    mSourceFileListWindow.setLocation
      (new Point((int) (findFileWindowBounds.getX()),
                 (int) (findFileWindowBounds.getY() + findFileWindowBounds.getHeight())));
    if (visible && mSourceFileListModel.getSize() > 0) {
      mSourceFileListWindow.setVisible(visible);
    } else {
      mSourceFileListWindow.setVisible(false);
    }

    // If the filter is showing, then bring it to the front
    if (mFilterButton.getActionCommand().equals(mHideFilterString)) {
      mFilterDialog.setVisible(visible);
    }
    mFilterDialog.toFront();
    
    // make the text field get focus
    mSourceFileNameField.requestFocus();
    
  }
  
  /**
   * Clear any source files currently in the list.
   */
  public void clearSourceFiles()
  {
    // if we are loading, ignore this request (as we do not want to remove
    // the loading label!)
    if (SourcePathManager.staticGetQuickAccessSourcePath() != null) {
      mSourceFileNameField.setText("");
    }
  }
  
  public void selectFile(String fileName)
  {
    if (fileName != null) {
      mSourceFileNameField.setText(fileName);
    }
  }
  
  //
  // Private Helper Methods
  //
  
  private void setSourceFiles(List sourceFiles)
  {
    mSourceFileFilter.filter(sourceFiles);
    mSourceFileListModel.refreshModel(sourceFiles);
    mSourceFileList.setSelectedIndex(0);

    // if there are no source files to show, then hide the file list
    if (sourceFiles.isEmpty()) {
      mSourceFileListWindow.setVisible(false);
    } 
    // otherwise, only show the rows that exist (up to a minimum of 8 at a 
    // time in the scroll pane).
    else {
      mSourceFileList.setVisibleRowCount(Math.min(sourceFiles.size(), 8));
      mSourceFileListWindow.pack();
      mSourceFileListWindow.setVisible(true);
    }

    mSourceFileList.invalidate();
    mScrollPane.revalidate();
    mScrollPane.repaint();
  }
  
  /**
   * Initialise the GUI components
   */
  private void init()
  {
    // initialize static button string contants
    mShowFilterString = jEdit.getProperty("openit.FindFileWindow.FilterButton.Show.label");
    mHideFilterString = jEdit.getProperty("openit.FindFileWindow.FilterButton.Hide.label");
    
    getContentPane().setLayout(new BorderLayout());
    
    // remove the JFrame top section so it looks like a window
    setUndecorated(true);
    
    // create import list
    mSourceFileListModel = new SourceFileListModel();
    mSourceFileList = new JList(mSourceFileListModel);
    mSourceFileList.setBorder(BorderFactory.createEtchedBorder());
    
    // escape listener (to close the window when escape is pressed).
    KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
    mSourceFileList.getInputMap().put(ks, ks);
    mSourceFileList.getActionMap().put(ks, new AbstractAction() 
    {
      public void actionPerformed(ActionEvent ae) 
      {
        closeWindow();
      }
    });
    
    // enter listener (to signify a user pressed enter to select an import statement)
    KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
    mSourceFileList.getInputMap().put(enterKeyStroke, enterKeyStroke);
    mSourceFileList.getActionMap().put(enterKeyStroke, new AbstractAction() 
    {
      public void actionPerformed(ActionEvent ae) 
      {
        selectionMade();
      }
    });
   
    // add a mouse listener for user selection
    mSourceFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    mSourceFileList.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        // double-click signifies a user selection
        if (e.getClickCount() >= 2) {
          selectionMade();
        }
      }
    });
       
    // add renderer
    mSourceFileList.setCellRenderer(new SourceFileListCellRenderer());  

    // add a source file filter and dialog
    if (mSourceFileFilter == null) {
      mSourceFileFilter = new SourceFileFilter();
    }
    mFilterDialog = new FilterDialog();
    mFilterDialog.addPropertyChangeListener(new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent evt)
      {
        String regularExpression = (String) evt.getNewValue();
        try {
          mSourceFileFilter.setRegularExpressionString(regularExpression);
          updateList(mSourceFileNameField.getText());
        } catch (REException ree) {
          mSourceFileFilter.clearRegularExpression();
          
          Log.log(Log.MESSAGE, FindFileWindow.class, 
            "[OpenIt Plugin]: Invalid regular expression: " + regularExpression);
        }
      }
    });
        
    mScrollPane = new JScrollPane(mSourceFileList);
    getContentPane().add(createClassNamePanel(), BorderLayout.NORTH);

    // add teh source file list in a new window
    mSourceFileListWindow = new JWindow();
    mSourceFileListWindow.getContentPane().setLayout(new BorderLayout());
    mSourceFileListWindow.getContentPane().add(mScrollPane);
    mSourceFileListWindow.pack();

    // create the loading thread if required
    if (SourcePathManager.staticGetQuickAccessSourcePath() == null) {
      createLoaderThread();
    }
    
    //
    // key listeners - this is done here once all components are initialized
    //
    
    // add a key listener to the source file list - any keys that should not
    // be handled by the soure file list and dispatched to the source file
    // name text field for handling there.
    mSourceFileList.addKeyListener(new KeyAdapterProxy(mSourceFileNameField)
    {
      protected boolean shouldForwardEvent(KeyEvent e) 
      {
        return !isKeyHandledBySourceFileList(e.getKeyCode());
      }
    });
    
    // add another key listener to the source file list to handle the beginning
    // and end of lists.
    mSourceFileList.addKeyListener(new KeyAdapter()
    {
      private boolean mDownAtEndPressed = false;
      private boolean mUpPressedAtBeginning = false;
      
      public void keyPressed(KeyEvent e)
      {
        handleEvent(e);
      }
      
      public void keyReleased(KeyEvent e)
      {
        handleEvent(e);
      }

      private void handleEvent(KeyEvent e)
      {
        if (downPressedAtEnd(e)) {    
          if (mDownAtEndPressed) {
            mSourceFileList.setSelectedIndex(0);
            mSourceFileList.ensureIndexIsVisible(0);
            mDownAtEndPressed = false;
            e.consume();
          } else {
            mDownAtEndPressed = true;
          }
          mUpPressedAtBeginning = false;
        } else if (upPressedAtBeginning(e)) {
          if (mUpPressedAtBeginning) {
            mSourceFileList.setSelectedIndex(mSourceFileList.getModel().getSize()-1);
            mSourceFileList.ensureIndexIsVisible(mSourceFileList.getModel().getSize()-1);
            mUpPressedAtBeginning = false;
            e.consume();
          } else {
            mUpPressedAtBeginning = true;
          }
          mDownAtEndPressed = false;
        } else {
          // neither up or down was pressed at beginnig or end
          mUpPressedAtBeginning = false;
          mDownAtEndPressed = false;
        }
      }
      
      private boolean upPressedAtBeginning(KeyEvent e)
      {
        return (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_PAGE_UP)
          && (mSourceFileList.getSelectedIndex() == 0);
      }
      
      private boolean downPressedAtEnd(KeyEvent e)
      {
        return (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
          && (mSourceFileList.getSelectedIndex() == mSourceFileList.getModel().getSize()-1);
      }
    });
    
    // add a key listener to the filter button, so ALL key events are forwarded 
    // to the name field.
    mFilterButton.addKeyListener(new KeyAdapterProxy(mSourceFileNameField));
   
    // certain key events (up,down,enter) should be dispatched onto the 
    // class list for movement and selecting
    mSourceFileNameField.addKeyListener(new KeyAdapterProxy(mSourceFileList)
    {
      protected boolean shouldForwardEvent(KeyEvent e)
      {
        return isKeyHandledBySourceFileList(e.getKeyCode());
      }
    });   
    
    // add a window listener to this JFrame, so that if it is iconified/deiconified
    // the file list is hidden/shown
    addWindowListener(new WindowAdapter()
    {
      public void windowIconified(WindowEvent e)
      {
        mSourceFileListWindow.setVisible(false);
      }
      
      public void windowDeiconified(WindowEvent e)
      {
        mSourceFileListWindow.setVisible(true);
      }
    });
    
    pack();
    
    mFilterDialog.notifyListeners();
  }

  /**
   * Creates a text field for users to enter class names in
   */
  private JPanel createClassNamePanel()
  {
    // create panel, with GridLayout
    JPanel classNamePanel = new JPanel(new GridLayout(2,1));
    classNamePanel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(Color.lightGray), 
      BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.lightGray, Color.darkGray)));
        
    // instruction label and filter panel
    JPanel labelAndFilterPanel = new JPanel(new BorderLayout());
    
    JLabel instructionLabel = new JLabel(jEdit.getProperty("openit.FindFileWindow.Instruction.label"));
    instructionLabel.setFont(new Font("dialog", Font.BOLD, 12));
    instructionLabel.setForeground(Color.black);
    instructionLabel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    labelAndFilterPanel.add(instructionLabel, BorderLayout.WEST);
    labelAndFilterPanel.add(createFilterPanel(), BorderLayout.CENTER);
            
    // the actual text field
    mSourceFileNameField = new JTextField();
    mSourceFileNameField.setColumns(35);
    
    // add components to panel
    classNamePanel.add(labelAndFilterPanel);
    classNamePanel.add(mSourceFileNameField);
   
    // listen for changes to the document (the text) in the text field, and 
    // reload the class list accordingly.
    mSourceFileNameField.getDocument().addDocumentListener(new DocumentListener()
    {
      public void changedUpdate(DocumentEvent e)
      {
        updateSourceFileList(e);
      }
      
      public void insertUpdate(DocumentEvent e)
      {
        updateSourceFileList(e);
      }
          
      public void removeUpdate(DocumentEvent e)
      {
        updateSourceFileList(e);
      }
      
      private void updateSourceFileList(DocumentEvent e)
      {
        // get document text
        String documentText = null;
        try {
          documentText = e.getDocument().getText(0, e.getDocument().getLength());
        } catch (BadLocationException ble) {
          // if we get this exception something really funky has gone wrong, so
          // we'll just return and do nothing.
          Log.log(Log.MESSAGE, FindFileWindow.class, 
            "[OpenIt Plugin]: BadLocationException caught!");
          return;
        }
        
        updateList(documentText);
      }
    });
    
    // escape listener (to close the window when escape is pressed).
    KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
    mSourceFileNameField.getInputMap().put(ks, ks);
    mSourceFileNameField.getActionMap().put(ks, new AbstractAction() 
    {
      public void actionPerformed(ActionEvent ae) 
      {
        closeWindow();
      }
    });
    return classNamePanel;
  }
  
  private JPanel createFilterPanel()
  {
    JPanel filterPanel = new JPanel(new BorderLayout());
       
    mFilterButton = new JButton(mShowFilterString);
    mFilterButton.setMargin(new Insets(1,1,1,1));
    mFilterButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) 
      {
        // position the filter dialog at the right, top corner of this window
        Rectangle currentWindowBounds = FindFileWindow.this.getBounds();
        mFilterDialog.setLocation(new Point((int) (currentWindowBounds.getX() + currentWindowBounds.getWidth()),
                                            (int) currentWindowBounds.getY()));
        
        // show the filter dialog
        mFilterDialog.setVisible(!mFilterDialog.isVisible());
        
        // change the button name
        String buttonName = mFilterDialog.isVisible() ? mHideFilterString : mShowFilterString;
        mFilterButton.setText(buttonName);
      }
    });
        
    //filterPanel.add(mLoadingLabel, BorderLayout.WEST);
    filterPanel.add(mFilterButton, BorderLayout.EAST);
    
    return filterPanel;
  }

  private void updateList(String documentText)
  {
    // if there is nothing in the document, then clear the source files
    // and return
    if (documentText.length() == 0) {
      setSourceFiles(new ArrayList()); // empty list
      return;
    }
    
    // determine starting letter
    char startingChar = documentText.charAt(0);

    // attempt to get teh quick access source path from the SourcePathManager
    QuickAccessSourcePath quickAccessSourcePath =
      SourcePathManager.staticGetQuickAccessSourcePath();
      
    // if the QuickAccessSourcePath instance is null (it can be null
    // if the initial creation thread has not finished yet), then just 
    // return as we cannot do anything yet.
    if (quickAccessSourcePath == null) {
      return;
    }
    
    // TODO: depending on user configuration - this should do one or the other
    List sourceFilesStartingWithLetter = null;
    if (jEdit.getBooleanProperty(OpenItProperties.ALLOW_SUBSTRING_MATCHING, true)) {
      sourceFilesStartingWithLetter = 
        new ArrayList(quickAccessSourcePath.getSourceFilesContaining(documentText));
    } else {
      sourceFilesStartingWithLetter = 
        new ArrayList(quickAccessSourcePath.getSourceFilesStartingWith(startingChar));
    }

    // iterate through list and remove those source files that do not 
    // start with the text in the source file name field
    for (Iterator i = sourceFilesStartingWithLetter.iterator(); i.hasNext();) {
      SourcePathFile currentSourcePathFile = (SourcePathFile) i.next();
      String currentFileName = currentSourcePathFile.getFullName(); 
      
      // if the match is *not* case sensitive then make the file name and
      // document text (to compare with) lowercase
      if (!jEdit.getBooleanProperty(OpenItProperties.POP_UP_CASE_SENSITIVE_FILE_MATCHING, false)) {
        currentFileName = currentFileName.toLowerCase();
        documentText = documentText.toLowerCase();
      }

      // if the user wants substring matching, then search for any substring
      // in the text, otherwise, just search for the first letter.
      if (jEdit.getBooleanProperty(OpenItProperties.ALLOW_SUBSTRING_MATCHING, true)) {
        if (currentFileName.indexOf(documentText) < 0) {
          i.remove();
        }
      } else {
        if (!currentFileName.startsWith(documentText)) {
          i.remove();
        }
      }

    }
        
    setSourceFiles(sourceFilesStartingWithLetter);
  }
  
  
  /**
   * @return whether or not the specified keycode is one that should be 
   * handled by the class list
   */
  private boolean isKeyHandledBySourceFileList(int keyCode)
  {
    return (keyCode == KeyEvent.VK_UP || 
            keyCode == KeyEvent.VK_DOWN ||
            keyCode == KeyEvent.VK_ENTER ||
            keyCode == KeyEvent.VK_PAGE_UP ||
            keyCode == KeyEvent.VK_PAGE_DOWN);
  }
  
  /**
   * Notify any import selection listeners that an import statement was 
   * selected.
   */
  private void notifyFileSelectionListeners(SourcePathFile sourceFile)
  {
    if (mFileSelectionListeners != null) {
      for (Iterator i = mFileSelectionListeners.iterator(); i.hasNext();) {
        FileSelectionListener listener = (FileSelectionListener) i.next();
        listener.fileSelected(sourceFile);
      }
    }
  }
  
  private void selectionMade()
  {
    if (!mSourceFileListModel.isEmpty()) {
      notifyFileSelectionListeners((SourcePathFile)mSourceFileList.getSelectedValue());
      closeWindow();
    }
  }
 
  private void closeWindow()
  {
    // hide this window
    hide();
    
    // and hide the filter dialog if it is showing
    mFilterDialog.hide();
    
    // hide the list window
    mSourceFileListWindow.hide();
  }
  
  private void createLoaderThread()
  {
    // set the loading text on the file name field
    mSourceFileNameField.setText(jEdit.getProperty("openit.FindFileWindow.InitialLoadingMessage.label"));
    
    // create and start the thread
    InitialLoadingThread loaderThread = new InitialLoadingThread();
    loaderThread.start();
  }
  
  //
  // Inner Classes
  //

  /**
   * Keeps checking to see whether the QuickAccessSourcePath is available, and
   * when it is, it updates clears the text on the file name field.
   * NOTE: could use a SwingWorker here
   */
  public class InitialLoadingThread extends Thread
  {
    public void run()
    {
      // periodicially check to see whether the QuickAccessSourcePath is 
      // available yet.
      while (SourcePathManager.staticGetQuickAccessSourcePath() == null) {
        try {
          Thread.sleep(300);
        } catch (Exception e) {
        }
      }
     
      // clear the text in the text field to notify user that the file list
      // has loaded.
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          mSourceFileNameField.setText("");
        }
      });
    }
  }
}
