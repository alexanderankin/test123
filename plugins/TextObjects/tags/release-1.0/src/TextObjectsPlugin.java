
/* Copyright (C) 2008 Matthew Gilbert */

package textobjects;

import java.util.*;
import java.lang.Character;
import java.lang.Math;
import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.gui.InputHandler;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;

public class TextObjectsPlugin extends EditPlugin
{
    public static final String NAME = "TextObjects";
    public static final String OPTION_PREFIX = "options.textobjects";
    
    public static String nowordsep = "_";
    // FIXME: actually use this.
    private static int DEFAULT_LENGTH = (1024 * 1024);

    private static boolean debug_enable = false;
    private static void debug(String s)
    {
        if (debug_enable)
            Log.log(Log.DEBUG, s, s);
    }
   
    private static abstract class Predicate implements Cloneable {
        public abstract boolean match(Character c);
        public abstract Predicate clone();
        public int length = DEFAULT_LENGTH;
        /*
        public Predicate clone()
        {
            T t;
            Class<T> cl = T.class;
            Constructor con;
            Class arg_classes[] = {};
            try {
                con = cl.getConstructor(arg_classes);
            } catch (java.lang.NoSuchMethodException e) {
                Log.log(Log.ERROR, this, "MMG: NoSuchMethodException " + e);
                e.printStackTrace();
                return null;
            }
            try {
                t = (T)con.newInstance();
            } catch (java.lang.InstantiationException e) {
                Log.log(Log.ERROR, this, "MMG: InstantiationException");
                e.printStackTrace();
                return null;
            } catch (java.lang.IllegalAccessException e) {
                Log.log(Log.ERROR, this, "MMG: IllegalAccessException");
                e.printStackTrace();
                return null;
            } catch(java.lang.reflect.InvocationTargetException e) {
                Log.log(Log.ERROR, this, "MMG: InvocationTargetException");
                e.printStackTrace();
                return null;
            }
            Field f;
            try {
                f = cl.getDeclaredField("length");
            } catch (java.lang.NoSuchFieldException e) {
                Log.log(Log.ERROR, this, "MMG: NoSuchFieldException");
                e.printStackTrace();
                return null;
            }
            try {
                f.setInt(t, this.length);
            } catch (java.lang.IllegalAccessException e) {
                Log.log(Log.ERROR, this, "MMG: IllegalAccessException");
                e.printStackTrace();
                return null;
            }
   
            debug("Creating a clone of " + cl.getName());
            return (Predicate)t;
        }
        */
    }
    
    private static abstract class StringIterator implements Iterator 
    {
        public final String text;
        protected int offset;
        
        public StringIterator(String text_, int offset_)
        {
            this.text = text_;
            this.offset = offset_;
        }
        
        public StringIterator(StringIterator i)
        {
            this.text = i.text;
            this.offset = i.offset();
        }
        
        public abstract Character next();
        public abstract boolean hasNext();
        public abstract void remove();
        protected abstract int adjust();
        
        public String substring(int len)
        {
            return text.substring(offset, offset + len);
        }
        public int offset()
        {
            return offset;
        }
        public final String text()
        {
            return text;
        }
    }
    
    private static class BackStringIterator extends StringIterator
    {
        BackStringIterator(final String text_, int offset_)
        {
            super(text_, offset_);
        }
        public boolean hasNext()
        {
            return (offset >= 0) ? true : false;
        }
        public Character next()
        {
            return text.charAt(this.offset--);
        }
        public void remove() {}
        protected int adjust() { return 1; }
    }
    
    private static class ForwardStringIterator extends StringIterator
    {
        public ForwardStringIterator(final String text_, int offset_)
        {
            super(text_, offset_);
        }
        public ForwardStringIterator(StringIterator i)
        {
            super(i);
        }
        public boolean hasNext()
        {
            return (offset < this.text.length()) ? true : false;
        }
        public Character next()
        {
            return text.charAt(this.offset++);
        }
        public void remove() {}
        protected int adjust() { return -1; }
    }
    
