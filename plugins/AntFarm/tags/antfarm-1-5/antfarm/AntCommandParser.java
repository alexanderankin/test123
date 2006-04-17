//------------------------------------------------------------------------------
// Copyright (c) 2000
//------------------------------------------------------------------------------

package antfarm;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
/**
*
* @author Richard Wan
*/
public class AntCommandParser 
{
  //--------------------------------------------------------------------------
  //   Constants:
  //--------------------------------------------------------------------------
  
  //--------------------------------------------------------------------------
  //   Protected Variables:
  //--------------------------------------------------------------------------
  
  //--------------------------------------------------------------------------
  //   Private Variables:
  //--------------------------------------------------------------------------
  
  private static final AntCommandParser _instance = new AntCommandParser();
  //--------------------------------------------------------------------------
  //   Constructors:
  //--------------------------------------------------------------------------
  
  //--------------------------------------------------------------------------
  //   Public Methods:
  //--------------------------------------------------------------------------

  public static Properties parseAntCommandProperties(String command)
  {
    Properties props = new Properties();
    InputStream input = _instance.getCommandAsInput(command);
    _instance.safelyLoad(props, input);
    return props;
  }
  //--------------------------------------------------------------------------
  //   Protected Methods:
  //--------------------------------------------------------------------------
  
  //--------------------------------------------------------------------------
  //   Private Methods:
  //--------------------------------------------------------------------------
  
  private InputStream getCommandAsInput(String command)
  {
    String fixedString = getFixedString(command);
    return new ByteArrayInputStream(fixedString.getBytes());
  }
  
  private String getFixedString(String command)
  {
    return command.replace(' ','\n');
  }
  
  private void safelyLoad(Properties props, InputStream input)
  {
    try
    {
      attemptLoad(props, input);
    }
    catch (IOException e)
    {
      return; // should not get IOExceptions
    }
  }
  
  private void attemptLoad(Properties props, InputStream input) throws IOException 
  {
    props.load(input);
  }
  //--------------------------------------------------------------------------
  //   Nested Top-Level Classes or Interfaces:
  //--------------------------------------------------------------------------
  
}




