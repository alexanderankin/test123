
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


import java.util.ArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import java.awt.EventQueue;

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.TextUtilities;

public class VoxSpellPlugin extends EBPlugin
{
    public static final String NAME = "VoxSpell";
    public static final String OPTION_PREFIX = "options.voxspellcheck";
    
    private static OffsetTrie checker = null;
    private static WordTrie user_checker = null;
    private static WordTrie ignore_checker = null;
    private static SuggestionTree suggestions = null;
    private static String prop_key = "VoxSpellExtension";
    
    public static OffsetTrie getChecker()
    {
        return checker;
    }
    
    public static SuggestionTree getSuggestionTree()
    {
        return suggestions;
    }
    
    public static VoxSpellPainter getVoxSpellPainter(EditPane ep)
    {
        try {
            return (VoxSpellPainter)ep.getClientProperty(prop_key);
        } catch (java.lang.ClassCastException ex) {
            return null;
        }
    }
    
    static protected void scheduleLater(final Runnable task)
    {
         Runnable runner = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(200);
                    EventQueue.invokeLater(task);
                } catch (InterruptedException ex) {
                    return;
                }
            }
        };
        new Thread(runner).start();
    }
    
    static protected void scheduleEditPaneUpdate(final EditPane editpane)
    {
        final Runnable task = new Runnable() {
            public void run() {
                updateEditPane(editpane);
            }
        };
        scheduleLater(task);
    }
    
    static public void updateEditPane(EditPane editpane)
    {
        if (editpane == null)
            return;
        
        VoxSpellPainter painter = null;
        Buffer buffer = editpane.getBuffer();
        TextArea text_area = editpane.getTextArea();
        
        if (buffer == null) {
            scheduleEditPaneUpdate(editpane);
            return;
        }
        
        painter = getVoxSpellPainter(editpane);
        if (painter == null) {
            String s = jEdit.getProperty("options.voxspellcheck.start_checking_on_activate");
            if (s.equals("true")) {
                startSpelling(editpane);
            }
            painter = getVoxSpellPainter(editpane);
        }
        if (painter != null) {
            Mode mode = buffer.getMode();
            if (mode == null) {
                scheduleEditPaneUpdate(editpane);
                return;
            }
            
            // We have a mode, see if we need to change the painter.
            String new_mode = mode.toString();
            if (!new_mode.equals(painter.getMode())) {
                painter.setMode(new_mode);
                text_area.repaint();
            }
        }
    }
    
    protected static void updateActiveView()
    {
        View view = jEdit.getActiveView();
        if (view == null) {
            return;
        }
        
        EditPane editpane = view.getEditPane();
        if (editpane == null) {
            final Runnable task = new Runnable() {
                public void run() {
                    updateActiveView();
                }
            };
            scheduleLater(task);
            return;
        }

        updateEditPane(editpane);
    }
   
    public void handleMessage(EBMessage msg)
    {
        EditPane editpane = null;
        if (msg instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate)msg;
            View view = bu.getView();
            if (view == null) {
                updateActiveView();
                return;
            }
            editpane = view.getEditPane();
            updateEditPane(editpane);
        } else if (msg instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate)msg;
            editpane = epu.getEditPane();
            updateEditPane(editpane);
        }
    }
    
    private static void loadDict(PluginJAR plugin_jar) throws java.io.IOException
    {
        ZipFile zip;
        zip = plugin_jar.getZipFile();
        ZipEntry entry = zip.getEntry("dicts/en.spl");
        long size = entry.getSize();
        byte[] data = new byte[(int)size];
        
        int read = 0;
        InputStream input_stream = zip.getInputStream(entry);
        while (read < size)
            read += input_stream.read(data, read, (int)(size - read));
        checker.read(data);
    }
    
    private static File getUserDictFile()
    {
        String settings_dir = jEdit.getSettingsDirectory();
        File user_dict = new File(settings_dir + "/user_dict");
        try {
            user_dict.createNewFile();
        } catch (java.io.IOException ex) {
            Log.log(Log.ERROR, ex, ex);
            return null;
        }
        return user_dict;
    }
    
    private static void loadUserDict()
    {
        File user_dict = getUserDictFile();
        if (user_dict == null)
            return;
        
        FileInputStream input;
        try {
            input = new FileInputStream(user_dict);
        } catch (java.io.FileNotFoundException ex) {
            Log.log(Log.ERROR, ex, "IOException " + ex);
            return;
        }
        
        try {
            user_checker.read(new DataInputStream(input));
        } catch (java.io.IOException ex) {
            Log.log(Log.ERROR, ex, ex);
            return;
        }
        
        /* Add all user_checker words to the suggestion tree. */
        ArrayList<String> user_words = user_checker.getWords();
        for (String w : user_words) {
            suggestions.addWord(w);
        }
    }
    
    public static void startSpelling(EditPane editpane)
    {
        if (checker == null || ignore_checker == null || user_checker == null) {
            Log.log(Log.ERROR, editpane, "Not checking spelling, plugin is not" +
                    "initialized");
            return;
        }
        
        Object obj = editpane.getClientProperty(prop_key);
        if (obj == null) {
            TextAreaPainter cur_painter = editpane.getTextArea().getPainter();
            
            VoxSpellPainter p = new VoxSpellPainter(editpane.getTextArea(), 
                                                    checker,
                                                    user_checker,
                                                    ignore_checker);
            cur_painter.addExtension(TextAreaPainter.BELOW_SELECTION_LAYER, p);
            editpane.putClientProperty(prop_key, p);
            scheduleEditPaneUpdate(editpane);
        }
    }
    
    public static void stopSpelling(EditPane editpane)
    {
        VoxSpellPainter p = getVoxSpellPainter(editpane);
        if (p != null) {
            TextAreaPainter cur_painter = editpane.getTextArea().getPainter();
            cur_painter.removeExtension(p);
            editpane.putClientProperty(prop_key, null);
        }
    }
    
    public void start()
    {
        if (this.ignore_checker == null) {
            this.ignore_checker = new WordTrie();
        }
        
        if (this.checker == null) {
            this.checker = new OffsetTrie();
            try {
                loadDict(getPluginJAR());
            } catch (java.io.IOException ex) {
                Log.log(Log.ERROR, this, "Could not load dictionary: " + ex);
                return;
            }
            this.suggestions = new SuggestionTree(this.checker);
        }
        
        /* user dictionary comes after the suggestion tree so that the words
        * in user_dict can be added to the suggestion tree.
        */
        if (this.user_checker == null) {
            this.user_checker = new WordTrie();
            loadUserDict();
        }
        
        View view = view = jEdit.getActiveView();
        if (view != null) {
            String s = jEdit.getProperty("options.voxspellcheck.start_checking_on_activate");
            if (s.equals("true")) {
                startSpelling(view.getEditPane());
            }
        }
    }
    
    public void stop()
    {
        View[] views = jEdit.getViews();
        for (View v : views) {
            EditPane ep = v.getEditPane();
            stopSpelling(ep);
        }
        this.checker = null;
    }
    
    public static void selectWordAtCaret(TextArea textarea)
    {
        int caret = textarea.getCaretPosition();
        int line = textarea.getLineOfOffset(caret);
        String text = textarea.getLineText(line);
        if (text.trim().equals(""))
            return;
        int line_start = textarea.getLineStartOffset(line);
        int line_offset = caret - line_start;
        try {
            int word_start = TextUtilities.findWordStart(text, line_offset, "'");
            int begin_line_end_pos = (line_offset == word_start) ? line_offset + 1 : line_offset;
            int word_end = TextUtilities.findWordEnd(text, begin_line_end_pos, "'");
            Selection sel = new Selection.Range(word_start + line_start,
                                                word_end + line_start);
            textarea.setSelection(sel);
        } catch (java.lang.Exception ex) {      // NOPMD
            // don't care
        }
    }
    
    public static String matchCase(String first, String second)
    {
        int len = Math.min(first.length(), second.length());
        char[] new_chars = new char[len];
        boolean at_least_one_lower = false;
        for (int i = 0; i < len; i++) {
            if (Character.isUpperCase(first.charAt(i))) {
                new_chars[i] = Character.toUpperCase(second.charAt(i));
            } else {
                at_least_one_lower = true;
                new_chars[i] = second.charAt(i);
            }
        }
        StringBuilder new_word = new StringBuilder();
        new_word.append(new_chars);
        if (len < second.length()) {
            if (at_least_one_lower)
                new_word.append(second.substring(len));
            else
                new_word.append(second.substring(len).toUpperCase());
        }
        return new_word.toString();
    }

    public static String getSuggestions(String word)
    {
        if (suggestions == null)
            return null;
        
        if (word.trim().equals(""))
            return null;

        ArrayList<String> words = suggestions.getSuggestions(word);
        if (words == null)
            return null;
        SuggestionDialog wp = new SuggestionDialog(jEdit.getActiveView(), 
                                                   true, 
                                                   words);
        wp.setVisible(true);
        
        if (wp.word == null)
            return null;
        
        return matchCase(word, wp.word);
    }
    
    protected static void writeUserDict()
    {
        File user_dict = getUserDictFile();
        if (user_dict == null)
            return;
        
        FileOutputStream output;
        try {
            output = new FileOutputStream(user_dict);
        } catch (java.io.FileNotFoundException ex) {
            Log.log(Log.ERROR, ex, "FileNotFoundException " + ex);
            return;
        }
           
        try {
            user_checker.write(new DataOutputStream(output));
            output.flush();
            output.close();
        } catch (java.io.IOException ex) {
            Log.log(Log.ERROR, ex, "EncodingException " + ex);
        }
    }
    
    public static void addWord(String word, TextArea textarea)
    {
        word = word.trim();
        if (user_checker == null)
            return;
        user_checker.addWord(word);
        textarea.repaint();
        
        if (word.length() > 0)
            suggestions.addWord(word);
        
        writeUserDict();
    }
    
    public static void addWord(TextArea textarea)
    {
        Selection sel = textarea.getSelectionAtOffset(textarea.getCaretPosition());
        if (sel == null)
            selectWordAtCaret(textarea);
        String word = textarea.getSelectedText().trim();
        if ((word != null) && (word.length() > 0)) {
            addWord(word, textarea);
        }
    }
    
    public static void ignoreWord(String word, TextArea textarea)
    {
        if (ignore_checker == null)
            return;
        ignore_checker.addWord(word.trim());
        textarea.repaint();
    }
    
    public static void ignoreWord(TextArea textarea)
    {
        Selection sel = textarea.getSelectionAtOffset(textarea.getCaretPosition());
        if (sel == null)
            selectWordAtCaret(textarea);
        String word = textarea.getSelectedText();
        if (word == null)
            return;
        word = word.trim();
        if ((word != null) && (word.length() > 0)) {
            ignoreWord(word, textarea);
        }
    }
    
    public static void resetWord(String word, TextArea textarea)
    {
        ignore_checker.removeWord(word.trim());
        user_checker.removeWord(word.trim());
        textarea.repaint();
        writeUserDict();
    }
    
    public static void resetWord(TextArea textarea)
    {
        Selection sel = textarea.getSelectionAtOffset(textarea.getCaretPosition());
        if (sel == null)
            selectWordAtCaret(textarea);
        String word = textarea.getSelectedText();
        if (word == null)
            return;
        word = word.trim();
        if ((word != null) && (word.length() > 0)) {
            resetWord(word, textarea);
        }
    }
    
    public static void reset(EditPane editpane)
    {
        ignore_checker = new WordTrie();
        VoxSpellPainter p = getVoxSpellPainter(editpane);
        if (p != null)
            p.setIgnoreChecker(ignore_checker);
        editpane.getTextArea().repaint();
    }
}

