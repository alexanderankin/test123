/*
 * CipherPlugin - A jEdit plugin as framework for cipher implementations
 * :tabSize=4:indentSize=4:noTabs=true:
 *
 * Copyright (C) 2007 Björn "Vampire" Kautler
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

package cipher;

import java.awt.Dimension;
import java.awt.Insets;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.jedit.gui.ExtendedGridLayout;
import org.gjt.sp.jedit.gui.ExtendedGridLayoutConstraints;

import static java.awt.event.ItemEvent.SELECTED;

/**
 * The content for the option pane
 * where you can configure the default cipher.
 * 
 * @author Björn "Vampire" Kautler
 * @since CipherPlugin 0.1
 */
public class CipherOptionPane extends AbstractOptionPane {
    /**
     * Whether to apply distances to borders or not.
     * e. g. in the option pane it should be applied,
     * for usage in the {@code DefaultCipherDialog} it
     * should not.
     */
    private boolean insets;
    
    /**
     * Whether the pane is used for choosing
     * the <strong>default</strong> cipher,
     * e. g. in the option pane this is true,
     * for usage in the {@code FileCrypter} it
     * is not.
     */
    private boolean asDefault;
    
    private JLabel cipherLabel;
    private JComboBox cipherComboBox;
    private JLabel descriptionLabel;
    private JTextArea descriptionTextArea;
    private JScrollPane descriptionScrollPane;
    
    /**
     * Constructs a new {@code CipherOptionPane} without applying insets.
     */
    public CipherOptionPane() {
        this(false,true);
    }
    
    /**
     * Constructs a new {@code CipherOptionPane}.
     * 
     * @param insets Whether to apply insets to the borders or not.
     * @param asDefault Whether the pane is used for choosing the <strong>default</strong> cipher
     */
    public CipherOptionPane(boolean insets, boolean asDefault) {
        super("cipher");
        this.insets = insets;
        this.asDefault = asDefault;
    }
    
    /**
     * This method should create and arrange the components of the option pane
     * and initialize the option data displayed to the user. This method
     * is called when the option pane is first displayed, and is not
     * called again for the lifetime of the object.
     */
    @Override
    protected void _init() {
        cipherLabel = new JLabel(jEdit.getProperty(asDefault ? "options.cipher.default-cipher.label" : "options.cipher.cipher.label"),JLabel.RIGHT);
        cipherLabel.setAlignmentX(RIGHT_ALIGNMENT);
        
        String[] availableCiphersNames = CipherPlugin.getAvailableCiphersNames();
        Arrays.sort(availableCiphersNames,String.CASE_INSENSITIVE_ORDER);
        String defaultCipherName = jEdit.getProperty("options.cipher.default-cipher");
        Vector<String> availableCiphersNamesVector = new Vector<String>(Arrays.asList(availableCiphersNames));
        if (0 == availableCiphersNamesVector.size()) {
            availableCiphersNamesVector.add(0,jEdit.getProperty("options.cipher.no-ciphers-installed.item"));
        } else if (!availableCiphersNamesVector.contains(defaultCipherName)) {
            availableCiphersNamesVector.add(0,jEdit.getProperty("options.cipher.select-cipher.item"));
        }
        cipherComboBox = new JComboBox(availableCiphersNamesVector);
        cipherComboBox.setSelectedItem(defaultCipherName);
        Dimension dim = cipherComboBox.getMaximumSize();
        dim.height = cipherComboBox.getPreferredSize().height;
        cipherComboBox.setMaximumSize(dim);
        cipherComboBox.addItemListener(new CipherDescriptionChanger());
        cipherLabel.setLabelFor(cipherComboBox);
        
        descriptionLabel = new JLabel(jEdit.getProperty("options.cipher.cipher-description.label"),JLabel.RIGHT);
        descriptionLabel.setAlignmentX(RIGHT_ALIGNMENT);
        descriptionLabel.setAlignmentY(TOP_ALIGNMENT);
        
        String description = jEdit.getProperty("cipher-description." + cipherComboBox.getSelectedItem());
        descriptionTextArea = new JTextArea(null == description ? "" : description);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setEditable(false);
        descriptionLabel.setLabelFor(descriptionTextArea);
        
        descriptionScrollPane = new JScrollPane(descriptionTextArea);
        
        setLayout(new ExtendedGridLayout(5,5,insets ? new Insets(5,5,5,5) : new Insets(0,0,0,0)));
        add(cipherLabel,null);
        add(cipherComboBox,null);
        add(descriptionLabel,new ExtendedGridLayoutConstraints(1,descriptionLabel));
        add(descriptionScrollPane,new ExtendedGridLayoutConstraints(1,descriptionScrollPane));
    }
    
    /**
     * Returns the chosen cipher or {@code null}
     * if no cipher was chosen or no cipher is installed.
     * 
     * @return The chosen cipher or {@code null}
     *         if no cipher was chosen or no cipher is installed
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the cipher name got requested it should be used")
    public String getCipherName() {
        Object currentlySelectedCipherName = cipherComboBox.getSelectedItem();
        if (jEdit.getProperty("options.cipher.select-cipher.item").equals(currentlySelectedCipherName) ||
            jEdit.getProperty("options.cipher.no-ciphers-installed.item").equals(currentlySelectedCipherName)) {
            return null;
        }
        return (String)currentlySelectedCipherName;
    }
    
    /**
     * Called when the options dialog's "ok" button is clicked.
     * This should save any properties being edited in this option
     * pane.
     */
    @Override
    protected void _save() {
        String currentlySelectedCipherName = getCipherName();
        if (asDefault && (null != currentlySelectedCipherName)) {
            jEdit.setProperty("options.cipher.default-cipher",currentlySelectedCipherName);
            jEdit.propertiesChanged();
            jEdit.saveSettings();
        }
    }
    
    /**
     * Changes the displayed description if the chosen cipher changes.
     * 
     * @author Björn "Vampire" Kautler
     * @since CipherPlugin 0.1
     */
    private class CipherDescriptionChanger implements ItemListener {
        /**
         * Invoked when an item has been selected or deselected by the user.
         * The code written for this method performs the operations
         * that need to occur when an item is selected (or deselected).
         * 
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        public void itemStateChanged(@NonNull ItemEvent ie) {
            if (SELECTED == ie.getStateChange()) {
                Object currentlySelectedCipherName = ie.getItem();
                String description = jEdit.getProperty("cipher-description." + currentlySelectedCipherName);
                descriptionTextArea.setText(null == description ? "" : description);
            }
        }
    }
}
