/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.utilities;

import java.util.HashMap;

/**
 *
 * @author Sune Simonsen
 */
public class Log {
    public enum Level {
        MESSAGE,
        DEBUG,
        NOTICE,
        WARNING,
        ERROR
    }
    
    private final static HashMap<Level, Integer> levelConversion = 
            new HashMap<Level, Integer>();
    
    static {
        levelConversion.put(Log.Level.MESSAGE, org.gjt.sp.util.Log.MESSAGE);
        levelConversion.put(Log.Level.DEBUG, org.gjt.sp.util.Log.DEBUG);
        levelConversion.put(Log.Level.NOTICE, org.gjt.sp.util.Log.NOTICE);
        levelConversion.put(Log.Level.WARNING, org.gjt.sp.util.Log.WARNING);
        levelConversion.put(Log.Level.ERROR, org.gjt.sp.util.Log.ERROR);
    }
    
    public static void log(Log.Level level, Class clazz, String message) {
        org.gjt.sp.util.Log.log(
                levelConversion.get(level), 
                clazz.getPackage() + "." + clazz.getName(), 
                message);
    }
    
    public static void log(Log.Level level, Class clazz, Exception ex) {
        org.gjt.sp.util.Log.log(
                levelConversion.get(level), 
                clazz.getPackage() + "." + clazz.getName(), 
                ex);
    }
}
