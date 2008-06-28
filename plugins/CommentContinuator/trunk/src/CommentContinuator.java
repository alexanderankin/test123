
/*
Copyright (C) 2008 Matthew Gilbert

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

package commentcontinuator;

import java.lang.String;
import java.lang.StringBuffer;
import java.lang.StringBuilder;
import java.lang.Character;
import java.util.StringTokenizer;

import java.awt.EventQueue;

import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.StandardUtilities;
import org.gjt.sp.jedit.textarea.TextArea;

import textobjects.TextObjectsPlugin;

public class CommentContinuator extends BufferAdapter
{
    protected static boolean in_comment(JEditBuffer buffer, final String text, int pos)
    {
        boolean inComment = false;
        DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
        int line = buffer.getLineOfOffset(pos);
        buffer.markTokens(line, tokenHandler);
        int offset = pos - buffer.getLineStartOffset(line);
        while (offset >= text.length())
            --offset;
        Token token = TextUtilities.getTokenAtOffset(tokenHandler.getTokens(), offset);
        if (token.id >= Token.COMMENT1 && token.id <= Token.COMMENT4) {
            return true;
        }
        return false;
    }
    
    protected static String get_prefix(final String text)
    {
        int start, pos;
        start = pos = StandardUtilities.getLeadingWhiteSpace(text);
        // If the line was only whitespace, return
        if (pos == text.length())
            return "";
        // Grab all non-alphanumeric characters.
        while(pos < text.length()) {
            Character c = text.charAt(pos);
            if (Character.isLetterOrDigit(c) ||
                Character.isWhitespace(c))
            {
                break;
            }
            ++pos;
        }
        return text.substring(start, pos);
    }
    
    public static void format_comment(TextArea ta, int pos)
    {
        JEditBuffer buffer = ta.getBuffer();
        int maxLineLength = new Integer(buffer.getStringProperty("maxLineLen"));
        int tabSize = buffer.getTabSize();
        StringBuilder buf = new StringBuilder();
        
        Selection sel = TextObjectsPlugin.comment(ta, pos, false);
        if (sel == null)
            return;
        
        // Change selection to include to the beginning of the line 
        sel = new Selection.Range(ta.getLineStartOffset(ta.getLineOfOffset(sel.getStart())),
                                  sel.getEnd());
        
        String text = ta.getText(sel.getStart(), sel.getEnd() - sel.getStart());
        
        String lineComment = buffer.getContextSensitiveProperty(pos, "lineComment");
        String commentStart = buffer.getContextSensitiveProperty(pos, "commentStart");
        String commentEnd = buffer.getContextSensitiveProperty(pos, "commentEnd");
        
        String prefix = "";
        String leadingPrefix = "";
        
        // TODO: make sure nothing needs to check for a lineComment here. Figure
        // out if there is a line with a prefix.
        int line = ta.getLineOfOffset(sel.getStart());
        int end_line = ta.getLineOfOffset(sel.getEnd());
        if (line == end_line) {
            // No lines with a prefix, make it line-up with commentStart plus an
            // additional space since I like a space between the comment start
            // and the first character. Some languages, like Python, don't have
            // a commentStart. Just use the line comment in that case.
            if (commentStart == null)
                prefix = lineComment.replaceAll(".", " ") + " ";
            else
                prefix = commentStart.replaceAll(".", " ") + " ";
        } else {
            String nextLine = buffer.getLineText(line + 1);
            prefix = get_prefix(nextLine) + " ";
            // prefix already has 1 appended space, so subtract that from extra.
            int extra = StandardUtilities.getLeadingWhiteSpace(nextLine) -
                        StandardUtilities.getLeadingWhiteSpace(text) - 1;
            if (extra > 0) {
                char[] spaces = new char[extra];
                for (int i = 0; i < extra; ++i)
                    spaces[i] = ' ';
                prefix += new String(spaces);
            }
        }
        // Need a leading prefix because formatParagraph skips existing
        // lineComments. So, need this to supply the first comment and then
        // formatParagraph supplies the rest.
        if ((lineComment != null) && (prefix.startsWith(lineComment)))
            leadingPrefix = lineComment + " ";
        formatParagraph(text, maxLineLength, tabSize, buf, leadingPrefix, 
                        prefix);
        ta.setSelectedText(sel, buf.toString());
        ta.setCaretPosition(pos);
    }
    
    public static void format(TextArea ta)
    {
        int pos = ta.getCaretPosition();
        int line = ta.getLineOfOffset(pos);
        Selection sel = ta.getSelectionAtOffset(pos);
        if (sel == null) {
            if (in_comment(ta.getBuffer(), ta.getLineText(line), pos)) {
                format_comment(ta, pos);
                return;
            }
            sel = TextObjectsPlugin.paragraph(ta, pos, false);
        }
        if (sel == null)
            return;
        
        // Change selection to include to the beginning of the line 
        sel = new Selection.Range(ta.getLineStartOffset(ta.getLineOfOffset(sel.getStart())),
                                  sel.getEnd());
        JEditBuffer buffer = ta.getBuffer();
        int maxLineLength = new Integer(buffer.getStringProperty("maxLineLen"));
        int tabSize = buffer.getTabSize();
        StringBuilder buf = new StringBuilder();

        formatParagraph(ta.getText(sel.getStart(), sel.getEnd() - sel.getStart()),
                        maxLineLength, tabSize, buf,
                        get_prefix(ta.getLineText(line)) + " ", 
                        get_prefix(ta.getLineText(line)) + " ");
        ta.setSelectedText(sel, buf.toString());
        ta.setCaretPosition(pos);
    }

	protected static void formatParagraph(String text, int maxLineLength,
                                          int tabSize, StringBuilder buf,
                                          String leadingPrefix, String prefix)
    {
        // align everything to paragraph's leading indent
		int leadingWhitespaceCount = StandardUtilities.getLeadingWhiteSpace(text);
		String leadingWhitespace = text.substring(0,leadingWhitespaceCount);
		int leadingWhitespaceWidth = StandardUtilities.getLeadingWhiteSpaceWidth(text,tabSize);
        int prefixWidth = prefix.length();
        int leadingWidth = leadingWhitespaceWidth + prefixWidth;

		buf.append(leadingWhitespace);
        buf.append(leadingPrefix);
        
        String trimmed_prefix = prefix.trim();
        
		int lineLength = leadingWhitespaceWidth + leadingPrefix.length();
		StringTokenizer st = new StringTokenizer(text);
		while(st.hasMoreTokens())
		{
			String word = st.nextToken();
            if (word.equals(trimmed_prefix))
                continue;
			if(lineLength == leadingWidth)
			{
                // Do nothing
			}
			else if(lineLength + word.length() + 1 > maxLineLength)
			{
				buf.append('\n');
				buf.append(leadingWhitespace);
                buf.append(prefix);
				lineLength = leadingWidth;
			}
			else
			{
				buf.append(' ');
				lineLength++;
			}
			buf.append(word);
			lineLength += word.length();
		}
    }

    public void contentInserted(JEditBuffer buffer, int startLine, int offset,
                                int numLines, int length)
    {
        //Log.log(Log.DEBUG, this, "startLine "+startLine+" offset "+offset+
        //        " numLines "+numLines+" length "+length);
        if ((numLines == 1) && (length == 1)) {
            
            int lineEnd = buffer.getLineEndOffset(startLine);
            int lineStart = buffer.getLineStartOffset(startLine);
           
            if (lineEnd == lineStart)
                return;
            
            final String text = buffer.getLineText(startLine);
            
            // Can't be in a comment without text
            if (text.length() == 0)
                return;
            
            if (in_comment(buffer, text, offset)) {
                
                String lineComment = buffer.getContextSensitiveProperty(offset, "lineComment");
                String commentStart = buffer.getContextSensitiveProperty(offset, "commentStart");
                String commentEnd = buffer.getContextSensitiveProperty(offset, "commentEnd");
                
                // If the block comment has been closed, nothing todo
                if ((commentEnd != null) && text.endsWith(commentEnd))
                    return;
                
                // Find the start of this lines comment char
                int pos = 0;
                if (!text.trim().equals("")) {
                    while ((pos < (lineEnd - lineStart)) && 
                           Character.isWhitespace(text.charAt(pos)))
                    {
                        ++pos;
                    }
                }
                // We're either at the start of the comment chars, in which case
                // we should match lineComment or commentStart, or we're in a block
                // comment and will use a prefix char if found.
                if ((lineComment != null) && text.substring(pos).startsWith(lineComment)) {
                    String extra_insert = "";
                    if (buffer.getLineText(startLine + 1).equals(""))
                        extra_insert = " ";
                    buffer.insert(lineEnd, lineComment + extra_insert);
                    return;
                } else {
                    StringBuffer sb = new StringBuffer();
                    // Only append leading space after the first line of the
                    // comment, after that idealIndentForLine takes care of
                    // things.
                    boolean first_line = text.substring(pos).startsWith(commentStart);
                    if (first_line) {
                        for (int i = 0; i < (commentStart.length() - 1); ++i) {
                            sb.append(' ');
                        }
                        // FIXME: why 2?
                        sb.append(' ');
                        sb.append(' ');
                    } else {
                        sb.append(get_prefix(buffer.getLineText(startLine)));
                    }
                    // Hack for c style comments
                    /*
                    if (commentStart.equals("/*")) {
                        sb.append('*');
                    } else if (first_line) {
                        sb.append(' ');
                        sb.append(' ');
                    }
                    */
                    final int nextLine = startLine + 1;
                    final JEditBuffer fb = buffer;
                    final String fs = sb.toString();
                    final Runnable task = new Runnable() {
                        public void run() {
                            int start = fb.getLineStartOffset(nextLine);
                            int end = fb.getLineEndOffset(nextLine);
                            int indent = fb.getIdealIndentForLine(nextLine);
                            String text = fb.getLineText(nextLine);
                            
                            // Automatically inserts a space for us on wrap
                            String extra_insert = "";
                            if (!fs.trim().equals("")) {
                                if (indent >= text.length()) {
                                    extra_insert = " ";
                                } else if (text.charAt(indent) != ' ') {
                                    extra_insert = " ";
                                }
                            }
                            fb.insert(start + indent, fs + extra_insert);
                        }
                    };
                    EventQueue.invokeLater(task);
                }
            }
        }
    }
}