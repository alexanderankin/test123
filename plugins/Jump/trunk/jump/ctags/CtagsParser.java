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
package jump.ctags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;


public class CtagsParser implements Serializable {
    /** SET true to sort ctags output (--sort=yes) */
    public boolean sort = false;

    /** SET "pattern" or "numbers" (--excmd=pattern) */
    public String excmd = "pattern";
    private String[] ctags_args;

    public CtagsParser() {
        String s = new String();

        if (this.sort == false) {
            s = "--sort=no";
        } else {
            s = "-sort=yes";
        }

        if ("numbers".equals(this.excmd) == false) {
            this.excmd = "--excmd=pattern";
        } else {
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
    }

    /**
    * Parse file and return new CTAGS_Buffer
    */
    public CtagsBuffer parse(String filename) throws IOException {
        ArrayList list = new ArrayList();
        list.add(filename);

        return doParse(list);
    }

    /**
    * Parse list file and return new CTAGS_Buffer
    */
    public CtagsBuffer parse(ArrayList filenames) throws IOException {
        return doParse(filenames);
    }

    public CtagsBuffer parseGlobalTags(String topFolder)
        throws IOException {
        String[] comm_line = new String[5];

        comm_line[0] = CTAGS_BG.CTAGS_EXECUTABLE;
        comm_line[1] = "-R";
        comm_line[2] = "-f";
        comm_line[3] = "-";
        comm_line[4] = topFolder;

        Process ctags = Runtime.getRuntime().exec(comm_line);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                    ctags.getInputStream()));

        CtagsBuffer buff = new CtagsBuffer(this);
        String line = new String();
        int index;

        while ((line = in.readLine()) != null) {
            index = line.lastIndexOf(";\"\t");

            if (index < 0) {
                continue;
            }

            System.out.println("Added-" + line);
            buff.add(new CtagsEntry(line));
        }

        in.close();

        return buff;
    }

    private boolean checkUnsupportedExtensions(String fn) {
        for (int i = 0; i < CTAGS_BG.UnsupportedExtensions.length; i++) {
            if (fn.endsWith(CTAGS_BG.UnsupportedExtensions[i]) == true) {
                return false;
            }
        }

        return true;
    }

    private CtagsBuffer doParse(final ArrayList list) throws IOException {
        CtagsBuffer b = new CtagsBuffer(this);
        CtagsBuffer b1 = new CtagsBuffer(this);

        for (int i = 0; i < list.size(); i++) {
            if (checkUnsupportedExtensions(list.get(i).toString()) == false) {
                continue;
            } else {
                b1 = parseFile(list.get(i).toString(), this.ctags_args);
                b.append(b1, list.get(i).toString());
            }
        }

        if (b.size() < 1) {
            System.out.println("Jump!.CTAGS: No files to parse!");

            return null;
        }

        return b;
    }

    private CtagsBuffer parseFile(String fn, String[] arguments)
        throws IOException {
        arguments[6] = fn;

        Process ctags = Runtime.getRuntime().exec(arguments);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                    ctags.getInputStream()));

        CtagsBuffer buff = new CtagsBuffer(this);
        String line;

        while ((line = in.readLine()) != null) {
            int index = line.lastIndexOf(";\"\t");

            if (index < 0) {
                continue;
            }

            buff.add(new CtagsEntry(line));
        }

        in.close();

        return buff;
    }
}
