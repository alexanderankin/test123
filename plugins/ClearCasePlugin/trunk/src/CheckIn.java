

public class CheckIn extends ClearToolCommand
{
/*
    public CheckIn(String file, String args[], String comment)
    {
        super(COMMAND_CHECKIN);
        setOptions(args);
        // " needed if path contains spaces. Will not work on all platforms?
        setTarget("\"" + file + "\"");
    }
*/
    /**
    *
    * TODO Save comment to temporary file and remove after. 
    *
    * @param String file. The file to check in.
    * @param String comment. The comment for the checkin. Can be null.
    */
    public CheckIn(String file, String comment)
    {
        super(COMMAND_CHECKIN);
        
        if(comment == null)
        {
            setOption(0, OPTION_NO_COMMENT);
        }
        else
        {
            setOption(0, OPTION_COMMENT);
            setOption(1, "\"" + comment + "\"");
        }
        setTarget("\"" + file + "\"");
    }

}