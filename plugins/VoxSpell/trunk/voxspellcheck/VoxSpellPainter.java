
/*
Copyright (C) 2008, 2009 Matthew Gilbert

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

import java.util.ArrayList;
import java.util.Iterator;

import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.TextUtilities;

import java.awt.geom.*;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Point;

import errorlist.*;

public class VoxSpellPainter extends TextAreaExtension
{
    private enum MarkupMode {ALL_TEXT, NON_MARKUP, COMMENT_OR_LITERAL};
    
    private SpellCheck checker;
    private SpellCheck user_checker;
    private SpellCheck ignore_checker;
    private TextArea textarea;
    private String mode;
    private MarkupMode markup_mode;
    private DefaultErrorSource error_source;
    
    // non-letter chars that should not indicate the end of the word.
    static private String no_word_sep = "'\u2019";
    
    static public java.awt.Color getUnderlineColor()
    {        
        String s;
        boolean b;
        
        s = jEdit.getProperty("options.voxspellcheck.use_custom_color");
        b = s.equals("true");
        Integer bg;
        if (b) {
            bg = jEdit.getColorProperty("options.voxspellcheck.custom_color").getRGB();
        } else {
            bg = jEdit.getColorProperty("view.bgColor").getRGB();
            // Default underline color is the inverse of the bg color
            bg ^= ~1;
        }
        
        bg = (bg & 0xffffff) | 0x88000000;
        return new java.awt.Color(bg, true);
    }
    
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
            
            // Classify by the first character, then grab the rest of the
            // matching characters.
            if (Character.isSpaceChar(c)) {
                while (Character.isSpaceChar(c) || c.equals('\t')) {
                    c = text.charAt(offset + num_chars++);
                }
            } else if (Character.isLetterOrDigit(c)) {
                while (Character.isLetterOrDigit(c) || (no_word_sep.indexOf(c) != -1)) {
                    c = text.charAt(offset + num_chars++);
                }
            } else {
                while (!Character.isLetterOrDigit(c)) {
                    c = text.charAt(offset + num_chars++);
                }
            }
        } catch (java.lang.IndexOutOfBoundsException ex) {  // NOPMD
            // don't care
        }
        
        return --num_chars;
    }
    
    private ArrayList<String> getWords(int buf_start, int buf_end)
    {
        ArrayList<String> words = new ArrayList<String>();
        
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
        this.error_source = new DefaultErrorSource("voxspellcheck");
        setMode("text");
    }
    
    protected boolean check(String word, int line_offset, 
                            DefaultTokenHandler tokenHandler,
                            boolean user_only)
    {
        // FIXME: Hack for Unicode apostrophe
        if (word.indexOf('\u2019') != -1) {
            word = word.replaceAll("\u2019", "'");
        }
        // FIXME: Hack
        if (word.endsWith("s'")) {
            word = word.substring(0, word.length() - 1);
        }
        
        String trim_word = word.trim();
        String low_word = trim_word.toLowerCase();
        
        if (trim_word.length() == 0)
            return true;
        if (low_word.length() == 1)
            return true;
        if (trim_word.endsWith("'") && trim_word.length() == 2)
            return true;
        Character c = trim_word.charAt(0);
        if (!Character.isLetter(c))
            return true;
        
        // FIXME: Hack!
        if (!user_only) {
            Token token;
            try {
                // FIXME: why am I getting null tokens (ArrayIndexOutOfBoundsException)?
                token = TextUtilities.getTokenAtOffset(tokenHandler.getTokens(), line_offset);
            } catch (ArrayIndexOutOfBoundsException ex) {
                return true;
            }
            switch (markup_mode) {
            case COMMENT_OR_LITERAL:
                if ((token.id < Token.COMMENT1 || token.id > Token.COMMENT4) &&
                    (token.id < Token.LITERAL1 || token.id > Token.LITERAL4))
                {
                    return true;
                }
                break;
            case NON_MARKUP:
                if (token.id != Token.NULL) {
                    return true;
                }
                break;
            }
        }
        
        if (user_only) {
            return user_checker.find(low_word) ||
                   user_checker.find(trim_word) ||
                   ignore_checker.find(low_word) ||
                   ignore_checker.find(trim_word);
        }
        
        return checker.find(low_word) ||
               checker.find(trim_word) ||
               user_checker.find(low_word) ||
               user_checker.find(trim_word) ||
               ignore_checker.find(low_word) ||
               ignore_checker.find(trim_word);
    }
    
    public boolean check(String word, int line_offset,
                         DefaultTokenHandler tokenHandler)
    {
        return check(word, line_offset, tokenHandler, false);
    }
    
    public boolean check(int pos, StringBuffer word_checked, boolean user_only)
    {
        int line;
        int start;
        int end;
        try {
            line = textarea.getLineOfOffset(pos);
            start = textarea.getLineStartOffset(line);
            end = textarea.getLineEndOffset(line);
        } catch (java.lang.NullPointerException ex) {
            // Getting NPE's on splits
            return true;
        } catch (java.lang.ArrayIndexOutOfBoundsException ai) {
            return true;   
        }
        
        JEditBuffer buffer = textarea.getBuffer();
        DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
        buffer.markTokens(line, tokenHandler);
        
        ArrayList<String> words = getWords(start, end);
        Iterator<String> iter = words.iterator();
        int char_count = 0;
        String word = null;
        while (iter.hasNext()) {
            word = iter.next();
            if ((start + char_count + word.length()) >= pos)
                break;
            char_count += word.length();
        }
        if (word == null)
            return true;
        word_checked.replace(0, word_checked.length(), word);
        return check(word, char_count, tokenHandler, user_only);
    }
    
    public boolean check(int pos, StringBuffer word_checked)
    {
        return check(pos, word_checked, false);
    }
    
    public void paintValidLine(java.awt.Graphics2D gfx, int screenLine, 
                               int physicalLine, int start, int end, int y)
    {
        // Make sure tokens are valid for this line
        JEditBuffer buffer = textarea.getBuffer();
        DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
        buffer.markTokens(textarea.getLineOfOffset(start), tokenHandler);
        
        gfx.setColor(getUnderlineColor());
        
        FontMetrics metrics = this.textarea.getPainter().getFontMetrics();
        int char_height = metrics.getHeight() - metrics.getLeading();
        
        ArrayList<String> words = getWords(start, end);
        
        int char_count = 0;
        for (String word : words) {
            skip: {
                if (check(word, char_count, tokenHandler)) {
                    break skip;
                }
    
                Point p = textarea.offsetToXY(start + char_count);
                if (p == null)
                    break skip;
                int x = p.x;
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
    
    public void checkAll(EditPane editpane)
    {
        ErrorSource.registerErrorSource(error_source);
        error_source.clear();
        
        Buffer buffer = editpane.getBuffer();
        
        for (int i = 0; i < textarea.getLineCount(); ++i) {
            String line_text = textarea.getLineText(i);
            DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
            buffer.markTokens(i, tokenHandler);
            ArrayList<String> words = getWords(textarea.getLineStartOffset(i), textarea.getLineEndOffset(i));
            int char_count = 0;
            for (String word : words) {
                if (!check(word, char_count, tokenHandler)) {
                    error_source.addError(new DefaultErrorSource.DefaultError(error_source,
                                                                              ErrorSource.ERROR,
                                                                              buffer.getPath(),
                                                                              i,
                                                                              char_count,
                                                                              char_count + word.length(),
                                                                              line_text));
                }
                char_count += word.length();
            }
        }
        ErrorSource.unregisterErrorSource(error_source);
    }
}