    private static ForwardStringIterator forward(StringIterator i)
    {
        return new ForwardStringIterator(i);
    }
    
    public static class Functors
    {
        public static class Anything extends Predicate
        {
            public boolean match(Character c)
            {
                return true;
            }
            
            public Anything clone()
            {
                return new Anything();
            }
        }
        
        public static class Nothing extends Predicate
        {
            public boolean match(Character c)
            {
                return false;
            }
            public Nothing clone()
            {
                return new Nothing();
            }
        }
        
        public static class Not extends Predicate
        {
            private final Predicate object;
            
            public Not(final Predicate object_)
            {
                this.object = object_;
                this.length = object_.length;
            }
            
            public boolean match(Character c)
            {
                return !this.object.match(c);
            }
            
            public Not clone()
            {
                return new Not(this.object);
            }
        }
        
        public static class Or extends Predicate
        {
            private ArrayList<Predicate> objects;
            
            public Or(final ArrayList<Predicate> objects_)
            {
                this.objects = new ArrayList<Predicate>();
                for (Predicate p : objects_)
                    this.objects.add(p.clone());
                // Default length is the max of all contained predicates.
                for (Predicate p : this.objects)
                    this.length = Math.max(this.length, p.length);
            }
            
            public boolean match(Character c)
            {
                boolean ret = false;
                for (Predicate p : objects) {
                    if (p.match(c)) {
                        ret = true;
                        int old_length = this.length;
                        this.length = Math.min(this.length, p.length);
                        if (old_length != this.length) {
                            debug("Setting or length from " + old_length + " to " +
                                  this.length + " for " + p);
                        }
                    }
                } 
                return ret;
            }
            
            public Or clone()
            {
                Or o = new Or(this.objects);
                o.length = this.length;
                return o;
            }
        }
        
        public static class And extends Predicate
        {
            private ArrayList<Predicate> objects;
            
            public And(ArrayList<Predicate> objects_)
            {
                this.objects = new ArrayList<Predicate>();
                for (Predicate p : objects_)
                    this.objects.add(p.clone());
                this.length = 0;
                // Default length is the max of all contained predicates.
                for (Predicate p : objects)
                    this.length = Math.max(this.length, p.length);
            }
            
            public boolean match(Character c)
            {
                boolean ret = true;
                for (Predicate p : objects) {
                    if (!p.match(c)) {
                        this.length = Math.min(this.length, p.length);
                        ret = false;
                    }
                }
                return ret;
            }
            
            public And clone()
            {
                And a = new And(this.objects);
                a.length = this.length;
                return a;
            }
        }
        
        public static class Space extends Predicate
        {
            public boolean match(Character c)
            {
                if (Character.isSpaceChar(c) || (c == '\t'))
                    return true;
                return false;
            }
            
            public Space clone()
            {
                Space s = new Space();
                s.length = this.length;
                return s;
            }
        }
        
        public static class Whitespace extends Predicate
        {
            public boolean match(Character c)
            {
                if (Character.isWhitespace(c))
                    return true;
                return false;
            }
            
            public Whitespace clone()
            {
                Whitespace w = new Whitespace();
                w.length = this.length;
                return w;
            }
        }
        
        public static class Word extends Predicate
        {
            public boolean match(Character c)
            {
                if (Character.isLetterOrDigit(c) || (nowordsep.indexOf(c) != -1)) {
                    return true;
                }
                return false;
            }
            
            public Word clone()
            {
                Word w = new Word();
                w.length = this.length;
                return w;
            }
        }
        
        public static class Newline extends Predicate
        {
            public boolean match(Character c)
            {
                //if (Character.getType(c) == Character.LINE_SEPARATOR)
                if (c == '\n') {
                    return true;
                }
                return false;
            }
            
            public Newline clone()
            {
                Newline n = new Newline();
                n.length = this.length;
                return n;
            }
        }
        
