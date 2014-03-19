/*
 * HtmlDumper.java -- Dumps an HTML document tree. 
 * Copyright (C) 1999 Quiotix Corporation.  
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as 
 * published by the Free Software Foundation.  
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License (http://www.gnu.org/copyleft/gpl.txt)
 * for more details.
 */

package xml.parser.javacc;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Simple XmlVisitor which dumps out the document to the specified
 * output stream.
 *
 * @author Brian Goetz, Quiotix
 */

public class XmlDebugDumper extends XmlVisitor {
    protected PrintWriter out;

    public XmlDebugDumper(OutputStream os) {
        out = new PrintWriter(os);
    }

    public void finish() {
        out.flush();
    }

    public void visit(XmlDocument.Tag t) {
        out.print("Tag(" + t + ")");
    }

    public void visit(XmlDocument.EndTag t) {
        out.print("Tag(" + t + ")");
    }

    public void visit(XmlDocument.Comment c) {
        out.print("Comment(" + c + ")");
    }

    public void visit(XmlDocument.Text t) {
        out.print(t);
    }

    public void visit(XmlDocument.Newline n) {
        out.println("-NL-");
    }

    public void visit(XmlDocument.Annotation a) {
        out.print(a);
    }

    public void visit(XmlDocument.TagBlock bl) {
        out.print("<BLOCK>");
        visit(bl.startTag);
        visit(bl.body);
        visit(bl.endTag);
        out.print("</BLOCK>");
    }

    public static void main(String[] args) throws ParseException {
        XmlParser parser = new XmlParser(System.in);
        XmlDocument doc = parser.XmlDocument();
        doc.accept(new XmlDebugDumper(System.out));
    }
}






