package superabbrevs.gui;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.Validator;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.Binding.SyncFailure;

import superabbrevs.model.Abbrev;

public class AbbrevsEditorPane extends JPanel {
	
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
    
    class EmptyStringValidator extends Validator<String> {
		@Override
		public Validator<String>.Result validate(
				String abbreviation) {
			if (abbreviation == null || "".equals(abbreviation)) {
				return new Result(0, "The input value must be non empty");
			} 
			return null;
		}
	}
    
    public AbbrevsEditorPane() {
		initComponents();
    }
	
	public void bind(Abbrev abbrev) {
		bindingGroup = new org.jdesktop.beansbinding.BindingGroup();
		AutoBinding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, 
				abbrev, ELProperty.create("${abbreviation}"),
				abbrevJTextField, ELProperty.create("${text}"));
		EmptyStringValidator emptyStringValidator = new EmptyStringValidator();
		binding.setValidator(emptyStringValidator);
		
		bindingGroup.addBinding(binding); 
		
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, 
				abbrev, BeanProperty.create("expansion"),
				abbrevsEditorJTextArea, ELProperty.create("${text}"))); 
		
		bindingGroup.bind();
		bindingGroup.addBindingListener(new AbstractBindingListener() {

			@Override
			public void syncFailed(Binding binding, SyncFailure failure) {
				System.out.println(failure);
			}

		});
		
	}
	
	public void unBind() {
		bindingGroup.unbind();
	}

	private void initComponents() {
		
        expansionJLabel = new javax.swing.JLabel();
        abbrevJLabel = new javax.swing.JLabel();
        abbrevJTextField = new javax.swing.JTextField();
        abbrevsEditorJScrollPane = new javax.swing.JScrollPane();
        abbrevsEditorJTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        commandSelectionReplacementJComboBox = new javax.swing.JComboBox();
        commandOrJLabel = new javax.swing.JLabel();
        commandNoSelectionReplacementJComboBox = new javax.swing.JComboBox();
        commandReplaceJLabel3 = new javax.swing.JLabel();
		
		expansionJLabel.setDisplayedMnemonic('E');
        expansionJLabel.setLabelFor(abbrevsEditorJTextArea);
        expansionJLabel.setText("Expansion:");

        abbrevJLabel.setDisplayedMnemonic('b');
        abbrevJLabel.setLabelFor(abbrevJTextField);
        abbrevJLabel.setText("Abbreviation:");

        abbrevJTextField.setToolTipText("Enter the abbreviation");
        abbrevJTextField.setEnabled(true);

        abbrevsEditorJTextArea.setColumns(80);
        abbrevsEditorJTextArea.setRows(5);
        abbrevsEditorJTextArea.setTabSize(4);
        abbrevsEditorJTextArea.setEnabled(true);
        abbrevsEditorJScrollPane.setViewportView(abbrevsEditorJTextArea);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("When invoked as a command"));

        commandSelectionReplacementJComboBox.setModel(commandInputSelectionModel);
        commandSelectionReplacementJComboBox.setToolTipText("Select the area that should be replaced when abbreviation is inserted");
        commandSelectionReplacementJComboBox.setEnabled(false);
        commandSelectionReplacementJComboBox.setNextFocusableComponent(commandNoSelectionReplacementJComboBox);

        commandOrJLabel.setDisplayedMnemonic('o');
        commandOrJLabel.setLabelFor(commandNoSelectionReplacementJComboBox);
        commandOrJLabel.setText("or");

        commandNoSelectionReplacementJComboBox.setModel(commandInputModel);
        commandNoSelectionReplacementJComboBox.setToolTipText("Select the area that should be replaced when abbreviation is inserted");
        commandNoSelectionReplacementJComboBox.setEnabled(false);

        commandReplaceJLabel3.setDisplayedMnemonic('p');
        commandReplaceJLabel3.setLabelFor(commandSelectionReplacementJComboBox);
        commandReplaceJLabel3.setText("Replace:");
		
        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(commandReplaceJLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(commandSelectionReplacementJComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(commandOrJLabel)
                .add(4, 4, 4)
                .add(commandNoSelectionReplacementJComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(521, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(commandReplaceJLabel3)
                .add(commandSelectionReplacementJComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(commandOrJLabel)
                .add(commandNoSelectionReplacementJComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout abbrevsEditorJPanelLayout = new org.jdesktop.layout.GroupLayout(this);
        setLayout(abbrevsEditorJPanelLayout);
        abbrevsEditorJPanelLayout.setHorizontalGroup(
            abbrevsEditorJPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abbrevsEditorJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(abbrevsEditorJPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, abbrevsEditorJScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE)
                    .add(expansionJLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(abbrevsEditorJPanelLayout.createSequentialGroup()
                        .add(abbrevJLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(abbrevJTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)))
                .addContainerGap())
        );
        abbrevsEditorJPanelLayout.setVerticalGroup(
            abbrevsEditorJPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(abbrevsEditorJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(abbrevsEditorJPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(abbrevJLabel)
                    .add(abbrevJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expansionJLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(abbrevsEditorJScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addContainerGap())
        );

        expansionJLabel.getAccessibleContext().setAccessibleName("expansionJLabel");
        abbrevJLabel.getAccessibleContext().setAccessibleName("abbrevJLabel");
        abbrevJTextField.getAccessibleContext().setAccessibleName("abbrevJTextField");
	}
	
	 private javax.swing.JLabel abbrevJLabel;
	private javax.swing.JTextField abbrevJTextField;
	private javax.swing.JScrollPane abbrevsEditorJScrollPane;
	private javax.swing.JTextArea abbrevsEditorJTextArea;
	private javax.swing.JComboBox commandNoSelectionReplacementJComboBox;
	private javax.swing.JLabel commandOrJLabel;
	private javax.swing.JLabel commandReplaceJLabel3;
	private javax.swing.JComboBox commandSelectionReplacementJComboBox;
	private javax.swing.JLabel expansionJLabel;
	private javax.swing.JPanel jPanel1;
	
	private BindingGroup bindingGroup;
}
