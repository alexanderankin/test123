/*
 * HtmlFormatter.java -- HTML document pretty-printer
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import beauty.beautifiers.Beautifier;
import beauty.beautifiers.CSSBeautifier;
import beauty.beautifiers.DefaultBeautifier;
import beauty.parsers.ParserException;

/**
 * HtmlFormatter is a Visitor which traverses an HtmlDocument, dumping the
 * contents of the document to a specified output stream.  It assumes that
 * the documents has been preprocessed by HtmlCollector (which matches up
 * beginning and end tags) and by HtmlScrubber (which formats tags in a
 * consistent way).  In particular, HtmlScrubber should be invoked with the
 * TRIM_SPACES option to remove trailing spaces, which can confuse the
 * formatting algorithm.
 * <p/>
 * <P>The right margin and indent increment can be specified as properties.
 * <p>danson:
 * Modified for Beauty plugin for jEdit, added ability to handle jsps. Removed
 * the PrintWriter from the MarginWriter as PrintWriter munges line separators
 * in its own weird way.  For jEdit, I want the same line separator that has
 * been specified for the current buffer, which is not necessarily the system
 * line separator.  Formatted content is now written to a StringBuffer and can
 * be retrieved with the <code>toString</code> method.
 * <p>
 * Did some minor modification to the handling of PRE, SCRIPT, and STYLE blocks.
 * Formatting once would be fine, formatting the same file a second time would
 * cause extra blank lines to be added just before the closing tag.  This also
 * required some minor modification to the .jj file too.
 *
 * @author Brian Goetz, Quiotix
 * @see beauty.parsers.html.HtmlVisitor
 * @see beauty.parsers.html.HtmlCollector
 * @see beauty.parsers.html.HtmlScrubber
 */

public class HtmlFormatter extends HtmlVisitor {
    protected MarginWriter out;
    protected int rightMargin = 80;
    protected int indentSize = 2;
    protected String lineSeparator = System.getProperty("line.separator");

    protected static Set tagsIndentBlock = new HashSet();
    protected static Set tagsNewlineBefore = new HashSet();
    protected static Set tagsPreformatted = new HashSet();
    protected static Set tagsLanguages = new HashSet();
    protected static Set tagsTryMatch = new HashSet();

    // these tags _should_ be block tags, so indent the block
    protected static final String[] tagsIndentStrings = {"TABLE", "TR", "TD", "TH", "FORM", "HTML", "HEAD", "BODY", "SELECT", "OL", "UL", "LI", "DIV", "SPAN", "P", "H1", "H2", "H3", "H4", "H5", "H6"} ;

    // always start these tags on a new line
    protected static final String[] tagsNewlineBeforeStrings = {"P", "H1", "H2", "H3", "H4", "H5", "H6", "BR", "HR", "taglib", "OL", "UL", "LI", "LINK"} ;

    // don't format inside these tags
    protected static final String[] tagsPreformattedStrings = {"PRE"} ;    //, "SCRIPT", "STYLE", "%"} ;
 
    // indent but otherwise don't format inside these tags
    protected static final String[] tagsLanguagesStrings = {"SCRIPT", "STYLE", "%"} ;

    // these are often missing the closing tag, attempt to match
    //= {"A", "TD", "TH", "TR", "I", "B", "EM", "FONT", "TT", "UL"};
    protected static final String[] tagsTryMatchStrings = {"A", "I", "B", "EM", "FONT", "TT"} ;

    static {
        for (int i = 0; i < tagsIndentStrings.length; i++) {
            tagsIndentBlock.add(tagsIndentStrings[i]);
        }

        for (int i = 0; i < tagsNewlineBeforeStrings.length; i++) {
            tagsNewlineBefore.add(tagsNewlineBeforeStrings[i]);
        }

        for (int i = 0; i < tagsPreformattedStrings.length; i++) {
            tagsPreformatted.add(tagsPreformattedStrings[i]);
        }
 
        for (int i = 0; i < tagsLanguagesStrings.length; i++) {
            tagsLanguages.add(tagsLanguagesStrings[i]); 
        }

        for (int i = 0; i < tagsTryMatchStrings.length; i++) {
            tagsTryMatch.add(tagsTryMatchStrings[i]);
        }

    } ;
    protected TagBlockRenderer blockRenderer = new TagBlockRenderer();
    protected HtmlDocument.HtmlElement previousElement;
    protected boolean inPreBlock;
    protected boolean inLanguage;

