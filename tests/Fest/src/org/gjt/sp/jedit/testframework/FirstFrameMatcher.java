/*
* $Revision: 12869 $
* $Date: 2008-06-21 10:02:28 -0600 (Sat, 21 Jun 2008) $
* $Author: kerik-sf $
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

package org.gjt.sp.jedit.testframework;


//{{{ Imports

import java.awt.Frame;
import java.awt.Component;


//{{{	junit
import org.junit.*;
import static org.junit.Assert.*;
//}}}

//{{{	FEST...
import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
//}}}

///}}}


public class FirstFrameMatcher implements ComponentMatcher{
	private boolean found = false;
	private String title;

	public FirstFrameMatcher(String title){
		this.title = title;
	}

	public boolean matches(Component comp){
		if(found)return false;
		if(comp instanceof Frame){
			found = title.equals(((Frame)comp).getTitle());
		}
		return found;
	}
}
