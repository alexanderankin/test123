/*
 * HtmlDocument.java -- classes to represent HTML documents as parse trees
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

package beauty.parsers.html;

import java.util.*;

/**
 * Represents an HTML document as a sequence of elements.  The defined
 * element types are: Tag, EndTag, TagBlock (matched tag..end tag, with the
 * intervening elements), Comment, Text, Newline, and Annotation.
 * <p/>
 * <P> The various element types are defined as nested classes within
 * HtmlDocument.
 * <p>
 * Modified for Beauty plugin for jEdit, added ability to handle jsp.
 *
 * @author Brian Goetz, Quiotix
 * @see beauty.parsers.html.HtmlVisitor
 */

public class HtmlDocument {
    ElementSequence elements;
    static String NL = System.getProperty("line.separator");

    public HtmlDocument(ElementSequence s) {
        elements = s;
    }

    public void setLineSeparator(String ls) {
        NL = ls;
    }

    public void accept(HtmlVisitor v) {
        v.visit(this);
    }

    private static String dequote(String s) {
        if (s == null)
            return "";
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
            return s.substring(1, s.length()-1);
        else
            return s;
    }

    // The various elements of the HtmlDocument (Tag, EndTag, etc) are included
    // as nested subclasses largely for reasons of namespace control.
    // The following subclasses of HtmlElement exist: Tag, EndTag, Text, Comment,
    // Newline, Annotation, TagBlock.  Also, the additional classes
    // ElementSequence, Attribute, and AttributeList are defined here as well.

    // Each subclass of HtmlElement should have a visit() method in the
    // HtmlVisitor class.

    /**
     * Abstract class for HTML elements.  Enforces support for Visitors.
     */
    public static abstract class HtmlElement {
        public abstract void accept(HtmlVisitor v);
    };

    /**
     * HTML start tag.  Stores the tag name and a list of tag attributes.
     */
    public static class Tag extends HtmlElement {
        public String tagStart = "<";
        public String tagName;
        public AttributeList attributeList;
        public String tagEnd = ">";
        public boolean emptyTag = false;
        public boolean isJspTag = false;

        public Tag(String name, AttributeList a) {
            tagName = name;
            if (a == null)
                a = new AttributeList();
            attributeList = a;
        }

        public Tag(String tagStart, String name, AttributeList a, String tagEnd) {
            if (tagStart != null)
                this.tagStart = tagStart;
            tagName = name;
            if (a == null)
                a = new AttributeList();
            attributeList = a;
            this.tagEnd = tagEnd;
        }

        public void setEmpty(boolean b) {
            emptyTag = b;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public boolean hasAttribute(String name) {
            return attributeList.contains(name);
        }

        public boolean hasAttributeValue(String name) {
            return attributeList.hasValue(name);
        }

        public String getAttributeValue(String name) {
            return attributeList.getValue(name);
        }

        public void setIsJspTag( boolean b ) {
            isJspTag = b;
        }

        public int getLength() {
            int length = 0;
            for (Iterator iterator = attributeList.attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                length += 1 + (attribute.getLength());
            }
            return length + tagName.length() + 2 + (emptyTag ? 1 : 0);
        }

        public String toString() {
            StringBuffer s = new StringBuffer();
            s.append(tagStart);
            s.append(tagName);
            for (Iterator iterator = attributeList.attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                s.append(' ');
                s.append(attribute.toString());
            }
            s.append(tagEnd);
            return s.toString();
        }
    }

    /**
     * Html end tag.  Stores only the tag name.
     */
    public static class EndTag extends HtmlElement {
        public String tagName;
        public boolean isJspTag = false;

        public EndTag(String t) {
            tagName = t;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return 3 + tagName.length();
        }

        public void setIsJspTag( boolean b ) {
            isJspTag = b;
        }

        public String toString() {
            return "</" + tagName + ">";
        }
    }

    /**
     * A tag block is a composite structure consisting of a start tag
     * a sequence of HTML elements, and a matching end tag.
     */
    public static class TagBlock extends HtmlElement {
        public Tag startTag;
        public EndTag endTag;
        public ElementSequence body;

        public TagBlock(String name, AttributeList aList, ElementSequence b) {
            startTag = new Tag(name, aList);
            endTag = new EndTag(name);
            body = b;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public String toString() {
            return startTag.toString();
        }
    }

