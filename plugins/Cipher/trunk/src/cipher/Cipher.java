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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;

import net.jcip.annotations.ThreadSafe;

/**
 * <p>The interface cipher implementations must implement to provide a
 * cipher to the {@code CipherPlugin}.</p>
 * 
 * <p>In addition to this the plugin has to define the service via
 * {@code jEdit}s ServicesAPI thus provide a {@code services.xml}
 * file with content similar to the following:</p>
 * 
 * <pre><font color="#000000">
 * <font color="#000000">   1:</font><font color="#0099ff"><strong>&lt;?</strong></font><font color="#0099ff"><strong>xml</strong></font><font color="#0099ff"><strong> </strong></font><font color="#0099ff"><strong>version=&quot;1.0&quot;</strong></font><font color="#0099ff"><strong> </strong></font><font color="#0099ff"><strong>?</strong></font><font color="#0099ff"><strong>&gt;</strong></font>
 * <font color="#000000">   2:</font><font color="#009966"><strong>&lt;!</strong></font><font color="#009966"><strong>DOCTYPE</strong></font><font color="#009966"><strong> </strong></font><font color="#009966"><strong>SERVICES</strong></font><font color="#009966"><strong> </strong></font><font color="#009966"><strong>SYSTEM</strong></font><font color="#009966"><strong> </strong></font><font color="#ff00cc">&quot;</font><font color="#ff00cc">services.dtd</font><font color="#ff00cc">&quot;</font><font color="#009966"><strong>&gt;</strong></font>
 * <font color="#000000">   3:</font>
 * <font color="#000000">   4:</font><font color="#0000ff">&lt;</font><font color="#0000ff">SERVICES</font><font color="#0000ff">&gt;</font>
 * <font color="#990066">   5:</font>    <font color="#0000ff">&lt;</font><font color="#0000ff">SERVICE</font><font color="#0000ff"> </font><font color="#0000ff">CLASS</font><font color="#0000ff">=</font><font color="#ff00cc">&quot;</font><font color="#ff00cc">cipher.Cipher</font><font color="#ff00cc">&quot;</font><font color="#0000ff"> </font><font color="#0000ff">NAME</font><font color="#0000ff">=</font><font color="#ff00cc">&quot;</font><font color="#ff00cc">wincrypt</font><font color="#ff00cc">&quot;</font><font color="#0000ff">&gt;</font>
 * <font color="#000000">   6:</font>        new wincrypt.WincryptCipher();
 * <font color="#000000">   7:</font>    <font color="#0000ff">&lt;</font><font color="#0000ff">/</font><font color="#0000ff">SERVICE</font><font color="#0000ff">&gt;</font>
 * <font color="#000000">   8:</font><font color="#0000ff">&lt;</font><font color="#0000ff">/</font><font color="#0000ff">SERVICES</font><font color="#0000ff">&gt;</font>
 * <font color="#000000">   9:</font>
 * </font></pre>
 * 
 * <p>The last thing needed is a description of the cipher that is shown
 * in the Cipher option pane and in the default cipher dialog. This
 * description has to be stored in a property named 
 * &quot;{@code cipher-description.}<em>service_name</em>&quot;, so
 * {@code cipher-description.wincrypt} for the example above.</p>
 * 
 * <p>Because several threads could use the same {@code Cipher} instance,
 * implementations have to be thread-safe. This means all access
 * to a {@code Cipher}s state like the rawData, encryptedData,
 * entropy and additionalInformation, has to be synchronized, so
 * most methods defined in this interface tend to be synchonized.</p>
 * 
 * <p>To ensure the atomicity of the encrypting or decrypting
 * process in a thread safe manner, the subsequent calls to
 * <ul><li>one of the {@code set*Data()},</li>
 * <li>one of the {@code setEntropy()},</li>
 * <li>the {@code setAdditionalInformation()} and</li>
 * <li>one of the {@code *cryptTo*()} methods</li></ul>
 * should all be made from within one {@code synchronized} block,
 * locking on the same object as the methods defined in this interface.</p>
 * 
 * <p>For an example of proper usage, see the {@code WincryptCipherPlugin}
 * which is the reference implementation for this interface and the
 * {@code CipherDockable.Crypter} which is the reference implementation of
 * the usage of {@code Cipher}s.</p>
 * 
 * @author Björn "Vampire" Kautler
 * @since CipherPlugin 0.1
 * @see CipherDockable.Crypter#actionPerformed(java.awt.event.ActionEvent)
 * @see org.gjt.sp.jedit.ServiceManager
 * @see wincrypt.WincryptCipher
 */
@ThreadSafe
public interface Cipher {
    /**
     * <p>Sets the raw data for an encrypting process
     * from a {@code byte} array. If your raw data is a password you may
     * consider using {@link #setRawData(String)}.</p>
     * 
     * <p>Implementations should be aware that {@code rawData}
     * can be {@code null} what should be handled in a reasonable
     * way. There should no {@code NullPointerException} be
     * thrown in this case.</p>
     * 
     * @param rawData The raw data as a {@code byte} array
     * @see #setRawData(String)
     */
    void setRawData(@Nullable byte[] rawData);
    
    /**
     * <p>Sets the raw data for an encrypting process
     * from a {@code String} object.</p>
     * 
     * <p>Implementations should be aware that {@code rawData}
     * can be {@code null} what should be handled in a reasonable
     * way. There should no {@code NullPointerException} be
     * thrown in this case.</p>
     * 
     * @param rawData The raw data as a {@code String} object
     * @see #setRawData(byte[])
     */
    void setRawData(@Nullable String rawData);
    
