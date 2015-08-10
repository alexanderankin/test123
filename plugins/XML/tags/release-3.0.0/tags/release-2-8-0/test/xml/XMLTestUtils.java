/*
 * XMLTestUtils.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml;

// {{{ jUnit imports 
import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;

import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.*;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;
import org.fest.swing.exception.WaitTimedOutError;

import static org.fest.assertions.Assertions.*;

import org.gjt.sp.jedit.testframework.Log;
import org.gjt.sp.jedit.testframework.TestUtils;

// }}}

import java.io.*;
import java.awt.Dialog;
import javax.swing.JWindow;
import java.awt.Component;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.PluginUpdate;

import static org.gjt.sp.jedit.testframework.EBFixture.*;
import static org.gjt.sp.jedit.testframework.TestUtils.*;

/**
 * a handful of utility methods.
 * Some of them may be moved to the test-framework as they prove useful
 * $Id$
 */
public class XMLTestUtils{
	
	/**
	 * test each byte from the streams for equality.
	 * Only stops when one of the streams is exhausted, so don't reuse them.
	 *
	 * @param	expectedIn	the reference stream
	 * @param	actualIn	the tested stream
	 */
	public static void assertInputStreamEquals(InputStream expectedIn, InputStream actualIn)
	throws IOException
	{
		try{
			for(int i=0,e=0,a=0;a!=-1 && e!=-1;i++){
				a=actualIn.read();
				e=expectedIn.read();
				assertEquals("at byte "+i, a,e);
			}
		}finally{
			expectedIn.close();
			actualIn.close();
		}
	}
	
	/**
	 * test each byte from the readers for equality.
	 * Only stops when one of the readers is exhausted, so don't reuse them.
	 *
	 * @param	expectedIn	the reference reader
	 * @param	actualIn	the tested reader
	 */
	public static void assertReaderEquals(Reader expectedIn, Reader actualIn)
	throws IOException
	{
		try{
			for(int i=0,e=0,a=0;a!=-1 && e!=-1;i++){
				a=actualIn.read();
				e=expectedIn.read();
				assertEquals("at char "+i, a,e);
			}
		}finally{
			expectedIn.close();
			actualIn.close();
		}
	}
	
	public static void parseAndWait(){
		doInBetween(
			new Runnable(){
				public void run(){
					action("sidekick-parse");
				}
			},
			messageOfClassCondition(sidekick.SideKickUpdate.class)
			,10000);
	}

	public static void gotoPositionAndWait(int pos){
		gotoPosition(pos);
		Pause.pause(500);
	}

	/**
	 * activates, deactivates and activates again the given plugin.
	 * this is useful to test that whatever settings it kept are well
	 * persisted.
	 * 
	 * @param	editPluginClass	the main class of the plugin to deactivate and reactivate
	 */
	public static final void reactivatePlugin(Class editPluginClass){
		final PluginJAR jar = jEdit.getPlugin(editPluginClass.getName()).getPluginJAR();
		
		assertNotNull(jar);
		
		// XMLPlugin is not activated, even if we tested the resolver, since
		// only Resolver.instance() is called
		// this ensures that its stop() method will be called !
		jar.activatePlugin();

		MessageListener listen =  new MessageListener();
		listen.registerForMessage(new EBCondition(){
				public boolean matches(EBMessage msg){
					if(msg instanceof PluginUpdate){
						PluginUpdate upd = (PluginUpdate)msg;
						return upd.getWhat() == PluginUpdate.DEACTIVATED
								&& upd.getPluginJAR() == jar;
					}else return false;
				}
		});

		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					jar.deactivatePlugin(false);
				}
		});
		
		listen.waitForMessage(10000);
		
		listen.registerForMessage(new EBCondition(){
				public boolean matches(EBMessage msg){
					if(msg instanceof PluginUpdate){
						PluginUpdate upd = (PluginUpdate)msg;
						return upd.getWhat() == PluginUpdate.ACTIVATED
								&& upd.getPluginJAR() == jar;
					}else return false;
				}
		});
		
		jar.activatePlugin();

		listen.waitForMessage(10000);
	}

	public static class JWindowFixture extends ContainerFixture<JWindow>{
		public JWindowFixture(Robot robot, JWindow target){
			super(robot,target);
		}
		
		public JWindowFixture requireNotVisible(){
			assertFalse(target.isVisible());
			return this;
		}
		
		public JWindowFixture requireVisible(){
			assertTrue(target.isVisible());
			return this;
		}
	}
	
	public static JWindowFixture completionPopup(){
		Component  completionC = TestUtils.robot().finder().find(
			new ComponentMatcher(){
				public boolean matches(Component c){
					return c instanceof org.gjt.sp.jedit.gui.CompletionPopup;
				}
			});
		
		return new JWindowFixture(TestUtils.robot(),(JWindow)completionC);
	}
	

}
