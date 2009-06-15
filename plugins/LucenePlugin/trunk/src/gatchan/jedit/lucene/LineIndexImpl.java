package gatchan.jedit.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lucene.SourceCodeAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

public class LineIndexImpl extends IndexImpl
{
	protected void addDocument(VFSFile file, Object session)
	{
		Log.log(Log.DEBUG, this, "Index:"+getName() + " add " + file);
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(
				file.getVFS()._createInputStream(session, file.getPath(), false,
				jEdit.getActiveView())));
			String line = null;
			int i = 0;
			while ((line = reader.readLine()) != null)
			{
				Document doc = new Document();
				doc.add(new Field("path", file.getPath(), Field.Store.NO, Field.Index.ANALYZED));
				doc.add(new Field("_path", file.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				i++;
				doc.add(new Field("line", String.valueOf(i), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("content", line, Field.Store.YES, Field.Index.ANALYZED));
				writer.updateDocument(new Term("_path", file.getPath()), doc);
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
	public void search(String query, ResultProcessor processor)
	{
		openSearcher();
		if (searcher == null)
			return;
		SourceCodeAnalyzer analyzer = new SourceCodeAnalyzer();
		QueryParser parser = new MultiFieldQueryParser(new String[]{"path", "content"}, analyzer);
		try
		{
			Query _query = parser.parse(query);
			TopDocs docs = searcher.search(_query, 100);
			ScoreDoc[] scoreDocs = docs.scoreDocs;
			LineResult result = new LineResult();
			for (ScoreDoc doc : scoreDocs)
			{
				Document document = searcher.doc(doc.doc);
				result.setDocument(document);
				if (!processor.process(doc.score, result))
				{
					break;
				}
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
