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
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.jEdit;

import java.util.ArrayList;
import java.util.List;

public class LaTeXPlugin extends EditPlugin {

    private static List<EBComponent> editBusList = new ArrayList<EBComponent>();
        
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
        for (EBComponent component: editBusList) {
            EditBus.removeFromBus(component);
        }
        editBusList.clear();
    }

    public void start() {
    	String options = jEdit.getProperty("latex.compile.options") ;
    	if (options == null || options.length() == 0) {    		
    		options = jEdit.getProperty("latex.compile.c-errors");
    		if (OperatingSystem.isUnix()) options = jEdit.getProperty("latex.compile.c-errors.linux");
    		jEdit.setProperty("latex.compile.options", options);    		
    	}
    }
}
