/*
 * IndexImpl.java - The Index implementation
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Shlomy Reinstein
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
import org.apache.lucene.index.Term;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

import java.io.BufferedReader;
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

	protected void addDocument(VFSFile file, Object session)
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
				doc.add(new Field("line", String.valueOf(i), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("content", line, Field.Store.YES, Field.Index.ANALYZED));
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
			IOUtilities.closeQuietly(reader);
		}
	}

	@Override
	protected Result getResultInstance()
	{
		return new LineResult();
	}
}
