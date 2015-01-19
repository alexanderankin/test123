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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A line based Index.
 * @author Shlomy Reinstein
 */
public class LineIndexImpl extends IndexImpl
{
	public LineIndexImpl(String name, File path)
	{
		super(name, path);
	}

	@Override
	protected void addDocument(VFSFile file, Object session) throws IndexInterruptedException
	{
		if (file.getPath() == null)
			return;
		Log.log(Log.DEBUG, this, "Index:"+getName() + " add " + file);
		BufferedReader reader = null;
		try
		{
			writer.deleteDocuments(new Term("_path", file.getPath()));
			LucenePlugin.CENTRAL.removeFile(file.getPath(), getName());
			reader = new BufferedReader(new InputStreamReader(
				file.getVFS()._createInputStream(session, file.getPath(), false,
				jEdit.getActiveView())));
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null)
			{
				Document doc = getEmptyDocument(file);
				i++;
				doc.add(new StringField("line", String.valueOf(i), Field.Store.YES));
				doc.add(new TextField("content", line, Field.Store.YES));
				writer.addDocument(doc);
			}
			LucenePlugin.CENTRAL.addFile(file.getPath(), getName());
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to read file " + path, e);
		}
		finally
		{
			IOUtilities.closeQuietly((Closeable) reader);
		}
	}

	@Override
	protected Result getResultInstance()
	{
		return new LineResult();
	}
}
