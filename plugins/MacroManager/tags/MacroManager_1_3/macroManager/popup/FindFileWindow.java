/*
 *  MacroManager jEdit Plugin
 *
 *  Copyright (C) 2006 Carmine Lucarelli
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package macroManager.popup;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.*;


/**
 *  Popup dialog that allows users to search for macros.
 */
public class FindFileWindow extends JDialog
{
	// source file list GUI components
	private JWindow mSourceFileListWindow;
	private JList mSourceFileList;
	private SourceFileListModel mSourceFileListModel;
	private JScrollPane mScrollPane;
	private ArrayList mFileSelectionListeners;

	// source file name and filter GUI components
	private JTextField mSourceFileNameField = new JTextField();
	private FilterPanel mFilterPanel;
	private SourceFileFilter mSourceFileFilter;
	private boolean useRegexp = false;

	// Loading label
	private JLabel mLoadingLabel;


	//{{{ showWindow method.
	/**
	 *  Description of the Method
	 */
	public void showWindow()
	{
		mSourceFileNameField.requestFocus();
		setLocationRelativeTo(jEdit.getActiveView().getTextArea());
		updateList(mSourceFileNameField.getText());
		setVisible(true);
	}

	//}}}


	//{{{ constructor.
	/**
	 *  Constructor for the FindFileWindow object
	 */
	public FindFileWindow()
	{
		// super();
		super(jEdit.getActiveView(), false);
		init();
	}

	//}}}


	// {{{ Import Selection Listener Methods
	/**
	 *  Adds a feature to the FileSelectionListener attribute of the FindFileWindow
	 *  object
	 *
	 *@param  listener The feature to be added to the FileSelectionListener
	 *      attribute
	 */
	public void addFileSelectionListener(FileSelectionListener listener)
	{
		if(mFileSelectionListeners == null)
			mFileSelectionListeners = new ArrayList();

		mFileSelectionListeners.add(listener);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  listener Description of the Parameter
	 */
	public void removeFileSelectionListener(FileSelectionListener listener)
	{
		if(mFileSelectionListeners != null)
			mFileSelectionListeners.remove(listener);

	}


	// }}}

	//{{{ clearSourceFiles method.
	/**
	 *  Clear any source files currently in the list.
	 */
	public void clearSourceFiles()
	{
		mSourceFileNameField.setText("");

	}

	//}}}


	//{{{ selectFile method.
	/**
	 *  Description of the Method
	 *
	 *@param  fileName Description of the Parameter
	 */
	public void selectFile(String fileName)
	{
		if(fileName != null)
			mSourceFileNameField.setText(fileName);

	}

	//}}}


	//{{{ setSourceFiles method.
	private void setSourceFiles(ArrayList sourceFiles)
	{
		mSourceFileListModel.refreshModel(sourceFiles);
		mSourceFileList.setSelectedIndex(0);

		if(sourceFiles.isEmpty())
		{
			mSourceFileListWindow.setVisible(false);
			mSourceFileNameField.requestFocus();
			mSourceFileNameField.setForeground(Color.RED);
		}
		else
		{
			mSourceFileNameField.setForeground(Color.BLACK);
			mSourceFileList.setVisibleRowCount(Math.min(sourceFiles.size(), 8));
			mSourceFileListWindow.pack();
			showFileListWindow();
		}

		// mSourceFileList.invalidate();
		// mScrollPane.revalidate();
		// mScrollPane.repaint();
	}


	//}}}

	//{{{ showFileListWindow method.
	private void showFileListWindow()
	{
		Rectangle findFileWindowBounds = getBounds();
		int x = (int)findFileWindowBounds.getX();
		int y = (int)(findFileWindowBounds.getY() + findFileWindowBounds.getHeight());
		mSourceFileListWindow.setLocation(new Point(x, y));
		mSourceFileListWindow.setVisible(true);
	}


	//}}}

	// {{{ init method.
	/**
	 *  Initialise the GUI components
	 */
	private void init()
	{
		getContentPane().setLayout(new BorderLayout());
		setUndecorated(true);

		mSourceFileListModel = new SourceFileListModel();
		mSourceFileList = new JList(mSourceFileListModel);
		mSourceFileList.setBorder(BorderFactory.createEtchedBorder());

		addEscapeKeyStroke(mSourceFileList);
		KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		mSourceFileList.getInputMap().put(enterKeyStroke, enterKeyStroke);
		mSourceFileList.getActionMap().put(enterKeyStroke,
			new AbstractAction()
			{
				public void actionPerformed(ActionEvent ae)
				{
					selectionMade();
				}
			});

		mSourceFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mSourceFileList.addMouseListener(
			new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					if(e.getClickCount() >= 2)
						selectionMade();

				}
			});

