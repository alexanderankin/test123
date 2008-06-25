
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

package voxspellcheck;

import java.util.Vector;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.TextUtilities;

import java.awt.geom.*;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.EventQueue;

public class VoxSpellPainter extends TextAreaExtension
{
    private enum MarkupMode {ALL_TEXT, NON_MARKUP, COMMENT_OR_LITERAL};
    
    private SpellCheck checker;
    private SpellCheck user_checker;
    private SpellCheck ignore_checker;
    private TextArea textarea;
    private String mode;
    private MarkupMode markup_mode;
    
    // non-letter chars that should not indicate the end of the word.
    static private String no_word_sep = "'";
    
    public void setMode(String mode_)
    {
        mode = mode_;
        String all_text_modes = jEdit.getProperty("options.voxspellcheck.all_text_modes");
        String non_markup_modes = jEdit.getProperty("options.voxspellcheck.non_markup_modes");
        String[] at_array = all_text_modes.split(" ");
        String[] nm_array = non_markup_modes.split(" ");
        java.util.Arrays.sort(at_array);
        java.util.Arrays.sort(nm_array);
        
        markup_mode = MarkupMode.COMMENT_OR_LITERAL;
        if (java.util.Arrays.binarySearch(at_array, mode) >= 0) {
            markup_mode = MarkupMode.ALL_TEXT;
        } else if (java.util.Arrays.binarySearch(nm_array, mode) >= 0) {
            markup_mode = MarkupMode.NON_MARKUP;
        }
    }
    
    public String getMode()
    {
        return mode;
    }
    
    public void setIgnoreChecker(SpellCheck ignore_checker_)
    {
        ignore_checker = ignore_checker_;
    }
    
    public SpellCheck getIgnoreChecker()
    {
        return ignore_checker;
    }
    
    private int getWordExtent(final String text, int offset)
    {
        int num_chars = 0;
        try {
            Character c = text.charAt(offset + num_chars++);
            
            if (Character.isSpaceChar(c)) {
                while (Character.isSpaceChar(c) || c.equals('\t')) {
                    c = text.charAt(offset + num_chars++);
                }
            } else if (Character.isLetterOrDigit(c)) {
                while (Character.isLetterOrDigit(c) || 
                       (no_word_sep.indexOf(c) != -1))
                {
                    c = text.charAt(offset + num_chars++);
                }
            } else {
                while (!Character.isLetterOrDigit(c)) {
                    c = text.charAt(offset + num_chars++);
                }
            }
        } catch (java.lang.IndexOutOfBoundsException ex) {
            ;
        }
        
        return --num_chars;
    }
    
    private Vector<String> getWords(TextArea textarea, int buf_start, int buf_end)
    {
        Vector<String> words = new Vector<String>();
        
        if (buf_start == textarea.getBufferLength())
            return words;
        
        // The last line includes the cursor position after the last char. This
        // compensates for that (otherwise getText throws an index exception).
        // FIXME: Not so sure about that.
        int len = java.lang.Math.min(buf_end - buf_start,
                                     textarea.getBufferLength() - buf_start);
        final String text = textarea.getText(buf_start, len);
        
        int start = 0;
        int end = 0;
        while (true) {
            end = getWordExtent(text, start);
            if (end == 0)
                break;
            words.add(text.substring(start, start + end));
            start += end;
        }
        return words;
    }
    
    public VoxSpellPainter(TextArea textarea_, 
                           SpellCheck checker_, 
                           SpellCheck user_checker_,
                           SpellCheck ignore_checker_)
    {
        super();
        this.textarea = textarea_;
        this.checker = checker_;
        this.user_checker = user_checker_;
        this.ignore_checker = ignore_checker_;
        setMode("text");
    }
    
    public void paintValidLine(java.awt.Graphics2D gfx, int screenLine, 
                               int physicalLine, int start, int end, int y)
    {
        // Make sure tokens are valid for this line
        JEditBuffer buffer = textarea.getBuffer();
        DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
        buffer.markTokens(textarea.getLineOfOffset(start), tokenHandler);
        
        String bg_string = jEdit.getProperty("view.status.background");
        Integer bg = Integer.decode(bg_string);
        // Default underline color is the inverse of the bg color at ~half
        // opacity.
        bg ^= ~1;
        bg = (bg & 0xffffff) | 0x88000000;
        java.awt.Color color = new java.awt.Color(bg, true);
        gfx.setColor(color);
        
        FontMetrics metrics = this.textarea.getPainter().getFontMetrics();
        int char_height = metrics.getHeight() - metrics.getLeading();
        
        Vector<String> words = getWords(textarea, start, end);
        
        int char_count = 0;
        for (String word : words) {
            skip: {
                String trim_word = word.trim();
                String low_word = trim_word.toLowerCase();
                
                if (trim_word.length() == 0)
                    break skip;
                if (low_word.length() == 1)
                    break skip;
                if (trim_word.endsWith("'") && trim_word.length() == 2)
                    break skip;
                Character c = trim_word.charAt(0);
                if (!Character.isLetter(c))
                    break skip;
                
                Token token;
                try {
                    // FIXME: why am I getting null tokens (ArrayIndexOutOfBoundsException)?
                    token = TextUtilities.getTokenAtOffset(tokenHandler.getTokens(), 
                                                           start + char_count - start);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    break skip;
                }
                switch (markup_mode) {
                case COMMENT_OR_LITERAL:
                    if ((token.id < Token.COMMENT1 || token.id > Token.COMMENT4) &&
                        (token.id < Token.LITERAL1 || token.id > Token.LITERAL4))
                    {
                        break skip;
                    }
                    break;
                case NON_MARKUP:
                    if (token.id != Token.NULL) {
                        break skip;
                    }
                    break;
                }

                boolean correct = checker.find(low_word) || 
                                  checker.find(trim_word) || 
                                  user_checker.find(low_word) || 
                                  user_checker.find(trim_word) ||
                                  ignore_checker.find(low_word) ||
                                  ignore_checker.find(trim_word);
                if (correct)
                    break skip;
    
                int x = textarea.offsetToXY(start + char_count).x;
                Point p;
                try {
                    p = textarea.offsetToXY(start + char_count + word.length());
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    break skip;
                }
                if (p == null)
                    break skip;
                
                int width = p.x - x;
                Rectangle r = new Rectangle(x, y + char_height - 1, width, 1);
                gfx.fill(r);
            }
            char_count += word.length();
        }
    }
}
