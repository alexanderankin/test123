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

import java.io.File;
import java.util.concurrent.*;
import java.util.*;

//annotations
import org.junit.*;
import static org.junit.Assert.*;


public class AspellEngineTest{
	private static final String SINK = System.getProperty("user.dir")+File.separator+"tests/sink.sh";
	private static final String WELCOME_THEN_SINK = System.getProperty("user.dir")+File.separator+"tests/welcome_then_sink.sh";
	private static final String ASPELL = "/opt/local/bin/aspell";//System.getProperty("user.dir")+File.separator+"tests/list-dicts.sh";
	private static final String SPELLCHECK_OK = System.getProperty("user.dir")+File.separator+"tests/spellcheck_ok.sh";
	private static final String DOES_NOT_EXIST = System.getProperty("user.dir")+File.separator+"tests/NOT_THERE";
	private static final String SPELLCHECK_WIN = System.getProperty("user.dir")+File.separator+"tests/spellcheck_win.sh";
	
	private Throwable throwable;

	@Before
	public void setUp(){
		throwable=null;
	}

	@After
	public void tearDown(){
		throwable=null;
	}

	@Test
	public void testCreationDoesNotExist(){
		System.err.println("testCreationDoesNotExist");
		try{
			Thread t = new Thread(){
				public void run(){
					try{
						AspellEngine ae = new AspellEngine(Arrays.asList(new String[]{DOES_NOT_EXIST,"pipe"}),"UTF-8", true);
						fail("Should not succeed");
					}catch(Throwable t){
						throwable=t;
					}
				}
			};
			t.start();
			try{
				t.join(3000);
			}catch(InterruptedException ie){
				fail("should not be interrupted");
			}
			if(t.isAlive())fail("AspellEngine() should not take too long");
			if(throwable != null)throw throwable;
		}catch(SpellException spe){
			System.out.println("Got the exception, ok");
		}catch(Error e){
			throw e;
		}catch(Throwable t){
			fail("Should not throw : "+t);
		}
	}
	
	@Test
	public void testCreationSink(){
		System.err.println("testCreationSink");
		try{
			Thread t = new Thread(){
				public void run(){
					try{
					AspellEngine ae = new AspellEngine(Arrays.asList(new String[]{SINK,"pipe"}),"UTF-8", true);
						fail("Should not succeed");
					}catch(Throwable t){
						System.err.println("caught exception");
						throwable=t;
					}
				}
			};
			t.start();
			try{
				t.join(30000);
			}catch(InterruptedException ie){
				fail("should not be interrupted");
			}
			if(t.isAlive())fail("AspellEngine() should not take too long");
			if(throwable != null)throw throwable;
		}catch(SpellException spe){
			System.out.println("Got the exception, ok");
		}catch(Error e){
			throw e;
		}catch(Throwable t){
			fail("Should not throw : "+t);
		}
	}

	@Test
	public void testCreationWelcome(){
		System.err.println("testCreationWelcome");
		try{
			Thread t = new Thread(){
				public void run(){
					try{
						AspellEngine ae = new AspellEngine(Arrays.asList(new String[]{WELCOME_THEN_SINK,"pipe"}),"UTF-8", true);
						ae.stop();
					}catch(Throwable t){
						throwable=t;
					}
				}
			};
			t.start();
			try{
				t.join(30000);
			}catch(InterruptedException ie){
				fail("should not be interrupted");
			}
			if(t.isAlive())fail("AspellEngine() should not take too long");
			if(throwable != null)throw throwable;
		}catch(SpellException spe){
				spe.printStackTrace();
				fail("should not throw anything");
		}catch(Error e){
			throw e;
		}catch(Throwable t){
			fail("should not throw "+t);
		}
	}
	
	@Test
	public void testSpellCheckOK(){
		System.err.println("testSpellCheckOK");
		final List<Result> correct_results = Arrays.asList(new Result[]{
				new Result("*"),new Result("*"),new Result("*"),
				new Result("& Foox 40 17: Fox, Fix, Foxy, Pox, Fax, Fonz, Foods, Fools, Foots, Fops, Cox, Box, Fog, Lox, Fogs, Foss, Coax, Coos, Flax, Flex, Flux, Foes, Fogy, Fork, Fons, Knox, Fobs, Hoax, Roux, Goo's, Fop's, Fox's, Fog's, Food's, Fool's, Foot's, Koo's, Foe's, Flo's, Fob's")
			});
		try{
			final AspellEngine ae = new AspellEngine(Arrays.asList(new String[]{SPELLCHECK_OK,"pipe","--lang='en'"}),"UTF-8", true);
			Thread t = new Thread(){
				public void run(){
					try{
						List<Result> results = ae.checkLine("The Quick Brown Foox");
						assertEquals(correct_results,results);
					}catch(Throwable t){
						throwable=t;
					}
				}
			};
			t.start();
			try{
				t.join(30000);
			}catch(InterruptedException ie){
				fail("should not be interrupted");
			}
			if(t.isAlive())fail("checkLine() should not take too long");
			if(throwable != null)throw throwable;
		}catch(SpellException spe){
			spe.printStackTrace();
			fail("should not throw an exception:"+spe); 
		}catch(Error e){
			throw e;
		}catch(Throwable t){
			fail("Should not throw : "+t);
		}
	}

	@Test
	public void testSpellCheckWin(){
		System.err.println("testSpellCheckWin");
		final List<Result> correct_results = Arrays.asList(new Result[]{
				new Result("*"),
				new Result("& Qwick 6 5: Wick, Quick, Vick, Kick, Quack, Quirk"),
				new Result("*"),
				new Result("& Foox 6 17: Fox, Foo, Food, Fool, Foot, Foxy")
			});
		try{
			final AspellEngine ae = new AspellEngine(Arrays.asList(new String[]{SPELLCHECK_WIN,"pipe","--lang='en'"}),"UTF-8", true);
			Thread t = new Thread(){
				public void run(){
					try{
						List<Result> results = ae.checkLine("The Qwick Brown Foox");
						assertEquals(correct_results,results);
					}catch(Throwable t){
						throwable=t;
					}
				}
			};
			t.start();
			try{
				t.join(2000);
			}catch(InterruptedException ie){
				fail("should not be interrupted");
			}
			if(t.isAlive())fail("checkLine() should not take too long");
			if(throwable != null)throw throwable;
		}catch(SpellException spe){
			spe.printStackTrace();
			fail("should not throw an exception:"+spe); 
		}catch(Error e){
			throw e;
		}catch(Throwable t){
			t.printStackTrace(System.err);
			fail("Should not throw : "+t);
		}
	}

	@Test
	public void testSpellCheckSink(){
		System.err.println("testSpellCheckSink");
		try{
			final AspellEngine ae = new AspellEngine(Arrays.asList(new String[]{WELCOME_THEN_SINK,"pipe","--lang='en'"}),"UTF-8", true);
			Thread t = new Thread(){
				public void run(){
					try{
						List<Result> results = ae.checkLine("The Quick Brown Foox");
						fail("should not return");
					}catch(Throwable t){
						throwable=t;
					}
				}
			};
			t.start();
			try{
				t.join(30000);
			}catch(InterruptedException ie){
				fail("should not be interrupted");
			}
			if(t.isAlive())fail("checkLine() should not take too long");
			if(throwable != null)throw throwable;
		}catch(SpellException spe){
			System.out.println("ok, got the exception");
		}catch(Error e){
			throw e;
		}catch(Throwable t){
			fail("Should not throw : "+t);
		}
	}
}
