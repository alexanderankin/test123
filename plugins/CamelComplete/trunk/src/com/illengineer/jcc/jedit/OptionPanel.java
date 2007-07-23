package com.illengineer.jcc.jedit;

import com.illengineer.jcc.*;

import java.io.*;
import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.illengineer.com.jgoodies.forms.factories.*;
import com.illengineer.com.jgoodies.forms.layout.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;

public class OptionPanel extends AbstractOptionPane
      implements ActionListener
{
	// {{{ Member variables
	private int selectedProvider, selectedTokenizer;
	private DefaultListModel providerModel, tokenizerModel;
	
	private HashMap<String, List<OptionGroup>> optionGroupMap;
	private Map<String,List<OptionPanel.OptionGroup>> enginesOptionsMap;
	private String currentEngineName = "default";
	
	private MessageDialog msgDialog;
	// }}}
	
	// {{{ Constructors
	public OptionPanel() {
	    super(CamelCompletePlugin.NAME);
	}
	// }}}
	
	// {{{ Lifecycle Methods
	
	protected void _init() {
	    initComponents();
	    
	    providerModel = new DefaultListModel();
	    tokenizerModel = new DefaultListModel();
	    providerList.setModel(providerModel);
	    tokenizerList.setModel(tokenizerModel);
	    optionGroupCombo.setModel(new DefaultComboBoxModel());
	    
	    
	    resetComponents();
	    selectedProvider = selectedTokenizer = -1;  // no currently selected items
	    
	    JButton[] buttons = {saveProviderButton, deleteProviderButton, editProviderButton, chooseButton,
	    			 addTokenizerButton, removeTokenizerButton, processButton, processAllButton,
				 loadOptionsButton, saveOptionsButton, appendOptionsButton, 
				 		deleteOptionsButton, newOptionsButton,
				 loadEngineButton, saveEngineButton, deleteEngineButton, newEngineButton};
	    for (JButton b : buttons)
		b.addActionListener(this);
	    
	    loadOptionGroups();
	    loadEngines();
	    cacheCheck.setSelected(((Boolean)CamelCompletePlugin.getOption("cache")).booleanValue());
	    updateCheck.setSelected(((Boolean)CamelCompletePlugin.getOption("update")).booleanValue());
	    addComponent(mainPanel);
	}
	
	protected void _save() {
	    saveCurrentEngine();
	    saveOptionGroups();
	    CamelCompletePlugin.setOption("cache", Boolean.valueOf(cacheCheck.isSelected()));
	    CamelCompletePlugin.setOption("update", Boolean.valueOf(updateCheck.isSelected()));
	}
	
	// }}}
	
	// {{{ Event Handlers
	
	public void actionPerformed(ActionEvent ev) {
	    JComponent source = (JComponent)ev.getSource();
	    // {{{ Providers Buttons
	    if (source == saveProviderButton) {
		// we need at least one tokenizer and a file name
		String fileName = filenameField.getText();
		String extra = extraField.getText();
		if (tokenizerModel.size() > 0 && (fileName.length() > 0 || extra.length() > 0)) {
		    OptionGroup og = new OptionGroup();
		    if (ctagsButton.isSelected())
			og.provider = "ctags";
		    else if (jarButton.isSelected())
			og.provider = "jar";
		    else if (codeButton.isSelected())
			og.provider = "code";
		    og.fileName = fileName;
		    og.extra = extra;
		    og.tokenizers = new ArrayList<String[]>(tokenizerModel.size()+1);
		    for (Enumeration e = tokenizerModel.elements(); e.hasMoreElements(); )
			og.tokenizers.add(((TokenizerHolder)e.nextElement()).tokenizer);
		    og.minparts = ((Integer)minpartsSpinner.getValue()).intValue();
		    og.maxparts = ((Integer)maxpartsSpinner.getValue()).intValue();
		    og.ignoreCase = ignoreCaseCheck.isSelected();
		    og.filterRegex = filterField.getText();
		    if (og.filterRegex.length() == 0)
			og.filterRegex = null;
			
		    if (selectedProvider != -1) {  // We're in editing mode
			providerModel.set(selectedProvider, og);
			selectedProvider = -1;
		    } else {
			providerModel.addElement(og);
		    }
		    tokenizerModel.clear();
		    resetComponents(true, true);
		}
		
	    } else if (source == deleteProviderButton) {
		selectedProvider = providerList.getSelectedIndex();
		if (selectedProvider != -1) {
		    providerModel.removeElementAt(selectedProvider);
		    selectedProvider = -1;
		}
		resetComponents(true, true);
	    } else if (source == editProviderButton) {
		selectedProvider = providerList.getSelectedIndex();
		if (selectedProvider != -1) {
		    fillFieldsFromOptions((OptionGroup)providerModel.get(selectedProvider));
		}
	    } else if (source == chooseButton) {
		String[] paths = GUIUtilities.showVFSFileDialog(jEdit.getFirstView(), null, 
						    VFSBrowser.OPEN_DIALOG, false);
		if (paths != null)
		    filenameField.setText(paths[0]);
	    } // }}} 
	    // {{{ Tokenizer Buttons
	    else if (source == addTokenizerButton) {
		if (camelCaseButton.isSelected()) {
		    String[] a = new String[1];
		    a[0] = "camelcase";
		    tokenizerModel.addElement(new TokenizerHolder(a));
		    resetComponents(false, true);
		} else if (regexButton.isSelected()) {
		    String s = regexField.getText();
		    if (s.length() != 0) {
			String[] a = new String[3];
			a[0] = "regex"; a[1] = s; a[2] = (regexIgnoreCaseCheck.isSelected() ? "y" : "n");
			tokenizerModel.addElement(new TokenizerHolder(a));
			resetComponents(false, true);
		    }
		}
	    } else if (source == removeTokenizerButton) {
		selectedTokenizer = tokenizerList.getSelectedIndex();
		if (selectedTokenizer != -1)
		    tokenizerModel.removeElementAt(selectedTokenizer);
		resetComponents(false, true);
	    } // }}} 
	    // {{{ Option Groups buttons
	    else if (source == newOptionsButton) {
		optionGroupCombo.setSelectedItem("");
		tokenizerModel.clear();
		providerModel.clear();
		resetComponents(true, true);
	    } else if (source == loadOptionsButton) {
		String key = (String)optionGroupCombo.getSelectedItem();
		if (key != null && key.length() > 0) {
		    loadOptions(optionGroupMap.get(key));
		}
	    } else if (source == appendOptionsButton) {
		String key = (String)optionGroupCombo.getSelectedItem();
		if (key != null && key.length() > 0) {
		    loadOptions(optionGroupMap.get(key), true);
		}
	    } else if (source == saveOptionsButton) {
		String key = (String)optionGroupCombo.getSelectedItem();
		if (key != null && key.length() > 0) {
		    if (!optionGroupMap.containsKey(key))
			optionGroupCombo.addItem(key);
		    optionGroupMap.put(key, saveOptions());
		}
	    } else if (source == deleteOptionsButton) {
		String key = (String)optionGroupCombo.getSelectedItem();
		if (key != null && key.length() > 0) {
		    if (optionGroupMap.containsKey(key)) {
			optionGroupMap.remove(key);
			optionGroupCombo.removeItem(key);
		    }
		}
	    }
	    // }}}
	    // {{{ Engines Buttons
	    else if (source == newEngineButton) {
		currentEngineName = null;
		enginesCombo.setSelectedItem("");
		tokenizerModel.clear();
		providerModel.clear();
		resetComponents(true, true);
	    } else if (source == loadEngineButton) {
		String engineName = (String)enginesCombo.getSelectedItem();
		if (engineName != null && engineName.length() > 0) {
		    if (enginesOptionsMap.containsKey(engineName)) {
			loadOptions(enginesOptionsMap.get(engineName));
			currentEngineName = engineName;
		    }
		}
	    } else if (source == saveEngineButton) {
		String engineName = (String)enginesCombo.getSelectedItem();
		if (engineName != null && engineName.length() > 0) {
		    currentEngineName = engineName;
		    if (!enginesOptionsMap.containsKey(engineName))
			enginesCombo.addItem(engineName);
		    saveCurrentEngine();
		}
	    } else if (source == deleteEngineButton) {
		String engineName = (String)enginesCombo.getSelectedItem();
		if (engineName != null && engineName.length() > 0) {
		    if (enginesOptionsMap.size() > 1 && enginesOptionsMap.containsKey(engineName)) {
			CamelCompletePlugin.deleteEngine(engineName);
			enginesCombo.removeItem(engineName);
			currentEngineName = null;
			
			// Standard clear the fields stuff.
			enginesCombo.setSelectedItem("");
			tokenizerModel.clear();
			providerModel.clear();
			resetComponents(true, true);
		    }
		}
	    } // }}}
	    // {{{ Process Buttons
	    else if (source == processButton || source == processAllButton) {
		saveCurrentEngine();
		showMsg("Updating identifier lists...");
		try {
		    if (source == processButton)
			CamelCompletePlugin.processConfiguration(currentEngineName);
		    else
			CamelCompletePlugin.processConfiguration();
		} catch (Exception ex) {
		    showMsg("Error: " + ex.getMessage());
		    return;
		}
		showMsg("");
	    } // }}}
	}
	
	// }}}
	
	// {{{ Utility Methods
	
	// 	{{{ resetComponents()
	private void resetComponents() {
	    resetComponents(true, true);
	}
	
	private void resetComponents(boolean clearProviderFields, boolean clearTokenizerFields) {
	    if (clearProviderFields) {
		ctagsButton.setSelected(true);
		filenameField.setText("");
		filterField.setText("");
		extraField.setText("");
		ignoreCaseCheck.setSelected(false);
		((SpinnerNumberModel)minpartsSpinner.getModel()).setValue(new Integer(2));
		((SpinnerNumberModel)maxpartsSpinner.getModel()).setValue(new Integer(8));
	    }

	    if (clearTokenizerFields) {
		camelCaseButton.setSelected(true);
		regexField.setText("");
		regexIgnoreCaseCheck.setSelected(false);
	    }
	}
	// }}}
	
	//	{{{ fillFieldsFromOptions()
	private void fillFieldsFromOptions(OptionGroup og) {
	    resetComponents();
	    tokenizerModel.clear();
	    
	    if (og.provider.equals("ctags"))
		ctagsButton.setSelected(true);
	    else if (og.provider.equals("jar"))
		jarButton.setSelected(true);
	    else if (og.provider.equals("code"))
		codeButton.setSelected(true);
	    if (og.fileName != null)
		filenameField.setText(og.fileName);
	    if (og.extra != null)
		extraField.setText(og.extra);
	    for (String [] t : og.tokenizers)
		tokenizerModel.addElement(new TokenizerHolder(t));
	    ((SpinnerNumberModel)minpartsSpinner.getModel()).setValue(new Integer(og.minparts));
	    ((SpinnerNumberModel)maxpartsSpinner.getModel()).setValue(new Integer(og.maxparts));
	    ignoreCaseCheck.setSelected(og.ignoreCase);
	    filterField.setText(og.filterRegex);
	}
	//	}}}

	// 	{{{ Message methods
	private void showMsgDlg(String msg) {
	    if (msgDialog == null)
		msgDialog = new MessageDialog();
	    msgDialog.showDlg("CamelComplete", msg);
	}
	
	private void hideMsgDlg() {
	    if (msgDialog != null)
		msgDialog.closeDlg();
	}
	private void showMsg(String msg) {
	    messageLabel.setText(msg);
	    MessageDialog._repaintImmediately(messageLabel);
	}
	//	}}}

	// 	{{{ Options routines
	private List<OptionGroup> saveOptions() {
	    ArrayList<OptionGroup> a = new ArrayList<OptionGroup>(providerModel.size()+1);
	    for (Enumeration e = providerModel.elements(); e.hasMoreElements(); )
		a.add((OptionGroup)e.nextElement());
	    return a;
	}
	
	private void loadOptions(List<OptionGroup> options) {
	    loadOptions(options, false);
	}
	
	private void loadOptions(List<OptionGroup> options, boolean append) {
	    if (options != null) {
		tokenizerModel.clear();
		if (!append)
		    providerModel.clear();
		resetComponents(true, true);
		for (OptionGroup og : options)
		    providerModel.addElement(og);
	    }
	}
	
	private void saveOptionGroups() {
	    CamelCompletePlugin.setOption("groups", optionGroupMap);
	}
	
	private void loadOptionGroups() {
	    optionGroupMap = (HashMap<String, List<OptionGroup>>)CamelCompletePlugin.getOption("groups");
	    if (optionGroupMap == null)
		optionGroupMap = new HashMap<String, List<OptionGroup>>();
	    else {
		optionGroupCombo.removeAllItems();
		for (String key : optionGroupMap.keySet())
		    optionGroupCombo.addItem(key);
	    }
	}
	
	private void saveCurrentEngine() {
	    if (currentEngineName != null && currentEngineName.length() > 0)
		enginesOptionsMap.put(currentEngineName, saveOptions());
	}
	
	private void loadEngines() {
	    enginesOptionsMap = 
		(Map<String,List<OptionPanel.OptionGroup>>)CamelCompletePlugin.getOption("engines");
	    for (String engineName : enginesOptionsMap.keySet()) {
		enginesCombo.addItem(engineName);
	    }
	    enginesCombo.setSelectedIndex(0);
	    currentEngineName = (String)enginesCombo.getSelectedItem();
	    loadOptions(enginesOptionsMap.get(currentEngineName));
	}
	    
	// }}}

	// }}}
	
	// {{{ JFormDesigner initComponents()
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		mainPanel = new JPanel();
		optionPanel = new JPanel();
		panel7 = new JPanel();
		label7 = new JLabel();
		enginesCombo = new JComboBox();
		loadEngineButton = new JButton();
		saveEngineButton = new JButton();
		deleteEngineButton = new JButton();
		newEngineButton = new JButton();
		panel6 = new JPanel();
		label6 = new JLabel();
		label1 = new JLabel();
		panel5 = new JPanel();
		panel9 = new JPanel();
		optionGroupCombo = new JComboBox();
		loadOptionsButton = new JButton();
		newOptionsButton = new JButton();
		saveOptionsButton = new JButton();
		deleteOptionsButton = new JButton();
		appendOptionsButton = new JButton();
		scrollPane1 = new JScrollPane();
		providerList = new JList();
		panel2 = new JPanel();
		ctagsButton = new JRadioButton();
		jarButton = new JRadioButton();
		codeButton = new JRadioButton();
		label3 = new JLabel();
		filenameField = new JTextField();
		chooseButton = new JButton();
		panel10 = new JPanel();
		extraLabel = new JLabel();
		extraField = new JTextField();
		saveProviderButton = new JButton();
		deleteProviderButton = new JButton();
		editProviderButton = new JButton();
		panel1 = new JPanel();
		radioButton1 = new JRadioButton();
		radioButton2 = new JRadioButton();
		label2 = new JLabel();
		scrollPane2 = new JScrollPane();
		tokenizerList = new JList();
		panel3 = new JPanel();
		camelCaseButton = new JRadioButton();
		regexButton = new JRadioButton();
		regexField = new JTextField();
		regexIgnoreCaseCheck = new JCheckBox();
		addTokenizerButton = new JButton();
		removeTokenizerButton = new JButton();
		panel4 = new JPanel();
		ignoreCaseCheck = new JCheckBox();
		label5 = new JLabel();
		minpartsSpinner = new JSpinner();
		label4 = new JLabel();
		filterField = new JTextField();
		label8 = new JLabel();
		maxpartsSpinner = new JSpinner();
		panel8 = new JPanel();
		cacheCheck = new JCheckBox();
		updateCheck = new JCheckBox();
		processButton = new JButton();
		processAllButton = new JButton();
		messageLabel = new JLabel();
		CellConstraints cc = new CellConstraints();

		//======== mainPanel ========
		{
			mainPanel.setLayout(new BorderLayout(7, 7));

			//======== optionPanel ========
			{
				optionPanel.setBorder(Borders.DLU7_BORDER);
				optionPanel.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					new RowSpec[] {
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.PARAGRAPH_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormFactory.PARAGRAPH_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormFactory.PARAGRAPH_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						new RowSpec("fill:max(default;12dlu)")
					}));

				//======== panel7 ========
				{
					panel7.setLayout(new FlowLayout(FlowLayout.LEFT));

					//---- label7 ----
					label7.setText("Engines:");
					label7.setHorizontalAlignment(SwingConstants.TRAILING);
					panel7.add(label7);

					//---- enginesCombo ----
					enginesCombo.setEditable(true);
					enginesCombo.setPrototypeDisplayValue("Java Engine");
					panel7.add(enginesCombo);

					//---- loadEngineButton ----
					loadEngineButton.setText("Load");
					panel7.add(loadEngineButton);

					//---- saveEngineButton ----
					saveEngineButton.setText("Save");
					panel7.add(saveEngineButton);

					//---- deleteEngineButton ----
					deleteEngineButton.setText("Delete");
					panel7.add(deleteEngineButton);

					//---- newEngineButton ----
					newEngineButton.setText("New");
					panel7.add(newEngineButton);
				}
				optionPanel.add(panel7, cc.xywh(1, 1, 5, 1));

				//======== panel6 ========
				{
					panel6.setLayout(new FlowLayout(FlowLayout.LEFT));
				}
				optionPanel.add(panel6, cc.xy(5, 1));

				//---- label6 ----
				label6.setText("Option Sets");
				optionPanel.add(label6, cc.xy(1, 3));

				//---- label1 ----
				label1.setText("Identifier Providers");
				optionPanel.add(label1, cc.xy(3, 3));

				//======== panel5 ========
				{
					panel5.setLayout(new FormLayout(
						new ColumnSpec[] {
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.RELATED_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC
						},
						new RowSpec[] {
							new RowSpec(RowSpec.TOP, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
						}));

					//======== panel9 ========
					{
						panel9.setLayout(new FlowLayout(FlowLayout.LEFT));

						//---- optionGroupCombo ----
						optionGroupCombo.setEditable(true);
						optionGroupCombo.setPrototypeDisplayValue("Java Set");
						panel9.add(optionGroupCombo);
					}
					panel5.add(panel9, cc.xywh(1, 1, 3, 1));

					//---- loadOptionsButton ----
					loadOptionsButton.setText("Load");
					panel5.add(loadOptionsButton, cc.xy(1, 3));

					//---- newOptionsButton ----
					newOptionsButton.setText("New");
					panel5.add(newOptionsButton, cc.xy(3, 3));

					//---- saveOptionsButton ----
					saveOptionsButton.setText("Save");
					panel5.add(saveOptionsButton, cc.xy(1, 5));

					//---- deleteOptionsButton ----
					deleteOptionsButton.setText("Delete");
					panel5.add(deleteOptionsButton, cc.xy(3, 5));

					//---- appendOptionsButton ----
					appendOptionsButton.setText("Append");
					panel5.add(appendOptionsButton, cc.xy(1, 7));
				}
				optionPanel.add(panel5, cc.xy(1, 5));

				//======== scrollPane1 ========
				{

					//---- providerList ----
					providerList.setPrototypeCellValue("XXXXXXXXXXXXXXXXXXXXX");
					providerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane1.setViewportView(providerList);
				}
				optionPanel.add(scrollPane1, cc.xy(3, 5));

				//======== panel2 ========
				{
					panel2.setLayout(new FormLayout(
						new ColumnSpec[] {
							new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
						},
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.RELATED_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.RELATED_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							new RowSpec(RowSpec.TOP, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormFactory.DEFAULT_ROWSPEC
						}));

					//---- ctagsButton ----
					ctagsButton.setText("Ctags");
					panel2.add(ctagsButton, cc.xy(1, 1));

					//---- jarButton ----
					jarButton.setText("JAR");
					panel2.add(jarButton, cc.xy(3, 1));

					//---- codeButton ----
					codeButton.setText("Bsh Code");
					panel2.add(codeButton, cc.xy(5, 1));

					//---- label3 ----
					label3.setText("Filename");
					panel2.add(label3, cc.xy(1, 3));
					panel2.add(filenameField, cc.xywh(1, 5, 3, 1));

					//---- chooseButton ----
					chooseButton.setText("Choose");
					panel2.add(chooseButton, cc.xy(5, 5));

					//======== panel10 ========
					{
						panel10.setLayout(new BorderLayout(4, 0));

						//---- extraLabel ----
						extraLabel.setText("Extra");
						panel10.add(extraLabel, BorderLayout.WEST);
						panel10.add(extraField, BorderLayout.CENTER);
					}
					panel2.add(panel10, cc.xywh(1, 7, 5, 1));

					//---- saveProviderButton ----
					saveProviderButton.setText("Save");
					panel2.add(saveProviderButton, cc.xy(1, 9));

					//---- deleteProviderButton ----
					deleteProviderButton.setText("Delete");
					panel2.add(deleteProviderButton, cc.xy(3, 9));

					//---- editProviderButton ----
					editProviderButton.setText("Edit");
					panel2.add(editProviderButton, cc.xy(5, 9));
				}
				optionPanel.add(panel2, cc.xy(5, 5));

				//======== panel1 ========
				{
					panel1.setLayout(new FormLayout(
						new ColumnSpec[] {
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC
						},
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}));

					//---- radioButton1 ----
					radioButton1.setText("Ctags");
					panel1.add(radioButton1, cc.xy(1, 1));

					//---- radioButton2 ----
					radioButton2.setText("JAR");
					panel1.add(radioButton2, cc.xy(3, 1));
				}
				optionPanel.add(panel1, cc.xy(5, 5));

				//---- label2 ----
				label2.setText("Tokenizers for Provider");
				optionPanel.add(label2, cc.xy(3, 7));

				//======== scrollPane2 ========
				{

					//---- tokenizerList ----
					tokenizerList.setPrototypeCellValue("XXXXX");
					tokenizerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane2.setViewportView(tokenizerList);
				}
				optionPanel.add(scrollPane2, cc.xy(3, 9));

				//======== panel3 ========
				{
					panel3.setLayout(new FormLayout(
						new ColumnSpec[] {
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(Sizes.dluX(21))
						},
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							new RowSpec(RowSpec.TOP, Sizes.DLUY3, FormSpec.NO_GROW),
							FormFactory.DEFAULT_ROWSPEC,
							new RowSpec(RowSpec.TOP, Sizes.DLUY3, FormSpec.DEFAULT_GROW),
							FormFactory.DEFAULT_ROWSPEC
						}));

					//---- camelCaseButton ----
					camelCaseButton.setText("CamelCase");
					panel3.add(camelCaseButton, cc.xy(1, 1));

					//---- regexButton ----
					regexButton.setText("Regex");
					panel3.add(regexButton, cc.xy(1, 3));
					panel3.add(regexField, cc.xywh(3, 3, 3, 1));

					//---- regexIgnoreCaseCheck ----
					regexIgnoreCaseCheck.setText("Regex Ignore Case");
					panel3.add(regexIgnoreCaseCheck, cc.xywh(1, 5, 5, 1));

					//---- addTokenizerButton ----
					addTokenizerButton.setText("Add");
					panel3.add(addTokenizerButton, cc.xy(1, 7));

					//---- removeTokenizerButton ----
					removeTokenizerButton.setText("Remove");
					panel3.add(removeTokenizerButton, cc.xy(3, 7));
				}
				optionPanel.add(panel3, cc.xy(5, 9));

				//======== panel4 ========
				{
					panel4.setLayout(new FormLayout(
						new ColumnSpec[] {
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.RELATED_GAP_COLSPEC,
							new ColumnSpec(Sizes.dluX(54)),
							new ColumnSpec(ColumnSpec.LEFT, Sizes.DLUX7, FormSpec.NO_GROW),
							new ColumnSpec(ColumnSpec.RIGHT, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(Sizes.dluX(41))
						},
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}));

					//---- ignoreCaseCheck ----
					ignoreCaseCheck.setText("Provider Ignore Case");
					panel4.add(ignoreCaseCheck, cc.xywh(1, 1, 3, 1));

					//---- label5 ----
					label5.setText("Minimum Parts");
					panel4.add(label5, cc.xy(5, 1));

					//---- minpartsSpinner ----
					minpartsSpinner.setModel(new SpinnerNumberModel(2, 1, null, 1));
					panel4.add(minpartsSpinner, cc.xy(7, 1));

					//---- label4 ----
					label4.setText("Filter Regexp");
					panel4.add(label4, cc.xy(1, 3));
					panel4.add(filterField, cc.xy(3, 3));

					//---- label8 ----
					label8.setText("Maximum Parts");
					panel4.add(label8, cc.xy(5, 3));

					//---- maxpartsSpinner ----
					maxpartsSpinner.setModel(new SpinnerNumberModel(8, 2, null, 1));
					panel4.add(maxpartsSpinner, cc.xy(7, 3));
				}
				optionPanel.add(panel4, cc.xywh(3, 11, 3, 1));

				//======== panel8 ========
				{
					panel8.setLayout(new FlowLayout(FlowLayout.LEFT));

					//---- cacheCheck ----
					cacheCheck.setText("Cache Data");
					panel8.add(cacheCheck);

					//---- updateCheck ----
					updateCheck.setText("Update on Startup");
					panel8.add(updateCheck);

					//---- processButton ----
					processButton.setText("Update Engine");
					panel8.add(processButton);

					//---- processAllButton ----
					processAllButton.setText("Update All Engines");
					panel8.add(processAllButton);
				}
				optionPanel.add(panel8, cc.xywh(1, 13, 5, 1));
				optionPanel.add(messageLabel, cc.xywh(3, 15, 3, 1));
			}
			mainPanel.add(optionPanel, BorderLayout.CENTER);
		}

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(ctagsButton);
		buttonGroup1.add(jarButton);
		buttonGroup1.add(codeButton);

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(camelCaseButton);
		buttonGroup2.add(regexButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	// }}}

	// {{{ JFormDesigner variables
	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	JPanel mainPanel;
	JPanel optionPanel;
	JPanel panel7;
	JLabel label7;
	JComboBox enginesCombo;
	JButton loadEngineButton;
	JButton saveEngineButton;
	JButton deleteEngineButton;
	JButton newEngineButton;
	JPanel panel6;
	JLabel label6;
	JLabel label1;
	JPanel panel5;
	JPanel panel9;
	JComboBox optionGroupCombo;
	JButton loadOptionsButton;
	JButton newOptionsButton;
	JButton saveOptionsButton;
	JButton deleteOptionsButton;
	JButton appendOptionsButton;
	JScrollPane scrollPane1;
	JList providerList;
	JPanel panel2;
	JRadioButton ctagsButton;
	JRadioButton jarButton;
	JRadioButton codeButton;
	JLabel label3;
	JTextField filenameField;
	JButton chooseButton;
	JPanel panel10;
	JLabel extraLabel;
	JTextField extraField;
	JButton saveProviderButton;
	JButton deleteProviderButton;
	JButton editProviderButton;
	JPanel panel1;
	JRadioButton radioButton1;
	JRadioButton radioButton2;
	JLabel label2;
	JScrollPane scrollPane2;
	JList tokenizerList;
	JPanel panel3;
	JRadioButton camelCaseButton;
	JRadioButton regexButton;
	JTextField regexField;
	JCheckBox regexIgnoreCaseCheck;
	JButton addTokenizerButton;
	JButton removeTokenizerButton;
	JPanel panel4;
	JCheckBox ignoreCaseCheck;
	JLabel label5;
	JSpinner minpartsSpinner;
	JLabel label4;
	JTextField filterField;
	JLabel label8;
	JSpinner maxpartsSpinner;
	JPanel panel8;
	JCheckBox cacheCheck;
	JCheckBox updateCheck;
	JButton processButton;
	JButton processAllButton;
	JLabel messageLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	// }}} 

	// {{{ Inner Classes
	public static class OptionGroup implements Serializable
	{
	    String provider;  //"ctags", "jar", or "code"
	    String fileName;
	    String extra;
	    java.util.List<String[]> tokenizers;
	    // Either ["camelcase"] or ["regex", <regex>, "y"|"n" (ignore case)]
	    int minparts, maxparts;
	    boolean ignoreCase;
	    String filterRegex;
	    
	    public String toString() {
		if (provider.equals("ctags") || provider.equals("jar")) {
		    int i = fileName.lastIndexOf(File.separatorChar);
		    i++;
		    return provider + ", " + fileName.substring(i);
		} else if (provider.equals("code")) {
		    return "BeanShell";
		}
		return "";
	    }
	}
	
	private static class TokenizerHolder
	{
	    String [] tokenizer;

	    TokenizerHolder(String[] t) {
		tokenizer = t;
	    }
	    
	    public String toString() {
		if (tokenizer[0].equals("camelcase"))
		    return "CamelCase";
		else if (tokenizer[0].equals("regex"))
		    return "RegEx, " + tokenizer[1];
		else
		    return "Unknown Tokenizer";
	    }
	}
	// }}}
}

// :folding=explicit:collapseFolds=1:
