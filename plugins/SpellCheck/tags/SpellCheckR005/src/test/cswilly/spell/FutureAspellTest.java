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
//annotations
import org.junit.*;
import org.junit.Test;


public class FutureAspellTest extends TestCase{
	private static final String SINK = System.getProperty("user.dir")+File.separator+"tests/sink.sh";
	private static final String OK = System.getProperty("user.dir")+File.separator+"tests/list-dicts.sh";
	
	@Test
	public void testTimeout(){
		FutureAspell<Object> ft = new MockAspell(SINK,new MockAspell.MockProcessor(null));
		try{
			Object dicts = ft.get(2,TimeUnit.SECONDS);
			fail("shouldn't succeed");
		}catch(CancellationException ce){
			fail("didn't cancel");
		}catch(InterruptedException ie){
			fail("didn't interrupt");
		}catch(ExecutionException ee){
			fail("don't throw execution exception");
		}catch(TimeoutException te){
			System.out.println("OK for testTimeout");
			ft.cancel(true);
		}
		finally{
			ft.cancel(true);
		}
	}
	
	@Test
	public void testCancel(){
		final FutureAspell<Object> ft = new MockAspell(SINK,new MockAspell.MockProcessor(null));
		Thread t=new Thread(){
			public void run(){
				try{
					ft.get(20,TimeUnit.SECONDS);
					fail("shouldn't succeed");
				}catch(CancellationException ce){
					System.out.println("OK, got cancel");
				}catch(InterruptedException ie){
					fail("didn't interrupt");
				}catch(ExecutionException ee){
					fail("don't throw execution exception");
				}catch(TimeoutException te){
					fail("should cancel before timeout");
				}finally{
					ft.cancel(true);
				}
			}
		};
		t.start();
		try{
			Thread.sleep(200);
		}catch(InterruptedException ie){}
		ft.cancel(true);
		try{
			t.join(200);
		}catch(InterruptedException ie){
			fail("shouldn't be interrupted");
		}
		if(t.isAlive()){
			ft.cancel(true);
			fail("shouldn't take to long");
		}

	}
	
	@Test
	public void testOK(){
		final Object expected=new Object();
		final MockAspell.MockProcessor processor = new MockAspell.MockProcessor(expected);
		final FutureAspell<Object> ft = new MockAspell(OK,processor);
		Thread t =new Thread(){
			public void run(){
				try{
					Object res = ft.get(20,TimeUnit.SECONDS);
					System.out.println("fini");
					assertEquals("the object returned by the processor is not the object returned",expected,res);
					assertEquals("some lines missed",42,processor.nbAccu);
					ft.cancel(true);
				}catch(CancellationException ce){
					fail("didn't cancel");
				}catch(InterruptedException ie){
					fail("didn't interrupt");
				}catch(ExecutionException ee){
					fail("don't throw execution exception");
				}catch(TimeoutException te){
					fail("should not timeout");
				}finally{
					ft.cancel(true);
				}
			}
		};
		t.start();
		try{
			t.join(2000);
		}catch(InterruptedException ie){
			fail("shouldn't be interrupted");
		}
		if(t.isAlive()){
			ft.cancel(true);
			fail("shouldn't take to long");
		}
	}
	
	
	@Test
	public void testException(){
		Object expected=new Object();
		final FutureAspell<Object> ft = new MockAspell(OK,new MockAspell.MockExceptionProcessor());
		Thread t= new Thread(){
			public void run(){
				try{
					Object res = ft.get(20,TimeUnit.SECONDS);
					fail("Should not succeed");
				}catch(CancellationException ce){
					fail("didn't cancel");
				}catch(InterruptedException ie){
					fail("didn't interrupt");
				}catch(ExecutionException ee){
					assertNotNull(ee.getCause());
					assertEquals("Should be a SpellException",SpellException.class,ee.getCause().getClass());
				}catch(TimeoutException te){
					fail("should not timeout");
				}finally{
					ft.cancel(true);
				}
			}
		};
		t.start();
		try{
			t.join(2000);
		}catch(InterruptedException ie){
			fail("shouldn't be interrupted");
		}
		if(t.isAlive()){
			ft.cancel(true);
			fail("shouldn't take to long");
		}
	}
	
	
	private static class MockAspell extends FutureAspell<Object>{
		
		MockAspell(String aspellExeFilename,Processor<Object> processor){
			super(Arrays.asList(new String[]{aspellExeFilename,"dump","dicts"}),processor);
		}
		
		/**
		* Allways accept lines
		* return a given result.
		*/
		static class MockProcessor implements Processor<Object>{
			private Object res;
			private int nbAccu;
			MockProcessor(Object res){
				this.res = res;
				nbAccu=0;
			}
			
			public void accumulate(String line) throws SpellException{nbAccu++;}
			
			public Object done(){
				return res;
			}		
		}
		/**
		* Refuse second line
		*/
		static class MockExceptionProcessor implements Processor<Object>{
			private int nbAccu;
			
			MockExceptionProcessor(){
				nbAccu=0;
			}
			
			public void accumulate(String line) throws SpellException{
				nbAccu++;
				if(nbAccu==2)throw new SpellException("MockExceptionProcessor");
			}
			
			public Object done(){
				return null;
			}
		}
	}
}
