package superabbrevs.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import superabbrevs.model.Abbrev;
import superabbrevs.model.Abbrev.ReplacementTypes;
import superabbrevs.model.Abbrev.ReplementSelectionTypes;

public class AbbrevsEditorPane extends JPanel {
	
	public AbbrevsEditorPane() {
		initComponents();
    }
	
	public void setAbbrev(Abbrev abbrev) {
		saveActiveAbbrev();
		
		activeAbbrev = abbrev;
		
		if (abbrev == null) {
			ClearPanel();
		} else {			
			UpdatePanelValues(abbrev);
		}
		setEnabled(activeAbbrev != null);
	}

	public void saveActiveAbbrev() {
		if (activeAbbrev != null) {
			activeAbbrev.setAbbreviation(abbrevJTextField.getText());
			activeAbbrev.setExpansion(abbrevsEditorJTextArea.getText());
			Abbrev.WhenInvokedAsCommand whenInvokedAsCommand = new Abbrev.WhenInvokedAsCommand();
			whenInvokedAsCommand.replacementType = (ReplacementTypes) commandNoSelectionReplacementJComboBox.getSelectedItem();
			whenInvokedAsCommand.replacementSelectionType = (ReplementSelectionTypes) commandSelectionReplacementJComboBox.getSelectedItem();
		}
	}

	private void UpdatePanelValues(Abbrev abbrev) {
		if ("".equals(abbrev.getAbbreviation())) {
			abbrevJTextField.requestFocus();
		}
		abbrevJTextField.setText(abbrev.getAbbreviation());
		abbrevsEditorJTextArea.setText(abbrev.getExpansion());
		commandNoSelectionReplacementJComboBox.setSelectedItem(abbrev.getWhenInvokedAsCommand().replacementType);
		commandSelectionReplacementJComboBox.setSelectedItem(abbrev.getWhenInvokedAsCommand().replacementSelectionType);
	}

	private void ClearPanel() {
		abbrevJTextField.setText("");
		abbrevsEditorJTextArea.setText("");
		commandNoSelectionReplacementJComboBox.setSelectedItem(Abbrev.ReplacementTypes.AT_CARET);
		commandSelectionReplacementJComboBox.setSelectedItem(Abbrev.ReplementSelectionTypes.NOTHING);
	}

	private void initComponents() {
		
		setEnabled(false);
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints constrains = new GridBagConstraints();
		constrains.insets = new Insets(11,7,0,14);
		constrains.fill = GridBagConstraints.HORIZONTAL;
		constrains.weightx = 100;
		constrains.gridy = 0;
		add(createAbbrevPanel(), constrains);
		
		constrains.insets = new Insets(7,7,0,14);
		constrains.gridy = 1;
		JPanel whenInvikedAsACommandJPanel = createWhenInvokedAsACommandPanel();
		add(whenInvikedAsACommandJPanel,constrains);
		
		constrains.insets = new Insets(7,7,14,14);
		constrains.gridy = 2;
		constrains.fill = GridBagConstraints.BOTH;
		constrains.weighty = 100;
		add(createExpansionPanel(), constrains);
	}

	private JPanel createExpansionPanel() {
		BorderLayout layout = new BorderLayout(0,7);
		JPanel panel = new JPanel(layout);
		expansionJLabel.setDisplayedMnemonic('E');
        expansionJLabel.setLabelFor(abbrevsEditorJTextArea);
        expansionJLabel.setText("Expansion:");
        expansionJLabel.getAccessibleContext().setAccessibleName("expansionJLabel");
        panel.add(expansionJLabel, BorderLayout.NORTH);
        
        
        abbrevsEditorJTextArea.setColumns(80);
        abbrevsEditorJTextArea.setRows(5);
        abbrevsEditorJTextArea.setTabSize(4);
        abbrevsEditorJTextArea.setInputVerifier(new NotEmptyTextVerifier());
        
        abbrevsEditorJScrollPane.setViewportView(abbrevsEditorJTextArea);
        panel.add(abbrevsEditorJScrollPane, BorderLayout.CENTER);
        return panel;
	}

