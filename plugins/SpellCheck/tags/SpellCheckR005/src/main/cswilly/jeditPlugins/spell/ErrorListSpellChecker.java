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


import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import errorlist.ErrorSource;

import java.util.List;

public class ErrorListSpellChecker{
	private String _aspellExeFilename;
	private String[] _aspellArgs;
	private Engine  _spellEngine = null;
	private ErrorListValidator spellValidator = null;
	
	
	public ErrorListSpellChecker( String aspellExeFilename, String[] aspellArgs)
	{
		_aspellExeFilename = aspellExeFilename;
		_aspellArgs = aspellArgs;
		spellValidator = new ErrorListValidator("SpellCheckPlugin");
	}
	
	/**
	* @return <i>true</i> if file completely checked and <i>false</i> if the user
	* interupted the checking.
	*/
	public
	boolean checkBuffer( Buffer input )
    throws SpellException
	{
		spellValidator.start();
		ErrorSource.unregisterErrorSource(spellValidator);
		spellValidator.setPath(input.getPath());
		input.readLock();
		try
		{
			for(int i=0;i<input.getLineCount(); i++ )
			{
				String line = input.getLineText(i);
				List<Result> results = _getSpellEngine().checkLine( line );
				
				List<Result> checkedLine = spellValidator.validate(i, line, results );
				if( checkedLine == null ) return false;
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
			spellValidator.done();
			input.readUnlock();
			if(spellValidator.getErrorCount()==0){
				Log.log(Log.DEBUG,this,"No misspelled word for "+input.getName());
				jEdit.getActiveView().getStatus().setMessage( "Spell check found no Error" );
			}
			else
			{	
				Log.log(Log.DEBUG,this,spellValidator.getErrorCount()+" mispelled words for "+input.getName());
				ErrorSource.registerErrorSource(spellValidator);
			}
		}
		
		return true;
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
		Log.log(Log.DEBUG,this,"unloading errorsource");
		stop();
		ErrorSource.unregisterErrorSource(spellValidator);
		
	}
	private
	Engine _getSpellEngine()
    throws SpellException
	{
		if( _spellEngine == null ){
			_spellEngine = new AspellEngine(_aspellExeFilename,_aspellArgs);
		}
		return _spellEngine;
	}
	
}
