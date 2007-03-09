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

import javax.swing.JButton;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import net.jcip.annotations.GuardedBy;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.gui.ExtendedGridLayout;
import org.gjt.sp.jedit.gui.ExtendedGridLayoutConstraints;

/**
 * A dialog for entering the additional information
 * for a {@code Cipher} implementation.
 * 
 * @author Björn "Vampire" Kautler
 * @since CipherPlugin 0.1
 */
public class AdditionalInformationDialog extends EnhancedDialog {
    /**
     * The additional information entered by the user.
     * Access to this field must be synchronized on this instance.
     */
    @GuardedBy("this") private Object[] additionalInformation = null;
    
    @SuppressWarnings(value = "SE_BAD_FIELD",
                      justification = "The class will not be serialized")
    private AdditionalInformationRequester additionalInformationRequester;
    private JButton okButton;
    
    /**
     * Constructs a new modal {@code AdditionalInformationDialog}.
     * 
     * @see #init()
     */
    @CheckReturnValue(explanation = "The additional information should get requested from the dialog")
    private AdditionalInformationDialog(@NonNull AdditionalInformationRequester additionalInformationRequester) {
        super(jEdit.getActiveView(),jEdit.getProperty("options.cipher.additional-information-dialog.title"),true);
        
        if (null == additionalInformationRequester) {
            throw new IllegalArgumentException("additionalInformationRequester must not be null");
        }
        
        this.additionalInformationRequester = additionalInformationRequester;
        
        okButton = new JButton(jEdit.getProperty("common.ok"));
        Dimension dim = okButton.getMaximumSize();
        dim.width = Integer.MAX_VALUE;
        okButton.setMaximumSize(dim);
        getRootPane().setDefaultButton(okButton);
        
        setLayout(new ExtendedGridLayout(5,5,new Insets(5,5,5,5)));
        add(additionalInformationRequester.getComponent(),null);
        add(okButton,new ExtendedGridLayoutConstraints(1,okButton));
    }
    
    /**
     * Initializes this {@code AdditionalInformationDialog} and shows it.
     */
    private void init() {
        okButton.addActionListener(new AdditionalInformationSetter());
        
        pack();
        GUIUtilities.loadGeometry(this,"cipher.additional-information-dialog");
        setVisible(true);
    }
    
    /**
     * Creates a new modal {@code AdditionalInformationDialog},
     * initializes it and shows it.
     * 
     * @return The newly created {@code AdditionalInformationDialog}
     */
    @CheckReturnValue(explanation = "The additional information should get requested from the dialog")
    public static AdditionalInformationDialog newInstance(@NonNull AdditionalInformationRequester additionalInformationRequester) {
        AdditionalInformationDialog instance = new AdditionalInformationDialog(additionalInformationRequester);
        instance.init();
        return instance;
    }
    
    /**
     * <p>Returns the additional information entered by the user or
     * {@code null} if the dialog got disposed.</p>
     * 
     * <p>This method is synchronized on this instance.</p>
     * 
     * @return The additional information entered by the user or {@code null}
     *         if the dialog got disposed.
     */
    @SuppressWarnings(value = "EI_EXPOSE_REP",
                      justification = "This field is only used to give the information out")
    @CheckForNull
    @CheckReturnValue(explanation = "If the additional information got requested it should be used")
    public synchronized Object[] getAdditionalInformation() {
        return additionalInformation;
    }
    
    /**
     * <p>Called when ok button or Enter key is pressed.</p>
     * 
     * @see org.gjt.sp.jedit.gui.EnhancedDialog#ok()
     */
    @Override
    public void ok() {
        additionalInformationRequester.save();
        synchronized (this) {
            additionalInformation = additionalInformationRequester.getAdditionalInformation();
        }
        GUIUtilities.saveGeometry(this,"cipher.additional-information-dialog");
        dispose();
    }
    
    /**
     * Called when ESC button is pressed or window is closed.
     * 
     * @see org.gjt.sp.jedit.gui.EnhancedDialog#cancel()
     */
    @Override
    public void cancel() {
        GUIUtilities.saveGeometry(this,"cipher.additional-information-dialog");
        dispose();
    }
    
    /**
     * Sets the additional information returned by
     * {@code getAdditionalInformation()}
     * 
     * @author Björn "Vampire" Kautler
     * @since CipherPlugin 0.1
     * @see AdditionalInformationDialog#getAdditionalInformation()
     */
    private class AdditionalInformationSetter implements ActionListener {
        /**
         * Invoked when an action occurs.
         * 
         * @param ae The {@code ActionEvent} of the action that occured
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(@Nullable ActionEvent ae) {
            ok();
        }
    }
}
