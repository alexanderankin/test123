public class CheckOut extends ClearToolCommand
{
    
    boolean reserved;
/*
    public CheckOut(String file, String args[])
    {
        super(COMMAND_CHECKOUT);
        setOptions(args);
        // " needed if path contains spaces. Will not work on all platforms?
        setTarget("\"" + file + "\"");
    }
*/
    /**
    *   Checks out a file. 
    *   TODO how to add comment and get comment at checkin?
    *   @param file The path to the file.
    *   @param reserved If true checkout is reserved.
    */
    public CheckOut(String file, boolean reserved, String comment)
    {
        super(COMMAND_CHECKOUT);
        
        this.reserved = reserved;
        
        if(comment == null)
        {
            setOption(getOptionsCount(), OPTION_NO_COMMENT);
        }
        else
        {
            setOption(getOptionsCount(), OPTION_COMMENT);
            setOption(getOptionsCount(), "\"" + comment + "\"");
        }

        if(reserved)
        {
            setOption(getOptionsCount(), OPTION_RESERVED);
        }
        else
        {
            setOption(getOptionsCount(), OPTION_UNRESERVED);
        }
        
        setTarget("\"" + file + "\"");
    }
    
    public boolean getReserved()
    {
        return reserved;
    }
    
}
