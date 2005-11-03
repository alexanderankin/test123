package logviewer;

import java.util.regex.*;

public class StringUtils {
    /**
     * Borrowed from Ant?
     * @param b the string to check
     * @return   false on these conditions: b is null or b starts with "n" or
     *      "f" or equals "off", regardless of case, true for any other input.
     */
    public static boolean stringToBoolean(String b) {
        if (b == null)
            return false;
        String t = b.toLowerCase();
        if (t.startsWith("n") || t.startsWith("f") || t.equals("off"))
            return false;
        return true;
    }
    
	/**
	 * Like the String.lastIndexOf, but uses a regular expression instead
	 * of an explicit string.
	 * @param s the string to search in
	 * @param regex what to find
	 * @return the offset to the start of the last occurrance of regex in s
	 */
    public static int lastIndexOf(String s, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(s);
        int offset = -1;
        while(m.find())
            offset = m.start();
        return offset;
    }
    
    public static boolean matches(String s, String regex) {
        if (s == null)
            return false;
        if (regex == null)
            throw new IllegalArgumentException("regex not allowed to be null");
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(s);
        return m.matches();
    }
}
