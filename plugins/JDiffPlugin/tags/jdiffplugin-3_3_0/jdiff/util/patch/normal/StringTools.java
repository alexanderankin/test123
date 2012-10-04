package jdiff.util.patch.normal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Tools working on Strings (d'oh)
 *
 * <pre>
 *          Copyright (c) 2006 Sebastian Erdweg
 *          Copyright (c) 2007 Dominik Schulz
 *          Copyright (c) 2006 Florian Lindner
 *          Copyright (c) 2006 Betim Berjani
 *
 *          This file is part of FlexiCrypt.
 *
 *          FlexiCrypt is free software; you can redistribute it and/or modify
 *          it under the terms of the GNU General Public License as published by
 *          the Free Software Foundation; either version 2 of the License, or
 *          (at your option) any later version.
 *
 *          FlexiCrypt is distributed in the hope that it will be useful,
 *          but WITHOUT ANY WARRANTY; without even the implied warranty of
 *          MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *          GNU General Public License for more details.
 *
 *          You should have received a copy of the GNU General Public License
 *          along with FlexiCrypt; if not, write to the Free Software
 *          Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * </pre>
 *
 * @author Sebastian
 * @author Dominik
 */
public class StringTools {

    /**
     *
     * @param arr
     *            the array to convert to a string line by line
     * @return the string from the array, every line sperated by \n
     */
    public static String arrayToString(Object[] arr) {
        return arrayToString(arr, "\n");
    }

    /**
     * @param arr
     *            the array to convert to a string line by line
     * @param delim
     *            the delimiter between the lines
     * @return the string from the array, every line sperated by delim
     */
    public static String arrayToString(Object[] arr, String delim) {
    	if (arr.length == 0)
    		return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i].toString()).append(delim);
        }
        //return sb.toString(); // TODO maybe this should be used instead
        return sb.toString().substring(0, (sb.toString().length()-delim.length())); // remove the last delim
    }

    public static String stringToStringBuilderCode(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("StringBuilder sb = new StringBuilder();\n");
        String[] lines = stringToArray(str, "\n");
        for (int i = 0; i < lines.length; i++) {
            sb.append("sb.append(\""+lines[i]+"\\n\");\n");
        }
        return sb.toString();
    }

    /**
     * Change the frist letter of each word in the string to upper case.
     * @param string the string to change
     * @return the changed string
     */
    public static String firstLetterUpperCaseOfEachWord(String string) {
        String[] nameParts = string.split(" ");
        for (int i = 0; i < nameParts.length; i++) {
            nameParts[i] = (nameParts[i].charAt(0) + "").toUpperCase()
                    + nameParts[i].substring(1, nameParts[i].length());
            if (i < nameParts.length - 1) {
                nameParts[i] += " ";
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nameParts.length; i++) {
            sb.append(nameParts[i]);
        }
        return sb.toString();
    }

    /**
     * Get a formated String of bytes with a given maximum length.
     * @param b
     * @param type
     * @param maxLength
     * @return the formated string
     */
    public static String getFormatedStringOfBytes(byte[] b, String type, int maxLength) {
        String formatedString = getFormatedStringOfBytes(b, type);
        if (formatedString.length() > maxLength) {
            formatedString = formatedString.substring(0, maxLength);
        }
        return formatedString;
    }

    /**
     * Get a formated string of bytes. Useful for debugging.
     * @param b the byte array
     * @param type box, dotted or other
     * @return the formated string
     */
    public static String getFormatedStringOfBytes(byte[] b, String type) {
        if (type.equals("box")) // "box"-Ansicht wie in der MessageDigest box
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                if ((i > 0) && (i % 8 == 0)) {
                    sb.append('\n' + StringTools.getStringOfByte(b[i]));
                } else if (i == 0) {
                    sb.append(StringTools.getStringOfByte(b[i]));
                } else {
                    sb.append(' ' + StringTools.getStringOfByte(b[i]));
                }
            }
            return sb.toString();
        } else if (type.equals("dotted")) { // "dotted"-Darstellung wie im
            // Zertifikats-Viewer
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < b.length - 1; i++) {
                sb.append(StringTools.getStringOfByte(b[i]) + ":");
            }
            if (b.length > 0)
                sb.append(StringTools.getStringOfByte(b[b.length - 1]));
            // nach dem letzten eintrag kein doppelpunkt mehr
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                sb.append(StringTools.getStringOfByte(b[i]));
            }
            return sb.toString();
        }
    }

    /**
     * @param b a byte
     * @return the corresponding string
     */
    public static String getStringOfByte(byte b) {
        int c = b;
        if (c < 0) {
            c = c + 256;
        }
        String s = Integer.toHexString(c).toUpperCase();

        if (s.length() == 1) {
            s = '0' + s;
        }

        return s;
    }

    /**
     *
     * @param text -
     *            The String that will be broken up into lines of n chars
     * @param breakAfter -
     *            The max. number of chars per line
     * @return the broken string
     */
    public static String lineBreakAfterNchars(String text, int breakAfter) {
        breakAfter--;
        int startMark = 0;
        int endMark = breakAfter;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (i % breakAfter == 0) {
                if ((text.length() - endMark) < 0) {
                    endMark = text.length();
                    sb.append(text.substring(startMark, endMark));
                } else {
                    sb.append(text.substring(startMark, endMark) + "\n");
                }
                startMark = endMark;
                endMark = endMark + breakAfter;
            }
        }
        return sb.toString();
    }

    /**
     * Read a file into a String
     * @param file the file to read from
     * @return the content of the file
     * @throws IOException
     */
    public static String readFileIntoString(File file) throws IOException {
        BufferedReader bw = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bw.readLine()) != null) {
            sb.append("\n" + line);
        }
        bw.close();
        return sb.toString().substring(1);
    }

    /**
     * Almost the same a readFileIntoString with the difference that the input is split a
     * newlines and put into an array.
     * @param filename
     * @return the content of the file
     */
    public static String[] readFileIntoStringArray(String filename) {
        try {
            return stringToArray(readFileIntoString(new File(filename)));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Convert a string to an string array. Split at newlines (\n).
     * @param msg string to split up
     * @return the string array
     */
    public static String[] stringToArray(String msg) {
        return stringToArray(msg, "\n");
    }

    /**
     * Convert a string to an string array. Split at arbitrary delimiters.
     * @param msg string to split up
     * @param delim the delimiter, e.g. "\n" or "\r\n" or maybe " ".
     * @return the string array
     */
    public static String[] stringToArray(String msg, String delim) {
        ArrayList<String> al = stringToArrayList(msg, delim);
        return al.toArray(new String[al.size()]);
    }

    /**
     * Convert a string to an ArrayList<String>. Split at "\n".
     * @param msg string to split up
     * @return the ArrayList<String>
     */
    public static ArrayList<String> stringToArrayList(String msg) {
        return stringToArrayList(msg, "\n");
    }

    /**
     * Splits a String at the given delimiters and puts the result into an ArrayList<String>
     *
     * @param msg string to split up
     * @param delim the delimiter, e.g. "\n" or "\r\n" or maybe " ".
     * @return the ArrayList<String>
     */
    public static ArrayList<String> stringToArrayList(String msg, String delim) {
        ArrayList<String> al = new ArrayList<String>();
        //System.out.println("stringToArrayList(msg: " + msg + ", delim: " + delim + ") - Processing ...");
        StringTokenizer tokens = new StringTokenizer(msg, delim, true);
        boolean lastTokenEmpty = false;
        while (tokens.hasMoreTokens()) {
            String currentToken = tokens.nextToken();
            if(currentToken.equals(delim) && lastTokenEmpty == true) {
                //System.out.println("stringToArrayList() - adding token: empty");
                al.add("");
                lastTokenEmpty = true;
            } else if(currentToken.equals(delim) && lastTokenEmpty == false) {
                // skip this one
                //System.out.println("stringToArrayList() - skipping token");
                lastTokenEmpty = true;
            } else {
                //System.out.println("stringToArrayList() - adding token: " + currentToken);
                al.add(currentToken);
                lastTokenEmpty = false;
            }
        }
        //System.out.println("stringToArrayList() - size of final array: " + al.size());
        //System.out.println("stringToArrayList() - final string: " + arrayToString(al.toArray(new String[1])));
        return al;
    }

    /**
     * Write a string into a file.
     * @param text the string to write to the file.
     * @param file the output file.
     * @throws IOException if an I/O error ocurs.
     */
    public static void writeStringIntoFile(String text, File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(text);
        bw.close();
    }

    /**
     * Remove every line-break from the string
     * @param text w/ or w/o line breaks
     * @return the string w/o line breaks.
     */
    public static String removeLineBreaks(String text) {
        String replace = text.replace("\n", " ");
        replace = replace.replace("\r\n", " ");
        replace = replace.replace("\n\r", " ");
        replace = replace.replace("\r", " ");
        return replace;
    }

    /**
     * Remove every line-break from the string. Cut at the given maxLength.
     * @param text w/ or w/o line breaks
     * @param maxLength the maximum length of the returned string
     * @return the string w/o line breaks.
     */
    public static String removeLineBreaks(String text, int maxLength) {
        String ret = removeLineBreaks(text);
        if (ret.length() > maxLength) {
            ret = ret.substring(0, maxLength);
        }
        return ret;
    }

    /**
     * This class provides only static methods. No need to instanciate.
     *
     */
    private StringTools() {

    }

    public static void main(String args[]) {
        /*
        File file = new File("/some/input/file.txt");
        try {
            System.out.println(stringToStringBuilderCode(readFileIntoString(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}
