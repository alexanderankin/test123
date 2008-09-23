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


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.text.Position;


import cswilly.spell.Result;
import cswilly.spell.Validator;
import cswilly.spell.ValidationDialog;
import cswilly.spell.WordListValidator;
import cswilly.spell.ChainingValidator;
import cswilly.spell.SpellCoordinator;
import cswilly.spell.SpellSource;
import cswilly.spell.SpellAction;
import cswilly.spell.ChangeWordAction;
import cswilly.spell.Engine;
import cswilly.spell.SpellException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;



/**
 * Handles the validation of a whole buffer.
 * Ensures that misspelled words are visible and selected.
 * Uses user's dictionary.
 */
public
class BufferDialogValidator implements SpellCoordinator
{
  private final HashMap<String,String>        _changeAllMap = new HashMap<String,String>();
  private WordListValidator        ignoreAll = null;
  private TextArea area;
  private JEditBuffer buffer;
  private Position savedPosition;
  private WordListValidator userDict;
  private Engine engine;
  private Engine engineForSuggest;
  private Validator engineValidator;
  private Validator engineValidatorForSuggest;
  private Validator        sourceValidator = null;

  /**
   * Validates a single correction
   *<p>
   *
   *<p>
   * @return action to take. <i>null</i>
   *         is returned if the operation is cancelled. The replacement word
   *         maybe the same or different from the original word in
   *         <code>result</code>.
   */
  private
  SpellAction validate(int lineNum, String line,Result result )
  {
	  //Log.log(Log.DEBUG,BufferDialogValidator.class,"validate("+result+")");
	if(result.getType() == Result.OK) return new NopAction(null);

	if(ignoreAll!=null)ignoreAll.validate(lineNum,line, result);
	
	if(result.getType() == Result.OK){
		//Log.log(Log.DEBUG,BufferDialogValidator.class,"ignoreAll contains result");
		return new NopAction(null);
	}
	
	if(userDict!=null)userDict.validate(lineNum, line, result);
	if(result.getType() == Result.OK){
		//Log.log(Log.DEBUG,BufferDialogValidator.class,"userDict contains result");
		return new NopAction(null);
	}
	
	
	if( _changeAllMap.containsKey( result.getOriginalWord() ) )
	{
	  return new ChangeWordAction(null,
		  		lineNum,
				result.getOffset(),
				_changeAllMap.get( result.getOriginalWord() ),
				result.getOriginalWord());
	}
    
	
	//no automatic correction...
	
	// ensures visible and selected
	sourceValidator.validate(lineNum,line,result);
	
	if(result.getType() == Result.OK){
		//Log.log(Log.DEBUG,BufferDialogValidator.class,"source validator validates result");
		return new NopAction(null);
	}

	return null;
  }


  /**
   * must be called before spellcheck()
   * @param	ta	TextArea showing the buffer to spell-check
   */
  public void setTextArea(TextArea ta){
	  this.area = ta;
	  this.buffer = ta.getBuffer();
	  savedPosition = buffer.createPosition(ta.getCaretPosition());
  }

  /**
   * set optional user dictionary (will be enriched by user action "add")
   * @param	valid	user dictionary
   */
  public void setUserDictionary(WordListValidator valid){
	 userDict = valid;
  }
  
  /**
   * @param	ignoreAll	user list of ignored words for current buffer
   */
  public void setIgnoreAll(WordListValidator ignoreAll){
	 this.ignoreAll = ignoreAll;
  }
  
  /**
   * must be called before spellcheck()
   * @param	e	engine to use for spell-checking
   */
  public void setEngine(Engine e){
	  engine = e;
  }
  
  /**
   * redundant engine for context-sensitive engines, where
   * "Suggest" action wouldn't work with a single word being
   * ignored by the engine.
   * must be called before spellcheck()
   * @param	e	engine to use when user clicks on "Suggest"
   */
  public void setEngineForSuggest(Engine e){
	  engineForSuggest = e;
  }

  /**
   * optional validator associated with the engine
   * @param	v	validator to use for spell-checking
   */
  public void setValidator(Validator v){
	  engineValidator = v;
  }
  
  /**
   * optional validator associated with the "Suggest" engine
   * @param	v	validator to use
   */
  public void setValidatorForSuggest(Validator v){
	 engineValidatorForSuggest = v;
  }

