/*
 * jcompile_compilepkg.java - compile action for JCompiler plugin
 * (c) 2000 Dirk Moebius
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
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

package jcompiler.actions;

import java.awt.event.ActionEvent;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import console.Console;
import JCompilerPlugin;


/**
 * Action that invokes JCompiler on the current package.
 */
public class jcompile_compilepkg extends EditAction {
    
    public jcompile_compilepkg() {
        super("jcompiler-compilepkg");
    }
    
    public void actionPerformed(ActionEvent evt) {
        DockableWindowManager wm = getView(evt).getDockableWindowManager();
        // start Console or make Console visible, if it is not already visible:
        wm.addDockableWindow("console");
        // get the Console instance:
        Console console = (Console) wm.getDockableWindow("console");
        // make sure the JCompiler shell is active:
        console.setShell(JCompilerPlugin.getShell());
        // run the "compilepkg" command:
        console.run("compilepkg");
    }
}

