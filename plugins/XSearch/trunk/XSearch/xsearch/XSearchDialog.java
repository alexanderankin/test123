/*
 * XSearchDialog.java - xsearch and replace dialog
 * :tabSize=2:indentSize=2:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Rudolf Widmann
 * portions copyright (C) 1998, 1999, 2000, 2001 Slava Pestov
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

// package org.gjt.sp.jedit.search;
package xsearch;

//{{{ Imports
import javax.swing.border.*;
//import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.SearchSettingsChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.search.*;
//}}}

/**
 * Search and replace dialog.
 * @author Slava Pestov
 * @version $Id$
 * derived version $Id$
 */
public class XSearchDialog extends EnhancedDialog implements EBComponent
{
	//{{{ Constants
	/**
	 * Default file set.
	 * @since jEdit 3.2pre2
	 */
	public static final int CURRENT_BUFFER = 0;
	public static final int ALL_BUFFERS = 1;
	public static final int DIRECTORY = 2;
	public static final int SEARCH_IN_OUT_NONE = 0;
	public static final int SEARCH_IN_OUT_INSIDE = 1;
	public static final int SEARCH_IN_OUT_OUTSIDE = 2;
	public static final int SEARCH_PART_NONE = 0;
	public static final int SEARCH_PART_PREFIX = 1;
	public static final int SEARCH_PART_SUFFIX = 2;
	public static final int SEARCH_PART_WHOLE_WORD = 3;
	//}}}

	//{{{ getSearchDialog() method
	public static XSearchDialog getSearchDialog(View view)
	{
		return (XSearchDialog)viewHash.get(view);
	} //}}}

	//{{{ showSearchDialog() method
	/**
	 * Displays a search and replace dialog box, reusing an existing one
	 * if necessary.
	 * @param view The view
	 * @param searchString The search string
	 * @param searchIn One of CURRENT_BUFFER, ALL_BUFFERS, or DIRECTORY
	 * @since jEdit 4.0pre6
	 */
	public static void showSearchDialog(View view, String searchString,
		int searchIn)
	{
		if (XSearchAndReplace.debug) Log.log(Log.DEBUG, BeanShell.class,
			"XSearchDialog.82: invoke showSearchDialog with searchString = "+searchString);
		XSearchDialog dialog = (XSearchDialog)viewHash.get(view);
		if(dialog != null)
		{
			// ugly workaround
			if(OperatingSystem.isUnix() && !OperatingSystem.isMacOS())
			{
				dialog.setVisible(false);
				dialog.setVisible(true);
			}

			dialog.setSearchString(searchString,searchIn);
			GUIUtilities.requestFocus(dialog,dialog.find);
			dialog.toFront();
			dialog.requestFocus();
		}
		else
		{
			dialog = new XSearchDialog(view,searchString,searchIn);
			viewHash.put(view,dialog);
		}
		XSearchAndReplace.resetIgnoreFromTop();
		dialog.keepDialogChanged = false;
	} //}}}

	//{{{ XSearchDialog constructor
	/**
	 * Creates a new search and replace dialog box.
	 * @param view The view
	 * @param searchString The search string
	 */
	public XSearchDialog(View view, String searchString)
	{
		this(view,searchString,CURRENT_BUFFER);
	} //}}}

	//{{{ XSearchDialog constructor
	/**
	 * Creates a new search and replace dialog box.
	 * @param view The view
	 * @param searchString The search string
	 * @param searchIn One of CURRENT_BUFFER, ALL_BUFFERS, or DIRECTORY
	 * @since jEdit 3.2pre2
	 * Dialog layout:
	 * ==============
			content.add(BorderLayout.EAST,buttonsPanel);
				grid.add(findBtn);
				grid.add(findAllBtn);
				grid.add(replaceAndFindBtn);
				grid.add(replaceAllBtn);
				grid.add(closeBtn);
			content.add(BorderLayout.CENTER,centerPanel);
				centerPanel.add(BorderLayout.NORTH,globalFieldPanel);
					fieldPanel.add(label);
					fieldPanel.add(find);
					fieldPanel.add(selectivShowBox);
					fieldPanel.add(currentSelectedOptionsLabel);
					fieldPanel.add(fieldPanelReplaceLabel);
					fieldPanel.add(replaceModeBox);
					fieldPanel.add(fieldPanelVerticalStrut);
					fieldPanel.add(replace);
				centerPanel.add(BorderLayout.CENTER,searchSettingsPanel);
					searchSettings.add(new JLabel(jEdit.getProperty("search.fileset")));
					searchSettings.add(new JLabel(jEdit.getProperty("search.settings")));
					searchSettings.add(new JLabel(jEdit.getProperty("search.direction")));
					searchSettings.add(searchSelection);
					searchSettings.add(keepDialog);
					searchSettings.add(searchFromTop);
					searchSettings.add(searchCurrentBuffer);
					searchSettings.add(ignoreCase);
					searchSettings.add(searchBack);
					searchSettings.add(searchAllBuffers);
					searchSettings.add(regexp);
					searchSettings.add(searchForward);
					searchSettings.add(searchDirectory);
					searchSettings.add(hyperSearch);
					searchSettings.add(wrap);
			content.add(BorderLayout.SOUTH,southPanel);
				southPanel.add(BorderLayout.CENTER,extendedOptionsPanel);
					extendedOptions.add(columnBox);
					extendedOptions.add(commentBox);
					extendedOptions.add(foldBox);
					extendedOptions.add(resetSettingsButton);
					extendedOptions.add(rowBox);
					extendedOptions.add(tentativRadioBtn);
					extendedOptions.add(wordPartBox);
				southPanel.add(BorderLayout.SOUTH,multiFilePanel);
					multifile.add(choose);
					multifile.add(directory);
					multifile.add(filter);
					multifile.add(label);
					multifile.add(searchSubDirectories);
	 */
	public XSearchDialog(View view, String searchString, int searchIn)
	{
		super(view,jEdit.getProperty("xsearch.title"),false);

		this.view = view;

		content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(0,12,12,12));
		setContentPane(content);

		centerPanel = new JPanel(new BorderLayout());
		southPanel = new JPanel(new BorderLayout());
		// rwchg: switchable panel display
		// memorize add-panels
		globalFieldPanel = createFieldPanel();
		searchSettingsPanel = createSearchSettingsPanel();
		multiFilePanel = createMultiFilePanel();
		buttonsPanel = createButtonsPanel();
		extendedOptionsPanel = createExtendedOptionsPanelGridBag();

		centerPanel.add(BorderLayout.NORTH,globalFieldPanel);
		centerPanel.add(BorderLayout.CENTER,searchSettingsPanel);
		content.add(BorderLayout.CENTER,centerPanel);
		southPanel.add(BorderLayout.CENTER,extendedOptionsPanel);
		southPanel.add(BorderLayout.SOUTH,multiFilePanel);
		content.add(BorderLayout.SOUTH,southPanel);

		content.add(BorderLayout.EAST,buttonsPanel);
		
		setupSearchSettings();

		// moved to  (0.9)
		setSearchString(searchString,searchIn);

		// all pre-selections are done: show / hide panels
		if (!showSettings.isSelected()) showHideOptions(showSettings);
		if (!showReplace.isSelected()) showHideOptions(showReplace);
		if (!showExtendedOptions.isSelected()) showHideOptions(showExtendedOptions);
		if (!searchDirectory.isSelected() && !searchAllBuffers.isSelected()) showHideOptions(searchDirectory);
		setupCurrentSelectedOptionsLabel();

		directory.addCurrentToHistory();

		keepDialog.setSelected(jEdit.getBooleanProperty(
			"search.keepDialog.toggle"));
		searchSettingsHistoryRadioBtn.setSelected(jEdit.getBooleanProperty(
			"search.settingsHistory.toggle"));

		// moved from (0.9) setSearchString(searchString,searchIn);

		pack();
		jEdit.unsetProperty("search.width");
		jEdit.unsetProperty("search.d-width");
		jEdit.unsetProperty("search.height");
		jEdit.unsetProperty("search.d-height");
		GUIUtilities.loadGeometry(this,"search");
		show();

		EditBus.addToBus(this);