  /**
   * realise the whole spell-checking.
   * setTextArea(), setEngine(), setEngineForSuggest() must have been called.
   *
   * @return	did the user confirm
   * @throws	SpellException	on precondition failure or error from the engine
   */
  public
  boolean spellcheck()
    throws SpellException
	{
		if(engine==null)throw new SpellException("No engine configured");
		if(engineForSuggest==null)throw new SpellException("No engine configured for suggest");

		boolean confirm = true;

		BufferSpellChecker source = new BufferSpellChecker(area,false);
		BufferDialogValidatorCallback callback = new BufferDialogValidatorCallback(source);

		source.start();
		try{
			sourceValidator = source.getValidator();

			Validator addValid = null;

			// here we only control "accept all tokens"
			// in relation with the engine
			// BufferSpellChecker handles the rest...
			if(engine.isContextSensitive()){
				source.setAcceptAllTokens();
			}
			
			/*if(!engine.isContextSensitive()){
				addValid = new ModeSensitiveValidator(area.getBuffer());
			}else{
				source.setAcceptAllTokens();
			}
			if(engineValidator!=null)
				if(addValid!=null)
					addValid = new ChainingValidator(addValid,engineValidator);
				else addValid = engineValidator;

			if(addValid!=null)
				sourceValidator= new ChainingValidator(sourceValidator,addValid);
			*/
			if(engineValidator!=null)
				sourceValidator= new ChainingValidator(sourceValidator,engineValidator);
			sourceValidator.start();
			if(ignoreAll!=null)ignoreAll.start();
			if(userDict!=null)userDict.start();
			
			Result firstRes = callback.firstResult();
	
			if(firstRes != null){
				ValidationDialog validationDialog = new ValidationDialog(((JEditTextArea)area).getView());
				confirm = validationDialog.showAndGo(firstRes,callback);
			}
			
		}catch(SpellException spe){
			engine.stop();
			engineForSuggest.stop();
			throw spe;
		}finally{
			source.done();//remove the lock
			//Log.log(Log.DEBUG,BufferDialogValidator.class,"spellcheck done, lock removed");
		}


		if(confirm)
		{
			List<ChangeWordAction> changes = callback.getChanges();
			if(!changes.isEmpty())source.apply(changes);
		}
		else{
			callback.undoAll();
		}
		done();

		return confirm;
	}

  
  private void done()
  {
	  //TODO can I save a rectangular selection and restore it in a meaningful way ?
	  area.setCaretPosition(savedPosition.getOffset());
	  area.scrollToCaret(false);

  	  if(ignoreAll!=null)ignoreAll.done();
	  if(userDict!=null)userDict.done();
	  sourceValidator.done();
	  userDict=null;
	  ignoreAll=null;
  }

  /**
   * handles previous/next, and actions to take according to user's actions on the
   * validation dialog
   */  
  private class BufferDialogValidatorCallback implements ValidationDialog.Callback{
	  private boolean confirm;
	  
	  private String line;
	  private List<Result> results;
	  private Result result;
	  
	  private int iLine;
	  private int iResults;
	  private SpellSource source;
	  /** the whole history of actions is kept to provide unlimited previous */
	  private List<HistoryNode> history;
	  
	  class HistoryNode{
		  int iLine,iResult;
		  String line;
		  List<Result> results;
		  SpellAction action;
		  HistoryNode(int iLine,int iResult,String line,List<Result> results,SpellAction act){
			  this.iLine=iLine;
			  this.iResult=iResult;
			  this.line = line;
			  this.results = results;
			  action = act;
		  }
	  }
	  
	  BufferDialogValidatorCallback(SpellSource source){
		  iLine = 0;
		  iResults = 0;
		  results = null;
		  result = null;
		  this.source = source;
		  history = new ArrayList<HistoryNode>();
	  }
	  
	  List<ChangeWordAction> getChanges(){  
		  List<ChangeWordAction> changes = new ArrayList<ChangeWordAction>();
		  for(HistoryNode node:history){
			  if(node.action instanceof ChangeWordAction) changes.add((ChangeWordAction)node.action);
		  }
		  return changes;
	  }
	 
	  void undoAll(){
		for(HistoryNode node:history){
			node.action.undo();
		}
	  }
	  Result firstResult()throws SpellException{
		  results = Collections.emptyList();
		  line = "";
		  return nextResult();
	  }
	  
	  private Result nextResult()throws SpellException{
		  
		  //next line...
		  if(results!=null && iResults>=results.size()){
			  line = "";
			  while(line != null && line.trim().equals("")){
				  line = source.getNextLine();
				  iLine=source.getLineNumber();
				  //Log.log(Log.DEBUG,BufferDialogValidatorCallback.class,"got line "+line);
			  }
			  
			  if(line == null){
				  results=null;
				  result = null;
				  return null;
			  } else{
				  results = engine.checkLine( line );
				  //Log.log(Log.DEBUG,BufferDialogValidatorCallback.class,"results:"+results);
				  iResults = 0;
			  }
		  }
		  
		  //next result
		  while(iResults<results.size()){
			  Result res = results.get(iResults);
			  iResults++;
			  SpellAction action = validate(iLine,line,res);
			  if(action == null){
				  result = res;
				  return res;
			  }
			  else{
				  //Log.log(Log.DEBUG,BufferDialogValidatorCallback.class,"automatic action :"+action);
				  //history.add(new HistoryNode(iLine,iResults,line,results,action));
			  }
		  }
		  
		  return nextResult();//new line...
	  }
	  
	  public Result add()throws SpellException{
		  if(userDict!=null){
			  history.add(new HistoryNode(iLine,iResults,line,results,
				  new AddAction(null,result.getOriginalWord())));
			  userDict.addWord(result.getOriginalWord());
		  }else{
			  history.add(new HistoryNode(iLine,iResults,line,results,new NopAction(null)));
		  }
		  return nextResult();
	  }
	  
