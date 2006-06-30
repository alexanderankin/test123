package xsearch;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.DockableWindowUpdate;
import org.gjt.sp.jedit.msg.SearchSettingsChanged;
import org.gjt.sp.jedit.search.AllBufferSet;
import org.gjt.sp.jedit.search.CurrentBufferSet;
import org.gjt.sp.jedit.search.DirectoryListSet;
import org.gjt.sp.jedit.search.SearchFileSet;
import org.gjt.sp.util.Log;

//}}}

/**

 * Dockable Search and replace Component
 * 
 * @author Slava Pestov, Alan Ezust
 * @version $Id$ derived
 *          version $Id$

 */
public class XSearchPanel extends JPanel implements EBComponent, DefaultFocusComponent
{
	// {{{ Constants
	/**
	 * Default file set.
	 * 
	 * @since jEdit 3.2pre2
	 */
	public static final String NAME = "xsearch-dockable";


	// }}}

//	private HashMap panels = null;

	// {{{ Private members

	// private static HashMap viewHash = new HashMap();

	// {{{ Instance variables
	private View view;

	// snapshot status of input fields
	private SearchReplaceFieldData srFieldData;

	private KeyHandler keyHandler;

	// search area
	private XSearchHistoryTextField find;

	private SettingsHistoryModel settingsHistory;

	private HistoryTextField replace;

	private JButton regexpSupportButton;

	private ReSupportPopup popup;

	private JRadioButton stringReplace, beanShellReplace;

//	 1.,4 awt style:
//	private CheckboxMenuItem autoSync = new CheckboxMenuItem("Auto-Sync");	

	// Requires 1.5? 
	private JCheckBoxMenuItem autoSync = new JCheckBoxMenuItem("Auto-Sync");

	private JPanel content;

	private JPanel centerPanel;

	private JPanel southPanel;

	private JPanel globalFieldPanel;

	private JPanel searchSettingsPanel;

	private JPanel multiFilePanel;

	private JPanel extendedOptionsPanel;

	private JPanel grid;

	private Box buttonsPanel;

	private Box replaceModeBox;

	// private Box foldBox;

	// components inside of panels
	private JLabel fieldPanelReplaceLabel;

	private JLabel currentSelectedOptionsLabel = new JLabel(jEdit
		.getProperty("search.currOpt.label")); 

	private int optionsLabelIndex;

	private StringBuffer currentSelectedSettingsOptions = new StringBuffer();

//	private StringBuffer currentSelectedExtendedOptions = new StringBuffer();

	private JCheckBox showSettings;

	private JCheckBox showReplace;

	private JCheckBox showExtendedOptions;

	// extended options: word part search
	private JRadioButton wordPartWholeRadioBtn;

	private JRadioButton wordPartPrefixRadioBtn;

	private JRadioButton wordPartSuffixRadioBtn;

	private JRadioButton wordPartDefaultRadioBtn = new JRadioButton();

	private JRadioButton tentativRadioBtn;

	// extended options: fold search
	private JRadioButton searchFoldInsideRadioBtn;

	private JRadioButton searchFoldOutsideRadioBtn;

	private JRadioButton searchFoldDefaultRadioBtn = new JRadioButton();

	// private JRadioButton searchFoldActualRadioBtn =
	// searchFoldDefaultRadioBtn;

	// extended options: comment search
	private JRadioButton searchCommentInsideRadioBtn;

	private JRadioButton searchCommentOutsideRadioBtn;

	private JRadioButton searchCommentDefaultRadioBtn = new JRadioButton();

	// private JRadioButton searchCommentActualRadioBtn =
	// searchCommentDefaultRadioBtn;
	private JLabel hyperRangeLabel;

	private JLabel hyperRangeLabelUp;

	private JLabel hyperRangeLabelDown;

	private JTextField hyperRangeUpTextField;

	private JTextField hyperRangeDownTextField;

	// extended options: load search settings
	private JRadioButton searchSettingsHistoryRadioBtn;

	private Component fieldPanelVerticalStrut = Box.createVerticalStrut(3);

	// search settings
	private JCheckBox keepDialog, ignoreCase, regexp, hyperSearch, wrap;

	private JRadioButton searchFromTop, searchBack, searchForward;

	private JRadioButton searchSelection, searchCurrentBuffer, searchAllBuffers,
		searchDirectory, searchProject;
		
	// "Search In: "
	private ButtonGroupHide filesetGrp;
	private ButtonGroupHide wordPartGrp;
	private ButtonGroupHide commentGrp;
	private ButtonGroupHide foldGrp;

	// extended options: column search
	private JRadioButton columnRadioBtn;

	private JRadioButton columnExpandTabsRadioBtn;

	private JLabel leftColumnLabel;

	private JTextField columnLeftColumnField;

	private JLabel rightColumnLabel;

	private JTextField columnRightColumnField;

	// extended options: row search
	private JRadioButton rowRadioBtn;

	private JLabel leftRowLabel;

	private JTextField rowLeftRowField;

	private JLabel rightRowLabel;

	private JTextField rowRightRowField;

	// multifile settings
	private HistoryTextField filter, directory;

	private JCheckBox searchSubDirectories;
	private JCheckBox skipBinaryFiles;
	private JCheckBox skipHidden;
	private JButton choose;

	// private JCheckBox synchronize;
	private Button synchronize;
	//	private JToggleButton synchronize;
	// buttons
	private JButton findBtn, /* replaceBtn, */replaceAndFindBtn, replaceAllBtn, closeBtn;

	private JButton findAllBtn; // since XSearch0.4

	private JButton resetSettingsButton; // since XSearch0.7

	private boolean resetSettingsButtonPresent = false; // since
								// XSearch0.7

	private boolean keepDialogChanged; // flag: keep dialog changed by
						// user

	private boolean saving;

	// }}}

	// {{{ XSearchPanel constructor
	/**
	 * Creates a new search and replace panel, which is dockable or
	 * floating.
	 * 
	 * @param view
	 *                The view
	 * @param searchString
	 *                The search string
	 * @param searchIn
	 *                One of CURRENT_BUFFER, ALL_BUFFERS, or DIRECTORY
	 */
	public XSearchPanel(View view)
	// , String searchString, int searchIn)
	{

		this.view = view;
		keyHandler = new KeyHandler();
		content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(0, 12, 12, 12));

		centerPanel = new JPanel(new BorderLayout());
		southPanel = new JPanel(new BorderLayout());
		// rwchg: switchable panel display
		// memorize add-panels
		globalFieldPanel = createFieldPanel();
		searchSettingsPanel = createSearchSettingsPanel();
		multiFilePanel = createMultiFilePanel();
		buttonsPanel = createButtonsPanel();
		extendedOptionsPanel = createExtendedOptionsPanelGridBag();

		centerPanel.add(BorderLayout.NORTH, globalFieldPanel);
		centerPanel.add(BorderLayout.CENTER, searchSettingsPanel);

		southPanel.add(BorderLayout.CENTER, extendedOptionsPanel);
		southPanel.add(BorderLayout.SOUTH, multiFilePanel);

		content.add(BorderLayout.CENTER, centerPanel);
		content.add(BorderLayout.SOUTH, southPanel);
		content.add(BorderLayout.EAST, buttonsPanel);

		// scrollPane = new JScrollPane();
		// scrollPane.add(content);

		add(content);

		load();

		// moved to (0.9)
		// setSearchString(searchString,searchIn);

		// all pre-selections are done: show / hide panels
		if (!showSettings.isSelected())
			showHideOptions(showSettings);
		if (!showReplace.isSelected())
			showHideOptions(showReplace);
		if (!showExtendedOptions.isSelected())
			showHideOptions(showExtendedOptions);
		if (!searchDirectory.isSelected() && !searchAllBuffers.isSelected())
			showHideOptions(searchDirectory);
		setupCurrentSelectedOptionsLabel();

		jEdit.unsetProperty("search.width");
		jEdit.unsetProperty("search.d-width");
		jEdit.unsetProperty("search.height");
		jEdit.unsetProperty("search.d-height");

