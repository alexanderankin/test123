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


import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import errorlist.ErrorSource;

import java.util.List;

public class ErrorListSpellChecker{
	private Engine  _spellEngine = null;
	private ErrorListValidator spellValidator = null;
	private Validator validator = null;
	
	public ErrorListSpellChecker()
	{
		spellValidator = new ErrorListValidator("SpellCheckPlugin");
		validator = spellValidator;
	}
	
	/**
	* @return <i>true</i> if file completely checked and <i>false</i> if the user
	* interupted the checking.
	*/
	public
	boolean checkBuffer( Buffer input )
    throws SpellException
	{
		Engine engine = getSpellEngine();
		if(getSpellEngine()==null)throw new SpellException("No engine configured");

		spellValidator.setPath(input.getPath());
		validator.start();

		boolean cancel = false;
		try
		{
			//put it in the try block to be sure to compensate in finally
			input.readLock();
			for(int i=0;!cancel && i<input.getLineCount(); i++ )
			{
				String line = input.getLineText(i);
				List<Result> results = engine.checkLine( line );
				cancel = !validator.validate(i, line, results );
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
			input.readUnlock();
			if(spellValidator.getErrorCount()==0){
				Log.log(Log.DEBUG,this,"No misspelled word for "+input.getName());
				jEdit.getActiveView().getStatus().setMessage( "Spell check found no Error" );
			}
			else
			{	
				Log.log(Log.DEBUG,this,spellValidator.getErrorCount()+" mispelled words for "+input.getName());
			}
		}
		
		return !cancel;
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
		Log.log(Log.DEBUG,this,"unloading errorsource");
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
		validator = new ChainingValidator(v,spellValidator);
	}
}