	  public Result change(String newWord)throws SpellException{
		  history.add(new HistoryNode(iLine,iResults,line,results,
			  new ChangeWordAction(null,
				  iLine,
				  result.getOffset(),
				  result.getOriginalWord(),
				  newWord)));
		  return nextResult();
	  }
	  
	  public Result changeAll(String newWord)throws SpellException{
		  history.add(new HistoryNode(iLine,iResults,line,results,
			  new ChangeAllAction(null,
				  iLine,
				  result.getOffset(),
				  result.getOriginalWord(),
				  newWord)));
		  _changeAllMap.put(result.getOriginalWord(),newWord);
		  return nextResult();
	  }
	  
	  public Result ignore()throws SpellException{
		  history.add(new HistoryNode(iLine,iResults,line,results,new NopAction(null)));
		  return nextResult();
	  }
	  
	  public Result ignoreAll()throws SpellException{
		  if(ignoreAll==null)history.add(new HistoryNode(iLine,iResults,line,results,new NopAction(null)));
		  else{
			  history.add(new HistoryNode(iLine,iResults,line,results,
				  new IgnoreAllAction(null,result.getOriginalWord())));
			  ignoreAll.addWord(result.getOriginalWord());
		  }
		  return nextResult();
	  }
	  
	  public Result suggest(String newWord)throws SpellException{
		  if(newWord==null)throw new IllegalArgumentException("word for suggest shouldn't be null");
		  List<Result> lr = engineForSuggest.checkLine(newWord);
		  if(lr.size()>0){
			  Result r = lr.get(0);
			  
			  if(r.getType() == Result.OK) return r;
			  
			  
			  if(ignoreAll!=null)ignoreAll.validate(0,newWord,r);
			  if(r.getType() == Result.OK) return r;
			  
			  if(userDict!=null)userDict.validate(0, newWord, r);
			  
			  if(result.getType() == Result.OK) return r;
			  
			  
			  if( _changeAllMap.containsKey( r.getOriginalWord() ) )
			  {
				  r.getSuggestions().add(0,_changeAllMap.get(r.getOriginalWord()));
			  }
			  
			  if(engineValidatorForSuggest!=null)engineValidatorForSuggest.validate(0,newWord,r);

			  return r;

		  }else{
			  return new Result(0,Result.OK,null,newWord);
		  }
	  }
	  
	  public Result previous()throws SpellException{
		  HistoryNode node = null;
		  while(!history.isEmpty()&&node==null){
			  node = history.remove(history.size()-1);
			  if(node.results.get(node.iResult-1).getType()==Result.OK){
				  node = null;
			  }
		  }
		  if(node == null){
			  Log.log(Log.ERROR,BufferDialogValidator.class,"previous called while history is empty");
			  return null;
		  }else{
			  //Log.log(Log.DEBUG,BufferDialogValidatorCallback.class,"results are : "+results);
			  iLine = node.iLine;
			  iResults = node.iResult;
			  line = node.line;
			  results = node.results;
			  result = results.get(iResults-1);
			  while(source.getLineNumber()>iLine)source.getPreviousLine();
			  node.action.undo();
			  validate(iLine,line,result);
			  //Log.log(Log.DEBUG,BufferDialogValidatorCallback.class,"results are now : "+results);
			  return result;
		  }
	  }
	  
	  public boolean cancel(){
		  confirm = false;
		  //TODO : display confirm dialog...
		  return true;
	  }
	  
	  public void done(){confirm = true;}
	  
	  public boolean hasPrevious(){return !history.isEmpty();}
	  
	  public boolean hasIgnored(){return ignoreAll!=null && !ignoreAll.getAllWords().isEmpty();}
	  
	  boolean isConfirmed(){return confirm;}
	  
  }
  
  
  private class PreviousAction extends SpellAction{
	  PreviousAction(Validator source){
		  super(source);
	  }
	  public void undo(){}
  }
  
  private class CancelAction extends SpellAction{
	  CancelAction(Validator source){
		  super(source);
	  }
	  public void undo(){}
  }
  
  private class ChangeAllAction extends ChangeWordAction{
	  ChangeAllAction(Validator source
		  , int line
		  , int offset
		  , String originalWord
		  , String newWord)
	  {
		  super(source,line,offset,originalWord,newWord);
	  }
	  public void undo(){
		  _changeAllMap.remove(originalWord);
	  }
  }
  
  private class IgnoreAllAction extends SpellAction{
	  private String word;
	  IgnoreAllAction(Validator source, String word)
	  {
		  super(source);
		  this.word = word;
	  }
	  
	  public void undo(){
		  ignoreAll.removeWord(word);
	  }
  }
  
  private class AddAction extends SpellAction{
	  private String word;
	  AddAction(Validator source, String word)
	  {
		  super(source);
		  this.word = word;
	  }
	  public void undo(){
		  userDict.removeWord(word);
	  }
  }
  
  private class NopAction extends SpellAction{
	  NopAction(Validator source){super(source);}
	  public void undo(){}
  }
}
