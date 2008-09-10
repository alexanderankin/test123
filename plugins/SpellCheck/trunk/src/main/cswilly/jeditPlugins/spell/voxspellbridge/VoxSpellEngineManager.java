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

package cswilly.jeditPlugins.spell.voxspellbridge;

import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.gjt.sp.jedit.jEdit;

import cswilly.spell.Engine;
import cswilly.spell.Validator;
import cswilly.spell.SpellException;
import cswilly.spell.EngineManager;
import cswilly.spell.Dictionary;

import voxspellcheck.OffsetTrie;
import voxspellcheck.VoxSpellPlugin;


public class VoxSpellEngineManager implements EngineManager{
	private VoxSpellEngine engine;

	public VoxSpellEngineManager(){
	}
	
	public Engine getEngine(String mode,String language, boolean terse) throws SpellException{
		if(engine == null||engine.isStopped()){
			try{
				Class.forName("voxspellcheck.VoxSpellPlugin");
				engine = new VoxSpellEngine(VoxSpellPlugin.getChecker());
			}catch(ClassNotFoundException cnfe){
				throw new SpellException("VoxSpell plugin is not started");
			}
		}
		return engine;
	}

	public Validator getValidator(String mode,String language)throws SpellException{
			try{
				Class.forName("voxspellcheck.VoxSpellPlugin");
				return new VoxSpellSuggestionValidator(VoxSpellPlugin.getSuggestionTree());
			}catch(ClassNotFoundException cnfe){
				throw new SpellException("VoxSpell plugin is not started");
			}		
	}
	
	/**
	 * @return list of dictionaries supported by VoxSpell
	 */
	 public Future<Vector<Dictionary>> getAlternateLangDictionaries(){
		 return new MyFuture();
	 }

	 
	public void stop(){
		if(engine!=null)engine.stop();
		engine = null;
	}

	public String getDescription(){
	  return jEdit.getProperty("spell-check-voxspell-engine.description");
	}
	
	 private class MyFuture implements Future<Vector<Dictionary>>{
			 private Vector<Dictionary> dicts;
			 private boolean cancelled;
			 MyFuture(){
				 dicts = new Vector<Dictionary>();
				 dicts.add(new Dictionary("en","en"));
				 cancelled = false;
			 }
		 
			 
			 public boolean cancel(boolean ignored){
				 cancelled = true;
				 return true;
			 }
			 
			 public Vector<Dictionary> get(){
				 return dicts;
			 }
			 public Vector<Dictionary> get(long to, TimeUnit tu){
				 return get();
			 }
			 
			 public boolean isCancelled(){
				 return cancelled;
			 }
			 
			 public boolean isDone(){
				 return !cancelled;
			 }
	}
	
	public void handleMessage(org.gjt.sp.jedit.EBMessage m){}
}
