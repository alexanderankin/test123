package superabbrevs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import superabbrevs.Abbrev;
import superabbrevs.Persistence;

/**
 * @author sune
 * Created on 28. januar 2007, 00:35
 *
 */
public class AbbrevsOptionPaneController {
    
    private String mode;
    private int modeIndex;
    
    /**
     * Creates a new instance of AbbrevsOptionPaneController
     */
    public AbbrevsOptionPaneController(String mode) {
        this.mode = mode;
    }

    public String[] getModes() {
        Mode[] modes = jEdit.getModes();
        String[] modeNames = new String[modes.length+1];
        
        modeNames[0] = "global";
        
        for (int i = 0; i < modes.length; i++) {
            modeNames[i+1] = modes[i].getName(); 
        }
        
        Arrays.sort(modeNames,1,modes.length-1,
                new MiscUtilities.StringICaseCompare());
        
        for (int i = 1; i < modes.length; i++) {
            if (modeNames[i].equals(this.mode)) {
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
    
    public ArrayList<Abbrev> loadsAbbrevs(String mode) {
        if (abbrevs.containsKey(mode)) {
            return abbrevs.get(mode);
        } else {
            ArrayList<Abbrev> modeAbbrevs = Persistence.loadAbbrevs(mode);
            abbrevs.put(mode, modeAbbrevs);
            return modeAbbrevs;
        }
    }

    public void saveAbbrevs() throws IOException {
        for(Entry<String, ArrayList<Abbrev>> e : abbrevs.entrySet()) {
            Persistence.saveAbbrevs(e.getKey(),e.getValue());  
        }
    }
    
    TreeMap<String,ArrayList<Abbrev>> abbrevs = 
            new TreeMap<String,ArrayList<Abbrev>>();
}
