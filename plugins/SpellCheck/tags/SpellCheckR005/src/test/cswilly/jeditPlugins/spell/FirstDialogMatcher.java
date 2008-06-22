/*
* $Revision$
* $Date$
* $Author$
*
* Copyright (C) 2008 Eric Le Lay
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

package cswilly.jeditPlugins.spell;


//{{{ Imports

import java.awt.Dialog;
import java.awt.Component;


//{{{	junit
import org.junit.*;
import static org.junit.Assert.*;
//}}}

//{{{	FEST...
import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
//}}}

///}}}


public class FirstDialogMatcher implements ComponentMatcher{
	private boolean found = false;
	private String title;
	
	public FirstDialogMatcher(String title){
		this.title = title;
	}
	
	public boolean matches(Component comp){
		if(found)return false;
		if(comp instanceof Dialog){
			//System.err.println("got this window : "+((JDialog)comp).getTitle());
			found = title.equals(((Dialog)comp).getTitle());
		}
		//System.out.println("discarded:"+comp);
		return found;
	}
}
