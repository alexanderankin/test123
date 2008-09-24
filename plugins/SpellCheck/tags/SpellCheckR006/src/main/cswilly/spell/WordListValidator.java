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

import java.util.List;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import org.gjt.sp.util.Log;


/**
 * A word-list based validator.
 * can be load and saved.
 *
 */ 
public class WordListValidator implements Validator
{
	private TreeSet<String> words;

	private boolean dirty;

	public WordListValidator(){
		words = new TreeSet<String>();
	}

	public void addWord(String word){
		if(!words.contains(word)){
			words.add(word);
			dirty = true;
		}
	}
	
	public void removeWord(String word){
		if(words.contains(word)){
			words.remove(word);
			dirty = true;
		}
	}
	
	/**
	 * @return an immutable collection, so don't try to edit...
	 */
	public Collection<String> getAllWords(){
		return Collections.unmodifiableCollection(words);
	}
	
  /**
   * Validate a result  of a spell
   * check.
   *<p>
   * @param	lineNum	index of the line in the text/buffer/file whatever
   * @param line String with a line of words that are to be corrected
   * @param r result of a spell check
   * @return confirmed (allways true except when error token)
   */
  public
  final boolean validate( int lineNum, String line, Result r ){
	  if(r.getType() == Result.OK)return true;
	  else if(r.getType() == Result.ERROR){
		  return false;
	  }else{
		  if(words.contains(r.getOriginalWord())){
			  //the word is in the word list
			  Log.log(Log.DEBUG,this,"the word "+r.getOriginalWord()+" is in the word list");
			  r.setType(Result.OK);
		  }
	  }
	  return true;
  }
  
   public final void start(){}

   public final void done(){}
   
   
   public boolean isDirty(){
	   return dirty;
   }
   
   
   public void load(File f) throws IOException{
	   words.clear();
	   if(!f.exists())throw new IOException("file "+f.getPath()+" doesn't exist");
	   BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
	   Pattern empty = Pattern.compile("^\\s*$");
	   Pattern comment = Pattern.compile("^\\s*#.*$");
	   for(String line = reader.readLine();line!=null;line = reader.readLine()){
		   //skip comments and empty lines
		   if(!empty.matcher(line).matches()
			   && !comment.matcher(line).matches())
		   			addWord(line.trim());
	   }
	   dirty = false;
   }
   /**
    * save the dictionary in given file
	* @param	f	file to write to. It will be silently overwritten.
	* @throws	IOException	if something occurs. One can call save again without loss of data.
    */
   public void save(File f) throws IOException{
	   if(!isDirty() && f.exists())return;//to recreate a file if necessary
	   //if(!f.exists())throw new IOException("file "+f.getPath()+" doesn't exist");
	   Writer writer = new OutputStreamWriter(new FileOutputStream(f),"UTF-8");
	   String header = "# this is a user dictionary for the SpellCheck plugin,\n"
	   				  +"# keep the formatting of one word per line and everything should work\n."
					  +"# comments are ignored and not re-created so beware...\n";
	   header+="#:";
	   header+="mode=text";
	   header+=":";
	   header+="encoding=UTF-8";
	   header+=":\n";
	   writer.write(header);
	   for(String word: words){
		  writer.write(word+"\n");
	   }
	   writer.flush();
	   writer.close();
	   dirty = false;
   }   
}
