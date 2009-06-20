/*
 Copyright (c) 2004-2005, The Dojo Foundation
 All Rights Reserved.

 Licensed under the Academic Free License version 2.1 or above OR the
 modified BSD license. For more information on Dojo licensing, see:

 http://dojotoolkit.org/community/licensing.shtml <http://dojotoolkit.org/community/licensing.shtml>

 Code donated to the Dojo Foundation by AOL LLC under the terms of
 the Dojo CCLA (http://dojotoolkit.org/ccla.txt).

 */
package sidekick.ecmascript.parser;

import java.util.*;
import java.util.regex.*;


/**
 * Objects of this class hold multi-line c-style comments in original source
 * form and for javadoc comments also parsed and broken down into tags. API
 * exists to query the tags, get the comment body and beautifying the comment
 * body that turns plain text into more formatted html according to some wild
 * guesses and silly heuristics.
 *
 *
 * @since JDK 1.4
 */
public class Comment extends Object {

    /**
     * Simple class holding a javadoc tag
     */
    static public class Tag {

        /**
         * tag key
         */
        public String key;

        /**
         * tag value
         */
        public String value;

        @Override
        public String toString() {
            return "@" + key + " " + value;
        }
    }

    /**
     * original comment string
     */
    private String comment;

    /**
     * list of tags (instances of Comment.Tag)
     */
    private ArrayList<Tag> tags;

    /**
     * list of lines that are not empty and not part of the tags (the comment
     * body)
     */
    private ArrayList<String> lines;

    /**
     * comment body string lazily calculated from lines
     */
    private String commentBody;

    /**
     * <code>true</code> if receiver is a javadoc comment
     */
    private boolean isJavadoc;

    /**
     * <code>true</code> if receiver is a marked plain text comment
     */
    private boolean markedPlainText;

    /**
     * Creates an instance of <code>Comment</code> from the specified comment
     * string
     *
     * @param comment
     *            comment string
     * @exception Exception
     *                if parsing the comment generates a parsing error
     */
    public Comment(String comment) throws Exception {
        super();
        this.comment = comment;
        tags = new ArrayList<Tag>();
        lines = new ArrayList<String>();
        isJavadoc = false;
        parse();

        // logic for this case:
        // /**
        // * @return (Type) bla
        // */
        // make bla the comment body
        String returnTag = getTag("return");
        if ((returnTag != null) && (lines.size() == 0)) {
            String[] rc = Comment.parseTypedDescription(returnTag);

            lines.add("Returns " + rc[1]);
        }
    }

    /**
     * Moves the passed in cursor passed the whitespace (excluding newlines).
     *
     * @param cursor
     *            current position in comment string
     * @return position in comment string after skipping whitespace
     */
    private int skipWS(int cursor) {
        char c = comment.charAt(cursor);

        while (Character.isWhitespace(c) && (c != '\r') && (c != '\n')) {
            cursor++;
            c = comment.charAt(cursor);
        }

        return cursor;
    }

    /**
     * Determines if specified position in comment string is the end-comment
     * marker (ie the current char is a "*" and the next char is a "/".
     *
     * @param cursor
     *            current position in comment string
     * @return <code>true</code> if current position is end-comment marker
     */
    private boolean isCommentEnd(int cursor) {
        return ((comment.charAt(cursor + 1) == '/')
                && (comment.charAt(cursor) == '*') && (comment
                .charAt(cursor - 1) != '\\'));
    }

