package cswilly.jeditPlugins.spell;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runner.notification.RunListener;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.internal.TextListener;

import java.io.*;
import javax.swing.JDialog;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;

import cswilly.jeditPlugins.spell.hunspellbridge.ProgressObs;

public class InjEditTestNotifier extends RunListener{
	private static Thread testThread = null;
	private static JDialog pod = null;
	private static ProgressObs po = null;
	
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
				// try{
				// core.addListener(new TextListener(
				// 	/*new PrintStream(
				// 	new FileOutputStream(
				// 	"/Users/elelay/temp/client2/jEdit/SpellCheck/tests-output"))*/
				// 	System.out));
				// }catch(Exception e){}
					core.run(SpellCheckPluginTest.class);
					if(pod!=null)pod.setVisible(false);
				}catch(Throwable t){
					Log.log(Log.ERROR,InjEditTestNotifier.class,"Throwable : "+t.toString());
				}
			}
		};
		
		po = new ProgressObs(testThread);
		pod = po.asDialog(jEdit.getActiveView(),"Tests runner");

		testThread.start();
		
	}
	
	public static void stopTests(){
		if(testThread == null)return;
		testThread.stop();
		testThread = null;	
		if(pod!=null)pod.setVisible(false);
	}
	
	 public void testFailure(Failure failure) {
		 Log.log(Log.ERROR,this,"Erreur : "+failure);
		 if(po!=null){
			 po.setValue(1);
		 }
    }

	public void testFinished(Description description){
		 Log.log(Log.ERROR,this,"Finished : "+description);
		 if(po!=null){
			 po.setValue(1);
		 }
	}
	public void testIgnored(Description description){
		 Log.log(Log.ERROR,this,"Ignored : "+description);
	}
 	public void testRunFinished(Result result){
		 Log.log(Log.ERROR,this,"finished : "+result);
		 if(po!=null){
			 po.setStatus("DONE !!!");
			 pod.setVisible(false);
		 }
	}
 	public void testRunStarted(Description description){
		
		 Log.log(Log.ERROR,this,"RunStarted : "+description);
		 if(po!=null){
			 po.setMaximum(description.testCount());
		 }
	}
 	public void testStarted(Description description){
		 Log.log(Log.ERROR,this,"Started : "+description);
		 if(po!=null){
			 po.setStatus("Running "+description);
			 po.setValue(1);
		 }
	}
	
}
