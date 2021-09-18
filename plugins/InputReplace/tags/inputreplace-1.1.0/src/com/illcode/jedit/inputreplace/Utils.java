package com.illcode.jedit.inputreplace;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class Utils
{
    /**
     * Read all characters available from a Reader and return them as a string.
     * We use a BufferedReader internally to make the process more efficient.
     * @param r Reader from which to read characters
     * @param bufferSize buffer size for our BufferedReader
     * @return String read from the reader, or null if an exception occurred
     */
    public static String slurpReaderText(Reader r, int bufferSize) {
        String s = null;
        try (BufferedReader reader = new BufferedReader(r)) {
            char [] buffer = new char[bufferSize];
            StringBuilder sb = new StringBuilder(5*bufferSize);
            int n;
            while ((n = reader.read(buffer, 0, buffer.length)) != -1)
                sb.append(buffer, 0, n);
            s = sb.toString();
        } catch (IOException ex) {
            s = null;
        }
        return s;
    }

    /**
     * Loads a ClassLoader text resource and returns its contents as a String, assuming a UTF-8 encoding.
     * @param resourcePath class-loader resource path, relative to {@code Utils.class}
     * @return String value of resource, or an empty string on error
     */
    public static String getStringResource(String resourcePath) {
        String val = null;
        InputStream in = Utils.class.getResourceAsStream(resourcePath);
        if (in != null)
            val = Utils.slurpReaderText(new InputStreamReader(in, StandardCharsets.UTF_8), 512);
        return val != null ? val : "";
    }

}
