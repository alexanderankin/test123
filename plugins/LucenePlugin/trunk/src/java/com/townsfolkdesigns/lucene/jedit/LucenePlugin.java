/*
 * Copyright (c) 2008 Eric Berry <elberry@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.townsfolkdesigns.lucene.jedit;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 *
 * @author eberry
 */
public class LucenePlugin extends EditPlugin {
	
	public LucenePlugin() {
		
	}

	public File getIndexStoreDirectory() {
		File pluginHome = getPluginHome();
		File indexStoreDir = new File(pluginHome, "indexes");
		if (!indexStoreDir.exists()) {
			indexStoreDir.mkdirs();
		}
		return indexStoreDir;
	}

	@Override
   public File getPluginHome() {
		String pluginHomePath = StringUtils.defaultIfEmpty(jEdit.getSettingsDirectory(), System.getProperty("user.dir"));
		File pluginHomeFile = new File(pluginHomePath, "LucenePlugin");
		if(!pluginHomeFile.exists()) {
			pluginHomeFile.mkdirs();
		}
	   return pluginHomeFile;
   }
	
	/* (non-Javadoc)
    * @see org.gjt.sp.jedit.EditPlugin#start()
    */
   @Override
   public void start() {
   	// adding indexers to IndexerExecutor
   	String indexerClassName = JEditIndexer.class.getName();
	   String[] indexerNames = ServiceManager.getServiceNames(indexerClassName);
	   JEditIndexer indexer = null;
	   for(String indexerName : indexerNames) {
	   	try {
	   		Log.log(Log.DEBUG, this, "Loading Indexer - name: " + indexerName);
	   		indexer = (JEditIndexer) ServiceManager.getService(indexerClassName, indexerName);
	   	} catch(Exception e) {
	   		Log.log(Log.ERROR, this, "Error loading Indexer - name: " + indexerName, e);
	   	}
	   	if(indexer != null) {
	   		Log.log(Log.DEBUG, this, "Adding Indexer to executor service - name: " + indexerName);
	   		IndexerExecutor.getInstance().addIndexer(indexer);
	   	}
	   }
   }

	/* (non-Javadoc)
    * @see org.gjt.sp.jedit.EditPlugin#stop()
    */
   @Override
   public void stop() {
	   IndexerExecutor.getInstance().shutdown();
   }

}
