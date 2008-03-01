package superabbrevs;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;

public class Paths {
    
    public static final String ABBREVS_DIR =
            EditPlugin.getPluginHome(SuperAbbrevsPlugin.class).getPath();

    public static final String OLD_ABBREVS_DIR = 
            MiscUtilities.constructPath(jEdit.getSettingsDirectory(), 
            "SuperAbbrevs");
    
    public static final String MACRO_DIR =
            MiscUtilities.constructPath(MiscUtilities.constructPath(
            jEdit.getSettingsDirectory(), "macros"), "SuperAbbrevs");
    
    public static final String RESOURCE_DIR = "resources";
    
    public static final String ABBREVS_FUNCTION_FILE = "UserFunctions.bsh";
    public static final String TEMPLATE_GENERATION_FUNCTION_FILE =
            "StandardLibrary.bsh";
    public static final String VARIABLES_FILE = "global.variables";
    
    public static final String TAB_MACRO = "tab.bsh";
    public static final String SHIFT_TAB_MACRO = "shift-tab.bsh";
                
    public static String ABBREVS_FUNCTION_PATH = 
            MiscUtilities.constructPath(ABBREVS_DIR, ABBREVS_FUNCTION_FILE);
    
    public static String TEMPLATE_GENERATION_FUNCTION_PATH = 
            MiscUtilities.constructPath(ABBREVS_DIR, 
            TEMPLATE_GENERATION_FUNCTION_FILE);
    
    static String getModeAbbrevsFile(String mode) {
        return MiscUtilities.constructPath(ABBREVS_DIR, mode+".xml");
    }
}
