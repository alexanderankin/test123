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


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import marker.FileMarker;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.analysis.TokenStream;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.IntegerArray;
import org.gjt.sp.util.Log;

/*
 * A query result processor that collects the lines containing results,
 * using the highlighter to find matches within the files.
 */
public class MarkerListQueryProcessor implements ResultProcessor
{
	private final Index index;
	private final List<Object> results;
	private final int max;
	private final TokenFilter tokenFilter;

	public MarkerListQueryProcessor(Index index, List<Object> results, int max, TokenFilter tokenFilter)
	{
		this.index = index;
		this.results = results;
		this.max = max;
		this.tokenFilter = tokenFilter;
	}

	@Override
	public boolean process(Query query, float score, Result result)
	{
		String s = result.getPath();
		addLinesMatching(query, s, max - results.size());
		return results.size() < max;
	}

	private boolean isFiltered(int offsetInLine, String file, int line, String lineText, int lineStart)
	{
		if (! tokenFilter.isFiltering())
			return false;
		Buffer b = jEdit.openTemporary(jEdit.getActiveView(), new File(file).getParent(), file, false);
		b.setMode();
		// Mark the tokens on the line, to filter out comments and strings
		DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
		b.markTokens(line, tokenHandler);
		Token token = tokenHandler.getTokens();
		int start = lineStart;
		int indexPos = lineStart + offsetInLine;
		while(token.id != Token.END)
		{
			int next = start + token.length;
			if (start <= indexPos && next > indexPos)
				break;
			start = next;
			token = token.next;
		}
		return tokenFilter.isFiltered(token);
	}

	private void addLinesMatching(Query query, String file, int max)
	{
		IntegerArray positions = new IntegerArray(30);
		Formatter sf = new SearchFormatter(positions, max);
		QueryScorer scorer = new QueryScorer(query);
		StringBuilder sb = new StringBuilder();
		List<Integer> lineStart = new ArrayList<Integer>(500);
		BufferedReader br = null;
		try
		{
			br = getReader(file);
			if (br == null)
			{
				Log.log(Log.WARNING, this, "Cannot read file " + file +
					" maybe it doesn't exist anymore");
				return;
			}
			String s;
			char sep = '\n';
			while ((s = br.readLine()) != null)
			{
				if (sb.length() > 0)
					sb.append(sep);
				lineStart.add(sb.length());
				sb.append(s);
			}
			Highlighter h = new Highlighter(sf, scorer);
			h.setMaxDocCharsToAnalyze(sb.length());
			String text = sb.toString();
			TokenStream tokenStream = index.getAnalyzer().tokenStream("field", new StringReader(text));
			h.getBestFragments(tokenStream, text, 0);
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			IOUtilities.closeQuietly(br);
		}

		for (int i = 0; i < positions.getSize(); i += 2)
		{
			int tokenStart = positions.get(i), tokenEnd = positions.get(i + 1);
			int start = tokenStart, stop = tokenEnd;
			// Find beginning and end of line
			while ((start >= 0) && (sb.charAt(start) != '\n'))
				start--;
			start++;
			while ((stop < sb.length()) && (sb.charAt(stop) != '\n'))
				stop++;
			String lineText = sb.substring(start, stop);
			int line = Collections.binarySearch(lineStart, tokenStart);
			if (line < 0)
				line = -line - 2;
			int startOffset = tokenStart - start;
			if (isFiltered(startOffset, file, line, lineText, start))
				continue;
			FileMarker marker = new FileMarker(file, line, lineText);
			int endOffset = startOffset + tokenEnd - tokenStart;
			if (endOffset > lineText.length())
				endOffset = lineText.length();
			marker.addSelection(marker.new Selection(startOffset,
				startOffset + tokenEnd - tokenStart));
			results.add(marker);
		}
	}

	/**
	 * Return a reader for the given file
	 * @param file the file
	 * @return a reader or null of the reader cannot be opened or if
	 * the file do not exist anymore
	 */
	private static BufferedReader getReader(String file)
	{
		VFS vfs = VFSManager.getVFSForPath(file);
		View view = jEdit.getActiveView();
		Object session = vfs.createVFSSession(file, view);
		BufferedReader reader = null;
		try
		{
			VFSFile vfsFile = vfs._getFile(session, file, view);
			if (vfsFile != null)
			{
				InputStream inputStream = vfs._createInputStream(session,
					vfsFile.getPath(), false, view);
				if (inputStream != null)
					reader = new BufferedReader(new InputStreamReader(
						inputStream));
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, MarkerListQueryProcessor.class, e);
		}
		return reader;
	}
}
