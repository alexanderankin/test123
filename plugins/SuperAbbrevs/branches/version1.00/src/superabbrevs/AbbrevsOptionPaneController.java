package superabbrevs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.StandardUtilities;
import superabbrevs.model.Abbrev;
import superabbrevs.model.Mode;

/**
 * @author sune
 * Created on 28. januar 2007, 00:35
 *
 */
public class AbbrevsOptionPaneController {
    
    private String modeName;
    private int modeIndex;
    
    Hashtable<String,Mode> modes = new Hashtable<String, Mode>();
    
    /**
     * Creates a new instance of AbbrevsOptionPaneController
     */
    public AbbrevsOptionPaneController(String modeName) {
        this.modeName = modeName;
    }

    public String[] getModes() {
        org.gjt.sp.jedit.Mode[] jEditModes = jEdit.getModes();
        String[] modeNames = new String[jEditModes.length+1];
        
        modeNames[0] = "global";
        
        for (int i = 0; i < jEditModes.length; i++) {
            modeNames[i+1] = jEditModes[i].getName(); 
        }
        
        Arrays.sort(modeNames,1,jEditModes.length-1,
                String.CASE_INSENSITIVE_ORDER);
        
        for (int i = 1; i < jEditModes.length; i++) {
            if (modeNames[i].equals(this.modeName)) {
                // We found the selected index
                modeIndex = i;
                break;
            }
        }
        
        return modeNames;
    }
    
    public int getIndexOfCurrentMode() {
        return modeIndex;
    }
    
    public ArrayList<Abbrev> loadsAbbrevs(String modeName) {
        if (modes.containsKey(modeName)) {
            return modes.get(modeName).getAbbreviations();
        } else {
            Mode mode = Persistence.loadMode(modeName);
            modes.put(modeName, mode);
            return mode.getAbbreviations();
        }
    }

    public void saveAbbrevs() throws IOException {
        for(Mode mode : modes.values()) {
            AbbrevsHandler.invalidateMode(mode.getName());
            Persistence.saveMode(mode);  
        }
    }
}
