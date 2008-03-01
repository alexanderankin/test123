/*
 * AbbrevsOptionPane.java
 *
 * Created on 27. januar 2007, 22:09
 */

package superabbrevs.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.gjt.sp.jedit.View;
import superabbrevs.model.Abbrev;
import superabbrevs.AbbrevsOptionPaneController;

/**
 *
 * @author  sune
 */
public class AbbrevsOptionPane extends JPanel {
    
    private ComboBoxModel inputModel = new DefaultComboBoxModel(
            new Abbrev.InputTypes[] {
        Abbrev.InputTypes.NO_INPUT,
        Abbrev.InputTypes.BUFFER,
        Abbrev.InputTypes.LINE,
        Abbrev.InputTypes.WORD,
        Abbrev.InputTypes.CHAR
    });
    
    private ComboBoxModel commandInputModel = new DefaultComboBoxModel(
            new Abbrev.InputTypes[] {
        Abbrev.InputTypes.NO_INPUT,
        Abbrev.InputTypes.BUFFER,
        Abbrev.InputTypes.LINE,
        Abbrev.InputTypes.WORD,
        Abbrev.InputTypes.CHAR
    });
    
    private ComboBoxModel commandInputSelectionModel = new DefaultComboBoxModel(
            new Abbrev.InputSelectionTypes[] {
        Abbrev.InputSelectionTypes.NO_INPUT,
        Abbrev.InputSelectionTypes.SELECTION,
        Abbrev.InputSelectionTypes.SELECTED_LINES
    });
    
    private ComboBoxModel commandReplaceModel = new DefaultComboBoxModel(
            new Abbrev.ReplacementTypes[] {
        Abbrev.ReplacementTypes.AT_CARET,
        Abbrev.ReplacementTypes.SELECTION,
        Abbrev.ReplacementTypes.SELECTED_LINES,
        Abbrev.ReplacementTypes.BUFFER,
        Abbrev.ReplacementTypes.LINE,
        Abbrev.ReplacementTypes.WORD,
        Abbrev.ReplacementTypes.CHAR
    });
    