	private JPanel createAbbrevPanel() {
		BorderLayout layout = new BorderLayout();
		JPanel panel = new JPanel(layout);
		
		abbrevJLabel.setDisplayedMnemonic('b');
        abbrevJLabel.setLabelFor(abbrevJTextField);
        abbrevJLabel.setText("Abbreviation:");
        abbrevJLabel.getAccessibleContext().setAccessibleName("abbrevJLabel");
        panel.add(abbrevJLabel, BorderLayout.WEST);
        
        abbrevJTextField.setToolTipText("Enter the abbreviation");
        abbrevJTextField.setEnabled(true);
        abbrevJTextField.getAccessibleContext().setAccessibleName("abbrevJTextField");
        abbrevJTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String text = abbrevJTextField.getText();
				activeAbbrev.setAbbreviation(text);
			}
        });
        abbrevJTextField.setInputVerifier(new NotEmptyTextVerifier());
        panel.add(abbrevJTextField, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createWhenInvokedAsACommandPanel() {
		TitledBorder titledBorder = BorderFactory.createTitledBorder("When invoked as a command");
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(titledBorder);
		
		commandReplaceJLabel.setDisplayedMnemonic('p');
        commandReplaceJLabel.setLabelFor(commandSelectionReplacementJComboBox);
        commandReplaceJLabel.setText("Replace:");
        panel.add(commandReplaceJLabel);
		
        commandSelectionReplacementJComboBox.setModel(commandInputSelectionModel);
        commandSelectionReplacementJComboBox.setToolTipText("Select the area that should be replaced when abbreviation is inserted");
        panel.add(commandSelectionReplacementJComboBox);

        commandOrJLabel.setDisplayedMnemonic('o');
        commandOrJLabel.setLabelFor(commandNoSelectionReplacementJComboBox);
        commandOrJLabel.setText("or");
        panel.add(commandOrJLabel);

        commandNoSelectionReplacementJComboBox.setModel(commandInputModel);
        commandNoSelectionReplacementJComboBox.setToolTipText("Select the area that should be replaced when abbreviation is inserted");
        panel.add(commandNoSelectionReplacementJComboBox);

        return panel;
	}
	
    private class NotEmptyTextVerifier extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			return !"".equals(((JTextComponent)input).getText());
		}
	}
    
    /**
     * A combobox model containing all the replacaments types used in the 
     * replacement area combobox if some text is selected in the text area.
     */
    private ComboBoxModel commandInputModel = new DefaultComboBoxModel(
            new Abbrev.ReplacementTypes[]{
        Abbrev.ReplacementTypes.AT_CARET,
        Abbrev.ReplacementTypes.CHAR,
        Abbrev.ReplacementTypes.LINE,
        Abbrev.ReplacementTypes.WORD,
        Abbrev.ReplacementTypes.BUFFER
    });
    
    /**
     * A combobox model containing all the replacaments types used in the 
     * replacement area combobox if no text is selected in the text area.
     */
    private ComboBoxModel commandInputSelectionModel = new DefaultComboBoxModel(
            new Abbrev.ReplementSelectionTypes[]{
        Abbrev.ReplementSelectionTypes.NOTHING,
        Abbrev.ReplementSelectionTypes.SELECTION,
        Abbrev.ReplementSelectionTypes.SELECTED_LINES
    });
    
	private Abbrev activeAbbrev;
	private JLabel abbrevJLabel = new JLabel();
	private JTextField abbrevJTextField = new JTextField();
	private JScrollPane abbrevsEditorJScrollPane = new JScrollPane();
	private JTextArea abbrevsEditorJTextArea = new JTextArea();
	private JComboBox commandNoSelectionReplacementJComboBox = new JComboBox();
	private JLabel commandOrJLabel = new JLabel();
	private JLabel commandReplaceJLabel = new JLabel();
	private JComboBox commandSelectionReplacementJComboBox = new JComboBox();
	private JLabel expansionJLabel = new JLabel();
}