    public static class JspScriptletBlock extends TagBlock {
        public JspScriptletBlock(ElementSequence b) {
            super("", null, b);
            startTag = new JspScriptletStartTag();
            endTag = new JspScriptletEndTag();
        }
        
        public String toString() {
            return "";   
        }
    }

    public static class JspScriptletStartTag extends Tag {
        public JspScriptletStartTag() {
            super("%", null);
            tagStart = "<";
            tagEnd = "";
        }

        public int getLength() {
            return 2;
        }

        public String toString() {
            return "<%";
        }
    }

    public static class JspScriptletEndTag extends EndTag {
        public JspScriptletEndTag() {
            super("%");
        }

        public int getLength() {
            return 2;
        }

        public String toString() {
            return "%>";
        }
    }

    /**
     * HTML comments.
     */
    public static class Comment extends HtmlElement {
        public String comment;

        public Comment(String c) {
            comment = c;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return 3 + comment.length();
        }

        public String toString() {
            return comment;
        }
    }

    /**
     * JSP comments.
     */
    public static class JspComment extends HtmlElement {
        public String comment;

        public JspComment(String c) {
            comment = c;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return 4 + comment.length();
        }

        public String toString() {
            return "<" + comment + ">";
        }
    }

    /**
     * Plain text
     */
    public static class Text extends HtmlElement {
        public String text;

        public Text(String t) {
            text = t;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return text.length();
        }

        public String toString() {
            return text;
        }
    }

    /**
     * End of line indicator.
     */
    public static class Newline extends HtmlElement {

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return NL.length();
        }

        public String toString() {
            return NL;
        }
    }

    /**
     * One or more blank lines.
     */
    public static class BlankLines extends HtmlElement {

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return (NL + NL).length();
        }

        public String toString() {
            return NL + NL;
        }
    }

    /**
     * A sequence of HTML elements.
     */
    public static class ElementSequence {
        private List elements;

        public ElementSequence(int n) {
            elements = new ArrayList(n);
        }

        public ElementSequence() {
            elements = new ArrayList();
        }

        public void addElement(HtmlElement o) {
            elements.add(o);
        }

        public int size() {
            return elements.size();
        }

        public Iterator iterator() {
            return elements.iterator();
        }

        public void setElements(List coll) {
            elements.clear();
            elements.addAll(coll);
        }
    }

    /**
     * Annotations.  These are not part of the HTML document, but
     * provide a way for HTML-processing applications to insert
     * annotations into the document.  These annotations can be used by
     * other programs or can be brought to the user's attention at a
     * later time.  For example, the HtmlCollector might insert an
     * annotation to indicate that there is no corresponding start tag
     * for an end tag.
     */
    public static class Annotation extends HtmlElement {
        String type, text;

        public Annotation(String type, String text) {
            this.type = type;
            this.text = text;
        }

        public void accept(HtmlVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return 14 + type.length() + text.length();
        }

        public String toString() {
            return "<!--NOTE(" + type + ") " + text + "-->";
        }
    }

    public static class Attribute {
        public String name, value;
        public boolean hasValue;

        public Attribute(String n) {
            name = n;
            hasValue = false;
        }

        public Attribute(String n, String v) {
            name = n;
            value = v;
            hasValue = v != null && !v.isEmpty();
        }

        public int getLength() {
            return (hasValue ? name.length() + 1 + value.length() : name.length());
        }

        public String toString() {
            return (hasValue ? name + "=" + quote(value) : name);
        }

        public String quote(String s) {
            if (s == null) {
                return "";
            }
            if (!s.startsWith("\"")) {
                s = "\"" + s;
            }
            if (!s.endsWith("\"")) {
                s += "\"";
            }
            return s;
        }
    }

    public static class AttributeList {
        public List attributes = new ArrayList();

        public void addAttribute(Attribute a) {
            attributes.add(a);
        }

        public boolean contains(String name) {
            for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                if (attribute.name.equalsIgnoreCase(name))
                    return true;
            }
            return false;
        }

        public boolean hasValue(String name) {
            for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                if (attribute.name.equalsIgnoreCase(name) && attribute.hasValue)
                    return true;
            }
            return false;
        }

        public String getValue(String name) {
            for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                if (attribute.name.equalsIgnoreCase(name) && attribute.hasValue)
                    return dequote(attribute.value);
            }
            return null;
        }
    }
}



