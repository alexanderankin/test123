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

///}}}

import cswilly.spell.SpellException;
import cswilly.spell.Engine;
import cswilly.spell.Result;

import org.gjt.sp.jedit.testframework.TestUtils;
import static org.gjt.sp.jedit.testframework.TestUtils.*;
import static cswilly.jeditPlugins.spell.TestUtils.ENV_ASPELL_EXE;


/**
 * Test the functions of AspellEngineManager
 */
public class AspellEngineManagerTest{
	private static String testsDir;
	private static String exePath;
	
	@BeforeClass
	public static void setUpjEdit(){
		testsDir = System.getProperty(ENV_TESTS_DIR);
		assertTrue("Forgot to set env. variable '"+ENV_TESTS_DIR+"'",testsDir!=null);
		exePath = System.getProperty(ENV_ASPELL_EXE);
		assertTrue("Forgot to set env. variable '"+ENV_ASPELL_EXE+"'",exePath!=null);

		TestUtils.beforeClass();
	}

	@AfterClass
	public static  void tearDownjEdit(){
		TestUtils.afterClass();
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
	public void testGetEngine(){
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		AspellEngineManager manager = new AspellEngineManager();
		Engine e = null;
		try{
			e = manager.getEngine("sgml","en",true);
			System.err.println("testGetEngine got engine:"+e);
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		assertTrue("getEngine() returned null",e!=null);
	}

	@Test
	public void testModeParam(){
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(AspellEngineManager.ASPELL_MARKUP_MODE_PROP,"aspellManualMarkupMode");
		AspellEngineManager manager = new AspellEngineManager();
		Engine e = null;
		try{
			e = manager.getEngine("text","en",true);
			System.err.println("testMode text got engine:"+e);
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		assertTrue("getEngine() returned null",e!=null);		
		List<Result> l = null;
		try{
			l = e.checkLine("<helo>world</helo>");
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		assertEquals(2,l.size());

		try{
			e = manager.getEngine("xml","en",true);
			System.err.println("xml: got engine:"+e);
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		try{
			l = e.checkLine("<helo>world</helo>");
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		assertEquals(0,l.size());
	}

	@Test
	public void testLanguageParam(){
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(AspellEngineManager.ASPELL_MARKUP_MODE_PROP,"aspellManualMarkupMode");
		AspellEngineManager manager = new AspellEngineManager();
		Engine e = null;
		try{
			e = manager.getEngine("text","en",true);
			System.err.println("lang got engine:"+e);
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		assertTrue("getEngine() returned null",e!=null);
		
		List<Result> l = null;
		try{
			l = e.checkLine("écriture");
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		assertEquals(1,l.size());

		try{
			e = manager.getEngine("text","fr",true);
			System.err.println("fr got engine:"+e);
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		try{
			l = e.checkLine("écriture");
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		assertEquals(0,l.size());
	}

	@Test
	public void testModeChange(){
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(AspellEngineManager.ASPELL_MARKUP_MODE_PROP,AspellEngineManager.AspellMarkupMode.MANUAL_MARKUP_MODE.toString());
		AspellEngineManager manager = new AspellEngineManager();
		Engine e = null;
		try{
			e = manager.getEngine("","en",true);
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		
		
		assertTrue("getEngine() returned null",e!=null);

		jEdit.setProperty(AspellEngineManager.ASPELL_MARKUP_MODE_PROP,AspellEngineManager.AspellMarkupMode.NO_MARKUP_MODE.toString());
		Engine e2 = null;
		try{
			e2 = manager.getEngine("","en",true);
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}

		assertTrue("didn't update",e!=e2);
	}

	@Test
	public void testStop(){
		jEdit.setProperty(AspellEngineManager.ASPELL_EXE_PROP,exePath);
		jEdit.setProperty(AspellEngineManager.ASPELL_MARKUP_MODE_PROP,"aspellManualMarkupMode");
		AspellEngineManager manager = new AspellEngineManager();
		Engine e = null;
		try{
			e = manager.getEngine("","en",true);
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		assertTrue("getEngine() returned null",e!=null);
		manager.stop();
		
		Engine e2 = null;
		try{
			e2 = manager.getEngine("","en",true);
		}catch(SpellException spe){
			spe.printStackTrace(System.err);
			fail("shouldn't throw"+spe.toString());
		}
		assertTrue("getEngine() returned stopped engine",e!=e2);
	}
}
