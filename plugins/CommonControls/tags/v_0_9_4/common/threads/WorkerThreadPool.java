/*
 * WorkerThreadPool.java - a thread pool.
 * Copyright (c) 2005 Marcelo Vanzin
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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
package common.threads;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gjt.sp.util.Log;

/**
 *	A thread pool that handles requests (<i>Runnable</i> objects) and provides
 *	some extra functionality.
 *
 *	<p>User's are encouraged to use the shared instance by calling
 *	{@link #getSharedInstance()}, but they can instantiate new pools
 *	if they so desire. Just remember to shut down the threads when
 *	the plugin is unloaded.</p>
 *
 *	<p>This is a less featureful version of Java 5's "ThreadPoolExecutor" and
 *	"Future" classes, with the benefit that they run on 1.4.</p>
 *
 *	@see	#addRequest(Runnable)
 *	@see	#addRequests(Runnable[])
 *	@see	WorkRequest
 *
 *	@author		Marcelo Vanzin
 *	@since		CC 0.9.0
 */
public class WorkerThreadPool
{

	private static WorkerThreadPool instance = new WorkerThreadPool();

	public static WorkerThreadPool getSharedInstance() {
		return instance;
	}

	private 		List		threads;
	private 		List		requests	= new LinkedList();

	private final 	Object		lock		= new Object();
	private final 	ThreadGroup	group		= new ThreadGroup("CommonControls Worker Pool");

	/**
	 *	Adds a request to the pool. The request will be run in the first
	 *	available thread after it becomes available. This method will not
	 *	block; it will just enqueue the request.
	 *
	 *	<p> If no threads have yet been started, a new thread is created
	 *	and started.</p>
	 */
	public WorkRequest addRequest(Runnable req)
	{
		ensureCapacity(1);
		WorkRequest wreq = new WorkRequest(req);
		synchronized (lock)
		{
			requests.add(wreq);
			lock.notifyAll();
		}
		return wreq;
	}

	/**
	 *	Makes sure that there are enough threads to execute all requests
	 *	in parallel and enqueue the requests. It's not guaranteed that all
	 *	the requests will run in parallel, but the pool is guaranteed to
	 *	have enough running threads to do so.
	 *
	 *	<p>So if you enqueue 4 requests and there are 4 idle threads, each
	 *	request will run on a separate thread. But if one thread is running
	 *	a long request, it may happen that one of the new requests might
	 *	finish before that running job, and another one of the new requests
	 *	will run on that same thread.</p>
	 */
	public WorkRequest[] addRequests(Runnable[] reqs)
	{
		ensureCapacity(reqs.length);
		WorkRequest[] wreqs = toWorkRequest(reqs);
		synchronized (lock)
		{
			for (int i = 0; i < wreqs.length; i++)
				requests.add(wreqs[i]);
			lock.notifyAll();
		}
		return wreqs;
	}

	/**
	 *	Immediately runs the given requests. If not enough worker threads
	 *	are free to handle all the requests, new threads are created to
	 *	be able to handle the new requests.
	 *
	 *	@since CC 0.9.1
	 */
	public WorkRequest[] runRequests(Runnable[] reqs)
	{
		WorkRequest[] wreqs = toWorkRequest(reqs);
		ensureCapacity(wreqs.length);
		synchronized (lock) {
			int curr = 0;
			for (Iterator i = threads.iterator();
				 curr < wreqs.length && i.hasNext(); )
			{
				WorkerThread wt = (WorkerThread) i.next();
				if (wt.isIdle()) {
					wt.setWorkload(wreqs[curr++]);
				}
			}
			for (int i = curr; i < wreqs.length; i++) {
				WorkerThread t = new WorkerThread();
				t.setWorkload(wreqs[i]);
				t.start();
				threads.add(t);
			}
			lock.notifyAll();
		}
		return wreqs;
	}

	/**
	 *	Ensures that at least <code>size</code> threads are available to
	 *	handle requests.
	 */
	public void ensureCapacity(int size) {
		synchronized (lock)
		{
			if (threads == null)
			{
				threads = new LinkedList();
			}

			while (threads.size() < size)
			{
				Thread t = new WorkerThread();
				t.start();
				threads.add(t);
			}
		}
	}

	/** Asks all running threads to shutdown. */
	public void shutdown()
	{
		synchronized (lock)
		{
			if (threads != null)
			{
				for (Iterator i = threads.iterator(); i.hasNext(); ) {
					((WorkerThread)i.next()).requestShutdown();
					i.remove();
				}
			}
		}
	}

	private WorkRequest[] toWorkRequest(Runnable[] reqs) {
		WorkRequest[] wreqs = new WorkRequest[reqs.length];
		for (int i = 0; i < wreqs.length; i++)
			wreqs[i] = new WorkRequest(reqs[i]);
		return wreqs;
	}

	private static int THREAD_ID = 0;

	private class WorkerThread extends Thread
	{

		private boolean run 		= true;
		private int		idleCount	= 0;
		private volatile WorkRequest work = null;

		public WorkerThread() {
			super(group, "CC::Worker #" + (++THREAD_ID));
			setDaemon(true);
		}

		/** not synchronized. call while holding "lock". */
		public void setWorkload(WorkRequest work) {
			this.work = work;
		}

		/** not synchronized. call while holding "lock". */
		public boolean isIdle() {
			return (work == null);
		}

		public void run()
		{
			while (run)
			{
				idleCount = 0;
				synchronized (lock)
				{
					while (run && work == null && idleCount < 10)
					{
						if (requests.size() > 0)
						{
							work = (WorkRequest) requests.remove(0);
							break;
						}

						try {
							lock.wait(10000);
						} catch (InterruptedException ie) {
							// ignore.
							ie.printStackTrace();
						}

						idleCount++;
					}
				}
				if (work != null)
				{
					Log.log(Log.NOTICE, this, "Executing request: " + work.getRunnable());
					work.run();
					work = null;
				}
				else if (idleCount >= 10)
				{
					// stop the thread if if has been inactive for a long
					// time and there's more than 1 thread running
					synchronized (lock)
					{
						run = !(threads.size() > 1);
						if (!run)
							threads.remove(this);
					}
				}
			}
		}

		public void requestShutdown()
		{
			synchronized (lock) {
				run = false;
				lock.notifyAll();
			}
		}

	}

}
