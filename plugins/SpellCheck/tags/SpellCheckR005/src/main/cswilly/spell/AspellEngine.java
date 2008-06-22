/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2001 C. Scott Willy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package cswilly.spell;

import java.io.*;
import java.util.*;

/**
 * Models a spelling checker
 *<p>
 *
 */
public
class AspellEngine
  implements Engine
{
  BufferedReader _aSpellReader;
  BufferedWriter _aSpellWriter;
  String         _aSpellWelcomeMsg;
  Process        _aSpellProcess;
  public AspellEngine( String aspell, String[] aSpellArgs)
    throws SpellException
  {
      List l = new ArrayList(aSpellArgs.length+1);
	  l.add(aspell);
	  l.addAll(Arrays.asList(aSpellArgs));
    try
    {
	  ProcessBuilder pb = new ProcessBuilder(l);
	  //this to allow us to catch error messages from Aspell
	  pb.redirectErrorStream(true);
      _aSpellProcess = pb.start();

	  InputStream is = _aSpellProcess.getInputStream();
	  for(int i=0;is.available()==0 && i<5;i++){
		  try{
			  Thread.sleep(500);
		  }catch(InterruptedException ie){
				throw new SpellException("Interrupted while  while starting aspell");
		  }
	  }
	  if(is.available()==0){
			throw new SpellException("Timeout while starting aspell");
	  }

      _aSpellReader =
        new BufferedReader( new InputStreamReader(is, "UTF-8") );

      _aSpellWriter =
        new BufferedWriter( new OutputStreamWriter( _aSpellProcess.getOutputStream(), "UTF-8" ) );

      _aSpellWelcomeMsg = _aSpellReader.readLine();
	  if(_aSpellWelcomeMsg == null){
		  throw new SpellException("Can't read Aspell Welcome Message");
	  }else if(_aSpellWelcomeMsg.startsWith("Error:")){
			  throw new SpellException("Aspell responded : "+_aSpellWelcomeMsg);
	  }
    }
    catch( IOException e )
    {
      String msg = "Cannot create aspell process.("+l+")";
      throw new SpellException( msg, e );
    }
	catch( SpellException e)
	{
		if(_aSpellProcess!=null)
		{
			_aSpellProcess.destroy();
			_aSpellProcess = null;
		}
		throw e;
	}
  }

  /**
   * Spell check a list of words
   *<p>
   * Spell checks the list of works in <code>words</code> and returns a list of
   * {@link Result}s. There is one {@link Result} for each word in
   * <code>words</code>.
   *<p>
   * @param words {@link String} with list of works to be spell checked.
   * @return List of {@link Result}
   */
  public List<Result> checkLine( String line )
    throws SpellException
  {
    try
    {
      List<Result> results = new ArrayList<Result>();

      final String spellCheckLinePrefix = "^";
      _aSpellWriter.write( spellCheckLinePrefix + line );
      _aSpellWriter.newLine();
      _aSpellWriter.flush();

      String response = readLine();
      while( response != null &&
        !response.equals( "" ) )
        {
          Result result = new Result( response );
          results.add( result );

          response = readLine();
        }

        return results;
    }
    catch( IOException e )
    {
      String msg = "Cannot access aspell process.";
      throw new SpellException( msg, e );
    }
	catch( SpellException spe){
		stop();
		throw spe;
	}
  }

  public
  String getVersion()
  {
    return _aSpellWelcomeMsg;
  }

  public
  void stop()
  {
    _aSpellProcess.destroy();
  }
  
  private String readLine() throws SpellException,IOException{
	  final String[] a_res = new String[1];
	  final IOException[] a_ioe = new IOException[1];
	  Thread t = new Thread(){
		  public void run(){
			  try{
				  a_res[0] = _aSpellReader.readLine();
			  }catch(IOException ioe){
				  a_ioe[0] = ioe;
			  }
		  }
	  };
	  t.start();
	  try{
	  t.join(2000);//2 seconds !
	  }catch(InterruptedException ie){
		  throw new SpellException("Interrupted while waiting for Aspell Process");
	  }
	  if(t.isAlive())
	  {
		  throw new SpellException("Timeout waiting for Aspell Process");
	  }
	  if(a_ioe[0] != null) throw a_ioe[0];
	  return a_res[0];
  }
}