        public static class InString extends Predicate
        {
            private final String string;
            public InString(final String string_)
            {
                this.string = string_;
            }
            
            public boolean match(Character c)
            {
                if (this.string.indexOf(c) != -1)
                    return true;
                return false;
            }
            
            public InString clone()
            {
                InString s = new InString(this.string);
                s.length = this.length;
                return s;
            }
        }
    }
    
    private static int match(StringIterator iter, Predicate p)
    {
        int num_match = 0;
        while (iter.hasNext()) {
            Character c = iter.next();
            if (!p.match(c)) {
                break;
            }
        }
        return num_match;
    }
    
    private static boolean find_debug_enable = false;
    private static void find_debug(String s)
    {
        if (find_debug_enable)
            Log.log(Log.DEBUG, s, "FIND: " + s);
    }
    
    /** Returns the number of non-matched characters; -1 if no character
        matched.
      */
    private static int find_first(StringIterator iter, Predicate p)
    {
        int offset = 0;
        while (iter.hasNext()) {
            Character c = iter.next();
            if (p.match(c)) {
                find_debug("first returned " + offset + " with " + p);
                return offset;
            }
            offset++;
            if (offset > p.length)
                return -1;
        }
        find_debug("first returned -1");
        return -1;
    }
    
    /** Returns the number of matched characters.
      */
    private static int find_last(StringIterator iter, Predicate p)
    {
        int offset = 0;
        while (iter.hasNext()) {
            Character c = iter.next();
            if (!p.match(c)) {
                find_debug("Failed to match \"" + c + "\"" + " with " + p);
                find_debug("length " + p.length);
                break;
            }
            offset++;
            if (offset > p.length)
                break;
        }
        find_debug("last returned " + offset);
        return offset;
    }
    
    private static boolean seq_debug_enabled = false;
    private static void fs_debug(String s)
    {
        if (seq_debug_enabled)
            Log.log(Log.DEBUG, s, "SEQ: " + s);
    }
    
    private static HashMap<String, Integer> 
    find_seq(StringIterator iter, ArrayList<Vector<Predicate>> fifos_)
    {
        HashMap<String, Integer> failed = new HashMap<String, Integer>();
        failed.put("offset", -1);
        failed.put("length", -1);
        
        ArrayList<Vector<Predicate>> fifos = new ArrayList();
        for (Vector<Predicate> source : fifos_) {
            Vector<Predicate> dest = new Vector(source.size());
            fifos.add(dest);
            for (Predicate p : source)
                dest.add(p.clone());
        }
        
        int offset = 0;
        // Outer loop is starting point for a sequence match
        while (iter.hasNext()) {
            fs_debug("Outer loop at " + iter.offset());
            int seq_len = 0;
            //int match_len = 0;
            ForwardStringIterator seq_iter = forward(iter);
            fs_debug("Inner loop initialized at " + seq_iter.offset());
            iter.next();
            
            // Init per try arrays;
            ArrayList<Vector> missed = new ArrayList<Vector>(fifos.size());
            ArrayList<Vector> matches = new ArrayList<Vector>(fifos.size());
            int[] match_lens = new int[fifos.size()];
            for (int i = 0; i < fifos.size(); i++) {
                matches.add(new Vector<Predicate>());
                match_lens[i] = 0;
            }
            
            fs_debug("matches size " + matches.size());
            // Inner loop is to find a matching seq starting from outer iter.
            while (seq_iter.hasNext() && (missed.size() < fifos.size())) {
                fs_debug("Inner loop at " + seq_iter.offset());
                Character c = seq_iter.next();
                seq_len++;
                for (int i = 0; i < fifos.size(); i++)
                    match_lens[i]++;
                for (int i = 0; i < fifos.size(); i++) {
                    Vector<Predicate> s = fifos.get(i);
                    Vector<Predicate> m = matches.get(i);
                    fs_debug("Trying " + s);
                    // If this stack already missed, skip it.
                    if (missed.indexOf(s) != -1) {
                        fs_debug("Skipped " + s);
                        continue;
                    }
                    
                    Predicate p = s.get(0);
                    // No match, mark this stack for removal
                    // FIXME: Add check for min amount, and add it as a match
                    // even if it missed
                    if (!p.match(c)) {
                        fs_debug("Missed '" + c + "' " + p + " " + s);
                        missed.add(s);
                        continue;
                    } else {
                        fs_debug("Matched '" + c + "' " + p + " " + p.length);
                    }
                    
                    // Matched the length for this predicate; pop it, it is
                    // successful.
                    fs_debug("match_len (" + match_lens[i] + ") p.length (" + p.length + ")");
                    if (match_lens[i] >= p.length) {
                        fs_debug("Satisfied " + p + " with length " + p.length);
                        m.add(m.size(), s.remove(0));
                        // FIXME: match_len should be reset here?
                        match_lens[i] = 0;
                    }

                    // If stack is empty, we have a shortest match
                    if (s.isEmpty()) {
                        HashMap<String, Integer> ret = new HashMap<String, Integer>();
                        ret.put("offset", offset);
                        ret.put("length", seq_len);
                        return ret;
                    }
                }
            }
            
            // Missed sequence, add the matches back
            for (int i = 0; i < fifos.size(); i++) {
                Vector<Predicate> s = fifos.get(i);
                Vector<Predicate> m = matches.get(i);
                s.addAll(0, m);
            }
            
            // Didn't find it
            offset++;
        }
        return failed;
    }
   
