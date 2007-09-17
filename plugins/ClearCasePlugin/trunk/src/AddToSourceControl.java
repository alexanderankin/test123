public class AddToSourceControl extends ClearToolCommand
{
	boolean checkOutParentDirectory;				
    boolean keepCheckedOut;
	String comment;
	
/*
    public AddToSourceControl(String file, String args[])
    {
        super(COMMAND_CHECKOUT);
        setOptions(args);
        // " needed if path contains spaces. Will not work on all platforms?
        setTarget("\"" + file + "\"");
    }
*/
    /**
    *   Adds a file to source control. 
    *   TODO comment adding
    *   @param file The path to the file.
    *   @param keepCheckedOut If true do not check file in.
    */
    public AddToSourceControl(String file, boolean keepCheckedOut, boolean checkOutParentDirectory, String comment)
    {
        super(COMMAND_MKELEM);
        
        this.keepCheckedOut = keepCheckedOut;
		this.checkOutParentDirectory = checkOutParentDirectory;
        this.comment = comment;
		
        if(keepCheckedOut)
        {
            setOption(0, OPTION_CHECK_OUT);
        }
        else
        {
            setOption(0, OPTION_CHECK_IN);
        }
        
        if(comment == null)
        {
            setOption(getOptionsCount(), OPTION_NO_COMMENT);
        }
        else
        {
            setOption(getOptionsCount(), OPTION_COMMENT);
            setOption(getOptionsCount(), "\"" + comment + "\"");
        }

        setTarget("\"" + file + "\"");
    }
    
    public String getComment()
	{
		return comment;
	}
	
	public boolean getKeepCheckedOut()
    {
        return keepCheckedOut;
    }
    
    public boolean getCheckOutParentDirectory()
    {
        return checkOutParentDirectory;
    }
    
}
