/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2010 Matthieu Casanova
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

package com.kpouer.jedit.rfc.full;

import gatchan.jedit.rfcreader.AbstractRFCIndex;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

import java.io.*;

/**
 * @author Matthieu Casanova
 */
public class FullRFCIndex extends AbstractRFCIndex
{
	private static int INDEX_VERSION = 0;
	private static final String RFCREADER_FULLINDEX_VERSION = "rfcreader.fullindex.version";
	private static final String PLUGIN_CLASS_NAME = "com.kpouer.jedit.rfc.full.FullRFCIndexPlugin";

	public FullRFCIndex() throws IOException
	{
		EditPlugin plugin = jEdit.getPlugin(FullRFCIndexPlugin.class.getName(), true);
		File path = plugin.getPluginHome();
		path.mkdirs();
		analyzer = new StandardAnalyzer(Version.LUCENE_30);
	}

	protected String[] getFields()
	{
		return new String[]{"number", "title", "content"};
	}

	@Override
	public void load() throws IOException
	{
		Log.log(Log.DEBUG, this, "load()");
		EditPlugin plugin = jEdit.getPlugin(PLUGIN_CLASS_NAME);
		File home = plugin.getPluginHome();
		File luceneIndex = new File(home, "lucene");
		luceneIndex.mkdirs();
		directory = FSDirectory.open(luceneIndex);
		if (jEdit.getIntegerProperty(RFCREADER_FULLINDEX_VERSION,-1) != INDEX_VERSION ||
			!IndexReader.indexExists(directory))
		{
			PluginJAR jar = plugin.getPluginJAR();
			String[] resources = jar.getResources();
			for (int i = 0; i < resources.length; i++)
			{
				String resource = resources[i];
				if (!"lucene/".equals(resource) && resource.startsWith("lucene/"))
				{
					InputStream stream = null;
					BufferedOutputStream out = null;
					try
					{
						stream = jar.getClassLoader().getResourceAsStream(resource);
						BufferedInputStream in = new BufferedInputStream(stream);
						File output = new File(home, resource);
						out = new BufferedOutputStream(new FileOutputStream(output));
						IOUtilities.copyStream(null, in, out, false);
					}
					finally
					{
						IOUtilities.closeQuietly(out);
						IOUtilities.closeQuietly(stream);
					}
				}
			}
			jEdit.setIntegerProperty(RFCREADER_FULLINDEX_VERSION,INDEX_VERSION);
		}
		directory = FSDirectory.open(luceneIndex);
		searcher = new IndexSearcher(directory, true);
	}
}
