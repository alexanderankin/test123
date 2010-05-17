package ctagsinterface.index;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import ctagsinterface.main.Tag;

public class TagIndex
{
	private static final int MAX_RESULTS = 1000;
	private IndexWriter writer;
	private StandardAnalyzer analyzer;
	private IndexSearcher searcher;
	private static final String[] FIXED_FIELDS = {
		"name", "_name", "pattern", "path", "_path"
	};
	private static Set<String> fixedFields;

	public TagIndex()
	{
		File path = new File(getIndexPath());
		path.mkdirs();
		try
		{
			FSDirectory directory = FSDirectory.open(path);
			if (IndexWriter.isLocked(directory))
			{
				Log.log(Log.WARNING, this, "The lucene index at " + path.getAbsolutePath() + " is locked");
				int ret = GUIUtilities.confirm(jEdit.getActiveView(),
					"lucene.index.locked", new Object[]{path},
					JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
				if (ret == JOptionPane.YES_OPTION)
					IndexWriter.unlock(directory);
			}
			analyzer = new StandardAnalyzer(Version.LUCENE_30,
				new HashSet<String>());
			writer = new IndexWriter(directory, analyzer,
				IndexWriter.MaxFieldLength.UNLIMITED);
			searcher = new IndexSearcher(directory, true);
			fixedFields = new HashSet<String>();
			for (String s: FIXED_FIELDS)
				fixedFields.add(s);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		try
		{
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void insertTag(Tag t, int origin)
	{
		Document doc = tagToDocument(t, origin);
		try
		{
			writer.addDocument(doc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void queryTag(String name, List<Tag> tags)
	{
		if (tags == null)
			return;
		QueryParser qp = new QueryParser(Version.LUCENE_30, "_name", analyzer);
		Query q = null;
		try
		{
			qp.parse(name);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		if (q != null)
		{
			try
			{
				TopDocs topDocs = searcher.search(q, MAX_RESULTS);
				for (ScoreDoc scoreDoc: topDocs.scoreDocs)
				{
					Document doc = searcher.doc(scoreDoc.doc);
					Tag tag = documentToTag(doc);
					tags.add(tag);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static String getIndexPath()
	{
		return jEdit.getSettingsDirectory() + File.separator +
			"CtagsInterface" + File.separator + "index";
	}

	private Document tagToDocument(Tag t, int origin)
	{
		Document doc = new Document();
		doc.add(new Field("name", t.getName(), Store.NO, Index.ANALYZED));
		doc.add(new Field("_name", t.getName(), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("pattern", t.getPattern(), Store.YES, Index.ANALYZED));
		doc.add(new Field("path", t.getFile(), Store.NO, Index.ANALYZED));
		doc.add(new Field("_path", t.getFile(), Store.YES, Index.NOT_ANALYZED));
		for (String ext: t.getExtensions())
		{
			String val = t.getExtension(ext);
			if (val == null)
				val = "";
			doc.add(new Field(ext, val, Store.YES, Index.ANALYZED));
		}
		doc.add(new Field("origin", String.valueOf(origin), Store.YES,
			Index.ANALYZED));
		return doc;
	}

	private Tag documentToTag(Document doc)
	{
		Tag tag = new Tag(doc.get("_name"), doc.get("_path"),
			doc.get("pattern"));
		Hashtable<String, String> extensions = new Hashtable<String, String>(); 
		for (Fieldable field: doc.getFields())
		{
			if (fixedFields.contains(field.name()))
				continue;
			String val = field.stringValue();
			if (val == null)
				val = "";
			extensions.put(field.name(), val);
		}
		tag.setExtensions(extensions);
		return tag;
	}
}