		GUIUtilities.requestFocus(this,find);
	} //}}}

	//{{{ setSearchString() method
	/**
	 * Sets the search string.
	 * @since jEdit 4.0pre5
	 */
	public void setSearchString(String searchString, int searchIn)
	{
		if(searchString == null)
			find.setText(null);
		else
		{
			if(searchString.indexOf('\n') == -1)
			{
				find.setText(searchString);
				find.selectAll();
			}
			else if(searchIn == CURRENT_BUFFER)
			{
				/* more than 1 line selected
				 * set "search in selection"
				 * set "hypersearch"
				 * setup start/end row
				 */
				searchSelection.setSelected(true);
				hyperSearch.setSelected(true);
				setupStartEndRowFromSelection();
			}
		}

		if(searchIn == CURRENT_BUFFER)
		{
			if(!searchSelection.isSelected())
			{
				// might be already selected, see above.
				searchCurrentBuffer.setSelected(true);

				/* this property is only loaded and saved if
				 * the 'current buffer' file set is selected.
				 * otherwise, it defaults to on. */
				hyperSearch.setSelected(jEdit.getBooleanProperty(
					"search.hypersearch.toggle"));
			}
		}
		else if(searchIn == ALL_BUFFERS)
		{
			searchAllBuffers.setSelected(true);
			hyperSearch.setSelected(true);
		}
		else if(searchIn == DIRECTORY)
		{
			hyperSearch.setSelected(true);
			searchDirectory.setSelected(true);
		}

		updateEnabled();
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		
		if (XSearchAndReplace.debug) Log.log(Log.DEBUG, BeanShell.class,"XSearchDialog.331: ok invoked with find.getText() = "+find.getText()+", regexp.isSelected() = "+regexp.isSelected()+", wordPartPrefixRadioBtn.isSelected() = "+wordPartPrefixRadioBtn.isSelected()+", wordPartWholeRadioBtn.isSelected() = "+wordPartWholeRadioBtn.isSelected());
		try
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			if(!save(false))
				return;
			
			if(hyperSearch.isSelected() || searchSelection.isSelected())
			{
				if(XSearchAndReplace.hyperSearch(view,
					searchSelection.isSelected()))
					closeOrKeepDialog();
			}
			else
			{
				boolean searchResult = XSearchAndReplace.find(view);
				if(searchResult)
					closeOrKeepDialog();
				else
				{
					toFront();
					requestFocus();
					find.requestFocus();
				}
			}
		}
		finally
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	} //}}}

	//{{{ cancel() method
	public void cancel()
	{
		save(true);
		GUIUtilities.saveGeometry(this,"search");
		dispose();
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof SearchSettingsChanged)
		{
			ignoreCase.setSelected(XSearchAndReplace.getIgnoreCase());
			regexp.setSelected(XSearchAndReplace.getRegexp());
		}
		else if(msg instanceof ViewUpdate)
		{
			ViewUpdate vmsg = (ViewUpdate)msg;
			if(vmsg.getView() == view
				&& vmsg.getWhat() == ViewUpdate.CLOSED)
			{
				viewHash.remove(view);
			}
		}
	} //}}}

	//{{{ dispose() method
	public void dispose()
	{
		EditBus.removeFromBus(this);
		viewHash.remove(view);
		super.dispose();
	} //}}}

	//{{{ Private members

	private static HashMap viewHash = new HashMap();

	//{{{ Instance variables
	private View view;

	// fields
	private XSearchHistoryTextField find;
	private SettingsHistoryModel settingsHistory;
	private HistoryTextField replace;

	private JRadioButton stringReplace, beanShellReplace;

	// panels
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
//	private Box foldBox;

	// components inside of panels
	private JLabel fieldPanelReplaceLabel;
	private JLabel currentSelectedOptionsLabel = new JLabel(jEdit.getProperty("search.currOpt.label"));
	private int optionsLabelIndex;
	private StringBuffer currentSelectedSettingsOptions = new StringBuffer();
	private StringBuffer currentSelectedExtendedOptions = new StringBuffer();
	private JRadioButton showSettings;
	private JRadioButton showReplace;
	private JRadioButton showExtendedOptions;

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
	//private JRadioButton searchFoldActualRadioBtn = searchFoldDefaultRadioBtn;

	// extended options: comment search
	private JRadioButton searchCommentInsideRadioBtn;
	private JRadioButton searchCommentOutsideRadioBtn;
	private JRadioButton searchCommentDefaultRadioBtn = new JRadioButton();
	//private JRadioButton searchCommentActualRadioBtn = searchCommentDefaultRadioBtn;
	private JLabel hyperRangeLabel;
	private JLabel hyperRangeLabelUp;
	private JLabel hyperRangeLabelDown;
	private JTextField hyperRangeUpTextField;
	private JTextField hyperRangeDownTextField;

	// extended options: load search settings
	private JRadioButton searchSettingsHistoryRadioBtn;

	private Component fieldPanelVerticalStrut = Box.createVerticalStrut(3);

	// search settings
	private JCheckBox keepDialog, ignoreCase, regexp, hyperSearch,
		wrap;
	private JRadioButton searchFromTop, searchBack, searchForward;
	private JRadioButton searchSelection, searchCurrentBuffer, searchAllBuffers,
		searchDirectory;

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
	private JButton choose;

	// buttons
	private JButton findBtn, /* replaceBtn, */ replaceAndFindBtn, replaceAllBtn,
		closeBtn;
	private JButton findAllBtn; // since XSearch0.4
	private JButton resetSettingsButton; // since XSearch0.7
	private boolean resetSettingsButtonPresent = false; // since XSearch0.7
	private boolean keepDialogChanged;  // flag: keep dialog changed by user
	//}}}

	//{{{ createFieldPanel() method
	private JPanel createFieldPanel()
	{
		ButtonActionHandler actionHandler = new ButtonActionHandler();

		JPanel fieldPanel = new JPanel(new VariableGridLayout(
			VariableGridLayout.FIXED_NUM_COLUMNS,1));
		fieldPanel.setBorder(new EmptyBorder(0,0,12,12));

		JLabel label = new JLabel(jEdit.getProperty("search.find"));
		label.setDisplayedMnemonic(jEdit.getProperty("search.find.mnemonic")
			.charAt(0));
		find = new XSearchHistoryTextField("find", true, false);
		find.setColumns(25);
		if (jEdit.getBooleanProperty("xsearch.textAreaFont", true))
			find.setFont(UIManager.getFont("TextArea.font"));

		find.addActionListener(actionHandler);
		label.setLabelFor(find);
		label.setBorder(new EmptyBorder(12,0,2,0));
		fieldPanel.add(label);
		// add: "Search for (press up arrow to recall previous)"
		fieldPanel.add(find);
		// add: <search input textField>
		settingsHistory = new SettingsHistoryModel("search.settings");

		// rwchg add panel display selection radio buttons
		SelectivShowActionHandler selectivShowActionHandler = new SelectivShowActionHandler();
		Box selectivShowBox = new Box(BoxLayout.X_AXIS);
		selectivShowBox.add(new JLabel(jEdit.getProperty("search.show-options")));

		showSettings = new MyJRadioButton(jEdit.getProperty("search.show-settings"));
		showSettings.setSelected(jEdit.getBooleanProperty("search.show-settings.toggle"));
		showSettings.addActionListener(selectivShowActionHandler);
		showReplace = new MyJRadioButton(jEdit.getProperty("search.show-replace"));
		showReplace.setSelected(jEdit.getBooleanProperty("search.show-replace.toggle"));
		showReplace.addActionListener(selectivShowActionHandler);
		showExtendedOptions = new MyJRadioButton(jEdit.getProperty("search.show-extended"));
		showExtendedOptions.setSelected(jEdit.getBooleanProperty("search.show-extended.toggle"));
		showExtendedOptions.addActionListener(selectivShowActionHandler);
		

		selectivShowBox.add(showSettings);
		selectivShowBox.add(showReplace);
		selectivShowBox.add(showExtendedOptions);

		fieldPanel.add(selectivShowBox);
		// add: <selectiv show options: search, replace, extended
		fieldPanel.add(currentSelectedOptionsLabel);
		// add: <display of current selected find options
		optionsLabelIndex = fieldPanel.getComponentCount() - 1;

		fieldPanelReplaceLabel = new JLabel(jEdit.getProperty("search.replace"));
		fieldPanelReplaceLabel.setDisplayedMnemonic(jEdit.getProperty("search.replace.mnemonic")
			.charAt(0));
		fieldPanelReplaceLabel.setBorder(new EmptyBorder(12,0,0,0));
		fieldPanel.add(fieldPanelReplaceLabel);
		// add: "Replace with"

		ButtonGroup grp = new ButtonGroup();
		ReplaceActionHandler replaceActionHandler = new ReplaceActionHandler();

		// we use a custom JRadioButton subclass that returns
		// false for isFocusTraversable() so that the user can
		// tab from the search field to the replace field with
		// one keystroke

		replaceModeBox = new Box(BoxLayout.X_AXIS);
		stringReplace = new MyJRadioButton(jEdit.getProperty(
			"search.string-replace-btn"));
		stringReplace.addActionListener(replaceActionHandler);
		grp.add(stringReplace);
		replaceModeBox.add(stringReplace);

		replaceModeBox.add(Box.createHorizontalStrut(12));

		beanShellReplace = new MyJRadioButton(jEdit.getProperty(
			"search.beanshell-replace-btn"));
		beanShellReplace.addActionListener(replaceActionHandler);
		grp.add(beanShellReplace);
		replaceModeBox.add(beanShellReplace);

		fieldPanel.add(replaceModeBox);
		// add: <replace mode: Text, Return value of BeanShell snippet
		fieldPanel.add(fieldPanelVerticalStrut);

		replace = new HistoryTextField("replace");
		if (jEdit.getBooleanProperty("xsearch.textAreaFont", true))
			replace.setFont(UIManager.getFont("TextArea.font"));
		replace.addActionListener(actionHandler);
		fieldPanelReplaceLabel.setLabelFor(replace);
		fieldPanel.add(replace);
		// add: <replace input textField>
		return fieldPanel;
	} //}}}
  private GridBagConstraints makeStdConstraints(int row, int col, int size, Insets ins) {
		return new GridBagConstraints(
			col, row, size, 1,  0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0);
	}

	//{{{ createExtendedOptionsPanelGridBag() method
	private JPanel createExtendedOptionsPanelGridBag()
	{
		JPanel extendedOptions = new JPanel(new GridBagLayout());
		extendedOptions.setBorder(new EmptyBorder(0,0,12,12));

		ExtendedOptionsActionHandler extendedOptionsActionHandler = new ExtendedOptionsActionHandler();
		
		int rowCounter = 0;  // grid x
		Insets stdInset = new Insets(0, 0, 1, 5);
		
		/*******************************************************************
		 * word part handling: whole word / prefix / suffix
		 *******************************************************************/
		wordPartWholeRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.word-whole"));
		wordPartWholeRadioBtn.addActionListener(extendedOptionsActionHandler);
		wordPartPrefixRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.word-prefix"));
		wordPartPrefixRadioBtn.addActionListener(extendedOptionsActionHandler);
		wordPartSuffixRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.word-suffix"));
		wordPartSuffixRadioBtn.addActionListener(extendedOptionsActionHandler);
/*
		extendedOptions.add(wordPartWholeRadioBtn, new GridBagConstraints(
			0, rowCounter,  2, 1,  0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.NONE, stdInset, 0, 0));
		extendedOptions.add(wordPartPrefixRadioBtn, new GridBagConstraints(
			2, rowCounter,  2, 1,  0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.NONE, stdInset, 0, 0));
		extendedOptions.add(wordPartSuffixRadioBtn, new GridBagConstraints(
			4, rowCounter,  2, 1,  0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.NONE, stdInset, 0, 0));
			*/
		if (jEdit.getBooleanProperty("xsearch.wordPartSearch", true)) {
			extendedOptions.add(wordPartWholeRadioBtn, makeStdConstraints(rowCounter, 0, 2, stdInset));
			extendedOptions.add(wordPartPrefixRadioBtn, makeStdConstraints(rowCounter, 2, 2, stdInset));
			extendedOptions.add(wordPartSuffixRadioBtn, makeStdConstraints(rowCounter, 4, 2, stdInset));
			rowCounter++;	
		}

		ButtonGroupHide wordPartGrp = new ButtonGroupHide();

		wordPartGrp.add(wordPartDefaultRadioBtn);
		wordPartGrp.add(wordPartWholeRadioBtn);
		wordPartGrp.add(wordPartPrefixRadioBtn);
		wordPartGrp.add(wordPartSuffixRadioBtn);
		wordPartDefaultRadioBtn.setSelected(true);

		/*******************************************************************
		 * column handling
		 *******************************************************************/
		columnRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.column"));
		columnRadioBtn.addActionListener(extendedOptionsActionHandler);

		columnExpandTabsRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.column.expand-tabs"));
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

		if (jEdit.getBooleanProperty("xsearch.columnSearch", true)) {
			extendedOptions.add(columnRadioBtn, makeStdConstraints(rowCounter, 0, 1, stdInset));
			extendedOptions.add(columnExpandTabsRadioBtn, makeStdConstraints(rowCounter, 6, 1, stdInset));
			extendedOptions.add(leftColumnLabel, makeStdConstraints(rowCounter, 2, 1, stdInset));
			extendedOptions.add(columnLeftColumnField, makeStdConstraints(rowCounter, 3, 1, stdInset));
			extendedOptions.add(rightColumnLabel, makeStdConstraints(rowCounter, 4, 1, stdInset));
			extendedOptions.add(columnRightColumnField, makeStdConstraints(rowCounter, 5, 1, stdInset));
			rowCounter++;	
		}
		
		/*******************************************************************
		 * row handling
		 *******************************************************************/
		rowRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.row"));
		rowRadioBtn.addActionListener(extendedOptionsActionHandler);

		rowLeftRowField = new JTextField(3);
		leftRowLabel = new JLabel(jEdit.getProperty("search.ext.row.left"));
		rowRightRowField = new JTextField(3);
		rowRightRowField.setMargin(new Insets(2,2,2,2));
		
		rightRowLabel = new JLabel(jEdit.getProperty("search.ext.row.right"));

		rowLeftRowField.setEnabled(false);
		leftRowLabel.setEnabled(false);
		rowRightRowField.setEnabled(false);
		rightRowLabel.setEnabled(false);

		if (jEdit.getBooleanProperty("xsearch.rowSearch", true)) {
			extendedOptions.add(rowRadioBtn, makeStdConstraints(rowCounter, 0, 1, stdInset));
			extendedOptions.add(leftRowLabel, makeStdConstraints(rowCounter, 2, 1, stdInset));
			extendedOptions.add(rowLeftRowField, makeStdConstraints(rowCounter, 3, 1, stdInset));
			extendedOptions.add(rightRowLabel, makeStdConstraints(rowCounter, 4, 1, stdInset));
			extendedOptions.add(rowRightRowField, makeStdConstraints(rowCounter, 5, 1, stdInset));
			rowCounter++;
		}

		/*******************************************************************
		 * tentativ search
		 *******************************************************************/
		tentativRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.tentativ"));
		if (jEdit.getBooleanProperty("xsearch.tentativSearch", false))
			extendedOptions.add(tentativRadioBtn);

		/*******************************************************************
		 * folding handling
		 * search only  o inside fold  o outside fold
		 *******************************************************************/
		JLabel searchFoldLabel = new JLabel(jEdit.getProperty("search.ext.fold"));
		searchFoldInsideRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.fold-inside"));
		searchFoldOutsideRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.fold-outside"));
		searchFoldInsideRadioBtn.addActionListener(extendedOptionsActionHandler);
		searchFoldOutsideRadioBtn.addActionListener(extendedOptionsActionHandler);
		if (jEdit.getBooleanProperty("xsearch.foldSearch", true)) {
			extendedOptions.add(searchFoldLabel, makeStdConstraints(rowCounter, 0, 2, stdInset));
			extendedOptions.add(searchFoldInsideRadioBtn, makeStdConstraints(rowCounter, 2, 2, stdInset));
			extendedOptions.add(searchFoldOutsideRadioBtn, makeStdConstraints(rowCounter, 4, 2, stdInset));
			rowCounter++;	
		}

		ButtonGroupHide foldGrp = new ButtonGroupHide();
		foldGrp.add(searchFoldDefaultRadioBtn);
		foldGrp.add(searchFoldInsideRadioBtn);
		foldGrp.add(searchFoldOutsideRadioBtn);
		searchFoldDefaultRadioBtn.setSelected(true);

		/*******************************************************************
		 * comment handling
		 * search only  o inside comment  o outside comment
		 *******************************************************************/
		JLabel searchCommentLabel = new JLabel(jEdit.getProperty("search.ext.comment"));
		searchCommentInsideRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.comment-inside"));
		searchCommentOutsideRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.comment-outside"));
		//searchCommentInsideRadioBtn.addActionListener(extendedOptionsActionHandler);
		//searchCommentOutsideRadioBtn.addActionListener(extendedOptionsActionHandler);
		if (jEdit.getBooleanProperty("xsearch.commentSearch", true)) {
			extendedOptions.add(searchCommentLabel, makeStdConstraints(rowCounter, 0, 2, stdInset));
			extendedOptions.add(searchCommentInsideRadioBtn, makeStdConstraints(rowCounter, 2, 2, stdInset));
			extendedOptions.add(searchCommentOutsideRadioBtn, makeStdConstraints(rowCounter, 4, 2, stdInset));
			rowCounter++;	
		}
		ButtonGroupHide commentGrp = new ButtonGroupHide();
		commentGrp.add(searchCommentDefaultRadioBtn);
		commentGrp.add(searchCommentInsideRadioBtn);
		commentGrp.add(searchCommentOutsideRadioBtn);
		searchCommentDefaultRadioBtn.setSelected(true);

		/*******************************************************************
		 * hyper range handling
		 * hyper range: upper [] lower []
		 *******************************************************************/
		hyperRangeLabel = new JLabel(jEdit.getProperty("search.ext.hyperRangeLabel"));
	  hyperRangeLabelUp = new JLabel(jEdit.getProperty("search.ext.hyperRangeLabelUp"));
		hyperRangeLabelDown = new JLabel(jEdit.getProperty("search.ext.hyperRangeLabelDown"));
		hyperRangeUpTextField = new JTextField(3);
		hyperRangeDownTextField = new JTextField(3);
		if (jEdit.getBooleanProperty("xsearch.hyperRange", true)) {
			extendedOptions.add(hyperRangeLabel, makeStdConstraints(rowCounter, 0, 2, stdInset));
			extendedOptions.add(hyperRangeLabelUp, makeStdConstraints(rowCounter, 2, 1, stdInset));
			extendedOptions.add(hyperRangeUpTextField, makeStdConstraints(rowCounter, 3, 1, stdInset));
			extendedOptions.add(hyperRangeLabelDown, makeStdConstraints(rowCounter, 4, 1, stdInset));
			extendedOptions.add(hyperRangeDownTextField, makeStdConstraints(rowCounter, 5, 1, stdInset));
			rowCounter++;	
		}
		/*******************************************************************
		 * search settings history
		 *******************************************************************/
		searchSettingsHistoryRadioBtn = new JRadioButton(jEdit.getProperty("search.ext.settings-history"));
		searchSettingsHistoryRadioBtn.addActionListener(extendedOptionsActionHandler);
		if (jEdit.getBooleanProperty("xsearch.settingsHistory", true)) {
			extendedOptions.add(searchSettingsHistoryRadioBtn, makeStdConstraints(rowCounter, 0, 2, stdInset));
			rowCounter++;	
		}
		
		return extendedOptions;
	} //}}}

	//{{{ createSearchSettingsPanel() method
	private JPanel createSearchSettingsPanel()
	{
		JPanel searchSettings = new JPanel(new VariableGridLayout(
			VariableGridLayout.FIXED_NUM_COLUMNS,3));
		searchSettings.setBorder(new EmptyBorder(0,0,12,12));

		SettingsActionHandler actionHandler = new SettingsActionHandler();
		SelectivShowActionHandler selectivShowActionHandler = new SelectivShowActionHandler();

		ButtonGroup fileset = new ButtonGroup();
		ButtonGroup direction = new ButtonGroup();

		searchSettings.add(new JLabel(jEdit.getProperty("search.fileset")));

		searchSettings.add(new JLabel(jEdit.getProperty("search.settings")));

		searchSettings.add(new JLabel(jEdit.getProperty("search.direction")));

		searchSelection = new JRadioButton(jEdit.getProperty("search.selection"));
		searchSelection.setMnemonic(jEdit.getProperty("search.selection.mnemonic")
			.charAt(0));
		fileset.add(searchSelection);
		searchSettings.add(searchSelection);
		searchSelection.addActionListener(actionHandler);
		searchSelection.addActionListener(selectivShowActionHandler);

		keepDialog = new JCheckBox(jEdit.getProperty("search.keep"));
		keepDialog.setMnemonic(jEdit.getProperty("search.keep.mnemonic")
			.charAt(0));
		keepDialog.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				keepDialogChanged = true;
			}
		});
		searchSettings.add(keepDialog);
		// rwchg: add search.fromTop
		// properties should be added in jedit_gui.props
		searchFromTop = new JRadioButton(jEdit.getProperty("search.fromTop")) ;
		searchFromTop.setMnemonic(jEdit.getProperty("search.fromTop.mnemonic")
			.charAt(0));
		direction.add(searchFromTop);
		searchSettings.add(searchFromTop);
		searchFromTop.addActionListener(actionHandler);

		searchCurrentBuffer = new JRadioButton(jEdit.getProperty("search.current"));
		searchCurrentBuffer.setMnemonic(jEdit.getProperty("search.current.mnemonic")
			.charAt(0));
		fileset.add(searchCurrentBuffer);
		searchSettings.add(searchCurrentBuffer);
		searchCurrentBuffer.addActionListener(actionHandler);
		searchCurrentBuffer.addActionListener(selectivShowActionHandler);

		ignoreCase = new JCheckBox(jEdit.getProperty("search.case"));
		ignoreCase.setMnemonic(jEdit.getProperty("search.case.mnemonic")
			.charAt(0));
		searchSettings.add(ignoreCase);
		ignoreCase.addActionListener(actionHandler);

		searchBack = new JRadioButton(jEdit.getProperty("search.back"));
		searchBack.setMnemonic(jEdit.getProperty("search.back.mnemonic")
			.charAt(0));
		direction.add(searchBack);
		searchSettings.add(searchBack);
		searchBack.addActionListener(actionHandler);

		searchAllBuffers = new JRadioButton(jEdit.getProperty("search.all"));
		searchAllBuffers.setMnemonic(jEdit.getProperty("search.all.mnemonic")
			.charAt(0));
		fileset.add(searchAllBuffers);
		searchSettings.add(searchAllBuffers);
		searchAllBuffers.addActionListener(actionHandler);
		searchAllBuffers.addActionListener(selectivShowActionHandler);

		regexp = new JCheckBox(jEdit.getProperty("search.ext.regexp"));
		regexp.setMnemonic(jEdit.getProperty("search.regexp.mnemonic")
			.charAt(0));
		searchSettings.add(regexp);
		regexp.addActionListener(actionHandler);

		searchForward = new JRadioButton(jEdit.getProperty("search.forward"));
		searchForward.setMnemonic(jEdit.getProperty("search.forward.mnemonic")
			.charAt(0));
		direction.add(searchForward);
		searchSettings.add(searchForward);
		searchForward.addActionListener(actionHandler);

		searchDirectory = new JRadioButton(jEdit.getProperty("search.directory"));
		searchDirectory.setMnemonic(jEdit.getProperty("search.directory.mnemonic")
			.charAt(0));
		fileset.add(searchDirectory);
		searchSettings.add(searchDirectory);
		searchDirectory.addActionListener(actionHandler);
		searchDirectory.addActionListener(selectivShowActionHandler);

		hyperSearch = new JCheckBox(jEdit.getProperty("search.hypersearch"));
		hyperSearch.setMnemonic(jEdit.getProperty("search.hypersearch.mnemonic")
			.charAt(0));
		searchSettings.add(hyperSearch);
		hyperSearch.addActionListener(actionHandler);

		wrap = new JCheckBox(jEdit.getProperty("search.wrap"));
		wrap.setMnemonic(jEdit.getProperty("search.wrap.mnemonic")
			.charAt(0));
		searchSettings.add(wrap);
		wrap.addActionListener(actionHandler);

		return searchSettings;
	} //}}}

	//{{{ createMultiFilePanel() method
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

		cons.insets = new Insets(0,0,3,0);

		JLabel label = new JLabel(jEdit.getProperty("search.filterField"),
			SwingConstants.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		label.setDisplayedMnemonic(jEdit.getProperty("search.filterField.mnemonic")
			.charAt(0));
		label.setLabelFor(filter);
		cons.weightx = 0.0f;
		layout.setConstraints(label,cons);
		multifile.add(label);

		cons.insets = new Insets(0,0,3,6);
		cons.weightx = 1.0f;
		layout.setConstraints(filter,cons);
		multifile.add(filter);

		cons.gridy++;

		directory = new HistoryTextField("search.directory");
		directory.setColumns(25);
		directory.addActionListener(actionListener);

		label = new JLabel(jEdit.getProperty("search.directoryField"),
			SwingConstants.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));

		label.setDisplayedMnemonic(jEdit.getProperty("search.directoryField.mnemonic")
			.charAt(0));
		label.setLabelFor(directory);
		cons.insets = new Insets(0,0,3,0);
		cons.weightx = 0.0f;
		layout.setConstraints(label,cons);
		multifile.add(label);

		cons.insets = new Insets(0,0,3,6);
		cons.weightx = 1.0f;
		cons.gridwidth = 2;
		layout.setConstraints(directory,cons);
		multifile.add(directory);

		choose = new JButton(jEdit.getProperty("search.choose"));
		choose.setMnemonic(jEdit.getProperty("search.choose.mnemonic")
			.charAt(0));
		cons.insets = new Insets(0,0,3,0);
		cons.weightx = 0.0f;
		cons.gridwidth = 1;
		layout.setConstraints(choose,cons);
		multifile.add(choose);
		choose.addActionListener(actionListener);

		cons.insets = new Insets(0,0,0,0);
		cons.gridy++;
		cons.gridwidth = 4;

		searchSubDirectories = new JCheckBox(jEdit.getProperty(
			"search.subdirs"));
		searchSubDirectories.setMnemonic(jEdit.getProperty("search.subdirs.mnemonic")
			.charAt(0));
		layout.setConstraints(searchSubDirectories,cons);
		multifile.add(searchSubDirectories);

		return multifile;
	} //}}}

	//{{{ createButtonsPanel() method
	private Box createButtonsPanel()
	{
		Box box = new Box(BoxLayout.Y_AXIS);

		ButtonActionHandler actionHandler = new ButtonActionHandler();

		box.add(Box.createVerticalStrut(12));

		grid = new JPanel(new GridLayout(0,1,0,12));

		findBtn = new JButton(jEdit.getProperty("search.findBtn"));
		getRootPane().setDefaultButton(findBtn);
		grid.add(findBtn);
		findBtn.addActionListener(actionHandler);

		findAllBtn = new JButton(jEdit.getProperty("search.findAllBtn"));
		// findAllBtn.setMnemonic(jEdit.getProperty("search.findAllBtn.mnemonic").charAt(0));
		if (jEdit.getBooleanProperty("xsearch.findAllButton", true))
			grid.add(findAllBtn);
		findAllBtn.addActionListener(actionHandler);

		/* replaceBtn = new JButton(jEdit.getProperty("search.replaceBtn"));
		replaceBtn.setMnemonic(jEdit.getProperty("search.replaceBtn.mnemonic")
			.charAt(0));
		grid.add(replaceBtn);
		replaceBtn.addActionListener(actionHandler); */

		replaceAndFindBtn = new JButton(jEdit.getProperty("search.replaceAndFindBtn"));
		replaceAndFindBtn.setMnemonic(jEdit.getProperty("search.replaceAndFindBtn.mnemonic")
			.charAt(0));
		grid.add(replaceAndFindBtn);
		replaceAndFindBtn.addActionListener(actionHandler);

		replaceAllBtn = new JButton(jEdit.getProperty("search.replaceAllBtn"));
		replaceAllBtn.setMnemonic(jEdit.getProperty("search.replaceAllBtn.mnemonic")
			.charAt(0));
		grid.add(replaceAllBtn);
		replaceAllBtn.addActionListener(actionHandler);

		resetSettingsButton = new JButton(jEdit.getProperty("search.ext.reset"));
		resetSettingsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				SearchSettings.resetSettings();
				setupSearchSettings();
				showHideOptions(searchCurrentBuffer);
				setupCurrentSelectedOptionsLabel();
			}
		});
		if (jEdit.getBooleanProperty("xsearch.resetButton", true)) {
			grid.add(resetSettingsButton);
			resetSettingsButtonPresent = true;
		} else 
			resetSettingsButtonPresent = false;

		closeBtn = new JButton(jEdit.getProperty("common.close"));
		grid.add(closeBtn);
		closeBtn.addActionListener(actionHandler);

		grid.setMaximumSize(grid.getPreferredSize());

		box.add(grid);
		box.add(Box.createGlue());

		return box;
	} //}}}

	//{{{ setupSearchSettings() method
	private void setupSearchSettings() {
		//boolean resetRegex = true; 
		// ico wordpart, regex was implicit set: reset it
		switch (XSearchAndReplace.getWordPartOption()) {
			case SEARCH_PART_PREFIX: wordPartPrefixRadioBtn.setSelected(true);
				break;
			case SEARCH_PART_SUFFIX: wordPartSuffixRadioBtn.setSelected(true);
				break;
			case SEARCH_PART_WHOLE_WORD: wordPartWholeRadioBtn.setSelected(true);
				break;
		//	default: resetRegex = false;	
	  	default: wordPartDefaultRadioBtn.setSelected(true);
		}
		// if (resetRegex) XSearchAndReplace.setRegexp(false);
		tentativRadioBtn.setSelected(XSearchAndReplace.getTentativOption());
		
		switch (XSearchAndReplace.getCommentOption()) {
			case SEARCH_IN_OUT_INSIDE: searchCommentInsideRadioBtn.setSelected(true);
				break;
			case SEARCH_IN_OUT_OUTSIDE: searchCommentOutsideRadioBtn.setSelected(true);
				break;
	  	default: searchCommentDefaultRadioBtn.setSelected(true);
				
		}
		switch (XSearchAndReplace.getFoldOption()) {
			case SEARCH_IN_OUT_INSIDE: searchFoldInsideRadioBtn.setSelected(true);
				break;
			case SEARCH_IN_OUT_OUTSIDE: searchFoldOutsideRadioBtn.setSelected(true);
				break;
	  	default: searchFoldDefaultRadioBtn.setSelected(true);
		}
		if (XSearchAndReplace.getColumnOption()) {
			columnRadioBtn.setSelected(true);
			columnExpandTabsRadioBtn.setSelected(XSearchAndReplace.getColumnExpandTabsOption());
			columnLeftColumnField.setText(Integer.toString(XSearchAndReplace.getColumnLeftCol()));
			columnRightColumnField.setText(Integer.toString(XSearchAndReplace.getColumnRightCol()));
			enableColumnOptions(true);
		} else {
			columnRadioBtn.setSelected(false);
			enableColumnOptions(false);
		}
		if (XSearchAndReplace.getRowOption()) {
			rowRadioBtn.setSelected(true);
			rowLeftRowField.setText(Integer.toString(XSearchAndReplace.getRowLeftRow()));
			rowRightRowField.setText(Integer.toString(XSearchAndReplace.getRowRightRow()));
			enableRowOptions(true);
		} else {
			rowRadioBtn.setSelected(false);
			enableRowOptions(false);
		}
		//setup hyper range
		if (XSearchAndReplace.getHyperRangeUpper() == -1) 
			hyperRangeUpTextField.setText("");
		else
			hyperRangeUpTextField.setText(Integer.toString(XSearchAndReplace.getHyperRangeUpper()));
		if (XSearchAndReplace.getHyperRangeLower() == -1) 
			hyperRangeDownTextField.setText("");
		else
			hyperRangeDownTextField.setText(Integer.toString(XSearchAndReplace.getHyperRangeLower()));
		
		ignoreCase.setSelected(XSearchAndReplace.getIgnoreCase());
		regexp.setSelected(XSearchAndReplace.getRegexp());
		wrap.setSelected(XSearchAndReplace.getAutoWrapAround());

		if(XSearchAndReplace.getReverseSearch())
			searchBack.setSelected(true);
		else if (XSearchAndReplace.getSearchFromTop())
			searchFromTop.setSelected(true);
		else
			searchForward.setSelected(true);

		// disable column/row options ico searchSelection
		if (searchSelection.isSelected())
			enableRowColumnSearch(false);
		if(XSearchAndReplace.getBeanShellReplace())
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
		SearchFileSet fileset = XSearchAndReplace.getSearchFileSet();
		Log.log(Log.DEBUG, BeanShell.class,"+++ XSearchDialog.1104: fileset = "+fileset);

		if(fileset instanceof DirectoryListSet)
		{
			filter.setText(((DirectoryListSet)fileset)
				.getFileFilter());
			directory.setText(((DirectoryListSet)fileset)
				.getDirectory());
			searchSubDirectories.setSelected(((DirectoryListSet)fileset)
				.isRecursive());
			searchDirectory.setSelected(true);
		}
		else
		{
			String path = view.getBuffer().getDirectory();
			//String path = MiscUtilities.getParentOfPath(
			//	view.getBuffer().getPath());

			if(path.endsWith("/") || path.endsWith(File.separator))
				path = path.substring(0,path.length() - 1);

			directory.setText(path);

			if(fileset instanceof AllBufferSet)
			{
				filter.setText(((AllBufferSet)fileset)
					.getFileFilter());
				searchAllBuffers.setSelected(true);
			}
			else
			{
				filter.setText("*" + MiscUtilities
					.getFileExtension(view.getBuffer()
					.getName()));
				if (!searchSelection.isSelected())
					searchCurrentBuffer.isSelected();
			}

			searchSubDirectories.setSelected(true);
		}
  }
  //}}}
	
	//{{{ updateEnabled() method
	private void updateEnabled()
	{
		wrap.setEnabled(!hyperSearch.isSelected()
			&& !searchSelection.isSelected());

		boolean reverseEnabled = !hyperSearch.isSelected() && !rowRadioBtn.isSelected()
			&& searchCurrentBuffer.isSelected();
		boolean regexpSelected = regexp.isSelected();
		searchBack.setEnabled(reverseEnabled && !regexpSelected 
		&& wordPartDefaultRadioBtn.isSelected());
		// word part search (and tentativ) not allowed in combination with regexp
		wordPartPrefixRadioBtn.setEnabled(!regexpSelected);
		wordPartSuffixRadioBtn.setEnabled(!regexpSelected);
		wordPartWholeRadioBtn.setEnabled(!regexpSelected);
		tentativRadioBtn.setEnabled(!regexpSelected);
		if (regexpSelected) {
			wordPartDefaultRadioBtn.setSelected(true);
			tentativRadioBtn.setSelected(false);
		}
		
		searchForward.setEnabled(reverseEnabled);
		searchFromTop.setEnabled(reverseEnabled);
		if(!reverseEnabled || (!searchBack.isEnabled() && searchBack.isSelected()))
		{
			searchFromTop.setSelected(true);
			XSearchAndReplace.setSearchFromTop(true);
		}
		filter.setEnabled(searchAllBuffers.isSelected()
			|| searchDirectory.isSelected());

		boolean directoryEnabled = searchDirectory.isSelected();

		directory.setEnabled(directoryEnabled);
		choose.setEnabled(directoryEnabled);
		searchSubDirectories.setEnabled(directoryEnabled);

		findBtn.setEnabled(!searchSelection.isSelected()
			|| hyperSearch.isSelected());
		replaceAndFindBtn.setEnabled(!hyperSearch.isSelected()
			&& !searchSelection.isSelected());
		if (hyperSearch.isSelected()) {
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
		} else {
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
			
	} //}}}

	//{{{ loadSettingsFromHistory() method
	private void loadSettingsFromHistory()
	{
		if (searchSettingsHistoryRadioBtn.isSelected() && find.getText().length() > 0) {
			SearchSettings searchHist = settingsHistory.getItem(find.getText());
			Log.log(Log.DEBUG, BeanShell.class,"+++ XSearchDialog.1210: find.getText() = "+find.getText());
			if (searchHist != null) {
				Log.log(Log.DEBUG, BeanShell.class,"+++ XSearchDialog.1212: searchHist = "+searchHist);
				searchHist.update();
				setupSearchSettings();
				showHideOptions(searchCurrentBuffer);
				if (!searchCurrentBuffer.isSelected())
					hyperSearch.setSelected(true);
				enableRowColumnSearch(!searchSelection.isSelected());
				updateEnabled();
				revalidatePanels();
			}
		}
	} //}}}

	//{{{ save() method
	/**
	 * @param cancel If true, we don't bother the user with warning messages
	 */
	private boolean save(boolean cancel)
	{
		String filter = this.filter.getText();
		this.filter.addCurrentToHistory();
		if(filter.length() == 0)
			filter = "*";

		SearchFileSet fileset = XSearchAndReplace.getSearchFileSet();

		if(searchSelection.isSelected())
			fileset = new CurrentBufferSet();
		else if(searchCurrentBuffer.isSelected())
		{
			fileset = new CurrentBufferSet();

			jEdit.setBooleanProperty("search.hypersearch.toggle",
				hyperSearch.isSelected());
		}
		else if(searchAllBuffers.isSelected())
			fileset = new AllBufferSet(filter);
		else if(searchDirectory.isSelected())
		{
			String directory = this.directory.getText();
			this.directory.addCurrentToHistory();

			if((VFSManager.getVFSForPath(directory).getCapabilities()
				& VFS.LOW_LATENCY_CAP) == 0)
			{
				if(cancel)
					return false;

				int retVal = GUIUtilities.confirm(
					XSearchDialog.this,"remote-dir-search",
					null,JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if(retVal != JOptionPane.YES_OPTION)
					return false;
			}

			boolean recurse = searchSubDirectories.isSelected();

			if(fileset instanceof DirectoryListSet)
			{
				DirectoryListSet dset = (DirectoryListSet)fileset;
				if(!dset.getDirectory().equals(directory)
					|| !dset.getFileFilter().equals(filter)
					|| !dset.isRecursive() == recurse)
					fileset = new DirectoryListSet(directory,filter,recurse);
			}
			else
				fileset = new DirectoryListSet(directory,filter,recurse);
		}
		else
		{
			// can't happen
			fileset = null;
		}

		jEdit.setBooleanProperty("search.keepDialog.toggle",
			keepDialog.isSelected());
		jEdit.setBooleanProperty("search.settingsHistory.toggle",
			searchSettingsHistoryRadioBtn.isSelected());

		boolean ok = true;

		XSearchAndReplace.setSearchFileSet(fileset);

		if(find.getText().length() != 0)
		{
			find.addCurrentToHistory();
			SearchSettings currSs = new SearchSettings();
			currSs.load();
			settingsHistory.addItem(find.getText(), currSs);
			if (XSearchAndReplace.getSearchString() != null) {
				if (!XSearchAndReplace.getSearchString().equals(find.getText())) {
					// search string has changed ==> reset refind
					XSearchAndReplace.resetIgnoreFromTop();
				}
			}
			// because of word part search, we have to set search string even if equal
			XSearchAndReplace.setSearchString(find.getText());
			replace.addCurrentToHistory();
			XSearchAndReplace.setReplaceString(replace.getText());
		}
		else
			ok = false;

			return ok;
	} //}}}

	//{{{ evalExtendedOptions() method
	/**
	 * checks extended options and assigns its values to XSearchAndReplace 
	 */
	private boolean evalExtendedOptions() {
//		boolean ok = true;
		boolean ok = evalIntegerOptions();
		if (ok) {
		/*******************************************************************
		 * column handling
		 *******************************************************************/
			if (!columnRadioBtn.isSelected()) {
//				ok = evalColumnOptions();
//			} else {
				XSearchAndReplace.resetColumnSearch();
			}
		/*******************************************************************
		 * row handling
		 *******************************************************************/
			if (!rowRadioBtn.isSelected())
				XSearchAndReplace.resetRowSearch();
		}
		if (ok) {
		/*******************************************************************
		 * comment handling
		 *******************************************************************/
			if (searchCommentDefaultRadioBtn.isSelected())
				XSearchAndReplace.setCommentOption(SEARCH_IN_OUT_NONE);
			else if (searchCommentInsideRadioBtn.isSelected())
				XSearchAndReplace.setCommentOption(SEARCH_IN_OUT_INSIDE);
			else XSearchAndReplace.setCommentOption(SEARCH_IN_OUT_OUTSIDE);
		/*******************************************************************
		 * folding handling
		 *******************************************************************/
			if (searchFoldDefaultRadioBtn.isSelected())
				XSearchAndReplace.setFoldOption(SEARCH_IN_OUT_NONE);
			else if (searchFoldOutsideRadioBtn.isSelected())
				XSearchAndReplace.setFoldOption(SEARCH_IN_OUT_OUTSIDE);
			else XSearchAndReplace.setFoldOption(SEARCH_IN_OUT_INSIDE);
		/*******************************************************************
		 * word part handling
		 *******************************************************************/
			if (wordPartDefaultRadioBtn.isSelected())
				XSearchAndReplace.setWordPartOption(SEARCH_PART_NONE);
			else if (wordPartWholeRadioBtn.isSelected())
				XSearchAndReplace.setWordPartOption(SEARCH_PART_WHOLE_WORD);
			else if (wordPartPrefixRadioBtn.isSelected())
				XSearchAndReplace.setWordPartOption(SEARCH_PART_PREFIX);
			else XSearchAndReplace.setWordPartOption(SEARCH_PART_SUFFIX);
		/*******************************************************************
		 * hyper range handling
		 *******************************************************************/
			XSearchAndReplace.setTentativOption(tentativRadioBtn.isSelected());
		/*******************************************************************
		 * tentativ handling
		 *******************************************************************/
			XSearchAndReplace.setTentativOption(tentativRadioBtn.isSelected());
		}
		return ok;
} //}}}

	//{{{ enableRowColumnSearch() methods
	private void enableRowColumnSearch(boolean setEnabled) {
		columnRadioBtn.setEnabled(setEnabled);
		rowRadioBtn.setEnabled(setEnabled);
		if (!setEnabled) {
			columnRadioBtn.setSelected(setEnabled);
			rowRadioBtn.setSelected(setEnabled);
		}
		enableColumnOptions(setEnabled);
		enableRowOptions(setEnabled);
	}
	// enableColumnOptions() method
	private void enableColumnOptions(boolean setEnabled) {
		columnExpandTabsRadioBtn.setEnabled(setEnabled); 
		columnRightColumnField.setEnabled(setEnabled);
		rightColumnLabel.setEnabled(setEnabled);
		columnLeftColumnField.setEnabled(setEnabled);
		leftColumnLabel.setEnabled(setEnabled);
	}
	// enableRowOptions() method
	private void enableRowOptions(boolean setEnabled) {
		rowRightRowField.setEnabled(setEnabled);
		rightRowLabel.setEnabled(setEnabled);
		rowLeftRowField.setEnabled(setEnabled);
		leftRowLabel.setEnabled(setEnabled);
	}//}}}
	
	//{{{ evalIntegerOptions() method
	/* evaluates:
	 * - column options
	 * - row options
	 * - hyper range options
	 */
	private boolean evalIntegerOptions() {
		boolean extendTabs = columnExpandTabsRadioBtn.isSelected();
		int startCol;
		int endCol;
		int startRow;
		int endRow;
		JTextField errorField=null;
		JLabel     errorLabel=null;
		try {
			/*******************************************************************
			 * eval column options
			 *******************************************************************/
			if (columnRadioBtn.isSelected()) {
				// eval startCol
				errorField = columnLeftColumnField;
				errorLabel = leftColumnLabel;
				if (columnLeftColumnField.getText().length() == 0) {
					throw new NumberFormatException();
				} else {
					startCol = Integer.parseInt(errorField.getText());
					if (startCol < 1) throw new NumberFormatException();
				}
				// eval endCol
				if (columnRightColumnField.getText().length() == 0) {
					if (find.getText().length() > 0) endCol = startCol + find.getText().length() - 1;
					else endCol = startCol;
					columnRightColumnField.setText(Integer.toString(endCol));
				}
				else {
					errorField = columnRightColumnField;
					errorLabel = rightColumnLabel;
					endCol = Integer.parseInt(errorField.getText());
					if (!regexp.isSelected() && endCol < startCol + find.getText().length()-1) 
						throw new NumberFormatException();
					if (endCol < startCol) throw new NumberFormatException();
				}
				XSearchAndReplace.setColumnSearchOptions(extendTabs, startCol, endCol);
			}
			/*******************************************************************
			 * eval row options
			 *******************************************************************/
			if (rowRadioBtn.isSelected()) {
				// eval startRow
				errorField = rowLeftRowField;
				errorLabel = leftRowLabel;
				if (rowLeftRowField.getText().length() == 0) {
					throw new NumberFormatException();
				} else {
					startRow = Integer.parseInt(errorField.getText());
					if (startRow < 1) throw new NumberFormatException();
					if (startRow > view.getBuffer().getLineCount()) throw new NumberFormatException();
				}
				// eval endRow
				if (rowRightRowField.getText().length() == 0) {
					endRow = view.getBuffer().getLineCount();
					rowRightRowField.setText(Integer.toString(endRow));
				}
				else {
					errorField = rowRightRowField;
					errorLabel = rightRowLabel;
					endRow = Integer.parseInt(errorField.getText());
					// dont check endrow overflow ==> search till end of buffer
					//if (endRow > view.getBuffer().getLineCount()) throw new NumberFormatException();
					//if (!regexp.isSelected() && endRow < startRow + find.getText().length()-1) 
				  //		throw new NumberFormatException();
					if (endRow < startRow) throw new NumberFormatException();
				}
				XSearchAndReplace.setRowSearchOptions(startRow-1, endRow-1);
			}
			/*******************************************************************
			 * eval hyper range options
			 *******************************************************************/
			if (hyperSearch.isSelected()) {
				errorField = hyperRangeUpTextField;
				errorLabel = hyperRangeLabelUp;
				int hyUp, hyDown;
				if (errorField.getText().length() == 0) {
					hyUp = -1;
				} else {
					hyUp = Integer.parseInt(errorField.getText());
					if (hyUp < 0) throw new NumberFormatException();
				}
				errorField = hyperRangeDownTextField;
				errorLabel = hyperRangeLabelDown;
				if (errorField.getText().length() == 0) {
					hyDown = -1;
				} else {
					hyDown = Integer.parseInt(errorField.getText());
					if (hyDown < 0) throw new NumberFormatException();
				}
				XSearchAndReplace.setHyperRange(hyUp, hyDown);
			}
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null,
			"Wrong input :"+errorField.getText(),
			"Field: "+errorLabel.getText(),
			JOptionPane.ERROR_MESSAGE);
			errorField.requestFocus();
			return false;
		}
		// if (XSearchAndReplace.debug) Log.log(Log.DEBUG, BeanShell.class,"extendTabs = "+
		//	extendTabs+", startCol = "+startCol+", endCol = "+endCol);
		return true;
	} //}}}

	//{{{ closeOrKeepDialog() method
	private void closeOrKeepDialog()
	{
		if(keepDialog.isSelected())
		{
			// Windows bug workaround in case a YES/NO confirm
			// was shown

			// ... but if HyperSearch results window is floating,
			// the workaround causes problems!
			if(!hyperSearch.isSelected())
			{
				toFront();
				requestFocus();
				find.requestFocus();
			}
			return;
		}
		else
		{
			GUIUtilities.saveGeometry(this,"search");
			dispose();
		}
	} //}}}

	//{{{ showHideOptions() method
	private void showHideOptions(Object source)	{
		if(source == showSettings) {
			jEdit.setBooleanProperty("search.show-settings.toggle",showSettings.isSelected());
			currentSelectedSettingsOptions.setLength(0); // init text
			if (showSettings.isSelected()) {
				// display "settings" and path
				centerPanel.add(BorderLayout.CENTER,searchSettingsPanel);
				if (searchDirectory.isSelected() || searchAllBuffers.isSelected())
					southPanel.add(BorderLayout.SOUTH,multiFilePanel);
			} else {
				// remove "settings" and path
				centerPanel.remove(searchSettingsPanel);
				southPanel.remove(multiFilePanel);
			}
		}
		else if(source == showReplace) {
			jEdit.setBooleanProperty("search.show-replace.toggle",showReplace.isSelected());
			if (showReplace.isSelected()) {
				globalFieldPanel.add(fieldPanelReplaceLabel);
				globalFieldPanel.add(replaceModeBox);
				globalFieldPanel.add(fieldPanelVerticalStrut);
				globalFieldPanel.add(replace);
				if (resetSettingsButtonPresent)
					grid.remove(resetSettingsButton);  // remove first to keep sorting
				grid.remove(closeBtn);  // remove first to keep sorting
				grid.add(replaceAndFindBtn);
				grid.add(replaceAllBtn);
				if (jEdit.getBooleanProperty("xsearch.resetButton", true)) {
					grid.add(resetSettingsButton);
					resetSettingsButtonPresent = true;
				} else 
					resetSettingsButtonPresent = false;
				grid.add(closeBtn);

			} else {
				globalFieldPanel.remove(fieldPanelReplaceLabel);
				globalFieldPanel.remove(replaceModeBox);
				globalFieldPanel.remove(fieldPanelVerticalStrut);
				globalFieldPanel.remove(replace);
				grid.remove(replaceAndFindBtn);
				grid.remove(replaceAllBtn);
			}
		}
		else if(source == showExtendedOptions) {
			jEdit.setBooleanProperty("search.show-extended.toggle",showExtendedOptions.isSelected());
			if (showExtendedOptions.isSelected()) {
				southPanel.add(BorderLayout.CENTER,extendedOptionsPanel);
			} else {
				southPanel.remove(extendedOptionsPanel);
			}
		}
		else if(source == searchCurrentBuffer || source == searchAllBuffers 
			|| source == searchSelection || source == searchDirectory)
			if (searchDirectory.isSelected() || searchAllBuffers.isSelected()) {
				southPanel.add(BorderLayout.SOUTH,multiFilePanel);
				Log.log(Log.DEBUG, BeanShell.class,"+++ XSearchDialog.1604: add multiFilePanel");
			} else {
				southPanel.remove(multiFilePanel);
				Log.log(Log.DEBUG, BeanShell.class,"+++ XSearchDialog.1607: remove multiFilePanel");
			}
	} //}}}

	//{{{ revalidatePanels() method
	private void revalidatePanels() {
			centerPanel.revalidate();
			globalFieldPanel.revalidate();
			southPanel.revalidate();
			pack();
			//content.revalidate();
			//		show();
	} //}}}

	//{{{ setupStartEndRowFromSelection() method
	private void setupStartEndRowFromSelection()	{
		int[] selLines = view.getTextArea().getSelectedLines();
		if (selLines.length > 0) {
			rowLeftRowField.setText(Integer.toString(selLines[0]+1));
			rowRightRowField.setText(Integer.toString(selLines[selLines.length-1]+1));
		}
	} //}}}

	//{{{ setupCurrentSelectedOptionsLabel() method
	// setup currentSelectedOptionsLabel to display options in one line if panel part is hidden
	private void setupCurrentSelectedOptionsLabel() {
		StringBuffer currentSelectedOptions = new StringBuffer();
		if (!showSettings.isSelected() || !showExtendedOptions.isSelected()) {
			if (!showSettings.isSelected()) {
				if (ignoreCase.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.ignoreCase")+" ");
				if (regexp.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.regexp")+" ");
				if (hyperSearch.isSelected())
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.hyper")+" ");
				if (searchFromTop.isSelected() && searchFromTop.isEnabled()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.fromTop")+" ");
				if (searchBack.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.backward")+" ");
				if (wrap.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.wrap")+" ");
			}
			if (!showExtendedOptions.isSelected()) {
				if (wordPartWholeRadioBtn.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.word")+" ");
				if (wordPartPrefixRadioBtn.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.prefix")+" ");
				if (wordPartSuffixRadioBtn.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.suffix")+" ");
				if (columnRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.column")+" ");
				if (rowRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.row")+" ");
				if (searchFoldInsideRadioBtn.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.insideFold")+" ");
				if (searchFoldOutsideRadioBtn.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.outsideFold")+" ");
				if (searchCommentInsideRadioBtn.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.insideComment")+" ");
				if (searchCommentOutsideRadioBtn.isSelected()) 
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.outsideComment")+" ");
				if (tentativRadioBtn.isSelected())
					currentSelectedOptions.append(jEdit.getProperty("search.currOpt.tentativ")+" ");
			}
		}

		// debug: display components
/*		Component[] globComps = globalFieldPanel.getComponents();
		if (XSearchAndReplace.debug) for(int i = 0; i < globComps.length; i++) {
			Log.log(Log.DEBUG, BeanShell.class,"tp1212: globComps["+i+"] = "+globComps[i]);
		} */
		if (currentSelectedOptions.length() > 0) {
			currentSelectedOptionsLabel.setText(
				jEdit.getProperty("search.currOpt.label")+" "+
				currentSelectedOptions.toString());
			// check if there are still the "replace" components
			if (globalFieldPanel.getComponentCount() > optionsLabelIndex) {
				if (globalFieldPanel.getComponent(optionsLabelIndex) != currentSelectedOptionsLabel) {
					if (XSearchAndReplace.debug) Log.log(Log.DEBUG, BeanShell.class,"tp1217: add Label at  "+optionsLabelIndex);
					globalFieldPanel.add(currentSelectedOptionsLabel,optionsLabelIndex);
				} else 
					if (XSearchAndReplace.debug) Log.log(Log.DEBUG, BeanShell.class,"tp1219: Label already exists at "+optionsLabelIndex);
			} else {
				globalFieldPanel.add(currentSelectedOptionsLabel);
			}
		} else 
			if (globalFieldPanel.getComponentCount() > optionsLabelIndex) {
				if (globalFieldPanel.getComponent(optionsLabelIndex) == currentSelectedOptionsLabel) {
					if (XSearchAndReplace.debug) Log.log(Log.DEBUG, BeanShell.class,"tp1224: remove Label at  "+optionsLabelIndex);
					globalFieldPanel.remove(optionsLabelIndex);
				} else 
					if (XSearchAndReplace.debug) Log.log(Log.DEBUG, BeanShell.class,"tp1227: Label does not exist at "+optionsLabelIndex);
			}
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ MyJRadioButton class

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
	} //}}}

	//{{{ ReplaceActionHandler class
	class ReplaceActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			replace.setModel(beanShellReplace.isSelected()
				? "replace.script"
				: "replace");
			XSearchAndReplace.setBeanShellReplace(
				beanShellReplace.isSelected());
		}
	} //}}}

	//{{{ SelectivShowActionHandler class
	class SelectivShowActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			showHideOptions(evt.getSource());
			setupCurrentSelectedOptionsLabel();
			revalidatePanels();
		}
	} //}}}

	//{{{ ExtendedOptionsActionHandler class
	class ExtendedOptionsActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			XSearchAndReplace.resetIgnoreFromTop();
			Object source = evt.getSource();
			if (source == columnRadioBtn) {
				enableColumnOptions(columnRadioBtn.isSelected());
			}
			else if (source == rowRadioBtn) {
				enableRowOptions(rowRadioBtn.isSelected());
					updateEnabled();
			}
			else if (source == wordPartPrefixRadioBtn || source == wordPartSuffixRadioBtn 
				|| source == wordPartWholeRadioBtn || source == tentativRadioBtn) {
					if (((JRadioButton)source).isSelected()) {
						regexp.setSelected(false);
//						regexp.setEnabled(false);
						searchBack.setEnabled(false);
						regexp.setEnabled(true);
					}
					updateEnabled();
			}
			else if (source == searchSettingsHistoryRadioBtn) {
				loadSettingsFromHistory();
			}
/*
			else if (source == searchFoldInsideRadioBtn || source == searchFoldOutsideRadioBtn) {
				// don't allow hypersearch ico fold searching
				if (((JRadioButton)source).isSelected()) {
					hyperSearch.setSelected(false);
					hyperSearch.setEnabled(false);
				} else {
					hyperSearch.setEnabled(true);
				}
			}
			*/
			/*
			else if (source == searchFoldInsideRadioBtn || source == searchFoldOutsideRadioBtn) {
				if (source == searchFoldActualRadioBtn) {
					// the already selected button is selected once more ==> select default button
					searchFoldDefaultRadioBtn.setSelected(true);
					searchFoldActualRadioBtn = searchFoldDefaultRadioBtn;
				} else {
					((JRadioButton)source).setSelected(true);
					searchFoldActualRadioBtn = (JRadioButton)source;
				}
			} else if (source == searchCommentInsideRadioBtn || source == searchCommentOutsideRadioBtn) {
				if (source == searchCommentActualRadioBtn) {
					// the already selected button is selected once more ==> select default button
					searchCommentDefaultRadioBtn.setSelected(true);
					searchCommentActualRadioBtn = searchCommentDefaultRadioBtn;
				} else {
					((JRadioButton)source).setSelected(true);
					searchCommentActualRadioBtn = (JRadioButton)source;
				}
			}
			*/
			pack();
			show();
		}
	} //}}}

	//{{{ SettingsActionHandler class
	class SettingsActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			XSearchAndReplace.resetIgnoreFromTop(); // rwchg: when settings change, no refind
			Object source = evt.getSource();

			if(source == ignoreCase)
				XSearchAndReplace.setIgnoreCase(ignoreCase.isSelected());
			else if(source == regexp)
				XSearchAndReplace.setRegexp(regexp.isSelected());
			else if(source == searchBack || source == searchForward || source == searchFromTop) {
				XSearchAndReplace.setReverseSearch(searchBack.isSelected());
				XSearchAndReplace.setSearchFromTop(searchFromTop.isSelected());
			}
			else if(source == wrap)
				XSearchAndReplace.setAutoWrapAround(wrap.isSelected());
			else if(source == searchCurrentBuffer) {
				hyperSearch.setSelected(false);
				enableRowColumnSearch(true);
			}
			else if(source == searchSelection
				|| source == searchAllBuffers
				|| source == searchDirectory)
				hyperSearch.setSelected(true);
				
			enableRowColumnSearch(source != searchSelection);
			updateEnabled();
		}
	} //}}}

	//{{{ MultiFileActionHandler class
	class MultiFileActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == choose)
			{
				String[] dirs = GUIUtilities.showVFSFileDialog(
					view,directory.getText(),
					VFSBrowser.CHOOSE_DIRECTORY_DIALOG,
					false);
				if(dirs != null)
					directory.setText(dirs[0]);
			}
			else // source is directory or filter field
			{
				// just as if Enter was pressed in another
				// text field
				ok();
			}
		}
	} //}}}

	//{{{ ButtonActionHandler class
	class ButtonActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();

			if(source == closeBtn)
				cancel();
			else if (source == find && find.getReceivedEvent() == XSearchHistoryTextField.RECEIVED_EVENT_SELECT) {
				// another item in historytextfield selected
				// set history settings if available
				Log.log(Log.DEBUG, BeanShell.class,"+++ XSearchDialog.1838");
				loadSettingsFromHistory();
			}
			else if (evalExtendedOptions()) {
				if(source == findBtn || source == find
				|| source == replace || source == findAllBtn)
				{
					if (source == findAllBtn) {
						XSearchAndReplace.setFindAll(true);
						// select "fromTop", repeat necessary commands as done in SettingsActionListener
						// note: fireActionPerformed not possible, as we are already in an awt thread
						XSearchAndReplace.resetIgnoreFromTop();
						XSearchAndReplace.setReverseSearch(false);
						XSearchAndReplace.setSearchFromTop(true);
						searchFromTop.setSelected(true);
					}
					ok();
				}
				else if(source == replaceAndFindBtn)
				{
					if (!keepDialogChanged) keepDialog.setSelected(true);
					save(false);
					if(XSearchAndReplace.replace(view))
						ok();
					else
						getToolkit().beep();
				}
				else if(source == replaceAllBtn)
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					save(false);

					if(searchSelection.isSelected())
					{
						if(XSearchAndReplace.replace(view))
							closeOrKeepDialog();
						else
							getToolkit().beep();
					}
					else
					{
						if(XSearchAndReplace.replaceAll(view, hyperSearch.isSelected() 
							&& jEdit.getBooleanProperty("xsearch.hyperReplace", true)))
							closeOrKeepDialog();
						else
							getToolkit().beep();
					}

					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}
	} //}}}

	//}}}
}
