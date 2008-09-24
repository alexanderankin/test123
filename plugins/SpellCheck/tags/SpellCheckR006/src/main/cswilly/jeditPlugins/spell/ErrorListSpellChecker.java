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
import cswilly.spell.ChainingValidator;
import cswilly.spell.SpellCoordinator;


import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;

import errorlist.ErrorSource;

import java.util.List;

public class ErrorListSpellChecker implements SpellCoordinator{
	private Engine  _spellEngine = null;
	private ErrorListValidator spellValidator = null;
	private Validator validator = null;
	private TextArea area = null;

	public ErrorListSpellChecker()
	{
		spellValidator = new ErrorListValidator("SpellCheckPlugin");
		validator = spellValidator;
	}
	
	/**
	 * spell-check a whole buffer and add errors in ErrorList
 	 */
	public
	boolean spellcheck()
    throws SpellException
	{
		Engine engine = getSpellEngine();
		if(engine==null)throw new SpellException("No engine configured");
		if(area==null)throw new SpellException("No textarea configured");
		Buffer buffer = (Buffer)area.getBuffer();
		spellValidator.setPath(buffer.getPath());
		BufferSpellChecker source = new BufferSpellChecker(area,true);
		if(engine.isContextSensitive()){
			Log.log(Log.DEBUG,ErrorListSpellChecker.class,"Will spellcheck all tokens because engine is contex-sensitive");
			source.setAcceptAllTokens();
		}
		validator = new ChainingValidator(source.getValidator(),validator);
		boolean confirm = true;
		try
		{
			source.start();
			validator.start();
			for(String line = source.getNextLine();
				confirm && line!=null;
				line=source.getNextLine() )
			{
				//System.out.println("spellcheck line "+line);
				List<Result> results = engine.checkLine( line );
				for(Result r:results)
				{
					confirm = validator.validate(source.getLineNumber(), line, r );
					//System.out.println("spellcheck result "+r);
					if(!confirm){
						Log.log(Log.ERROR,ErrorListSpellChecker.class,"validator didn't confirm");
						break;
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
			validator.done();
			validator = spellValidator;
			source.done();
			if(spellValidator.getErrorCount()==0){
				Log.log(Log.DEBUG,this,"No misspelled word for "+buffer.getName());
				jEdit.getActiveView().getStatus().setMessage( "Spell check found no Error" );
			}
			else
			{	
				Log.log(Log.DEBUG,this,spellValidator.getErrorCount()+" mispelled words for "+buffer.getName());
				jEdit.getActiveView().getStatus().setMessage( "Spell-check finished" );
			}
		}
		
		return confirm;
	}
	
	private
	void stop()
	{
		if( _spellEngine != null )
		{
			_spellEngine.stop();
			_spellEngine = null;
		}
	}
	
	public void unload(){
		ErrorSource.unregisterErrorSource(spellValidator);
	}
	
	public
	Engine getSpellEngine()
	{
		return _spellEngine;
	}
	
	public
	void setSpellEngine(Engine engine)
	{
		_spellEngine = engine;
	}
	
	public void setValidator(Validator v){
		if(v==null)return;
		validator = new ChainingValidator(v,spellValidator);
	}
	
	public void setTextArea(TextArea area){
		this.area = area;
	}
}
