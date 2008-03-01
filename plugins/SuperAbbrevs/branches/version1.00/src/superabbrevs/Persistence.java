package superabbrevs;
import com.thoughtworks.xstream.XStream;
import java.util.*;
import java.io.*;
import java.net.*;
import superabbrevs.model.*;


public class Persistence {
    
    public static void saveMode(Mode mode) 
    throws IOException {
        File modeFile = new File(mode.getName()+".xml");
        if (mode.getAbbreviations().isEmpty()) {
            // Remove the abbreviation file
            if (modeFile.exists()) {
                modeFile.delete();
            }
        } else {
            modeFile.createNewFile();        
            FileOutputStream out = new FileOutputStream(
                    Paths.getModeAbbrevsFile(mode.getName()));
            XStream xstream = new XStream();
            xstream.setMode(XStream.NO_REFERENCES);
            setupFormating(xstream);
            xstream.toXML(mode, out);   
            out.close();
        }
    }
    
    public static Mode loadMode(String mode) {
        try {
            FileInputStream in = new FileInputStream(
                    Paths.getModeAbbrevsFile(mode));
            XStream xstream = new XStream();
            xstream.setMode(XStream.NO_REFERENCES);
            setupFormating(xstream);
            Mode result = (Mode)xstream.fromXML(in);
            in.close();
            return result;
        } catch (Exception ex) {
            // If the mode file does not exist return a empty abbreviation list
            return new Mode(mode);
        }
    }

    private static void setupFormating(XStream xstream) {
        xstream.alias("mode", Mode.class);
        xstream.alias("abbreviation", Abbrev.class);
        xstream.alias("dynamicVariable", DynamicVariable.class);
        xstream.alias("stringVariable", StringVariable.class);
    }
}
