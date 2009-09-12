
/* 
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

import java.lang.Character;
import java.lang.String;
import java.lang.StringBuffer;

import java.nio.CharBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.File;
import java.io.FileReader;

import java.net.URL;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.gui.KeyEventTranslator;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.bsh.NameSpace;
import org.gjt.sp.jedit.bsh.BshMethod;
import org.gjt.sp.jedit.bsh.BshClassManager;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.textarea.Selection;

import textobjects.TextObjectsPlugin;
import textobjects.TextObjectsPlugin.*;

class Mapping
{
    public String mode;
    public String key;
    public String block;
    public int end;
}

public class VimageParser
{
    public static class EOF extends java.lang.RuntimeException
    {
        EOF(String s)
        {
            super(s);
        }
    }

    public static class ParseError extends java.lang.RuntimeException
    {
        ParseError(String s)
        {
            super(s);
        }
    }

    public static String read(BufferedReader reader)
    {
        char[] cbuf = new char[4096];
        StringBuffer sbuf = new StringBuffer();

        try {
            int read;
            do {
                read = reader.read(cbuf);
                if (read > 0)
                    sbuf.append(cbuf, 0, read);
            } while (read == cbuf.length);
        } catch (java.io.IOException ex) {
            Log.log(Log.DEBUG, ex, "Error reading in VimageParser");
        }
        return sbuf.toString();
    }

    protected static Mapping next_mapping(CharSequence s, int offset)
    {
        Mapping mapping = new Mapping();
        // Format is "mode key { };"
        int start, end;
        Predicate word = new Functors.Word();
        Predicate whitespace = new Functors.Whitespace();

        ForwardCharSequenceIterator fcsi = new ForwardCharSequenceIterator(s, offset);

        TextObjectsPlugin.find_last(fcsi, whitespace);
        // fcsi has consumed the first non-whitespce character, so subtract 1 to
        // get the real start value. Since it's consumed at least 1 character, 
        // can subtract 1 without worrying about being < 0.
        start = fcsi.offset() - 1;
        if (fcsi.offset() >= s.length())
            throw new EOF("EOF");
        end = TextObjectsPlugin.find_last(fcsi, new Functors.Not(whitespace));
        if (end == 0) {
            throw new ParseError("Parse error at line \"" + s.subSequence(fcsi.offset(), s.length()) + "\"");
        }
        // Move past the last word char
        ++end;
        if (fcsi.offset() >= s.length()) {
            throw new ParseError("Parse error at line \"" + s.toString() + "\"");
        }
        mapping.mode = s.subSequence(start, start + end).toString();
        if (mapping.mode.startsWith("//")) {
            // Skip to line end, and restart trying to get a mapping.
            while (fcsi.hasNext()) {
                if (fcsi.next().equals('\n'))
                    break;
            }
            return next_mapping(s, fcsi.offset());
        }
        
        TextObjectsPlugin.find_last(fcsi, whitespace);
        start = fcsi.offset() - 1;
        end = TextObjectsPlugin.find_last(fcsi, new Functors.Not(whitespace));
        ++end;
        if (fcsi.offset() >= s.length()) {
            throw new ParseError("Parse error at line \"" + s.subSequence(fcsi.offset(), s.length()) + "\"");
        }
        mapping.key = s.subSequence(start, start + end).toString();

        TextObjectsPlugin.find_last(fcsi, whitespace);
        if (fcsi.offset() >= s.length()) {
            throw new ParseError("Parse error at line \"" + s.subSequence(fcsi.offset(), s.length()) + "\"");
        }
        Selection sel = TextObjectsPlugin.block(s, fcsi.offset(), "{}");
        if (sel == null) {
            throw new ParseError("Parse error at line \"" + s.subSequence(fcsi.offset(), s.length()) + "\"");
        }
        mapping.block = s.subSequence(sel.getStart() - 1, sel.getEnd() + 1).toString();
        // Selection end is the last character (i.e. range is inclusive []).
        mapping.end = sel.getEnd() + 1;
        return mapping;
    }

    public static void parse(VimageMap vimage_map, BufferedReader reader)
        throws java.io.IOException
    {
        String script = read(reader);
        Vector<Mapping> mappings = new Vector<Mapping>();

        int offset = 0;
        try {
            while (true) {
                Mapping mapping = next_mapping(script, offset);
                //Log.log(Log.DEBUG, mapping, "mapping {" + mapping.mode +
                //        ", " + mapping.key + ", " + mapping.block + " }");
                mappings.add(mapping);
                offset = mapping.end;
            }
        } catch (EOF ex) {
            ;
        } catch (ParseError ex) {
            Log.log(Log.ERROR, vimage_map, "error parsing at " + offset);
            throw ex;
        }

        for (Mapping mapping : mappings) {
            vimage_map.add(mapping.mode, mapping.key, mapping.block);
        }
    }
}

