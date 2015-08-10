/*
 * HtmlVisitor.java
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

import java.util.Iterator;

/**
 * Abstract class implementing Visitor pattern for XmlDocument objects.
 *
 * @author Brian Goetz, Quiotix
 */

public abstract class XmlVisitor {
    public void visit(XmlDocument.Tag t) {
    }

    public void visit(XmlDocument.EndTag t) {
    }

    public void visit(XmlDocument.Comment c) {
    }

    public void visit(XmlDocument.Text t) {
    }

    public void visit(XmlDocument.Newline n) {
    }

    public void visit(XmlDocument.Annotation a) {
    }

    public void visit(XmlDocument.TagBlock bl) {
        if (bl == null)
            return;
        bl.startTag.accept(this);
        visit(bl.body);
        bl.endTag.accept(this);
    }

    public void visit(XmlDocument.ElementSequence s) {
        if (s == null)
            return;
        for (Iterator iterator = s.iterator(); iterator.hasNext();) {
            XmlDocument.XmlElement htmlElement = (XmlDocument.XmlElement) iterator.next();
            htmlElement.accept(this);
        }
    }

    public void visit(XmlDocument d) {
        if (d == null)
            return;
        start();
        visit(d.elements);
        finish();
    }

    public void start() {
    }

    public void finish() {
    }
}

