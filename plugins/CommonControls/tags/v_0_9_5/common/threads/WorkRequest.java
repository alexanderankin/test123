/*
 * WorkRequest.java - a work request in a WorkThreadPool.
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

import org.gjt.sp.util.Log;

/**
 *	<p>A work request to the thread pool. Allow other threads to wait until
 *	the "runnable" is finished.</p>
 *
 *	@author		Marcelo Vanzin
 *	@since		CC 0.9.0
 */
public final class WorkRequest
{

	private volatile boolean done;
	private Object		lock;
	private Runnable	work;
	private Exception	error;

	public WorkRequest(Runnable work)
	{
		this.done = false;
		this.lock = new Object();
		this.work = work;
	}

	/** Waits until the running job is finished. */
	public void waitFor() throws InterruptedException
	{
		if (done)
			return;
		synchronized (lock)
		{
			while (!done) {
				lock.wait(1000);
			}
		}
	}

	public boolean isDone() {
		synchronized (lock)
		{
			return done;
		}
	}

	/**
	 * Returns any exception that was caught while running the request.
	 *
	 * @since CC 0.9.4
	 */
	public Exception getError()
	{
		return error;
	}

	protected void run()
	{
		try {
			work.run();
		} catch (Exception e) {
			Log.log(Log.ERROR, this, e);
			error = e;
		}
		synchronized (lock)
		{
			done = true;
			lock.notifyAll();
		}
	}

	protected Runnable getRunnable()
	{
		return work;
	}

}

