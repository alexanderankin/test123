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

import org.gjt.sp.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 *  CTAGS_BACKGROUND. Main class of tags manipulation<br>
 *  <code>
 *  CTAGS_BG bg = new CTAGS_BG("C:/ctags55/ctags.exe");
 *  CTAGS_Parser parser = bg.getParser();
 *  CTAGS_Buffer buff = parser.parse("file_to_parse.java");
 *  sout(buff.get...());
 *  </code>
 */
public class CtagsMain implements Serializable {
    
	/** Full path and filename of ctags application */
    public static String ctagsExecutable;

    /** List of unsupported extensions */
    public static String[] unsupportedExtensions = {
        "txt", "html", "htm", "xml", "log"
    };
	
    
    private static final String LOADING_PROCESS_MSG = 
    	"Loading CtagsBuffer from ";
	private static final String UNSERIALIZATION_EXCEPTION_MSG = 
    	"Exception during loading from serialized ";
    private static final String SERIALIZATION_EXCEPTION_MSG = 
    	"Exception during serialization .jump file";

    public CtagsMain(String path) {
        ctagsExecutable = path;
    }

    public CtagsParser getParser() {
        return new CtagsParser();
    }

    public static void setCtagsExecutable(String path) {
        ctagsExecutable = path;
    }

    public static void setUnsupportedExtensions(String[] ext) {
        unsupportedExtensions = ext;
    }

    // TODO: Need throws IOException
    public CtagsBuffer reloadBuffer(CtagsBuffer buff) {
        CtagsBuffer newBuffer = null;

        try {
            newBuffer = getParser().parse(buff.getFileNames());
            buff = null;
        } catch (IOException e) {}

        return newBuffer;
    }

    /**
     *  Serialize given CtagsBuffer to file on disk
     *
     *@param  buff      CtagsBuffer to serialize
     *@param  filename  Name of new file
     *@return           boolean
     */

    //  TODO: Need throws IOException
    public static boolean saveBuffer(CtagsBuffer buff, String filename) {
        try {
            ObjectOutputStream out = 
            	new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(buff);
            out.close();
            return true;
        } 
        catch (Exception e) {
            Log.log(Log.WARNING, CtagsMain.class, SERIALIZATION_EXCEPTION_MSG);
            e.printStackTrace();
            return false;
        }
    }

    /**
     *  Restore already serialized CtagsBuffer
     *
     *@param  filename  Filename. Look saveBuffer(CtagsBuffer buff, String filename)
     *@return           boolean
     */
    public static CtagsBuffer loadBuffer(String filename) {
        try {
            Log.log(Log.DEBUG, CtagsMain.class, LOADING_PROCESS_MSG + filename);

            ObjectInputStream in = 
            	new ObjectInputStream(new FileInputStream(filename));
            return (CtagsBuffer) in.readObject();
        } 
        catch (Exception e) {
            Log.log(Log.WARNING, CtagsMain.class, UNSERIALIZATION_EXCEPTION_MSG + filename);
            e.printStackTrace();
            return null;
        }
    }
}
