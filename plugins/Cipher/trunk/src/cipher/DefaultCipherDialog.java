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

import net.jcip.annotations.GuardedBy;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.gui.ExtendedGridLayout;
import org.gjt.sp.jedit.gui.ExtendedGridLayoutConstraints;

/**
 * A dialog for choosing the default {@code Cipher} instance.
 * 
 * @author Björn "Vampire" Kautler
 * @since CipherPlugin 0.1
 */
public class DefaultCipherDialog extends EnhancedDialog {
    /**
     * The name of the default {@code Cipher} instance that was chosen.
     * Access to this field must be synchronized on this instance.
     */
    @GuardedBy("this") private String defaultCipherName = null;
    
    private CipherOptionPane cipherOptionPane;
    private JButton okButton;
    
    /**
     * Constructs a new modal {@code DefaultCipherDialog}.
     * 
     * @see #init()
     */
    @CheckReturnValue(explanation = "The default cipher name should get requested from the dialog")
    private DefaultCipherDialog() {
        super(jEdit.getActiveView(),jEdit.getProperty("options.cipher.default-cipher-dialog.title"),true);
        
        cipherOptionPane = new CipherOptionPane();
        
        okButton = new JButton(jEdit.getProperty("common.ok"));
        getRootPane().setDefaultButton(okButton);
        
        setLayout(new ExtendedGridLayout(5,5,new Insets(5,5,5,5)));
        add(cipherOptionPane,null);
        add(okButton,new ExtendedGridLayoutConstraints(1,okButton));
    }
    
    /**
     * Initializes this {@code DefaultCipherDialog} and shows it.
     */
    private void init() {
        cipherOptionPane.init();
        
        Dimension dim = okButton.getMaximumSize();
        dim.width = Integer.MAX_VALUE;
        okButton.setMaximumSize(dim);
        okButton.addActionListener(new DefaultCipherSetter());
        
        pack();
        GUIUtilities.loadGeometry(this,"cipher.default-cipher-dialog");
        setVisible(true);
    }
    
    /**
     * Creates a new modal {@code DefaultCipherDialog},
     * initializes it and shows it.
     * 
     * @return The newly created {@code DefaultCipherDialog}
     */
    @NonNull
    @CheckReturnValue(explanation = "The default cipher name should get requested from the dialog")
    public static DefaultCipherDialog newInstance() {
        DefaultCipherDialog instance = new DefaultCipherDialog();
        instance.init();
        return instance;
    }
    
    /**
     * <p>Returns the name of the chosen default {@code Cipher} instance
     * or {@code null} or {@code null} if no cipher was chosen or
     * no cipher is installed.
     * It's not guaranteed that this ciphers {@code isAvailable()}
     * method returns {@code true}, this should be checked before usage.</p>
     * 
     * <p>This method is synchronized on this instance.</p>
     * 
     * @return The name of the chosen default {@code Cipher} instance
     * @see Cipher#isAvailable()
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the default cipher name got requested it should be used")
    public synchronized String getDefaultCipherName() {
        return defaultCipherName;
    }
    
    /**
     * <p>Called when create button or Enter key is pressed.</p>
     * 
     * <p>Sets the default cipher name returned by
     * {@code getDefaultCipherName()}</p>
     * 
     * @see org.gjt.sp.jedit.gui.EnhancedDialog#ok()
     */
    @Override
    public void ok() {
        synchronized (this) {
            defaultCipherName = cipherOptionPane.getCipherName();
        }
        GUIUtilities.saveGeometry(this,"cipher.default-cipher-dialog");
        dispose();
    }
    
    /**
     * <p>Called when ESC button is pressed or window is closed.</p>
     * 
     * @see org.gjt.sp.jedit.gui.EnhancedDialog#cancel()
     */
    @Override
    public void cancel() {
        GUIUtilities.saveGeometry(this,"cipher.default-cipher-dialog");
        dispose();
    }
    
    /**
     * Sets the default cipher name returned by
     * {@code getDefaultCipherName()}
     * 
     * @author Björn "Vampire" Kautler
     * @since CipherPlugin 0.1
     * @see DefaultCipherDialog#getDefaultCipherName()
     */
    private class DefaultCipherSetter implements ActionListener {
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
