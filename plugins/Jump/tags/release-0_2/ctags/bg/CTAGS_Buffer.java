package ctags.bg;
// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:

import ctags.bg.*;
import java.util.*;

/**
*This class - storage for CTAGS_Entry objects.
*/
public class CTAGS_Buffer extends ArrayList
{
    //Store list of all files which already parsed into this CTAGS_Buffer
    private Vector Files;
    //Parser which generate this buffer
    private CTAGS_Parser Parser;
    
    //*********************************************************************************************
    // CONSTRUCTOR(S)
    //**************************************************************************

    /**
    * Create new CTAGS_Buffer
    * Not public. Call from CTAGS_Parser.getEmptyCTAGS_Buffer
    */

    public CTAGS_Buffer(CTAGS_Parser p)
    {
        super();
        Files = new Vector();
        Parser = p;
    }

    /**
    * Create CTAGS_Buffer which represent ctags output for fn file.
    * Later it can be append to existing CTAGS_Buffer
    */
    public CTAGS_Buffer(CTAGS_Parser p, String fn)
    {
        super();

        //Add filename to CTAGS_Buffer.Files
        for (int i = 0; i < Files.size(); i++)
        {
            if (fn.equals(Files.get(i).toString()) == false)
            {
                 this.addFileName(fn);
            }
            else
            {
                this.removeFile(fn);
                Files.add(fn);
            }
        }
        //Set CTAGS_Buffer.Parser
        Parser = p;
    }

    //**************************************************************************
    // PUBLIC METHODS
    //**************************************************************************

    /**
    * append CTAGS_Buffer.
    * when new file is added to Project we need the way to add it to CTAGS_Buffer.
    */
    public void append(CTAGS_Buffer b)
    {

        for (int i = 0; i<b.size(); i++)
        {
                CTAGS_Entry en = (CTAGS_Entry) b.get(i);
                this.add(en);
                this.addFileName(en.getFileName());

        }
    }
    
    /**
    * append CTAGS_Buffer.
    * when new file is added to Workspace we need the way to add it to CTAGS_Buffer.
    */
    public void append(CTAGS_Buffer b, String filename)
    {
        this.removeFile(filename);

        for (int i = 0; i<b.size(); i++)
        {
                CTAGS_Entry en = (CTAGS_Entry) b.get(i);
                this.add(en);
        }
        this.addFileName(filename);
    }

    /**
    * Refresh CTAGS_Buffer (after removing file from project, for example)
    */
    public void reload()
    {
        Vector files = Files;
        this.clear();
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // = Parser.parse(files);
    }

    /**
    * remove all tags of spec. file from CTAGS_Buffer
    */
    public void removeFile(String f)
    {
        Vector files = getTagsByFile(f);
        for (int i = 0; i < files.size(); i++)
        {
            this.remove((CTAGS_Entry) files.get(i));
        }
        Files.remove(f);

    }

    /**
    * Files (full path) for which CTAGS_Buffer was generated
    */
    public Vector getFileNames()
    {
        return Files;
    }

    /**
    * Returns Vector of CTAGS_Entries with spec. tag_name.
    * May be usualy it will return just one entry in vector,
    * but how about repeated tags? For example - all actionPerformed()
    */
    public Vector getEntry(String tag_name)
    {
        Vector v = new Vector();
        for (int i = 0; i < this.size(); i++)
        {
            CTAGS_Entry en = (CTAGS_Entry) this.get(i);
            if (tag_name.equals(en.getTagName()))
            {
                v.add(en);
            }
        }
        return v;
    }


    public void addFileName(String f) 
    {
        for (int i = 0; i < Files.size(); i++)
        {
            if (f.equals(Files.get(i).toString()) == true)
            {
            }
            else
            {
                Files.add(f);
            }
        }
    }

    /**
    * Scan entire CTAGS_Buffer for entries with specified signature (i.e. all "method" entries)
    */
    // public Vector get TagsBySignature(String signature)
    // {
    // ...
    // }

    /**
    * Scan entire CTAGS_Buffer for entries from spec. file which spec. signature.
    */
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public Vector getTagsBySignature(String signature) 
    {
        Vector v = new Vector();
        for (int i = 0; i < this.size(); i++)
        {
            CTAGS_Entry en = (CTAGS_Entry) this.get(i);
            if (signature.equals(en.getSignature()))
            {
                v.add(en);
            }
        }
        return v;
    }

    /**
    * Scan entire CTAGS_Buffer for entries from spec. file
    */
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public Vector getTagsByFile(String file)
    {
        Vector v = new Vector();
        for (int i = 0; i < this.size(); i++)
        {
            CTAGS_Entry en = (CTAGS_Entry) this.get(i);
            if (file.equals(en.getFileName()))
            {
                v.add((Object) en);
            }
        }
        return v;
    }

    //**************************************************************************
    // LinkedList's  methods
    //**************************************************************************

    public boolean add(CTAGS_Entry entry)
    {
        this.addFileName(entry.getFileName());
        return super.add(entry);  
    }

    // public void add(int index, CTAGS_Entry entry)
    // {
    // if (Files.contains(entry.getFileName()) == false)
    // {
    // Files.add(entry.getFileName());
    // }
    // //return super.add(index, entry);
    // }

    public void clear() 
    {
        Files.clear();
        super.clear();
    }

    public Object get(int index)
    {
        return super.get(index);
    }

}