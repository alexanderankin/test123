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

package cswilly.jeditPlugins.spell.hunspellbridge;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileFilter;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.Vector;

import org.gjt.sp.util.Log;

import cswilly.spell.Engine;
import cswilly.spell.Validator;
import cswilly.spell.SpellException;
import cswilly.spell.EngineManager;

import cswilly.spell.hunspellbridge.HunspellEngine;
import cswilly.spell.hunspellbridge.HunspellSuggestionValidator;

import cswilly.jeditPlugins.spell.SpellCheckPlugin;

import com.stibocatalog.hunspell.Hunspell;


public class HunspellEngineManager implements EngineManager{
	private Hunspell hspl;
	static final Pattern DICT_PATTERN=Pattern.compile("([a-z]{2}_[A-Z]{2}[-_\\w]*)\\.(?:dic|aff)");
	
	public HunspellEngineManager(){}
	
	
	public Engine getEngine(String mode,String language, boolean terse) throws SpellException{		
		initHunspell();
		try{
			String home = getDirectoryForDict(language);
			Hunspell.Dictionary d = hspl.getDictionary(home+"/"+language);
			
			assert(d!=null);//hunspell returns non-null or throws an exception
			
			return new HunspellEngine(d);
			
		}catch(FileNotFoundException e){
			throw new SpellException("Unable to find Hunspell dictionary for "+language);
		}catch(UnsupportedEncodingException e){
			throw new SpellException("Encoding error with Hunspell, please report !",e);
		}
	}

	public Validator getValidator(String mode,String language)throws SpellException{
		initHunspell();
		try{
			String home = getDirectoryForDict(language);
			Hunspell.Dictionary d = hspl.getDictionary(home+"/"+language);
			
			assert(d!=null);//hunspell returns non-null or throws an exception
			
			return new HunspellSuggestionValidator(d);
			
		}catch(FileNotFoundException e){
			throw new SpellException("Unable to find Hunspell dictionary for "+language);
		}catch(UnsupportedEncodingException e){
			throw new SpellException("Encoding error with Hunspell, please report !",e);
		}
	}
	
	/**
	 * @return list of dictionaries supported by VoxSpell
	 */
	 public Future<Vector<String>> getAlternateLangDictionaries(){
		 return new MyFuture();
	 }

	 
	public void stop(){
		hspl = null;
	}

	/**
	 * simply can't be cancelled, because it's synchronous
	 */
	 private class MyFuture implements Future<Vector<String>>{
			 private Vector<String> dicts;
			 
			 MyFuture(){
				 dicts = getDicts();
			 }
		 
			 
			 public boolean cancel(boolean ignored){
				 return false;
			 }
			 
			 public Vector<String> get(){
				 return dicts;
			 }
			 public Vector<String> get(long to, TimeUnit tu){
				 return get();
			 }
			 
			 public boolean isCancelled(){
				 return false;
			 }
			 
			 public boolean isDone(){
				 return dicts!=null;
			 }
	}
	
	public void handleMessage(org.gjt.sp.jedit.EBMessage m){}
	
	
	private void initHunspell() throws SpellException{
		if(hspl==null){
			try{
				hspl = Hunspell.getInstance();
			}catch(Exception e){
				throw new SpellException("Unable to load Hunspell",e);
			}
		}
		
		assert(hspl!=null);//hunspell always returns or throws an exception
	}
	
	private String getDirectoryForDict(String dict){
		File home = SpellCheckPlugin.getHomeDir(null);
		return home.getPath();
	}
	
	private Vector<String> getDicts(){
		Vector<String> dicts = new Vector<String>();

		File home = SpellCheckPlugin.getHomeDir(null);
		File[]dd = home.listFiles(new FileFilter(){
				public boolean accept(File f){
					return (!f.isDirectory());
				}
		});
		
		for(int i=0;i<dd.length;i++){
			Matcher m = DICT_PATTERN.matcher(dd[i].getName());
			//Log.log(Log.DEBUG,HunspellEngineManager.class,"Considering : "+dd[i].getName());
			if(m.matches()){
				String d = m.group(1);
				//Log.log(Log.DEBUG,HunspellEngineManager.class,"found : "+d);
				if(!dicts.contains(d))dicts.add(d);
			}
		}
		
		return dicts;
	}
}
