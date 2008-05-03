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

import junit.framework.TestCase;

public class AspellEngineTest extends TestCase{
	private static final String SINK = System.getProperty("user.dir")+File.separator+"tests/sink.sh";
	private static final String WELCOME_THEN_SINK = System.getProperty("user.dir")+File.separator+"tests/welcome_then_sink.sh";
	private static final String ASPELL = "/opt/local/bin/aspell";//System.getProperty("user.dir")+File.separator+"tests/list-dicts.sh";
	private static final String SPELLCHECK_OK = System.getProperty("user.dir")+File.separator+"tests/spellcheck_ok.sh";
	private static final String DOES_NOT_EXIST = System.getProperty("user.dir")+File.separator+"tests/NOT_THERE";
	
	private Throwable throwable;
	protected void setUp(){
		throwable=null;
	}
	
	protected void tearDown(){
		throwable=null;
	}

	public void testCreationDoesNotExist(){
		try{
			Thread t = new Thread(){
				public void run(){
					try{
						AspellEngine ae = new AspellEngine(DOES_NOT_EXIST,new String[]{"pipe"});
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
	
	public void testCreationSink(){
		try{
			Thread t = new Thread(){
				public void run(){
					try{
						AspellEngine ae = new AspellEngine(SINK,new String[]{"pipe"});
						fail("Should not succeed");
					}catch(Throwable t){
						throwable=t;
					}
				}
			};
			t.start();
			try{
				t.join(10000);
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

	public void testCreationWelcome(){
		try{
			Thread t = new Thread(){
				public void run(){
					try{
						AspellEngine ae = new AspellEngine(WELCOME_THEN_SINK,new String[]{"pipe"});
						ae.stop();
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
	
	public void testSpellCheckOK(){
		final List<Result> correct_results = Arrays.asList(new Result[]{
				new Result("*"),new Result("*"),new Result("*"),
				new Result("& Foox 40 16: Fox, Fix, Foxy, Pox, Fax, Fonz, Foods, Fools, Foots, Fops, Cox, Box, Fog, Lox, Fogs, Foss, Coax, Coos, Flax, Flex, Flux, Foes, Fogy, Fork, Fons, Knox, Fobs, Hoax, Roux, Goo's, Fop's, Fox's, Fog's, Food's, Fool's, Foot's, Koo's, Foe's, Flo's, Fob's")
			});
		try{
			final AspellEngine ae = new AspellEngine(SPELLCHECK_OK,new String[]{"pipe","--lang='en'"});
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
			fail("Should not throw : "+t);
		}
	}

	public void testSpellCheckSink(){
		try{
			final AspellEngine ae = new AspellEngine(WELCOME_THEN_SINK,new String[]{"pipe","--lang='en'"});
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
				t.join(10000);
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