    /** 
     * Creates new form AbbrevsOptionPane 
     */
    public AbbrevsOptionPane(View view, AbbrevsOptionPaneController controller) {
        this.mainPanel = this;
        this.controller = controller;
        this.view = view;
        
        abbrevsModel = new AbbrevsListModel(
                controller.loadsAbbrevs(getCurrentMode()));
        initComponents();
        
        // register actions
        ActionMap actionMap = getActionMap();
        actionMap.put(addAction.getValue(Action.NAME), addAction);
        actionMap.put(removeAction.getValue(Action.NAME), removeAction);
        actionMap.put(renameAction.getValue(Action.NAME), renameAction);
        
        // Bind keyboard shortcuts
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(
                KeyStroke.getKeyStroke("control N"), 
                addAction.getValue(Action.NAME));
        
        inputMap.put(
                KeyStroke.getKeyStroke("control R"), 
                renameAction.getValue(Action.NAME));
        
        inputMap.put(
                KeyStroke.getKeyStroke("DELETE"), 
                removeAction.getValue(Action.NAME));
                
        selectFirstAbbrev();
        
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                abbrevsJList.requestFocus();
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        abbrevsJPopupMenu = new javax.swing.JPopupMenu();
        addJMenuItem = new javax.swing.JMenuItem();
        removeAddJMenuItem = new javax.swing.JMenuItem();
        renameJMenuItem = new javax.swing.JMenuItem();
        abbrevsEditorJSplitPane = new javax.swing.JSplitPane();
        abbrevsJPanel = new javax.swing.JPanel();
        modesJComboBox = new javax.swing.JComboBox();
        addJButton = new javax.swing.JButton();
        removeJButton = new javax.swing.JButton();
        modeJLabel = new javax.swing.JLabel();
        abbrevsJLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        abbrevsJList = new javax.swing.JList();
        abbrevsEditorJPanel = new javax.swing.JPanel();
        expansionJLabel = new javax.swing.JLabel();
        abbrevJLabel = new javax.swing.JLabel();
        abbrevJTextField = new javax.swing.JTextField();
        abbrevsEditorJScrollPane = new javax.swing.JScrollPane();
        abbrevsEditorJTextArea = new javax.swing.JTextArea();
        inputJLabel = new javax.swing.JLabel();
        inputJComboBox = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        commandInputJLabel = new javax.swing.JLabel();
        commandSelectionInputJComboBox = new javax.swing.JComboBox();
        commandOrJLabel = new javax.swing.JLabel();
        commandNoSelectionJComboBox = new javax.swing.JComboBox();
        commandReplaceJLabel3 = new javax.swing.JLabel();
        commandReplaceJComboBox = new javax.swing.JComboBox();

        addJMenuItem.setAction(addAction);
        abbrevsJPopupMenu.add(addJMenuItem);

        removeAddJMenuItem.setAction(removeAction);
        abbrevsJPopupMenu.add(removeAddJMenuItem);

        renameJMenuItem.setAction(renameAction);
        abbrevsJPopupMenu.add(renameJMenuItem);

        setLayout(new java.awt.BorderLayout());

        abbrevsEditorJSplitPane.setDividerLocation(191);

        modesJComboBox.setModel(new DefaultComboBoxModel(controller.getModes()));
        modesJComboBox.setSelectedIndex(controller.getIndexOfCurrentMode());
        modesJComboBox.setNextFocusableComponent(abbrevsJList);
        modesJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modesJComboBoxActionPerformed(evt);
            }
        });

        addJButton.setAction(addAction);
        addJButton.setText("Add...");

        removeJButton.setAction(removeAction);
        removeJButton.setText("Remove");
        removeJButton.setEnabled(false);

        modeJLabel.setDisplayedMnemonic('M');
        modeJLabel.setLabelFor(modesJComboBox);
        modeJLabel.setText("Mode:");

        abbrevsJLabel.setDisplayedMnemonic('A');
        abbrevsJLabel.setLabelFor(abbrevsJList);
        abbrevsJLabel.setText("Abbreviations:");

        abbrevsJList.setModel(abbrevsModel);
        abbrevsJList.setNextFocusableComponent(addJButton);
        abbrevsJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                abbrevsJListMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                abbrevsJListMouseReleased(evt);
            }
        });
        abbrevsJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                abbrevsJListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(abbrevsJList);

        javax.swing.GroupLayout abbrevsJPanelLayout = new javax.swing.GroupLayout(abbrevsJPanel);
        abbrevsJPanel.setLayout(abbrevsJPanelLayout);
        abbrevsJPanelLayout.setHorizontalGroup(
            abbrevsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(abbrevsJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abbrevsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addGroup(abbrevsJPanelLayout.createSequentialGroup()
                        .addComponent(modeJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modesJComboBox, 0, 136, Short.MAX_VALUE))
                    .addComponent(abbrevsJLabel)
                    .addGroup(abbrevsJPanelLayout.createSequentialGroup()
                        .addComponent(addJButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeJButton)))
                .addContainerGap())
        );
        abbrevsJPanelLayout.setVerticalGroup(
            abbrevsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, abbrevsJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abbrevsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modeJLabel)
                    .addComponent(modesJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(abbrevsJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(abbrevsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeJButton)
                    .addComponent(addJButton))
                .addContainerGap())
        );

        addJButton.getAccessibleContext().setAccessibleName("addJButton");

        abbrevsEditorJSplitPane.setLeftComponent(abbrevsJPanel);

        expansionJLabel.setDisplayedMnemonic('E');
        expansionJLabel.setLabelFor(abbrevsEditorJTextArea);
        expansionJLabel.setText("Expansion:");

        abbrevJLabel.setDisplayedMnemonic('b');
        abbrevJLabel.setLabelFor(abbrevJTextField);
        abbrevJLabel.setText("Abbreviation:");

        abbrevJTextField.setToolTipText("Enter the abbreviation");
        abbrevJTextField.setEnabled(false);
        abbrevJTextField.setNextFocusableComponent(inputJComboBox);

        abbrevsEditorJTextArea.setColumns(20);
        abbrevsEditorJTextArea.setRows(5);
        abbrevsEditorJTextArea.setTabSize(4);
        abbrevsEditorJTextArea.setEnabled(false);
        abbrevsEditorJScrollPane.setViewportView(abbrevsEditorJTextArea);

        inputJLabel.setDisplayedMnemonic('I');
        inputJLabel.setLabelFor(inputJComboBox);
        inputJLabel.setText("Input:");

        inputJComboBox.setModel(inputModel);
        inputJComboBox.setToolTipText("Select the input that should be provided when the abbreviation is expanded");
        inputJComboBox.setEnabled(false);
        inputJComboBox.setNextFocusableComponent(commandSelectionInputJComboBox);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("When invoked as a command"));

        commandInputJLabel.setLabelFor(commandSelectionInputJComboBox);
        commandInputJLabel.setText("Input:");
        commandInputJLabel.setDisplayedMnemonicIndex(1);

        commandSelectionInputJComboBox.setModel(commandInputSelectionModel);
        commandSelectionInputJComboBox.setToolTipText("Select the input the should be provided when the abbreviation is inserted");
        commandSelectionInputJComboBox.setEnabled(false);
        commandSelectionInputJComboBox.setNextFocusableComponent(commandNoSelectionJComboBox);

        commandOrJLabel.setLabelFor(commandNoSelectionJComboBox);
        commandOrJLabel.setText("or");
        commandOrJLabel.setDisplayedMnemonicIndex(0);

        commandNoSelectionJComboBox.setModel(commandInputModel);
        commandNoSelectionJComboBox.setToolTipText("Select the input the should be provided when the abbreviation is inserted");
        commandNoSelectionJComboBox.setEnabled(false);
        commandNoSelectionJComboBox.setNextFocusableComponent(commandReplaceJComboBox);

        commandReplaceJLabel3.setLabelFor(commandReplaceJComboBox);
        commandReplaceJLabel3.setText("Replace:");
        commandReplaceJLabel3.setDisplayedMnemonicIndex(2);

        commandReplaceJComboBox.setModel(commandReplaceModel);
        commandReplaceJComboBox.setToolTipText("Select what should be replaced when the abbreviation is inserted");
        commandReplaceJComboBox.setEnabled(false);
        commandReplaceJComboBox.setNextFocusableComponent(abbrevsEditorJTextArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(commandReplaceJLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commandReplaceJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(commandInputJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commandSelectionInputJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commandOrJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commandNoSelectionJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(389, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(commandInputJLabel)
                    .addComponent(commandSelectionInputJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commandOrJLabel)
                    .addComponent(commandNoSelectionJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(commandReplaceJLabel3)
                    .addComponent(commandReplaceJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout abbrevsEditorJPanelLayout = new javax.swing.GroupLayout(abbrevsEditorJPanel);
        abbrevsEditorJPanel.setLayout(abbrevsEditorJPanelLayout);
        abbrevsEditorJPanelLayout.setHorizontalGroup(
            abbrevsEditorJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, abbrevsEditorJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abbrevsEditorJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(abbrevsEditorJScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, abbrevsEditorJPanelLayout.createSequentialGroup()
                        .addComponent(abbrevJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(abbrevJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(expansionJLabel, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        abbrevsEditorJPanelLayout.setVerticalGroup(
            abbrevsEditorJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(abbrevsEditorJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abbrevsEditorJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(abbrevJLabel)
                    .addComponent(inputJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputJLabel)
                    .addComponent(abbrevJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(expansionJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(abbrevsEditorJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                .addContainerGap())
        );

        expansionJLabel.getAccessibleContext().setAccessibleName("expansionJLabel");
        abbrevJLabel.getAccessibleContext().setAccessibleName("abbrevJLabel");
        abbrevJTextField.getAccessibleContext().setAccessibleName("abbrevJTextField");

        abbrevsEditorJSplitPane.setRightComponent(abbrevsEditorJPanel);

        add(abbrevsEditorJSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    private void abbrevsJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_abbrevsJListValueChanged
        int selection = getSelection();
        if(lastSelection != -1 && lastSelection != selection) {
            saveAbbrev(lastSelection);
        }
        loadAbbrev();
        lastSelection = selection;
    }//GEN-LAST:event_abbrevsJListValueChanged
    
    private void modesJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modesJComboBoxActionPerformed
        // remove the selection
        abbrevsJList.clearSelection();
        abbrevsModel = new AbbrevsListModel(controller.loadsAbbrevs(
                (String)modesJComboBox.getSelectedItem()));
        abbrevsJList.setModel(abbrevsModel);
        selectFirstAbbrev();
    }//GEN-LAST:event_modesJComboBoxActionPerformed

    private void abbrevsJListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_abbrevsJListMouseReleased
        maybeShowPopup(evt);
    }//GEN-LAST:event_abbrevsJListMouseReleased

    private void abbrevsJListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_abbrevsJListMousePressed
        int selection = 
                abbrevsJList.locationToIndex(new Point(evt.getX(), evt.getY()));
        setSelection(selection);
        maybeShowPopup(evt);
    }//GEN-LAST:event_abbrevsJListMousePressed
            
    private void maybeShowPopup(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            abbrevsJPopupMenu.show(evt.getComponent(),
                       evt.getX(), evt.getY());
        }
    }
    
    private int getSelection() {
        return abbrevsJList.getSelectedIndex();
    }
    
    private void setSelection(int selection) {
        if(selection != -1) {
            abbrevsJList.setSelectedIndex(selection);
        } else {
            abbrevsJList.clearSelection();
        }
    }
    
    /**
     * Copy all the elements in the abbreviation model to an arraylist and 
     * use the controller to save them.
     */
    public void save() {
        int selection = getSelection();
        if(selection != -1) {
            saveAbbrev(selection);
        }
        
        try {
            controller.saveAbbrevs();
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Could not write the file abbreviation file", 
                    "Write to file error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveAbbrev(int selection) {
        if(selection  != -1) {
            Abbrev abbrev = abbrevsModel.get(selection);
            
            abbrev.abbreviation = abbrevJTextField.getText();
            abbrev.expansion = abbrevsEditorJTextArea.getText();
            abbrev.inputType = (Abbrev.InputTypes) 
                    inputJComboBox.getSelectedItem();
            abbrev.whenInvokedAsCommand.inputType = (Abbrev.InputTypes) 
                    commandNoSelectionJComboBox.getSelectedItem();
            abbrev.whenInvokedAsCommand.inputSelectionType = (Abbrev.InputSelectionTypes)
                    commandSelectionInputJComboBox.getSelectedItem();
            abbrev.whenInvokedAsCommand.replacementType = (Abbrev.ReplacementTypes)
                    commandReplaceJComboBox.getSelectedItem();
        }
    }
    
    private void loadAbbrev() {
        int selection = getSelection();
        
        if(selection != -1) {
            Abbrev abbrev = abbrevsModel.get(selection);
            setAbbrevPanelEnabled(true);
            setAbbrevValues(abbrev);
        } else {
            Abbrev clear = new Abbrev("","","");
            setAbbrevValues(clear);
            setAbbrevPanelEnabled(false);
        }
    }
    
    private void setAbbrevPanelEnabled(boolean enabled) {
        abbrevJTextField.setEnabled(enabled);
        inputJComboBox.setEnabled(enabled);
        abbrevsEditorJTextArea.setEnabled(enabled);
        removeAction.setEnabled(enabled);
        commandSelectionInputJComboBox.setEnabled(enabled);
        commandNoSelectionJComboBox.setEnabled(enabled);
        commandReplaceJComboBox.setEnabled(enabled);
    }
    
    private void setAbbrevValues(Abbrev abbrev) {

        abbrevJTextField.setText(abbrev.abbreviation);
        abbrevsEditorJTextArea.setText(abbrev.expansion);
        inputJComboBox.setSelectedItem(abbrev.inputType);
        commandSelectionInputJComboBox.setSelectedItem(abbrev.whenInvokedAsCommand.inputSelectionType);
        commandNoSelectionJComboBox.setSelectedItem(abbrev.whenInvokedAsCommand.inputType);
        commandReplaceJComboBox.setSelectedItem(abbrev.whenInvokedAsCommand.replacementType);
    }

    private String getCurrentMode() {
        String[] modes = controller.getModes();
        return modes[controller.getIndexOfCurrentMode()];
    }

    private void selectFirstAbbrev() {
        if (abbrevsModel.getSize() != 0) {
            abbrevsJList.setSelectedIndex(0);
        } else {
            // If the list is empty clear the selection
            abbrevsJList.clearSelection();
        }
    }
    
    private View view;
    private AbbrevsOptionPaneController controller;
    private AbbrevsListModel abbrevsModel;
    private AbbrevsOptionPane mainPanel;
    private AddAbbrevAction addAction = new AddAbbrevAction(); 
    private RemoveAbbrevAction removeAction = new RemoveAbbrevAction(); 
    private RenameAbbrevAction renameAction = new RenameAbbrevAction(); 
    private int lastSelection = -1;
    
    private class AddAbbrevAction extends AbstractAction {
        public AddAbbrevAction() {
            putValue(Action.NAME, "Add...");
            putValue(Action.LARGE_ICON_KEY, 
                    new javax.swing.ImageIcon(
                    getClass().getResource("/superabbrevs/gui/icons/Plus.png")));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
            putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
            putValue(Action.SHORT_DESCRIPTION, "Add a new abbreviation (Ctrl+N)");
            setEnabled(true);
        }
        
        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog(mainPanel,
                "Enter the name of the new abbreviation");
            if(name != null) {
                abbrevsJList.clearSelection();
                int selection = abbrevsModel.add(name);
                setSelection(selection);
                abbrevJTextField.grabFocus();
                abbrevsJList.ensureIndexIsVisible(selection);
            }
        }
    }
    
    private class RemoveAbbrevAction extends AbstractAction {
        public RemoveAbbrevAction() {
            putValue(Action.NAME, "Remove...");
            putValue(Action.LARGE_ICON_KEY, 
                    new javax.swing.ImageIcon(
                    getClass().getResource("/superabbrevs/gui/icons/Minus.png")));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
            putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 0);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));
            putValue(Action.SHORT_DESCRIPTION, "Remove the selected abbreviation (DELETE)");
            setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.showConfirmDialog(mainPanel,
                    "Do you really wish to delete the selected abbreviation",
                    "Delete Abbreviation", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                int selection = getSelection();
                abbrevsJList.clearSelection();
                int newSelection = abbrevsModel.remove(selection);

                setSelection(newSelection);
                abbrevsJList.ensureIndexIsVisible(newSelection);
            }
        }
    }
    
    private class RenameAbbrevAction extends AbstractAction {
        public RenameAbbrevAction() {
            putValue(Action.NAME, "Rename...");
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
            putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control R"));
            putValue(Action.SHORT_DESCRIPTION, "Rename the selected abbreviation (CTRL R)");
            setEnabled(true);
        }
        
        public void actionPerformed(ActionEvent e) {
            int selection = getSelection();
            Abbrev abbrev = abbrevsModel.get(selection);
            
            String name = JOptionPane.showInputDialog(mainPanel,
                "Enter the new name for the abbreviation", abbrev.name);
            if(name != null && !name.equals(abbrev.name)) { 
                abbrevsJList.clearSelection();
                
                abbrev.name = name;
                selection = abbrevsModel.sort(selection);
                setSelection(selection);
                abbrevsJList.ensureIndexIsVisible(selection);
                abbrevsJList.grabFocus();
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abbrevJLabel;
    private javax.swing.JTextField abbrevJTextField;
    private javax.swing.JPanel abbrevsEditorJPanel;
    private javax.swing.JScrollPane abbrevsEditorJScrollPane;
    private javax.swing.JSplitPane abbrevsEditorJSplitPane;
    private javax.swing.JTextArea abbrevsEditorJTextArea;
    private javax.swing.JLabel abbrevsJLabel;
    private javax.swing.JList abbrevsJList;
    private javax.swing.JPanel abbrevsJPanel;
    private javax.swing.JPopupMenu abbrevsJPopupMenu;
    private javax.swing.JButton addJButton;
    private javax.swing.JMenuItem addJMenuItem;
    private javax.swing.JLabel commandInputJLabel;
    private javax.swing.JComboBox commandNoSelectionJComboBox;
    private javax.swing.JLabel commandOrJLabel;
    private javax.swing.JComboBox commandReplaceJComboBox;
    private javax.swing.JLabel commandReplaceJLabel3;
    private javax.swing.JComboBox commandSelectionInputJComboBox;
    private javax.swing.JLabel expansionJLabel;
    private javax.swing.JComboBox inputJComboBox;
    private javax.swing.JLabel inputJLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel modeJLabel;
    private javax.swing.JComboBox modesJComboBox;
    private javax.swing.JMenuItem removeAddJMenuItem;
    private javax.swing.JButton removeJButton;
    private javax.swing.JMenuItem renameJMenuItem;
    // End of variables declaration//GEN-END:variables
}
