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

import java.io.*;
import java.util.*;


//annotations
import org.junit.*;
import static org.junit.Assert.*;

import static cswilly.jeditPlugins.spell.TestUtils.ENV_TESTS_DIR;


public class WordListValidatorTest{
	private static final String OK = System.getProperty(ENV_TESTS_DIR)+File.separator+"word-list-ok.dict";
	private static final String EMPTY = System.getProperty(ENV_TESTS_DIR)+File.separator+"word-list-empty.dict";
	private static final String EMPTY_LINE = System.getProperty(ENV_TESTS_DIR)+File.separator+"word-list-empty-line.dict";
	private static final String DOES_NOT_EXIST = System.getProperty(ENV_TESTS_DIR)+File.separator+"NOT_THERE.dict";
	
	@Test
	public void testValidate(){
		WordListValidator dict = new WordListValidator();
		ArrayList<Result> oldRes = new ArrayList(3);
		
		oldRes.add(new Result(0,Result.NONE,null,"currry"));
		oldRes.add(new Result(7,Result.SUGGESTION,Arrays.asList(new String[]{"carry"}),"carri"));
		oldRes.add(new Result(13,Result.OK,null,"curry"));
		
		List<Result> res = (List<Result>)oldRes.clone();
		
		dict.start();
		assertTrue(dict.validate(0,"currry carri curry",res));
		assertEquals(2,res.size());
		assertEquals(res.get(0),oldRes.get(0));
		assertEquals(res.get(1),oldRes.get(1));
		
		assertTrue(!dict.validate(0,"currry carri curry",Arrays.asList(new Result[]{new Result(0,Result.ERROR,null,null)})));
		dict.done();
	}
	
	@Test
	public void testSave(){
	}
	
	@Test
	public void testLoad(){
		WordListValidator dict = new WordListValidator();
				
		assertEquals(0,dict.getAllWords().size());
		
		//normal file
		File f = new File(OK);
		try{
			dict.load(f);
		}catch(IOException ioe){
			ioe.printStackTrace(System.err);
			fail("shouldn't throw an exception");
		}
		
		assertEquals(3,dict.getAllWords().size());

		//empty file
		f = new File(EMPTY);
		try{
			dict.load(f);
		}catch(IOException ioe){
			ioe.printStackTrace(System.err);
			fail("shouldn't throw an exception");
		}

		assertEquals(0,dict.getAllWords().size());
		
		//whitespace and empty line
		f = new File(EMPTY_LINE);
		try{
			dict.load(f);
		}catch(IOException ioe){
			ioe.printStackTrace(System.err);
			fail("shouldn't throw an exception");
		}
		assertEquals(2,dict.getAllWords().size());
		assertEquals("jEdit",dict.getAllWords().iterator().next());

		//inexistant file
		f = new File(DOES_NOT_EXIST);
		try{
			dict.load(f);
			fail("should throw an exception");
		}catch(IOException ioe){}

		assertEquals(0,dict.getAllWords().size());
	}
}
