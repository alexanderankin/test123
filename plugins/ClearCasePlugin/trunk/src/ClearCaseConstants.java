/**
*   TODO how to support different versions of ClearCase?
*/
public interface ClearCaseConstants
{

    // =========================================
    // ClearCase commands
    // =========================================
    /**
    */
    String COMMAND_CLEARTOOL = "cleartool";
    /**
     */
    String COMMAND_CLEARFINDCO = "clearfindco";
    /**
     */
    String COMMAND_SHOW_DETAILS = "cleardetails";
    /**
     */
    String COMMAND_MERGE = "clearmrgman";

	String COMMAND_COMPARE_PREVIOUS_VERSION = "diff -pred";
	
    // =========================================
    // ClearTool commands
    // =========================================
    /** 
    */
    String COMMAND_UNCHECKOUT = "uncheckout";
    /**
     */
    String COMMAND_UPDATE = "update";
    /**
     */
    String COMMAND_CHECKOUT = "checkout";
    /**
     */
    String COMMAND_CHECKIN = "checkin";
    /**
     */
    String COMMAND_LSHISTORY = "lshistory";
    /**
     */
    String COMMAND_LSVTREE = "lsvtree";
    /**
     */
    String COMMAND_MKELEM = "mkelem";

    // =========================================
    // ClearTool command options
    // =========================================
    /**
     */
    String OPTION_KEEP_FILE = "-keep";
    /**
     */
    String OPTION_COMMENT = "-comment";
    /**
     */
    String OPTION_NO_COMMENT = "-nc";
    /**
     */
    String OPTION_CHECK_IN = "-ci";
    /**
     */
    String OPTION_CHECK_OUT = "-nco";
    /**
     */
    String OPTION_RESERVED = "-reserved";
    /**
     */
    String OPTION_UNRESERVED = "-unreserved";
    /**
     */
    String OPTION_DELETE_FILE = "-rm";
    /**
     */
    String OPTION_GRAPHICAL = "-graphical";

	int FILE_RELOAD_DELAY = 3;
}