		mSourceFileList.setCellRenderer(new SourceFileListCellRenderer());

		if(mSourceFileFilter == null)
			mSourceFileFilter = new SourceFileFilter();

		// add teh source file list in a new window
		mScrollPane = new JScrollPane(mSourceFileList);
		mSourceFileListWindow = new JWindow(this);
		mSourceFileListWindow.getContentPane().setLayout(new BorderLayout());
		mSourceFileListWindow.getContentPane().add(mScrollPane);
		mSourceFileListWindow.pack();

		mSourceFileList.addKeyListener(
			new KeyAdapterProxy(mSourceFileNameField)
			{
				protected boolean shouldForwardEvent(KeyEvent e)
				{
					return !isKeyHandledBySourceFileList(e.getKeyCode());
				}
			});

		//{{{ mSourceFileList.addKeyListener
		mSourceFileList.addKeyListener(
			new KeyAdapter()
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
					if(downPressedAtEnd(e))
					{
						if(mDownAtEndPressed)
						{
							mSourceFileList.setSelectedIndex(0);
							mSourceFileList.ensureIndexIsVisible(0);
							mDownAtEndPressed = false;
							e.consume();
						}
						else
							mDownAtEndPressed = true;

						mUpPressedAtBeginning = false;
					}
					else if(upPressedAtBeginning(e))
					{
						if(mUpPressedAtBeginning)
						{
							mSourceFileList.setSelectedIndex(mSourceFileList.getModel().getSize() - 1);
							mSourceFileList.ensureIndexIsVisible(mSourceFileList.getModel().getSize() - 1);
							mUpPressedAtBeginning = false;
							e.consume();
						}
						else
							mUpPressedAtBeginning = true;

						mDownAtEndPressed = false;
					}
					else
					{
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
						 && (mSourceFileList.getSelectedIndex() == mSourceFileList.getModel().getSize() - 1);
				}
			});
		//}}}

		// certain key events (up,down,enter) should be dispatched onto the
		// class list for movement and selecting
		getContentPane().add(createClassNamePanel(), BorderLayout.NORTH);
		mSourceFileNameField.addKeyListener(
			new KeyAdapterProxy(mSourceFileList)
			{
				protected boolean shouldForwardEvent(KeyEvent e)
				{
					return isKeyHandledBySourceFileList(e.getKeyCode());
				}
			});

		pack();
	}


	// }}}

	//{{{ createClassNamePanel method.
	/**
	 *  Creates a text field for users to enter class names in
	 *
	 *@return  Description of the Return Value
	 */
	private JPanel createClassNamePanel()
	{
		JPanel classNamePanel = new JPanel(new GridLayout(2, 1));
		classNamePanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.lightGray),
			BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.lightGray, Color.darkGray)));

		JPanel labelAndFilterPanel = new JPanel(new BorderLayout());

		JLabel instructionLabel = new JLabel(jEdit.getProperty("macro-manager.popup.message"));
		instructionLabel.setFont(new Font("dialog", Font.BOLD, 12));
		instructionLabel.setForeground(Color.black);
		instructionLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		mSourceFileNameField.setColumns(35);

		labelAndFilterPanel.add(instructionLabel, BorderLayout.WEST);
		labelAndFilterPanel.add(mFilterPanel = new FilterPanel(), BorderLayout.EAST);

		classNamePanel.add(labelAndFilterPanel);
		classNamePanel.add(mSourceFileNameField);

		// listen for changes to the document (the text) in the text field, and
		// reload the class list accordingly.
		//{{{ DocumentListener methods.
		mSourceFileNameField.getDocument().addDocumentListener(
			new DocumentListener()
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
					String documentText = null;
					try
					{
						documentText = e.getDocument().getText(0, e.getDocument().getLength());
					}
					catch(BadLocationException ble)
					{
						Log.log(Log.MESSAGE, FindFileWindow.class,
							"[MacroManager Plugin]: BadLocationException caught!");
						return;
					}

					updateList(documentText);
					// fix for focus problems under windows
					SwingUtilities.invokeLater(
						new Runnable()
						{
							public void run()
							{
								mSourceFileNameField.requestFocus();
							}
						});
				}
			});
		// }}}

		// escape listener (to close the window when escape is pressed).
		addEscapeKeyStroke(mSourceFileNameField);
		return classNamePanel;
	}


	//}}}

	private static final EditAction[] actions = Macros.getMacroActionSet().getActions();
	private static ArrayList alist = new ArrayList(actions.length);
	static
	{
		for(int i = 0; i < actions.length; i++)
			alist.add(actions[i]);
		Collections.sort(alist, new Comparator() {
			public int compare(Object o1, Object o2)
			{
				EditAction e1 = (EditAction)o1;
				EditAction e2 = (EditAction)o2;
				return e1.getLabel().compareTo(e2.getLabel());
			}
		});
	}


	// {{{ updateList method.
	private void updateList(String documentText)
	{
		if(documentText.length() == 0)
		{
			setSourceFiles(alist);
			return;
		}

		ArrayList sourceFilesStartingWithLetter = null;

		if(useRegexp)
		{
			try
			{
				mSourceFileFilter.setRegularExpressionString(mSourceFileNameField.getText());
			}
			catch(Exception e)
			{
				mSourceFileFilter.clearRegularExpression();

				Log.log(Log.MESSAGE, FindFileWindow.class,
					"[MacroManager Plugin]: Invalid regular expression: " + mSourceFileNameField.getText());
			}

			sourceFilesStartingWithLetter =
				new ArrayList();
			sourceFilesStartingWithLetter.addAll(alist);
			mSourceFileFilter.filter(sourceFilesStartingWithLetter);
		}
		else
		{
			sourceFilesStartingWithLetter =
				new ArrayList();
			sourceFilesStartingWithLetter.addAll(alist);

			// iterate through list and remove those source files that do not
			// start with the text in the source file name field
			for(Iterator i = sourceFilesStartingWithLetter.iterator(); i.hasNext(); )
			{
				EditAction currentSourcePathFile = (EditAction)i.next();
				String currentFileName = currentSourcePathFile.getLabel();

				currentFileName = currentFileName.toLowerCase();
				documentText = documentText.toLowerCase();

				if(!currentFileName.startsWith(documentText))
					i.remove();
			}
		}
		setSourceFiles(sourceFilesStartingWithLetter);
	}


	// }}}

	//{{{ isKeyHandledBySourceFileList method.
	/**
	 *@param  keyCode Description of the Parameter
	 *@return  whether or not the specified keycode is one that should be handled
	 *      by the class list
	 */
	private boolean isKeyHandledBySourceFileList(int keyCode)
	{
		return (keyCode == KeyEvent.VK_UP ||
			keyCode == KeyEvent.VK_DOWN ||
			keyCode == KeyEvent.VK_ENTER ||
			keyCode == KeyEvent.VK_PAGE_UP ||
			keyCode == KeyEvent.VK_PAGE_DOWN);
	}


	//}}}

	//{{{ notifyFileSelectionListeners method.
	/**
	 *  Notify any import selection listeners that an import statement was
	 *  selected.
	 *
	 *@param  sourceFile Description of the Parameter
	 */
	private void notifyFileSelectionListeners(EditAction sourceFile)
	{
		if(mFileSelectionListeners != null)
			for(Iterator i = mFileSelectionListeners.iterator(); i.hasNext(); )
			{
				FileSelectionListener listener = (FileSelectionListener)i.next();
				listener.fileSelected(sourceFile);
			}

	}


	//}}}

	//{{{ selectionMade method.
	private void selectionMade()
	{
		if(!mSourceFileListModel.isEmpty())
		{
			EditAction selectedAction = (EditAction)mSourceFileList.getSelectedValue();
			closeWindow();
			notifyFileSelectionListeners(selectedAction);
		}
	}

	//}}}


	//{{{ closeWindow method.
	private void closeWindow()
	{
		mSourceFileListWindow.setVisible(false);
		setVisible(false);
	}

	//}}}


	//{{{ escape listener (to close the window when escape is pressed).
	private void addEscapeKeyStroke(JComponent component)
	{
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		component.getInputMap().put(ks, ks);
		component.getActionMap().put(ks,
			new AbstractAction()
			{
				public void actionPerformed(ActionEvent ae)
				{
					closeWindow();
				}
			});
	}

	//}}}


	// {{{ FilterPanel class
	/**
	 *  Small panel for entering a regular expression string
	 */
	public class FilterPanel extends JPanel
	{
		private JCheckBox mFilterCheckBox;


		//{{{ constructor.
		/**
		 *  Constructor for the FilterPanel object
		 */
		public FilterPanel()
		{
			setLayout(new BorderLayout(6, 6));
			mFilterCheckBox = new JCheckBox(jEdit.getProperty("macro-manager.popup.filter-message"));
			mFilterCheckBox.setMnemonic('F');
			mFilterCheckBox.addItemListener(
				new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						useRegexp = (e.getStateChange() == ItemEvent.SELECTED);
						jEdit.setBooleanProperty("MacroManager.FilterApplied",
							(e.getStateChange() == ItemEvent.SELECTED));
						updateList(mSourceFileNameField.getText());
						SwingUtilities.invokeLater(
							new Runnable()
							{
								public void run()
								{
									mSourceFileNameField.requestFocus();
								}
							});
					}
				});

			mFilterCheckBox.setSelected(
				jEdit.getBooleanProperty("MacroManager.FilterApplied", false));
			addEscapeKeyStroke(mFilterCheckBox);
			add(mFilterCheckBox, BorderLayout.WEST);
		}
		//}}}
	}


	//}}}

	public class SourceFileListModel extends AbstractListModel
	{
		// the list of source files
		private java.util.List mContents = new ArrayList();


		public void refreshModel(java.util.List newContents)
		{
			// if there are any elements, then remove them and fire appropriate event
			if(!mContents.isEmpty())
			{
				int currentContentsSize = mContents.size();
				mContents.clear();
				fireIntervalRemoved(this, 0, currentContentsSize - 1);
			}

			// add any new elements to the list
			if(!newContents.isEmpty())
			{
				// add new contents
				mContents.addAll(newContents);
				fireIntervalAdded(this, 0, mContents.size() - 1);
			}
		}


		public Object getElementAt(int index)
		{
			return mContents.get(index);
		}


		public int getSize()
		{
			return mContents.size();
		}


		public boolean isEmpty()
		{
			return mContents.isEmpty();
		}
	}


	public class SourceFileListCellRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent
			(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			Component comp =
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(comp instanceof JLabel)
			{
				StringBuffer showBuffer = new StringBuffer();
				if(value instanceof EditAction)
				{
					EditAction file = (EditAction)value;
					((JLabel)comp).setText(file.getLabel());
				}
			}
			return comp;
		}
	}


	public class KeyAdapterProxy implements KeyListener
	{
		private JComponent mTarget;


		/**
		 *  Constructor for the KeyAdapterProxy object
		 *
		 *@param  target Description of the Parameter
		 */
		public KeyAdapterProxy(JComponent target)
		{
			mTarget = target;
		}


		protected boolean shouldForwardEvent(KeyEvent e)
		{
			return true;
		}


		public void keyPressed(KeyEvent e)
		{
			handleKeyEvent(e);
		}


		public void keyReleased(KeyEvent e)
		{
			handleKeyEvent(e);
		}


		public void keyTyped(KeyEvent e)
		{
			handleKeyEvent(e);
		}


		private void handleKeyEvent(KeyEvent e)
		{
			if(shouldForwardEvent(e))
				mTarget.dispatchEvent(e);
		}
	}
}

