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
import javax.swing.*;
import java.util.*;

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

import errorlist.*;

import cswilly.spell.SpellException;
import cswilly.spell.WordListValidator;
import cswilly.spell.Result;

///}}}



import static org.gjt.sp.jedit.testframework.TestUtils.*;
import org.gjt.sp.jedit.testframework.TestUtils;
import org.gjt.sp.jedit.testframework.*;
import static cswilly.jeditPlugins.spell.TestUtils.ENV_ASPELL_EXE;


/**
 * Test the functions of ErrorListValidator
 *	- test 
 *	- test commit/undo
 */
public class ErrorListValidatorTest{
	
	@BeforeClass
	public static void setUpjEdit(){
		TestUtils.setUpjEdit();
	}

	@AfterClass
	public static  void tearDownjEdit(){
		TestUtils.tearDownjEdit();
	}

	@Before
	public void beforeTest(){
		jEdit.getPlugin(SpellCheckPlugin.class.getName()).getPluginJAR().activatePluginIfNecessary();
	}

	@After
	public void afterTest(){
		jEdit.getPlugin(SpellCheckPlugin.class.getName()).getPluginJAR().deactivatePlugin(false);
	}

	@Test
	public void testNormal(){
		final ErrorListValidator valid = new ErrorListValidator("ErrorListValidatorTest");
		
		TestUtils.jEditFrame().menuItemWithPath("Plugins").click();
		TestUtils.jEditFrame().menuItemWithPath("Plugins","ErrorList").click();
		TestUtils.jEditFrame().menuItemWithPath("Plugins","ErrorList","Error List").click();

		
		String path  = "/My/Test/Path";
		valid.setPath(path);
		valid.start();
		final List<Result> res = new LinkedList<Result>();
		res.add(new Result(1,Result.OK,null,"The"));
		res.add(new Result(5,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(11,Result.SUGGESTION,Arrays.asList(new String[]{"wick","quick","Vick"}),"qwick"));
		res.add(new Result(17,Result.SUGGESTION,Arrays.asList(new String[]{"Fox","fox","Foxes"}),"foxe"));
		
		ErrorSource.Error[] expectedErrors = new ErrorSource.Error[3];
		expectedErrors[0] = new DefaultErrorSource.DefaultError(valid, ErrorSource.WARNING, path,0, 4, 4+5, "");
		expectedErrors[1] = new DefaultErrorSource.DefaultError(valid, ErrorSource.WARNING, path,0, 10, 10+5, "");
		expectedErrors[2] = new DefaultErrorSource.DefaultError(valid, ErrorSource.WARNING, path,0, 16, 16+4, "");

		for(Result r:res){
			assertTrue(valid.validate(0,"The quick brown foxe",r));
		}
		valid.done();
		
		
		waitForMessage(new ErrorSourceUpdate(valid,ErrorSourceUpdate.ERROR_ADDED,null),10000);

		
		ErrorSource[] sources = ErrorSource.getErrorSources();
		ErrorSource spellError = null;
		for(int i=0;i<sources.length;i++){
			if(sources[i]==valid){
				spellError = sources[i];
			}
		}
		
		try{Thread.sleep(2000);}catch(InterruptedException ie){}
		assertNotNull(spellError);
		ErrorSource.Error[] errors = spellError.getFileErrors(path);
		for(int i=0;i<errors.length;i++){
			System.err.println(errors[i].toString());
		}
		
		assertEquals(3,errors.length);

		
		for(int i=0;i<expectedErrors.length;i++){
			assertEquals(expectedErrors[i].getLineNumber(),errors[i].getLineNumber());
			assertEquals(expectedErrors[i].getStartOffset(),errors[i].getStartOffset());
			assertEquals(expectedErrors[i].getEndOffset(),errors[i].getEndOffset());
			assertEquals(path,errors[i].getFilePath());
		}
		
		ErrorSource.unregisterErrorSource(spellError);
	}

	
	public static void waitForMessage(final ErrorSourceUpdate msg,long timeout){
		//final AtomicReference<Boolean> atr = new AtomicReference(Boolean.FALSE);
		EBComponent eb = new EBComponent(){
			public void handleMessage(EBMessage ebm){
				if(ebm instanceof ErrorSourceUpdate){
					//System.err.println("got message : "+ebm);
//					if(msg.getSource()==null || ebm.getSource() == msg.getSource()){
						if(((ErrorSourceUpdate)ebm).getWhat().equals(msg.getWhat())){
							synchronized(this){
								notifyAll();
							}
						}
//					}
				}
			}
		};
		
		EditBus.addToBus(eb);
		try{
			synchronized(eb){
				eb.wait(timeout);
			}
		}catch(InterruptedException ie){	
			fail("didn't receive message");
		}finally{
			EditBus.removeFromBus(eb);
		}
	}
}
