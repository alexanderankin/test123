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
import javax.swing.JLabel;
import javax.swing.JPasswordField;

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
 * A dialog for requesting a password from a user.
 * 
 * @author Björn "Vampire" Kautler
 * @since CipherPlugin 0.1
 */
public class PasswordDialog extends EnhancedDialog {
    @GuardedBy("this") private String password = null;
    
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton okButton;
    
    /**
     * Constructs a new modal {@code PasswordDialog}.
     * 
     * @see #init()
     */
    @CheckReturnValue(explanation = "The password should get requested from the dialog")
    private PasswordDialog() {
        super(jEdit.getActiveView(),jEdit.getProperty("cipher.password-dialog.title"),true);
        
        passwordLabel = new JLabel(jEdit.getProperty("cipher.password-dialog.password.label"));
        
        passwordField = new JPasswordField();
        passwordField.setColumns(25);
        passwordLabel.setLabelFor(passwordField);
        
        okButton = new JButton(jEdit.getProperty("common.ok"));
        Dimension dim = okButton.getMaximumSize();
        dim.width = Integer.MAX_VALUE;
        okButton.setMaximumSize(dim);
        getRootPane().setDefaultButton(okButton);
        
        setLayout(new ExtendedGridLayout(5,5,new Insets(5,5,5,5)));
        add(passwordLabel,null);
        add(passwordField,null);
        
        add(okButton,new ExtendedGridLayoutConstraints(1,2,1,okButton));
    }
    
    /**
     * Initializes this {@code PasswordDialog} and shows it.
     */
    private void init() {
        okButton.addActionListener(new PasswordSetter());
        
        pack();
        GUIUtilities.loadGeometry(this,"cipher.password-dialog");
        setVisible(true);
    }
    
    /**
     * Creates a new modal {@code PasswordDialog},
     * initializes it and shows it.
     * 
     * @return The newly created {@code PasswordDialog}
     */
    @NonNull
    @CheckReturnValue(explanation = "The password should get requested from the dialog")
    public static PasswordDialog newInstance() {
        PasswordDialog instance = new PasswordDialog();
        instance.init();
        return instance;
    }
    
    /**
     * Returns the password entered by the user,
     * or {@code null} if the dialog got disposed.
     * 
     * <p>This method is synchronized on this instance.</p>
     * 
     * @return The entered password or {@code null} if the dialog got disposed
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the password got requested it should be used")
    public synchronized String getPassword() {
        return password;
    }
    
    /**
     * <p>Called when OK button or Enter key is pressed.</p>
     * 
     * <p>Sets the password returned by
     * {@code getPassword()}.</p>
     * 
     * @see org.gjt.sp.jedit.gui.EnhancedDialog#ok()
     */
    @Override
    public void ok() {
        synchronized (this) {
            password = new String(passwordField.getPassword());
        }
        GUIUtilities.saveGeometry(this,"cipher.password-dialog");
        dispose();
    }
    
    /**
     * <p>Called when ESC button is pressed or window is closed.</p>
     * 
     * @see org.gjt.sp.jedit.gui.EnhancedDialog#cancel()
     */
    @Override
    public void cancel() {
        GUIUtilities.saveGeometry(this,"cipher.password-dialog");
        dispose();
    }
    
    /**
     * Sets the password returned by
     * {@code getPassword()}.
     * 
     * @author Björn "Vampire" Kautler
     * @since CipherPlugin 0.1
     */
    private class PasswordSetter implements ActionListener {
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