    /**
     * <p>Sets the encrypted data for a decrypting process
     * from a {@code byte} array.</p>
     * 
     * <p>Implementations should be aware that {@code encryptedData}
     * can be {@code null} what should be handled in a reasonable
     * way. There should no {@code NullPointerException} be
     * thrown in this case.</p>
     * 
     * @param encryptedData The encrypted data as a {@code byte} array
     * @see #setEncryptedData(String)
     */
    void setEncryptedData(@Nullable byte[] encryptedData);
    
    /**
     * <p>Sets the encrypted data for a decrypting process
     * from a {@code String} object.</p>
     * 
     * <p>Implementations should be aware that {@code encryptedData}
     * can be {@code null} what should be handled in a reasonable
     * way. There should no {@code NullPointerException} be
     * thrown in this case.</p>
     * 
     * @param encryptedData The encrypted data as a {@code String} object
     * @see #setEncryptedData(byte[])
     */
    void setEncryptedData(@Nullable String encryptedData);
    
    /**
     * <p>Sets the entropy for an encrypting or decrypting process
     * from a {@code byte} array. The entropy could e. g. be a
     * &quot;fingerprint&quot; in byte data.</p>
     * 
     * <p>Implementations should be aware that {@code entropy}
     * can be {@code null} what should be handled in a reasonable
     * way. There should no {@code NullPointerException} be
     * thrown in this case.</p>
     * 
     * @param entropy The entropy as a {@code byte} array
     * @see #setEntropy(String)
     */
    void setEntropy(@Nullable byte[] entropy);
    
    /**
     * <p>Sets the entropy for an encrypting or decrypting process
     * from a {@code String} object. The entropy could e. g. be a
     * password, like a master password that is used to encrypt
     * all stored passwords.</p>
     * 
     * <p>Implementations should be aware that {@code entropy}
     * can be {@code null} what should be handled in a reasonable
     * way. There should no {@code NullPointerException} be
     * thrown in this case.</p>
     * 
     * @param entropy The entropy as a {@code String} object
     * @see #setEntropy(byte[])
     */
    void setEntropy(@Nullable String entropy);
    
    /**
     * <p>Returns an {@code AdditionalInformationRequester} that request
     * the needed additional information for an encrypting or
     * decrypting process. If no additional information is needed,
     * the method should return {@code null}.</p>
     * 
     * @return The additional information requester
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the additional information requester got requested it should be used")
    AdditionalInformationRequester getAdditionalInformationRequester();
    
    /**
     * <p>Sets the additional information for an encrypting or
     * decrypting process from an {@code Object} vararg.
     * If and which information can be set depends on the actual
     * {@code Cipher} implementation.</p>
     * 
     * <p>Implementations should check if the right number and
     * types of arguments got supplied and throw an
     * {@code IllegalArgumentException} otherwise.
     * Be aware that additionalInformation can be
     * {@code null} what should be handled in a reasonable
     * way. There should no {@code NullPointerException} be
     * thrown in this case but an {@code IllegalArgumentException}
     * if the implementation needs additional information.</p>
     * 
     * @param additionalInformation The additional information as an {@code Object} vararg
     * @throws IllegalArgumentException If the wrong amount or types of arguments got supplied
     */
    void setAdditionalInformation(@Nullable Object... additionalInformation);
    
    /**
     * Encrypts the given raw data with the given entropy
     * and the given additional information. If you want
     * to store the encrypted data on some text device like
     * a text file, consider using {@link #encryptToString()}.
     * 
     * @return The encrypted data as a {@code byte} array or {@code null} if encryption was not successful
     * @see #encryptToString()
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the encryption result is not of further interest, the process should not be invoked")
    byte[] encryptToByteArray();
    
    /**
     * Decrypts the given encrypted data with the given
     * entropy and the given additional information.
     * If you want to decrypt a password, consider using
     * {@link #decryptToString()}.
     * 
     * @return The decrypted data as a {@code byte} array or {@code null} if decryption was not successful
     * @see #decryptToString()
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the decryption result is not of further interest, the process should not be invoked")
    byte[] decryptToByteArray();
    
    /**
     * Encrypts the given raw data with the given entropy
     * and the given additional information.
     * 
     * @return The encrypted data as a {@code String} object or {@code null} if encryption was not successful
     * @see #encryptToByteArray()
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the encryption result is not of further interest, the process should not be invoked")
    String encryptToString();
    
    /**
     * Decrypts the given encrypted data with the given
     * entropy and the given additional information.
     * 
     * @return The decrypted data as a {@code String} object or {@code null} if decryption was not successful
     * @see #decryptToByteArray()
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the decryption result is not of further interest, the process should not be invoked")
    String decryptToString();
    
    /**
     * Checks if this {@code Cipher} implementation is
     * currently available. This method should return
     * the same value during one JVM session. An
     * implementation could maybe be unavailable
     * if it depends on some native libraries
     * only found on certain operating system
     * like the {@code WincryptCipherPlugin}
     * which is only available on windows boxes and
     * even there not on all.
     * 
     * @return Whether the implementation is currently available
     */
    @CheckReturnValue(explanation = "If the availability got requested it should be used")
    boolean isAvailable();
}
