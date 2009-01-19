package superabbrevs;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import superabbrevs.model.Abbrev;
import superabbrevs.model.Mode;
import superabbrevs.model.Variable;
import superabbrevs.utilities.Log;

import com.thoughtworks.xstream.XStream;


public class Persistence {
    
    public void saveMode(Mode mode) 
    throws IOException {
        Log.log(Log.Level.DEBUG, Persistence.class, 
                "Saving mode: "+mode.getName());
        
        File modeFile = getModeFileName(mode.getName());
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
            FileOutputStream out = null;
            try {
				out = new FileOutputStream(modeFile);
	            XStream xstream = new XStream();
	            xstream.setMode(XStream.NO_REFERENCES);
	            setupFormating(xstream);
	            xstream.toXML(mode, out);   
            } finally {
            	out.close();            	
            }
        }
    }
    
    public Mode loadMode(String mode) {
        File modeFile = getModeFileName(mode);
        if (modeFile.exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(modeFile);
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

	protected File getModeFileName(String mode) {
		return new File(Paths.getModeAbbrevsFile(mode));
	}

    private static void setupFormating(XStream xstream) {
        xstream.alias("mode", Mode.class);
        xstream.alias("abbreviation", Abbrev.class);
        xstream.alias("Variable", Variable.class);
    }
}
