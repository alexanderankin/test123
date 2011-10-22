/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2011 Matthieu Casanova
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

import java.util.List;

import org.apache.lucene.search.Query;

/*
 * A query result processor that collects the files containing results
 */
public class FileListQueryProcessor implements ResultProcessor
{
	private final List<Object> files;
	private final int max;
	public FileListQueryProcessor(List<Object> files, int max)
	{
		this.files = files;
		this.max = max;
	}
	@Override
	public boolean process(Query query, float score, Result result)
	{
		String s = result.getPath();
		files.add(s);
		return files.size() < max;
	}
}
