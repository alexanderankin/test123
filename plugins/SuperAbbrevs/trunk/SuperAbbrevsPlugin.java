/*
*  
* Copyright (C) 1999, 2001 Sune Simonsen
*
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

import java.util.*;
import java.io.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.View;
import superabbrevs.SuperAbbrevs;

public class SuperAbbrevsPlugin extends EditPlugin {
	
	public static final String NAME = "SuperAbbrevs";
		
	public void start(){
		SuperAbbrevs.makeDefaults();
	}
	
	public static void nextAbbrev(JEditTextArea textArea){
		SuperAbbrevs.nextAbbrev(textArea);
	}
	
	public static void prevAbbrev(JEditTextArea textArea){
		SuperAbbrevs.prevAbbrev(textArea);
	}
	
	public static void expandAbbrev(View view){
		SuperAbbrevs.expandAbbrev(view);
	}
	
	public static boolean enabled(Buffer buffer){
		return SuperAbbrevs.enabled(buffer);
	}
}
