public class ShowVersionTree extends ClearToolCommand
{
    boolean graphical = false;
    
    public ShowVersionTree(String file, String args[])
    {
        super(COMMAND_LSVTREE);
        setOptions(args);
        // " needed if path contains spaces. Will not work on all platforms?
        setTarget("\"" + file + "\"");
    }
    
    public ShowVersionTree(String file)
    {
        super(COMMAND_LSVTREE);
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