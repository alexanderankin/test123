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

import java.io.Serializable;


/**
* This class represents a one tag (line from ctags output), which splitted into
* logical units. For everything you need to know about this tag - available by
* its methods. getExtensionFields(), getFileName(), etc.
*/
public class CtagsEntry implements Serializable {
    private static final String CONSTRUCT_EXCEPTION_MSG = "Exception during construct CTAGS_Entry. Line:";
    private String stringValue;
    private String tagName;
    private String filename;
    private String exCmd;
    private String extensionFields;

    // In Java mode, for example: 
    // signature "m" for methods, "f"-fields etc.
    private String signature;

    // In Java mode, for example: 
    // "methods" is description of signature "m"
    private String signatureDescr;

    public CtagsEntry(String lineFromCtags) {
        try {
            int i = lineFromCtags.indexOf("\t", 0);
            tagName = stringValue = lineFromCtags.substring(0, i).trim();
            lineFromCtags = lineFromCtags.substring(i + 1);

            i = lineFromCtags.indexOf("\t", 0);
            filename = lineFromCtags.substring(0, i).trim();
            lineFromCtags = lineFromCtags.substring(i + 1).trim();

            // Better do ("$/^",0) but jextlauncer.cpp not parsed
            i = lineFromCtags.indexOf("/^", 0);

            int end = lineFromCtags.indexOf("/;\"", 0);
            exCmd = lineFromCtags.substring(i + 2, end - 1).trim();
            extensionFields = lineFromCtags.substring(end + 3).trim();
            
        } catch (Exception e) {
            System.out.println(CONSTRUCT_EXCEPTION_MSG + lineFromCtags);
            System.out.println("tag = " + tagName);
            System.out.println("file = " + filename);
            System.out.println("excmd = " + exCmd);
            System.out.println("ex_f = " + extensionFields);
        }
    }

    public String toString() {
        return this.stringValue;
    }

    public void setToStringValue(String value) {
        this.stringValue = value;
    }

    /**
    * Returns tag name.
    * For ctags line: Jext<TAB>pathtoJext.java<TAB>public class Jext<TAB> c -
    * it return "Jext"
    */
    public String getTagName() {
        return tagName;
    }

    /**
    * For ctags line:
    * Jext<TAB>pathtoJext.java<TAB>public class Jext<TAB> c -
    * it return path to Jext.java
    */
    public String getFileName() {
        return filename;
    }

    /**
    * For ctags line:
    * Jext<TAB>pathtoJext.java<TAB>public class Jext<TAB> c -
    * it return "public class Jext"
    */
    public String getExCmd() {
        return exCmd;
    }

    /**
    * Return everything after Excmd from line of ctags output.
    */
    public String getExtensionFields() {
        return extensionFields;
    }

    public String getSignature() {
        return signature;
    }

    public String getSignatureDescr() {
        return signatureDescr;
    }
}
