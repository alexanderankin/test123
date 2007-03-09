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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import javax.swing.border.LineBorder;

import bsh.NameSpace;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.jedit.gui.BeanShellErrorDialog;
import org.gjt.sp.jedit.gui.ExtendedGridLayout;
import org.gjt.sp.jedit.gui.ExtendedGridLayoutConstraints;

import org.gjt.sp.jedit.msg.PluginUpdate;

import org.gjt.sp.util.Log;

import static java.awt.Color.BLACK;

import static java.awt.event.ItemEvent.SELECTED;

import static javax.swing.SwingConstants.VERTICAL;

import static org.gjt.sp.jedit.gui.DockableWindowManager.BOTTOM;
import static org.gjt.sp.jedit.gui.DockableWindowManager.FLOATING;
import static org.gjt.sp.jedit.gui.DockableWindowManager.LEFT;
import static org.gjt.sp.jedit.gui.DockableWindowManager.RIGHT;
import static org.gjt.sp.jedit.gui.DockableWindowManager.TOP;

import static org.gjt.sp.jedit.msg.PluginUpdate.LOADED;
import static org.gjt.sp.jedit.msg.PluginUpdate.UNLOADED;

/**
 * A dockable for decrypting and encrypting
 * strings and data with the installed
 * {@code Cipher} implementations.
 * 
 * @author Björn "Vampire" Kautler
 * @since CipherPlugin 0.1
 */
public class CipherDockable extends JPanel implements EBComponent {
    /**
     * Whether to use vertical layout, if docked LEFT, RIGHT or FLOATING,
     * or horizontal layout if docked TOP or BOTTOM.
     */
    private boolean verticalLayout;
    
    private JLabel rawDataLabel;
    private JTextArea rawDataTextArea;
    private JScrollPane rawDataScrollPane;
    private JRadioButton stringRawDataRadioButton;
    private JRadioButton beanshellRawDataRadioButton;
    
    private JLabel encryptedDataLabel;
    private JTextArea encryptedDataTextArea;
    private JScrollPane encryptedDataScrollPane;
    private JRadioButton stringEncryptedDataRadioButton;
    private JRadioButton beanshellEncryptedDataRadioButton;
    
    private JLabel entropyLabel;
    private JTextArea entropyTextArea;
    private JScrollPane entropyScrollPane;
    private JRadioButton stringEntropyRadioButton;
    private JRadioButton beanshellEntropyRadioButton;
    
    private JLabel additionalInformationLabel;
    private JPanel additionalInformationRequesterPanel;
    @SuppressWarnings(value = "SE_BAD_FIELD",
                      justification = "The class will not be serialized")
    private AdditionalInformationRequester additionalInformationRequester;
    
    private JComboBox cipherComboBox;
    private JButton encryptButton;
    private JButton decryptButton;
    
    /**
     * Constructs a new {@code CipherDockable} for docking at
     * the specified {@code position}.
     * 
     * @param position The position where the dockable gets docked.
     *                 One of {@code BOTTOM}, {@code FLOATING},
     *                 {@code LEFT}, {@code RIGHT} or {@code TOP}
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#BOTTOM
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#FLOATING
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#LEFT
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#RIGHT
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#TOP
     * @throws IllegalArgumentException If position is not one of {@code BOTTOM},
     *                                  {@code FLOATING}, {@code LEFT},
     *                                  {@code RIGHT} or {@code TOP}
     */
    private CipherDockable(@NonNull String position) {
        if (LEFT.equals(position) ||
            RIGHT.equals(position) ||
            FLOATING.equals(position)) {
            verticalLayout = true;
        } else if (TOP.equals(position) ||
                   BOTTOM.equals(position)) {
            verticalLayout = false;
        } else {
            throw new IllegalArgumentException("Illegal dockable position: \""+position+'"');
        }
        
        rawDataLabel = new JLabel(jEdit.getProperty("cipher.raw-data.label"));
        
        rawDataTextArea = new JTextArea();
        rawDataTextArea.setLineWrap(true);
        rawDataTextArea.setWrapStyleWord(true);
        rawDataLabel.setLabelFor(rawDataTextArea);
        
        rawDataScrollPane = new JScrollPane(rawDataTextArea);
        Dimension bigDimension = new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE);
        rawDataScrollPane.setPreferredSize(bigDimension);
        
