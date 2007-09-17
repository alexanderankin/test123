import java.io.File;

public class Update extends ClearToolCommand
{

    public Update(String path)
    {
        super(COMMAND_UPDATE);
        
        File file = new File(path);
        
        if(file.isDirectory())
        {
        
        }
        else if(file.isFile())
        {
            
        }
        else
        {
            // TODO
        }
        
        setTarget("\"" + path + "\"");
    }
    
}