
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
*   What happens when external program hangs??
*   Should we run program in a thread and kill it?
*/
public class CommandExec
{
    String commandLine;
    
    OutputStream output;
    
    OutputStream errorOutput;
    
    public CommandExec(String commandLine, OutputStream output, OutputStream errorOutput)
    {
        this.commandLine = commandLine;
        this.output = output;
        this.errorOutput = errorOutput;
    }
    
    public int execute() throws IOException, InterruptedException
    {
        InputStream input = null;
        
        InputStream error = null;
        
        String line;
        
        Process process;
        
        try 
        {
            System.out.println("Executing process " + commandLine);
            
            // Execute process
            process = Runtime.getRuntime().exec(commandLine);
            
            input = process.getInputStream();
            
            error = process.getErrorStream();
            
            // We won't be doing output. Close the stream otherwise the application will hang.
            process.getOutputStream().close();

            StreamReaderThread inputReader = new StreamReaderThread(input, output, "INPUT");

            StreamReaderThread errorReader = new StreamReaderThread(error, errorOutput, "ERROR");
        }
        finally
        {
            // Close all streams otherwise the application will hang.
            /*
            System.out.println("Closing streams");
            
            if(input != null)
            {
                input.close();
            }
            
            if(error != null)
            {
                error.close();
            }
            */
        }
        
        System.out.println("Waiting for process");

        // Wait for process to finish
        int exitValue = process.waitFor();
        
        System.out.println("Process finished ");
        
        System.out.println("Exit value: " + exitValue);
        
        return exitValue;
    }
    
    /**
    *   Not needed for ClearTool
    public static String getShell()
    {
        return "cmd /C ";
    }
    */
}