        stringRawDataRadioButton = new JRadioButton(jEdit.getProperty("cipher.string.label"),true);
        
        beanshellRawDataRadioButton = new JRadioButton(jEdit.getProperty("cipher.beanshell.label"));
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(stringRawDataRadioButton);
        buttonGroup.add(beanshellRawDataRadioButton);
        
        encryptedDataLabel = new JLabel(jEdit.getProperty("cipher.encrypted-data.label"));
        
        encryptedDataTextArea = new JTextArea();
        encryptedDataTextArea.setLineWrap(true);
        encryptedDataTextArea.setWrapStyleWord(true);
        encryptedDataLabel.setLabelFor(encryptedDataTextArea);
        
        encryptedDataScrollPane = new JScrollPane(encryptedDataTextArea);
        encryptedDataScrollPane.setPreferredSize(bigDimension);
        
        stringEncryptedDataRadioButton = new JRadioButton(jEdit.getProperty("cipher.string.label"),true);
        
        beanshellEncryptedDataRadioButton = new JRadioButton(jEdit.getProperty("cipher.beanshell.label"));
        
        buttonGroup = new ButtonGroup();
        buttonGroup.add(stringEncryptedDataRadioButton);
        buttonGroup.add(beanshellEncryptedDataRadioButton);
        
        entropyLabel = new JLabel(jEdit.getProperty("cipher.entropy.label"));
        
        entropyTextArea = new JTextArea();
        entropyTextArea.setLineWrap(true);
        entropyTextArea.setWrapStyleWord(true);
        entropyLabel.setLabelFor(entropyTextArea);
        
        entropyScrollPane = new JScrollPane(entropyTextArea);
        entropyScrollPane.setPreferredSize(bigDimension);
        
        stringEntropyRadioButton = new JRadioButton(jEdit.getProperty("cipher.string.label"),true);
        
        beanshellEntropyRadioButton = new JRadioButton(jEdit.getProperty("cipher.beanshell.label"));
        
        buttonGroup = new ButtonGroup();
        buttonGroup.add(stringEntropyRadioButton);
        buttonGroup.add(beanshellEntropyRadioButton);
        
        additionalInformationLabel = new JLabel(jEdit.getProperty("cipher.additional-information.label"));
        
        additionalInformationRequesterPanel = new JPanel(new ExtendedGridLayout(5,5,new Insets(5,5,5,5)));
        additionalInformationRequesterPanel.setBorder(new LineBorder(BLACK));
        additionalInformationRequesterPanel.setPreferredSize(bigDimension);
        additionalInformationRequesterPanel.setMaximumSize(entropyScrollPane.getMaximumSize());
        additionalInformationLabel.setLabelFor(additionalInformationRequesterPanel);
        
        cipherComboBox = new JComboBox();
        Dimension dim = cipherComboBox.getMaximumSize();
        dim.height = cipherComboBox.getPreferredSize().height;
        cipherComboBox.setMaximumSize(dim);
        
        encryptButton = new JButton(jEdit.getProperty("cipher.encrypt.label"));
        dim = encryptButton.getMaximumSize();
        dim.width = Integer.MAX_VALUE;
        encryptButton.setMaximumSize(dim);
        
        decryptButton = new JButton(jEdit.getProperty("cipher.decrypt.label"));
        dim = decryptButton.getMaximumSize();
        dim.width = Integer.MAX_VALUE;
        decryptButton.setMaximumSize(dim);
        
        updateAvailableCiphers();
        Cipher selectedCipher = CipherPlugin.getCipher((String)cipherComboBox.getSelectedItem());
        if (null != selectedCipher) {
            additionalInformationRequester = selectedCipher.getAdditionalInformationRequester();
            if (null != additionalInformationRequester) {
                additionalInformationRequesterPanel.add(additionalInformationRequester.getComponent());
            }
        }
        
        setLayout(new ExtendedGridLayout(5,5,new Insets(5,5,5,5)));
        
