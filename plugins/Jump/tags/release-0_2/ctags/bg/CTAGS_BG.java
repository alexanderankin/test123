/*  
// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:
 * CTAGS_BG.java - CTAGS_BACKGROUND. Main class of tags manipulation
 * Copyright (C) 5 May 2003 ã. 
 * author: Pavlikus
 * email: pavlikus@front.ru 
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ 
package ctags.bg;

//{{{ imports
import ctags.bg.*;

import java.util.*;
import java.io.*;
//}}}

/**
 *  CTAGS_BACKGROUND. Main class of tags manipulation<br> 
 *  <code>
 *  CTAGS_BG bg = new CTAGS_BG("C:/ctags55/ctags.exe");
 *  CTAGS_Parser parser = bg.getParser();
 *  CTAGS_Buffer buff = parser.parse("file_to_parse.java");
 *  sout(buff.get...());
 *  </code>
 */
public class CTAGS_BG implements Serializable
{
//{{{ fields
    public static boolean DEBUG = true; 
    
    /**
     *  Full path and filename of ctags application
     */
    public static String CTAGS_EXECUTABLE;
    
    /**
     *  List of unsupported extensions
     */
    public static String[] UnsupportedExtensions =
            {
            "txt", "html", "htm", "xml", "log"
            };
//}}}

//{{{ Constructor
    /**
     *  Constructor for the CTAGS_BG object
     *
     *@param  path  Path and filename of ctags executable
     */
    public CTAGS_BG(String path)
    {
        CTAGS_EXECUTABLE = path;
    }
//}}}
    
//{{{ CTAGS_Parser getParser()
    /**
     *  Return the CTAGS_Parser object
     *
     *@return    The parser value
     */
    public CTAGS_Parser getParser()
    {
        return new CTAGS_Parser();
    }
//}}}

//{{{ CTAGS_Buffer reloadBuffer
    /**
     *  Refresh CTAGS_Buffer
     *
     *@param  buff  CTAGS_Buffer to refresh
     *@return       Refreshed CTAGS_Buffer
     */
    public CTAGS_Buffer reloadBuffer(CTAGS_Buffer buff)
    {
        Vector files = buff.getFileNames();
        CTAGS_Parser p = this.getParser();
        try
        {
            CTAGS_Buffer Buffer = p.parse(files);
            buff = null;
            return Buffer;
        }
        catch (IOException e)
        {
            return null;
        }

    }
//}}}

//{{{ SERIALIZATION METHODS
    /**
     *  Serialize given CTAGS_Buffer to file on disk
     *
     *@param  buff      CTAGS_Buffer to serialize
     *@param  filename  Name of new file
     *@return           boolean
     */
    public static boolean saveBuffer(CTAGS_Buffer buff, String filename)
    {
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(buff);
            out.close();
            return true;
        }
        catch (Exception e)
        {
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
    public static CTAGS_Buffer loadBuffer(String filename)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            CTAGS_Buffer b = (CTAGS_Buffer) in.readObject();
            return b;
        }
        catch (Exception e)
        {
            System.out.println("CTAGS: Exception during loading from serialized object");
            return null;
        }
    }
//}}}
}
// end of CTAGS_BG