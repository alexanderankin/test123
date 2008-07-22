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
import cswilly.spell.ChangeWordAction;


import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

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
		return line;
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
		selections = null;
		iSelection = -1;
		iLine = -1;
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
}
