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
import ctags.bg.*;
import java.io.*; //}}}
/**
* This class represents a one tag (line from ctags output), which splitted into logical units.
* For everything you need to know about this tag - available by its methods. getExtensionFields(), getFileName(), etc. 
*/
public class CTAGS_Entry implements Serializable
{

    //{{{ fields
    private String toString;
    private String Tag_Name;
    private String File_Name;
    private String Ex_Cmd;
    // In Java mode, for example: signature "m" for methods, "f"-fields etc.
    private String Signature;
    // In Java mode, for example: "methods" is description of signature "m"
    private String Signature_Descr;
    private String Extension_Fields; //}}}

    //{{{ CONSTRUCTOR
    public CTAGS_Entry(String LineCtagsOutput)
    {
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
    } //}}}

    //{{{ toString
    public String toString()
    {
        return this.toString;
    } //}}}

    //{{{ setToStringValue
    public void setToStringValue(String i)
    {
        this.toString = i;
    } //}}}

    //{{{ getTagName
    /**
    *  return tag name. For ctags line: Jext<TAB>pathtoJext.java<TAB>public class Jext<TAB> c - it return "Jext"
    */
    public String getTagName()
    {
        return Tag_Name;
    } //}}}

    //{{{ getFileName
    /**
    *  For ctags line: Jext<TAB>pathtoJext.java<TAB>public class Jext<TAB> c - it return path to Jext.java
    */
    public String getFileName()
    {
        return File_Name;
    } //}}}

    //{{{ getExCmd
    /**
    *  For ctags line: Jext<TAB>pathtoJext.java<TAB>public class Jext<TAB> c - it return "public class Jext"
    */ 
    public String getExCmd()
    {
        return Ex_Cmd;
    } //}}}

    //{{{ getExtensionFields
    /**
    *   Return everything after Excmd from line of ctags output.
    */
    public String getExtensionFields()
    {
        return Extension_Fields;
    } //}}}

    //{{{ getSignature
    public String getSignature()
    {
        return Signature;
    } //}}}

    //{{{ getSignatureDescr
    public String getSignatureDescr()
    {
        return Signature_Descr;
    } //}}}
}