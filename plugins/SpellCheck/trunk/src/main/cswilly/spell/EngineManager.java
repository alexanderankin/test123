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

package cswilly.spell;


import org.gjt.sp.jedit.EBComponent;

import java.util.Vector;
import java.util.concurrent.Future;

/**
 * Class responsible for instanciating an Engine.
 * @see	cswilly.spell.AspellEngineManager
 */
public interface EngineManager extends EBComponent
{
	
	/**
	 * @return a user-friendly description of this spell-check engine
	 */
	public String getDescription();
	
	/**
	 * It's not specified wether it should return a new one at each invocation.
	 * As far as aspell engine is concerned, an instance can be shared accross
	 * buffers, and this saves initialization time.
	 * TODO : Does this hold with context-mode support ?
	 * 
	 * @param	mode	jEdit mode of the buffer (the manager is responsible for finding the right context-filter)
	 * @param	language	language identifier to use. It should be one picked from getAlternateLangDictionaries().
	 * @param	terse	wether the engine should make suggestions or not
	 * @return 	a started Engine, with give mode and given language
	 */
	public Engine getEngine(String mode,String language, boolean terse) throws SpellException;

	public Validator getValidator(String mode, String language) throws SpellException;

	/**
	 * Asynchronously fetch available dictionaries.
	 * @see cswilly.
	 * @return a future to get the list of suported dictionaries
	 */
	public Future<Vector<Dictionary>> getAlternateLangDictionaries();
	
	
	/**
	 * release any resources.
	 */
	public void stop();
	
	
}