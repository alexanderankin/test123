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


//{{{ Imports

import java.io.*;
import java.util.concurrent.atomic.*;
import java.util.*;
import javax.swing.*;

//{{{ 	jEdit
import org.gjt.sp.jedit.*;

//}}}

//{{{	junit
import org.junit.*;
import static org.junit.Assert.*;
//}}}

//{{{	FEST...
import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
//}}}

import cswilly.spell.Result;
import cswilly.spell.SpellException;
import cswilly.spell.MockEngine;
import cswilly.spell.WordListValidator;

import errorlist.*;

import static org.gjt.sp.jedit.testframework.TestUtils.*;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.*;
import static cswilly.jeditPlugins.spell.TestUtils.ENV_ASPELL_EXE;

///}}}



/**
 * Test the functions of ErrorListValidator
 *	- test 
 *	- test commit/undo
 */
public class ErrorListSpellCheckerTest{
	
	@BeforeClass
	public static void setUpjEdit(){
		TestUtils.beforeClass();
	}

	@AfterClass
	public static  void tearDownjEdit(){
		TestUtils.afterClass();
	}

	@Before
	public void beforeTest(){
		jEdit.getPlugin(SpellCheckPlugin.class.getName()).getPluginJAR().activatePluginIfNecessary();
		TestUtils.jEditFrame().menuItemWithPath("Plugins").click();
		TestUtils.jEditFrame().menuItemWithPath("Plugins","ErrorList").click();
		TestUtils.jEditFrame().menuItemWithPath("Plugins","ErrorList","Error List").click();
	}

	@After
	public void afterTest(){
		jEdit.getPlugin(SpellCheckPlugin.class.getName()).getPluginJAR().deactivatePlugin(false);
	}

	@Test
	public void testNormal(){
		final ErrorListSpellChecker checker = new ErrorListSpellChecker();
		
		final View view = TestUtils.view();
		jEdit.newFile(view);
		try{Thread.sleep(1000);}catch(InterruptedException ie){}//let new file be created
		final Buffer buff = view.getBuffer();
		buff.setProperty(SpellCheckPlugin.BUFFER_LANGUAGE_PROP,"en");
		buff.insert(0,"The qwick qwick foxe.\nqwick The pwett.");
		
		MockEngine eng = new MockEngine();
		
		List<Result> res = new LinkedList<Result>();
		res.add(new Result(1,Result.OK,null,"The"));
		res.add(new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(11,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(17,Result.SUGGESTION,Arrays.asList(new String[]{"Fox","fox","Foxes"}),"foxe"));
		eng.addResults(res);
		
		res = new LinkedList<Result>();
		res.add(new Result(1,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(6,Result.OK,null,"The"));
		res.add(new Result(11,Result.SUGGESTION,Arrays.asList(new String[]{"pouet","pwet","poete"}),"pwett"));
		eng.addResults(res);
		
		checker.setSpellEngine(eng);
		checker.setTextArea(view.getTextArea());
		try{
			assertTrue(checker.spellcheck());
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail(spe.toString());
		}
		
		ErrorListValidatorTest.waitForMessage(new ErrorSourceUpdate(new DefaultErrorSource("Dummy"),ErrorSourceUpdate.ERROR_ADDED,null),10000);

		
		ErrorSource[] sources = ErrorSource.getErrorSources();
		ErrorSource spellError = null;
		for(int i=0;i<sources.length;i++){
			if(sources[i] instanceof ErrorListValidator){
				spellError = sources[i];
			}
		}
		
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		assertNotNull(spellError);
		ErrorSource.Error[] errors = spellError.getFileErrors(buff.getPath());
		assertNotNull(errors);
		for(int i=0;i<errors.length;i++){
			System.err.println(errors[i].toString());
		}
		
		assertEquals(5,errors.length);
		checker.unload();
	}

	@Test
	public void testAdditional(){
		final ErrorListSpellChecker checker = new ErrorListSpellChecker();
		
		final View view = TestUtils.view();
		jEdit.newFile(view);
		try{Thread.sleep(1000);}catch(InterruptedException ie){}//let new file be created
		final Buffer buff = view.getBuffer();
		buff.setProperty(SpellCheckPlugin.BUFFER_LANGUAGE_PROP,"en");
		buff.insert(0,"The qwick qwick foxe.\nqwick The pwett.");
		
		MockEngine eng = new MockEngine();
		
		List<Result> res = new LinkedList<Result>();
		res.add(new Result(1,Result.OK,null,"The"));
		res.add(new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(11,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(17,Result.SUGGESTION,Arrays.asList(new String[]{"Fox","fox","Foxes"}),"foxe"));
		eng.addResults(res);
		
		res = new LinkedList<Result>();
		res.add(new Result(1,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(6,Result.OK,null,"The"));
		res.add(new Result(11,Result.SUGGESTION,Arrays.asList(new String[]{"pouet","pwet","poete"}),"pwett"));
		eng.addResults(res);
		
		checker.setSpellEngine(eng);
		
		WordListValidator valid = new WordListValidator();
		valid.addWord("qwick");
		valid.addWord("pwett");
		
		checker.setValidator(valid);
		checker.setTextArea(view.getTextArea());
		try{
			assertTrue(checker.spellcheck());
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail(spe.toString());
		}
		ErrorListValidatorTest.waitForMessage(new ErrorSourceUpdate(new DefaultErrorSource("Dummy"),ErrorSourceUpdate.ERROR_ADDED,null),10000);

		
		ErrorSource[] sources = ErrorSource.getErrorSources();
		ErrorSource spellError = null;
		for(int i=0;i<sources.length;i++){
			if(sources[i] instanceof ErrorListValidator){
				spellError = sources[i];
			}
		}
		
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		assertNotNull(spellError);
		ErrorSource.Error[] errors = spellError.getFileErrors(buff.getPath());
		for(int i=0;i<errors.length;i++){
			System.err.println(errors[i].toString());
		}
		
		assertEquals(1,errors.length);
		checker.unload();
	}
}
