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

import org.gjt.sp.util.Log;

import java.io.*;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Run reliably aspell and pass the output to a Processor to
 * finally return a result via the Future interface.
 * 
 * @author $Author$
 * @version $Revision$
 */
public abstract class FutureAspell<T> implements Future<T>{
	private boolean done;
	private boolean cancelled;
	private T result;
	private Process process;
	private Thread workThread;
	private Object lock;
	private SpellException exception;
	
	protected FutureAspell(List<String> aspellCommandLine,Processor<T> processor)
	{
		exception = null;
		process = null;
		done = false;
		cancelled = false;
		result = null;
		lock = new Object();
		if(aspellCommandLine.isEmpty())
			throw new IllegalArgumentException("Aspell command line should at list contain the path to the executable!");
		if(processor == null)
			throw new IllegalArgumentException("The Processor should not be null !");
		doProcess(aspellCommandLine,processor);
	}
	
	private final void doProcess(final List<String> aspellCommandLine,final Processor<T> processor){
		workThread = new Thread(){
			public void run(){
				String line;
				//directly dump the aspell dicts via `aspell dump dicts` 
				ProcessBuilder pb = new ProcessBuilder(aspellCommandLine);
				//this to allow us to catch error messages from Aspell
				pb.redirectErrorStream(true);
				try
				{
					Log.log(Log.DEBUG,this,"Starting listing with aspell="+aspellCommandLine.get(0));
					process = pb.start();
					InputStream is = process.getInputStream();
					BufferedReader input  = new BufferedReader(new InputStreamReader(is ) );
					
					while ( ( line = input.readLine() ) != null )
					{
						processor.accumulate(line);
					}
				}
				catch ( IOException e )
				{
					Log.log(Log.ERROR, FutureListDicts.class, "Exception while listing dicts");
					Log.log(Log.ERROR, FutureListDicts.class,e.getMessage());
					saveException(new SpellException(e.getMessage()));
				}
				catch ( SpellException spe)
				{
					saveException(spe);
				}
				catch ( Abort a){
					Log.log(Log.ERROR, FutureListDicts.class, "Dictionnaries listing was aborted");
				}
				finally{
					if(process!=null)process.destroy();
					synchronized(lock){
						done = true;
						result = processor.done();
					}
				}
			}
		};
		//actually start it !
		workThread.start();
	}
	
	private final void saveException(SpellException e){
		synchronized(lock){
			this.exception = e;
		}
	}
	
	public final boolean cancel(boolean mayInterruptIfRunning){
		synchronized(lock){
			if(done || cancelled)return false;
			cancelled = true;
		}
		
		if(mayInterruptIfRunning){
			//work thread will catch the abort() and know that it must stop
			workThread.stop(new Abort());
			//free work thread blocked in read()
			process.destroy();
			return true;
		}
		else return false;
	}
	
	public final T get() throws CancellationException,InterruptedException, ExecutionException{
		
		if(isCancelled())throw new CancellationException();
		
		if(!isDone())workThread.join();//InterruptedException is propagated
		
		synchronized(lock){
			if(exception != null)throw new ExecutionException(exception);
			return result;
		}
	}
	
	public T get(long timeout, TimeUnit unit)
	throws CancellationException, InterruptedException, ExecutionException, TimeoutException{
		if(isCancelled())throw new CancellationException();
		
		if(!isDone())unit.timedJoin(workThread,timeout);//InterruptedException is propagated
		
		if(isCancelled())throw new CancellationException();
		if(!isDone())throw new TimeoutException();
		
		synchronized(lock){
			if(exception != null)throw new ExecutionException(exception);
			return result;
		}
	}
	
	public boolean isCancelled(){
		synchronized(lock){
			return cancelled;
		}
	}
	public boolean isDone(){
		synchronized(lock){
			return done;
		}
	}
	
	protected static interface Processor<T>{
		public void accumulate(String line) throws SpellException;
		public T done(); 
	}
}
	final class Abort extends Error
	{
		public Abort()
		{
			super("Work request aborted");
		}
	}

