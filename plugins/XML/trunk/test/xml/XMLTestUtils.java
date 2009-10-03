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

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.PluginUpdate;

/**
 * a handful of utility methods.
 * Some of them may be moved to the test-framework as they prove useful
 
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
	
	/**
	 * Click on an OptionPane asynchronously.
	 * Typical usage pattern is :
	 * <code>
	 *  ClickT clickT = new ClickT(true);
	 *  clickT.start();
	 *
	 *  // do something to prompt an option pane
	 *  
	 *  clickT.waitForClick();
	 *  </code>
	 */
	public static final class ClickT extends Thread{
		private final boolean yes;
		private transient WaitTimedOutError savedException;
		
		/**
		 * @param	yes	if you want to click on the yes button
		 */
		public ClickT(boolean yes){
			this.yes = yes;
		}
		
		
		public void run(){
			try{
				final JOptionPaneFixture options = TestUtils.jEditFrame().optionPane(Timeout.timeout(2000));
				if(yes)
					options.yesButton().click();
				else
					options.noButton().click();
			}catch(WaitTimedOutError e){
				System.err.println("TOO bad : timeout");
				savedException = e;
			}
		}
		
		/**
		 * blocking method.
		 * wait until the OptionPane shows up and we click on it,
		 * or until the timeout expires (then you get an WaitTimedOutError)
		 */
		public void waitForClick() throws WaitTimedOutError{
			try{
				this.join();
				if(savedException != null)throw savedException;
			}catch(InterruptedException e){
				fail("Interrupted");
			}
		}
		
	}
	
	
	
	/**
	 * Matcher for EBMessages
	 */
	public interface EBCondition {
		
		/**
		 * @return true if the message is what you were waiting for
		 */
		public boolean matches(EBMessage message);
		
	}
	
	
	
	/**
	 * listener for messages, with a timeout
	 */
	public static class MessageListener implements EBComponent{
		private EBCondition condition;
		private EBMessage msg;
		
		MessageListener(){}
		
		/**
		 * register and pass messages to matcher.
		 * You can reuse a MessageListener, but only after waitForMessage has been called
		 * @param	matcher	what message interests us
		 */
		public synchronized void registerForMessage(EBCondition matcher) throws IllegalStateException{
			if(condition!=null)throw new IllegalStateException("already listening");
			EditBus.addToBus(this);
			condition = matcher;
			msg = null;
		}
		
		/**
		 * wait for a matching message for the given amount of time.
		 * fails when we get an interrupt or a timeout
		 *
		 * @param	timeout	the amount of milliseconds to wait for a message
		 * @return	found message
		 */
		public EBMessage waitForMessage(long timeout){
			
			try{
				synchronized(this){
					if(msg == null){
						this.wait(timeout);
					}
					condition = null;
					EditBus.removeFromBus(this);
					if(msg == null){
						fail("Timeout !");
						return null;
					}else{
						return msg;
					}
				}
			}catch(InterruptedException ie){
				EditBus.removeFromBus(this);
				condition = null;
				fail("Interrupted !");
				return null;
			}
		}
		
		public void handleMessage(EBMessage message){
			if(condition.matches(message)){
				synchronized(this){
					msg=message;
					this.notify();
				}
			}
		}
	}
	
	
	/**
	 * execute an action, like via the ActionBar
	 * 
	 * @param	action	the action to execute
	 * @param	count	the number of time to execute it (should be at least 1)
	 */
	public static void action(final String action, final int count){
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					TestUtils.view().getInputHandler().setRepeatCount(count);
					TestUtils.view().getInputHandler().invokeAction(action);
				}
		});
	}

}