    public HtmlFormatter() throws Exception {
        out = new MarginWriter();
        out.setRightMargin(rightMargin);
        out.setLineSeparator(lineSeparator);
    }

    public String toString() {
        return out.toString();
    }

    public void setRightMargin(int margin) {
        rightMargin = margin;
        out.setRightMargin(rightMargin);
    }

    public void setIndent(int indent) {
        indentSize = indent;
    }

    public void setLineSeparator(String ls) {
        lineSeparator = ls;
        out.setLineSeparator(lineSeparator);
    }
 
    String trimStart(String s, int max) {
        StringBuilder sb = new StringBuilder(s);
        int trimmed = 0;
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0)) && trimmed < max) {
            sb.deleteCharAt(0);
            ++trimmed;
        }
        return sb.toString();
    } 

    public void visit(HtmlDocument.TagBlock block) {
        boolean indent;
        boolean preformat;
        boolean haveLanguage;
        int wasMargin = 0;

        preformat = block.startTag.tagName == null || tagsPreformatted.contains(block.startTag.tagName.toUpperCase());
        haveLanguage = tagsLanguages.contains(block.startTag.tagName.toUpperCase());
 
        if (haveLanguage) {
            // ----- Don't worry about this anymore.  This was to support jsp files,
            // and the jsp parser does that better than this hack. 
            // -----
            // Format other languages with their own formatters.
            // There are 3 other languages at the moment: javascript, css, and java.
            // Combine the elements of the block (they are either text or new line elements)
            // into a single string, then pass that string to the appropriate formatter, then
            // split the string back into text and new line elements, refill the block with
            // the newly formatted elements, and finally apply the visitor pattern.
            // For the moment, only javascript and css are passed off as I don't have a
            // suitable formatter for java fragments yet.
            String tagName = block.startTag.tagName;
            if ("SCRIPT".equals(tagName.toUpperCase()) || "STYLE".equals(tagName.toUpperCase()) || "%".equals(tagName)) {
                try {
                    // combine the tag body into a single string
                    HtmlDocument.ElementSequence elements = block.body;
                    StringBuilder sb = new StringBuilder();
                    Iterator it = elements.iterator();
                    while (it.hasNext()) {
                        String s = it.next().toString();
                        s = trimStart(s, Integer.MAX_VALUE);
                        sb.append(s); 
                        if (!s.endsWith(lineSeparator)) {
                            sb.append(lineSeparator);   
                        }
                    }
 
                    // format the string with the appropriate beautifier
                    Beautifier beautifier;
                    String formatted = sb.toString();
                    /*
                    if ("SCRIPT".equals(tagName.toUpperCase())) {
                        beautifier = new DefaultBeautifier("javascript");
                    } else if ("%".equals(tagName)) {
                        beautifier = new DefaultBeautifier("java");
                    } else {
                        beautifier = new CSSBeautifier();
                    }
                    formatted = beautifier.beautify(sb.toString());
                    */
                    
                    // replace the tag body with the formatted text
                    String[] lines = formatted.split(lineSeparator);
                    elements = new HtmlDocument.ElementSequence(lines.length * 2);
                    elements.addElement(new HtmlDocument.Newline());
                    for (String line : lines) {
                        elements.addElement(new HtmlDocument.Text(line)); 
                        elements.addElement(new HtmlDocument.Newline());
                    }
                    block.body = elements;
                } catch (Exception /*ParserException*/ pe) {                    // NOPMD
                    // do nothing, just handle the block as a regular block
                }
            }
        }

        if (block.startTag.tagName != null && tagsTryMatch.contains(block.startTag.tagName.toUpperCase())) {
            blockRenderer.start();
            blockRenderer.setTargetWidth(out.getRightMargin() - out.getLeftMargin());
            blockRenderer.visit(block);
            blockRenderer.finish();
            if (! blockRenderer.hasBlownTarget()) {
                if (preformat) {
                    out.print(blockRenderer.getString());
                } else {
                    out.printAutoWrap(blockRenderer.getString());
                }

                previousElement = block.endTag;
                return ;
            }
        }

        // will get here only if we've failed the try-block test
        indent = (block.startTag.tagName != null && tagsIndentBlock.contains(block.startTag.tagName.toUpperCase())) || (block.startTag.isJspTag && block.startTag.tagEnd.equals(">"));
 
        if (preformat) {
            // preformat gets printed out as is
            wasMargin = out.getLeftMargin();
            out.setLeftMargin(0);
            visit(block.startTag);
            inPreBlock = true;
            visit(block.body);
            inPreBlock = false;
            out.setLeftMargin(wasMargin);
            visit(block.endTag);
        } else {
            if (haveLanguage) {
                // languages (javascript, css, java) get indented to align with start
                // and end tags, but are otherwise unaltered.
                visit(block.startTag);
                inLanguage = true;
                out.setLeftMargin(out.getLeftMargin() + indentSize);
                visit(block.body);
                out.setLeftMargin(out.getLeftMargin() - indentSize);
                inLanguage = false;
                visit(block.endTag);
            } else {
                if (indent) {
                    out.printlnSoft();
                    visit(block.startTag);
                    out.printlnSoft();
                    out.setLeftMargin(out.getLeftMargin() + indentSize);
                    visit(block.body);
                    out.setLeftMargin(out.getLeftMargin() - indentSize);
                    out.printlnSoft();
                    visit(block.endTag);
                    out.printlnSoft();
                    inPreBlock = false;
                } else {
                    visit(block.startTag);
                    visit(block.body);
                    visit(block.endTag);
                }
            }
        }


    }

    public void visit(HtmlDocument.Tag t) {
        String s = t.toString();
        int hanging;
        if ((t.tagName != null && tagsNewlineBefore.contains(t.tagName.toUpperCase())) || out.getCurPosition() + s.length() > out.getRightMargin()) {
            out.printlnSoft();
        }

        out.print(t.tagStart + (t.tagName == null ? "" : t.tagName));
        hanging = t.tagName == null ? 0 : t.tagName.length() + 1;
        // TODO: make this a user option
        ///boolean splitAttrs = jEdit.getBooleanProperty( "xmlindenter.splitAttributes", false );
        boolean splitAttrs = false;
        if (splitAttrs) {
            out.setLeftMargin(out.getLeftMargin() + indentSize);
            out.print(lineSeparator);
        }
        for (Iterator it = t.attributeList.attributes.iterator(); it.hasNext();) {
            HtmlDocument.Attribute a = (HtmlDocument.Attribute) it.next();
            if (splitAttrs) {
                out.printAttr(a.toString());
                if (it.hasNext()) {
                    out.print(lineSeparator);
                }
            } else {
                out.printAutoWrap(" " + a.toString(), hanging);
            }
        }
        if (splitAttrs) {
            out.setLeftMargin(out.getLeftMargin() - indentSize);
        }
 
        // TODO: this should be a setting for the user.  Not everyone likes having
        // an extra space separating the end of their tags
        if (t.isJspTag) {
            out.print(" ");            // got a jsp tag
        }
        out.print(t.tagEnd);
        if (t.tagEnd.endsWith("%>")) {
            out.println(); 
        }
        previousElement = t;
    }

    public void visit(HtmlDocument.EndTag t) {
        out.printAutoWrap(t.toString());
        if (t.tagName == null || tagsNewlineBefore.contains(t.tagName.toUpperCase())) {
            out.printlnSoft();
        } else {
            out.print(" ");
        }
        previousElement = t;
    }

    public void visit(HtmlDocument.Comment c) {
        out.print(c.toString());
        previousElement = c;
    }

    public void visit(HtmlDocument.Text t) {
        if (inPreBlock || inLanguage) {
            out.print(t.text);
        } else {
            // TODO: this should be a user setting
            // collapse multiple spaces to a single space
            t.text = t.text.replaceAll("[ ]+", " ");
            int start = 0;
            while (start < t.text.length()) {
                int index = t.text.indexOf(' ', start) + 1;
                if (index == 0) {
                    index = t.text.length();
                }

                out.printAutoWrap(t.text.substring(start, index));
                start = index;
            }
        }
        previousElement = t;
    }

    public void visit(HtmlDocument.Newline n) {
        if (inPreBlock) {
            out.print(lineSeparator);
        } else {
            if (inLanguage) {
                out.println();
            } else {
                if (previousElement instanceof HtmlDocument.Tag || previousElement instanceof HtmlDocument.EndTag || previousElement instanceof HtmlDocument.Comment || previousElement instanceof HtmlDocument.Newline) {
                    out.printlnSoft();
                } else {
                    if (previousElement instanceof HtmlDocument.Text) {
                        out.print(" ");
                    }
                }
            }
        }

        previousElement = n;
    }
 
    public void visit(HtmlDocument.BlankLines b) {
        // TODO: make this a user setting.  Currently, all multiple blank lines
        // are collapsed to a single blank line.  Making this a setting means
        // also adjusting the HtmlCollector and the parser.
        if (! (previousElement instanceof HtmlDocument.BlankLines)) {
            out.println(); 
        }
        previousElement = b;
    }

    public void start() {
        previousElement = null;
        inPreBlock = false;
        inLanguage = false;
    }

    public void finish() { }
}


