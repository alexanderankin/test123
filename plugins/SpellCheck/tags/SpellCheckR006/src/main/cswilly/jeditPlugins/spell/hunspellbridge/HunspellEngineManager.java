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
import java.util.List;

import java.io.*;

import org.gjt.sp.util.Log;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.EditBus;

import cswilly.spell.Engine;
import cswilly.spell.Validator;
import cswilly.spell.SpellException;
import cswilly.spell.EngineManager;

import cswilly.spell.hunspellbridge.HunspellEngine;
import cswilly.spell.hunspellbridge.HunspellSuggestionValidator;

import cswilly.jeditPlugins.spell.SpellCheckPlugin;

import com.stibocatalog.hunspell.Hunspell;

import static cswilly.jeditPlugins.spell.hunspellbridge.HunspellDictsManager.Dictionary;

/**
 * Entry point for the Hunspell bridge.
 * Keeps a reference to the dictionary manager and to Hunspell library.
 *
 */
public class HunspellEngineManager implements EngineManager{
	/** property hold the path/name to an external library for hunspell */
	public static final String HUNSPELL_LIBRARY_PROP="spell-check-hunspell-library";
	
	/** reference to the library via JNA */
	private Hunspell hspl;
	
	/** reference to the dictionary registry */
	private HunspellDictsManager dictsManager;
	
	/** pattern to identify dictionary files : *.aff or *.dic */
	static final Pattern DICT_PATTERN=Pattern.compile("([a-z]{2}_[A-Z]{2}[-_\\w]*)\\.(?:dic|aff)");
	
	/** the cached name of the library */
	private String libName;
	
	public HunspellEngineManager(){
		dictsManager = new HunspellDictsManager();
		EditBus.addToBus(this);
	}
	
	/**
	 * allways return a fresh engine, but caching is performed at the library level
	 * @param	mode	the jEdit mode of the buffer (not used)
	 * @param	language	the language of the dictionary to use
	 * @param	terse	should the engine do suggestions (not used)
	 */
	public Engine getEngine(String mode,String language, boolean terse) throws SpellException{		
		initHunspell();
		try{
			String sd = getFileForDict(language);
			Hunspell.Dictionary d = null;
			try{
				d = hspl.getDictionary(sd);
			}catch(UnsatisfiedLinkError ule){
				throw new SpellException("Unable to load Hunspell library",ule);
			}
			if(d==null)throw new SpellException("Unable to find Hunspell dictionary for "+language);
			
			return new HunspellEngine(d);
			
		}catch(FileNotFoundException e){
			throw new SpellException("Unable to find Hunspell dictionary for "+language);
		}catch(UnsupportedEncodingException e){
			throw new SpellException("Encoding error with Hunspell, please report !",e);
		}
	}

	/**
	 * @param	mode	unused
	 * @param	language	name of the dictionary to use
	 */
	public Validator getValidator(String mode,String language)throws SpellException{
		initHunspell();
		try{
			String sd = getFileForDict(language);
			Hunspell.Dictionary d = hspl.getDictionary(sd);
			
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
	 public Future<Vector<cswilly.spell.Dictionary>> getAlternateLangDictionaries(){
		 return new MyFuture();
	 }

	 
	public void stop(){
		hspl = null;
		EditBus.removeFromBus(this);
	}

	public String getDescription(){
		return jEdit.getProperty("spell-check-hunspell-engine.description");
	}
	
	
	/**
	 * simply can't be cancelled, because it's synchronous
	 */
	 private class MyFuture implements Future<Vector<cswilly.spell.Dictionary>>{
			 private Vector<cswilly.spell.Dictionary> dicts;
			 
			 MyFuture(){
				 dicts = getDicts();
			 }
		 
			 
			 public boolean cancel(boolean ignored){
				 return false;
			 }
			 
			 public Vector<cswilly.spell.Dictionary> get(){
				 return dicts;
			 }
			 public Vector<cswilly.spell.Dictionary> get(long to, TimeUnit tu){
				 return get();
			 }
			 
			 public boolean isCancelled(){
				 return false;
			 }
			 
			 public boolean isDone(){
				 return dicts!=null;
			 }
	}
	
	public void handleMessage(org.gjt.sp.jedit.EBMessage m){
		if(m instanceof PropertiesChanged){
			String newLibName = jEdit.getProperty(HUNSPELL_LIBRARY_PROP);
			if((libName==null && newLibName!=null)
				|| (libName!=null && !libName.equals(newLibName)))
			{
				// TODO: are we leaking memory there ?
				hspl=null;
			}
		}
	}
	
	
	private void initHunspell() throws SpellException{
		libName = jEdit.getProperty(HUNSPELL_LIBRARY_PROP);
		if(hspl==null){
			Log.log(Log.DEBUG,HunspellEngineManager.class,"Loading Hunspell library from "+libName);
			try{
				if(libName==null)hspl = Hunspell.getInstance();
				else{
					File f = new File(libName);
					if(!f.exists())Log.log(Log.WARNING,HunspellEngineManager.class,"The library file doesn't exist...");
					hspl = Hunspell.getInstance(f.getParent(),f.getName());
				}
			}catch(Exception e){
				throw new SpellException("Unable to load Hunspell",e);
			}
		}
		
		assert(hspl!=null);//hunspell always returns or throws an exception
	}
	
	private String getFileForDict(String dict) throws FileNotFoundException{
		File home = SpellCheckPlugin.getHomeDir(null);
		
		assert(home.exists());
		
		Dictionary d = dictsManager.getInstalledDict(dict);
		if(d==null)throw new FileNotFoundException("Dictionary "+dict+" doesn't exist");
		
		File dirdict = new File(home,d.getDirectory());
		File fdict = new File(dirdict,d.getFile());
		return fdict.getPath();
	}
	
	private Vector<cswilly.spell.Dictionary> getDicts(){
		Vector<cswilly.spell.Dictionary> dicts = new Vector<cswilly.spell.Dictionary>();
		
		List<Dictionary> installed = dictsManager.getInstalled();
		
		for(Dictionary d:installed){
			dicts.add(d);
		}
		
		return dicts;
	}
	
	/**
	 * package protected because it's used in the option pane.
	 */
	HunspellDictsManager getDictsManager(){
		return dictsManager;
	}
	
}
