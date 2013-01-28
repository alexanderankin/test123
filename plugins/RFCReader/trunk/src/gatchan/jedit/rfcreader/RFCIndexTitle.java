/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2010, 2013 Matthieu Casanova
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
package gatchan.jedit.rfcreader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * @author Matthieu Casanova
 */
public class RFCIndexTitle extends AbstractRFCIndex
{
	/** increment this */
	int INDEX_VERSION = 6;

	public RFCIndexTitle() throws IOException
	{
		RFCReaderPlugin plugin = (RFCReaderPlugin) jEdit.getPlugin("gatchan.jedit.rfcreader.RFCReaderPlugin");
		File home = plugin.getPluginHome();
		analyzer = new StandardAnalyzer(Version.LUCENE_41);
		directory = FSDirectory.open(new File(home, "lucene"));
	}

	@Override
	protected String[] getFields()
	{
		return new String[]{"number", "title"};
	}

	@Override
	public void load() throws IOException
	{
		Log.log(Log.DEBUG, this, "load()");
		if (jEdit.getIntegerProperty("rfcreader.index.version",-1) != INDEX_VERSION ||
			!DirectoryReader.indexExists(directory))
		{
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_41, analyzer);
			IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
			Document document = new Document();
			StringField numField = new StringField("number", "", Field.Store.YES);
			TextField title = new TextField("title", "", Field.Store.NO);
			document.add(numField);
			document.add(title);
			for (RFC rfc : rfcs.values())
			{
				numField.setStringValue(Integer.toString(rfc.getNumber()));
				title.setStringValue(rfc.getTitle());
				writer.addDocument(document);
			}
			writer.close();
			jEdit.setIntegerProperty("rfcreader.index.version",INDEX_VERSION);
		}
		directoryReader = DirectoryReader.open(directory);
		searcher = new IndexSearcher(directoryReader);
	}
}
