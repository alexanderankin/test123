/*
 * WorkRequest.java - a work request in a WorkThreadPool.
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.gjt.sp.util.Log;

/**
 *	<p>A work request to the thread pool. Allow other threads to wait until
 *	the "runnable" is finished. This is basically a concurrent.Future implementation.
 *
 *  @see java.util.concurrent.Future
 *  @see org.gjt.sp.util.Task
 *  @see org.gjt.sp.util.ThreadUtilities
 *	@author		Marcelo Vanzin
 *	@since		CC 0.9.0
 */
@Deprecated
public final class WorkRequest
{

	private final Runnable work;
	private final Future<?> future;
	private Exception error;

	WorkRequest(ExecutorService executor, Runnable work)
	{
		this.work = work;
		this.future = executor.submit(new Runnable() {
			@Override
			public void run()
			{
				WorkRequest.this.run();
			}
		});
	}

	/** Waits until the running job is finished. */
	public void waitFor() throws InterruptedException
	{
		try {
			future.get();
		} catch (ExecutionException ee) {
			Log.log(Log.ERROR, this, ee);
		}
	}

	public boolean isDone() {
		return future.isDone();
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
	}

	protected Runnable getRunnable()
	{
		return work;
	}

}
