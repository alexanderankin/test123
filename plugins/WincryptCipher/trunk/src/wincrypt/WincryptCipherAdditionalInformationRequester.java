/*
 * WincryptCipherPlugin - A jEdit plugin as wincrypt cipher implementation for the CipherPlugin
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

package wincrypt;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cipher.AdditionalInformationRequester;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import net.jcip.annotations.GuardedBy;

import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.jedit.gui.ExtendedGridLayout;
import org.gjt.sp.jedit.gui.ExtendedGridLayoutConstraints;

/**
 * The content for the additional information dialog
 * where you can enter the description for an encrypting
 * or decrypting process.
 * 
 * @author Björn "Vampire" Kautler
 * @since WincryptCipherPlugin 0.1
 */
public class WincryptCipherAdditionalInformationRequester extends JPanel implements AdditionalInformationRequester {
    @GuardedBy("this") private Object[] additionalInformation;
    
    private JLabel descriptionLabel;
    private JTextArea descriptionTextArea;
    private JScrollPane descriptionScrollPane;
    
    /**
     * Constructs a new {@code WincryptCipherAdditionalInformationRequester}.
     */
    public WincryptCipherAdditionalInformationRequester() {
        descriptionLabel = new JLabel(jEdit.getProperty("options.wincrypt.description.label"));
        
        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionLabel.setLabelFor(descriptionTextArea);
        
        descriptionScrollPane = new JScrollPane(descriptionTextArea);
        
        setLayout(new ExtendedGridLayout(5,5,new Insets(0,0,0,0)));
        add(descriptionLabel,null);
        add(descriptionScrollPane,new ExtendedGridLayoutConstraints(1,descriptionScrollPane));
    }
    
    /**
     * <p>This method is called before the dialog is displayed.
     * Any creation of non-static inner classes should go here
     * to not let the {@code this} reference escape during construction.</p>
     * 
     * @see cipher.AdditionalInformationRequester#init()
     */
    public void init() {
    }
    
    /**
     * <p>Returns the {@code Component} that should be displayed
     * for this additional information requester.
     * The return value must not be {@code null}. If no additional
     * information is needed, then no additional information requester
     * is needed either.</p>
     * 
     * @return The {@code Component} that should be displayed for this additional information requester
     * @see cipher.AdditionalInformationRequester#getComponent()
     * @see cipher.Cipher#getAdditionalInformationRequester()
     */
    @NonNull
    @CheckReturnValue(explanation = "If the component got requested it should be used")
    public Component getComponent() {
        return this;
    }
    
    /**
     * <p>Called when the dialogs &quot;OK&quot; button is clicked.</p>
     * 
     * @see #getAdditionalInformation()
     * @see cipher.AdditionalInformationRequester#save()
     */
    public synchronized void save() {
        additionalInformation = new Object[] { descriptionTextArea.getText() };
    }
    
    /**
     * <p>Returns the additional information entered by the user,
     * or {@code null} if the user discarded the dialog.</p>
     * 
     * @return The addtional information entered by the user or {@code null} if the dialog got discarded
     */
    @SuppressWarnings(value = "EI_EXPOSE_REP",
                      justification = "This field is only used to give the information out")
    @CheckForNull
    @CheckReturnValue(explanation = "If the additional information got requested it should be used")
    public synchronized Object[] getAdditionalInformation() {
        return additionalInformation;
    }
}