        if (verticalLayout) {
            add(rawDataLabel,new ExtendedGridLayoutConstraints(0,2,1,rawDataLabel));
            add(rawDataScrollPane,new ExtendedGridLayoutConstraints(1,2,1,rawDataScrollPane));
            add(stringRawDataRadioButton,new ExtendedGridLayoutConstraints(2,stringRawDataRadioButton));
            add(beanshellRawDataRadioButton,new ExtendedGridLayoutConstraints(2,beanshellRawDataRadioButton));
            
            JSeparator separator = new JSeparator();
            add(separator,new ExtendedGridLayoutConstraints(3,2,1,separator));
            
            add(encryptedDataLabel,new ExtendedGridLayoutConstraints(4,2,1,encryptedDataLabel));
            add(encryptedDataScrollPane,new ExtendedGridLayoutConstraints(5,2,1,encryptedDataScrollPane));
            add(stringEncryptedDataRadioButton,new ExtendedGridLayoutConstraints(6,stringEncryptedDataRadioButton));
            add(beanshellEncryptedDataRadioButton,new ExtendedGridLayoutConstraints(6,beanshellEncryptedDataRadioButton));
            
            separator = new JSeparator();
            add(separator,new ExtendedGridLayoutConstraints(7,2,1,separator));
            
            add(entropyLabel,new ExtendedGridLayoutConstraints(8,2,1,entropyLabel));
            add(entropyScrollPane,new ExtendedGridLayoutConstraints(9,2,1,entropyScrollPane));
            add(stringEntropyRadioButton,new ExtendedGridLayoutConstraints(10,stringEntropyRadioButton));
            add(beanshellEntropyRadioButton,new ExtendedGridLayoutConstraints(10,beanshellEntropyRadioButton));
            
            separator = new JSeparator();
            add(separator,new ExtendedGridLayoutConstraints(11,2,1,separator));
            
            add(additionalInformationLabel,new ExtendedGridLayoutConstraints(12,2,1,additionalInformationLabel));
            add(additionalInformationRequesterPanel,new ExtendedGridLayoutConstraints(13,2,1,additionalInformationRequesterPanel));
            
            add(cipherComboBox,new ExtendedGridLayoutConstraints(14,2,1,cipherComboBox));
            add(encryptButton,new ExtendedGridLayoutConstraints(15,encryptButton));
            add(decryptButton,new ExtendedGridLayoutConstraints(15,decryptButton));
        } else {
            add(rawDataLabel,new ExtendedGridLayoutConstraints(0,2,1,rawDataLabel));
            add(rawDataScrollPane,new ExtendedGridLayoutConstraints(1,2,1,rawDataScrollPane));
            add(stringRawDataRadioButton,new ExtendedGridLayoutConstraints(2,stringRawDataRadioButton));
            add(beanshellRawDataRadioButton,new ExtendedGridLayoutConstraints(2,beanshellRawDataRadioButton));
            
            JSeparator separator = new JSeparator(VERTICAL);
            add(separator,new ExtendedGridLayoutConstraints(0,1,3,separator));
            
            add(encryptedDataLabel,new ExtendedGridLayoutConstraints(0,2,1,encryptedDataLabel));
            add(encryptedDataScrollPane,new ExtendedGridLayoutConstraints(1,2,1,encryptedDataScrollPane));
            add(stringEncryptedDataRadioButton,new ExtendedGridLayoutConstraints(2,stringEncryptedDataRadioButton));
            add(beanshellEncryptedDataRadioButton,new ExtendedGridLayoutConstraints(2,beanshellEncryptedDataRadioButton));
            
            separator = new JSeparator(VERTICAL);
            add(separator,new ExtendedGridLayoutConstraints(0,1,3,separator));
            
            add(entropyLabel,new ExtendedGridLayoutConstraints(0,2,1,entropyLabel));
            add(entropyScrollPane,new ExtendedGridLayoutConstraints(1,2,1,entropyScrollPane));
            add(stringEntropyRadioButton,new ExtendedGridLayoutConstraints(2,stringEntropyRadioButton));
            add(beanshellEntropyRadioButton,new ExtendedGridLayoutConstraints(2,beanshellEntropyRadioButton));
            
            separator = new JSeparator(VERTICAL);
            add(separator,new ExtendedGridLayoutConstraints(0,1,3,separator));
            
            add(additionalInformationLabel,new ExtendedGridLayoutConstraints(0,1,1,additionalInformationLabel));
            add(additionalInformationRequesterPanel,new ExtendedGridLayoutConstraints(1,1,2,additionalInformationRequesterPanel));
            
            add(cipherComboBox,new ExtendedGridLayoutConstraints(0,cipherComboBox));
            add(encryptButton,new ExtendedGridLayoutConstraints(1,encryptButton));
            add(decryptButton,new ExtendedGridLayoutConstraints(2,decryptButton));
        }
    }
    
    /**
     * Initializes this {@code CipherDockable}.
     */
    private void init() {
        Crypter crypter = new Crypter();
        encryptButton.addActionListener(crypter);
        decryptButton.addActionListener(crypter);
        cipherComboBox.addItemListener(new AdditionalInformationRequesterChanger());
    }
    
    /**
     * Constructs a new {@code CipherDockable} for docking at
     * the specified {@code position} and initializes it.
     * 
     * @param position The position where the dockable gets docked.
     *                 One of {@code BOTTOM}, {@code FLOATING},
     *                 {@code LEFT}, {@code RIGHT} or {@code TOP}
     * @return The newly created {@code CipherDockable}
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#BOTTOM
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#FLOATING
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#LEFT
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#RIGHT
     * @see org.gjt.sp.jedit.gui.DockableWindowManager#TOP
     * @throws IllegalArgumentException If position is not one of {@code BOTTOM},
     *                                  {@code FLOATING}, {@code LEFT},
     *                                  {@code RIGHT} or {@code TOP}
     */
    @NonNull
    public static CipherDockable newInstance(String position) {
        CipherDockable instance = new CipherDockable(position);
        instance.init();
        return instance;
    }
    
    /**
     * Handles a message sent on the EditBus.
     *
     * This method must specify the type of responses the plugin will have
     * for various subclasses of the {@link EBMessage} class. Typically
     * this is done with one or more <code>if</code> blocks that test
     * whether the message is an instance of a derived message class in
     * which the component has an interest. For example:
     *
     * <pre> if(msg instanceof BufferUpdate) {
     *     // a buffer's state has changed!
     * }
     * else if(msg instanceof ViewUpdate) {
     *     // a view's state has changed!
     * }
     * // ... and so on</pre>
     * 
     * This implementation updates the {@code JComboBox} of
     * installed ciphers if a plugin gets loaded or unloaded.
     * 
     * @param message The message
     * @see org.gjt.sp.jedit.EBComponent#handleMessage(org.gjt.sp.jedit.EBMessage)
     */
    public void handleMessage(@NonNull EBMessage message) {
        if (message instanceof PluginUpdate) {
            PluginUpdate puMessage = (PluginUpdate)message;
            if (!puMessage.isExiting()) {
                Object what = puMessage.getWhat();
                if ((LOADED == what) || (UNLOADED == what)) {
                    updateAvailableCiphers();
                }
            }
        }
    }
    
    /**
     * Updates the {@code JComboBox} of installed ciphers.
     */
    private void updateAvailableCiphers() {
        String[] availableCiphersNames = CipherPlugin.getAvailableCiphersNames();
        Arrays.sort(availableCiphersNames,String.CASE_INSENSITIVE_ORDER);
        List<String> availableCiphersNamesList = new ArrayList<String>(Arrays.asList(availableCiphersNames));
        if (0 == availableCiphersNamesList.size()) {
            availableCiphersNamesList.add(0,jEdit.getProperty("options.cipher.no-ciphers-installed.item"));
            encryptButton.setEnabled(false);
            decryptButton.setEnabled(false);
        } else {
            encryptButton.setEnabled(true);
            decryptButton.setEnabled(true);
        }
        Object lastSelected = cipherComboBox.getSelectedItem();
        Object toSelect;
        if (availableCiphersNamesList.contains(lastSelected)) {
            toSelect = lastSelected;
        } else {
            toSelect = jEdit.getProperty("options.cipher.default-cipher");
        }
        cipherComboBox.removeAllItems();
        for (String cipherName : availableCiphersNamesList) {
            cipherComboBox.addItem(cipherName);
        }
        cipherComboBox.setSelectedItem(toSelect);
    }
    
    /**
     * Notifies this component that it now has a parent component.
     * When this method is invoked, the chain of parent components is
     * set up with <code>KeyboardAction</code> event listeners.
     * 
     * This implementation adds this component to the {@code EditBus}
     * and updates the the {@code JComboBox} of installed ciphers.
     * 
     * @see javax.swing.JComponent#addNotify()
     */
    @Override
    public void addNotify() {
        super.addNotify();
        EditBus.addToBus(this);
        updateAvailableCiphers();
    }
    
    /**
     * Notifies this component that it no longer has a parent component.
     * When this method is invoked, any <code>KeyboardAction</code>s
     * set up in the the chain of parent components are removed.
     * 
     * This implementation removes this component from the {@code EditBus}.
     * 
     * @see javax.swing.JComponent#removeNotify()
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        EditBus.removeFromBus(this);
    }
    
    /**
     * Changes the displayed additional information requester
     * if the chosen cipher changes.
     * 
     * @author Björn "Vampire" Kautler
     * @since CipherPlugin 0.1
     */
    private class AdditionalInformationRequesterChanger implements ItemListener {
        /**
         * Invoked when an item has been selected or deselected by the user.
         * The code written for this method performs the operations
         * that need to occur when an item is selected (or deselected).
         * 
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        public void itemStateChanged(@NonNull ItemEvent ie) {
            if (SELECTED == ie.getStateChange()) {
                String currentlySelectedCipherName = (String)cipherComboBox.getSelectedItem();
                cipherComboBox.setToolTipText(jEdit.getProperty("cipher-description." + currentlySelectedCipherName));
                Cipher selectedCipher = CipherPlugin.getCipher(currentlySelectedCipherName);
                if (null == selectedCipher) {
                    additionalInformationRequesterPanel.removeAll();
                    additionalInformationRequester = null;
                } else {
                    additionalInformationRequesterPanel.removeAll();
                    additionalInformationRequester = selectedCipher.getAdditionalInformationRequester();
                    if (null != additionalInformationRequester) {
                        additionalInformationRequesterPanel.add(additionalInformationRequester.getComponent());
                    }
                }
                additionalInformationRequesterPanel.repaint();
            }
        }
    }
    
    /**
     * Decrypts or encrypts the given data if one of the
     * buttons got pressed.
     * 
     * @author Björn "Vampire" Kautler
     * @since CipherPlugin 0.1
     */
    private class Crypter implements ActionListener {
        /**
         * Invoked when an action occurs.
         * 
         * This implementation decrypts or encrypts the given data.
         * 
         * @param ae The event that occured
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(@NonNull ActionEvent ae) {
            Object source = ae.getSource();
            boolean beanshellError = false;
            
            String data;
            boolean beanshellDataRadioButtonIsSelected;
            String errorPropertiesName;
            if (encryptButton == source) {
                data = rawDataTextArea.getText();
                beanshellDataRadioButtonIsSelected = beanshellRawDataRadioButton.isSelected();
                errorPropertiesName = "cipher.error.beanshell-raw-data";
            } else {
                data = encryptedDataTextArea.getText();
                beanshellDataRadioButtonIsSelected = beanshellEncryptedDataRadioButton.isSelected();
                errorPropertiesName = "cipher.error.beanshell-encrypted-data";
            }
            String stringData = null;
            byte[] byteArrayData = null;
            if (beanshellDataRadioButtonIsSelected) {
                NameSpace nameSpace = new NameSpace(BeanShell.getNameSpace(),"Cipher Data Evaluation");
                try {
                    Object objectData = BeanShell._eval(jEdit.getActiveView(),nameSpace,data);
                    if (objectData instanceof String) {
                        stringData = (String)objectData;
                    } else if (objectData instanceof byte[]) {
                        byteArrayData = (byte[])objectData;
                    } else if (null != objectData) {
                        beanshellError = true;
                        GUIUtilities.error(CipherDockable.this,errorPropertiesName,null);
                    }
                } catch (Exception e) {
                    Log.log(Log.ERROR,Crypter.class,e);
                    beanshellError = true;
                    new BeanShellErrorDialog(jEdit.getActiveView(),e);
                }
            } else {
                stringData = data;
            }
            
            String entropy = entropyTextArea.getText();
            String stringEntropy = null;
            byte[] byteArrayEntropy = null;
            if (beanshellEntropyRadioButton.isSelected()) {
                NameSpace nameSpace = new NameSpace(BeanShell.getNameSpace(),"Cipher Entropy Evaluation");
                try {
                    Object objectEntropy = BeanShell._eval(jEdit.getActiveView(),nameSpace,entropy);
                    if (objectEntropy instanceof String) {
                        stringEntropy = (String)objectEntropy;
                    } else if (objectEntropy instanceof byte[]) {
                        byteArrayEntropy = (byte[])objectEntropy;
                    } else if (null != objectEntropy) {
                        beanshellError = true;
                        GUIUtilities.error(CipherDockable.this,"cipher.error.beanshell-entropy",null);
                    }
                } catch (Exception e) {
                    Log.log(Log.ERROR,Crypter.class,e);
                    beanshellError = true;
                    new BeanShellErrorDialog(jEdit.getActiveView(),e);
                }
            } else {
                stringEntropy = entropy;
            }
            
            Object[] objectArrayAdditionalInformation = null;
            if (null == additionalInformationRequester) {
                objectArrayAdditionalInformation = new Object[0];
            } else {
                additionalInformationRequester.save();
                objectArrayAdditionalInformation = additionalInformationRequester.getAdditionalInformation();
            }
            
            if (!beanshellError) {
                try {
                    String cipherName = (String)cipherComboBox.getSelectedItem();
                    Cipher cipher = CipherPlugin.getCipher(cipherName);
                    if (null == cipher) {
                        GUIUtilities.error(CipherDockable.this,"cipher.error.cipher-not-available",new String[] { cipherName });
                    } else {
                        synchronized (cipher) {
                            if (null == stringData) {
                                if (encryptButton == source) {
                                    cipher.setRawData(byteArrayData);
                                } else {
                                    cipher.setEncryptedData(byteArrayData);
                                }
                            } else {
                                if (encryptButton == source) {
                                    cipher.setRawData(stringData);
                                } else {
                                    cipher.setEncryptedData(stringData);
                                }
                            }
                            
                            if (null == stringEntropy) {
                                cipher.setEntropy(byteArrayEntropy);
                            } else {
                                cipher.setEntropy(stringEntropy);
                            }
                            
                            cipher.setAdditionalInformation(objectArrayAdditionalInformation);
                            
                            if (encryptButton == source) {
                                String result;
                                if (stringEncryptedDataRadioButton.isSelected()) {
                                    result = cipher.encryptToString();
                                    if (null == result) {
                                        result = jEdit.getProperty("cipher.unable-to-encrypt.message");
                                    }
                                } else {
                                    byte[] resultByteArray = cipher.encryptToByteArray();
                                    if (null == resultByteArray) {
                                        result = jEdit.getProperty("cipher.unable-to-encrypt.message");
                                    } else {
                                        result = Arrays.toString(resultByteArray)
                                                       .replace("]"," }")
                                                       .replace("[","new byte[] { ");
                                    }
                                }
                                encryptedDataTextArea.setText(result);
                            } else {
                                String result;
                                if (stringRawDataRadioButton.isSelected()) {
                                    result = cipher.decryptToString();
                                    if (null == result) {
                                        result = jEdit.getProperty("cipher.unable-to-decrypt.message");
                                    }
                                } else {
                                    byte[] resultByteArray = cipher.decryptToByteArray();
                                    if (null == resultByteArray) {
                                        result = jEdit.getProperty("cipher.unable-to-decrypt.message");
                                    } else {
                                        result = Arrays.toString(resultByteArray)
                                                       .replace("]"," }")
                                                       .replace("[","new byte[] { ");
                                    }
                                }
                                rawDataTextArea.setText(result);
                            }
                        }
                    }
                } catch (Throwable t) {
                    new BeanShellErrorDialog(jEdit.getActiveView(),t);
                }
            }
        }
    }
}
