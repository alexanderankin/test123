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

import static xml.EBFixture.*;
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
     * Convenience method to select a path in a JTree.
     * @param treeFixture the JTree
     * @param path the path to select
     */
    public static void selectPath( JTreeFixture treeFixture, String path ) {
    	String[] components = path.split("/",-1);
    	TestUtils.selectPath(treeFixture, components);
    }
    
    /**
     * @param path path in the option panes tree (eg. XML/XML)
     * @param name internal name of the option pane (eg. xml.general)
     * @return an option pane, ensuring that it's visible
     */
    public static OptionsFixture pluginOptions(){
    	TestUtils.jEditFrame().menuItemWithPath("Plugins","Plugin Options...").click();
		
		DialogFixture optionsDialog = WindowFinder.findDialog(org.gjt.sp.jedit.options.PluginOptions.class).withTimeout(5000).using(TestUtils.robot());
		Dialog target = optionsDialog.targetCastedTo(Dialog.class);
		return new OptionsFixture(TestUtils.robot(),target);
    }
    
    public static enum Option{YES,NO,OK,CANCEL}
	/**
	 * Click on an OptionPane asynchronously.
	 * Typical usage pattern is :
	 * <code>
	 *  ClickT clickT = new ClickT(Option.YES);
	 *  clickT.start();
	 *
	 *  // do something to prompt an option pane
	 *  
	 *  clickT.waitForClick();
	 *  </code>
	 */
	public static final class ClickT extends Thread{
		private final Option opt;
		private final long timeout;
		private transient WaitTimedOutError savedException;
		
		/**
		 * @param	opt	the button you want to click on
		 */
		public ClickT(Option opt){
			this(opt,2000);
		}

		/**
		 * @param	opt	the button you want to click on
		 * @param	timeout	how long do you wait for the Dialog ?
		 */
		public ClickT(Option opt, long timeout){
			this.opt = opt;
			this.timeout = timeout;
		}
		
		
		public void run(){
			try{
				final JOptionPaneFixture options = TestUtils.jEditFrame().optionPane(Timeout.timeout(timeout));
				switch(opt){
				case YES:
					options.yesButton().click();
					break;
				case NO:
					options.noButton().click();
					break;
				case OK:
					options.okButton().click();
					break;
				case CANCEL:
					options.cancelButton().click();
					break;
				default:
					fail("unspecified option to click on !");
				}
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
	
	public static void gotoPosition(final int caretPosition){
		GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					TestUtils.view().getTextArea().setCaretPosition(caretPosition);
				}
		});
	}
	
	
	public static void requireEmpty(JTreeFixture f){
		
		try{
			f.toggleRow(0);
			fail("should be empty !");
		}catch(RuntimeException re){
			//fine
			System.err.println(re.getClass());
		}
	}
}
