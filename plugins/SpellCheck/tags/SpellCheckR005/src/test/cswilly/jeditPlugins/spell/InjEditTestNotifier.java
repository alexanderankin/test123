package cswilly.jeditPlugins.spell;

import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.notification.RunListener;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.internal.TextListener;

import java.io.*;

import org.gjt.sp.util.Log;


public class InjEditTestNotifier extends RunListener{
	private static Thread testThread = null;
	
	/**
	 * Run : cswilly.jeditPlugins.spell.InjEditTestNotifier.runTests()
	 */
	public static void runTests(){
		if(testThread != null){
			stopTests();
		}
		
		testThread = new Thread(){
			public void run(){
				try{
				JUnitCore core = new JUnitCore();
				core.addListener(new InjEditTestNotifier());
				try{
				core.addListener(new TextListener(
					/*new PrintStream(
					new FileOutputStream(
					"/Users/elelay/temp/client2/jEdit/SpellCheck/tests-output"))*/
					System.out));
				}catch(Exception e){}
				core.run(SpellCheckPluginTest.class);
				}catch(Throwable t){
					Log.log(Log.ERROR,InjEditTestNotifier.class,"Throwable : "+t.toString());
				}
			}
		};
		
		testThread.start();
	}
	
	public static void stopTests(){
		if(testThread == null)return;
		testThread.stop();
		testThread = null;	
	}
	
	 public void testFailure(Failure failure) {
		 Log.log(Log.ERROR,this,"Erreur : "+failure);
    }

	
}
