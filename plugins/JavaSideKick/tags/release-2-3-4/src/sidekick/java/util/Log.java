package sidekick.java.util;

import java.io.*;
import java.util.*;

/**
 * Simple logger for debugging, useful for web app components that don't have
 * access to the servlet container log.  All exceptions that could possibly be
 * caught are caught and silently ignored so as to not cause an application
 * failure.
 * <p>
 * The specific log file may be set by setting a System property named "LOG_FILE".
 * Of course, the file must be writable, or there will be a problem!  If this
 * property is not set, the log file will be written to $user.home/debug.txt.
 * <p>
 * The log file will always be appended to, never overwritten.
 *
 * @author Dale Anson
 */
public class Log {

    private static File logfile = System.getProperty("LOG_FILE") != null ?
            new File(System.getProperty("LOG_FILE")) :
            new File(System.getProperty("user.home"), "jsk_debug.txt");
    static { System.out.println(logfile); }

    /**
     * Write a message to the log file.
     *
     * @param msg  the message to write.
     */
    public static void log(String msg) {
        try {
            FileWriter fw = new FileWriter(logfile, true);
            fw.write(new Date().toString() + ": " + msg + "\n");
            fw.flush();
            fw.close();
        }
        catch (Exception e) {
        }
    }

    /**
     * Write an exception to the log file.  This will write out the full stack
     * trace, not just the message.
     *
     * @param e The exception to log.
     */
    public static void log(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log(sw.toString());
    }

    /**
     * Convenience method to log a StringBuffer to the log.
     *
     * @param sb The StringBuffer containing the message to write to the log.
     */
    public static void log(StringBuffer sb) {
        log(sb.toString());
    }
}

