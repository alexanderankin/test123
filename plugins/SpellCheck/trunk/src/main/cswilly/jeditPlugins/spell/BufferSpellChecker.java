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
import cswilly.spell.SpellSource;
import cswilly.spell.SpellEffector;
import cswilly.spell.Validator;
import cswilly.spell.ChangeWordAction;


import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;


import java.util.List;
import java.util.Collections;

/**
 * Performs spell-checking on a jEdit Buffer.
 * Read lock during spell-checking, then compound edit to apply modifications
 * @see	BufferDialogValidator	used to highlight the word currently checked, and scroll to it.
 * 
 */
public class BufferSpellChecker implements SpellSource, SpellEffector{
  
	private TextArea area;
	private JEditBuffer input;
	
	//state
	private Selection[] selections;
	private int iSelection;
	private int iLine;
	private boolean lastWasNext;
	
  public BufferSpellChecker( TextArea area )
  {
	  this.area = area;
  }

  
  /**
   * @return <i>true</i> if file completely checked and <i>false</i> if the user
   * interupted the checking.
   */
  public
  void start()
	{
		input = area.getBuffer();

		if(area.getSelectionCount() == 0){
			area.setSelection(new Selection.Range(0,input.getLength()));
		}
		
		selections = area.getSelection();

		input.readLock();
		
		assert(selections.length>0);
		
		iSelection = 0;
		iLine = selections[0].getStartLine();
		lastWasNext=true;
	}
	
	public String getNextLine(){
		if(!lastWasNext)iLine++;
		lastWasNext=true;

		if(iSelection>=selections.length)return null;
		
		Selection sel = selections[iSelection];
		if(iLine>sel.getEndLine())
		{
			iSelection++;
			if(iSelection>=selections.length)return null;
			sel = selections[iSelection];
			iLine = sel.getStartLine();
		}

		String line = input.getLineText(iLine);
		iLine++;
		if(contextFilter())return line;
		else return getNextLine();
	}
	
	private boolean contextFilter(){

		DefaultTokenHandler handler = new DefaultTokenHandler();
		input.markTokens(iLine-1,handler);
		
		for(Token token = handler.getTokens();token!=null;token=token.next){
			switch(token.id){
				case Token.COMMENT1 :
				case Token.COMMENT2 :
				case Token.COMMENT3 :
				case Token.COMMENT4 :
				case Token.LITERAL1 :
				case Token.LITERAL2 :
				case Token.LITERAL3 :
				case Token.LITERAL4 :
					return true;
			}
		}
		return false;
	}
	
	public int getLineNumber(){
		if(lastWasNext)return iLine-1;
		else return iLine;
	}
	
	public String getPreviousLine(){
		if(lastWasNext)iLine--;
		lastWasNext=false;
		
		Selection sel=null;
		if(iSelection>selections.length
			|| iLine<=selections[iSelection].getStartLine())
		{
			iSelection--;
			sel = selections[iSelection];
			iLine = sel.getEndLine()+1;
		}

		iLine--;
		String line = input.getLineText(iLine);

		return line;
	}
	
	public void done(){
		if(input!=null)input.readUnlock();
	}

  /**
   * apply all changes in a compoundEdit
   * @param	results	list of Changes to apply
   */
  public
  void apply(List<ChangeWordAction> results)
    throws SpellException
	{
		if(results.isEmpty())return;
		if(input == null)throw new SpellException("input buffer is null");
		try
		{
			input.beginCompoundEdit();
			for(int i=results.size()-1;i>=0;i--){
				ChangeWordAction res=results.get(i);
				int lineOffset = input.getLineStartOffset(res.line);
				int originalIndex = lineOffset+res.offset-1;//offset starts at 1 for aspell
				String newWord = res.newWord;
				Log.log(Log.DEBUG,this,"o="+originalIndex+",n="+newWord);
				input.remove(originalIndex,res.originalWord.length());
				input.insert(originalIndex,newWord);
			}
		}
		catch( Exception e )
		{
			if( e instanceof SpellException )
				throw (SpellException)e;
			else
				throw new SpellException( "Error applying changes", e );
		}                 
		finally{                       
				input.endCompoundEdit();
				input=null;                    
		}
	}
	
	public Validator getValidator(){
		return new BufferValidator();
	}
	
	private class BufferValidator implements Validator{
		
		public
		boolean validate( int lineNum, String line, Result result ){
			if(selections==null)throw new IllegalStateException("validate not between BufferSpellChecker.start() and done()");
			int lineOffset = input.getLineStartOffset(lineNum)+result.getOffset()-1;
			int endOffset = lineOffset+result.getOriginalWord().length();
			
			//Log.log(Log.DEBUG,BufferValidator.class,"selection count:"+selections.length);
			for(int iSelection=0;iSelection<selections.length;iSelection++){
				/*Log.log(Log.DEBUG,BufferValidator.class,"selection:"+selections[iSelection]
					+selections[iSelection].getStartLine()
					+" "
					+ selections[iSelection].getEndLine()
					+" "
					+ selections[iSelection].getStart(input,lineNum)
					+" "
					+selections[iSelection].getEnd(input,lineNum));
				*/
				if(selections[iSelection].getStartLine()<=lineNum
					&& selections[iSelection].getEndLine()>=lineNum
					&& lineOffset>=selections[iSelection].getStart(input,lineNum)
					&& endOffset<=selections[iSelection].getEnd(input,lineNum)
				){
				//Log.log(Log.DEBUG,BufferSpellChecker.class,result.getOriginalWord()+" in selection");
				Selection s = new Selection.Range(lineOffset,endOffset);
				area.setSelection(s);
				area.scrollTo(lineNum,result.getOffset()-1,false);
				return true;
				}
			}
			
			//out of selection
			//Log.log(Log.DEBUG,BufferSpellChecker.class,result.getOriginalWord()+" not in selection");
			result.setType(Result.OK);
			return true;
		}
		
		public void start(){}
		
		public void done(){
			selections = null;
			iSelection = -1;
			iLine = -1;
		}
	}
}