    private static Selection pair_first(final String text, int pos, 
                                  Predicate start, Predicate end)
    {
        int s = pos - find_first(new BackStringIterator(text, pos - 1), start);
        int e = pos + find_first(new ForwardStringIterator(text, pos), end);
        if (s == -1 || e == -1)
            return null;
        return new Selection.Range(s, e);
    }
    
    private static Selection pair_last(final String text, int pos, 
                                  Predicate start, Predicate end)
    {
        int s = pos - find_last(new BackStringIterator(text, pos - 1), start);
        int e = pos + find_last(new ForwardStringIterator(text, pos), end);
        debug("pair_last returned " + s + " " + e);
        return new Selection.Range(s, e);
    }
    
    private static ForwardStringIterator forward(final String text, int pos)
    {
        return new ForwardStringIterator(text, pos);
    }
    
    private static BackStringIterator back(final String text, int pos)
    {
        return new BackStringIterator(text, pos);
    }

    private static<T> ArrayList<T> make_array(T... args)
    {
        // FIXME: Can I return args?
        ArrayList<T> array = new ArrayList(args.length);
        for (T t : args) {
            array.add(t);
        }
        return array;
    }
    
    private static Selection 
    extend_whitespace(final String text, Selection selection, 
                      boolean stop_on_newline)
    {
        Predicate word = new Functors.Word();
        Predicate whitespace;
        if (stop_on_newline) {
            whitespace = new Functors.Space();
        } else {
            whitespace = new Functors.Whitespace();
            word = new Functors.Or(make_array(word, new Functors.Newline()));
        }
        // FIXME: check genEnd() - 1 so that we complete actual whitespace for
        // paragraph.
        boolean is_whitespace = whitespace.match(text.charAt(selection.getEnd() - 1));
        Predicate p = is_whitespace ? word : whitespace;
        int extension = find_last(forward(text, selection.getEnd()), p) + selection.getEnd();
        return new Selection.Range(selection.getStart(), extension);
    }
    
    private static Selection extend_whitespace(final String text, Selection selection)
    {
        return extend_whitespace(text, selection, true);
    }
   
    /** Start objects */
    
