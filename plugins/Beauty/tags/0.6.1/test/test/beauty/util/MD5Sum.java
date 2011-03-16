package beauty.util;

import java.io.*;
import java.security.MessageDigest;

// calculates an MD5 sum
public class MD5Sum {
    static final byte[] HEX_CHARS = { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f' } ; 
 
    /**
     * Calculate the MD5 sum of an input stream.  The stream will be closed when
     * this method returns.
     * @param an input stream
     * @return MD5 sum of the given stream
     */ 
    public static String sum(InputStream stream) throws Exception {
        byte[] buffer = new byte[1024];
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = stream.read(buffer);
            if (numRead > 0) {
                messageDigest.update(buffer, 0, numRead);
            }
        } while (numRead != - 1);
        stream.close();
        return toHex(messageDigest.digest());
    }
 
    /**
     * Calculates the MD5 sum of the contents of the given file.
     * @param file The file to find the sum for.
     * @return MD5 sum of the contents of the given file.
     */ 
    public static String sum(File file) throws Exception {
        return sum(new FileInputStream(file));
    }
 
    /**
     * Calculates the MD5 sum of the contents of the given string.
     * @param string The string to find the sum for.
     * @return MD5 sum of the contents of the given string.
     */ 
    public static String sum(String string) throws Exception {
        return sum(new ByteArrayInputStream(string.getBytes()));
    }
 
    /**
     * Converts the given byte array to a hex string.
     * @param raw The byte array to convert.
     * @return The byte array represented as a string.
     */ 
    public static String toHex(byte[] raw) throws UnsupportedEncodingException {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int c = b & 0xFF;
            hex[index++] = HEX_CHARS[ c >>> 4];
            hex[index++] = HEX_CHARS[ c & 0xF];
        }
        return new String(hex, "ASCII");
    }
}