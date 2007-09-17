import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

/**
* Does not work with a buffered stream for some reason.
*/
public class StreamWriterThread extends Thread
{
    public static final int EOF = -1;
    
    InputStream input;
    
    OutputStream output;

    public StreamWriterThread(OutputStream output, InputStream input)
    {
        this.input = input;
        this.output = output;
        
        //setDaemon(true);
        
        start();
    }
    
    public void run()
    {
        try
        {
            int c;

            while ( (c = input.read()) != EOF ) 
            {
                output.write(c);

                // Is this the correct way to do it?
                output.flush();
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
}