    public static Selection word(TextArea ta, int pos, boolean whole)
    {
        Selection res;
        final String text = ta.getText();
        pos = Math.min(pos, text.length() - 1);
        
        if (text.length() == 0)
            return null;
       
        Predicate word = new Functors.Word();
        Predicate whitespace = new Functors.Whitespace();
        Predicate space = new Functors.Space();
        
        if (whitespace.match(text.charAt(pos))) {
            res = pair_last(text, pos, space, space);
        } else if (word.match(text.charAt(pos))) {
            res = pair_last(text, pos, word, word);
        } else {
            ArrayList nws_functors = make_array(whitespace, word);
            Predicate nws = new Functors.Not(new Functors.Or(nws_functors));
            res = pair_last(text, pos, nws, nws);
        }
        if (whole) {
            int orig_end = res.getEnd();
            res = extend_whitespace(text, res);
            // Try to match Vim behavior for words; if there was no forward 
            // whitespace, then search back.
            if (orig_end == res.getEnd()) {
                int line_start = ta.getLineStartOffset(ta.getLineOfOffset(res.getStart()));
                int new_start = find_last(back(text, res.getStart() - 1),
                                          new Functors.Space());
                if ((new_start != -1) && 
                    ((res.getStart() - new_start) != line_start)) 
                {
                    res = new Selection.Range(res.getStart() - new_start, res.getEnd());
                }
            }
        }
        
        return res;
    }
    
    private static boolean block_debug_enable = false;
    private static void block_debug(String s)
    {
        debug("BLOCK: " + s);
    }
    
    private static class InvalidBlock extends Throwable
    {
    }
    
    private static int get_block_end(final String text, int pos, String pair)
        throws InvalidBlock
    {
        char open_char = pair.charAt(0);
        char close_char = pair.charAt(1);
        Predicate either = new Functors.InString(pair);
        
        // Find open or close:
        //      - if open, call block to match the a new pair
        //      - if close, look for open from offset
        int end_pos = pos;
        while (true) {
            int offset = find_first(forward(text, end_pos), either);
            if (offset == -1) {
                throw new InvalidBlock();
            }
            end_pos += offset;
            char cur_char = text.charAt(end_pos);
            if (cur_char == close_char) {
                // Found a the closing character
                break;
            }
            
            // Found the opening char, move past it and continue.
            Selection tmp_sel = block(text, end_pos, pair);
            if (tmp_sel == null)
                throw new InvalidBlock();
            // Ignore the valid block, set end_pos just past the close and
            // continue the search.
            end_pos = tmp_sel.getEnd() + 1;
        }
        return end_pos;
    }
    
    private static int get_block_begin(final String text, int pos, String pair)
        throws InvalidBlock
    {
        char open_char = pair.charAt(0);
        char close_char = pair.charAt(1);
        Predicate either = new Functors.InString(pair);
        
        int begin_pos = pos;
        while (true) {
            int offset = find_first(back(text, begin_pos), either);
            debug("find_first found " + offset);
            if (offset == -1) {
                throw new InvalidBlock();
            }
            begin_pos -= offset;
            char cur_char = text.charAt(begin_pos);
            if (cur_char == open_char) {
                // Found the opening character. Move begin_pos just past it for
                // default inside match.
                begin_pos++;
                break;
            }
            
            // Closing character, find its pair.
            Selection tmp_sel = block(text, begin_pos, pair);
            if (tmp_sel == null)
                throw new InvalidBlock();
            // Ignore the valid block, set begin_pos just before the open char
            // and continue the search. block returns inside the brackets, so
            // have to move past the match to continue the search.
            begin_pos = tmp_sel.getStart() - 2;
        }
        return begin_pos;
    }
    
