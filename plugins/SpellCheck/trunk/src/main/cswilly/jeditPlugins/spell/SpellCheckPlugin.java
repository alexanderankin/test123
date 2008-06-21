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

import cswilly.spell.FileSpellChecker;
import cswilly.spell.SpellException;
import cswilly.spell.FutureListDicts;


import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.BufferUpdate;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.gui.StatusBar;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;


import java.util.concurrent.Future;

public class SpellCheckPlugin
  extends EBPlugin
{
  public static final String PLUGIN_NAME                        = "SpellCheck";
  public static final String SPELL_CHECK_ACTIONS                = "spell-check-menu";
  public static final String ASPELL_EXE_PROP                    = "spell-check-aspell-exe";
  public static final String ASPELL_MARKUP_MODE_PROP            = "spell-check-aspell-markup-mode";
  public static final String ASPELL_LANG_PROP                   = "spell-check-aspell-lang";
  public static final String ASPELL_OTHER_PARAMS_PROP			= "spell-check-aspell-other-params";
  public static final String FILTER_AUTO						= "AUTO";					
  public static final String FILTERS_PROP						= "spell-check-filter";					
  public static final String SPELLCHECK_ON_SAVE_PROP			= "spell-check-on-save";
  public static final String BUFFER_LANGUAGE_PROP				= "spell-check-buffer-lang";
  public static enum AspellMarkupMode{
	  NO_MARKUP_MODE("aspellNoMarkupMode"),
	  MANUAL_MARKUP_MODE("aspellManualMarkupMode"),
	  AUTO_MARKUP_MODE("aspellAutoMarkupMode");
	  
	  AspellMarkupMode(String v){ this.value = v; }
	  
	  private final String value;
	  public String toString(){ return value; }
	  public static AspellMarkupMode fromString(String s){
		  for(AspellMarkupMode mode : AspellMarkupMode.values()){
			  if(mode.value.equals(s))return mode;
		  }
		  throw new IllegalArgumentException("Invalid mode name :"+s);
	  }
  }
  
  public static final ArrayList defaultModes = new ArrayList( Arrays.asList( new String[] {"html", "shtml", "sgml", "xml", "xsl"} ) );

//  private static FileSpellChecker _fileSpellChecker = null;
  private static BufferSpellChecker _bufferSpellChecker = null;
  private static ErrorListSpellChecker _errorListSpellChecker = null;

  private static String aspellMainLanguage;
  private static List<String> aspellCommandLine;

  
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
	  if(oldDict == null)oldDict = getAspellMainLanguage();
	  final DictionaryPicker dp = new DictionaryPicker(oldDict);
	  
	  dp.getPropertyStore().put(ASPELL_EXE_PROP,getAspellExeFilename());
	  
	  
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
    ErrorListSpellChecker checker = null;

	Log.log(Log.DEBUG,SpellCheckPlugin.class,"SpellCheck started for "+buffer.getName()+" ("+buffer.getPath()+")");

	StatusBar status = view.getStatus();
	status.setMessage( "Spell check in process..." );
	
	initCommandLine(buffer);

    try
    {
      checker = getErrorListSpellChecker();

      if ( checker == null )
        throw new SpellException("No or invalid executable specified");

	//TODO handle return
      checker.checkBuffer( buffer );
	  
	  Log.log(Log.DEBUG,SpellCheckPlugin.class,"SpellCheck finished for "+buffer.getName()+" ("+buffer.getPath()+")");

    }
    catch( SpellException e )
    {
      Log.log(Log.ERROR, SpellCheckPlugin.class, "Error spell checking (Aspell).");
      Log.log(Log.ERROR, SpellCheckPlugin.class, e);
      Object[] args = { new String (e.getMessage()) };
      GUIUtilities.error( view, "spell-check-error", args);
    }
  }

  private static void initCommandLine(Buffer buffer){
	 String dict = buffer.getStringProperty(BUFFER_LANGUAGE_PROP);
	 if(dict == null) dict = jEdit.getProperty(ASPELL_LANG_PROP,"");
	 setAspellMainLanguage(dict);

    // Construct aspell command line arguments
    List aspellCommandLine = new ArrayList(4);
	// use this option switch to prevent any encoding issue
	// available at least since aspell 0.5.3
	aspellCommandLine.add("--encoding=utf-8");
    String mode = buffer.getMode().getName();
    if ( getAspellManualMarkupMode() || ( getAspellAutoMarkupMode() && isMarkupModeSelected( mode ) ) ){
		aspellCommandLine.add("--mode="+jEdit.getProperty(FILTERS_PROP+"."+mode,"none"));
	}

    String aspellMainLanguage = getAspellMainLanguage();
    if ( !aspellMainLanguage.equals("") ){
      aspellCommandLine.add("--lang=" + aspellMainLanguage);
	  //can't find this option:
	  //aspellCommandLine.add("--language-tag=" + aspellMainLanguage);
	}

	String aspellOtherParams = getAspellOtherParams();
	//TODO : fix params with spaces protected with quotes
	for(StringTokenizer st=new StringTokenizer(aspellOtherParams);st.hasMoreTokens();){
		aspellCommandLine.add(st.nextToken());
	}

    aspellCommandLine.add("pipe");

    setAspellCommandLine( aspellCommandLine);
  }
  
  public static
  void checkBuffer(View view,Buffer buffer)
  {
	 JEditTextArea area = view.getEditPane().getTextArea();
	 if(area.getBuffer()!=buffer)
	 	throw new IllegalArgumentException("The buffer must correspond to the first area");
	 BufferSpellChecker checker = null;

	 StatusBar status = view.getStatus();
	 status.setMessage( "Spell check in process..." );
	 
	 initCommandLine(buffer);
	 
    try
    {
      checker = getBufferSpellChecker();

      if ( checker == null )
        throw new SpellException("No or invalid executable specified");

	//TODO handle return
      checker.checkBuffer( area, buffer );

	  status.setMessage( "Spell terminated with no Error..." );

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
	 Log.log(Log.DEBUG,SpellCheckPlugin.class,buffer.getName()+" was in "+oldDict);
	 String result = pickLanguage(view,buffer);
	 if(result != null){
		 Log.log(Log.DEBUG,SpellCheckPlugin.class,buffer.getName()+" is in "+result);
		 buffer.setStringProperty(BUFFER_LANGUAGE_PROP,result);
	 }
  }

  private static
  BufferSpellChecker getBufferSpellChecker()
  {
    String  aspellExeFilename = getAspellExeFilename();
    String[]  aspellCommandLine = (String[])getAspellCommandLine().toArray(new String[]{});

    if ( aspellExeFilename == null )
      return null;

    if( _bufferSpellChecker == null )
      _bufferSpellChecker = new BufferSpellChecker( aspellExeFilename, aspellCommandLine );
    else
      if( !aspellExeFilename.equals( _bufferSpellChecker.getAspellExeFilename() )
       || !aspellCommandLine.equals( _bufferSpellChecker.getAspellArgs() ) )
      {
        _bufferSpellChecker.stop();
        _bufferSpellChecker = new BufferSpellChecker( aspellExeFilename, aspellCommandLine );
      }

    return _bufferSpellChecker;
  }

  private static
  ErrorListSpellChecker getErrorListSpellChecker()
  {
    String  aspellExeFilename = getAspellExeFilename();
    String[]  aspellCommandLine = (String[])getAspellCommandLine().toArray(new String[]{});

    if ( aspellExeFilename == null )
      return null;

    if( _errorListSpellChecker == null )
      _errorListSpellChecker = new ErrorListSpellChecker( aspellExeFilename, aspellCommandLine );
    else
      if( !aspellExeFilename.equals( _errorListSpellChecker.getAspellExeFilename() )
       || !aspellCommandLine.equals( _errorListSpellChecker.getAspellArgs() ) )
      {
        _errorListSpellChecker.stop();
        _errorListSpellChecker = new ErrorListSpellChecker( aspellExeFilename, aspellCommandLine );
      }

    return _errorListSpellChecker;
  }

  static
  String getAspellExeFilename()
  {
    String aspellExeFilename = jEdit.getProperty( ASPELL_EXE_PROP );

    if( aspellExeFilename == null || aspellExeFilename.equals("") )
    {
      if ( OperatingSystem.isUnix() )
        aspellExeFilename = "aspell";
      else
      {
        GUIUtilities.message(null, "spell-check-noAspellExe", null);
        String[] paths = GUIUtilities.showVFSFileDialog( null, null, JFileChooser.OPEN_DIALOG, false );

        if (paths != null)
          aspellExeFilename = paths[0];
        else
        {
          return null;
        }
      }
      jEdit.setProperty( SpellCheckPlugin.ASPELL_EXE_PROP, aspellExeFilename );
    }

    return aspellExeFilename;
  }

  private static
  List<String> getAspellCommandLine()
  {
    if( aspellCommandLine == null )
      aspellCommandLine = new ArrayList<String>();

    return aspellCommandLine;
  }

  private static
  void setAspellCommandLine(List<String> newCommandLine)
  {
    aspellCommandLine = newCommandLine;
	Log.log(Log.DEBUG,SpellCheckPlugin.class,"setting command line "+newCommandLine);
  }

  public static
  String getAspellMainLanguage()
  {
    if( aspellMainLanguage == null )
      aspellMainLanguage = "";

    return aspellMainLanguage;
  }

  private static
  void setAspellMainLanguage(String newLanguage)
  {
    aspellMainLanguage = newLanguage;
  }

  private static
  String getAspellOtherParams()
  {
    return jEdit.getProperty( ASPELL_OTHER_PARAMS_PROP, "");
  }

  private static
  boolean getAspellManualMarkupMode()
  {
    return AspellMarkupMode.MANUAL_MARKUP_MODE.toString().equals(jEdit.getProperty( ASPELL_MARKUP_MODE_PROP));
  }

  private static
  boolean getAspellAutoMarkupMode()
  {
    return AspellMarkupMode.AUTO_MARKUP_MODE.toString().equals(jEdit.getProperty( ASPELL_MARKUP_MODE_PROP));
  }

  private static
  boolean isMarkupModeSelected ( String editMode )
  {
    boolean defaultVal;
    if ( defaultModes.contains( editMode ) )
      return jEdit.getBooleanProperty("spell-check-mode-" + editMode + "-isSelected", true);
    else
      return jEdit.getBooleanProperty("spell-check-mode-" + editMode + "-isSelected", false);
  }

  public static
  Future<Vector<String>> getAlternateLangDictionaries()
  {
	  return new FutureListDicts(getAspellExeFilename());
  }
  
  public void stop(){
	  Log.log(Log.DEBUG,this,"stopping SpellCheckPlugin");
	  // if(_fileSpellChecker != null){
		//   _fileSpellChecker.stop();
		//   _fileSpellChecker = null;
	  // }
	  if(_errorListSpellChecker != null){
		  _errorListSpellChecker.unload();
		  _errorListSpellChecker = null;
	  }
	  if(_bufferSpellChecker!=null){
		  _bufferSpellChecker.unload();
		  _bufferSpellChecker = null;
	  }
  }
   
  
  public void handleMessage(EBMessage message){
	  if(!jEdit.getBooleanProperty(SPELLCHECK_ON_SAVE_PROP))return;
	  if(message instanceof BufferUpdate){
		  final BufferUpdate bu = (BufferUpdate)message;
		  if(BufferUpdate.SAVED == bu.getWhat()){
			  new Thread("SpellCheck-"+bu.getBuffer().getName()){
				  public void run(){
					  checkBufferErrorList(bu.getView(), bu.getBuffer());
				  }
			  }.start(); 
		  }
	  }
  }
}