    /**
     * Parses the comment string collecting the comment body lines and in case
     * of javadoc comments the javadoc tags.
     *
     * @exception Exception
     *                in case of parsing error
     */
    private void parse() throws Exception {
        int cursor = 0;
        char c;
        Tag lastTagObj = null;
        boolean seenTags = false;
        boolean startedBody = false;

        try {
            if ((comment.charAt(cursor) != '/')
                    || (comment.charAt(cursor + 1) != '*')) {
                throw new Exception(
                        "comment parser: comment string doesn't start with /* "
                                + comment);
            }

            if (comment.charAt(cursor + 2) == '*') {
                isJavadoc = true;
                cursor += 3;
            } else {
                cursor += 2;
            }

            if (comment.startsWith("TEXT", cursor)) {
                cursor += 4;
                markedPlainText = true;
            } else if (comment.startsWith("HTML", cursor)) {
                cursor += 4;
            }

            if (comment.charAt(cursor) == '/') {
                return;
            }

            boolean done = false;

            boolean seenStar = false;
            StringBuffer leadingBuffer = null;

            while (!done) {
                c = comment.charAt(cursor);

                seenStar = false;
                leadingBuffer = null;

                while ((!isCommentEnd(cursor))
                        && ((Character.isWhitespace(c) && (c != '\r') && (c != '\n')) || (c == '*'))) {
                    if (seenStar) {
                        if (leadingBuffer == null) {
                            leadingBuffer = new StringBuffer();
                        }
                        leadingBuffer.append(c);
                    } else if (c == '*') {
                        seenStar = true;
                    }
                    cursor++;
                    c = comment.charAt(cursor);
                }

                if (isCommentEnd(cursor)) {
                    done = true;
                } else {
                    if (c == '@') {
                        seenTags = true;
                        cursor++;
                        cursor = skipWS(cursor);
                        c = comment.charAt(cursor);

                        if (!Character.isJavaIdentifierStart(c)) {
                            throw new Exception(
                                    "comment parser: identifier expected at position "
                                            + cursor + " in comment " + comment);
                        }

                        StringBuffer tag = new StringBuffer();

                        while ((!isCommentEnd(cursor))
                                && Character.isJavaIdentifierPart(c)) {
                            tag.append(c);
                            cursor++;
                            c = comment.charAt(cursor);
                        }

                        if (isCommentEnd(cursor)) {
                            done = true;
                        } else {
                            cursor = skipWS(cursor);
                            c = comment.charAt(cursor);

                            StringBuffer line = new StringBuffer();

                            while ((!isCommentEnd(cursor)) && (c != '\r')
                                    && (c != '\n')) {
                                line.append(c);
                                cursor++;
                                c = comment.charAt(cursor);
                            }

                            if (tag.length() > 0) {
                                Tag tagObj = new Tag();

                                tagObj.key = tag.toString();

                                String cLine = line.toString().trim();

                                if (cLine.length() > 0) {
                                    tagObj.value = cLine;
                                }

                                tags.add(tagObj);
                                lastTagObj = tagObj;
                            }

                            if (isCommentEnd(cursor)) {
                                done = true;
                            } else {
                                cursor++;
                                if (comment.charAt(cursor) == '\n') {
                                    cursor++;
                                }
                            }
                        }
                    } else {
                        StringBuffer line = new StringBuffer();

                        while ((!isCommentEnd(cursor)) && (c != '\r')
                                && (c != '\n')) {
                            line.append(c);
                            cursor++;
                            c = comment.charAt(cursor);
                        }

                        if (!seenTags) {
                            // String cLine = line.toString().trim();
                            // dip made me do it
                            String cLine = (leadingBuffer != null) ? leadingBuffer
                                    .toString()
                                    + line.toString()
                                    : line.toString();

                            if (startedBody) {
                                lines.add(cLine);
                            } else {
                                if (cLine.length() > 0) {
                                    lines.add(cLine);
                                    startedBody = true;
                                }
                            }
                        } else {
                            if (line.length() > 0) {
                                if (lastTagObj != null) {
                                    // lastTagObj.value = lastTagObj.value +
                                    // "\n" + line.toString().trim();
                                    // dip made me do it
                                    if (leadingBuffer != null) {
                                        lastTagObj.value = lastTagObj.value
                                                + "\n"
                                                + leadingBuffer.toString()
                                                + line.toString();
                                    } else {
                                        lastTagObj.value = lastTagObj.value
                                                + "\n" + line.toString();
                                    }
                                }
                            }
                        }

                        if (isCommentEnd(cursor)) {
                            done = true;
                        } else {
                            cursor++;
                            if (comment.charAt(cursor) == '\n') {
                                cursor++;
                            }
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException exc) {
            throw new Exception(
                    "comment parser: unexpected end in comment string: "
                            + comment);
        }
    }

    /**
     * Returns <code>true</code> if receiver is a javadoc comment.
     *
     * @return <code>true</code> if receiver is a javadoc comment
     */
    public boolean isJavadoc() {
        return isJavadoc;
    }

    /**
     * Returns an unmodifiable list of Strings representing the comment body
     * lines.
     *
     * @return list of comment body lines
     */
    public List getLines() {
        return Collections.unmodifiableList(lines);
    }

    /**
     * Returns the comment body.
     *
     * @return comment body
     */
    public String getCommentBody() {
        if (commentBody == null) {
            StringBuffer buffer = new StringBuffer();
            Iterator iter = lines.iterator();
            boolean appended = false;

            while (iter.hasNext()) {
                String line = (String) iter.next();

                if (appended) {
                    buffer.append('\n');
                } else {
                    appended = true;
                }
                buffer.append(line);
            }

            commentBody = buffer.toString();
        }

        return commentBody;
    }

    /**
     * Returns the first sentence of the comment body (suitable for summary
     * lines). In case no sentence ending char sequence is found (period
     * followed by blank) then up to maxLength chars are returned.
     *
     * @param maxLength
     *            maximum length of returned string
     * @return first sentence of comment body.
     */
    public String getFirstSentenceInCommentBody(int maxLength) {
        String cBody = getCommentBody();
        String result = null;

        int sentenceEndIndex = -1;
        int i = 0;
        boolean sawDot = false;
        int n = cBody.length();

        while ((i < n) && (sentenceEndIndex == -1)) {
            char c = cBody.charAt(i);
            if (c == '.') {
                sawDot = true;
                if (i == n - 1) {
                    sentenceEndIndex = i;
                }
            } else if (sawDot) {
                sawDot = false;
                if ((c == ' ') || (c == '\r') || (c == '<') || (c == '\n')
                        || (c == '\t')) {
                    sentenceEndIndex = i - 1;
                }
            }
            i++;
        }

        if (sentenceEndIndex != -1) {
            result = cBody.substring(0, sentenceEndIndex + 1);
        } else {
            int ll = Math.min(Math.max(maxLength, 3) - 3, cBody.length());

            if (ll == cBody.length()) {
                result = cBody.substring(0, ll);
            } else {
                result = cBody.substring(0, ll) + "...";
            }
        }

        return result;
    }

    static private Pattern detectPlaintextPattern = Pattern
            .compile("</?plaintext\\s*([^>]*)>");

    /**
     * Returns the comment body transforming &lt;plaintext&gt; blocks into
     * &lt;pre&gt blocks and escaping all the &lt; and &gt; in there.
     *
     * @return comment body
     */
    public String getHtmlifiedCommentBody() {
        String cBody = getCommentBody();

        // quick bail-outs first
        if (markedPlainText) {
            return Util.escape2Html(cBody);
        }

        // detect if cBody contains <plaintext> blocks
        Matcher matcher = Comment.detectPlaintextPattern.matcher(cBody);

        int cursor = 0;
        boolean inPlainTextBlock = false;

        StringBuffer buffer = null;

        while (matcher.find()) {
            if (buffer == null) {
                buffer = new StringBuffer();
            }

            int n = matcher.start();

            if ((!inPlainTextBlock) && (cBody.charAt(n + 1) == 'p')) {

                if (cursor < n) {
                    buffer.append(cBody.substring(cursor, n));
                }
                inPlainTextBlock = true;

                String preAttr = matcher.group(1);

                if (preAttr != null) {
                    preAttr = preAttr.trim();

                    if (preAttr.length() > 0) {
                        buffer.append("<pre ");
                        buffer.append(preAttr);
                        buffer.append(">");
                    } else {
                        buffer.append("<pre>");
                    }
                } else {
                    buffer.append("<pre>");
                }
            } else if (inPlainTextBlock && (cBody.charAt(n + 1) == '/')) {
                int i = cursor;

                while (i < n) {
                    char c = cBody.charAt(i);

                    switch (c) {
                    case '<':
                        buffer.append("&lt;");
                        break;
                    case '>':
                        buffer.append("&gt;");
                        break;
                    case '&':
                        buffer.append("&amp;");
                        break;
                    case '"':
                        buffer.append("&quot;");
                        break;
                    case '\'':
                        buffer.append("&#039;");
                        break;
                    default:
                        buffer.append(c);
                        break;
                    }

                    i++;
                }

                inPlainTextBlock = false;
                buffer.append("</pre>");
            }
            cursor = matcher.end();
        }

        if ((buffer != null) && (cursor < cBody.length())) {
            buffer.append(cBody.substring(cursor, cBody.length()));
        }

        return buffer == null ? cBody : buffer.toString();
    }

    /**
     * Returns an unmodifiable list of javadoc tags (instances of Comment.Tag)
     *
     * @return list of javadoc tags
     */
    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Returns the first javadoc tag value in the list of tags whose key matches
     * the specified key
     *
     * @param key
     *            javadoc tag key
     * @return String of the javadoc tag value
     */
    public String getTag(String key) {
        Iterator iter = tags.iterator();

        while (iter.hasNext()) {
            Tag tag = (Tag) iter.next();

            if (tag.key.equals(key)) {
                return tag.value;
            }
        }

        return null;
    }

    /**
     * Returns all javadoc tag values in the list of tags whose keys match the
     * specified key
     *
     * @param key
     *            javadoc tag key
     * @return List of Strings that are the javadoc tag values
     */
    public List<String> getTags(String key) {
        List<String> result = new LinkedList<String>();
        Iterator iter = tags.iterator();

        while (iter.hasNext()) {
            Tag tag = (Tag) iter.next();

            if (tag.key.equals(key)) {
                result.add(tag.value);
            }
        }

        return result;
    }

    /**
     * Returns all javadoc tag values in the list of tags whose keys match the
     * specified key
     *
     * @param key
     *            javadoc tag key
     * @return List of Tag instances
     */
    public List<Tag> getTags(Pattern pattern) {
        List<Tag> result = new LinkedList<Tag>();
        Iterator<Tag> iter = tags.iterator();
        Matcher matcher = null;

        while (iter.hasNext()) {
            Tag tag = iter.next();

            if (matcher == null) {
                matcher = pattern.matcher(tag.key);
            } else {
                matcher.reset(tag.key);
            }

            if (matcher.matches()) {
                result.add(tag);
            }
        }

        return result;
    }

    /**
     * Returns <code>true</code> if receiver has a javadoc tag matching
     * specified key.
     *
     * @param key
     *            javadoc tag key
     * @return <code>true</code> if receiver has a javadoc tag matching
     *         specified key
     */
    public boolean containsTag(String key) {
        Iterator<Tag> iter = tags.iterator();

        while (iter.hasNext()) {
            Tag tag = iter.next();

            if (tag.key.equals(key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the original comment string.
     *
     * @return comment string
     */
    public String getOriginalSource() {
        return comment;
    }

    /**
     * Returns a version of the comment string with the plain text portion
     * converted to more structured html. Also deletes all the type information
     * given with the "(CXFoo)" convention
     *
     * Used in the deprecated javadoc task. For ogredoc use
     * getHtmlifiedCommentBody()
     *
     * @deprecated
     * @return comment string
     */
    @Deprecated
    public String getHtmlifiedSource() {
        if (!isJavadoc) {
            return comment;
        }

        StringBuffer buffer = new StringBuffer();

        buffer.append("/**\n");
        String lastLine = null;

        Iterator iter = lines.iterator();
        while (iter.hasNext()) {
            String line = ((String) iter.next());

            if (line.length() > 0) {
                buffer.append(" * ");

                if (lastLine == null) {
                    // we're in the first line
                    if (line.startsWith("(")) {
                        int closeParenIndex = line.indexOf(')');

                        if (closeParenIndex > 2) {
                            String typeSuggestion = line.substring(1,
                                    closeParenIndex);

                            if (Util.isJavaIdentifier(typeSuggestion)
                                    && ((closeParenIndex + 1) < line.length())) {
                                line = line.substring(closeParenIndex + 1)
                                        .trim();
                            }
                        }
                    }
                }

                buffer.append(line);
                buffer.append('\n');
            } else {
                if (lastLine != null) {
                    if ((!lastLine.endsWith("<br>"))
                            && (!lastLine.endsWith("<p>"))) {
                        buffer.append(" * <p>\n");
                    }
                }
            }

            lastLine = line.toLowerCase();
        }

        buffer.append(" * \n");

        iter = tags.iterator();

        while (iter.hasNext()) {
            Tag tag = (Tag) iter.next();

            buffer.append(" * @");
            buffer.append(tag.key);
            if (tag.value != null) {
                buffer.append(" ");

                String line = tag.value;

                if (tag.key.equals("return")) {
                    if (line.startsWith("(")) {
                        int closeParenIndex = line.indexOf(')');

                        if (closeParenIndex > 2) {
                            String typeSuggestion = line.substring(1,
                                    closeParenIndex);

                            if (Util.isJavaIdentifier(typeSuggestion)
                                    && ((closeParenIndex + 1) < line.length())) {
                                line = line.substring(closeParenIndex + 1)
                                        .trim();
                            }
                        }
                    }
                } else if (tag.key.equals("param")) {
                    if (Comment.parseNamedTypedDescription(tag.value)[1] != null) {
                        int lparenIndex = line.indexOf('(');
                        int rparenIndex = line.indexOf(')');

                        line = line.substring(0, lparenIndex)
                                + line.substring(rparenIndex + 1);
                    }
                }

                buffer.append(line);
            }

            buffer.append('\n');
        }

        buffer.append("*/");

        return buffer.toString();
    }

    static public String[] parseNamedTypedDescription(String tagValue) {
        Pattern pattern = Pattern.compile("(\\w+)(\\s+\\((\\w+)\\))?(.*)",
                Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(tagValue);
        String[] returnVal = new String[3];

        if (matcher.matches()) {
            returnVal[0] = matcher.group(1);
            returnVal[1] = matcher.group(3);
            returnVal[2] = matcher.group(4);

            if (returnVal[2] != null) {
                returnVal[2] = returnVal[2].trim();
            }
        }

        return returnVal;
    }

    static public String[] parseTypedDescription(String tagValue) {
        Pattern pattern = Pattern.compile("(\\(\\w+\\))?(.*)",
                Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(tagValue);
        String[] returnVal = new String[2];

        if (matcher.matches()) {
            returnVal[0] = matcher.group(1);
            returnVal[1] = matcher.group(2);

            if (returnVal[0] != null) {
                returnVal[0] = returnVal[0].substring(1,
                        returnVal[0].length() - 1);
            }

            if (returnVal[1] != null) {
                returnVal[1] = returnVal[1].trim();
            }
        }

        return returnVal;
    }

    @Override
    public String toString() {
        return "Comment[" + comment + "]";
    }

    static public void main(String[] args) {

        try {
            String commentStr = "/**\n* Here is a test for <plaintext>\n* hahah hahah < ahaha > ahaha\n*  aha \t \n*  </plaintext><plaintext></plaintext>\n * hoaoaoao\n * <plaintext> yeyeyey \n* hshshshs \t </plaintext> hahahahh uuuuu*/";

            Comment comment = new Comment(commentStr);

            System.out.println(comment.getHtmlifiedCommentBody());

            commentStr = "/**\n* Here is a test for <plaintext foo=bar tar klar=wahr>\n* hahah hahah < ahaha > ahaha\n*  aha \t \n*  </plaintext><plaintext></plaintext>\n * hoaoaoao\n * <plaintext> yeyeyey \n* hshshshs \t </plaintext> hahahahh uuuuu*/";

            comment = new Comment(commentStr);

            System.out.println(comment.getHtmlifiedCommentBody());

            commentStr = "/**\n* @return (Type) bla\n*/";

            comment = new Comment(commentStr);

            System.out.println(comment.getCommentBody());

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
