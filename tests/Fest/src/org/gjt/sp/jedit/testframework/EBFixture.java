/*
 * EBFixture.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009-2010 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package org.gjt.sp.jedit.testframework;

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

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.PluginUpdate;

/**
 * Try to wrap nicely the EditBus for tests
 * $Id$
 */
public class EBFixture{
	
	/**
	 * register for a certain message, run the runnable r, wait for the message.
	 * @param	r	runnable to run asyncronously
	 * @param	condition	message to wait for
	 * @param	timeout	how long to wait for the message
	 */
	public static void doInBetween(Runnable r,EBCondition condition, long timeout){
		MessageListener listen =  new MessageListener();
		listen.registerForMessage(condition);
		new Thread(r).start();
		listen.waitForMessage(timeout);
	}
	
	/**
	 * blocks until an EBMessage of class clazz happened, or fail after timeout.
	 * @param	clazz	class to wait for
	 * @param	timeout	how long to wait for the message
	 */
	public static void simplyWaitForMessageOfClass(final Class clazz, long timeout){
		MessageListener listen = new MessageListener();
		listen.registerForMessage(messageOfClassCondition(clazz));
		listen.waitForMessage(timeout);
	}
	
	/**
	 * @param	clazz	class to wait for
	 * @return	an EBCondition returning true when the message instanceof clazz
	 */
	public static EBCondition messageOfClassCondition(final Class clazz){
		return new EBCondition(){
				public boolean matches(EBMessage ebm){
					return clazz.isInstance(ebm);
				}
			};
	}

	/**
	 * Matcher for EBMessages
	 */
	public static interface EBCondition {
		
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
		
		public MessageListener(){}
		
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
					EditBus.removeFromBus(this);
					condition = null;
					if(msg == null){
						fail("Timeout : "+timeout+"ms !");
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
			// condition == null iff timeout or interrupted
			// so the message will be discarded anyway
			if(condition == null || condition.matches(message)){
				synchronized(this){
					msg=message;
					this.notify();
				}
			}
		}
	}
}