    private static Selection block(final String text, int pos, String pair)
    {
        block_debug("Starting block search with " + pair + " at " + pos);
        char open_char = pair.charAt(0);
        char close_char = pair.charAt(1);
        int begin_pos, end_pos;
        
        try {
            char cur_char = text.charAt(pos);
            if (cur_char == open_char) {
                // Already have the open char, find the close 1 past open.
                begin_pos = pos;
                end_pos = get_block_end(text, pos + 1, pair);
            } else if (cur_char == close_char) {
                // Already have the close char, find the open 1 before close.
                end_pos = pos;
                begin_pos = get_block_begin(text, end_pos - 1, pair);
            } else {
                end_pos = get_block_end(text, pos, pair);
                begin_pos = pos;
                if (end_pos == pos)
                    begin_pos--;
                begin_pos = get_block_begin(text, begin_pos, pair);
            }
        } catch (InvalidBlock ex) {
            return null;
        }
        
        return new Selection.Range(begin_pos, end_pos);
    }
   
    public static Selection block(TextArea ta, int pos, boolean whole, String pair)
    {
        Selection res;
        final String text = ta.getText();
        pos = Math.min(pos, text.length() - 1);
        
        if (text.length() == 0)
            return null;
        
        res = block(text, pos, pair);
        if (res == null) 
            return null;
        
        if (whole)
            res = new Selection.Range(res.getStart() - 1, res.getEnd() + 1);
        return res;
    }
    
    private static int find_quote(final String text, int pos, Character quote, 
                                  boolean do_forward)
    {
        int orig_pos = pos;
        ArrayList match_functors = make_array(new Functors.Newline(),
                                              new Functors.InString(quote.toString()));
        Predicate either = new Functors.Or(match_functors);
        int offset_sign = do_forward ? 1 : -1;
        
        while (true) {
            StringIterator iter = do_forward ? forward(text, pos) : back(text, pos);
            int offset = find_first(iter, either);
            if (offset == -1) {
                return -1;
            }
            offset *= offset_sign;
            if (text.charAt(pos + offset) != quote) {
                return -1;
            }
            // Make sure this isn't escaped
            if (((pos + offset) == 0) || (text.charAt(pos + offset - 1) != '\\')) {
                // quote expects a positive value
                return Math.abs(offset + (pos - orig_pos));
            } else {
                // adjust pos past this escaped quote
                pos += (offset + offset_sign);
            }
        }
    }
    
    public static Selection quote(TextArea ta, int pos, boolean whole, Character quote)
    {
        Selection res, s;
        final String text = ta.getText();
        pos = Math.min(pos, text.length() - 1);
        
        if (text.length() == 0)
            return null;
     
        // There has to be at least 1 quote char ahead.
        int end = find_quote(text, pos, quote, true);
        if (end == -1)
            return null;
        
        // First try searching back for match 1 back from the 
        // forward search start location.
        int start = find_quote(text, pos - 1, quote, false);
        if (start == -1) {
            // Try searching forward for another quote char
            start = end;
            end = find_quote(text, pos + start + 1, quote, true);
            if (end == -1)
                return null;
            // Make end relative to pos (1 and start are added to start above, so 
            // need to add it here).
            end = (++end + start);
        } else {
            // make start relative to pos (1 is subtracted to pos in call to 
            // find_quote, so make start 1 greater).
            start = -(++start);
        }
            
        // start is pointing to the opening quote, we initially want what's
        // inside.
        res = new Selection.Range(pos + start + 1, pos + end);
        if (whole) {
            res = new Selection.Range(Math.min(pos, pos + start), 
                                      Math.min(res.getEnd() + 1, text.length()));
            res = extend_whitespace(text, res);
        }
        return res;
    }
    
    private static ArrayList<Predicate> string_to_predicate(String string)
    {
        ArrayList<Predicate> array = new ArrayList<Predicate>(string.length());
        for (int i = 0; i < string.length(); i++) {
            Predicate p = new Functors.InString(string.substring(i, i + 1));
            p.length = 1;
            array.add(p);
        }
        return array;
    }
    
