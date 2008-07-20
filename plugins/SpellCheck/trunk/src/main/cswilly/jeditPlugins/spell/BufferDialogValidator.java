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


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;

import javax.swing.text.Position;


import cswilly.spell.Result;
import cswilly.spell.Validator;
import cswilly.spell.ValidationDialog;
import cswilly.spell.WordListValidator;
import cswilly.spell.SpellCoordinator;
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
 * A validator of a spell check results, ensuring that misspelled words are visible and selected
 * Upon prompt of the user
 */
public
class BufferDialogValidator implements SpellCoordinator
{
  private final HashMap<String,String>        _changeAllMap = new HashMap<String,String>();
  private WordListValidator        ignoreAll = null;
  private TextArea area;
  private JEditBuffer buffer;
  private Position savedPosition;
  private ValidationDialog validationDialog;
  private WordListValidator userDict;
  private Engine engine;  
  private List<SpellAction> history;

  /**
   * Validates a single correction
   *<p>
   *
   *<p>
   * @param result A {@link Result} of spell checking one word
   * @return validated correction (this is the replacement word). <i>null</i>
   *         is returned if the operation is cancelled. The replacement word
   *         maybe the same or different from the original word in
   *         <code>result</code>.
   */
  private
  SpellAction validate(int lineNum, String line,Result result )
  {
    SpellAction action = null;
	String replacementWord;

	if(ignoreAll!=null)ignoreAll.validate(lineNum,line, result);
	if(result.getType() == Result.OK) return new NopAction(null);
	if(userDict!=null)userDict.validate(lineNum, line, result);
	if(result.getType() == Result.OK) return new NopAction(null);
	
	
	if( _changeAllMap.containsKey( result.getOriginalWord() ) )
	{
	  action = new ChangeWordAction(null,
		  		lineNum,
				result.getOffset(),
				_changeAllMap.get( result.getOriginalWord() ),
				result.getOriginalWord());
	}
    
	// ensures visible and selected
	int offset = buffer.getLineStartOffset(lineNum)+result.getOffset()-1;
	Selection s = new Selection.Range(offset,offset+result.getOriginalWord().length());
	area.setSelection(s);
	/* Waiting for fix to bug [ 1990960 ] "Invalid screen line error" when looping in macro
	   Note : should be OK now, as I don't modify the buffer...
	*/
	area.scrollTo(lineNum,result.getOffset()-1,false);

    ValidationDialog.UserAction userAction =
		validationDialog.getUserAction(result.getOriginalWord(),
				result.getSuggestions(),
				ignoreAll!=null && ignoreAll.getAllWords().size()>0,
				history.size()>0);
    if( userAction == validationDialog.CANCEL )
    {
      action = new  CancelAction(null);
    }
    else if( userAction == validationDialog.PREVIOUS )
    {
      action = new  PreviousAction(null);
    }
    else if( userAction == validationDialog.CHANGE_ALL )
    {
      _changeAllMap.put( result.getOriginalWord(),
                         validationDialog.getSelectedWord() );
      action = new ChangeAllAction(null
		  			,lineNum
					,result.getOffset()
					,result.getOriginalWord()
					,validationDialog.getSelectedWord());
    }
    else if( userAction == validationDialog.CHANGE )
    {
      action = new ChangeWordAction(null
		  			,lineNum
					,result.getOffset()
					,result.getOriginalWord()
					,validationDialog.getSelectedWord());
    }
    else if( userAction == validationDialog.IGNORE_ALL )
    {
		replacementWord = result.getOriginalWord();
		if(ignoreAll!=null)ignoreAll.addWord(replacementWord);
		
		action = new IgnoreAllAction(null,replacementWord);
    }
    else if( userAction == validationDialog.IGNORE )
    {
		action = new NopAction(null);
    }
    else if( userAction == validationDialog.ADD )
    {
      replacementWord = result.getOriginalWord();
	  if(userDict!=null)userDict.addWord(replacementWord);
	  action = new AddAction(null,replacementWord);
    }
	return action;
  }


 
  public void setTextArea(TextArea ta){
	  this.area = ta;
	  this.buffer = ta.getBuffer();
	  savedPosition = buffer.createPosition(ta.getCaretPosition());
	  validationDialog = new ValidationDialog(((JEditTextArea)area).getView());
  }

  public void setUserDictionary(WordListValidator valid){
	 userDict = valid;
  }
  
  public void setIgnoreAll(WordListValidator ignoreAll){
	 this.ignoreAll = ignoreAll;
  }
  
  public void setEngine(Engine e){
	  engine = e;
  }

  /**
   */
  public
  boolean spellcheck()
    throws SpellException
	{
		if(engine==null)throw new SpellException("No engine configured");

		boolean confirm = true;
		//to prevent false undo when the user cancelled before any change was made
		boolean changed = false;
		
		history = new ArrayList<SpellAction>();
		
		BufferSpellChecker source = new BufferSpellChecker(area);
		source.start();
		if(ignoreAll!=null)ignoreAll.start();
		if(userDict!=null)userDict.start();
		
		int iLine = 0;
		for(String line = source.getNextLine();confirm && line!=null;line = source.getNextLine(),iLine++)
		{
			if( line.trim().equals( "" ) )continue;
			
			List<Result> results = engine.checkLine( line );
			
			for(int i=0;i<results.size();){
				Result result = results.get(i);
				SpellAction res = this.validate(iLine,line,result);
				
				if(res instanceof CancelAction){
					confirm = false;
					break;
				}else if(res instanceof PreviousAction){
					if(!history.isEmpty()){
						undo();
						if(i==0){
							do{
							assert(iLine>1);//history is not empty so there must be a line before
							iLine--;
							
							//source.getPreviousLine();
							line = source.getPreviousLine();
							results = engine.checkLine(line);
							i = results.size()-1;
							}while(results.size()==0);
						}
						else{
							results = engine.checkLine(line);
							i--;
						}
					}
				}
				else{
					history.add(res);
					i++;
				}
			}
		}

		source.done();
		if(confirm)
		{
			List<ChangeWordAction> changes = new ArrayList<ChangeWordAction>();
			for(SpellAction act:history){
				if(act instanceof ChangeWordAction) changes.add((ChangeWordAction)act);
			}
			if(!changes.isEmpty())source.apply(changes);
		}
		else{
			for(SpellAction act:history){
				act.undo();
			}
		}
		done();

		return confirm;
	}

  
  private void done()
  {
	  //TODO can I save a rectangular selection and restore it in a meaningful way ?
	  area.setCaretPosition(savedPosition.getOffset());
	  /*
	  Waiting for fix to bug [ 1990960 ] "Invalid screen line error" when looping in macro
	  area.scrollToCaret(false);
	*/
  	  if(ignoreAll!=null)ignoreAll.done();
	  if(userDict!=null)userDict.done();
	  userDict=null;
	  ignoreAll=null;
  }
  
  private void undo()
  {
	  SpellAction last = history.remove(history.size()-1);
	  last.undo();
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
