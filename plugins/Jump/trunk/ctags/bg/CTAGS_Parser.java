/*
 *  Jump plugin for jEdit
 *  Copyright (c) 2003 Pavlikus
 *
 *  :tabSize=4:indentSize=4:
 *  :folding=explicit:collapseFolds=1:
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package ctags.bg;

    //{{{ imports
    import java.util.*;
    import java.io.*;
    import org.gjt.sp.jedit.*;
    import org.gjt.sp.jedit.gui.*; //}}}

public class CTAGS_Parser implements Serializable
{
    //{{{ fields
    /** SET true to sort ctags output (--sort=yes) */
    public boolean sort = false;
    /** SET "pattern" or "numbers" (--excmd=pattern) */
    public String excmd = "pattern";
    private String[] ctags_args; //}}}

    //{{{ CONSTRUCTOR
    public CTAGS_Parser()
    {
        String s = new String();
        if (this.sort == false)
        {
            s = "--sort=no";
        }
        else
        {
            s = "-sort=yes";
        }

        if ("numbers".equals(this.excmd) == false)
        {
            this.excmd = "--excmd=pattern";
        }
        else
        {
            this.excmd = "--excmd=numbers";
        }

        ctags_args = new String[7];

        ctags_args[0] = CTAGS_BG.CTAGS_EXECUTABLE;
        ctags_args[1] = "--verbose=yes";
        ctags_args[2] = s;
        ctags_args[3] = this.excmd;
        ctags_args[4] = "-f";
        ctags_args[5] = "-";
        ctags_args[6] = "";
    } //}}}

    //{{{ parse(String filename)
    /**
    * Parse file and return new CTAGS_Buffer
    */
    public CTAGS_Buffer parse(String filename) throws IOException
    {
        Vector v = new Vector();
        v.add(filename);
        return doParse(v);
    } //}}}

    //{{{ parse(Vector filenames)
    /**
    * Parse list file and return new CTAGS_Buffer
    */
    public CTAGS_Buffer parse(Vector filenames) throws IOException
    {
        return doParse(filenames);
    } //}}}

    //{{{ parseGlobalTags method
    public CTAGS_Buffer parseGlobalTags(String topFolder) throws IOException
    {
        String[] comm_line = new String[5];

        comm_line[0] = CTAGS_BG.CTAGS_EXECUTABLE;
        comm_line[1] = "-R";
        comm_line[2] = "-f";
        comm_line[3] = "-";
        comm_line[4] = topFolder;

        Process ctags = Runtime.getRuntime().exec(comm_line);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ctags.getInputStream()));

        CTAGS_Buffer buff = new CTAGS_Buffer(this);
        String line = new String();
        int index;
        while ((line = in.readLine()) != null)
        {
            index = line.lastIndexOf(";\"\t");
            if (index < 0)
            {
                continue;
            }
            System.out.println("Added-"+line);
            buff.add(new CTAGS_Entry(line));
        }
        in.close();
        return buff;
    } //}}}

    //{{{ private

    //{{{ checkUnsupportedExtensions
    private boolean checkUnsupportedExtensions(String fn)
    {
        for (int i = 0; i < CTAGS_BG.UnsupportedExtensions.length; i++)
        {
            if (fn.endsWith(CTAGS_BG.UnsupportedExtensions[i]) == true)
            {
                return false;
            }
        }

        return true;
    } //}}}

    //{{{ doParse
    private CTAGS_Buffer doParse(final Vector f) throws IOException
    {
        CTAGS_Buffer b = new CTAGS_Buffer(this);
        CTAGS_Buffer b1 = new CTAGS_Buffer(this);
        for (int i = 0; i < f.size(); i++)
        {
            if (checkUnsupportedExtensions(f.get(i).toString()) == false)
            {
                continue;
            }
            else
            {
                b1 = parseFile(f.get(i).toString(), this.ctags_args);
                b.append(b1,f.get(i).toString());
            }
        }
        if (b.size() < 1)
        {
            System.out.println("Jump!.CTAGS: No files to parse!");
            return null;
        }
        return b;
    } //}}}

    //{{{ parseFile
    private CTAGS_Buffer parseFile(String fn, String[] arguments) throws IOException
    {
        arguments[6] = fn;
        Process ctags = Runtime.getRuntime().exec(arguments);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ctags.getInputStream()));

        CTAGS_Buffer buff = new CTAGS_Buffer(this);
        String line;
        while ((line = in.readLine()) != null)
        {
            int index = line.lastIndexOf(";\"\t");
            if (index < 0)
            {
                continue;
            }
            buff.add(new CTAGS_Entry(line));
        }
        in.close();
        return buff;
    } //}}}

    //}}}
}