    public static Selection paragraph(TextArea ta, int pos, boolean whole)
    {
        final String text = ta.getText();
        pos = Math.min(pos, text.length() - 1);
        
         if (text.length() == 0)
            return null;
       
        // Here space and newline are separate since we want newline to
        // terminate on the first match (instead of using Whitespace).
        Predicate newline = new Functors.Newline();
        Predicate space = new Functors.Space();
        Predicate sp_or_nl = new Functors.Or(make_array(newline, space));
        
        int orig_offset = pos;
        // First find non-blank.
        int par_start = find_first(forward(text, pos), 
                                   new Functors.Or(make_array(new Functors.Word(), 
                                                              new Functors.Not(sp_or_nl))));
        if (par_start == -1)
            return null;
        par_start = pos + par_start;
        
        // For finding blanks, newline must be 1
        newline.length = 1;
        // Must reset sp_or_nl so that the new length takes effect.
        sp_or_nl = new Functors.Or(make_array(newline, space));
        Vector<Predicate> blank = new Vector();
        ArrayList<Vector<Predicate>> list = new ArrayList<Vector<Predicate>>();
        list.add(blank);
        blank.add(newline);
        blank.add(sp_or_nl);

        // FIXME: this means first line that is blank is included.
        int start = 0;
        HashMap<String, Integer> back = find_seq(back(text, par_start), list);
        if (back.get("offset") != -1)
            start = par_start - back.get("offset") + back.get("length");
        
        // If start is still zero, make sure the first line isn't blank
        if (start == 0) {
            int line_end = ta.getLineEndOffset(0);
            if (line_end <= find_last(forward(text, 0), new Functors.Whitespace())) {
                start = line_end;
            }
        }
        
        int end = text.length();
        HashMap<String, Integer> forward = find_seq(forward(text, par_start), list);
        if (forward.get("offset") != -1)
            end = par_start + forward.get("offset");
        
        Selection res = new Selection.Range(Math.min(start, orig_offset), end);
        if (whole && forward.get("length") != -1) {
            res = extend_whitespace(text, res, false);
            // Match Vim, only select to the start of the first non-blank.
            res = new Selection.Range(res.getStart(),
                                      ta.getLineStartOffset(ta.getLineOfOffset(res.getEnd())));
        }

        return res;
    }
    
