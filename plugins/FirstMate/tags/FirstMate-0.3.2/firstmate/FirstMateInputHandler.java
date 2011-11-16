/**
 * FirstMateInputHandler.java - FirstMate plugin
 *
 * Copyright 2006-2008 Ollie Rutherfurd <oliver@rutherfurd.net>
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
import java.awt.event.KeyEvent;
import org.gjt.sp.jedit.gui.InputHandler;
import org.gjt.sp.jedit.gui.DefaultInputHandler;
import org.gjt.sp.jedit.gui.KeyEventTranslator;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;
//}}}

/**
 * Input handle to do Auto-Character Pairing, etc...
 */
public class FirstMateInputHandler extends DefaultInputHandler
{
	//{{{ constants
	private static final String APOSTROPHE = "apostrophe";
	private static final String QUOTE = "quote";
	private static final String PARENTHESIS = "parenthesis";
	private static final String BRACKET = "bracket";
	private static final String BRACE = "brace";
	//}}}

	//{{{ FirstMateInputHandler(View, DefaultInputHandler)
	public FirstMateInputHandler(View view, DefaultInputHandler defaultInputHandler)
	{
		super(view,defaultInputHandler);
		this.view = view;
		this.defaultInputHandler = defaultInputHandler;
	} //}}}

	//{{{ handleKey() method
	public boolean handleKey(KeyEventTranslator.Key keyStroke, boolean dryRun)
	{
		if(dryRun)
			return defaultInputHandler.handleKey(keyStroke, dryRun);

		Buffer buffer = view.getBuffer();
		JEditTextArea textArea = view.getTextArea();
		int caret = textArea.getCaretPosition();
		String mode = buffer.getRuleSetAtOffset(caret).getModeName();
		String insert = null;
		boolean handled = false;
		Selection[] selections = textArea.getSelection();
		// XXX don't check every time
		boolean wrapSelections = FirstMatePlugin.getWrapSelections();

		// figure out what to do
		if(keyStroke.key == KeyEvent.VK_BACK_SPACE)
		{
			// XXX don't check every time
			if(canUndo && FirstMatePlugin.getUndoOnBackspace())
			{
				buffer.remove(caret-1,2);
				canUndo = false;
				handled = true;
			}
		}
		else if(keyStroke.input == '"' && getPairEnabled(mode,QUOTE))
			insert = "\"\"";
		else if(keyStroke.input == '\'' && getPairEnabled(mode,APOSTROPHE))
		{
			boolean pair = true;
			if(selections.length == 0
				&& FirstMatePlugin.getNoApostropheAfterLetter())
			{
				if(caret > 0)
				{
					char prev = buffer.getText(caret-1,1).charAt(0);
					if((prev == 'r' || prev == 'u') && "python".equals(buffer.getMode().getName()))
						pair = true;
					else if(Character.isLetter(prev))
						pair = false;
				}
			}
			if(pair)
				insert = "''";
		}
		else if(keyStroke.input == '[' && getPairEnabled(mode,BRACE))
			insert = "[]";
		else if(keyStroke.input == '{' && getPairEnabled(mode,BRACKET))
			insert = "{}";
		else if(keyStroke.input == '(' && getPairEnabled(mode,PARENTHESIS))
			insert = "()";
		// XXX might want to check that either prev == > or if selection
		//     that selection start -1 == >
		// XXX might want to preserve selection
		else if(keyStroke.input == '>' && selections.length <= 1 
			&& caret > 0 && buffer.getText(caret-1,1).charAt(0) == '>'
			&& jEdit.getProperty("mode."+mode+".xml.completion-info","").length() > 0)
		{
			String selected = "";
			if(selections.length == 1)
				selected = buffer.getText(selections[0].getStart(),
					selections[0].getEnd()-selections[0].getStart());
			buffer.beginCompoundEdit();
			textArea.insertEnterAndIndent();
			textArea.setCaretPosition(caret);
			textArea.insertEnterAndIndent();
			textArea.shiftIndentRight();
			textArea.setSelectedText(selected);
			buffer.endCompoundEdit();
			handled = true;
		}

		if(insert != null)
		{
			if(FirstMatePlugin.getIgnoreNext())
				FirstMatePlugin.setIgnoreNext(false);
			else if(selections.length == 0)
			{
				buffer.beginCompoundEdit();
				buffer.insert(caret, insert);
				view.getTextArea().setCaretPosition(caret+1);
				buffer.endCompoundEdit();
				handled = true;
			}
			else if(wrapSelections)
			{
				// this is the amount to move the caret to keep it in the 
				// same position after 1 or more insertions have been done
				int caretOffset = 0;
				// new selections, updated to reflect inserted characters
				Selection[] wrapped = new Selection[selections.length];
				String prefix = insert.substring(0,1);
				String suffix = insert.substring(1);
				int caretPos = textArea.getCaretPosition();
				buffer.beginCompoundEdit();
				for(int i=selections.length-1; i>=0; i--)
				{
					Selection s = selections[i];
					if(caretPos >= s.getEnd())
						caretOffset += 1;
					if(i > 0 && caretPos > s.getStart())
						caretOffset += 1;
					if(s instanceof Selection.Rect)
					{
						Selection.Rect r = (Selection.Rect)s;
						int startCol = r.getStartColumn(textArea.getBuffer());
						int endCol = r.getEndColumn(textArea.getBuffer());
						int startLine = textArea.getLineOfOffset(s.getStart());
						int endLine = textArea.getLineOfOffset(s.getEnd());
						for(int lineno = endLine; lineno >= startLine; lineno--)
						{
							int lineStart = textArea.getLineStartOffset(lineno);
							buffer.insert(lineStart + endCol, suffix);
							buffer.insert(lineStart + startCol, prefix);
						}
						wrapped[i] = new Selection.Rect(textArea.getBuffer(),
														startLine, startCol+1,
														endLine, endCol+1);
					}
					else
					{
						int shift = (i*2)+1;	// shift selection to account inserted characters
						wrapped[i] = new Selection.Range(s.getStart()+shift, s.getEnd()+shift);
						buffer.insert(s.getEnd(), suffix);
						buffer.insert(s.getStart(), prefix);
					}
				}
				textArea.setSelection(wrapped);
				if(caretOffset != 0)
					textArea.moveCaretPosition(textArea.getCaretPosition()-caretOffset);
				buffer.endCompoundEdit();
				handled = true;
			}
		}

		// if we just inserted something, set
		// canUndo flag, so BACKSPACE can remove both
		canUndo = handled == true && insert != null;

		// XXX changed from 'return this.defaultInputHandler.handleKey(keyStroke);'
		if(!handled)
			return super.handleKey(keyStroke, dryRun);
		return true;
	} //}}}

	//{{{ getDefaultHandler() method
	public InputHandler getDefaultHandler()
	{
		return this.defaultInputHandler;
	} //}}}

	//{{{ getPairEnabled() method
	private boolean getPairEnabled(String mode, String type)
	{
		return jEdit.getBooleanProperty("mode."+mode+".pair."+type,true);
	}//}}}

	//{{{ privates
	private View view;
	private InputHandler defaultInputHandler;
	private boolean canUndo = false;
	//}}}
}

// :folding=explicit:collapseFolds=1:tabSize=4:noTabs=false:
