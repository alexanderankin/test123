/*
 *  Jump plugin for jEdit
 *  Copyright (c) 2003-2004 Pavlikus
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

import org.gjt.sp.util.Log;


public class CtagsParser implements Serializable {

	private static final String CTAGS_END_OF_LINE = ";\"\t";
	private static final String NO_FILES_MSG = "No files to parse";

	private String sort = "--sort=yes";
    private String excmd = "--excmd=pattern";    
    private String[] ctagsArguments;

    public CtagsParser() {
        ctagsArguments = new String[7];
        ctagsArguments[0] = CtagsMain.ctagsExecutable;
        ctagsArguments[1] = "--verbose=no";
        ctagsArguments[2] = sort;
        ctagsArguments[3] = excmd;
        ctagsArguments[4] = "-f";
        ctagsArguments[5] = "-";
        ctagsArguments[6] = "";
    }

    /**
    * Parse file and return new CTAGS_Buffer
    */
    public CtagsBuffer parse(String filename) throws IOException {
    	if (!isValidExtension(filename)) return null;
    	
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

    public boolean isValidExtension(String fn) {
        for (int i = 0; i < CtagsMain.unsupportedExtensions.length; i++) {
            if (fn.endsWith(CtagsMain.unsupportedExtensions[i])) {
                return false;
            }
        }
        return true;
    }
  
    private CtagsBuffer doParse(ArrayList list) throws IOException {
        CtagsBuffer buf = new CtagsBuffer(this);

        for (int i = 0; i < list.size(); i++) {
            if (isValidExtension((String)list.get(i))) {
                buf.append(parseFile((String)list.get(i), ctagsArguments), (String)list.get(i));
            }
        }

        if (buf.size() < 1) {
        	Log.log(Log.NOTICE, CtagsParser.class, NO_FILES_MSG);
        	buf = null;
        }

        return buf;
    }

    private CtagsBuffer parseFile(String fn, String[] arguments) throws IOException {
        arguments[6] = fn;

        Process ctags = Runtime.getRuntime().exec(arguments);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                    ctags.getInputStream()));

        CtagsBuffer buff = new CtagsBuffer(this);
        String line;

        while ((line = in.readLine()) != null) {
        	//System.out.println(line);
            if (line.lastIndexOf(CTAGS_END_OF_LINE) < 0) continue;
            buff.add(new CtagsEntry(line));
        }
        in.close();
        return buff;
    }
}
