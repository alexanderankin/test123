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

import cswilly.jeditPlugins.spell.TestUtils;

public class FutureListModesTest extends TestCase{
	private static final String SINK = System.getProperty(TestUtils.ENV_TESTS_DIR)+File.separator+"sink.sh";
	private static final String OK = System.getProperty(TestUtils.ENV_TESTS_DIR)+File.separator+"list-modes.sh";
	private static final String UNKNOWN = System.getProperty(TestUtils.ENV_TESTS_DIR)+File.separator+"unknown_action.sh";
	
	
	@Test
	public void testTimeout(){
		FutureListModes ft = new FutureListModes(SINK);
		try{
			ft.get(2,TimeUnit.SECONDS);
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
		final FutureListModes ft = new FutureListModes(SINK);
		new Thread(){
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
		}.start();
		try{
		Thread.sleep(200);
		}catch(InterruptedException ie){}
		ft.cancel(true);
	}

	@Test
	public void testOK(){
		final FutureListModes ft = new FutureListModes(OK);
		new Thread(){
			public void run(){
				try{
					Map<String,String> modes = ft.get(20,TimeUnit.SECONDS);
					assertEquals(11,modes.size());
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
		}.start();
	}

	@Test
	public void testUnknown(){
		FutureListModes ft = new FutureListModes(UNKNOWN);
		try{
			Map<String,String> modes = ft.get(2,TimeUnit.SECONDS);
		}catch(CancellationException ce){
			fail("didn't cancel");
		}catch(InterruptedException ie){
			fail("didn't interrupt");
		}catch(ExecutionException ee){
			ee.printStackTrace(System.err);
			fail("don't throw execution exception");
		}catch(TimeoutException te){
			fail("don't timeout");
		}
		finally{
			ft.cancel(true);
		}
	}

}
