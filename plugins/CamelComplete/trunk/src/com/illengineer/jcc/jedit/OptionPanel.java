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
	private DefaultListModel impExpListModel;
	private JButton impExpButtonClicked;
	
	private HashMap<String, List<OptionGroup>> optionGroupMap;
	private Map<String,List<OptionPanel.OptionGroup>> enginesOptionsMap;
	private HashMap<String, OptionPanel.EngineOpts> eoMap;
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
	    currentViewCheck.setVisible(false);
	    
	    providerModel = new DefaultListModel();
	    tokenizerModel = new DefaultListModel();
	    impExpListModel = new DefaultListModel();
	    providerList.setModel(providerModel);
	    tokenizerList.setModel(tokenizerModel);
	    impExpList.setModel(impExpListModel);
	    optionGroupCombo.setModel(new DefaultComboBoxModel());
	    
	    
	    resetComponents();
	    selectedProvider = selectedTokenizer = -1;  // no currently selected items
	    
	    AbstractButton[] buttons 
		 = {saveProviderButton, deleteProviderButton, editProviderButton, chooseButton,
		    	copyProviderButton,
		    addTokenizerButton, removeTokenizerButton, editTokenizerButton,
		    processButton, processAllButton,
		    saveOptionsButton, appendOptionsButton,  deleteOptionsButton, newOptionsButton,
		    saveEngineButton, deleteEngineButton, newEngineButton,
		    importEnginesButton, exportEnginesButton, impExpButton, cancelImpExpButton,
		    ctagsButton, jarButton, textFileButton, codeButton, bufferWordsButton,
		    simpleModeButton, advancedModeButton};
	    for (AbstractButton b : buttons)
		b.addActionListener(this);
		
	    enginesCombo.addActionListener(this);
	    optionGroupCombo.addActionListener(this);
	    
	    loadOptionGroups();
	    loadEngines();
	    cacheCheck.setSelected(((Boolean)CamelCompletePlugin.getOption("cache")).booleanValue());
	    updateCheck.setSelected(((Boolean)CamelCompletePlugin.getOption("update")).booleanValue());
	    loadDialogCheck.setSelected(((Boolean)CamelCompletePlugin.getOption("loading-dlg")).booleanValue());
	    ((SpinnerNumberModel)popupRowsSpinner.getModel()).setValue(
		      (Integer)CamelCompletePlugin.getOption("popup-rows"));
	    addComponent(mainPanel);
	    
	    searchAllBuffersButton.setSelected(
		    ((Boolean)CamelCompletePlugin.getOption("simple-search-all")).booleanValue());
	    if (((Boolean)CamelCompletePlugin.getOption("simple-token-C-style")).booleanValue())
		CJavaButton.setSelected(true);
	    else
		lispButton.setSelected(true);
	    if (((Boolean)CamelCompletePlugin.getOption("simple-mode")).booleanValue()) {
		simpleModeButton.doClick();
	    } else {
		advancedModeButton.doClick();
	    }
	    
	    CamelCompletePlugin.rememberOptionPanel(this);
	}
	
	protected void _save() {
	    if (simpleModeButton.isSelected()) {
		setSimpleMode();
	    } else {
		saveCurrentEngine();
		saveOptionGroups();
	    }
	    CamelCompletePlugin.setOption("cache", Boolean.valueOf(cacheCheck.isSelected()));
	    CamelCompletePlugin.setOption("update", Boolean.valueOf(updateCheck.isSelected()));
	    CamelCompletePlugin.setOption("simple-mode", Boolean.valueOf(simpleModeButton.isSelected()));
	    CamelCompletePlugin.setOption("simple-search-all", Boolean.valueOf(searchAllBuffersButton.isSelected()));
	    CamelCompletePlugin.setOption("simple-token-C-style", Boolean.valueOf(CJavaButton.isSelected()));
	    CamelCompletePlugin.setOption("loading-dlg", Boolean.valueOf(loadDialogCheck.isSelected()));
	    CamelCompletePlugin.setOption("popup-rows", popupRowsSpinner.getValue());
	}
	
	void forgetStaticThings() {
	    // These are actually references to static members of CamelCompletePlugin
	    optionGroupMap = null;
	    enginesOptionsMap = null;
	    eoMap = null;
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
		    else if (textFileButton.isSelected())
			og.provider = "text";
		    else if (codeButton.isSelected())
			og.provider = "code";
		    else if (bufferWordsButton.isSelected()) {
			og.provider = "buffer";
			og.config = Boolean.valueOf(!currentViewCheck.isSelected());
		    }
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
	    } else if (source == copyProviderButton) {
		selectedProvider = providerList.getSelectedIndex();
		if (selectedProvider != -1) {
		    OptionGroup og = ((OptionGroup)providerModel.get(selectedProvider)).copy();
		    providerModel.addElement(og);
		    
		    // Now it will be as though we hit edit.
		    providerList.setSelectedValue(og, true);
		    selectedProvider = providerList.getSelectedIndex();
		    fillFieldsFromOptions(og);
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
	    } else if (source == editTokenizerButton) {
		selectedTokenizer = tokenizerList.getSelectedIndex();
		if (selectedTokenizer != -1) {
		    resetComponents(false, true);
		    String [] tokenizer = ((TokenizerHolder)tokenizerModel.
		    				get(selectedTokenizer)).tokenizer;
		    if (tokenizer[0].equals("camelcase"))
			camelCaseButton.setSelected(true);
		    else if (tokenizer[0].equals("regex")) {
			regexButton.setSelected(true);
			regexField.setText(tokenizer[1]);
			regexIgnoreCaseCheck.setSelected(tokenizer[2].equals("y"));
			tokenizerModel.removeElementAt(selectedTokenizer);
		    }
		}
	    } // }}} 
	    // {{{ Option Groups buttons
	    else if (source == newOptionsButton) {
		optionGroupCombo.setSelectedItem("");
		tokenizerModel.clear();
		providerModel.clear();
		resetComponents(true, true);
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
		disabledNormalButton.setSelected(true);
		engineEnabledCheck.setSelected(true);
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
			disabledNormalButton.setSelected(true);
		    }
		}
	    } // }}} 
	    // {{{ Import/Export buttons
	    else if (source == importEnginesButton) {
		String[] paths = GUIUtilities.showVFSFileDialog(jEdit.getFirstView(), null, 
						    VFSBrowser.OPEN_DIALOG, false);
		if (paths == null)
		    return;
		importEngines(paths[0]);
	    } else if (source == exportEnginesButton) {
		exportEngines();
	    } else if (source == impExpButton || source == cancelImpExpButton) {
		impExpButtonClicked = (JButton)source;
		impExpDialog.setVisible(false);
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
	    // {{{ Combo boxes
	    else if (source == enginesCombo) {
		String engineName = (String)enginesCombo.getSelectedItem();
		if (engineName != null && engineName.length() > 0) {
		    if (enginesOptionsMap.containsKey(engineName)) {
			loadOptions(enginesOptionsMap.get(engineName));
			currentEngineName = engineName;
			EngineOpts eo = eoMap.get(engineName);
			switch(eo.normalCompletionMode) {
			  case 0:
			    disabledNormalButton.setSelected(true);
			    break;
			  case 1:
			    startsNormalButton.setSelected(true);
			    break;
			  case 2:
			    containsNormalButton.setSelected(true);
			    break;
			}
			engineEnabledCheck.setSelected(eo.enabled);
		    }
		}
	    } else if (source == optionGroupCombo) {
		String key = (String)optionGroupCombo.getSelectedItem();
		if (key != null && key.length() > 0) {
		    loadOptions(optionGroupMap.get(key));
		}
	    }
	    // }}}
	    // {{{ Provider Type buttons
	    else if (source == ctagsButton || source == jarButton || source == textFileButton) {
		filenameField.setEnabled(true);
		label3.setEnabled(true);	// The "Filename" label
		chooseButton.setEnabled(true);
		extraLabel.setText("");
		extraField.setEnabled(false);
		currentViewCheck.setVisible(false);
	    } else if (source == codeButton || source == bufferWordsButton) {
		filenameField.setEnabled(false);
		label3.setEnabled(false);
		chooseButton.setEnabled(false);
		extraField.setEnabled(true);
		if (source == codeButton) {
		    extraLabel.setText("Code");
		    currentViewCheck.setVisible(false);
		} else if (source == bufferWordsButton) {
		    extraLabel.setText("Regex");
		    currentViewCheck.setVisible(true);
		    currentViewCheck.setSelected(false);
		}
	    }
	    // }}}
	    // {{{ Simple/Advanced Mode Buttons
	    else if (source == simpleModeButton || source == advancedModeButton) {
		optionPanel.setVisible(!(source == simpleModeButton));
		simplePanel.setVisible(source == simpleModeButton);
	    }
	    // }}}
	}
	
	// }}}
	
	// {{{ Utility Methods
	
	// 	{{{ resetComponents()
	private void resetComponents() {
	    resetComponents(true, true);
	}
	
	private void resetComponents(boolean clearProviderFields, boolean clearTokenizerFields) {
	    if (clearProviderFields) {
		ctagsButton.doClick();
		filenameField.setText("");
		filterField.setText("");
		extraField.setText("");
		ignoreCaseCheck.setSelected(false);
		((SpinnerNumberModel)minpartsSpinner.getModel()).setValue(new Integer(2));
		((SpinnerNumberModel)maxpartsSpinner.getModel()).setValue(new Integer(8));
	    }

	    if (clearTokenizerFields) {
		camelCaseButton.doClick();
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
		ctagsButton.doClick();
	    else if (og.provider.equals("jar"))
		jarButton.doClick();
	    else if (og.provider.equals("text"))
		textFileButton.doClick();
	    else if (og.provider.equals("code"))
		codeButton.doClick();
	    else if (og.provider.equals("buffer")) {
		bufferWordsButton.doClick();
		if (og.config != null)
		    currentViewCheck.setSelected(!((Boolean)og.config).booleanValue());
	    }
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
	
	private void displayMessage(String msg) {
	    JOptionPane.showMessageDialog(jEdit.getFirstView(), msg, "CamelComplete",
	    				  JOptionPane.INFORMATION_MESSAGE);
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
	    if (currentEngineName != null && currentEngineName.length() > 0) {
		enginesOptionsMap.put(currentEngineName, saveOptions());
		EngineOpts eo = new EngineOpts();
		if (disabledNormalButton.isSelected())
		    eo.normalCompletionMode = 0;
		else if (startsNormalButton.isSelected())
		    eo.normalCompletionMode = 1;
		else if (containsNormalButton.isSelected())
		    eo.normalCompletionMode = 2;
		eo.enabled = engineEnabledCheck.isSelected();
		eoMap.put(currentEngineName, eo);
	    }
	}
	
	private void loadEngines() {
	    enginesOptionsMap = 
		(Map<String,List<OptionPanel.OptionGroup>>)CamelCompletePlugin.getOption("engines");
	    eoMap = (HashMap<String, EngineOpts>)CamelCompletePlugin.getOption("engine-opts");
	    for (String engineName : enginesOptionsMap.keySet()) {
		enginesCombo.addItem(engineName);
	    }
	    enginesCombo.setSelectedIndex(0);
	    currentEngineName = (String)enginesCombo.getSelectedItem();
	    loadOptions(enginesOptionsMap.get(currentEngineName));
	}
	    
	// }}}

	//	{{{ Import/Export routines (and simple mode)
	private void importEngines(String filename) {
	    FileInputStream in = null;
	    HashMap<String,Object> import_optionsMap = null;
	    HashMap<String,List<OptionPanel.OptionGroup>> import_enginesOptionsMap = null;
	    HashMap<String, OptionPanel.EngineOpts> import_eoMap = null;
	    
	    ObjectInputStream  ois = null;
	    try {
		in = new FileInputStream(filename);
		ois = new ObjectInputStream(in);
		import_optionsMap = (HashMap<String,Object>)ois.readObject();
	    } catch (FileNotFoundException ex) {
		displayMessage("The file " + filename + " doesn't exist.");
		import_optionsMap = null;
	    } catch (IOException ex) {
		displayMessage("I had trouble reading valid engine data from that file.");
		import_optionsMap = null;
	    } catch (ClassNotFoundException ex) {
		displayMessage("This doesn't look like valid engine data.");
		import_optionsMap = null;
	    } finally {
		if (ois != null) {
		    try {
			ois.close();
		    } catch (IOException ex) {}
		}
	    }
	    
	    if (import_optionsMap == null)
		return;
		
	    import_enginesOptionsMap = (HashMap<String,List<OptionPanel.OptionGroup>>)
					    import_optionsMap.get("engines");
	    import_eoMap = (HashMap<String, OptionPanel.EngineOpts>)import_optionsMap.get("engine-opts");
	    impExpListModel.clear();
	    impExpList.clearSelection();
	    for (String engineName : import_enginesOptionsMap.keySet())
		impExpListModel.addElement(engineName);
	    
	    impExpButtonClicked = null;
	    impExpButton.setText("Import");
	    // Show the modal dialog.
	    impExpDialog.setVisible(true);
	    
	    if (impExpButtonClicked == impExpButton) {
		for (Object o : impExpList.getSelectedValues()) {
		    String engineName = (String)o;
		    if (!enginesOptionsMap.containsKey(engineName))
			enginesCombo.addItem(engineName);
		    enginesOptionsMap.put(engineName, import_enginesOptionsMap.get(engineName));
		    eoMap.put(engineName, import_eoMap.get(engineName));
		}
	    }
	}
	
	private void exportEngines() {
	    FileOutputStream out = null;
	    HashMap<String,Object> export_optionsMap = null;
	    HashMap<String,List<OptionPanel.OptionGroup>> export_enginesOptionsMap = null;
	    HashMap<String, OptionPanel.EngineOpts> export_eoMap = null;
	    
	    impExpListModel.clear();
	    impExpList.clearSelection();
	    for (String engineName : enginesOptionsMap.keySet())
		impExpListModel.addElement(engineName);
		
	    impExpButtonClicked = null;
	    impExpButton.setText("Export");
	    impExpDialog.setVisible(true);
	    
	    if (impExpButtonClicked == impExpButton) {
		export_enginesOptionsMap = new HashMap<String,List<OptionPanel.OptionGroup>>();
		export_eoMap = new HashMap<String, OptionPanel.EngineOpts>();
		export_optionsMap = new HashMap<String,Object>();
		export_optionsMap.put("engines", export_enginesOptionsMap);
		export_optionsMap.put("engine-opts", export_eoMap);
		
		for (Object o : impExpList.getSelectedValues()) {
		    String engineName = (String)o;
		    export_enginesOptionsMap.put(engineName, enginesOptionsMap.get(engineName));
		    export_eoMap.put(engineName, eoMap.get(engineName));
		}
	
		String[] paths = GUIUtilities.showVFSFileDialog(jEdit.getFirstView(), null, 
						    VFSBrowser.SAVE_DIALOG, false);
		if (paths == null)
		    return;
		try {
		    out = new FileOutputStream(paths[0]);
		    ObjectOutputStream oos = new ObjectOutputStream(out);
		    oos.writeObject(export_optionsMap);
		    oos.close();
		} catch (IOException ex) {
		    displayMessage("I had an error writing to that file.");
		}
	    }
	}
	
	private void setSimpleMode() {
	    HashMap<String,Object> import_optionsMap = null;
	    HashMap<String,List<OptionPanel.OptionGroup>> import_enginesOptionsMap = null;
	    HashMap<String, OptionPanel.EngineOpts> import_eoMap = null;

	    try {
		InputStream i = CamelCompletePlugin.class.getResourceAsStream(CJavaButton.isSelected() ?
			"/simple-complete-config.options" : "/simple-complete-lisp-config.options");
		ObjectInputStream  ois = new ObjectInputStream(i);
		import_optionsMap = (HashMap<String,Object>)ois.readObject();
		ois.close();
		
		import_enginesOptionsMap = (HashMap<String,List<OptionPanel.OptionGroup>>)
						import_optionsMap.get("engines");
		import_eoMap = (HashMap<String, OptionPanel.EngineOpts>)import_optionsMap.get("engine-opts");
		String[] _names = new String[] {"View Buffers", "Buffers"};
		for (String _n : _names) {
		    enginesOptionsMap.put(_n, import_enginesOptionsMap.get(_n));
		    eoMap.put(_n, import_eoMap.get(_n));
		}
	    } catch (Exception e) {} /* Naturally, should never happen.*/
	}
		
	//	}}}

	// }}}
	
	// {{{ JFormDesigner initComponents()
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
		mainPanel = new JPanel();
		optionPanel = new JPanel();
		panel7 = new JPanel();
		label7 = new JLabel();
		enginesCombo = new JComboBox();
		saveEngineButton = new JButton();
		deleteEngineButton = new JButton();
		newEngineButton = new JButton();
		engineEnabledCheck = new JCheckBox();
		panel16 = new JPanel();
		importEnginesButton = new JButton();
		exportEnginesButton = new JButton();
		label6 = new JLabel();
		label1 = new JLabel();
		panel5 = new JPanel();
		panel9 = new JPanel();
		optionGroupCombo = new JComboBox();
		saveOptionsButton = new JButton();
		newOptionsButton = new JButton();
		appendOptionsButton = new JButton();
		deleteOptionsButton = new JButton();
		separator1 = compFactory.createSeparator("");
		panel12 = new JPanel();
		label10 = new JLabel();
		disabledNormalButton = new JRadioButton();
		startsNormalButton = new JRadioButton();
		containsNormalButton = new JRadioButton();
		scrollPane1 = new JScrollPane();
		providerList = new JList();
		panel2 = new JPanel();
		ctagsButton = new JRadioButton();
		jarButton = new JRadioButton();
		textFileButton = new JRadioButton();
		panel11 = new JPanel();
		codeButton = new JRadioButton();
		bufferWordsButton = new JRadioButton();
		label3 = new JLabel();
		currentViewCheck = new JCheckBox();
		filenameField = new JTextField();
		chooseButton = new JButton();
		panel10 = new JPanel();
		extraLabel = new JLabel();
		extraField = new JTextField();
		saveProviderButton = new JButton();
		deleteProviderButton = new JButton();
		editProviderButton = new JButton();
		copyProviderButton = new JButton();
		label2 = new JLabel();
		panel14 = new JPanel();
		loadDialogCheck = new JCheckBox();
		scrollPane2 = new JScrollPane();
		tokenizerList = new JList();
		panel3 = new JPanel();
		camelCaseButton = new JRadioButton();
		regexButton = new JRadioButton();
		regexField = new JTextField();
		regexIgnoreCaseCheck = new JCheckBox();
		panel13 = new JPanel();
		addTokenizerButton = new JButton();
		removeTokenizerButton = new JButton();
		editTokenizerButton = new JButton();
		panel1 = new JPanel();
		label9 = new JLabel();
		popupRowsSpinner = new JSpinner();
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
		panel6 = new JPanel();
		modePanel = new JPanel();
		simpleModeButton = new JRadioButton();
		advancedModeButton = new JRadioButton();
		simplePanel = new JPanel();
		searchAllBuffersButton = new JCheckBox();
		label11 = new JLabel();
		CJavaButton = new JRadioButton();
		lispButton = new JRadioButton();
		impExpDialog = new JDialog();
		scrollPane3 = new JScrollPane();
		impExpList = new JList();
		panel15 = new JPanel();
		impExpButton = new JButton();
		cancelImpExpButton = new JButton();
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
						FormFactory.LINE_GAP_ROWSPEC,
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

					//---- saveEngineButton ----
					saveEngineButton.setText("Save");
					panel7.add(saveEngineButton);

					//---- deleteEngineButton ----
					deleteEngineButton.setText("Delete");
					panel7.add(deleteEngineButton);

					//---- newEngineButton ----
					newEngineButton.setText("New");
					panel7.add(newEngineButton);

					//---- engineEnabledCheck ----
					engineEnabledCheck.setText("Enabled");
					panel7.add(engineEnabledCheck);
				}
				optionPanel.add(panel7, cc.xywh(1, 1, 5, 1));

				//======== panel16 ========
				{
					panel16.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

					//---- importEnginesButton ----
					importEnginesButton.setText("Import");
					panel16.add(importEnginesButton);

					//---- exportEnginesButton ----
					exportEnginesButton.setText("Export");
					panel16.add(exportEnginesButton);
				}
				optionPanel.add(panel16, cc.xywh(1, 3, 3, 1));

				//---- label6 ----
				label6.setText("Option Sets");
				optionPanel.add(label6, cc.xy(1, 5));

				//---- label1 ----
				label1.setText("Identifier Providers");
				optionPanel.add(label1, cc.xy(3, 5));

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

					//---- saveOptionsButton ----
					saveOptionsButton.setText("Save");
					panel5.add(saveOptionsButton, cc.xy(1, 3));

					//---- newOptionsButton ----
					newOptionsButton.setText("New");
					panel5.add(newOptionsButton, cc.xy(3, 3));

					//---- appendOptionsButton ----
					appendOptionsButton.setText("Append");
					panel5.add(appendOptionsButton, cc.xy(1, 5));

					//---- deleteOptionsButton ----
					deleteOptionsButton.setText("Delete");
					panel5.add(deleteOptionsButton, cc.xy(3, 5));
					panel5.add(separator1, cc.xywh(1, 7, 3, 1));

					//======== panel12 ========
					{
						panel12.setLayout(new FormLayout(
							ColumnSpec.decodeSpecs("left:default"),
							new RowSpec[] {
								new RowSpec(RowSpec.BOTTOM, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
								FormFactory.LINE_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.LINE_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.LINE_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC
							}));

						//---- label10 ----
						label10.setText("<html>Normal Completion<br>Mode</html>");
						panel12.add(label10, cc.xy(1, 1));

						//---- disabledNormalButton ----
						disabledNormalButton.setText("Disabled");
						disabledNormalButton.setSelected(true);
						panel12.add(disabledNormalButton, cc.xy(1, 3));

						//---- startsNormalButton ----
						startsNormalButton.setText("Starts With");
						panel12.add(startsNormalButton, cc.xy(1, 5));

						//---- containsNormalButton ----
						containsNormalButton.setText("Contains");
						panel12.add(containsNormalButton, cc.xy(1, 7));
					}
					panel5.add(panel12, cc.xywh(1, 9, 3, 1));
				}
				optionPanel.add(panel5, cc.xy(1, 7));

				//======== scrollPane1 ========
				{

					//---- providerList ----
					providerList.setPrototypeCellValue("XXXXXXXXXXXXXXXXXXXXX");
					providerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane1.setViewportView(providerList);
				}
				optionPanel.add(scrollPane1, cc.xy(3, 7));

				//======== panel2 ========
				{
					panel2.setLayout(new FormLayout(
						new ColumnSpec[] {
							new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.CENTER, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC
						},
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(Sizes.dluY(12)),
							FormFactory.RELATED_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.RELATED_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.RELATED_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}));

					//---- ctagsButton ----
					ctagsButton.setText("Ctags");
					panel2.add(ctagsButton, cc.xy(1, 1));

					//---- jarButton ----
					jarButton.setText("JAR");
					panel2.add(jarButton, cc.xy(3, 1));

					//---- textFileButton ----
					textFileButton.setText("Text");
					panel2.add(textFileButton, cc.xy(5, 1));

					//======== panel11 ========
					{
						panel11.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

						//---- codeButton ----
						codeButton.setText("Bsh Code");
						panel11.add(codeButton);

						//---- bufferWordsButton ----
						bufferWordsButton.setText("Buffer Words");
						panel11.add(bufferWordsButton);
					}
					panel2.add(panel11, cc.xywh(1, 3, 5, 1));

					//---- label3 ----
					label3.setText("Filename");
					panel2.add(label3, cc.xy(1, 5));

					//---- currentViewCheck ----
					currentViewCheck.setText("Only Active Views");
					panel2.add(currentViewCheck, cc.xywh(3, 5, 3, 1));
					panel2.add(filenameField, cc.xywh(1, 7, 3, 1));

					//---- chooseButton ----
					chooseButton.setText("Choose");
					panel2.add(chooseButton, cc.xy(5, 7));

					//======== panel10 ========
					{
						panel10.setLayout(new BorderLayout(4, 0));

						//---- extraLabel ----
						extraLabel.setText("Extra");
						panel10.add(extraLabel, BorderLayout.WEST);
						panel10.add(extraField, BorderLayout.CENTER);
					}
					panel2.add(panel10, cc.xywh(1, 9, 5, 1));

					//---- saveProviderButton ----
					saveProviderButton.setText("Save");
					panel2.add(saveProviderButton, cc.xy(1, 11));

					//---- deleteProviderButton ----
					deleteProviderButton.setText("Delete");
					panel2.add(deleteProviderButton, cc.xy(3, 11));

					//---- editProviderButton ----
					editProviderButton.setText("Edit");
					panel2.add(editProviderButton, cc.xy(5, 11));

					//---- copyProviderButton ----
					copyProviderButton.setText("Copy");
					panel2.add(copyProviderButton, cc.xy(1, 13));
				}
				optionPanel.add(panel2, cc.xy(5, 7));

				//---- label2 ----
				label2.setText("Tokenizers for Provider");
				optionPanel.add(label2, cc.xy(3, 9));

				//======== panel14 ========
				{
					panel14.setLayout(new BorderLayout());

					//---- loadDialogCheck ----
					loadDialogCheck.setText("<html>Show \"Loading\"<br>Dialog</html>");
					panel14.add(loadDialogCheck, BorderLayout.SOUTH);
				}
				optionPanel.add(panel14, cc.xywh(1, 11, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

				//======== scrollPane2 ========
				{

					//---- tokenizerList ----
					tokenizerList.setPrototypeCellValue("XXXXX");
					tokenizerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane2.setViewportView(tokenizerList);
				}
				optionPanel.add(scrollPane2, cc.xy(3, 11));

				//======== panel3 ========
				{
					panel3.setLayout(new FormLayout(
						new ColumnSpec[] {
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(21), FormSpec.DEFAULT_GROW)
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

					//======== panel13 ========
					{
						panel13.setLayout(new FlowLayout(FlowLayout.LEFT));

						//---- addTokenizerButton ----
						addTokenizerButton.setText("Add");
						panel13.add(addTokenizerButton);

						//---- removeTokenizerButton ----
						removeTokenizerButton.setText("Remove");
						panel13.add(removeTokenizerButton);

						//---- editTokenizerButton ----
						editTokenizerButton.setText("Edit");
						panel13.add(editTokenizerButton);
					}
					panel3.add(panel13, cc.xywh(1, 7, 5, 1));
				}
				optionPanel.add(panel3, cc.xy(5, 11));

				//======== panel1 ========
				{
					panel1.setLayout(new BorderLayout(3, 3));

					//---- label9 ----
					label9.setText("Popup List Rows");
					panel1.add(label9, BorderLayout.NORTH);

					//---- popupRowsSpinner ----
					popupRowsSpinner.setModel(new SpinnerNumberModel(12, 4, null, 1));
					popupRowsSpinner.setPreferredSize(new Dimension(60, 20));
					panel1.add(popupRowsSpinner, BorderLayout.WEST);
				}
				optionPanel.add(panel1, cc.xy(1, 13));

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
					maxpartsSpinner.setModel(new SpinnerNumberModel(8, 1, null, 1));
					panel4.add(maxpartsSpinner, cc.xy(7, 3));
				}
				optionPanel.add(panel4, cc.xywh(3, 13, 3, 1));

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
				optionPanel.add(panel8, cc.xywh(1, 15, 5, 1));
				optionPanel.add(messageLabel, cc.xywh(3, 17, 3, 1));
			}
			mainPanel.add(optionPanel, BorderLayout.CENTER);

			//======== panel6 ========
			{
				panel6.setLayout(new BorderLayout());

				//======== modePanel ========
				{
					modePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));

					//---- simpleModeButton ----
					simpleModeButton.setText("Simple Mode");
					modePanel.add(simpleModeButton);

					//---- advancedModeButton ----
					advancedModeButton.setText("Advanced Mode");
					modePanel.add(advancedModeButton);
				}
				panel6.add(modePanel, BorderLayout.NORTH);

				//======== simplePanel ========
				{
					simplePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

					//---- searchAllBuffersButton ----
					searchAllBuffersButton.setText("Search All Buffers");
					simplePanel.add(searchAllBuffersButton);

					//---- label11 ----
					label11.setText("    Token Style:");
					simplePanel.add(label11);

					//---- CJavaButton ----
					CJavaButton.setText("C/Java");
					simplePanel.add(CJavaButton);

					//---- lispButton ----
					lispButton.setText("Lisp");
					simplePanel.add(lispButton);
				}
				panel6.add(simplePanel, BorderLayout.SOUTH);
			}
			mainPanel.add(panel6, BorderLayout.NORTH);
		}

		//======== impExpDialog ========
		{
			impExpDialog.setTitle("Engines");
			impExpDialog.setModal(true);
			Container impExpDialogContentPane = impExpDialog.getContentPane();
			impExpDialogContentPane.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.UNRELATED_GAP_COLSPEC,
					new ColumnSpec("max(default;63dlu)"),
					FormFactory.UNRELATED_GAP_COLSPEC
				},
				new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC
				}));

			//======== scrollPane3 ========
			{
				scrollPane3.setViewportView(impExpList);
			}
			impExpDialogContentPane.add(scrollPane3, cc.xy(2, 3));

			//======== panel15 ========
			{
				panel15.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));

				//---- impExpButton ----
				impExpButton.setText("ImpExp");
				panel15.add(impExpButton);

				//---- cancelImpExpButton ----
				cancelImpExpButton.setText("Cancel");
				panel15.add(cancelImpExpButton);
			}
			impExpDialogContentPane.add(panel15, cc.xy(2, 5));
			impExpDialog.setSize(200, 200);
			impExpDialog.setLocationRelativeTo(impExpDialog.getOwner());
		}

		//---- buttonGroup3 ----
		ButtonGroup buttonGroup3 = new ButtonGroup();
		buttonGroup3.add(disabledNormalButton);
		buttonGroup3.add(startsNormalButton);
		buttonGroup3.add(containsNormalButton);

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(ctagsButton);
		buttonGroup1.add(jarButton);
		buttonGroup1.add(textFileButton);
		buttonGroup1.add(codeButton);
		buttonGroup1.add(bufferWordsButton);

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(camelCaseButton);
		buttonGroup2.add(regexButton);

		//---- buttonGroup4 ----
		ButtonGroup buttonGroup4 = new ButtonGroup();
		buttonGroup4.add(simpleModeButton);
		buttonGroup4.add(advancedModeButton);

		//---- buttonGroup5 ----
		ButtonGroup buttonGroup5 = new ButtonGroup();
		buttonGroup5.add(CJavaButton);
		buttonGroup5.add(lispButton);
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
	JButton saveEngineButton;
	JButton deleteEngineButton;
	JButton newEngineButton;
	JCheckBox engineEnabledCheck;
	JPanel panel16;
	JButton importEnginesButton;
	JButton exportEnginesButton;
	JLabel label6;
	JLabel label1;
	JPanel panel5;
	JPanel panel9;
	JComboBox optionGroupCombo;
	JButton saveOptionsButton;
	JButton newOptionsButton;
	JButton appendOptionsButton;
	JButton deleteOptionsButton;
	JComponent separator1;
	JPanel panel12;
	JLabel label10;
	JRadioButton disabledNormalButton;
	JRadioButton startsNormalButton;
	JRadioButton containsNormalButton;
	JScrollPane scrollPane1;
	JList providerList;
	JPanel panel2;
	JRadioButton ctagsButton;
	JRadioButton jarButton;
	JRadioButton textFileButton;
	JPanel panel11;
	JRadioButton codeButton;
	JRadioButton bufferWordsButton;
	JLabel label3;
	JCheckBox currentViewCheck;
	JTextField filenameField;
	JButton chooseButton;
	JPanel panel10;
	JLabel extraLabel;
	JTextField extraField;
	JButton saveProviderButton;
	JButton deleteProviderButton;
	JButton editProviderButton;
	JButton copyProviderButton;
	JLabel label2;
	JPanel panel14;
	JCheckBox loadDialogCheck;
	JScrollPane scrollPane2;
	JList tokenizerList;
	JPanel panel3;
	JRadioButton camelCaseButton;
	JRadioButton regexButton;
	JTextField regexField;
	JCheckBox regexIgnoreCaseCheck;
	JPanel panel13;
	JButton addTokenizerButton;
	JButton removeTokenizerButton;
	JButton editTokenizerButton;
	JPanel panel1;
	JLabel label9;
	JSpinner popupRowsSpinner;
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
	JPanel panel6;
	JPanel modePanel;
	JRadioButton simpleModeButton;
	JRadioButton advancedModeButton;
	JPanel simplePanel;
	JCheckBox searchAllBuffersButton;
	JLabel label11;
	JRadioButton CJavaButton;
	JRadioButton lispButton;
	JDialog impExpDialog;
	JScrollPane scrollPane3;
	JList impExpList;
	JPanel panel15;
	JButton impExpButton;
	JButton cancelImpExpButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	// }}} 

	// {{{ Inner Classes
	public static class OptionGroup implements Serializable
	{
	    static final long serialVersionUID = 3023920492517680506L;
	    
	    String provider;  //"ctags", "jar", "text", "buffer", or "code"
	    String fileName;
	    String extra;
	    java.util.List<String[]> tokenizers;
	    // Either ["camelcase"] or ["regex", <regex>, "y"|"n" (ignore case)]
	    int minparts, maxparts;
	    boolean ignoreCase;
	    String filterRegex;
	    Object config; // Extra options, see below
	    // For "buffer", Boolean: search all buffers (true), or only active view (false)
	    
	    public String toString() {
		if (provider.equals("ctags") || provider.equals("jar") || provider.equals("text")) {
		    int i = fileName.lastIndexOf(File.separatorChar);
		    i++;
		    return provider + ", " + fileName.substring(i);
		} else if (provider.equals("code")) {
		    return "BeanShell";
		} else if (provider.equals("buffer")) {
		    return "Buffer Words, " + extra;
		}
		return "";
	    }
	    
	    public OptionGroup copy() {
		OptionGroup og = new OptionGroup();
		og.provider = provider;
		og.fileName = fileName;
		og.extra = extra;
		og.tokenizers = new ArrayList(tokenizers);
		og.minparts = minparts;
		og.maxparts = maxparts;
		og.ignoreCase = ignoreCase;
		og.filterRegex = filterRegex;
		og.config = config;
		return og;
	    }
	}
	
	public static class EngineOpts implements Serializable
	{
	    static final long serialVersionUID = 2989263505948239743L;

	    boolean enabled = true;
	    int normalCompletionMode;  // 0 = disabled, 1 = starts with, 2 = contains
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
