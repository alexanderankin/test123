import java.io.OutputStream;
import java.awt.Color;

import console.Console;

/**
*   This class might not be optimal.
*/
public class ConsoleOutputStream extends OutputStream
{
    Console console;
    
    Color color;
    
    public ConsoleOutputStream(Color color, Console console)
    {
        this.console = console;
        this.color = color;
    }
    
    public void close()
    {
        console = null;
    }
    
    public void flush()
    {
        
    }

    public void write(byte[] b)
    {
        console.print(color, new String(b));
    }

    /**
    * 
    */
    public void write(byte[] src, int off, int len)
    {
        byte dest[] = new byte[len];
        
        System.arraycopy(src, off, dest, 0, len);
        
        console.print(color, new String(dest));
    }

    public void write(int b)
    {
        console.print(color, (byte) b + "");
    }

}