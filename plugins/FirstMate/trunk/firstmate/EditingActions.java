/**
 * EditingActions.java - FirstMate Plugin
 *
 * Copyright 2006 Ollie Rutherfurd <oliver@jedit.org>
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
 *
 * $Id$
 */
package firstmate;

//{{{ imports
import org.gjt.sp.jedit.textarea.JEditTextArea;
//}}}

public class EditingActions
{
	//{{{ appendNewlineAndIndent() method
	public static void appendNewlineAndIndent(JEditTextArea textArea)
	{
		if(!textArea.getBuffer().isEditable())
		{
			textArea.getToolkit().beep();
			return;
		}
		textArea.goToEndOfLine(false);
		textArea.insertEnterAndIndent();
	}//}}}

	//{{{ insertAtEOL() method
	public static void insertAtEOL(JEditTextArea textArea, String suffix, boolean withNewLine)
	{
		if(!textArea.getBuffer().isEditable())
		{
			textArea.getToolkit().beep();
			return;
		}
		int caret = textArea.getCaretPosition();
		int eol = textArea.getBuffer().getLineEndOffset(textArea.getCaretLine());
		textArea.getBuffer().beginCompoundEdit();
		textArea.getBuffer().insert(eol-1,suffix);
		if(withNewLine)
		{
			textArea.goToEndOfLine(false);
			textArea.insertEnterAndIndent();
		}
		textArea.getBuffer().endCompoundEdit();
	}//}}}

	//{{{ newlineIndentNewline() method
	public static void newlineIndentNewline(JEditTextArea textArea)
	{
		if(!textArea.getBuffer().isEditable())
		{
			textArea.getToolkit().beep();
			return;
		}
		textArea.getBuffer().beginCompoundEdit();
		textArea.insertEnterAndIndent();
		textArea.goToPrevLine(false);
		textArea.goToEndOfLine(false);
		textArea.insertEnterAndIndent();
		textArea.getBuffer().endCompoundEdit();
	}//}}}
}

// :folding=explicit:collapseFolds=1:tabSize=4:noTabs=false:
