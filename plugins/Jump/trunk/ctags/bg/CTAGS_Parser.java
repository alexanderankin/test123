package ctags.bg;

import java.util.*;
import java.io.*;
/**
*
*/
public class CTAGS_Parser implements Serializable
{
    /**
    *  SET true to sort ctags output (--sort=yes)
    */
    public boolean sort = false;
    /**
    * SET "pattern" or "numbers" (--excmd=pattern)
    */
    public String excmd = "pattern";

    private String[] ctags_args;
    //**************************************************************************
    // CONSTRUCTOR(S)
    //**************************************************************************

    // --sort=yes/no, excmd="pattern"/"number"
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

    }

    //**************************************************************************
    // PUBLIC
    //**************************************************************************

    /**
    * Parse file and return new CTAGS_Buffer
    */
    public CTAGS_Buffer parse(String filename) throws IOException
    {
        Vector v = new Vector();
        v.add(filename);
        return doParse(v);
    }

    /**
    * Parse list file and return new CTAGS_Buffer
    */
    public CTAGS_Buffer parse(Vector filenames) throws IOException
    {
        return doParse(filenames);
    }

    //**************************************************************************
    // PRIVATE
    //**************************************************************************

    private boolean checkUnsupportedExtensions(String fn)
    {

        for (int i = 0; i < CTAGS_BG.UnsupportedExtensions.length; i++)
        {

            if (fn.endsWith(CTAGS_BG.UnsupportedExtensions[i]) == true)
            {
                if (CTAGS_BG.DEBUG == true)
                {
                    //System.out.println("CTAGS: Invalid type of file: "+fn);
                }
                return false;
            }
        }

        return true;
    }

    // End of checkUnsupportedExtensions

    private CTAGS_Buffer doParse(Vector f) throws IOException
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
            System.out.println("CTAGS: No files to parse!");
            return null;
        }
        return b;
    }

    private CTAGS_Buffer parseFile(String fn,
            String[] arguments) throws IOException
    {

        arguments[6] = fn;
        
        // TESTING
        // for (int p=0 ; p<arguments.length; p++)
        // {
            // System.out.println("CTAGS: TESTING - "+arguments[p]);   
        // }
        
        Process ctags = Runtime.getRuntime().exec(arguments);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ctags.getInputStream()));

        CTAGS_Buffer buff = new CTAGS_Buffer(this);
        String line = new String();
        while ((line = in.readLine()) != null)
        {
            int index = line.lastIndexOf(";\"\t");
            if (index < 0)
            {
                continue;
            }
            buff.add(new CTAGS_Entry(line));

        }
        return buff;
    }

}