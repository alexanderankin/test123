public class UnCheckOut extends ClearToolCommand
{
    boolean keepFile;
/* 
    public UnCheckOut(String file, String args[])
    {
        super(COMMAND_UNCHECKOUT);
        setOptions(args);
        // " needed if path contains spaces. Will not work on all platforms?
        setTarget("\"" + file + "\"");
    }
*/
    /** 
    *
    */
    public UnCheckOut(String file, boolean keepFile)
    {
        super(COMMAND_UNCHECKOUT);
        
        this.keepFile = keepFile;
        
        if(keepFile)
        {
            setOption(getOptionsCount(), OPTION_KEEP_FILE);
        }
        else
        {
            setOption(getOptionsCount(), OPTION_DELETE_FILE);
        }
        setTarget("\"" + file + "\"");
    }
    
    /**
    */
    public boolean setKeepFile()
    {
        return keepFile;
    }
    
}