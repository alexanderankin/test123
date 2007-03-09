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

import edu.umd.cs.findbugs.annotations.CheckReturnValue;

import org.gjt.sp.jedit.EditPlugin;

/**
 * A jEdit plugin providing a wincrypt cipher
 * implementation for the CipherPlugin
 * 
 * @author Björn "Vampire" Kautler
 * @since WincryptCipherPlugin 0.1
 * @see cipher.Cipher
 */
public class WincryptCipherPlugin extends EditPlugin {
    /**
     * jEdit calls this method when the plugin is being activated,
     * either during startup or at any other time.
     * A plugin can get activated for a number of reasons:
     * 
     * <ul>
     * <li>The plugin is written for jEdit 4.1 or older,
     *     in which case it will always be loaded at startup.</li>
     * <li>The plugin has its <code>activate</code> property set to
     *     <code>startup</code>, in which case it will
     *     always be loaded at startup.</li>
     * <li>One of the properties listed in the plugin's
     *     <code>activate</code> property is set to <code>true</code>,
     *     in which case it will always be loaded at startup.</li>
     * <li>One of the plugin's classes is being accessed
     *     by another plugin, a macro, or a BeanShell snippet
     *     in a plugin API XML file.</li>
     * </ul>
     * 
     * Note that this method is always called from the event dispatch
     * thread, even if the activation resulted from a class being loaded
     * from another thread. A side effect of this is that some of your
     * plugin's code might get executed before this method finishes
     * running.<p>
     * 
     * When this method is being called for plugins
     * written for jEdit 4.1 and below, no views or
     * buffers are open. However, this is not the
     * case for plugins using the new API. For example,
     * if your plugin adds tool bars to views, make sure
     * you correctly handle the case where views are
     * already open when the plugin is loaded.<p>
     * 
     * If your plugin must be loaded on startup, take care
     * to have this method return as quickly as possible.<p>
     *
     * This implementation loads the {@code WincryptCipher} class
     * and tries to load the native library. If this fails, an
     * exception is thrown which causes the plugin to be
     * marked as broken. Because of that, the class is loaded
     * at this point.
     * 
     * @see org.gjt.sp.jedit.EditPlugin#start()
     */
    @Override
    public void start() {
        try {
            Class.forName("wincrypt.WincryptCipher");
        } catch (ClassNotFoundException cnfe) {
            throw new InternalError("Missing class wincrypt.WincryptCipher");
        }
    }
    
    /**
     * Returns true if the plugin uses the standard plugin home.
     * 
     * @return {@code true}
     * @see org.gjt.sp.jedit.EditPlugin#usePluginHome()
     */
    @Override
    @CheckReturnValue(explanation = "It makes no sense to call this method if the return value is not checked")
    public boolean usePluginHome() {
        return true;
    }
}
