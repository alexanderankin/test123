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

import java.awt.Component;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.ServiceManager;

import org.gjt.sp.util.Log;

import static org.gjt.sp.util.Log.DEBUG;

/**
 * A jEdit plugin as framework for cipher implementations.
 * 
 * @author Björn "Vampire" Kautler
 * @since CipherPlugin 0.1
 */
public class CipherPlugin extends EditPlugin {
    /**
     * Returns the names of the installed
     * {@code Cipher} implementations, no matter if their
     * {@code isAvailable()} method returns
     * {@code true} or {@code false}.
     * 
     * @return An array with the names of the installed cipher implementations
     * @see Cipher
     * @see Cipher#isAvailable()
     */
    @NonNull
    @CheckReturnValue(explanation = "If the cipher names got requested they should be used")
    public static String[] getAvailableCiphersNames() {
        return ServiceManager.getServiceNames("cipher.Cipher");
    }
    
    /**
     * Returns a {@code Cipher} implementation if it is installed
     * and its {@code isAvailable()} method returns true.
     * 
     * @param cipherName The name of the {@code Cipher} implementation in question
     * @return The {@code Cipher} instance if installed and available,
     *         {@code null} otherwise
     * @see Cipher#isAvailable()
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the cipher got requested it should be used")
    public static Cipher getCipher(@Nullable String cipherName) {
        if (null != cipherName) {
            Cipher cipher = (Cipher)ServiceManager.getService("cipher.Cipher",cipherName);
            if ((null != cipher) && (cipher.isAvailable())) {
                return cipher;
            }
        }
        return null;
    }
    
    /**
     * Returns the default {@code Cipher} instance
     * if a default is chosen, which is installed and available.
     * Otherwise a dialog is shown where you can choose
     * the default {@code Cipher} instance. The default
     * can also be set in the options panel.
     * 
     * @return The default {@code Cipher} instance or {@code null}
     *         if the dialog got discarded, no cipher was chosen,
     *         no cipher is installed or the chosen cipher is not available
     * @see Cipher
     * @see Cipher#isAvailable()
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the default cipher got requested it should be used")
    public static Cipher getDefaultCipher() {
        String defaultCipherName = jEdit.getProperty("options.cipher.default-cipher");
        Cipher cipher = getCipher(defaultCipherName);
        if (null == cipher) {
            final DefaultCipherDialog[] argumentArray = new DefaultCipherDialog[1];
            try {
                Runnable runnable = new Runnable() {
                                        public void run() {
                                            argumentArray[0] = DefaultCipherDialog.newInstance();
                                        }
                                    };
                if (SwingUtilities.isEventDispatchThread()) {
                    runnable.run();
                } else {
                    SwingUtilities.invokeAndWait(runnable);
                }
            } catch (InterruptedException ie) {
                Log.log(DEBUG,CipherPlugin.class,ie);
                return null;
            } catch (InvocationTargetException ite) {
                Log.log(DEBUG,DefaultCipherDialog.class,ite.getCause());
                return null;
            }
            defaultCipherName = argumentArray[0].getDefaultCipherName();
            cipher = getCipher(defaultCipherName);
            if (null != cipher) {
                jEdit.setProperty("options.cipher.default-cipher",defaultCipherName);
                jEdit.propertiesChanged();
                jEdit.saveSettings();
            }
        }
        return cipher;
    }
    
    /**
     * Returns the additional information, needed by the given {@code Cipher}
     * implementation. A Dialog is shown with content supplied by the implementation
     * and the result is given back for usage.
     * 
     * @param cipherName The name of the {@code Cipher} implementation
     * @return The additional information as {@code Object} array
     *         or null if the Cipher is not found, not available or
     *         the dialog got discarded.
     * @see #getAdditionalInformation(Cipher)
     * @see Cipher#setAdditionalInformation()
     */
    @SuppressWarnings(value = "PZLA_PREFER_ZERO_LENGTH_ARRAYS",
                      justification = "null is used to indicate an error, not an empty result list")
    @CheckForNull
    @CheckReturnValue(explanation = "If the additional information got requested it should be used")
    public static Object[] getAdditionalInformation(@Nullable String cipherName) {
        Cipher cipher = getCipher(cipherName);
        if (null == cipher) {
            return null;
        }
        return getAdditionalInformation(cipher);
    }
    
    /**
     * Returns the additional information, needed by the given {@code Cipher}
     * implementation. A Dialog is shown with content supplied by the implementation
     * and the result is given back for usage.
     * 
     * @param cipher The {@code Cipher} implementation which must be non-{@code null}
     * @return The additional information as {@code Object} array
     *         or {@code null} if the dialog got discarded
     * @throws IllegalArgumentException If cipher is {@code null}
     * @see #getAdditionalInformation(String)
     * @see Cipher#setAdditionalInformation()
     */
    @SuppressWarnings(value = "PZLA_PREFER_ZERO_LENGTH_ARRAYS",
                      justification = "null is used to indicate an error, not an empty result list")
    @CheckForNull
    @CheckReturnValue(explanation = "If the additional information got requested it should be used")
    public static Object[] getAdditionalInformation(@NonNull final Cipher cipher) {
        if (null == cipher) {
            throw new IllegalArgumentException("cipher must not be null");
        }
        
        AdditionalInformationRequester additionalInformationRequester = cipher.getAdditionalInformationRequester();
        if (null == additionalInformationRequester) {
            return new Object[0];
        }
        
        final AdditionalInformationDialog[] argumentArray = new AdditionalInformationDialog[1];
        try {
            Runnable runnable = new Runnable() {
                                    public void run() {
                                        argumentArray[0] = AdditionalInformationDialog.newInstance(
                                            cipher.getAdditionalInformationRequester());
                                    }
                                };
            if (SwingUtilities.isEventDispatchThread()) {
                runnable.run();
            } else {
                SwingUtilities.invokeAndWait(runnable);
            }
        } catch (InterruptedException ie) {
            Log.log(DEBUG,CipherPlugin.class,ie);
            return null;
        } catch (InvocationTargetException ite) {
            Log.log(DEBUG,AdditionalInformationDialog.class,ite.getCause());
            return null;
        }
        return argumentArray[0].getAdditionalInformation();
    }
    
    /**
     * Returns true if the plugin uses the standard plugin home.
     *
     * @return true
     */
    @Override
    @CheckReturnValue(explanation = "It makes no sense to call this method if the return value is not checked")
    public boolean usePluginHome() {
        return true;
    }
}
