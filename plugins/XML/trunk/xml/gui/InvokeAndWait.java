/*
 * InvokeAndWait.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2013 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

/**
 * Utility class to call the EDT in background tasks, 
 * backing out after some time if there is a chance of deadlock
 */
public class InvokeAndWait {

	/**
	 * Try to avoid deadlocks between the background task (sidekick parsing) and the AWT EDT
	 * when the background task holds a lock the EDT is blocked waiting for and the background
	 * task calls invokeAndWait() because it needs input from the user.
	 * Deadlocks are not prevented, but the background task will back-out after some time.
	 * 
	 * Two timeouts may be specified: 
	 *  - timeToGetInEDT is in case of existing blocked EDT; withTimeouts will return if r
	 *    has not began to run after timeToGetInEDT. When r eventually gets scheduled in the EDT
	 *    it will not be run if timeToGetInEDT has passed.
	 *  - timeToRun is in case r itself gets blocked waiting for some lock held by the background task.
	 *    withTimeouts will return anyway after timeToGetInEDT+timeToRun. r will not be interrupted.
	 *
	 * withTimeouts will return early if interrupted, even if r is not done. 
	 * In this case, it will cancel running r (if it's not already running) and will return false.
	 * 
	 * if withTimeouts is called from the EDT it will run r synchronously
	 * (timeout on timeToRun will not be respected)
	 * 
	 * @param	r				task to run in the EDT
	 * @param	timeToGetInEDT	presumably short time to wait for r to be scheduled in the EDT. 0 to wait forever, but it wouldn't make sense
	 * @param	timeToRun		presumably longer time to wait for r to be run.
	 * 							0 to wait forever, if you're sure the task will not block
	 * 							and you except an answer from the user.
	 * @return	if r was run successfully to completion
	 * @throws InvocationTargetException	if an exception is caught running r
	 **/
	public static boolean  withTimeouts(final Runnable r, long timeToGetInEDT, long timeToRun) throws InvocationTargetException{
		final AtomicBoolean cancelled = new AtomicBoolean(false);
		final AtomicBoolean started = new AtomicBoolean(false);
		final AtomicBoolean done = new AtomicBoolean(false);
		final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
		final Object lock = new Object();
		if(SwingUtilities.isEventDispatchThread()){
			try{
				r.run();
			}catch(Throwable t){
				throw new InvocationTargetException(t);
			}
			return true;
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if(!cancelled.get()){
						synchronized (lock) {
							started.set(true);
							lock.notify();
						}
						try{
							r.run();
						}catch(Throwable t){
							exception.set(t);
						}
						synchronized (lock) {
							done.set(exception.get()==null);
							lock.notify();
						}
					}
				}
			});
		
			synchronized (lock)
			{
				try
				{
					if(!started.get()){
						lock.wait(timeToGetInEDT);
					}
					if(started.get() && !done.get()){
						lock.wait(timeToRun);
					}
				}catch(InterruptedException e){
					// fine, I'll cancel
				}
				cancelled.set(true);
			}
		
			if(exception.get() != null){
				throw new InvocationTargetException(exception.get());
			}
			
			return done.get();
		}		
	}

}
