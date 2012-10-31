/*
 * JakartaCommonsPlugin.java
 * Copyright (c) 2002 Konstantin Pribluda (kpribluda@j-tec-team.de)
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


package jakartacommons;

import java.io.File;

import org.apache.commons.lang.StringEscapeUtils;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.TextArea;
import org.w3c.dom.ranges.Range;

/**
* Contains implementations for actions that use jakarta commons libs.
*/
public class JakartaCommonsPlugin extends EditPlugin {

	static Selection[] selections;
	/** Selects word before caret if no selection exists.
	    @return the selected text */
	public static String selectedText(TextArea textArea, Buffer buffer) {
		if (textArea.getSelectionCount() == 0) {
			int line = textArea.getCaretLine();
			int lineLength = textArea.getLineLength(line);
			if (lineLength == 0)
				return null;
			String lineText = textArea.getLineText(line);
			int lineStart = textArea.getLineStartOffset(line);
			int offset = textArea.getCaretPosition() - lineStart;
/*			if (offset == lineLength)
				return null; */
			String noWordSep =  buffer.getProperty("noWordSep") + "\\";
			int wordStart = TextUtilities.findWordStart(lineText, offset-1, noWordSep);
			int wordEnd = TextUtilities.findWordEnd(lineText, offset, noWordSep);
			String retval = textArea.getText(lineStart + wordStart, wordEnd - wordStart);
			Selection.Range range = new Selection.Range(lineStart+wordStart, wordEnd);
			textArea.setSelection(range);
			selections = textArea.getSelection();
			return retval;
		}
		else {
			selections = textArea.getSelection();
			int len = selections[0].getEnd() - selections[0].getStart();
			return buffer.getText(selections[0].getStart(), len);
		}
	}
	/** Unescape unicode characters that are selected in current TextArea
	    using Java conventions.
	    @author Alan Ezust
	*/
	public static void unescapeUnicodeSelection(TextArea textArea, Buffer buffer) {
		String text = selectedText(textArea, buffer);
		if (text == null) return;
		String esctext = StringEscapeUtils.unescapeJava(text);
		buffer.remove(selections[0].getStart(), text.length());
		buffer.insert(selections[0].getStart(), esctext);
	}

	/** Escape unicode characters that are selected in current TextArea
	    using Java conventions.
	    @author Alan Ezust
	*/
	public static void escapeUnicodeSelection(TextArea textArea, Buffer buffer) {
		String text = selectedText(textArea, buffer);
		if (text == null) return;
		String esctext = StringEscapeUtils.escapeJava(text);
		buffer.remove(selections[0].getStart(), text.length());
		buffer.insert(selections[0].getStart(), esctext);
	}
}
