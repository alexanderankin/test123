package com.illengineer.jcc.jedit;

import com.illengineer.jcc.*;

import java.io.*;
import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

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
	    			 addTokenizerButton, removeTokenizerButton, processButton,
				 loadOptionsButton, saveOptionsButton, deleteOptionsButton, newOptionsButton};
	    for (JButton b : buttons)
		b.addActionListener(this);
	    
	    loadOptionGroups();
	    loadCurrentEngine();
	    addComponent(mainPanel);
	}
	
	protected void _save() {
	    saveCurrentEngine(); // TODO, save all engines
	    saveOptionGroups();
	}
	
	// }}}
	
	// {{{ Event Handlers
	
	public void actionPerformed(ActionEvent ev) {
	    JComponent source = (JComponent)ev.getSource();
	    // {{{ Providers Buttons
	    if (source == saveProviderButton) {
		// we need at least one tokenizer and a file name
		String fileName = filenameField.getText();
		if (tokenizerModel.size() > 0 && fileName.length() > 0) {
		    OptionGroup og = new OptionGroup();
		    if (ctagsButton.isSelected())
			og.provider = "ctags";
		    else if (jarButton.isSelected())
			og.provider = "jar";
		    og.fileName = fileName;
		    og.tokenizers = new ArrayList<String[]>(tokenizerModel.size()+1);
		    for (Enumeration e = tokenizerModel.elements(); e.hasMoreElements(); )
			og.tokenizers.add(((TokenizerHolder)e.nextElement()).tokenizer);
		    og.minparts = ((Integer)minpartsSpinner.getValue()).intValue();
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
		    tokenizerModel.clear();
		    providerModel.clear();
		    resetComponents(true, true);
		    loadOptions(optionGroupMap.get(key));
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
	    else if (source == processButton) {
		saveCurrentEngine();
		showMsg("Updating identifier lists...");
		try {
		    CamelCompletePlugin.processConfiguration(currentEngineName);
		} catch (Exception ex) {
		    showMsg("Error: " + ex.getMessage());
		    return;
		}
		showMsg("");
	    }
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
		ignoreCaseCheck.setSelected(false);
		((SpinnerNumberModel)minpartsSpinner.getModel()).setValue(new Integer(2));
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
	    filenameField.setText(og.fileName);
	    for (String [] t : og.tokenizers)
		tokenizerModel.addElement(new TokenizerHolder(t));
	    ((SpinnerNumberModel)minpartsSpinner.getModel()).setValue(new Integer(og.minparts));
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
	    if (options != null)
		for (OptionGroup og : options)
		    providerModel.addElement(og);
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
	    Map<String,List<OptionPanel.OptionGroup>> _enginesMap =
		(Map<String,List<OptionPanel.OptionGroup>>)CamelCompletePlugin.getOption("engines");
	    _enginesMap.put(currentEngineName, saveOptions());
	}
	
	private void loadCurrentEngine() {
	    Map<String,List<OptionPanel.OptionGroup>> _enginesMap =
		(Map<String,List<OptionPanel.OptionGroup>>)CamelCompletePlugin.getOption("engines");
	    loadOptions((List<OptionGroup>)_enginesMap.get(currentEngineName));
	}
	    
	// }}}

	// }}}
	
	// {{{ JFormDesigner initComponents()
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		mainPanel = new JPanel();
		optionPanel = new JPanel();
		label6 = new JLabel();
		label1 = new JLabel();
		panel5 = new JPanel();
		optionGroupCombo = new JComboBox();
		loadOptionsButton = new JButton();
		saveOptionsButton = new JButton();
		deleteOptionsButton = new JButton();
		newOptionsButton = new JButton();
		scrollPane1 = new JScrollPane();
		providerList = new JList();
		panel2 = new JPanel();
		ctagsButton = new JRadioButton();
		jarButton = new JRadioButton();
		label3 = new JLabel();
		filenameField = new JTextField();
		chooseButton = new JButton();
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
		label4 = new JLabel();
		filterField = new JTextField();
		label5 = new JLabel();
		minpartsSpinner = new JSpinner();
		processButton = new JButton();
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

				//---- label6 ----
				label6.setText("Option Sets");
				optionPanel.add(label6, cc.xy(1, 1));

				//---- label1 ----
				label1.setText("Identifier Providers");
				optionPanel.add(label1, cc.xy(3, 1));

				//======== panel5 ========
				{
					panel5.setLayout(new GridLayout(0, 1, 0, 2));

					//---- optionGroupCombo ----
					optionGroupCombo.setEditable(true);
					panel5.add(optionGroupCombo);

					//---- loadOptionsButton ----
					loadOptionsButton.setText("Load");
					panel5.add(loadOptionsButton);

					//---- saveOptionsButton ----
					saveOptionsButton.setText("Save");
					panel5.add(saveOptionsButton);

					//---- deleteOptionsButton ----
					deleteOptionsButton.setText("Delete");
					panel5.add(deleteOptionsButton);

					//---- newOptionsButton ----
					newOptionsButton.setText("New");
					panel5.add(newOptionsButton);
				}
				optionPanel.add(panel5, cc.xy(1, 3));

				//======== scrollPane1 ========
				{

					//---- providerList ----
					providerList.setPrototypeCellValue("XXXXXXXXXXXXXXXXXXXXX");
					providerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane1.setViewportView(providerList);
				}
				optionPanel.add(scrollPane1, cc.xy(3, 3));

				//======== panel2 ========
				{
					panel2.setLayout(new FormLayout(
						new ColumnSpec[] {
							new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
						},
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
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

					//---- label3 ----
					label3.setText("Filename");
					panel2.add(label3, cc.xy(1, 3));
					panel2.add(filenameField, cc.xywh(1, 5, 3, 1));

					//---- chooseButton ----
					chooseButton.setText("Choose");
					panel2.add(chooseButton, cc.xy(5, 5));

					//---- saveProviderButton ----
					saveProviderButton.setText("Save");
					panel2.add(saveProviderButton, cc.xy(1, 7));

					//---- deleteProviderButton ----
					deleteProviderButton.setText("Delete");
					panel2.add(deleteProviderButton, cc.xy(3, 7));

					//---- editProviderButton ----
					editProviderButton.setText("Edit");
					panel2.add(editProviderButton, cc.xy(5, 7));
				}
				optionPanel.add(panel2, cc.xy(5, 3));

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
				optionPanel.add(panel1, cc.xy(5, 3));

				//---- label2 ----
				label2.setText("Tokenizers for Provider");
				optionPanel.add(label2, cc.xy(3, 5));

				//======== scrollPane2 ========
				{

					//---- tokenizerList ----
					tokenizerList.setPrototypeCellValue("XXXXX");
					tokenizerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane2.setViewportView(tokenizerList);
				}
				optionPanel.add(scrollPane2, cc.xy(3, 7));

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
				optionPanel.add(panel3, cc.xy(5, 7));

				//======== panel4 ========
				{
					panel4.setLayout(new FormLayout(
						new ColumnSpec[] {
							FormFactory.DEFAULT_COLSPEC,
							new ColumnSpec(ColumnSpec.LEFT, Sizes.DLUX7, FormSpec.NO_GROW),
							new ColumnSpec(ColumnSpec.RIGHT, Sizes.DEFAULT, FormSpec.NO_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(Sizes.dluX(70))
						},
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}));

					//---- ignoreCaseCheck ----
					ignoreCaseCheck.setText("Provider Ignore Case");
					panel4.add(ignoreCaseCheck, cc.xy(1, 1));

					//---- label4 ----
					label4.setText("Filter Regexp");
					panel4.add(label4, cc.xy(3, 1));
					panel4.add(filterField, cc.xy(5, 1));

					//---- label5 ----
					label5.setText("Minimum Parts");
					panel4.add(label5, cc.xy(3, 3));

					//---- minpartsSpinner ----
					minpartsSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(1), null, new Integer(1)));
					panel4.add(minpartsSpinner, cc.xy(5, 3));
				}
				optionPanel.add(panel4, cc.xywh(3, 9, 3, 1));

				//---- processButton ----
				processButton.setText("Update Engine");
				optionPanel.add(processButton, cc.xy(3, 11));
				optionPanel.add(messageLabel, cc.xywh(3, 13, 3, 1));
			}
			mainPanel.add(optionPanel, BorderLayout.CENTER);
		}

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(ctagsButton);
		buttonGroup1.add(jarButton);

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(camelCaseButton);
		buttonGroup2.add(regexButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	// }}}

	// {{{ JFormDesigner variables
	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	JPanel mainPanel;
	JPanel optionPanel;
	JLabel label6;
	JLabel label1;
	JPanel panel5;
	JComboBox optionGroupCombo;
	JButton loadOptionsButton;
	JButton saveOptionsButton;
	JButton deleteOptionsButton;
	JButton newOptionsButton;
	JScrollPane scrollPane1;
	JList providerList;
	JPanel panel2;
	JRadioButton ctagsButton;
	JRadioButton jarButton;
	JLabel label3;
	JTextField filenameField;
	JButton chooseButton;
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
	JLabel label4;
	JTextField filterField;
	JLabel label5;
	JSpinner minpartsSpinner;
	JButton processButton;
	JLabel messageLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	// }}} 

	// {{{ Inner Classes
	public static class OptionGroup implements Serializable
	{
	    String provider;  //"ctags" or "jar"
	    String fileName;
	    java.util.List<String[]> tokenizers;
	    // Either ["camelcase"] or ["regex", <regex>, "y"|"n" (ignore case)]
	    int minparts;
	    boolean ignoreCase;
	    String filterRegex;
	    
	    public String toString() {
		int i = fileName.lastIndexOf(File.separatorChar);
		i++;
		return provider + ", " + fileName.substring(i);
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
