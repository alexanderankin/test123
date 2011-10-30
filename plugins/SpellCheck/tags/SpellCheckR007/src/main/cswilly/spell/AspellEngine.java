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
import java.util.concurrent.atomic.AtomicReference;

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
  private List<String> args;
  private String encoding;
  private boolean checkOffsets;
  /**
   * @param	aspellArgs	aspell command and arguments
   */
  public AspellEngine( List<String> aspellArgs, String encoding, boolean checkOffsets)
    throws SpellException
  {
	  args = aspellArgs;
	  this.encoding = encoding;
	  this.checkOffsets = checkOffsets;
    try
    {
	  ProcessBuilder pb = new ProcessBuilder(aspellArgs);
	  //this to allow us to catch error messages from Aspell
	  pb.redirectErrorStream(true);
      _aSpellProcess = pb.start();

	  InputStream is = _aSpellProcess.getInputStream();
	  for(int i=0;is.available()==0 && i<50;i++){
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
        new BufferedReader( new InputStreamReader(is, encoding) );

      _aSpellWriter =
        new BufferedWriter( new OutputStreamWriter( _aSpellProcess.getOutputStream(), encoding ) );

      _aSpellWelcomeMsg = _aSpellReader.readLine();
	  if(_aSpellWelcomeMsg == null){
		  throw new SpellException("Can't read Aspell Welcome Message");
	  }else if(_aSpellWelcomeMsg.startsWith("Error:")){
			  throw new SpellException("Aspell responded : "+_aSpellWelcomeMsg);
	  }
	  
	  //turn on 'terse' mode : aspell won't report correct words.
	  _aSpellWriter.write("!\n");
    }
    catch( IOException e )
    {
      String msg = "Cannot create aspell process.("+aspellArgs+")";
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
	  if(_aSpellProcess==null)throw new SpellException("Aspell engine is stopped");
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
			//don't create it just to throw it away
			if(response.charAt(0)=='*')continue;
          Result result = new Result( response );
		  /* correct a bug with aspell 0.50 reporting offsets as bytes
		     counts even in utf-8
		   */
		  if(checkOffsets&& result.getType()!=Result.OK){
			  int oo = result.getOffset()+result.getOriginalWord().length();
			  if(oo>line.length())oo=line.length();
			  int o = line.substring(0,oo).lastIndexOf(result.getOriginalWord());
			  result.setOffset(o+1);//aspell allways starts at 1
		  }
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
	if(_aSpellProcess==null)return;
    _aSpellProcess.destroy();
	_aSpellProcess=null;
  }
  
  public boolean isStopped(){
	  return _aSpellProcess == null;
  }
  
  private String readLine() throws SpellException,IOException{
	  final AtomicReference<String> a_res = new AtomicReference<String>(null);
	  final AtomicReference<IOException> a_ioe = new AtomicReference<IOException>(null);
	  Thread t = new Thread(){
		  public void run(){
			  try{
				  a_res.set(_aSpellReader.readLine());
			  }catch(IOException ioe){
				  a_ioe.set(ioe);
			  }
		  }
	  };
	  t.start();
	  try{
	  t.join(10000);//10 seconds !
	  }catch(InterruptedException ie){
		  throw new SpellException("Interrupted while waiting for Aspell Process");
	  }
	  if(t.isAlive())
	  {
		  throw new SpellException("Timeout waiting for Aspell Process");
	  }
	  if(a_ioe.get() != null) throw a_ioe.get();
	  return a_res.get();
  }
  
  public boolean isContextSensitive(){
	  // TODO: restore when context-sensitive config. is configurable
	  // return !args.contains("--mode=none");
	  return true;
  }
  
  public String toString(){
	  return "AspellEngine[encoding="+encoding+",checkOffsets="+checkOffsets+",args="+args+" ]";
  }
}
