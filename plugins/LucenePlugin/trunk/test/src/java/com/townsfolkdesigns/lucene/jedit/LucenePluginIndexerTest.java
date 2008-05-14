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
/**
 * 
 */
package com.townsfolkdesigns.lucene.jedit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.townsfolkdesigns.lucene.jedit.manager.OptionsManager;

/**
 * @author elberry
 *
 */
public class LucenePluginIndexerTest extends TestCase {

	public void testIndexer() throws Exception {
		File pluginHome = new LucenePlugin().getPluginHome();
		File testFile1 = new File(pluginHome, "test1.txt");
		File testFile2 = new File(pluginHome, "test2.txt");
		if(!testFile1.exists()) {
			testFile1.createNewFile();
		}
		if(!testFile2.exists()) {
			testFile2.createNewFile();
		}
		List<String> directories = new ArrayList<String>();
		directories.add(pluginHome.getPath());
		OptionsManager optionsManager = OptionsManager.getInstance();
		optionsManager.clear();
		optionsManager.setDirectories(directories);
		optionsManager.save();
		LucenePluginIndexer indexer = new LucenePluginIndexer();
		indexer.init();
		indexer.index();
		if(testFile1.exists()) {
			testFile1.delete();
		}
		if(testFile2.exists()) {
			testFile2.delete();
		}
	}
}
