package ise.calculator;

import java.io.*;

/**
 * Reads a file in the function format.
 *
 * @version   $Revision$
 */
public class FunctionReader {

   private String name = "";
   private String desc = "";
   private StringBuffer func = new StringBuffer();
   private String cmd = "";
   private boolean isConstant = false;
   private String LS = System.getProperty( "line.separator" );

   /**
    * Reads the given file.
    *
    * @param filename the file to read
    */
   public FunctionReader( String filename ) {
      this( new File( System.getProperty( "calc.home" ), filename ) );
   }

   /**
    * Reads the given file.
    *
    * @param f the file to read
    */
   public FunctionReader( File f ) {
      try {
         if ( f.length() == 0 )
            throw new Exception( "Zero length file." );
         BufferedReader br = new BufferedReader( new FileReader( f ) );
         String filename = f.getName();
         cmd = filename.substring( 0, filename.lastIndexOf( "." ) );

         // file format:
         // line 0: <name>
         // line 1: some text
         // line 2: </name>
         // line 3: <description>
         // line 4: description
         // line 5: </description>
         // line 6: <function>
         // next lines: commands
         // last line: </function>

         br.readLine();   // <function>
         name = br.readLine().trim();   // name
         br.readLine();   // </function>
         br.readLine();   // <description>
         desc = br.readLine();   // description
         br.readLine();   // </description>
         String line = br.readLine();   // <function> or <constant>
         isConstant = line.trim().equals( "<constant>" ) || cmd.startsWith("const");
         func = new StringBuffer();
         line = "";
         while ( line != null ) {
            line = br.readLine();
            if ( line == null || line.trim().equals( "</function>" ) || line.trim().equals( "</constant>" ) )
               break;
            func.append( line ).append( LS );
         }
         br.close();
      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
   }

   /**
    * Gets the name of the function.
    *
    * @return   The name value
    */
   public String getName() {
      return name;
   }

   /**
    * Gets the description of the function.
    *
    * @return   The description value
    */
   public String getDescription() {
      return desc;
   }

   /**
    * Gets the function steps.
    *
    * @return   The function steps.
    */
   public String getFunction() {
      return func.toString();
   }

   public boolean isConstant() {
      return isConstant;
   }

   /**
    * Gets the command string for the function, this is the file name minus
    * any extension.
    *
    * @return   The command value
    */
   public String getCommand() {
      return cmd;
   }
}