		EditBus.addToBus(this);

		
		

	} // }}}

	public static XSearchPanel getSearchPanel(View view)
	{
		DockableWindowManager wm = view.getDockableWindowManager();
		wm.addDockableWindow(XSearchPanel.NAME);
		XSearchPanel panel = (XSearchPanel) wm.getDockable(XSearchPanel.NAME);
		panel.load();
		panel.revalidatePanels();

		return panel;
	}

	public FloatingWindowContainer getFrame()
	{
		Container parent = getParent();
		FloatingWindowContainer fwc = null;
		while (parent != null)
		{
			try
			{
				fwc = (FloatingWindowContainer) parent;
				return fwc;
			}
			catch (ClassCastException cce)
			{
			}
			parent = parent.getParent();
		}
		return fwc;
	}

	public static void showSearchPanel(View view, String searchString, int searchIn)
	{

		if (SearchAndReplace.debug)
			Log.log(Log.DEBUG, BeanShell.class,
				"XSearchPanel.82: invoke showSearchPanel with searchString = "
					+ searchString);
		XSearchPanel panel = getSearchPanel(view);
		/*
		 * in the org.gjt.sp.jedit.search package, the dialog is
		 * preloaded therefore, no check is necessary for a plugin, we
		 * do not have such a lux
		 */

		// check if project viewer got active / inactive
		panel.enableSearchProject();
		panel.setSearchString(searchString, searchIn);
		SearchAndReplace.resetIgnoreFromTop();
		panel.keepDialogChanged = false;

	} // }}}

	public void setCurrentBuffer() {
		searchCurrentBuffer.setSelected(true);
	}
	
	public void setCurrentSelection() {
		searchSelection.setSelected(true);
	}

	// {{{ setSearchString() method
	/**
	 * Sets the search string.
	 * 
	 * @since jEdit 4.0pre5
	 */
	public void setSearchString(String searchString, int searchIn)
	{
		find.setText(null);
		replace.setText(null);

		if (searchString == null)
			filesetGrp.setSelected(searchCurrentBuffer, true);
		else
		{
			if (searchString.indexOf('\n') == -1)
			{
				find.setText(searchString);
				find.selectAll();
				filesetGrp.setSelected(searchCurrentBuffer, true);
			}
			else if (searchIn == XSearch.SEARCH_TYPE_CURRENT_BUFFER)
			{
				/*
				 * more than 1 line selected set "search in
				 * selection" set "hypersearch" setup start/end
				 * row
				 */
				filesetGrp.setSelected(searchSelection, true);
				hyperSearch.setSelected(true);
				setupStartEndRowFromSelection();
			}
		}

		if (searchIn == XSearch.SEARCH_TYPE_CURRENT_BUFFER)
		{
			if (!searchSelection.isSelected())
			{
				// might be already selected, see above.
				filesetGrp.setSelected(searchCurrentBuffer, true);

				/*
				 * this property is only loaded and saved if the
				 * 'current buffer' file set is selected.
				 * otherwise, it defaults to on.
				 */
				hyperSearch.setSelected(jEdit
					.getBooleanProperty("search.hypersearch.toggle"));
			}
		}
		else if (searchIn == XSearch.SEARCH_TYPE_ALL_BUFFERS)
		{
			filesetGrp.setSelected(searchAllBuffers, true);
			hyperSearch.setSelected(true);
		}
		else if(searchIn == XSearch.SEARCH_TYPE_PROJECT)
		{
			filesetGrp.setSelected(searchProject, true);
			hyperSearch.setSelected(true);
		}
		else if (searchIn == XSearch.SEARCH_TYPE_DIRECTORY)
		{
			SearchFileSet fileset = SearchAndReplace.getSearchFileSet();

			if (fileset instanceof DirectoryListSet)
			{
				filter.setText(((DirectoryListSet) fileset).getFileFilter());
				directory.setText(((DirectoryListSet) fileset).getDirectory());
				Log.log(Log.DEBUG, BeanShell.class,
					"XSearchPanel.160: directory.getText = "
						+ directory.getText());
				searchSubDirectories.setSelected(((DirectoryListSet) fileset)
					.isRecursive());
			}

			hyperSearch.setSelected(true);
			filesetGrp.setSelected(searchDirectory, true);
			/* if (synchronize.isSelected())
				synchronizeMultiFileSettings(); */
			// Log.log(Log.DEBUG, BeanShell.class,"+++
			// XSearchPanel.191");
			showHideOptions(searchDirectory); // 20031228
		}

		updateEnabled();
		revalidatePanels();
	} // }}}

	/**
	 * Also sets the parent frame if it's a floating dockwindow
	 */
	public void setVisible(boolean isVisible)
	{
		JFrame frame = getFrame();
		if (frame != null)
		{
			frame.setVisible(isVisible);
			if (isVisible)
				frame.pack();
		}
		super.setVisible(isVisible);
	}

	// {{{ ok() method
	public void ok()
	{

		if (SearchAndReplace.debug)
		{
			Log.log(Log.DEBUG, BeanShell.class,
				"XSearchPanel.331: ok invoked with find.getText() = "
					+ find.getText() + ", regexp.isSelected() = "
					+ regexp.isSelected()
					+ ", wordPartPrefixRadioBtn.isSelected() = "
					+ wordPartPrefixRadioBtn.isSelected()
					+ ", wordPartWholeRadioBtn.isSelected() = "
					+ wordPartWholeRadioBtn.isSelected());
		}

		try
		{

			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			if (!save(false))
				return;

			if (searchSelection.isSelected()
				&& view.getTextArea().getSelectionCount() == 0)
			{
				GUIUtilities.error(view, "search-no-selection", null);
				return;
			}

			if (hyperSearch.isSelected() || searchSelection.isSelected())
			{
				if (SearchAndReplace
					.hyperSearch(view, searchSelection.isSelected()))
					closeOrKeepDialog();
			}
			else
			{
				boolean searchResult = SearchAndReplace.find(view);
				if (searchResult)
					closeOrKeepDialog();
				else
				{
					find.requestFocus();
				}
			}
		}
		finally
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		if (!keepDialog.isSelected())
		{
			setVisible(false);
		}
	} // }}}

	// {{{ cancel() method
	public void cancel()
	{
		save(true);
		setVisible(false);

	} // }}}

	// {{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{

		if (msg instanceof SearchSettingsChanged)
		{
			if (!saving)
				load();
		}
		/*
		 * else if(msg instanceof ViewUpdate) { ViewUpdate vmsg =
		 * (ViewUpdate)msg; if(vmsg.getView() == view && vmsg.getWhat() ==
		 * ViewUpdate.CLOSED) { viewHash.remove(view); } }
		 */
	} // }}}

	// {{{ dispose() method
	public void dispose()
	{
		EditBus.removeFromBus(this);
	} // }}}

	// {{{ createFieldPanel() method
	private JPanel createFieldPanel()
	{
		ButtonActionHandler buttonActionHandler = new ButtonActionHandler();

		JPanel fieldPanel = new JPanel(new VariableGridLayout(
			VariableGridLayout.FIXED_NUM_COLUMNS, 1));
		fieldPanel.setBorder(new EmptyBorder(0, 0, 12, 12));

		JLabel label = new JLabel(jEdit.getProperty("search.find"));
		label.setDisplayedMnemonic(jEdit.getProperty("search.find.mnemonic").charAt(0));
		find = new XSearchHistoryTextField("find", true, false);
		
		find.addKeyListener(keyHandler);
		
		find.setColumns(20);
		if (jEdit.getBooleanProperty("xsearch.textAreaFont", true))
			find.setFont(UIManager.getFont("TextArea.font"));

		find.addActionListener(buttonActionHandler);
		label.setLabelFor(find);
		label.setBorder(new EmptyBorder(12, 0, 2, 0));
		fieldPanel.add(label);
		// add: "Search for (press up arrow to recall previous)"
		Box findBox = new Box(BoxLayout.X_AXIS);
		findBox.add(find);
		regexpSupportButton = new ReSupportButton(jEdit
			.getProperty("search.ext.regexp-support.label"));
		regexpSupportButton.setMinimumSize(new Dimension(20, 10));
		regexpSupportButton.setPreferredSize(new Dimension(30, 12));
		regexpSupportButton.setMargin(new Insets(2, 2, 2, 2));
		regexpSupportButton.addMouseListener(new RegexpSupportMouseListener());
		regexpSupportButton.addActionListener(new RegexpSupportActionListener());
		findBox.add(regexpSupportButton);
		fieldPanel.add(findBox);
		// add: <search input textField>
		settingsHistory = new SettingsHistoryModel("search.settings");

		// rwchg add panel display selection radio buttons
		SelectivShowActionHandler selectivShowActionHandler = new SelectivShowActionHandler();
		Box selectivShowBox = new Box(BoxLayout.X_AXIS);
		selectivShowBox.setBorder(new TitledBorder(jEdit.getProperty("search.show-options")));

		showSettings = new JCheckBox(jEdit.getProperty("search.show-settings"));
		showSettings.setSelected(jEdit.getBooleanProperty("search.show-settings.toggle"));
		showSettings.addActionListener(selectivShowActionHandler);
		showReplace = new JCheckBox(jEdit.getProperty("search.show-replace"));
		showReplace.setSelected(jEdit.getBooleanProperty("search.show-replace.toggle"));
		showReplace.addActionListener(selectivShowActionHandler);
		showExtendedOptions = new JCheckBox(jEdit.getProperty("search.show-extended"));
		showExtendedOptions.setSelected(jEdit
			.getBooleanProperty("search.show-extended.toggle"));
		showExtendedOptions.addActionListener(selectivShowActionHandler);

		selectivShowBox.add(showSettings);
		selectivShowBox.add(showReplace);
		selectivShowBox.add(showExtendedOptions);

		// add: <selectiv show options: search, replace, extended
		fieldPanel.add(currentSelectedOptionsLabel);
		// add: <display of current selected find options
		optionsLabelIndex = fieldPanel.getComponentCount() - 1;

		fieldPanelReplaceLabel = new JLabel(jEdit.getProperty("search.replace"));
		fieldPanelReplaceLabel.setDisplayedMnemonic(jEdit.getProperty(
			"search.replace.mnemonic").charAt(0));
		fieldPanelReplaceLabel.setBorder(new EmptyBorder(12, 0, 0, 0));
		// add: "Replace with"
		fieldPanel.add(fieldPanelReplaceLabel); 

		ButtonGroup grp = new ButtonGroup();
		ReplaceActionHandler replaceActionHandler = new ReplaceActionHandler();

		// we use a custom JRadioButton subclass that returns
		// false for isFocusTraversable() so that the user can
		// tab from the search field to the replace field with
		// one keystroke

		replaceModeBox = new Box(BoxLayout.X_AXIS);
//		TitledBorder b = new TitledBorder(jEdit.getProperty("search.replace"));
//		replaceModeBox.setBorder(b);
		
		
		stringReplace = new MyJRadioButton(jEdit.getProperty("search.string-replace-btn"));
		stringReplace.addActionListener(replaceActionHandler);
		grp.add(stringReplace);
		replaceModeBox.add(stringReplace);

		replaceModeBox.add(Box.createHorizontalStrut(12));

		beanShellReplace = new MyJRadioButton(jEdit
			.getProperty("search.beanshell-replace-btn"));
		beanShellReplace.addActionListener(replaceActionHandler);
		grp.add(beanShellReplace);
		replaceModeBox.add(beanShellReplace);

		fieldPanel.add(replaceModeBox);
		// add: <replace mode: Text, Return value of BeanShell snippet
		fieldPanel.add(fieldPanelVerticalStrut);

		
		replace = new HistoryTextField("replace");
		replace.addKeyListener(keyHandler);
		if (jEdit.getBooleanProperty("xsearch.textAreaFont", true))
			replace.setFont(UIManager.getFont("TextArea.font"));
		replace.addActionListener(buttonActionHandler);
		fieldPanelReplaceLabel.setLabelFor(replace);
		fieldPanel.add(replace);
		// add the show options after the replace with field.
		fieldPanel.add(selectivShowBox);

		// add: <replace input textField>
		return fieldPanel;
	} // }}}

	private GridBagConstraints makeStdConstraints(int row, int col, int size, Insets ins)
	{
		return new GridBagConstraints(col, row, size, 1, 0.0, 0.0, GridBagConstraints.WEST,
			GridBagConstraints.NONE, ins, 0, 0);
	}

	// {{{ createExtendedOptionsPanelGridBag() method
	private JPanel createExtendedOptionsPanelGridBag()
	{
		JPanel extendedOptions = new JPanel(new GridBagLayout());
		extendedOptions.setBorder(new EmptyBorder(0, 0, 12, 12));

		ExtendedOptionsActionHandler extendedOptionsActionHandler = new ExtendedOptionsActionHandler();

		int rowCounter = 0; // grid x
		Insets stdInset = new Insets(0, 0, 1, 5);

		/***************************************************************
		 * word part handling: whole word / prefix / suffix
		 **************************************************************/
		wordPartWholeRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.word-whole"));
		wordPartWholeRadioBtn.addActionListener(extendedOptionsActionHandler);
		wordPartPrefixRadioBtn = new JRadioButton(jEdit
			.getProperty("search.ext.word-prefix"));
		wordPartPrefixRadioBtn.addActionListener(extendedOptionsActionHandler);
		wordPartSuffixRadioBtn = new JRadioButton(jEdit
			.getProperty("search.ext.word-suffix"));
		wordPartSuffixRadioBtn.addActionListener(extendedOptionsActionHandler);
		/*
		 * extendedOptions.add(wordPartWholeRadioBtn, new
		 * GridBagConstraints( 0, rowCounter, 2, 1, 0.0, 0.0,
		 * GridBagConstraints.WEST, GridBagConstraints.NONE, stdInset,
		 * 0, 0)); extendedOptions.add(wordPartPrefixRadioBtn, new
		 * GridBagConstraints( 2, rowCounter, 2, 1, 0.0, 0.0,
		 * GridBagConstraints.WEST, GridBagConstraints.NONE, stdInset,
		 * 0, 0)); extendedOptions.add(wordPartSuffixRadioBtn, new
		 * GridBagConstraints( 4, rowCounter, 2, 1, 0.0, 0.0,
		 * GridBagConstraints.WEST, GridBagConstraints.NONE, stdInset,
		 * 0, 0));
		 */
		if (jEdit.getBooleanProperty("xsearch.wordPartSearch", true))
		{
			extendedOptions.add(wordPartWholeRadioBtn, makeStdConstraints(rowCounter,
				0, 2, stdInset));
			extendedOptions.add(wordPartPrefixRadioBtn, makeStdConstraints(rowCounter,
				2, 2, stdInset));
			extendedOptions.add(wordPartSuffixRadioBtn, makeStdConstraints(rowCounter,
				4, 2, stdInset));
			rowCounter++;
		}

		wordPartGrp = new ButtonGroupHide();

		wordPartGrp.add(wordPartDefaultRadioBtn);
		wordPartGrp.add(wordPartWholeRadioBtn);
		wordPartGrp.add(wordPartPrefixRadioBtn);
		wordPartGrp.add(wordPartSuffixRadioBtn);
		wordPartDefaultRadioBtn.setSelected(true);

		/***************************************************************
		 * column handling
		 **************************************************************/
		columnRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.column"));
		columnRadioBtn.addActionListener(extendedOptionsActionHandler);

		columnExpandTabsRadioBtn = new JRadioButton(jEdit
			.getProperty("search.ext.column.expand-tabs"));
		columnExpandTabsRadioBtn.setSelected(true);
		// columnExpandTabsRadioBtn.addActionListener(extendedOptionsActionHandler);

		columnLeftColumnField = new JTextField(3);
		leftColumnLabel = new JLabel(jEdit.getProperty("search.ext.column.left"));
		columnRightColumnField = new JTextField(3);
		rightColumnLabel = new JLabel(jEdit.getProperty("search.ext.column.right"));

		columnExpandTabsRadioBtn.setEnabled(false);
		columnLeftColumnField.setEnabled(false);
		leftColumnLabel.setEnabled(false);
		columnRightColumnField.setEnabled(false);
		rightColumnLabel.setEnabled(false);

		if (jEdit.getBooleanProperty("xsearch.columnSearch", true))
		{
			extendedOptions.add(columnRadioBtn, makeStdConstraints(rowCounter, 0, 1,
				stdInset));
			extendedOptions.add(columnExpandTabsRadioBtn, makeStdConstraints(
				rowCounter, 6, 1, stdInset));
			extendedOptions.add(leftColumnLabel, makeStdConstraints(rowCounter, 2, 1,
				stdInset));
			extendedOptions.add(columnLeftColumnField, makeStdConstraints(rowCounter,
				3, 1, stdInset));
			extendedOptions.add(rightColumnLabel, makeStdConstraints(rowCounter, 4, 1,
				stdInset));
			extendedOptions.add(columnRightColumnField, makeStdConstraints(rowCounter,
				5, 1, stdInset));
			rowCounter++;
		}

		/***************************************************************
		 * row handling
		 **************************************************************/
		rowRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.row"));
		rowRadioBtn.addActionListener(extendedOptionsActionHandler);

		rowLeftRowField = new JTextField(3);
		leftRowLabel = new JLabel(jEdit.getProperty("search.ext.row.left"));
		rowRightRowField = new JTextField(3);
		rowRightRowField.setMargin(new Insets(2, 2, 2, 2));

		rightRowLabel = new JLabel(jEdit.getProperty("search.ext.row.right"));

		rowLeftRowField.setEnabled(false);
		leftRowLabel.setEnabled(false);
		rowRightRowField.setEnabled(false);
		rightRowLabel.setEnabled(false);

		if (jEdit.getBooleanProperty("xsearch.rowSearch", true))
		{
			extendedOptions.add(rowRadioBtn, makeStdConstraints(rowCounter, 0, 1,
				stdInset));
			extendedOptions.add(leftRowLabel, makeStdConstraints(rowCounter, 2, 1,
				stdInset));
			extendedOptions.add(rowLeftRowField, makeStdConstraints(rowCounter, 3, 1,
				stdInset));
			extendedOptions.add(rightRowLabel, makeStdConstraints(rowCounter, 4, 1,
				stdInset));
			extendedOptions.add(rowRightRowField, makeStdConstraints(rowCounter, 5, 1,
				stdInset));
			rowCounter++;
		}

		/***************************************************************
		 * tentativ search
		 **************************************************************/
		tentativRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.tentativ"));
		if (jEdit.getBooleanProperty("xsearch.tentativSearch", false))
			extendedOptions.add(tentativRadioBtn);

		/***************************************************************
		 * folding handling search only o inside fold o outside fold
		 **************************************************************/
		JLabel searchFoldLabel = new JLabel(jEdit.getProperty("search.ext.fold"));
		searchFoldInsideRadioBtn = new JRadioButton(jEdit
			.getProperty("search.ext.fold-inside"));
		searchFoldOutsideRadioBtn = new JRadioButton(jEdit
			.getProperty("search.ext.fold-outside"));
		searchFoldInsideRadioBtn.addActionListener(extendedOptionsActionHandler);
		searchFoldOutsideRadioBtn.addActionListener(extendedOptionsActionHandler);
		if (jEdit.getBooleanProperty("xsearch.foldSearch", true))
		{
			extendedOptions.add(searchFoldLabel, makeStdConstraints(rowCounter, 0, 2,
				stdInset));
			extendedOptions.add(searchFoldInsideRadioBtn, makeStdConstraints(
				rowCounter, 2, 2, stdInset));
			extendedOptions.add(searchFoldOutsideRadioBtn, makeStdConstraints(
				rowCounter, 4, 2, stdInset));
			rowCounter++;
		}

		foldGrp = new ButtonGroupHide();
		foldGrp.add(searchFoldDefaultRadioBtn);
		foldGrp.add(searchFoldInsideRadioBtn);
		foldGrp.add(searchFoldOutsideRadioBtn);
		searchFoldDefaultRadioBtn.setSelected(true);

		/***************************************************************
		 * comment handling search only o inside comment o outside
		 * comment
		 **************************************************************/
		JLabel searchCommentLabel = new JLabel(jEdit.getProperty("search.ext.comment"));
		searchCommentInsideRadioBtn = new JRadioButton(jEdit
			.getProperty("search.ext.comment-inside"));
		searchCommentOutsideRadioBtn = new JRadioButton(jEdit
			.getProperty("search.ext.comment-outside"));
		// searchCommentInsideRadioBtn.addActionListener(extendedOptionsActionHandler);
		// searchCommentOutsideRadioBtn.addActionListener(extendedOptionsActionHandler);
		if (jEdit.getBooleanProperty("xsearch.commentSearch", true))
		{
			extendedOptions.add(searchCommentLabel, makeStdConstraints(rowCounter, 0,
				2, stdInset));
			extendedOptions.add(searchCommentInsideRadioBtn, makeStdConstraints(
				rowCounter, 2, 2, stdInset));
			extendedOptions.add(searchCommentOutsideRadioBtn, makeStdConstraints(
				rowCounter, 4, 2, stdInset));
			rowCounter++;
		}
		commentGrp = new ButtonGroupHide();
		commentGrp.add(searchCommentDefaultRadioBtn);
		commentGrp.add(searchCommentInsideRadioBtn);
		commentGrp.add(searchCommentOutsideRadioBtn);
		searchCommentDefaultRadioBtn.setSelected(true);

		/***************************************************************
		 * hyper range handling hyper range: upper [] lower []
		 **************************************************************/
		hyperRangeLabel = new JLabel(jEdit.getProperty("search.ext.hyperRangeLabel"));
		hyperRangeLabelUp = new JLabel(jEdit.getProperty("search.ext.hyperRangeLabelUp"));
		hyperRangeLabelDown = new JLabel(jEdit
			.getProperty("search.ext.hyperRangeLabelDown"));
		hyperRangeUpTextField = new JTextField(3);
		hyperRangeDownTextField = new JTextField(3);
		if (jEdit.getBooleanProperty("xsearch.hyperRange", true))
		{
			extendedOptions.add(hyperRangeLabel, makeStdConstraints(rowCounter, 0, 2,
				stdInset));
			extendedOptions.add(hyperRangeLabelUp, makeStdConstraints(rowCounter, 2, 1,
				stdInset));
			extendedOptions.add(hyperRangeUpTextField, makeStdConstraints(rowCounter,
				3, 1, stdInset));
			extendedOptions.add(hyperRangeLabelDown, makeStdConstraints(rowCounter, 4,
				1, stdInset));
			extendedOptions.add(hyperRangeDownTextField, makeStdConstraints(rowCounter,
				5, 1, stdInset));
			rowCounter++;
		}
		/***************************************************************
		 * search settings history
		 **************************************************************/
		searchSettingsHistoryRadioBtn = new JRadioButton(jEdit
			.getProperty("search.ext.settings-history"));
		searchSettingsHistoryRadioBtn.addActionListener(extendedOptionsActionHandler);
		if (jEdit.getBooleanProperty("xsearch.settingsHistory", true))
		{
			extendedOptions.add(searchSettingsHistoryRadioBtn, makeStdConstraints(
				rowCounter, 0, 2, stdInset));
			rowCounter++;
		}

		return extendedOptions;
	} // }}}

	// {{{ createSearchSettingsPanel() method
	private JPanel createSearchSettingsPanel()
	{
		
		
		
		SettingsActionHandler settingsActionHandler = new SettingsActionHandler();
		SelectivShowActionHandler selectivShowActionHandler = new SelectivShowActionHandler();
		
		
		JPanel searchInPanel = new JPanel();
		searchInPanel.setBorder(new TitledBorder(jEdit.getProperty("search.fileset")));
		searchInPanel.setLayout(new BoxLayout(searchInPanel, BoxLayout.Y_AXIS));

		JPanel searchSettingsPanel = new JPanel();
		searchSettingsPanel.setBorder(new TitledBorder(jEdit.getProperty("search.settings")));
		searchSettingsPanel.setLayout(new BoxLayout(searchSettingsPanel, BoxLayout.Y_AXIS));
		
		JPanel searchDirectionPanel = new JPanel();
		searchDirectionPanel.setBorder(new TitledBorder(jEdit.getProperty("search.direction")));
		searchDirectionPanel.setLayout(new BoxLayout(searchDirectionPanel, BoxLayout.Y_AXIS));
		
		filesetGrp = new ButtonGroupHide();

		ButtonGroup direction = new ButtonGroup();
		
		
		
		searchCurrentBuffer = new JRadioButton(jEdit.getProperty("search.current"));
		filesetGrp.add(searchCurrentBuffer);
		searchInPanel.add(searchCurrentBuffer);

		
		searchSelection = new JRadioButton(jEdit.getProperty("search.selection"));
		searchSelection.setToolTipText(jEdit.getProperty("search.selection.tooltip"));
		searchSelection = new JRadioButton(jEdit.getProperty("search.selection"));
		searchSelection.setMnemonic(jEdit.getProperty("search.selection.mnemonic")
			.charAt(0));
		searchSelection.addActionListener(settingsActionHandler);
		searchSelection.addActionListener(selectivShowActionHandler);
		filesetGrp.add(searchSelection);
		searchInPanel.add(searchSelection);
		

		keepDialog = new JCheckBox(jEdit.getProperty("search.keep"));
		keepDialog.setMnemonic(jEdit.getProperty("search.keep.mnemonic").charAt(0));
		keepDialog.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				keepDialogChanged = true;
			}
		});
		searchSettingsPanel.add(keepDialog);
		
		// properties should be added in jedit_gui.props
		searchFromTop = new JRadioButton(jEdit.getProperty("search.fromTop"));
		searchFromTop.setMnemonic(jEdit.getProperty("search.fromTop.mnemonic").charAt(0));
		direction.add(searchFromTop);
		searchFromTop.addActionListener(settingsActionHandler);
		searchDirectionPanel.add(searchFromTop);
		
		 
		
		searchAllBuffers = new JRadioButton(jEdit.getProperty("search.all"));
		searchAllBuffers.setToolTipText(jEdit.getProperty("search.all.tooltip"));
		searchAllBuffers.setMnemonic(jEdit.getProperty("search.all.mnemonic")
			.charAt(0));
		searchAllBuffers.addActionListener(settingsActionHandler);
		searchAllBuffers.addActionListener(selectivShowActionHandler);

		filesetGrp.add(searchAllBuffers);
		searchInPanel.add(searchAllBuffers);
		

		regexp = new JCheckBox(jEdit.getProperty("search.ext.regexp"));
		// regexp.setSelected(jEdit.getProperty("search.regexp.toggle"));
		regexp.setMnemonic(jEdit.getProperty("search.regexp.mnemonic").charAt(0));
		searchSettingsPanel.add(regexp);
		regexp.addActionListener(settingsActionHandler);

		searchForward = new JRadioButton(jEdit.getProperty("search.forward"));
		searchForward.setMnemonic(jEdit.getProperty("search.forward.mnemonic").charAt(0));
		direction.add(searchForward);
		searchDirectionPanel.add(searchForward);
		searchForward.addActionListener(settingsActionHandler);

		searchProject = new JRadioButton(jEdit.getProperty("search.ext.project"));
		searchProject.setMnemonic(jEdit.getProperty("search.ext.project.mnemonic")
			.charAt(0));
		searchProject.setToolTipText(jEdit.getProperty("search.ext.project.tooltip"));
		searchProject.addActionListener(settingsActionHandler);
		searchProject.addActionListener(selectivShowActionHandler);
		filesetGrp.add(searchProject);
		searchInPanel.add(searchProject);


		ignoreCase = new JCheckBox(jEdit.getProperty("search.case"));
		ignoreCase.setMnemonic(jEdit.getProperty("search.case.mnemonic").charAt(0));
		searchSettingsPanel.add(ignoreCase);
		ignoreCase.addActionListener(settingsActionHandler);

		searchBack = new JRadioButton(jEdit.getProperty("search.back"));
		searchBack.setMnemonic(jEdit.getProperty("search.back.mnemonic").charAt(0));
		direction.add(searchBack);
		searchDirectionPanel.add(searchBack);
		searchBack.addActionListener(settingsActionHandler);

		searchDirectory = new JRadioButton(jEdit.getProperty("search.directory"));
		searchDirectory.addActionListener(settingsActionHandler);
		searchDirectory.addActionListener(selectivShowActionHandler);
		searchDirectory.setToolTipText(jEdit.getProperty("search.directory.tooltip"));
		searchDirectory.setMnemonic(jEdit.getProperty("search.directory.mnemonic")
			.charAt(0));
		filesetGrp.add(searchDirectory);
		searchInPanel.add(searchDirectory);
		
		

		hyperSearch = new JCheckBox(jEdit.getProperty("search.hypersearch"));
		hyperSearch.setMnemonic(jEdit.getProperty("search.hypersearch.mnemonic").charAt(0));
		searchSettingsPanel.add(hyperSearch);
		hyperSearch.addActionListener(settingsActionHandler);

		wrap = new JCheckBox(jEdit.getProperty("search.wrap"));
		// wrap.setSelected(jEdit.getProperty("search.wrap.toggle"));
		wrap.setMnemonic(jEdit.getProperty("search.wrap.mnemonic").charAt(0));
		searchSettingsPanel.add(wrap);
		wrap.addActionListener(settingsActionHandler);

		JPanel combinedPanel = new JPanel();
		combinedPanel.setLayout(new FlowLayout());
		combinedPanel.add(searchInPanel);
		combinedPanel.add(searchSettingsPanel);
		combinedPanel.add(searchDirectionPanel);
		
		return combinedPanel;
	} // }}}

	// {{{ createMultiFilePanel() method
	private JPanel createMultiFilePanel()
	{
		JPanel multifile = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		multifile.setLayout(layout);

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridy = cons.gridwidth = cons.gridheight = 1;
		cons.anchor = GridBagConstraints.WEST;
		cons.fill = GridBagConstraints.HORIZONTAL;

		MultiFileActionHandler actionListener = new MultiFileActionHandler();
		filter = new HistoryTextField("search.filter");
		filter.addActionListener(actionListener);

		cons.insets = new Insets(0, 0, 3, 0);

		JLabel label = new JLabel(jEdit.getProperty("search.filterField"),
			SwingConstants.RIGHT);
		label.setBorder(new EmptyBorder(0, 0, 0, 12));
		label.setDisplayedMnemonic(jEdit.getProperty("search.filterField.mnemonic").charAt(
			0));
		label.setLabelFor(filter);
		cons.weightx = 0.0f;
		layout.setConstraints(label, cons);
		multifile.add(label);

		cons.gridwidth = 2;
		cons.insets = new Insets(0, 0, 3, 6);
		cons.weightx = 1.0f;
		layout.setConstraints(filter, cons);
		multifile.add(filter);

		cons.gridwidth = 1;
		cons.weightx = 0.0f;
		cons.insets = new Insets(0, 0, 3, 0);

		// synchronize = new JCheckBox(jEdit.getProperty("search.ext.synchronize"));
		synchronize = new Button(jEdit.getProperty("search.synchronize"));
		synchronize.setMnemonic(jEdit.getProperty("search.synchronize.mnemonic").charAt(0));
		synchronize.setToolTipText(jEdit.getProperty("xsearch.synchronize.tooltip"));
		JPopupMenu autoSyncMenu = new JPopupMenu("Synchronize Button");

		autoSyncMenu.add(autoSync);
		boolean isAutoSync = SearchAndReplace.isAutoSync();
		autoSync.setState(isAutoSync);
		autoSync.addActionListener(actionListener);
 
		synchronize.addPopupMenu(autoSyncMenu);
		
		synchronize.setMnemonic(jEdit.getProperty("search.synchronize.mnemonic").charAt(0));
		synchronize.setEnabled(true);
		synchronize.addActionListener(actionListener);
		layout.setConstraints(synchronize, cons);
		multifile.add(synchronize);

		cons.gridy++;

		directory = new HistoryTextField("search.directory");
		directory.setColumns(25);
		directory.addActionListener(actionListener);

		label = new JLabel(jEdit.getProperty("search.directoryField"), SwingConstants.RIGHT);
		label.setBorder(new EmptyBorder(0, 0, 0, 12));

		label.setDisplayedMnemonic(jEdit.getProperty("search.directoryField.mnemonic")
			.charAt(0));
		label.setLabelFor(directory);
		cons.insets = new Insets(0, 0, 3, 0);
		cons.weightx = 0.0f;
		layout.setConstraints(label, cons);
		multifile.add(label);

		cons.insets = new Insets(0, 0, 3, 6);
		cons.weightx = 1.0f;
		cons.gridwidth = 2;
		layout.setConstraints(directory, cons);
		multifile.add(directory);

		choose = new JButton(jEdit.getProperty("search.choose"));
		choose.setMnemonic(jEdit.getProperty("search.choose.mnemonic").charAt(0));
		cons.insets = new Insets(0, 0, 3, 0);
		cons.weightx = 0.0f;
		cons.gridwidth = 1;
		layout.setConstraints(choose, cons);
		multifile.add(choose);
		choose.addActionListener(actionListener);

		
		JPanel dirCheckBoxPanel = new JPanel();
		dirCheckBoxPanel.setLayout(new FlowLayout());
		
		searchSubDirectories = new JCheckBox(jEdit.getProperty("search.subdirs"));
		searchSubDirectories.setSelected(jEdit.getBooleanProperty("search.subdirs.toggle"));
		
		searchSubDirectories.setMnemonic(jEdit.getProperty("search.subdirs.mnemonic")
			.charAt(0));

		skipHidden = new JCheckBox(jEdit.getProperty("search.skipHidden"));
		skipHidden.setSelected(jEdit.getBooleanProperty("search.skipHidden.toggle", true));
		skipBinaryFiles = new JCheckBox(jEdit.getProperty("search.skipBinary"));
		skipBinaryFiles.setSelected(jEdit.getBooleanProperty("search.skipBinary.toggle", true));
		dirCheckBoxPanel.add(searchSubDirectories);
		dirCheckBoxPanel.add(skipHidden);
		dirCheckBoxPanel.add(skipBinaryFiles);

		cons.insets = new Insets(0, 0, 0, 0);
		cons.gridy++;
		cons.gridwidth = 3;
		layout.setConstraints(dirCheckBoxPanel, cons);
		multifile.add(dirCheckBoxPanel);
		
//		multifile.add(searchSubDirectories);
		
		return multifile;
	} // }}}

	// {{{ createButtonsPanel() method
	private Box createButtonsPanel()
	{
		Box box = new Box(BoxLayout.Y_AXIS);

		ButtonActionHandler buttonActionHandler = new ButtonActionHandler();

		box.add(Box.createVerticalStrut(12));

		grid = new JPanel(new GridLayout(0, 1, 0, 12));

		findBtn = new JButton(jEdit.getProperty("search.findBtn"));
		findBtn.setDefaultCapable(true);
		findBtn.setMnemonic(jEdit.getProperty("search.findBtn.mnemonic").charAt(0));
		// getRootPane().setDefaultButton(findBtn);
		grid.add(findBtn);
		findBtn.addActionListener(buttonActionHandler);

		findAllBtn = new JButton(jEdit.getProperty("search.findAllBtn"));
		// findAllBtn.setMnemonic(jEdit.getProperty("search.findAllBtn.mnemonic").charAt(0));
		if (jEdit.getBooleanProperty("xsearch.findAllButton", true))
			grid.add(findAllBtn);
		findAllBtn.addActionListener(buttonActionHandler);

		/*
		 * replaceBtn = new
		 * JButton(jEdit.getProperty("search.replaceBtn"));
		 * replaceBtn.setMnemonic(jEdit.getProperty("search.replaceBtn.mnemonic")
		 * .charAt(0)); grid.add(replaceBtn);
		 * replaceBtn.addActionListener(buttonActionHandler);
		 */

		replaceAndFindBtn = new JButton(jEdit.getProperty("search.replaceAndFindBtn"));
		replaceAndFindBtn.setMnemonic(jEdit
			.getProperty("search.replaceAndFindBtn.mnemonic").charAt(0));
		grid.add(replaceAndFindBtn);
		replaceAndFindBtn.addActionListener(buttonActionHandler);

		replaceAllBtn = new JButton(jEdit.getProperty("search.replaceAllBtn"));
		replaceAllBtn.setMnemonic(jEdit.getProperty("search.replaceAllBtn.mnemonic")
			.charAt(0));
		grid.add(replaceAllBtn);
		replaceAllBtn.addActionListener(buttonActionHandler);

		resetSettingsButton = new JButton(jEdit.getProperty("search.ext.reset"));
		resetSettingsButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				SearchSettings.resetSettings();
				load();
				showHideOptions(searchCurrentBuffer);
				setupCurrentSelectedOptionsLabel();
			}
		});
		if (jEdit.getBooleanProperty("xsearch.resetButton", true))
		{
			grid.add(resetSettingsButton);
			resetSettingsButtonPresent = true;
		}
		else
			resetSettingsButtonPresent = false;

		closeBtn = new JButton(jEdit.getProperty("common.close"));
		grid.add(closeBtn);
		closeBtn.addActionListener(buttonActionHandler);

		grid.setMaximumSize(grid.getPreferredSize());

		box.add(grid);
		box.add(Box.createGlue());

		return box;
	} // }}}

	// {{{ updateEnabled() method
	private void updateEnabled()
	{
		wrap.setEnabled(!hyperSearch.isSelected() && !searchSelection.isSelected());

		boolean reverseEnabled = !hyperSearch.isSelected() && !rowRadioBtn.isSelected()
			&& searchCurrentBuffer.isSelected();
		boolean regexpSelected = regexp.isSelected();
		// XSearch 1.0: backward regexp allowed
		searchBack.setEnabled(reverseEnabled);
		// && !regexpSelected && wordPartDefaultRadioBtn.isSelected());
		// word part search (and tentativ) not allowed in combination
		// with regexp
		wordPartPrefixRadioBtn.setEnabled(!regexpSelected);
		wordPartSuffixRadioBtn.setEnabled(!regexpSelected);
		wordPartWholeRadioBtn.setEnabled(!regexpSelected);
		tentativRadioBtn.setEnabled(!regexpSelected);
		if (regexpSelected)
		{
			wordPartDefaultRadioBtn.setSelected(true);
			tentativRadioBtn.setSelected(false);
		}

		searchForward.setEnabled(reverseEnabled);
		searchFromTop.setEnabled(reverseEnabled);
		if (!reverseEnabled || (!searchBack.isEnabled() && searchBack.isSelected()))
		{
			// searchFromTop.setSelected(true); changed 1.06
			searchForward.setSelected(true);
			SearchAndReplace.setSearchFromTop(false);
		}
		filter.setEnabled(searchAllBuffers.isSelected() || searchDirectory.isSelected());

		directory.setEnabled(searchDirectory.isSelected());
		choose.setEnabled(searchDirectory.isSelected());
		searchSubDirectories.setEnabled(searchDirectory.isSelected());
		skipBinaryFiles.setEnabled(searchDirectory.isSelected());
		skipHidden.setEnabled(searchDirectory.isSelected());
		// synchronize.setEnabled(searchAllBuffers.isSelected()
		// || searchDirectory.isSelected());

		findBtn.setEnabled(!searchSelection.isSelected() || hyperSearch.isSelected());
		replaceAndFindBtn.setEnabled(!hyperSearch.isSelected()
			&& !searchSelection.isSelected());
		if (hyperSearch.isSelected())
		{
			// disable fold search
			searchFoldDefaultRadioBtn.setSelected(true);
			searchFoldInsideRadioBtn.setEnabled(false);
			searchFoldOutsideRadioBtn.setEnabled(false);
			findAllBtn.setEnabled(false);
			// enable hyper range
			hyperRangeLabel.setEnabled(true);
			hyperRangeLabelUp.setEnabled(true);
			hyperRangeLabelDown.setEnabled(true);
			hyperRangeUpTextField.setEnabled(true);
			hyperRangeDownTextField.setEnabled(true);
		}
		else
		{
			searchFoldInsideRadioBtn.setEnabled(true);
			searchFoldOutsideRadioBtn.setEnabled(true);
			findAllBtn.setEnabled(true);
			// enable hyper range
			hyperRangeLabel.setEnabled(false);
			hyperRangeLabelUp.setEnabled(false);
			hyperRangeLabelDown.setEnabled(false);
			hyperRangeUpTextField.setEnabled(false);
			hyperRangeDownTextField.setEnabled(false);
		}

	} // }}}

	// {{{ loadSettingsFromHistory() method
	private void loadSettingsFromHistory()
	{
		if (searchSettingsHistoryRadioBtn.isSelected() && find.getText().length() > 0)
		{
			SearchSettings searchHist = settingsHistory.getItem(find.getText());
			if (searchHist != null)
			{
				searchHist.update();
				load();
				showHideOptions(searchCurrentBuffer);
				if (!searchCurrentBuffer.isSelected())
					hyperSearch.setSelected(true);
				enableRowColumnSearch(!searchSelection.isSelected());
				updateEnabled();
				revalidatePanels();
			}
		}
	} // }}}

	// {{{ escapeRegexp() method
	/**
	 * Escapes characters with special meaning in a regexp.
	 * 
	 * @param multiline
	 *                Should \n be escaped?
	 * @since jEdit 4.3pre1
	 */
	public static String escapeRegexp(String str, boolean multiline)
	{
		return MiscUtilities.charsToEscapes(str, "\r\t\\()[]{}$^*+?|."
			+ (multiline ? "" : "\n"));
	} // }}}

	// {{{ save() method
	/**
	 * @param cancel
	 *                If true, we don't bother the user with warning
	 *                messages
	 */
	private boolean save(boolean cancel)
	{
		try
		{
			// prevents us from handling SearchSettingsChanged
			// as a result of below
			saving = true;
			SearchAndReplace.setIgnoreCase(ignoreCase.isSelected());
			SearchAndReplace.setRegexp(regexp.isSelected());
			SearchAndReplace.setReverseSearch(searchBack.isSelected());
			SearchAndReplace.setAutoWrapAround(wrap.isSelected());

			String filter = this.filter.getText();
			this.filter.addCurrentToHistory();
			if (filter.length() == 0)
				filter = "*";

			SearchFileSet fileset = SearchAndReplace.getSearchFileSet();
			boolean recurse = searchSubDirectories.isSelected();
			jEdit.setBooleanProperty("search.subdirs.toggle", searchSubDirectories.isSelected());
			jEdit.setBooleanProperty("search.skipHidden.toggle", skipHidden.isSelected());
			jEdit.setBooleanProperty("search.skipBinary.toggle", skipBinaryFiles.isSelected());
			if (searchSelection.isSelected())
				fileset = new CurrentBufferSet();
			else if (searchCurrentBuffer.isSelected())
			{
				fileset = new CurrentBufferSet();

				jEdit.setBooleanProperty("search.hypersearch.toggle", hyperSearch
					.isSelected());
			}
			else if (searchAllBuffers.isSelected())
				fileset = new AllBufferSet(filter);
			else if(searchProject.isSelected())
				fileset = new ProjectViewerListSet(view);
			else if (searchDirectory.isSelected())
			{
				String directory = this.directory.getText();
				this.directory.addCurrentToHistory();
				directory = MiscUtilities.constructPath(view.getBuffer()
					.getDirectory(), directory);

				if ((VFSManager.getVFSForPath(directory).getCapabilities() & VFS.LOW_LATENCY_CAP) == 0)
				{
					if (cancel)
						return false;

					int retVal = GUIUtilities.confirm(XSearchPanel.this,
						"remote-dir-search", null,
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
					if (retVal != JOptionPane.YES_OPTION)
						return false;
				}

				if (fileset instanceof DirectoryListSet)
				{
					DirectoryListSet dset = (DirectoryListSet) fileset;
					dset.setDirectory(directory);
					dset.setFileFilter(filter);
					dset.setRecursive(recurse);
					EditBus.send(new SearchSettingsChanged(null));
				}
				else
					fileset = new DirectoryListSet(directory, filter, recurse);
			}
			else
			{
				// can't happen
				fileset = null;
			}

			jEdit.setBooleanProperty("search.subdirs.toggle", recurse);
			jEdit.setBooleanProperty("search.keepDialog.toggle", keepDialog
				.isSelected());
			jEdit.setBooleanProperty("xsearch.synchronize.toggle", synchronize
				.isSelected());
			// moved to actionListener
			// jEdit.setBooleanProperty("search.settingsHistory.toggle",
			// searchSettingsHistoryRadioBtn.isSelected());

			SearchAndReplace.setSearchFileSet(fileset);

			replace.addCurrentToHistory();
			SearchAndReplace.setReplaceString(replace.getText());

			if (find.getText().length() == 0)
			{
				if (!cancel)
					getToolkit().beep();
				return false;
			}
			find.addCurrentToHistory();
			SearchSettings currSs = new SearchSettings();
			currSs.load();
			settingsHistory.addItem(find.getText(), currSs);
			if (SearchAndReplace.getSearchString() != null)
			{
				if (!SearchAndReplace.getSearchString().equals(find.getText()))
				{
					// search string has changed ==> reset
					// refind
					SearchAndReplace.resetIgnoreFromTop();
				}
			}
			// because of word part search, we have to set search
			// string even if equal
			SearchAndReplace.setSearchString(find.getText());
			// Log.log(Log.DEBUG, XSearchPanel.class,"+++
			// XSearchPanel.1287: call SearchAndReplace.save()");
			SearchAndReplace.save();
			return true;
		}
		finally
		{
			saving = false;
		}
	} // }}}

	// {{{ synchronizeMultiFileSettings() method
	private void synchronizeMultiFileSettings()
	{
		directory.setText(view.getBuffer().getDirectory());
		Log.log(Log.DEBUG, BeanShell.class, "XSearchPanel.1276: directory.getText = "
			+ directory.getText());

		SearchFileSet fileset = SearchAndReplace.getSearchFileSet();

		if (fileset instanceof AllBufferSet)
		{
			filter.setText(((AllBufferSet) fileset).getFileFilter());
		}
		else
		{
			filter.setText("*"
				+ MiscUtilities.getFileExtension(view.getBuffer().getName()));
			// fileset = new
			// DirectoryListSet(directory.getText(),filter.getText(),searchSubDirectories.isEnabled());
			// 1.06
			fileset = new DirectoryListSet(directory.getText(), filter.getText(),
				searchSubDirectories.isSelected());
			SearchAndReplace.setSearchFileSet(fileset);
		}
				
	} // }}}

	// {{{ evalExtendedOptions() method
	/**
	 * checks extended options and assigns its values to SearchAndReplace
	 */
	private boolean evalExtendedOptions()
	{
		// boolean ok = true;
		boolean ok = evalIntegerOptions();
		if (ok)
		{
			/*******************************************************
			 * column handling
			 ******************************************************/
			if (!columnRadioBtn.isSelected())
			{
				// ok = evalColumnOptions();
				// } else {
				SearchAndReplace.resetColumnSearch();
			}
			/*******************************************************
			 * row handling
			 ******************************************************/
			if (!rowRadioBtn.isSelected())
				SearchAndReplace.resetRowSearch();
		}
		if (ok)
		{
			/*******************************************************
			 * comment handling
			 ******************************************************/
			if (searchCommentDefaultRadioBtn.isSelected())
				SearchAndReplace.setCommentOption(XSearch.SEARCH_IN_OUT_NONE);
			else if (searchCommentInsideRadioBtn.isSelected())
				SearchAndReplace.setCommentOption(XSearch.SEARCH_IN_OUT_INSIDE);
			else
				SearchAndReplace.setCommentOption(XSearch.SEARCH_IN_OUT_OUTSIDE);
			/*******************************************************
			 * folding handling
			 ******************************************************/
			if (searchFoldDefaultRadioBtn.isSelected())
				SearchAndReplace.setFoldOption(XSearch.SEARCH_IN_OUT_NONE);
			else if (searchFoldOutsideRadioBtn.isSelected())
				SearchAndReplace.setFoldOption(XSearch.SEARCH_IN_OUT_OUTSIDE);
			else
				SearchAndReplace.setFoldOption(XSearch.SEARCH_IN_OUT_INSIDE);
			/*******************************************************
			 * word part handling
			 ******************************************************/
			if (wordPartDefaultRadioBtn.isSelected())
				SearchAndReplace.setWordPartOption(XSearch.SEARCH_PART_NONE);
			else if (wordPartWholeRadioBtn.isSelected())
				SearchAndReplace.setWordPartOption(XSearch.SEARCH_PART_WHOLE_WORD);
			else if (wordPartPrefixRadioBtn.isSelected())
				SearchAndReplace.setWordPartOption(XSearch.SEARCH_PART_PREFIX);
			else
				SearchAndReplace.setWordPartOption(XSearch.SEARCH_PART_SUFFIX);
			/*******************************************************
			 * hyper range handling
			 ******************************************************/
			SearchAndReplace.setTentativOption(tentativRadioBtn.isSelected());
			/*******************************************************
			 * tentativ handling
			 ******************************************************/
			SearchAndReplace.setTentativOption(tentativRadioBtn.isSelected());
		}
		return ok;
	} // }}}
	
	
	// check if project viewer got active / inactive
	void enableSearchProject()
	{
		searchProject.setEnabled(
			new ProjectViewerListSet(view).isProjectViewerPresent());
	}

	// {{{ enableRowColumnSearch() methods
	private void enableRowColumnSearch(boolean setEnabled)
	{
		columnRadioBtn.setEnabled(setEnabled);
		rowRadioBtn.setEnabled(setEnabled);
		if (!setEnabled)
		{
			columnRadioBtn.setSelected(setEnabled);
			rowRadioBtn.setSelected(setEnabled);
		}
		enableColumnOptions(setEnabled);
		enableRowOptions(setEnabled);
	}

	// enableColumnOptions() method
	private void enableColumnOptions(boolean setEnabled)
	{
		columnExpandTabsRadioBtn.setEnabled(setEnabled);
		columnRightColumnField.setEnabled(setEnabled);
		rightColumnLabel.setEnabled(setEnabled);
		columnLeftColumnField.setEnabled(setEnabled);
		leftColumnLabel.setEnabled(setEnabled);
	}

	// enableRowOptions() method
	private void enableRowOptions(boolean setEnabled)
	{
		rowRightRowField.setEnabled(setEnabled);
		rightRowLabel.setEnabled(setEnabled);
		rowLeftRowField.setEnabled(setEnabled);
		leftRowLabel.setEnabled(setEnabled);
	}// }}}

	// {{{ evalIntegerOptions() method
	/*
	 * evaluates: - column options - row options - hyper range options
	 */
	private boolean evalIntegerOptions()
	{
		boolean extendTabs = columnExpandTabsRadioBtn.isSelected();
		int startCol;
		int endCol;
		int startRow;
		int endRow;
		JTextField errorField = null;
		JLabel errorLabel = null;
		try
		{
			/*******************************************************
			 * eval column options
			 ******************************************************/
			if (columnRadioBtn.isSelected())
			{
				// eval startCol
				errorField = columnLeftColumnField;
				errorLabel = leftColumnLabel;
				if (columnLeftColumnField.getText().length() == 0)
				{
					throw new NumberFormatException();
				}
				else
				{
					startCol = Integer.parseInt(errorField.getText());
					if (startCol < 1)
						throw new NumberFormatException();
				}
				// eval endCol
				if (columnRightColumnField.getText().length() == 0)
				{
					if (find.getText().length() > 0)
						endCol = startCol + find.getText().length() - 1;
					else
						endCol = startCol;
					columnRightColumnField.setText(Integer.toString(endCol));
				}
				else
				{
					errorField = columnRightColumnField;
					errorLabel = rightColumnLabel;
					endCol = Integer.parseInt(errorField.getText());
					if (!regexp.isSelected()
						&& endCol < startCol + find.getText().length() - 1)
						throw new NumberFormatException();
					if (endCol < startCol)
						throw new NumberFormatException();
				}
				SearchAndReplace.setColumnSearchOptions(extendTabs, startCol,
					endCol);
			}
			/*******************************************************
			 * eval row options
			 ******************************************************/
			if (rowRadioBtn.isSelected())
			{
				// eval startRow
				errorField = rowLeftRowField;
				errorLabel = leftRowLabel;
				if (rowLeftRowField.getText().length() == 0)
				{
					throw new NumberFormatException();
				}
				else
				{
					startRow = Integer.parseInt(errorField.getText());
					if (startRow < 1)
						throw new NumberFormatException();
					if (startRow > view.getBuffer().getLineCount())
						throw new NumberFormatException();
				}
				// eval endRow
				if (rowRightRowField.getText().length() == 0)
				{
					endRow = view.getBuffer().getLineCount();
					rowRightRowField.setText(Integer.toString(endRow));
				}
				else
				{
					errorField = rowRightRowField;
					errorLabel = rightRowLabel;
					endRow = Integer.parseInt(errorField.getText());
					// dont check endrow overflow ==> search
					// till end of buffer
					// if (endRow >
					// view.getBuffer().getLineCount())
					// throw new NumberFormatException();
					// if (!regexp.isSelected() && endRow <
					// startRow + find.getText().length()-1)
					// throw new NumberFormatException();
					if (endRow < startRow)
						throw new NumberFormatException();
				}
				SearchAndReplace.setRowSearchOptions(startRow - 1, endRow - 1);
			}
			/*******************************************************
			 * eval hyper range options
			 ******************************************************/
			if (hyperSearch.isSelected())
			{
				errorField = hyperRangeUpTextField;
				errorLabel = hyperRangeLabelUp;
				int hyUp, hyDown;
				if (errorField.getText().length() == 0)
				{
					hyUp = -1;
				}
				else
				{
					hyUp = Integer.parseInt(errorField.getText());
					if (hyUp < 0)
						throw new NumberFormatException();
				}
				errorField = hyperRangeDownTextField;
				errorLabel = hyperRangeLabelDown;
				if (errorField.getText().length() == 0)
				{
					hyDown = -1;
				}
				else
				{
					hyDown = Integer.parseInt(errorField.getText());
					if (hyDown < 0)
						throw new NumberFormatException();
				}
				SearchAndReplace.setHyperRange(hyUp, hyDown);
			}
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null, "Wrong input :" + errorField.getText(),
				"Field: " + errorLabel.getText(), JOptionPane.ERROR_MESSAGE);
			errorField.requestFocus();
			return false;
		}
		// if (SearchAndReplace.debug) Log.log(Log.DEBUG,
		// BeanShell.class,"extendTabs = "+
		// extendTabs+", startCol = "+startCol+", endCol = "+endCol);
		return true;
	} // }}}

	// {{{ closeOrKeepDialog() method
	private void closeOrKeepDialog()
	{
		if (keepDialog.isSelected())
		{
			// Windows bug workaround in case a YES/NO confirm
			// was shown

			// ... but if HyperSearch results window is floating,
			// the workaround causes problems!
			if (!hyperSearch.isSelected())
			{
				requestFocus();
				find.requestFocus();
			}
			return;
		}

	} // }}}

	// {{{ load() method
	private void load()
	{
		// boolean resetRegex = true;
		// ico wordpart, regexp was implicit set: reset it
		switch (SearchAndReplace.getWordPartOption())
		{
		case XSearch.SEARCH_PART_PREFIX: wordPartGrp.setSelected(wordPartPrefixRadioBtn, true);
			break;
		case XSearch.SEARCH_PART_SUFFIX: wordPartGrp.setSelected(wordPartSuffixRadioBtn, true);
			break;
		case XSearch.SEARCH_PART_WHOLE_WORD: wordPartGrp.setSelected(wordPartWholeRadioBtn, true);
			break;
		// default: resetRegex = false;
		default:
			wordPartDefaultRadioBtn.setSelected(true);
		}
		// if (resetRegex) SearchAndReplace.setRegexp(false);
		tentativRadioBtn.setSelected(SearchAndReplace.getTentativOption());

		switch (SearchAndReplace.getCommentOption())
		{
			case XSearch.SEARCH_IN_OUT_INSIDE: commentGrp.setSelected(searchCommentInsideRadioBtn, true);
				break;
			case XSearch.SEARCH_IN_OUT_OUTSIDE: commentGrp.setSelected(searchCommentOutsideRadioBtn, true);
				break;
		default:
			searchCommentDefaultRadioBtn.setSelected(true);

		}
		switch (SearchAndReplace.getFoldOption())
		{
		case XSearch.SEARCH_IN_OUT_INSIDE: foldGrp.setSelected(searchFoldInsideRadioBtn, true);
			break;
		case XSearch.SEARCH_IN_OUT_OUTSIDE: foldGrp.setSelected(searchFoldOutsideRadioBtn, true);
			break;
		default:
			searchFoldDefaultRadioBtn.setSelected(true);
		}
		if (SearchAndReplace.getColumnOption())
		{
			columnRadioBtn.setSelected(true);
			columnExpandTabsRadioBtn.setSelected(SearchAndReplace
				.getColumnExpandTabsOption());
			columnLeftColumnField.setText(Integer.toString(SearchAndReplace
				.getColumnLeftCol()));
			columnRightColumnField.setText(Integer.toString(SearchAndReplace
				.getColumnRightCol()));
			enableColumnOptions(true);
		}
		else
		{
			columnRadioBtn.setSelected(false);
			enableColumnOptions(false);
		}
		if (SearchAndReplace.getRowOption())
		{
			rowRadioBtn.setSelected(true);
			rowLeftRowField.setText(Integer.toString(SearchAndReplace.getRowLeftRow()));
			rowRightRowField.setText(Integer
				.toString(SearchAndReplace.getRowRightRow()));
			enableRowOptions(true);
		}
		else
		{
			rowRadioBtn.setSelected(false);
			enableRowOptions(false);
		}
		// setup hyper range
		if (SearchAndReplace.getHyperRangeUpper() == -1)
			hyperRangeUpTextField.setText("");
		else
			hyperRangeUpTextField.setText(Integer.toString(SearchAndReplace
				.getHyperRangeUpper()));
		if (SearchAndReplace.getHyperRangeLower() == -1)
			hyperRangeDownTextField.setText("");
		else
			hyperRangeDownTextField.setText(Integer.toString(SearchAndReplace
				.getHyperRangeLower()));

		ignoreCase.setSelected(SearchAndReplace.getIgnoreCase());
		regexp.setSelected(SearchAndReplace.getRegexp());
		wrap.setSelected(SearchAndReplace.getAutoWrapAround());

		if (SearchAndReplace.getReverseSearch())
			searchBack.setSelected(true);
		else if (SearchAndReplace.getSearchFromTop())
			searchFromTop.setSelected(true);
		else
			searchForward.setSelected(true);

		// disable column/row options ico searchSelection
		if (searchSelection.isSelected())
			enableRowColumnSearch(false);
		if (SearchAndReplace.getBeanShellReplace())
		{
			replace.setModel("replace.script");
			beanShellReplace.setSelected(true);
		}
		else
		{
			replace.setModel("replace");
			stringReplace.setSelected(true);
		}

		// setup search fileset
		SearchFileSet fileset = SearchAndReplace.getSearchFileSet();
		HistoryModel model = filter.getModel();
		if (model.getSize() != 0)
			filter.setText(model.getItem(0));
		else
		{
			filter.setText("*"
				+ MiscUtilities.getFileExtension(view.getBuffer().getName()));
		}
		model = directory.getModel();
		boolean isAutoSync = SearchAndReplace.isAutoSync(); 
		autoSync.setState(isAutoSync);
		synchronize.setEnabled(!isAutoSync);
		directory.setEditable(!isAutoSync);
		filter.setEditable(!isAutoSync);
		
		if (isAutoSync) {
			synchronizeMultiFileSettings();
		}
		if (model.getSize() != 0)
			directory.setText(model.getItem(0));
		else
			directory.setText(view.getBuffer().getDirectory());
		// Log.log(Log.DEBUG, BeanShell.class,"XSearchPanel.1621:
		// directory.getText = "+directory.getText());

		searchSubDirectories.setSelected(jEdit.getBooleanProperty("search.subdirs.toggle"));

		if (fileset instanceof ProjectViewerListSet) {
			searchProject.setSelected(true);
			synchronizeMultiFileSettings();
		}
		else if (fileset instanceof DirectoryListSet)
		{
			filter.setText(((DirectoryListSet) fileset).getFileFilter());
			directory.setText(((DirectoryListSet) fileset).getDirectory());
			searchSubDirectories.setSelected(((DirectoryListSet) fileset).isRecursive());
			directory.addCurrentToHistory();
		}
		else if (fileset instanceof AllBufferSet)
		{
			filter.setText(((AllBufferSet) fileset).getFileFilter());
		}

		keepDialog.setSelected(jEdit.getBooleanProperty("search.keepDialog.toggle"));
		synchronize.setSelected(jEdit.getBooleanProperty("xsearch.synchronize.toggle"));
		// Log.log(Log.DEBUG, BeanShell.class,"XSearchPanel.1648:
		// synchronize.isSelected = "+synchronize.isSelected());
		searchSettingsHistoryRadioBtn.setSelected(jEdit
			.getBooleanProperty("search.settingsHistory.toggle"));



	} // }}}

	// {{{ showHideOptions() method
	private void showHideOptions(Object source)
	{
		if (source == showSettings)
		{
			jEdit.setBooleanProperty("search.show-settings.toggle", showSettings
				.isSelected());
			currentSelectedSettingsOptions.setLength(0); // init
									// text
			if (showSettings.isSelected())
			{
				// display "settings" and path
				centerPanel.add(BorderLayout.CENTER, searchSettingsPanel);
				if (searchDirectory.isSelected() || searchAllBuffers.isSelected())
					southPanel.add(BorderLayout.SOUTH, multiFilePanel);
			}
			else
			{
				// remove "settings" and path
				centerPanel.remove(searchSettingsPanel);
				southPanel.remove(multiFilePanel);
			}
		}
		else if (source == showReplace)
		{
			jEdit.setBooleanProperty("search.show-replace.toggle", showReplace
				.isSelected());
			if (showReplace.isSelected())
			{
				globalFieldPanel.add(fieldPanelReplaceLabel);
				globalFieldPanel.add(replaceModeBox);
				globalFieldPanel.add(fieldPanelVerticalStrut);
				globalFieldPanel.add(replace);
				if (resetSettingsButtonPresent)
					grid.remove(resetSettingsButton); // remove
										// first
										// to
										// keep
										// sorting
				grid.remove(closeBtn); // remove first to keep
							// sorting
				grid.add(replaceAndFindBtn);
				grid.add(replaceAllBtn);
				if (jEdit.getBooleanProperty("xsearch.resetButton", true))
				{
					grid.add(resetSettingsButton);
					resetSettingsButtonPresent = true;
				}
				else
					resetSettingsButtonPresent = false;
				grid.add(closeBtn);

			}
			else
			{
				globalFieldPanel.remove(fieldPanelReplaceLabel);
				globalFieldPanel.remove(replaceModeBox);
				globalFieldPanel.remove(fieldPanelVerticalStrut);
				globalFieldPanel.remove(replace);
				grid.remove(replaceAndFindBtn);
				grid.remove(replaceAllBtn);
			}
		}
		else if (source == showExtendedOptions)
		{
			jEdit.setBooleanProperty("search.show-extended.toggle", showExtendedOptions
				.isSelected());
			if (showExtendedOptions.isSelected())
			{
				southPanel.add(BorderLayout.CENTER, extendedOptionsPanel);
			}
			else
			{
				southPanel.remove(extendedOptionsPanel);
			}
		}
		else if (source == searchCurrentBuffer || source == searchAllBuffers || source == searchProject 
			|| source == searchSelection || source == searchDirectory)
			if (searchDirectory.isSelected() || searchAllBuffers.isSelected())
			{
				southPanel.add(BorderLayout.SOUTH, multiFilePanel);
			}
			else
			{
				southPanel.remove(multiFilePanel);
			}
	} // }}}

	// {{{ revalidatePanels() method
	private void revalidatePanels()
	{
		centerPanel.revalidate();
		globalFieldPanel.revalidate();
		southPanel.revalidate();
		revalidate();
		setVisible(true);
		// content.revalidate();
		// show();
	} // }}}

	// {{{ setupStartEndRowFromSelection() method
	private void setupStartEndRowFromSelection()
	{
		int[] selLines = view.getTextArea().getSelectedLines();
		if (selLines.length > 0)
		{
			rowLeftRowField.setText(Integer.toString(selLines[0] + 1));
			rowRightRowField.setText(Integer
				.toString(selLines[selLines.length - 1] + 1));
		}
	} // }}}

	// {{{ setupCurrentSelectedOptionsLabel() method
	// setup currentSelectedOptionsLabel to display options in one line if
	// panel part is hidden
	private void setupCurrentSelectedOptionsLabel()
	{
		StringBuffer currentSelectedOptions = new StringBuffer();
		if (!showSettings.isSelected() || !showExtendedOptions.isSelected())
		{
			if (!showSettings.isSelected())
			{
				if (ignoreCase.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.ignoreCase")
						+ " ");
				if (regexp.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.regexp")
						+ " ");
				if (hyperSearch.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.hyper")
						+ " ");
				if (searchFromTop.isSelected() && searchFromTop.isEnabled())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.fromTop")
						+ " ");
				if (searchBack.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.backward")
						+ " ");
				if (wrap.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.wrap")
						+ " ");
			}
			if (!showExtendedOptions.isSelected())
			{
				if (wordPartWholeRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.word")
						+ " ");
				if (wordPartPrefixRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.prefix")
						+ " ");
				if (wordPartSuffixRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.suffix")
						+ " ");
				if (columnRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.column")
						+ " ");
				if (rowRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.row")
						+ " ");
				if (searchFoldInsideRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.insideFold")
						+ " ");
				if (searchFoldOutsideRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.outsideFold")
						+ " ");
				if (searchCommentInsideRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.insideComment")
						+ " ");
				if (searchCommentOutsideRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.outsideComment")
						+ " ");
				if (tentativRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit
						.getProperty("search.currOpt.tentativ")
						+ " ");
			}
		}

		// debug: display components
		/*
		 * Component[] globComps = globalFieldPanel.getComponents(); if
		 * (SearchAndReplace.debug) for(int i = 0; i < globComps.length;
		 * i++) { Log.log(Log.DEBUG, BeanShell.class,"tp1212:
		 * globComps["+i+"] = "+globComps[i]); }
		 */
		if (currentSelectedOptions.length() > 0)
		{
			currentSelectedOptionsLabel.setText(jEdit
				.getProperty("search.currOpt.label")
				+ " " + currentSelectedOptions.toString());
			// check if there are still the "replace" components
			if (globalFieldPanel.getComponentCount() > optionsLabelIndex)
			{
				if (globalFieldPanel.getComponent(optionsLabelIndex) != currentSelectedOptionsLabel)
				{
					if (SearchAndReplace.debug)
						Log.log(Log.DEBUG, BeanShell.class,
							"tp1217: add Label at  "
								+ optionsLabelIndex);
					globalFieldPanel.add(currentSelectedOptionsLabel,
						optionsLabelIndex);
				}
				else if (SearchAndReplace.debug)
					Log.log(Log.DEBUG, BeanShell.class,
						"tp1219: Label already exists at "
							+ optionsLabelIndex);
			}
			else
			{
				globalFieldPanel.add(currentSelectedOptionsLabel);
			}
		}
		else if (globalFieldPanel.getComponentCount() > optionsLabelIndex)
		{
			if (globalFieldPanel.getComponent(optionsLabelIndex) == currentSelectedOptionsLabel)
			{
				if (SearchAndReplace.debug)
					Log.log(Log.DEBUG, BeanShell.class,
						"tp1224: remove Label at  " + optionsLabelIndex);
				globalFieldPanel.remove(optionsLabelIndex);
			}
			else if (SearchAndReplace.debug)
				Log.log(Log.DEBUG, BeanShell.class,
					"tp1227: Label does not exist at " + optionsLabelIndex);
		}
	} // }}}

	// }}}

	// {{{ Inner classes

	// {{{ MyJRadioButton class

	// used for the stringReplace and beanShell replace radio buttons,
	// so that the user can press tab to go from the find field to the
	// replace field in one go
	class MyJRadioButton extends JRadioButton
	{
		MyJRadioButton(String label)
		{
			super(label);
		}

		public boolean isFocusTraversable()
		{
			return false;
		}
	} // }}}

	class ReSupportButton extends JButton
	{
		ReSupportButton(String label)
		{
			super(label);
		}

		public boolean isFocusTraversable()
		{
			// return !showReplace.isSelected();
			return false;
		}
	} // }}}

	// {{{ ReplaceActionHandler class
	class ReplaceActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			replace.setModel(beanShellReplace.isSelected() ? "replace.script"
				: "replace");
			SearchAndReplace.setBeanShellReplace(beanShellReplace.isSelected());
		}
	} // }}}

	// {{{ SelectivShowActionHandler class
	class SelectivShowActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			showHideOptions(evt.getSource());
			setupCurrentSelectedOptionsLabel();
			revalidatePanels();
		}
	} // }}}

	// {{{ ExtendedOptionsActionHandler class
	class ExtendedOptionsActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			SearchAndReplace.resetIgnoreFromTop();
			Object source = evt.getSource();
			if (source == columnRadioBtn)
			{
				enableColumnOptions(columnRadioBtn.isSelected());
			}
			else if (source == rowRadioBtn)
			{
				enableRowOptions(rowRadioBtn.isSelected());
				updateEnabled();
			}
			else if (source == wordPartPrefixRadioBtn
				|| source == wordPartSuffixRadioBtn
				|| source == wordPartWholeRadioBtn || source == tentativRadioBtn)
			{
				if (((JRadioButton) source).isSelected())
				{
					regexp.setSelected(false);
					// searchBack.setEnabled(false);
					regexp.setEnabled(true);
				}
				updateEnabled();
			}
			else if (source == searchSettingsHistoryRadioBtn)
			{
				// i don't remeber why, but this statement is
				// useless
				// loadSettingsFromHistory();
				jEdit.setBooleanProperty("search.settingsHistory.toggle",
					searchSettingsHistoryRadioBtn.isSelected());

			}
			/*
			 * else if (source == searchFoldInsideRadioBtn || source ==
			 * searchFoldOutsideRadioBtn) { // don't allow
			 * hypersearch ico fold searching if
			 * (((JRadioButton)source).isSelected()) {
			 * hyperSearch.setSelected(false);
			 * hyperSearch.setEnabled(false); } else {
			 * hyperSearch.setEnabled(true); } }
			 */
			/*
			 * else if (source == searchFoldInsideRadioBtn || source ==
			 * searchFoldOutsideRadioBtn) { if (source ==
			 * searchFoldActualRadioBtn) { // the already selected
			 * button is selected once more ==> select default
			 * button searchFoldDefaultRadioBtn.setSelected(true);
			 * searchFoldActualRadioBtn = searchFoldDefaultRadioBtn; }
			 * else { ((JRadioButton)source).setSelected(true);
			 * searchFoldActualRadioBtn = (JRadioButton)source; } }
			 * else if (source == searchCommentInsideRadioBtn ||
			 * source == searchCommentOutsideRadioBtn) { if (source ==
			 * searchCommentActualRadioBtn) { // the already
			 * selected button is selected once more ==> select
			 * default button
			 * searchCommentDefaultRadioBtn.setSelected(true);
			 * searchCommentActualRadioBtn =
			 * searchCommentDefaultRadioBtn; } else {
			 * ((JRadioButton)source).setSelected(true);
			 * searchCommentActualRadioBtn = (JRadioButton)source; } }
			 */

		}
	} // }}}

	// {{{ SettingsActionHandler class
	class SettingsActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			SearchAndReplace.resetIgnoreFromTop(); // rwchg: when
								// settings
								// change, no
								// refind
			Object source = evt.getSource();
			// handle default filesetGrp
			if ((source == searchSelection
				|| source == searchAllBuffers
				|| source == searchDirectory) 
				&& !((AbstractButton)source).isSelected())
			{
				// source has been deselected
				// ==> set source to default
				source = searchCurrentBuffer;
			}
			if (source instanceof Component)
				org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, XSearchPanel.class,"+++ .2021: source = "+((Component)source).getName());
			else
				org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, XSearchPanel.class,"+++ .2028: source = "+source);
			
			/*
			 * these checks are removed in 42pre6, but I'm not sure
			 * if needed
			 */
			if (source == ignoreCase)
				SearchAndReplace.setIgnoreCase(ignoreCase.isSelected());
			else if (source == regexp)
				SearchAndReplace.setRegexp(regexp.isSelected());
			else if (source == searchBack || source == searchForward
				|| source == searchFromTop)
			{
				SearchAndReplace.setReverseSearch(searchBack.isSelected());
				SearchAndReplace.setSearchFromTop(searchFromTop.isSelected());
			}
			else if (source == wrap)
				SearchAndReplace.setAutoWrapAround(wrap.isSelected());
			else
			/* end of 42pre6 nopping */
			if (source == searchCurrentBuffer)
			{
				hyperSearch.setSelected(false);
				enableRowColumnSearch(true);
			}
			else if (source == searchSelection || source == searchAllBuffers
				|| source == searchProject || source == searchDirectory)
			{
				hyperSearch.setSelected(true);
/*				if ((source == searchAllBuffers || source == searchDirectory)
					&& synchronize.isSelected())
					synchronizeMultiFileSettings(); */ 
			}

			enableRowColumnSearch(source != searchSelection);
			save(true);
			updateEnabled();
		}
	} // }}}

	// {{{ MultiFileActionHandler class
	class MultiFileActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if (evt.getSource() == autoSync) {
				boolean selected = autoSync.getState();
				synchronize.setEnabled(!selected);
				directory.setEditable(!selected);
				filter.setEditable(!selected);
				SearchAndReplace.setAutoSync(selected);
				synchronizeMultiFileSettings();
				return;
			}
			if (evt.getSource() == choose)
			{
				String[] dirs = GUIUtilities.showVFSFileDialog(view, directory
					.getText(), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false);
				if (dirs != null)
					directory.setText(dirs[0]);
			}
			else if (evt.getSource() == synchronize)
			{
				Log.log(Log.DEBUG, BeanShell.class,
					"XSearchPanel.1972: synchronize.isSelected() = "
						+ synchronize.isSelected());
				jEdit.setBooleanProperty("xsearch.synchronize.toggle", synchronize
					.isSelected());
				synchronizeMultiFileSettings();
			}
			else
			// source is directory or filter field
			{
				// just as if Enter was pressed in another
				// text field
				ok();
			}
		}
	} // }}}

	// {{{ ButtonActionHandler class
	class ButtonActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();

			if (source == closeBtn)
				cancel();
			else if (source == find
				&& find.getReceivedEvent() == XSearchHistoryTextField.RECEIVED_EVENT_SELECT)
			{
				// another item in historytextfield selected
				// set history settings if available
				loadSettingsFromHistory();
			}
			else if (evalExtendedOptions())
			{
				if (source == findBtn || source == find || source == replace
					|| source == findAllBtn)
				{
					if (source == findAllBtn)
					{
						SearchAndReplace.setFindAll(true);
						// select "fromTop", repeat
						// necessary commands as done in
						// SettingsActionListener
						// note: fireActionPerformed not
						// possible, as we are already
						// in an awt thread
						SearchAndReplace.resetIgnoreFromTop();
						SearchAndReplace.setReverseSearch(false);
						SearchAndReplace.setSearchFromTop(true);
						searchFromTop.setSelected(true);
					}
					ok();
				}
				else if (source == replaceAndFindBtn)
				{
					if (!keepDialogChanged)
						keepDialog.setSelected(true);
					save(false);
					if (SearchAndReplace.replace(view))
						ok();
					else
						getToolkit().beep();
				}
				else if (source == replaceAllBtn)
				{
					if (searchSelection.isSelected()
						&& view.getTextArea().getSelectionCount() == 0)
					{
						GUIUtilities.error(view, "search-no-selection",
							null);
						return;
					}

					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					if (!save(false))
					{
						setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						getToolkit().beep();
						return;
					}

					if (searchSelection.isSelected())
					{
						if (SearchAndReplace.replace(view))
							closeOrKeepDialog();
						else
							getToolkit().beep();
					}
					else
					{
						if (SearchAndReplace.replaceAll(view, hyperSearch
							.isSelected()
							&& jEdit.getBooleanProperty(
								"xsearch.hyperReplace", true)))
							closeOrKeepDialog();
						else
							getToolkit().beep();
					}

					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}
	} // }}}

	// {{{ RegexpSupportActionListener class
	class RegexpSupportMouseListener extends MouseAdapter
	{
		public void mouseEntered(MouseEvent evt)
		{
			Log.log(Log.DEBUG, XSearchPanel.class,
				"+++ XSearchPanel.2097.mouse entered: find.getText = "
					+ find.getText() + ", selectedtext = "
					+ find.getSelectedText() + ", selectionStart = "
					+ find.getSelectionStart() + ", caret = "
					+ find.getCaretPosition());
			// pass the field which has focus; default is "find"
			srFieldData = new SearchReplaceFieldData(
				replace.hasFocus() ? (JTextField) replace : (JTextField) find,
				replace.hasFocus());
		}
	}

	class RegexpSupportActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			// second click on button should close popup, but I
			// cannot detect window
			// if(popup != null) {
			// Log.log(Log.DEBUG, XSearchPanel.class,"+++
			// XSearchPanel.2112: popup.isVisible() =
			// "+popup.isVisible());
			// Log.log(Log.DEBUG, XSearchPanel.class,"+++
			// XSearchPanel.2114: popup.isShowing() =
			// "+popup.isShowing());
			// Log.log(Log.DEBUG, XSearchPanel.class,"+++
			// XSearchPanel.2116: popup.isForegroundSet() =
			// "+popup.isForegroundSet());
			// }
			// if(popup != null && popup.isVisible())
			// {
			// //Log.log(Log.DEBUG, XSearchPanel.class,"+++
			// XSearchPanel.2095");
			// popup.setVisible(false);
			// popup = null;
			// return;
			// }
			popup = new ReSupportPopup(view, srFieldData);
			popup.show(regexpSupportButton, regexpSupportButton.getWidth() + 1,
				regexpSupportButton.getHeight() + 1);
			// select regexp per default
			regexp.setSelected(true);
			updateEnabled();
		}
	}

	// }}}

	class KeyHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent evt)
		{
			JFrame frame = getFrame();

			if (evt.getKeyCode() == KeyEvent.VK_ENTER)
			{
				ok();
				evt.consume();
			}
			else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				cancel();
				evt.consume();
			}
		}
	}

	public void focusOnDefaultComponent()
	{
		find.requestFocus();
		
	}

}