/**
 * Utility class, used by HtmlFormatter, which adds some word-wrapping
 * and hanging indent functionality to a PrintWriter.
 */

class MarginWriter {
    protected int tabStop;
    protected int curPosition;
    protected int leftMargin;
    protected int rightMargin;
    StringBuilder sb = null;
    protected char[] spaces = new char[256];
    protected String lineSeparator = System.getProperty("line.separator");

    public MarginWriter() {
        sb = new StringBuilder();
        for (int i = 0; i < spaces.length; i++) {
            spaces[i] = ' ';
        }

    }

    public String toString() {
        return sb.toString();
    }

    public void print(String s) {
        if (s == null) {
            s = "";
        }

        if (curPosition == 0 && leftMargin > 0) {
            sb.append(spaces, 0, leftMargin);
            curPosition = leftMargin;
        }
        sb.append(s);
        curPosition += s.length();
    }

    public void printAutoWrap(String s) {
        if (s == null) {
            s = "";
        }

        if (curPosition > leftMargin && curPosition + s.length() > rightMargin) {
            println();
        }
        print(s);
    }

    public void printAutoWrap(String s, int hanging) {
        if (s == null) {
            s = "";
        }

        if (curPosition > leftMargin && curPosition + s.length() > rightMargin) {
            println();
            sb.append(spaces, 0, hanging + leftMargin);
            curPosition = leftMargin + hanging;
        }
        print(s);
    }

