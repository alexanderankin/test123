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
import java.util.logging.Level;
import java.io.*;
import java.text.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;


/**
 * Collection of useful static utility methods.
 *
 *
 * @since JDK 1.4
 */
public class Util extends Object {

    /**
     * Converts milliseconds to some more human readable representation.
     *
     * @param millis
     *            An amount of elapsed milliseconds
     * @return A human readable time string
     */
    static public final String millisToNice(long millis) {
        long seconds = millis / 1000;
        long sec = (seconds % 3600) % 60;
        long min = (seconds % 3600) / 60;
        long hour = seconds / 3600;
        StringBuffer strbuf = new StringBuffer(60);
        strbuf.append(" [ ");
        if (hour > 0L) {
            strbuf.append(hour + " h ");
        }
        if (min > 0L) {
            strbuf.append(min + " min ");
        }

        if (sec > 0L) {
            strbuf.append(sec + " sec ");
        }

        strbuf.append((millis % 1000) + " millis");
        strbuf.append(" ]");
        return strbuf.toString();
    }

    /**
     * Creates an array of strings from a string of comma-separated string
     * tokens.
     *
     * @param aString
     *            string containing tokens separated by ","
     * @return array containing string tokens, can be null if string empty
     */
    static public final String[] tokenizeCommaSepString(String aString) {
        if (aString == null) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(aString, ",");
        String[] result = null;

        int n = tokenizer.countTokens();

        if (n > 0) {
            result = new String[n];

            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                result[i] = tokenizer.nextToken().trim();
                i++;
            }
        }
        return result;
    }

    /**
     * Finds the common prefix of two specified strings.
     *
     * @param str1
     *            first string
     * @param str2
     *            second string
     * @return string which is a common prefix of the two strings, can be empty
     *         string, is never null
     */
    static public final String commonPrefix(String str1, String str2) {
        boolean done = false;
        StringBuffer buffer = new StringBuffer();

        int i = 0;
        int n1 = str1.length();
        int n2 = str2.length();

        while (!done) {
            char c = str1.charAt(i);

            if (c == str2.charAt(i)) {
                buffer.append(c);
                i++;
                if ((i == n1) || (i == n2)) {
                    done = true;
                }
            } else {
                done = true;
            }
        }

        return buffer.toString();
    }

    /**
     * Finds the common path prefix of two specified paths. Paths have to be
     * canonical
     *
     * @param path1
     *            first path
     * @param path2
     *            second path
     * @return string which is a non-empty common path prefix of the two paths,
     *         or null if they don't have a non-empty common prefix
     */
    static public final String commonPathPrefix(String path1, String path2) {
        boolean done = false;
        StringBuffer buffer = new StringBuffer();

        StringTokenizer st1 = new StringTokenizer(path1, File.separator, true);
        StringTokenizer st2 = new StringTokenizer(path2, File.separator, true);

        done = (!(st1.hasMoreTokens() && st2.hasMoreTokens()));

        while (!done) {
            String p1 = st1.nextToken();
            String p2 = st2.nextToken();

            if (p1.equals(p2)) {
                buffer.append(p1);
                done = (!(st1.hasMoreTokens() && st2.hasMoreTokens()));
            } else {
                done = true;
            }
        }

        return buffer.length() > 0 ? buffer.toString() : null;
    }

    static private final ByteBuffer copyBuffer = ByteBuffer
            .allocateDirect(16 * 1024);

    /**
     * Does a fast file copy from specified source to specified destination.
     * Uses nio API introduced in jdk 1.4.
     *
     * @param srcFilename
     *            file name of source file
     * @param dstFilename
     *            file name of copy
     * @exception IOException
     *                if an I/O error occurs
     */
    static public final void copyFile(String srcFilename, String dstFilename)
            throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel ifc = null;
        FileChannel ofc = null;

        Util.copyBuffer.clear();

        try {
            // Open the file and then get a channel from the stream
            fis = new FileInputStream(srcFilename);
            ifc = fis.getChannel();
            fos = new FileOutputStream(dstFilename);
            ofc = fos.getChannel();

            int sz = (int) ifc.size();

            int n = 0;
            while (n < sz) {
                if (ifc.read(Util.copyBuffer) < 0) {
                    break;
                }
                Util.copyBuffer.flip();
                n += ofc.write(Util.copyBuffer);
                Util.copyBuffer.compact();
            }

        } finally {
            try {
                if (ifc != null) {
                    ifc.close();
                } else if (fis != null) {
                    fis.close();
                }
            } catch (IOException exc) {
            }

            try {
                if (ofc != null) {
                    ofc.close();
                } else if (fos != null) {
                    fos.close();
                }
            } catch (IOException exc) {
            }
        }

        // FileInputStream fis = null;
        // FileOutputStream fos = null;
        // FileChannel ifc = null;
        // FileChannel ofc = null;

        // try {
        // fis = new FileInputStream(srcFilename);
        // ifc = fis.getChannel();
        // fos = new FileOutputStream(dstFilename);
        // ofc = fos.getChannel();

        // int sz = (int)ifc.size();
        // ifc.transferTo(0, sz, ofc);
        // } finally {
        // try {
        // if(ifc != null){
        // ifc.close();
        // } else if(fis != null){
        // fis.close();
        // }
        // } catch(IOException exc){
        // }

        // try {
        // if(ofc != null){
        // ofc.close();
        // } else if(fos != null){
        // fos.close();
        // }
        // } catch(IOException exc){
        // }
        // }
    }

    static private final DateFormat dateFormat = DateFormat
            .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    /**
     * Creates a string representing a date stamp of the current system date and
     * system time. Returned string is suitable to be used in a filename.
     *
     * @return string with date/time stamp
     */
    static public final String getDateStamp() {
        String dateStamp = dateFormat.format(new Date());
        dateStamp = dateStamp.replace(' ', '_');
        dateStamp = dateStamp.replace(',', '_');
        dateStamp = dateStamp.replace('/', '-');
        dateStamp = dateStamp.replace(':', '-');

        return dateStamp;
    }

    /**
     * Returns <code>true</code> if specified string is a valid identifier for
     * java or javascript.
     *
     * @param candidate
     *            potential identifier
     * @return <code>true</code> if it is in fact an identifier
     */
    static public final boolean isJavaIdentifier(String candidate) {
        if ((candidate == null) || (candidate.length() == 0)) {
            return false;
        }

        char c = candidate.charAt(0);

        if (!Character.isJavaIdentifierStart(c)) {
            return false;
        }

        int n = candidate.length();

        for (int i = 1; i < n; i++) {
            c = candidate.charAt(i);

            if (!Character.isJavaIdentifierPart(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns <code>true</code> if specified string is a valid composite
     * reference where each part is a valid identifier for java or javascript.
     *
     * @param candidate
     *            potential identifier
     * @return <code>true</code> if it is in fact a valid composite reference
     */
    static public final boolean isValidCompositeReference(String candidate) {
        if ((candidate == null) || (candidate.length() == 0)) {
            return false;
        }

        char c = candidate.charAt(0);

        if (!Character.isJavaIdentifierStart(c)) {
            return false;
        }

        int n = candidate.length();

        for (int i = 1; i < n; i++) {
            c = candidate.charAt(i);

            if (!(Character.isJavaIdentifierPart(c) || (c == '.'))) {
                return false;
            }
        }

        if (c == '.') {
            return false;
        }

        return true;
    }

    /**
     * Returns <code>true</code> if specified string is whitespace
     *
     * @param candidate
     *            potential whitespace
     * @return <code>true</code> if it is in fact whitespace
     */
    static public final boolean isWhitespace(String candidate) {
        int n = candidate.length();

        for (int i = 0; i < n; i++) {
            char c = candidate.charAt(i);

            if (!Character.isWhitespace(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns <code>true</code> if specified string is a mixed case string
     * with case mixing happening in the middle of the string (starting with
     * uppercase char and continuing with all lowercase chars does count).
     *
     * @param candidate
     *            potential mixed case
     * @return <code>true</code> if it is in fact mixed case
     */
    static public final boolean isLikelyIdentifier(String candidate) {
        int n = candidate.length();

        if (n < 3) {
            return false;
        }

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasUnderscore = false;
        int nUpper = Character.isUpperCase(candidate.charAt(0)) ? 1 : 0;

        for (int i = 1; i < n; i++) {
            char c = candidate.charAt(i);

            if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
                nUpper++;
            } else if (c == '_') {
                hasUnderscore = true;
            }
        }

        return (hasLower && hasUpper) || hasUnderscore
                || (nUpper == candidate.length());
    }

    /**
     * Replaces occurences of substring <code>sub</code> with string
     * <code>with</code> in specified string.
     *
     * @param s
     *            string for replacement
     * @param sub
     *            substring to replace
     * @param with
     *            substring to take its place
     * @return new string with replacements done
     */
    static public String replaceString(String s, String sub, String with) {
        int c = 0;
        int i = s.indexOf(sub, c);
        if (i == -1) {
            return s;
        }

        StringBuffer buf = new StringBuffer(s.length() + with.length());

        do {
            buf.append(s.substring(c, i));
            buf.append(with);
            c = i + sub.length();
        } while ((i = s.indexOf(sub, c)) != -1);

        if (c < s.length()) {
            buf.append(s.substring(c, s.length()));
        }

        return buf.toString();
    }

    /**
     * Replaces occurences of substring <code>sub</code> with string
     * <code>with</code> in specified string but only if substring is
     * delimited by non-alphanumeric characters.
     *
     * @param s
     *            string for replacement
     * @param sub
     *            substring to replace
     * @param with
     *            substring to take its place
     * @return new string with replacements done
     */
    static public String replaceSeparatedString(String s, String sub,
            String with) {
        int c = 0;
        int i = s.indexOf(sub, c);
        if (i == -1) {
            return s;
        }

        StringBuffer buf = new StringBuffer(s.length() + with.length());

        int n = s.length();

        do {
            buf.append(s.substring(c, i));
            int beginChar = i > 0 ? s.charAt(i - 1) : -1;
            int m = i + sub.length();
            int endChar = m < n ? s.charAt(m) : -1;

            if (((beginChar == -1) || (!Character
                    .isJavaIdentifierStart((char) beginChar)))
                    && ((endChar == -1) || (!Character
                            .isJavaIdentifierPart((char) endChar)))) {
                buf.append(with);
            } else {
                buf.append(sub);
            }
            c = i + sub.length();
        } while ((i = s.indexOf(sub, c)) != -1);

        if (c < s.length()) {
            buf.append(s.substring(c, s.length()));
        }

        return buf.toString();
    }

    /**
     * Tests if specified string ends with the specified suffix.
     *
     * @param aString
     *            string to test
     * @param aSuffix
     *            suffix.
     * @return <code>true</code> if the ends in suffix
     */
    static public boolean endsWith(String aString, String aSuffix) {
        if (aString == null) {
            return aSuffix == null;
        }

        if (aString.equals("")) {
            return (aSuffix != null) && aSuffix.equals("");
        }

        int index = aString.lastIndexOf(aSuffix);

        return index == aString.length() - aSuffix.length();
    }

    /**
     * Returns <code>true</code> if the specified filename has wildcard
     * characters in it, i.e. the name contains either "*" or "?" characters.
     *
     * @param filename
     *            filename
     * @return <code>true</code> if has wildcard characters
     */
    static public boolean hasWildcards(String filename) {
        return (filename.indexOf('*') != -1) || (filename.indexOf('?') != -1);
    }

    /**
     * Transforms a user wildcard into a java.util.regex.Pattern pattern string.
     *
     * @param wildCard
     *            wildcard string
     * @return pattern string
     */
    static public String wildCard2Pattern(String wildcard) {
        int n = wildcard.length();
        StringBuffer regexPatternBuffer = new StringBuffer(n);

        for (int i = 0; i < n; i++) {
            char c = wildcard.charAt(i);
            if (c == '*') {
                regexPatternBuffer.append(".*");
            } else if (c == '?') {
                regexPatternBuffer.append('.');
            } else if (c == '.') {
                regexPatternBuffer.append("\\.");
            } else if (c == '$') {
                regexPatternBuffer.append("\\$");
            } else {
                regexPatternBuffer.append(c);
            }
        }

        return regexPatternBuffer.toString();
    }

    /**
     * Resolves the specified url path to a file on the local file system. Uses
     * the specified web root and web map aliases to complete the resolution.
     *
     * @param urlPath
     *            a url path
     * @param webroot
     *            directory path on local file system of the web root
     * @param webmaps
     *            web map aliases
     * @return a local File instance
     */
    static public File resolveWebURL(String urlPath, String webroot, Map webmaps) {
        File result = null;

        Iterator iter = webmaps.keySet().iterator();

        while (iter.hasNext()) {
            String key = (String) iter.next();

            if (urlPath.startsWith(key)) {
                result = new File(((String) webmaps.get(key))
                        + urlPath.substring(key.length()));
                break;
            }
        }

        if (result == null) {
            result = new File(webroot + urlPath);
        }

        return result;
    }

    /**
     * Reads an input stream completely into memory and returns a CharBuffer
     * instance with the contents.
     *
     * @param inputStream
     *            an input stream
     * @param decoder
     *            charset decoder
     * @return CharBuffer instance with the contents
     * @exception IOException
     *                if reading from inputStream throws IOException
     */
    static public CharBuffer readBytes(InputStream inputStream,
            CharsetDecoder decoder) throws IOException {
        byte[] buffer = new byte[1024];
        int b = inputStream.read();
        int i = 0;

        while (b != -1) {
            if (i == buffer.length) {
                byte[] grow = new byte[buffer.length * 2];
                System.arraycopy(buffer, 0, grow, 0, buffer.length);
                buffer = grow;
            }
            buffer[i++] = (byte) b;
            b = inputStream.read();
        }

        return decoder.decode(ByteBuffer.wrap(buffer, 0, i));
    }

    /**
     * Escapes a plain text string for html. Doesn't look into those funny
     * characters like euro symbol, copyright symbol etc.
     *
     * @param plainString
     *            plain text string
     * @return html-escaped string
     */
    static public String escape2Html(String plainString) {
        if (plainString == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        int n = plainString.length();

        int spaceState = 0;
        int nlState = 0;

        for (int i = 0; i < n; i++) {
            char c = plainString.charAt(i);

            if (c == ' ') {
                if (nlState > 0) {
                    if (nlState == 1) {
                        sb.append('\n');
                    } else {
                        sb.append("<p>");
                    }
                }
                nlState = 0;
                if (spaceState == 0) {
                    spaceState = 1;
                    sb.append(c);
                } else if (spaceState == 1) {
                    spaceState = 0;
                    sb.append("&nbsp;");
                }
            } else if (c == '\n') {
                if (nlState == 0) {
                    nlState = 1;
                } else if (nlState == 1) {
                    nlState = 2;
                }
            } else {
                if (nlState > 0) {
                    if (nlState == 1) {
                        sb.append('\n');
                    } else {
                        sb.append("<p>");
                    }
                }
                nlState = 0;
                spaceState = 0;
                switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#039;");
                    break;
                case '\n':
                    sb.append("<br>");
                    break;
                default:
                    sb.append(c);
                    break;
                }
            }
        }

        return sb.toString();
    }
}
