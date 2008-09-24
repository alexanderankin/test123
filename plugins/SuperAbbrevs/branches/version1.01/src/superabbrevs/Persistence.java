package superabbrevs;
import com.thoughtworks.xstream.XStream;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import superabbrevs.utilities.Log;
import superabbrevs.model.*;


public class Persistence {
    
    public static void saveMode(Mode mode) 
    throws IOException {
        Log.log(Log.Level.DEBUG, Persistence.class, 
                "Saving mode: "+mode.getName());
        
        File modeFile = new File(Paths.getModeAbbrevsFile(mode.getName()));
        if (mode.getAbbreviations().isEmpty()) {
            Log.log(Log.Level.DEBUG, Persistence.class, 
                "There are no abbrevations defined for the "+mode.getName() + 
                " mode");
            // Remove the abbreviation file
            if (modeFile.exists()) {
                Log.log(Log.Level.DEBUG, Persistence.class, 
                        "Deleting mode file " + modeFile);
                modeFile.delete();
            }
        } else {
            Log.log(Log.Level.DEBUG, Persistence.class, 
                        "Saving mode file " + modeFile);
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
        String modeFileName = Paths.getModeAbbrevsFile(mode);
        if (new File(modeFileName).exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(modeFileName);
                XStream xstream = new XStream();
                xstream.setMode(XStream.NO_REFERENCES);
                setupFormating(xstream);
                Mode result = (Mode) xstream.fromXML(in);
                return result;
            } catch (FileNotFoundException ex) {
                Log.log(Log.Level.ERROR,Persistence.class, ex);
            } finally {
                try {
                    if (in != null) in.close();
                } catch (IOException ex) {
                    Log.log(Log.Level.ERROR,Persistence.class, ex);
                }
            }    
        }
        
        // If the mode file does not exist return a empty abbreviation list
        return new Mode(mode);
    }

    private static void setupFormating(XStream xstream) {
        xstream.alias("mode", Mode.class);
        xstream.alias("abbreviation", Abbrev.class);
        xstream.alias("Variable", Variable.class);
    }
}
