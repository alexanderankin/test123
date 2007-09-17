public class ComparePreviousVersion extends ClearToolCommand
{
    boolean graphical = false;
    
    public ComparePreviousVersion(String file, String args[])
    {
        super(COMMAND_LSHISTORY);
        setOptions(args);
        // " needed if path contains spaces. Will not work on all platforms?
        setTarget("\"" + file + "\"");
    }
    
    public ComparePreviousVersion(String file)
    {
        super(COMMAND_COMPARE_PREVIOUS_VERSION);
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