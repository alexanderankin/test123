public class ShowHistory extends ClearToolCommand
{
    boolean graphical = false;
    
    public ShowHistory(String file, String args[])
    {
        super(COMMAND_LSHISTORY);
        setOptions(args);
        // " needed if path contains spaces. Will not work on all platforms?
        setTarget("\"" + file + "\"");
    }
    
    public ShowHistory(String file)
    {
        super(COMMAND_LSHISTORY);
        setTarget("\"" + file + "\"");
    }
    
    public void setGraphical(boolean graphical)
    {
        this.graphical = graphical;
        
        if(graphical)
        {
            setOption(getOptionsCount(), OPTION_GRAPHICAL);
        }
    }
    
}