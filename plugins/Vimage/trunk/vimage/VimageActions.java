
/* 

Copyright (C) 2005 Ollie Rutherfurd <oliver@jedit.org>
Copyright (C) 2009 Matthew Gilbert 

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package vimage;

import java.lang.Class;
import java.lang.Math;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Registers;
import org.gjt.sp.jedit.gui.InputHandler;
import org.gjt.sp.jedit.TextUtilities;

public class VimageActions
{
    protected static int start_line;
    
    protected static void _(View view, String action)
    {
        EditAction ea = jEdit.getAction(action);
        if (ea == null)
            return;
        ea.invoke(view);
    }

    public static void goto_line_text_start(View view, TextArea text_area, boolean select)
    {
        int line_number = text_area.getCaretLine();
        String line_text = text_area.getLineText(line_number);
        int i = 0;
        while (java.lang.Character.isWhitespace(line_text.charAt(i)) && (i < line_text.length())) {
            ++i;
        }
        if (select) {
            int pos = text_area.getCaretPosition();
            text_area.setCaretPosition(text_area.getLineStartOffset(line_number) + i);
            text_area.resizeSelection(pos, text_area.getCaretPosition(), 0, false);
        } else {
            text_area.setCaretPosition(text_area.getLineStartOffset(line_number) + i);
        }
    }

	/**
	 * Support for f, F, t, and T.
	 */
	public static void find_char_on_line(View view, boolean forward,
                                         boolean select, int offset,
                                         String ch)
	{
		Buffer buffer = view.getBuffer();
		int caretLine = view.getTextArea().getCaretLine();
		int startOffset = view.getTextArea().getLineStartOffset(caretLine);
		int endOffset = view .getTextArea().getLineEndOffset(caretLine);
		int caret = view.getTextArea().getCaretPosition();
		int index = -1;

		String text = null;
		if(forward)
		{
			if(caret+1 >= buffer.getLength())
			{
				return;
			}
			int len = endOffset - (caret+1);
			if(endOffset >= buffer.getLength())
				len--;
			text = view.getTextArea().getText(caret+1,len);
			index = text.indexOf(ch);
		}
		else
		{
			text = view.getTextArea().getText(startOffset,caret-startOffset);
			index = text.lastIndexOf(ch);
		}

		if(index > -1)
		{
			if(forward)
				index = caret + index + 1;
			else
				index = startOffset + index;

			if(offset != 0)
				index += offset;

			if(select)
			{
				// extend selection through match char
				if(forward)
					index++;
				int mn = Math.min(caret, index);
				int mx = Math.max(caret, index);
				Selection s = new Selection.Range(mn,mx);
				if(view.getTextArea().isMultipleSelectionEnabled())
					view.getTextArea().addToSelection(s);
				else
					// XXX don't want to add if disjointed
					view.getTextArea().addToSelection(s);
			}
			view.getTextArea().moveCaretPosition(index);
		}
	}

    public static void goto_line(View view, TextArea text_area, int count, boolean select)
    {
        if (count == 0) {
            if (select)
                _(view, "select-document-end");
            else
                _(view, "document-end");
            return;
        }

        // getLineStartOffset takes 0 based line numbers, so subtract 1 from
        // count.
        int line = count - 1;
        if (line >= text_area.getLineCount())
            return;
        text_area.setCaretPosition(text_area.getLineStartOffset(line));
        if (select) {
            int pos = text_area.getCaretPosition();
            Selection sel = text_area.getSelectionAtOffset(pos);
            if (sel == null)
                text_area.resizeSelection(pos, text_area.getCaretPosition(), 0, false);
            else
                text_area.resizeSelection(sel.getStart(), text_area.getCaretPosition(), 0, false);
        }
    }

    public static void cut(View view, VimageInputHandler mode, TextArea text_area, char reg_name)
    {
        Selection ds = view.getTextArea().getSelectionAtOffset(view.getTextArea().getCaretPosition());
        if (ds == null) {
            return;
        }
        if (mode.isIterating()) {
            Registers.append(text_area, reg_name, "", true);
        } else {
            Registers.cut(text_area, reg_name);
        }
    }

    public static void copy(View view, VimageInputHandler mode, TextArea text_area, char reg_name)
    {
        Registers.copy(text_area, reg_name);
    }

    public static void paste(View view, TextArea text_area, char reg_name, boolean after_caret)
    {
        int pos = text_area.getCaretPosition();
        Selection sel = text_area.getSelectionAtOffset(pos);
        if (after_caret && (sel == null)) {
            int line_end = text_area.getLineEndOffset(text_area.getCaretLine());
            pos += 1;
            // Don't let after_caret move past the newline at the end of the
            // line, so take min of pos and (line_end - 1).
            // Ordering is important here. (line_end - 1) may be -1, so make sure the max 
            // call comes second.
            pos = java.lang.Math.min(line_end - 1, pos);
            pos = java.lang.Math.max(0, pos);
        }
        
        if (sel != null) {
            Registers.paste(text_area, reg_name);
            return;
        }
        
        // Make a guess that anything that ends with a newline is line-wise.
        // eek.
        Registers.Register reg = Registers.getRegister(reg_name);
        if (reg == null)
            return;
        Buffer b = view.getBuffer();
        String reg_text = reg.toString();
        boolean linewise = reg_text.endsWith("\n");
        if (sel == null) {
            if (linewise) {
                if (after_caret) {
                    pos = text_area.getLineEndOffset(text_area.getCaretLine());
                } else {
                    pos = text_area.getLineStartOffset(text_area.getCaretLine());
                }
            }
        }
        pos = java.lang.Math.min(text_area.getBufferLength(), pos);
        pos = java.lang.Math.max(0, pos);
        b.insert(pos, reg_text);
        if (!linewise) {
            if (after_caret) {
                text_area.setCaretPosition(pos + reg_text.length());
            } else {
                text_area.setCaretPosition(pos);
            }
        }
    }

    public static void swap_case(TextArea text_area)
    {
        text_area.getBuffer().beginCompoundEdit();
        try {
            Selection[] selections = text_area.getSelection();
            if (selections.length == 0) {
                int pos = text_area.getCaretPosition();
                Selection.Range sel = new Selection.Range(pos, pos + 1);
                text_area.setSelection(sel);
                selections = text_area.getSelection();
            } 
            for (int i = 0; i < selections.length; ++i) {
                Selection sel = selections[i];
                int start = sel.getStart();
                int end = sel.getEnd();
                String text = text_area.getText(start, end-start);
                StringBuffer new_text = new StringBuffer();
                for (int j = 0; j < text.length(); ++j) {
                    char c = text.charAt(j);
                    if (java.lang.Character.isUpperCase(c)) {
                        c = java.lang.Character.toLowerCase(c);
                    } else {
                        c = java.lang.Character.toUpperCase(c);
                    }
                    new_text.append(c);
                }
                text_area.getBuffer().remove(start, end - start);
                text_area.getBuffer().insert(start, new_text.toString());
            }
        } catch (java.lang.Exception ex) {
            Log.log(Log.DEBUG, ex, ex);
        } finally {
            text_area.getBuffer().endCompoundEdit();
        }
    }
    
    public static void select_line(TextArea text_area)
    {
        start_line = text_area.getCaretLine();
        select_line(text_area, 0);
    }

    public static void select_line(TextArea text_area, int line_offset)
    {
        if (text_area.getBufferLength() == 0)
            return;
        
        int pos = text_area.getCaretPosition();
        int caret_line = text_area.getCaretLine();
        int max_line = Math.max(0, text_area.getLineCount() - 1);
        
        Selection orig = text_area.getSelectionAtOffset(pos);
        // If caret is at the end of the document, create at least a 1 character
        // selection. This fixes the case where the last line in a document is a
        // blank line. Without (pos - 1) then no selection will be created for
        // that line (jEdit doesn't do 0 width selections). We want to select
        // the newline of the previous line anyway.
        if ((orig == null) && (pos == text_area.getBufferLength())) {
            orig = new Selection.Range(pos - 1, pos);
        } else if (orig == null) {
            // No selection, just select the current line
            int line = text_area.getCaretLine();
            int start = text_area.getLineStartOffset(line);
            int end = Math.min(text_area.getLineEndOffset(line), text_area.getBufferLength());
            
            if (end == text_area.getBufferLength()) {
                // Add in the previous line like below.
                start = Math.max(0, start - 1);
            }
            Selection sel = new Selection.Range(start, end);
            text_area.setSelection(sel);
            return;
        }
        
        int sel_line_start = text_area.getLineOfOffset(orig.getStart());
        int sel_line_end = text_area.getLineOfOffset(orig.getEnd());
        
        // The following are fixups to how lines are selected. jEdit includes

        // If caret is at the end, then we've selected one character back, so
        // increment sel_line_start.
        if (orig.getEnd() == text_area.getBufferLength()) {
            sel_line_start = text_area.getLineOfOffset(Math.min(text_area.getBufferLength(), 
                                                                orig.getStart() + 1));
            if (text_area.getLineText(max_line).equals("") && 
                (sel_line_start < (max_line - 1)) )
            {
                sel_line_end = text_area.getLineOfOffset(Math.max(0, orig.getEnd() - 1));
            }
        }
        // Subtract 1 because line selections always include the newline which
        // extends to the next line, unless already at the end.
        if (orig.getEnd() != text_area.getBufferLength()) {
            sel_line_end = text_area.getLineOfOffset(Math.max(0, orig.getEnd() - 1));
        }
        
        if (sel_line_end > caret_line) {
            // Adjust the end line since we've moved past the caret
            sel_line_end += line_offset;
            sel_line_end = Math.min(sel_line_end, max_line);
            text_area.scrollTo(sel_line_end, 0, true);
        } else {
            sel_line_start += line_offset;
            // If at the last line, then sel_line_end == caret_line so we end up
            // here, we still shouldn't try to go past the last line.
            sel_line_start = Math.min(Math.max(sel_line_start, 0), max_line);
            text_area.scrollTo(sel_line_start, 0, true);
        }
        
        // Flip-flop start and end if line_offset has caused start and end to
        // flip-flop.
        int start = Math.min(sel_line_start, sel_line_end);
        int end = Math.max(sel_line_start, sel_line_end);
        
        int caret_start = text_area.getLineStartOffset(start);
        int caret_end = text_area.getLineEndOffset(end);
        caret_end = Math.min(caret_end, text_area.getBufferLength());
        
        // This is causing not to be able to re-select only the last line.
        // FIXME: some check.
        if (caret_end == text_area.getBufferLength()) {
            // Want to delete the last line, so delete the previous new-line
            // if it exists.
            caret_start -= 1;
        }
        caret_start = Math.max(0, caret_start);
        
        Selection sel = new Selection.Range(caret_start, caret_end);
        text_area.setSelection(sel);
    }
    
	/**
	 * Scroll the text area relative to the caret position,
	 * depending on the value of <code>position</code>.
	 * @param textArea
	 * @param select whether or not to select text
	 * @param count new caret line, if > 0
	 * @param position -1 = caret at top, 0 = center, 1 = bottom
	 */
	public static void scroll(JEditTextArea textArea, boolean select, int position)
	{
		int firstLine = textArea.getFirstLine();
		int visibleLines = textArea.getVisibleLines();
		int caretLine = textArea.getScreenLineOfOffset(textArea.getCaretPosition());
		int offset = 0;

		if(position < 0)
			offset = (firstLine + caretLine) - textArea.getElectricScroll();
		else if(position > 0)
			// not sure why -2 is needed, but without it, the 
			// caret line ends up in the electric scroll area
			offset = firstLine - (((visibleLines - caretLine) - textArea.getElectricScroll())-2);
		else
			offset = (firstLine + caretLine - (visibleLines / 2));

		textArea.setFirstLine(offset);
	}
}

