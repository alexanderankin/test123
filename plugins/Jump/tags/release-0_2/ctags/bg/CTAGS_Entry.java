package ctags.bg;
// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:

import ctags.bg.*;
import java.io.*;
/**
* This class represents a one tag (line from ctags output), which splitted into logical units.
* For everything you need to know about this tag - available by its methods. getExtensionFields(), getFileName(), etc.
*/
public class CTAGS_Entry implements Serializable
{

    private String toString;
    private String Tag_Name;
    private String File_Name;
    private String Ex_Cmd;
    // In Java mode, for example: signature "m" for methods, "f"-fields etc.
    private String Signature;
    // In Java mode, for example: "methods" is description of signature "m"
    private String Signature_Descr;
    private String Extension_Fields;

    //**************************************************************************
    // CONSTRUCTOR(S)
    //**************************************************************************

    public CTAGS_Entry(String LineCtagsOutput)
    {

        //        int index = LineCtagsOutput.lastIndexOf(";\"\t");
        //
        //        //Grab Signature
        //        Signature = LineCtagsOutput.substring(index + 3, index + 4);

        //TODO Signature_Descr!!!
        //Signature_Descr = "temporary descr";

        //Define fields
        try
        {

            int i = LineCtagsOutput.indexOf("\t", 0);
            Tag_Name = LineCtagsOutput.substring(0, i);
            Tag_Name.trim();
            LineCtagsOutput = LineCtagsOutput.substring(i + 1);

            i = LineCtagsOutput.indexOf("\t", 0);
            File_Name = LineCtagsOutput.substring(0, i);
            File_Name.trim();
            LineCtagsOutput = LineCtagsOutput.substring(i + 1);
            LineCtagsOutput = LineCtagsOutput.trim();

            i = LineCtagsOutput.indexOf("/^",0);//????????? ("$/^",0) (but jextlauncer.cpp not parsed)
            int end = LineCtagsOutput.indexOf("/;\"",0);
            Ex_Cmd = LineCtagsOutput.substring(i + 2, end - 1);
            Ex_Cmd = Ex_Cmd.trim();
            LineCtagsOutput = LineCtagsOutput.substring(end + 3);
            Extension_Fields = LineCtagsOutput;

            toString = this.getTagName();

        }
        catch (Exception e)
        {
            System.out.println("Exception during construct CTAGS_Entry. Line-"+
                    LineCtagsOutput);
            System.out.println("tag = "+Tag_Name);
            System.out.println("file = "+File_Name);
            System.out.println("excmd = "+Ex_Cmd);
            System.out.println("ex_f = "+Extension_Fields);
        }

    }

    public String toString()
    {
        return this.toString;
    }

    //**************************************************************************
    // PUBLIC METHODS
    //**************************************************************************

    // I try to use this class in Jump.
    // TagsJumpAction.java and WorkspacesTagsAction.java needs to display different presentations of same CTAGS_Entry (AbstractListModel).
    // <TAG><SPACE><(EXCMD)> - for TagsJumpAction.java, and <FILENAME><SPACE><(EXCMD)> - for WorkspacesTagsAction.java
    // First, I tryed to define my own classes, which extends CTAGS_Entry and overrides toString(). But it always ends by ClassCastError at startup.
    // If you know the propely methods to extend CTAGS_Entry - mail me please. pavlikus@front.ru
    //
    public void setToStringValue(String i)
    {
        this.toString = i;
    }

    /**
    *  return tag name. For ctags line: Jext<TAB>pathtoJext.java<TAB>public class Jext<TAB> c - it return "Jext"
    */
    public String getTagName()
    {
        return Tag_Name;
    }
    /**
    *  For ctags line: Jext<TAB>pathtoJext.java<TAB>public class Jext<TAB> c - it return path to Jext.java
    */
    public String getFileName()
    {
        return File_Name; 
    }


    /**
    *  For ctags line: Jext<TAB>pathtoJext.java<TAB>public class Jext<TAB> c - it return "public class Jext"
    */   
    public String getExCmd()
    {
        return Ex_Cmd;
    }

    /**
    *   Return everything after Excmd from line of ctags output.
    */
    public String getExtensionFields()     
    {
        return Extension_Fields;
    }

    public String getSignature() 
    {
        return Signature;
    }

    public String getSignatureDescr() 
    {
        return Signature_Descr;
    }
}