    public static Token get_token(TextArea ta, int pos)
    {
         try {
            JEditBuffer buffer = ta.getBuffer();
            DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
            int line = ta.getLineOfOffset(pos);
            buffer.markTokens(line, tokenHandler);
            int offset = pos - ta.getLineStartOffset(line);
            return TextUtilities.getTokenAtOffset(tokenHandler.getTokens(), offset);
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }
    
    public static Selection comment(TextArea ta, int pos, boolean whole)
    {
        final String text = ta.getText();
        pos = Math.min(pos, text.length() - 1);
        
        Predicate ws = new Functors.Whitespace();
        
        int cur_line = ta.getCaretLine();
        int line_start = ta.getLineStartOffset(cur_line);
        int line_end = ta.getLineEndOffset(cur_line);
        
        //int forward_char_pos = find_last(forward(text, pos), ws);
        int back_char_pos = pos - find_last(back(text, pos), ws);
        
        // Gotta be at the start of the comment or somewhere inside it, so
        // initially search backwards.
        Token token = get_token(ta, back_char_pos);
        
        // FIXME: Could optimize by using the token offset and length.
        if (token.id >= Token.COMMENT1 && token.id <= Token.COMMENT4) {
            int last_match_offset;
            // find forward matches
            int forward_pos = pos;
            while (true) {
                last_match_offset = forward_pos;
                forward_pos += find_last(forward(text, forward_pos), ws);
                Token new_token = get_token(ta, forward_pos);
                if ((new_token == null) || (token.id != new_token.id)) {
                    break;
                }
                forward_pos++;
            }
            forward_pos = last_match_offset;
            int back_pos = pos;
            while (true) {
                last_match_offset = back_pos;
                back_pos -= find_last(back(text, back_pos), ws);
                Token new_token = get_token(ta, back_pos);
                if (new_token == null || token.id != new_token.id) {
                    break;
                }
                back_pos--;
            }
            // last_match_offset is set to the back_pos of the failure, so
            // increment to get to the last success. forward_pos is also set to
            // the failure, but since ranges are [...), that's exactly what we
            // want.
            back_pos = last_match_offset + 1;
            
            // TODO: search for a space after the first or second comment char
            // for in-comment.
            try {
                if (!whole) {
                    if (ws.match(text.charAt(back_pos + 1)))
                        back_pos += 2;
                    else if (ws.match(text.charAt(back_pos + 2)))
                        back_pos += 3;
                    
                    // TODO: Total hack.
                    if (token.id == Token.COMMENT1) {
                        if (ws.match(text.charAt(forward_pos - 1)))
                            forward_pos -= 2;
                        else if (ws.match(text.charAt(forward_pos - 2)))
                            forward_pos -= 3;
                    }
                }
            } catch (IndexOutOfBoundsException ex) {
                ; // do nothing
            }
            
            return new Selection.Range(back_pos, forward_pos);
        }
        return null;
    }
    
    public static Selection sentence(TextArea ta, int pos, boolean whole)
    {
        final String text = ta.getText();
        pos = Math.min(pos, text.length() - 1);
        
        Predicate punct = new Functors.InString(".!?");
        punct.length = 1;
        Predicate sen_end = new Functors.InString(" \t\n");
        sen_end.length = 1;
        Predicate quote = new Functors.InString("'\"");
        quote.length = 1;
        
        ArrayList<Vector<Predicate>> list = new ArrayList<Vector<Predicate>>();
        
        // Unquoted sentence ending
        Vector<Predicate> vec = new Vector();
        vec.add(punct);
        vec.add(sen_end);
        list.add(vec);
        
        // Quoted sentence ending
        Vector<Predicate> vec_q = new Vector();
        vec_q.add(punct);
        vec_q.add(quote);
        vec_q.add(sen_end);
        list.add(vec_q);
        
        Selection sel = paragraph(ta, pos, false);
        HashMap<String, Integer> ret;
        int start, end;
        int start_len, end_len;
        boolean para_adjusted = false;
        
        ret = find_seq(forward(text, pos), list);
        end = pos + ret.get("offset") + ret.get("length");
        end_len = ret.get("length");
        if ((ret.get("offset") == -1) || (end > sel.getEnd())) {
            end = sel.getEnd();
            end_len = 1;
            para_adjusted = true;
        } else {
            // Don't include the last space.
            end--;
        }
        
        ret = find_seq(back(text, Math.max(pos - 1, 0)), list);
        start = pos - ret.get("offset") + ret.get("length") - 1;
        if ((ret.get("offset") == -1) || (start < sel.getStart())) {
            start = sel.getStart();
        } else {
            // adjust it to either start right after the previous
            // sentence's punctuation or at the end of the whitespace.
            if (whole) {
                start -= 1;
            } else {
                // In a sentence, so skip leading whitespace.
                sen_end.length = DEFAULT_LENGTH;
                start += find_last(forward(text, start), sen_end);
            }
        }
        
        Selection ret_sel = new Selection.Range(start, end);
        if (!whole && !para_adjusted) {
            // inside a sentence doesn't include the starting quote.
            if (quote.match(text.charAt(start)))
                start++;
            // Already subtracted 1 above from end, so add it back in here.
            ret_sel = new Selection.Range(start, end - end_len + 1);
        }
        return ret_sel;
    }
   
    /* start keyboard handler */
    
    public static void start_keyboard_handler(View view)
    {
        InputHandler old = view.getInputHandler();
        view.setInputHandler(new TextObjectsInputHandler(view, old));
    }
    
    public static void start_keyboard_handler(View view, boolean whole)
    {
        InputHandler old = view.getInputHandler();
        view.setInputHandler(new TextObjectsInputHandler(view, old, whole));
    }
}
    
