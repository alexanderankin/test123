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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


/**
 *  CTAGS_BACKGROUND. Main class of tags manipulation<br>
 *  <code>
 *  CTAGS_BG bg = new CTAGS_BG("C:/ctags55/ctags.exe");
 *  CTAGS_Parser parser = bg.getParser();
 *  CTAGS_Buffer buff = parser.parse("file_to_parse.java");
 *  sout(buff.get...());
 *  </code>
 */
public class CTAGS_BG implements Serializable {
    public static boolean DEBUG = true;

    /** Full path and filename of ctags application */
    public static String CTAGS_EXECUTABLE;

    /** List of unsupported extensions */
    public static String[] UnsupportedExtensions = {
        "txt", "html", "htm", "xml", "log"
    };

    /**
     *@param  path  Path and filename of ctags executable
     */
    public CTAGS_BG(String path) {
        CTAGS_EXECUTABLE = path;
    }

    /**
     *  Return the CTAGS_Parser object
     *
     *@return    The parser value
     */
    public CtagsParser getParser() {
        return new CtagsParser();
    }

    /**
     *  Refresh CTAGS_Buffer
     *
     *@param  buff  CTAGS_Buffer to refresh
     *@return       Refreshed CTAGS_Buffer
     */
    public CtagsBuffer reloadBuffer(CtagsBuffer buff) {
        ArrayList files = buff.getFileNames();
        CtagsParser p = this.getParser();

        try {
            CtagsBuffer Buffer = p.parse(files);
            buff = null;

            return Buffer;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     *  Serialize given CTAGS_Buffer to file on disk
     *
     *@param  buff      CTAGS_Buffer to serialize
     *@param  filename  Name of new file
     *@return           boolean
     */
    public static boolean saveBuffer(CtagsBuffer buff, String filename) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                        filename));
            out.writeObject(buff);
            out.close();

            return true;
        } catch (Exception e) {
            System.out.println("CTAGS: Exception during serialization. \n" + e);

            return false;
        }
    }

    public static boolean saveGlobalTagsBuffer(CtagsBuffer buff,
        String filename) {
        System.out.println("Try to save " + filename);

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                        filename));
            out.writeObject(buff);
            out.close();

            return true;
        } catch (Exception e) {
            System.out.println("CTAGS: Exception during serialization. \n" + e);

            return false;
        }
    }

    /**
     *  Restore already serialized CTAGS_Buffer
     *
     *@param  filename  Filename. Look saveBuffer(CTAGS_Buffer buff, String filename)
     *@return           boolean
     */
    public static CtagsBuffer loadBuffer(String filename) {
        try {
            System.out.println("Loading CTAGS_Buffer from " + filename);

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(
                        filename));
            CtagsBuffer b = (CtagsBuffer) in.readObject();

            return b;
        } catch (Exception e) {
            System.out.println(
                "CTAGS: Exception during loading from serialized object");
            e.printStackTrace();

            return null;
        }
    }
}
