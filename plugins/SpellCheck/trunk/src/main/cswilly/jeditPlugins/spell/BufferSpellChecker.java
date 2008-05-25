/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2008 Eric Le Lay
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


package cswilly.jeditPlugins.spell;

import cswilly.spell.SpellException;
import cswilly.spell.AspellEngine;
import cswilly.spell.Engine;
import cswilly.spell.Result;
import cswilly.spell.Validator;


import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import java.util.List;
import java.util.Collections;

/**
 * Performs spell-checking on a jEdit Buffer : takes advantage of compound edit.
 * @see	BufferDialogValidator	used to highlight the word currently checked, and scroll to it.
 * 
 */
public class BufferSpellChecker{
  private String _aspellExeFilename;
  private String[] _aspellArgs;
  private Engine  _spellEngine = null;
  private BufferDialogValidator spellValidator = null;
  

  public BufferSpellChecker( String aspellExeFilename, String[] aspellArgs)
  {
    _aspellExeFilename = aspellExeFilename;
    _aspellArgs = aspellArgs;
	  spellValidator = new BufferDialogValidator();
  }

  /**
   * @return <i>true</i> if file completely checked and <i>false</i> if the user
   * interupted the checking.
   */
  public
  boolean checkBuffer(TextArea area, Buffer input )
    throws SpellException
	{
		spellValidator.start();
		spellValidator.setTextArea(area);
		Selection[] selections = area.getSelection();
		if(area.getSelectionCount() == 0){
			selections = new Selection[1];
			selections[0] = new Selection.Range(0,input.getLength());
		}
		input.beginCompoundEdit();
		//input.writeLock();
		boolean confirm = false;
		try
		{
			for(int iSelection = 0; iSelection<selections.length;iSelection++){
				Selection sel = selections[iSelection];
				for(int i=sel.getStartLine();i<=sel.getEndLine(); i++ )
				{
					String line = input.getLineText(i);
					if( line.trim().equals( "" ) )
					{
						List<Result> results = Collections.emptyList();
						spellValidator.validate(i, line, results);
					}
					else
					{
						List<Result> results = _getSpellEngine().checkLine( line );
						int startSelLine = sel.getStart(input,i);
						int endSelLine = sel.getEnd(input,i);
						int delta = 0;
						int lineOffset = input.getLineStartOffset(i);

						//filter away results out of the selection
						for(Result result : results){
							if(Result.SUGGESTION == result.getType()){
								int originalIndex = lineOffset+result.getOffset()-1;//offset starts at 1 for aspell
								if(originalIndex<startSelLine || originalIndex+result.getOriginalWord().length()>endSelLine)
								{
									Log.log(Log.DEBUG,this,"ignore :"+originalIndex+","+(originalIndex+result.getOriginalWord().length())+"->"+startSelLine+","+endSelLine+" "+result);
									result.setType(Result.OK);
								}
							}
						}
						//validate the rest
						List<Result> checkedLine = spellValidator.validate( i, line, results );
						if( checkedLine != null ){
							confirm = true;
							for(Result result : checkedLine){
								if(Result.SUGGESTION == result.getType()){
									int originalIndex = lineOffset+result.getOffset()-1+delta;//offset starts at 1 for aspell
									String newWord = result.getSuggestions().get(0);
									Log.log(Log.DEBUG,this,"o="+originalIndex+",d="+delta+",n="+newWord);
									input.remove(originalIndex,result.getOriginalWord().length());
									input.insert(originalIndex,newWord);
									delta += newWord.length()-result.getOriginalWord().length();
								}
							}
						}else{
							confirm=false;
							break;
						}
					}
				}
			}
		}
		catch( Exception e )
		{
			stop();
			if( e instanceof SpellException )
				throw (SpellException)e;
			else
				throw new SpellException( "Error communicating with the aspell subprocess", e );
		}
		finally{
			try{
				spellValidator.done();
			}finally{
				input.endCompoundEdit();
				//input.writeUnlock();
				//if cancelled, must undo !
			}
		}
		if(!confirm)
		{
			Log.log(Log.DEBUG,this,"cancelled spell-check");
			input.undo(area);
		}
		
		return confirm;
	}

  public
  String getAspellExeFilename()
  {
    return _aspellExeFilename;
  }

  public
  String[] getAspellArgs()
  {
    return _aspellArgs;
  }

  public
  void stop()
  {
    if( _spellEngine != null )
    {
      _spellEngine.stop();
      _spellEngine = null;
    }
  }

  public void unload(){
	  stop();
  }
  private
  Engine _getSpellEngine()
    throws SpellException
  {
	  if( _spellEngine == null ){
		  String logStr = "command line is:"+_aspellExeFilename;
		  for(int i=0;i<_aspellArgs.length;i++)logStr+=" "+_aspellArgs[i];
		  Log.log(Log.DEBUG,this,logStr);
      _spellEngine = new AspellEngine(_aspellExeFilename,_aspellArgs);
	  }
    return _spellEngine;
  }

}
