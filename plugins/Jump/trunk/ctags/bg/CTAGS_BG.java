package ctags.bg;

import ctags.bg.*;

import java.util.*;
import java.io.*;

public class CTAGS_BG implements Serializable
{
    public static boolean DEBUG = true;
    //Full path and filename of ctags application
    public static String CTAGS_EXECUTABLE; 
    public static String[] UnsupportedExtensions = 
    {
        "txt", "html", "htm", "xml"
    };
    //**************************************************************************
    // CONSTRUCTOR(S)
    //**************************************************************************

    public CTAGS_BG(String path) 
    {
        CTAGS_EXECUTABLE = path;
    }
    




    //**************************************************************************
    // METHOD(S) FOR ANOTHER PLUGINS
    //**************************************************************************

    //Return the CTAGS_Parser object
    public CTAGS_Parser getParser() {
        return new CTAGS_Parser();
    }
// End of getParser
    
    
    public CTAGS_Buffer reloadBuffer(CTAGS_Buffer buff) {
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
//End of reloadBuffer
    
//********************************************************    
//SERIALIZATION METHODS    
//********************************************************

    public static boolean saveBuffer(CTAGS_Buffer buff, String filename)
    {
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            System.out.println("out generated. Start write");
            out.writeObject(buff);
            System.out.println("closing out");
            out.close();
            return true;   
        }
        catch (Exception e)
        {
            System.out.println("Exception during serialization. \n"+e);
            return false;
        }
    }
    
    public static CTAGS_Buffer loadBuffer(String filename)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            CTAGS_Buffer b = (CTAGS_Buffer)in.readObject();
            return b;   
        }
        catch (Exception e)
        {
            System.out.println("Exception during loading from ser. object");
            return null;
        }
    }
}