/*
 * WorkerThreadPool.java - a thread pool that handles groups of requests.
 * Copyright (c) 2005-2014 Marcelo Vanzin
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/** A thread pool that handles groups of requests (<i>Runnable</i> objects).
 *
 *	<p>Users are encouraged to use the shared instance by calling
 *	{@link #getSharedInstance()}, but they can instantiate new pools
 *	if they so desire. Just remember to shut down the threads when
 *	the plugin is unloaded.</p>
 *
 *	<p>This is a version of Java 5's java.util.concurrent.ThreadPoolExecutor
 *	with one addition:
 *     addRequests() will start the runnables at the same time, or
 *        wait until size threads are available in the pool.
 *
 *	@author		Marcelo Vanzin
 *	@since		CC 0.9.0
 *  @see org.gjt.sp.util.ThreadUtilities
 *	@deprecated Use java.util.concurrent instead.
 */
@Deprecated
public class WorkerThreadPool
{

	private static WorkerThreadPool instance = new WorkerThreadPool();

	public static WorkerThreadPool getSharedInstance() {
		return instance;
	}

	private final ExecutorService executor;

	public WorkerThreadPool()
	{
		this.executor = new ThreadPoolExecutor(10, 64, 10, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(), new CCThreadFactory());
	}

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
		return new WorkRequest(executor, req);
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
		int i = 0;
		WorkRequest[] wreqs = new WorkRequest[reqs.length];
		for (Runnable req : reqs) {
			wreqs[i++] = new WorkRequest(executor, req);
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
		return addRequests(reqs);
	}

	/**
	 *	Ensures that at least <code>size</code> threads are available to
	 *	handle requests.
	 */
	public void ensureCapacity(int size) {
		// no-op; handled by underlying executor already.
	}

	/** Asks all running threads to shutdown. */
	public void shutdown()
	{
		executor.shutdownNow();
	}

	/**
	 * Returns the underlying executor service.
	 *
	 * @since CC 1.7.4
	 */
	public ExecutorService getExecutor()
	{
		return executor;
	}

	private static class CCThreadFactory implements ThreadFactory
	{

		private static final AtomicLong threadId = new AtomicLong();

		@Override
		public Thread newThread(Runnable r)
		{
			Thread t = new Thread(r);
			t.setName("CC::Worker #" + threadId.incrementAndGet());
			return t;
		}

	}

}
