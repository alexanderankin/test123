
package net.sourceforge.jedit.javainsight;

import java.util.zip.*;
import java.util.Enumeration;
import net.sourceforge.jedit.buildtools.StaticLogger;

public class Test {


    public Test() {
        
        try {
    
            ZipFile zip = new ZipFile( "/usr/local/jdk118/lib/classes.zip" );

            Enumeration elements = zip.entries();

            int count = 0;
            while( elements.hasMoreElements() ) {
                
                ZipEntry entry = (ZipEntry)elements.nextElement();
                StaticLogger.log( entry.getName() );
                ++count;
            }
            StaticLogger.log("found " + count + " entries");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
            
    }
    
    
    public static void main(String[] args) {
        new Test();
    }
    
    
}