    public void println() {
        curPosition = 0;
        sb.append(lineSeparator);
    }

    public void printAttr(String s) {
        if (s == null) {
            s = "";
        }

        sb.append(spaces, 0, leftMargin);
        print(s);
    }

    public void printlnSoft() {
        if (curPosition > 0) {
            println();
        }
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    public int getRightMargin() {
        return rightMargin;
    }

    public int getCurPosition() {
        return (curPosition == 0 ? leftMargin : curPosition);
    }

    public void setCurPosition(int p) {
        curPosition = p >= 0 ? p : 0;
    }

    public void setLineSeparator(String ls) {
        this.lineSeparator = ls;
    }
}

/**
 * Utility class, used by HtmlFormatter, which tentatively tries to format
 * the contents of an HtmlDocument.TagBlock to see if the entire block can
 * fit on the rest of the line.  If it cannot, it gives up and indicates
 * failure through the hasBlownTarget method; if it can, the contents can
 * be retrieved through the getString method.
 */
// TODO: fix this.  Not sure it's needed.  Probably would be best to always
// put the start and end tags on separate lines.
class TagBlockRenderer extends HtmlVisitor {
    protected String s;
    protected boolean multiLine;
    protected boolean blownTarget;
    protected int targetWidth = 80;

    public void start() {
        s = "";
        multiLine = false;
        blownTarget = false;
    }

    public void finish() { }

    public void setTargetWidth(int w) {
        targetWidth = w;
    }

    public String getString() {
        return s;
    }

    public boolean isMultiLine() {
        return multiLine;
    }

    public boolean hasBlownTarget() {
        return blownTarget;
    }

    public void visit(HtmlDocument.Tag t) {
        if (s.length() < targetWidth) {
            s += t.toString();
        } else {
            blownTarget = true;
        }

    }

    public void visit(HtmlDocument.EndTag t) {
        if (s.length() < targetWidth) {
            s += t.toString();
        } else {
            blownTarget = true;
        }

    }

    public void visit(HtmlDocument.Comment c) {
        if (s.length() < targetWidth) {
            s += c.toString();
        } else {
            blownTarget = true;
        }

    }

    public void visit(HtmlDocument.Text t) {
        if (s.length() < targetWidth) {
            s += t.toString();
        } else {
            blownTarget = true;
        }

    }

    public void visit(HtmlDocument.Newline n) {
        multiLine = true;
        s += " ";
    }
}