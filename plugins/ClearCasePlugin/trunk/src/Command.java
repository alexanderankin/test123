import java.io.IOException;
import java.io.OutputStream;

import java.util.Vector;
import java.util.Enumeration;

public class Command
{
    String command = "";
    
    String target;
    
    Vector options = new Vector(10);
    
    public Command(String command)
    {
        this.command = command;
    }
    
    /**
    *   One or more files or targets for the command which should 
    *   be last in the command line.
    */
    public void setTarget(String target)
    {
        this.target = target;
    }
    
    public void setOptions(String args[])
    {
        // Clear options first.
        options.clear();
        
        for( int i = 0; i < args.length; i++ )
        {
            if(i < getOptionsCount())
            {
                options.setElementAt(args[i], i);
            }
            else
            {
                options.add(args[i]);
            }
        }
    }
    
    public void setOption(int index, String value)
    {
        if(index < getOptionsCount())
        {
            options.setElementAt(value, index);
        }
        else
        {
            options.add(value);
        }
    }
    
    public String getOption(int index)
    {
        return (String) options.elementAt(index);
    }
    
    public int getOptionsCount()
    {
        return options.size();
    }

/*
    public void execute() throws IOException, InterruptedException
    {
        CommandExec command;
        
        if(output == null)
        {
            output = System.out;
        }
        
        if(errorOutput == null)
        {
            errorOutput = System.err;
        }

        command = new CommandExec(getCommandLine(), output, errorOutput);

        command.execute();
    }
*/

    public String getCommandLine()
    {
        StringBuffer buffer = new StringBuffer(1024);
        
        // Add the command to execute
        buffer.append(command);
        
        // Add options for the command
        for(int i = 0; i < options.size() ; i++) 
        {
            String option = (String) options.elementAt(i);
            // Add option
            buffer.append(" ");
            buffer.append(option);
        }
        
        // Append the target for the command. Usually a file.
        if(target != null)
        {
            buffer.append(" ");
            buffer.append(target);
        }
        
        return buffer.toString();
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer(1024);
        buffer.append(command);
        buffer.append(" ");
        
        for(int i = 0; i < options.size() ; i++) 
        {
            buffer.append((String)options.get(i));
            buffer.append(" ");
        }

        buffer.append(" ");
        buffer.append(target);

        return buffer.toString();
    }
}