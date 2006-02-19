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
import org.gjt.sp.jedit.*;
import superabbrevs.SuperAbbrevs;

public class SuperAbbrevsPlugin extends EditPlugin {

	public static final String NAME = "SuperAbbrevs";
	
	public void start(){
		SuperAbbrevs.makeDefaults();
	}
	
	public static void shiftTab(View view, JEditTextArea textArea, Buffer buffer){
		
		if (SuperAbbrevs.enabled(buffer)){
			SuperAbbrevs.prevAbbrev(textArea);
		} else if (!SuperAbbrevs.expandAbbrev(view,true)) {	
			textArea.shiftIndentLeft();
		} 
	}
	
	
	public static void tab(View view, JEditTextArea textArea, Buffer buffer){
		int line = textArea.getCaretLine();
		
		//beep if the textarea is not editable 
		if (!textArea.isEditable()){
			textArea.getToolkit().beep();
			return;
		}
		
		if (SuperAbbrevs.enabled(buffer)){
			SuperAbbrevs.nextAbbrev(textArea);
		} else if(0 < textArea.getSelectionCount()){
			textArea.insertTabAndIndent();
		} else if (!SuperAbbrevs.expandAbbrev(view,false)) {	
			textArea.insertTabAndIndent();
		}
	}
	
	public static void showDialog(View view, JEditTextArea textArea, Buffer buffer){
		int line = textArea.getCaretLine();
		
		//beep if the textarea is not editable 
		if (!textArea.isEditable()){
			textArea.getToolkit().beep();
			return;
		}
		
		 if(1 < textArea.getSelectionCount() || SuperAbbrevs.enabled(buffer) || 
			 !SuperAbbrevs.showAbbrevDialog(view)){
			
		 	textArea.getToolkit().beep();
		} 
	}
}
