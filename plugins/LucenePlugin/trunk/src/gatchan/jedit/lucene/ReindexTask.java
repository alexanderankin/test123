/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2013 Matthieu Casanova
 * Copyright (C) 2009, 2011 Shlomy Reinstein
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package gatchan.jedit.lucene;

import java.util.concurrent.CountDownLatch;

import org.gjt.sp.util.Log;
import org.gjt.sp.util.Task;
import org.gjt.sp.util.ThreadUtilities;

/**
 * @author Matthieu Casanova
 */
class ReindexTask extends Task
{
	private final String indexName;
	private final Runnable awtTask;

	ReindexTask(String indexName, Runnable awtTask)
	{
		this.indexName = indexName;
		this.awtTask = awtTask;
	}

	@Override
	public void _run()
	{
		try
		{
			setMaximum(3L);
			Log.log(Log.NOTICE, this, "Reindex " + indexName + " asked");
			final Index index = LucenePlugin.instance.getIndex(indexName);
			setStatus("Reindex " + indexName);
			final CountDownLatch latch = new CountDownLatch(1);
			Task task = new Task()
			{
				@Override
				public void _run()
				{
					try
					{
						index.reindex(this);
					} 
					catch (IndexInterruptedException e) 
					{
						Log.log(Log.WARNING, this, "Indexing Halted by user");
						Thread.currentThread().interrupt();
						return;
					}
					finally
					{
						latch.countDown();
					}
				}
			};
			ThreadUtilities.runInBackground(task);
			try
			{
				latch.await();
			}
			catch (InterruptedException e)
			{
				Log.log(Log.ERROR, this, e);
			}
			setValue(1L);
			Log.log(Log.NOTICE, this, "Reindex " + indexName + " DONE");
			setStatus("Commit " + indexName);
			index.commit();
			setValue(2L);
			Log.log(Log.NOTICE, this, "Optimize index:" + indexName + "DONE");
			Log.log(Log.NOTICE, this, "Optimize Central Index");
			setStatus("Commit Central Index");
			LucenePlugin.CENTRAL.commit();
			setValue(3L);
			Log.log(Log.NOTICE, this, "Optimize Central Index DONE");
		}
		finally
		{
			ThreadUtilities.runInDispatchThread(awtTask);
		}
	}
}
