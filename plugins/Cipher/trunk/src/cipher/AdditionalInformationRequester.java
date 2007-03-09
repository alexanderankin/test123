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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * <p>The interface the additional information requester must implement.</p>
 * 
 * <p>For an example of proper usage, see the {@code WincryptCipherPlugin}
 * which has the reference implementation for this interface and the
 * {@code CipherDockable.Crypter} which is the reference implementation of
 * the usage of {@code Cipher}s.</p>
 * 
 * @author Björn "Vampire" Kautler
 * @since CipherPlugin 0.1
 * @see CipherDockable.Crypter#actionPerformed(java.awt.event.ActionEvent)
 * @see wincrypt.WincryptCipherAdditionalInformationRequester
 */
public interface AdditionalInformationRequester {
    /**
     * <p>Returns the {@code Component} that should be displayed
     * for this additional information requester.
     * The return value must not be {@code null}. If no additional
     * information is needed, then no additional information requester
     * is needed either.</p>
     * 
     * @return The {@code Component} that should be displayed for this additional information requester
     * @see Cipher#getAdditionalInformationRequester()
     */
    @NonNull
    @CheckReturnValue(explanation = "If the component got requested it should be used")
    Component getComponent();
    
    /**
     * <p>This method is called before the dialog is displayed.
     * Any creation of non-static inner classes should go here
     * to not let the {@code this} reference escape during construction.</p>
     */
    void init();
    
    /**
     * <p>Called when the dialogs &quot;OK&quot; button is clicked.</p>
     * 
     * @see #getAdditionalInformation()
     */
    void save();
    
    /**
     * <p>Returns the additional information entered by the user,
     * or {@code null} if the user discarded the dialog.</p>
     * 
     * @return The addtional information entered by the user or
     *         {@code null} if the dialog got discarded
     */
    @CheckForNull
    @CheckReturnValue(explanation = "If the additional information got requested it should be used")
    Object[] getAdditionalInformation();
}
