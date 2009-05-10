package superabbrevs;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gjt.sp.util.IOUtilities;

import superabbrevs.model.Abbreviation;
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
        FileInputStream in = null;
        if (modeFile.exists() && modeFile.length() != 0) {
        	try {
                in = new FileInputStream(modeFile);
                XStream xstream = new XStream();
                xstream.setMode(XStream.NO_REFERENCES);
                setupFormating(xstream);
                Mode result = (Mode) xstream.fromXML(in);
                return result;
            } catch (FileNotFoundException ex) {
                // If the mode file does not exist return a empty abbreviation list
                return new Mode(mode);
            } finally {
            	IOUtilities.closeQuietly(in);
            }
		}
        
        return new Mode(mode);
    }

	protected File getModeFileName(String mode) {
		return new File(Paths.getModeAbbrevsFile(mode));
	}

    private static void setupFormating(XStream xstream) {
        xstream.alias("mode", Mode.class);
        xstream.alias("abbreviation", Abbreviation.class);
        xstream.alias("variable", Variable.class);
    }
}
