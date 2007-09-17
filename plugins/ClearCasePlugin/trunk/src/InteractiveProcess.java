import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;

public class InteractiveProcess
{
    // Information stream from application.
    OutputStream input;
    // Error stream from application.
    OutputStream error;
    // Output stream for data sent to the application.
    InputStream output;
    // Interactive application that we execute
    String command;

    Process process;

    StreamReaderThread inputReader;
    
    StreamReaderThread errorReader;
    
    StreamWriterThread outputWriter;
    
    public InteractiveProcess(String command, OutputStream in, OutputStream err, InputStream out)
    {
        this.command = command;
        
        input = in;
        
        error = err;
        
        output = out;
    }
    /*
    public void flush()
    {
        try
        {
            input.flush();
            error.flush();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    */
    /**
    *   Non interactive. Data must be sent through write(String text) method.
    */
    public InteractiveProcess(String command, OutputStream in, OutputStream err)
    {
        this.command = command;
        
        input = in;
        
        error = err;
    }

    public void start() throws IOException, InterruptedException
    {
        if(process != null)
        {
            stop();
        }
        
        // Improve...
        if(command == null || "".equals(command))
        {
            throw new IOException("Command cannot be empty or null.");
        }
        
        // Execute process
        System.out.println("Starting process. " + command);
        
        process = Runtime.getRuntime().exec(command);

        inputReader = new StreamReaderThread(process.getInputStream(), input, "INPUT");

        errorReader = new StreamReaderThread(process.getErrorStream(), error, "ERROR");
        
        if(output != null)
        {
            outputWriter = new StreamWriterThread(process.getOutputStream(), output);
        }
    }

    /**
    *   
    */
    public void write(String text) throws IOException
    {
        if(process == null)
        {
            throw new IllegalStateException("Process has not been started. Call start() before calling write().");
        }

        if(text == null)
        {
            return;
        }

        process.getOutputStream().write(text.getBytes());
        process.getOutputStream().flush();
        
    }
    /*
    public boolean isStarted()
    {
        if(process != null)
        {
            return true;
        }
            
        return false;
    }
    */
    
    public int stop()
    {
        System.out.println("Stopping process.");
        
        try
        {
            if(input != null)
                input.close();
        }
        catch(IOException ex) { }

        try
        {
            if(error != null)
                error.close();
        }
        catch(IOException ex) { }
        
        try
        {
            if(output != null)
                output.close();
        }
        catch(IOException ex) { }
        
        // Should we use something else?
        if(process != null)
        {
            process.destroy();
        
            return process.exitValue();
        }
        else
        {
            return -1;
        }
    }

    /**
    *
    */
    public static void main(String args[])
    {
        try
        {
            // Use System.in instead of ByteArrayInputStream.
            //InteractiveProcess p = new InteractiveProcess(args[0], System.out, System.err);
            InteractiveProcess p = new InteractiveProcess(args[0], System.out, System.err, System.in);
            p.start();
            //System.out.println("help");
            //p.write("help\n");
            System.in.read();
            //p.write("mklabel\n");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
}