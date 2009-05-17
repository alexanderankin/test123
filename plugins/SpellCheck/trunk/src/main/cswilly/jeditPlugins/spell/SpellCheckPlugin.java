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

package cswilly.jeditPlugins.spell;

import cswilly.spell.EngineManager;
import cswilly.spell.Engine;
import cswilly.spell.Validator;
import cswilly.spell.SpellException;
import cswilly.spell.FutureListDicts;
import cswilly.spell.WordListValidator;
import cswilly.spell.ChainingValidator;
import cswilly.spell.SpellCoordinator;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.ServiceManager;
  
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.gui.StatusBar;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.util.Log;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.regex.*;


import java.util.concurrent.Future;

public class SpellCheckPlugin
  extends EBPlugin
{
  public static final String PLUGIN_NAME                        = "SpellCheck";
  public static final String SPELL_CHECK_ACTIONS                = "spell-check-menu";
  public static final String SPELLCHECK_ON_SAVE_PROP			= "spell-check-on-save";
  public static final String BUFFER_LANGUAGE_PROP				= "spell-check-buffer-lang";
  public static final String MAIN_LANGUAGE_PROP					= "spell-check-main-lang";
  public static final String BUFFER_IGNORE_ALL_LIST_PROP		= "spell-check-ignore-all-list";
  public static final String ENGINE_MANAGER_PROP				= "spell-check-engine";
  public static final Pattern pat = Pattern.compile("user\\.(.*)\\.dict");
  
  //no problem : lightweight
  private static EngineManager _engineManager;
  private static ErrorListSpellChecker _errorListSpellChecker = null;

  private static Map<String,WordListValidator> userDicts = new HashMap<String,WordListValidator>();
  private static Map<Buffer,WordListValidator> ignoreAlls = new HashMap<Buffer,WordListValidator>();
  
  /**
  * Displays the spell checker dialog box with specified lang dictionary. This method
  * is called by the spell-check-selection-with-lang action, defined in actions.xml.
  */
  public static void showCustomLangSpellDialog(View view,Buffer buffer)
    throws SpellException
  {
	  String oldDict = buffer.getStringProperty(BUFFER_LANGUAGE_PROP);
	  setBufferLanguage(view,buffer);
	  checkBuffer(view,buffer);
	  buffer.setStringProperty(BUFFER_LANGUAGE_PROP,oldDict);
  }
  
  private static String pickLanguage(View view,Buffer buffer)
  {
	  String result = null;
	  String oldDict = buffer.getStringProperty(BUFFER_LANGUAGE_PROP);
	  Log.log(Log.DEBUG,SpellCheckPlugin.class,buffer.getName()+" was in "+oldDict);
	  if(oldDict == null)oldDict = getMainLanguage();
	  final DictionaryPicker dp = new DictionaryPicker(getEngineManager(),oldDict);
	  
	  JDialog dialog = dp.asDialog(view);
	  dp.getRefreshAction().actionPerformed(null);
	  dialog.setVisible(true);
	  
	  if(dp.getPropertyStore().get(DictionaryPicker.CONFIRMED_PROP)!=null)
	  {
		  Log.log(Log.DEBUG,SpellCheckPlugin.class,"confirmed");
		  result = dp.getPropertyStore().get(DictionaryPicker.LANG_PROP);
	  }
	  else
	  {
		  Log.log(Log.DEBUG,SpellCheckPlugin.class,"cancelled");
	  }
	  return result;
  }


  public static
  void checkBufferErrorList(View view, Buffer buffer)
  {
	assert(buffer != null);

    ErrorListSpellChecker checker = getErrorListSpellChecker();
	Validator userDict = getUserDictionaryForLang(view,getBufferLanguage(buffer));
	Validator ignoreAll = getIgnoreAll(buffer);
	
	ChainingValidator valid = new ChainingValidator(userDict,ignoreAll);
	checker.setValidator(valid);
	Log.log(Log.DEBUG,SpellCheckPlugin.class,"SpellCheck started for "+buffer.getName()+" ("+buffer.getPath()+")");

	StatusBar status = view.getStatus();
	status.setMessage( "Spell check in process..." );
	
    try
    {
		checker.setSpellEngine(getEngineManager().getEngine(buffer.getMode().getName(),getBufferLanguage(buffer),true));
		checker.setTextArea( view.getTextArea() );
		boolean ret = checker.spellcheck();
	  
		if(!ret)Log.log(Log.DEBUG,SpellCheckPlugin.class,"ErrorList SpellCheck cancelled for "+buffer.getName()+" ("+buffer.getPath()+")");
		else Log.log(Log.DEBUG,SpellCheckPlugin.class,"SpellCheck finished for "+buffer.getName()+" ("+buffer.getPath()+")");

    }
    catch( SpellException e )
    {
      Log.log(Log.ERROR, SpellCheckPlugin.class, "Error spell checking (Aspell).");
      Log.log(Log.ERROR, SpellCheckPlugin.class, e);
      Object[] args = { new String (e.getMessage()) };
      GUIUtilities.error( view, "spell-check-error", args);
    }
	status.setMessage( "Spell check terminated with no error" );
  }

  
  public static
  void checkBuffer(View view,Buffer buffer)
  {
	
	 StatusBar status = view.getStatus();
	 status.setMessage( "Spell check in process..." );
	 
	
	 
    try
    {
		SpellCoordinator coordinator =  getDialogValidator(view,buffer);
		boolean ret  = coordinator.spellcheck();
		if(ret)
			status.setMessage( "Spellcheck terminated with no Error." );
		else status.setMessage("Spellcheck was cancelled.");

    }
    catch( SpellException e )
    {
      Log.log(Log.ERROR, SpellCheckPlugin.class, "Error spell checking (Aspell).");
      Log.log(Log.ERROR, SpellCheckPlugin.class, e);
      Object[] args = { new String (e.getMessage()) };
      GUIUtilities.error( view, "spell-check-error", args);
    }
  }

  /**
   * sets buffer's property SpellCheckPlugin.BUFFER_LANGUAGE_PROP
   * according to user's choice
   * @param	view	Currently Active view
   * @param	buffer	Buffer whose language is to be set
   */
  public static void setBufferLanguage(View view, final Buffer buffer)
  {
	 String oldDict = buffer.getStringProperty(BUFFER_LANGUAGE_PROP);
	 Log.log(Log.DEBUG,SpellCheckPlugin.class,buffer.getName()+(oldDict==null?" was not set":(" was in "+oldDict)));
	 String result = pickLanguage(view,buffer);
	 if(result != null){
		 Log.log(Log.DEBUG,SpellCheckPlugin.class,buffer.getName()+" is in "+result);
		 buffer.setStringProperty(BUFFER_LANGUAGE_PROP,result);
	 }
  }

  /**
   * sets buffer's property SpellCheckPlugin.BUFFER_LANGUAGE_PROP
   * according to user's choice
   * @param	view	Currently Active view
   * @param	buffer	Buffer whose language is to be set
   */
  public static String getBufferLanguage(Buffer buffer)
  {
	  String lang = buffer.getStringProperty(BUFFER_LANGUAGE_PROP);
	  if(lang == null)lang = getMainLanguage();
	  return lang;
  }

  public static String getMainLanguage()
  {
	 return jEdit.getProperty(MAIN_LANGUAGE_PROP,"");
  }

  
  //must keep the ErrorListSpellChecker as it's an ErrorSource
  private static
  ErrorListSpellChecker getErrorListSpellChecker()
  {
    if( _errorListSpellChecker == null )
      _errorListSpellChecker = new ErrorListSpellChecker();
    return _errorListSpellChecker;
  }

  private static SpellCoordinator getDialogValidator(View view, Buffer buffer){
	 JEditTextArea area = view.getEditPane().getTextArea();
	 if(area.getBuffer()!=buffer)
	 	throw new IllegalArgumentException("The buffer must correspond to the first area");
	 
	 BufferDialogValidator validator = new BufferDialogValidator(); 

	 validator.setTextArea(area);
	  String lang = getBufferLanguage(buffer);
	  validator.setUserDictionary(getUserDictionaryForLang(view, lang));
	  validator.setIgnoreAll(getIgnoreAll(buffer));
	  try{
		  String modeName = buffer.getMode().getName();
		  validator.setEngine(getEngineManager().getEngine(modeName,lang,false));
		  validator.setValidator(getEngineManager().getValidator(modeName,lang));
		  validator.setEngineForSuggest(getEngineManager().getEngine("text",getBufferLanguage(buffer),false));
		  validator.setValidatorForSuggest(getEngineManager().getValidator("text",lang));
	  }catch(SpellException e){
		  Log.log(Log.ERROR, SpellCheckPlugin.class, "Error setting engine (Aspell).");
		  Log.log(Log.ERROR, SpellCheckPlugin.class, e);
		  Object[] args = { new String (e.getMessage()) };
		  GUIUtilities.error( view, "spell-check-error", args);
		  
	  }
	  return validator;
  }
  
  public static EngineManager getEngineManager(){
	  String engineName = jEdit.getProperty(ENGINE_MANAGER_PROP,"Aspell");
	  
	  EngineManager eng = (EngineManager)ServiceManager.getService(EngineManager.class.getName(), engineName);
	  if(eng==null){
		  Log.log(Log.ERROR,SpellCheckPlugin.class,jEdit.getProperty("spell-check-no-such-service",new String[]{engineName}));
		  eng = (EngineManager)ServiceManager.getService(EngineManager.class.getName(), "Aspell");
	  }
	  if(eng!=_engineManager){
		  if(_engineManager!=null)_engineManager.stop();
		  _engineManager=eng;
	  }
	  return _engineManager;
  }
  
  public void stop(){
	  Log.log(Log.DEBUG,this,"stopping SpellCheckPlugin");
	  if(_errorListSpellChecker != null){
		  _errorListSpellChecker.unload();
		  _errorListSpellChecker = null;
	  }
	  if(_engineManager!=null){
		  _engineManager.stop();
		  _engineManager = null;
	  }
	  saveDictionaries(null);
  }
   
  
  public void handleMessage(EBMessage message){
	  if(message instanceof BufferUpdate){
		  final BufferUpdate bu = (BufferUpdate)message;
		  if(BufferUpdate.SAVED == bu.getWhat()){
			  File home = getHomeDir(null);
			  if(home.getPath().equals(bu.getBuffer().getDirectory())){
				  Matcher m = pat.matcher(bu.getBuffer().getName());
				  if(m.matches()){
					  String dictName = m.group(1);
					  WordListValidator dict = userDicts.get(dictName);
					  if(dict!=null){
						  if(dict.isDirty()){
							  GUIUtilities.error(null,"spell-check-dirty-dict",new String[]{dictName});
							  return;
						  }else{
							  try{
							  dict.load(new File(bu.getBuffer().getPath()));
							  }catch(IOException ioe){
								  	GUIUtilities.error( null, "spell-check-userdict-load-error.message", new String[]{dictName,ioe.getMessage()});
							  }
						  }
					  }
				  }
			  }if(jEdit.getBooleanProperty(SPELLCHECK_ON_SAVE_PROP)){
				  new Thread("SpellCheck-"+bu.getBuffer().getName()){
					  public void run(){
						  checkBufferErrorList(bu.getView(), bu.getBuffer());
					  }
				  }.start(); 
			  }
		  }else if(BufferUpdate.CLOSED == bu.getWhat()){
			  if(ignoreAlls.containsKey(bu.getBuffer()))
				  ignoreAlls.remove(bu.getBuffer());
		  }else if(BufferUpdate.LOADED == bu.getWhat()){
				  Matcher m = pat.matcher(bu.getBuffer().getName());
				  if(m.matches()){
					  String dictName = m.group(1);
					  WordListValidator dict = userDicts.get(dictName);
					  if(dict.isDirty()){
						  GUIUtilities.error(null,"spell-check-dirty-dict",new String[]{dictName});
						  return;
					  }
				  }
		  }
	  }
  }
  
  /**
   * @param	view	view to use for error messages
   * @param	lang	ISO code of language
   * @return	a dictionary for given language (empty if no words added)
   */
  private static WordListValidator getUserDictionaryForLang(View view,String lang){
	  //lazy-loading...
	  if(! userDicts.containsKey(lang)){
		  File home = EditPlugin.getPluginHome(SpellCheckPlugin.class);
		  assert home != null;
		  
		  WordListValidator dict = new WordListValidator();
		  File userDictFile = new File(home,"user."+lang+".dict");
		  if(userDictFile.exists()){
			  try{
				  dict.load(userDictFile);
			  }catch(IOException ioe){
				  GUIUtilities.error( view, "spell-check-userdict-load-error.message", new String[]{lang,ioe.getMessage()});
			  }
		  }
		  userDicts.put(lang,dict);
	  }
	  return userDicts.get(lang);
  }
  
  public static File getHomeDir(View view){
	  File home = EditPlugin.getPluginHome(SpellCheckPlugin.class);
	  assert home != null;
	  if(!home.exists()){
		  Log.log(Log.DEBUG,SpellCheckPlugin.class, "creating settings directory");
		  try{
			  boolean created = home.mkdirs();
			  if(!created)
			  	GUIUtilities.error( view, "spell-check-plugin-home-error.message", new String[]{"mkdirs failed"});
		  }catch(SecurityException se){
		  	GUIUtilities.error( view, "spell-check-plugin-home-error.message", new String[]{se.getMessage()});
		  }
	  }
	  return home;
  }
  
  public static void saveDictionaries(View view){
	  File home = getHomeDir(view);
	  for(String lang : userDicts.keySet()){
		  WordListValidator dict = userDicts.get(lang);

		  File userDictFile = new File(home,"user."+lang+".dict");

		  try{
			  dict.save(userDictFile);
		  }catch(IOException ioe){
			  ioe.printStackTrace(System.err);
			  GUIUtilities.error( view, "spell-check-userdict-save-error.message", new String[]{lang,ioe.getMessage()});
		  }
	  }
  }
  
  private static WordListValidator getIgnoreAll(Buffer buff){
	  if(ignoreAlls.containsKey(buff))return ignoreAlls.get(buff);
	  String w = buff.getStringProperty(BUFFER_IGNORE_ALL_LIST_PROP);
	  WordListValidator valid = new WordListValidator();
	  if(w != null){
		  String[]subst = w.split("\t");
		  for(int i=0;i<subst.length;i++){
			  valid.addWord(subst[i]);
		  }
	  }
	  ignoreAlls.put(buff,valid);
	  return valid;
  }
  
  public static void clearIgnoreAll(View view, Buffer buffer){
	  if(ignoreAlls.containsKey(buffer)){
		  ignoreAlls.remove(buffer);
		  String msg = jEdit.getProperty("spell-check-clear-ignore-all-cleared.message","");
		  view.getStatus().setMessage(msg);
	  }else{
		  String msg = jEdit.getProperty("spell-check-clear-ignore-all-none.message","");
		  view.getStatus().setMessage(msg);
	  }
  }
  
  public static void browseUserDicts(View view){
	  saveDictionaries(view);
	  File home = getHomeDir(view);
	  VFSBrowser.browseDirectory(view,home.getPath());
  }
}