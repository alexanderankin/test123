package cswilly.spell;

import org.gjt.sp.util.Log;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class FutureListDicts implements Future<Vector<String>>{
	private boolean done;
	private boolean cancelled;
	private Vector<String> dicts;
	private Process process;
	private Thread workThread;
	private Object lock;
	private SpellException exception;
	
	public FutureListDicts(String aspellExeFileName){
		exception = null;
		process = null;
		done = false;
		cancelled = false;
		lock = new Object();
		doListDicts(aspellExeFileName);
	}
	
	private void doListDicts(final String aspellExeFilename){
		workThread = new Thread(){
			public void run(){
				String line;
				Vector<String> langs = new Vector<String>();
				//directly dump the aspell dicts via `aspell dump dicts` 
				ProcessBuilder pb = new ProcessBuilder(Arrays.asList(new String[]{aspellExeFilename,"dump","dicts"}));
				//this to allow us to catch error messages from Aspell
				pb.redirectErrorStream(true);
				try
				{
					Log.log(Log.DEBUG,this,"Starting listing with aspell="+aspellExeFilename);
					process = pb.start();
					InputStream is = process.getInputStream();
					BufferedReader input  = new BufferedReader(new InputStreamReader(is ) );
					
					// each line is a dictionnary
					Pattern p = Pattern.compile("^[a-z]{2}[-\\w]*$");//at least 2 letters language code, then anything
					
					while ( ( line = input.readLine() ) != null )
					{
						if(!p.matcher(line).matches())
							throw new IOException("Suspect dictionnary name ("+line+")");
						Log.log(Log.DEBUG,FutureListDicts.class, "dict:"+line);
						langs.add(line);
					}
				}
				catch ( IOException e )
				{
					Log.log(Log.ERROR, FutureListDicts.class, "Exception while listing dicts");
					Log.log(Log.ERROR, FutureListDicts.class,e.getMessage());
					saveException(new SpellException(e.getMessage()));
				}
				catch ( Abort a){
					Log.log(Log.ERROR, FutureListDicts.class, "Dictionnaries listing was aborted");
					//saveException(new SpellException("Dictionnaries listing was aborted"));
				}
				finally{
					if(process!=null)process.destroy();
					synchronized(lock){
						done = true;
						dicts = langs;
					}
				}
			}
		};
		//actually start it !
		workThread.start();
	}
	
	private void saveException(SpellException e){
		synchronized(lock){
			this.exception = e;
		}
	}
	
	public boolean cancel(boolean mayInterruptIfRunning){
		synchronized(lock){
			if(dicts != null || cancelled)return false;
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
	
	public Vector<String> get() throws CancellationException,InterruptedException, ExecutionException{
		
		if(isCancelled())throw new CancellationException();
		
		if(!isDone())workThread.join();//InterruptedException is propagated
		
		synchronized(lock){
			if(exception != null)throw new ExecutionException(exception);
			return dicts;
		}
	}
	
	public Vector<String> get(long timeout, TimeUnit unit)
	throws CancellationException, InterruptedException, ExecutionException, TimeoutException{
		if(isCancelled())throw new CancellationException();
		
		if(!isDone())unit.timedJoin(workThread,timeout);//InterruptedException is propagated
		
		if(isCancelled())throw new CancellationException();
		if(!isDone())throw new TimeoutException();
		
		synchronized(lock){
			if(exception != null)throw new ExecutionException(exception);
			return dicts;
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
	
	class Abort extends Error
	{
		public Abort()
		{
			super("Work request aborted");
		}
	}
	
}
