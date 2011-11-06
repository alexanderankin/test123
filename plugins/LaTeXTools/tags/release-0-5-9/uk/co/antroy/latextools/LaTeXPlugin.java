/*
 * LaTeXPlugin.java
 * Copyright (C) 2002 Anthony Roy
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
package uk.co.antroy.latextools;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

import sidekick.SideKickPlugin;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LaTeXPlugin extends EditPlugin {

    private static List editBusList = new ArrayList();
        
    //~ Methods ...............................................................
    
    public static void addToEditBus(EBComponent component){
            EditBus.addToBus(component);
            editBusList.add(component);
    }

    public static void removeFromEditBus(EBComponent component){
            EditBus.removeFromBus(component);
            editBusList.remove(component);
    }
    
    public void stop(){
        for (Iterator it = editBusList.iterator(); it.hasNext(); ){
            EBComponent component = (EBComponent) it.next();
            EditBus.removeFromBus(component);
            it.remove();
        }
    }

    public void start() {
        if(System.getProperty("os.name").indexOf("Linux")!= -1 &&
           !jEdit.getBooleanProperty("latex.compile.c-errors.initialized")) {
            
            jEdit.setBooleanProperty("latex.compile.c-errors.initialized", true);
            String linuxErrorStyleSwitch = jEdit.getProperty("latex.compile.c-errors.linux");
            jEdit.setProperty("latex.compile.c-errors", linuxErrorStyleSwitch);
        }
    }
}
