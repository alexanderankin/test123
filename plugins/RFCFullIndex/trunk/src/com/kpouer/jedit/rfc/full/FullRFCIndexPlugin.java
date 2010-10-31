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

import gatchan.jedit.rfcreader.RFC;
import gatchan.jedit.rfcreader.RFCReaderPlugin;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.TaskManager;
import org.gjt.sp.util.ThreadUtilities;

import java.io.*;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class FullRFCIndexPlugin extends EditPlugin
{
	/**
	 * This method creates the lucene index. It is usually only called to create the package, the user never needs
	 * to use this.
	 * To create the index :
	 * copy all RFCs to index in the jEdit/rfcs/ folder
	 * then call the method
	 * com.kpouer.jedit.rfc.full.FullRFCIndexPlugin.buildIndex()
	 * @throws IOException
	 */
	public static void buildIndex() throws IOException
	{
		Directory directory = FSDirectory.open(new File("lucene"));
		final IndexWriter writer = new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_30),
			true, IndexWriter.MaxFieldLength.UNLIMITED);

		RFCReaderPlugin plugin = (RFCReaderPlugin) jEdit.getPlugin("gatchan.jedit.rfcreader.RFCReaderPlugin", true);
		final Map<Integer, RFC> map = plugin.getRfcList();
		org.gjt.sp.util.Task task = new org.gjt.sp.util.Task()
		{
			@Override
			public void _run()
			{
				try
				{
					File rfcs = new File("rfcs");
					File[] files = rfcs.listFiles();
					setMaximum(files.length);
					setLabel("Indexing RFCs");
					for (int i = 0; i < files.length; i++)
					{
						setValue(i);
						File file = files[i];
						if (file.getName().startsWith("rfc") && file.getName().endsWith(".txt"))
						{
							String num = file.getName().substring(3, file.getName().length() - 4);
							System.out.println(num);
							Document document = new Document();
							RFC rfc;
							try
							{
								rfc = map.get(Integer.parseInt(num));
								if (rfc == null)
								{
									Log.log(Log.ERROR, FullRFCIndexPlugin.class, "RFC doesn't exists " + num + " file " + file.getName());
									continue;
								}
							}
							catch (NumberFormatException e)
							{
								Log.log(Log.ERROR, FullRFCIndexPlugin.class, "RFC doesn't exists " + num + " file " + file.getName());
								continue;
							}
							Log.log(Log.DEBUG, FullRFCIndexPlugin.class, "Adding RFC " + num + " file " + file.getName() + " " + rfc.getTitle());
							setLabel(num + " " + rfc.getTitle());
							document.add(new Field("number",
								num, Field.Store.YES,
								Field.Index.ANALYZED, Field.TermVector.NO));
							document.add(new Field("title", rfc.getTitle(),
								Field.Store.NO, Field.Index.ANALYZED,
								Field.TermVector.NO));
							Reader reader = null;
							try
							{
								reader = new BufferedReader(new FileReader(file));
								document.add(new Field("content", reader));
								writer.addDocument(document);
							}
							finally
							{
								IOUtilities.closeQuietly(reader);
							}
						}
					}
					writer.optimize();
					writer.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		ThreadUtilities.runInBackground(task);
	}
}
