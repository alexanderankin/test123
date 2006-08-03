package testcase;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexModifier;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * JUnit 3.x testcase of Lucene classes with no jedit dependencies.
 * 
 * @author ezust
 *
 */

public class TestIndex extends TestCase
{

	static String userDir = "/home/ezust/.jedit";
	static String jEditHome = "/home/ezust/workspace/jEditCVS/build"; 
	
	Analyzer analyzer = new StandardAnalyzer();
	QueryParser parser = new QueryParser("content", analyzer);
	IndexReader indexReader = null;
	IndexModifier indexModifier = null;
	Searcher searcher;
	Query query;
	
	public TestIndex() {
		super("testSearch");
	}
	public static Test suite() {
		return new TestIndex();
	}

	@Override 
	public void setUp() {
		indexModifier = getWriter("help");
		indexStaticHelp();
		indexReader = getReader("help");
		System.out.println("setup complete");
	}
	
	public void testSearch() throws IOException {
		assertEquals(1, 1);
		assertTrue(true);
		assertFalse(false);
		Hits hits = search("fold");
		Iterator<Hit> hitr = hits.iterator();
		int numHits = 0;
		while (hitr.hasNext()) {
			numHits ++;
			Hit hit = hitr.next();
			System.out.println("id: " + hit.getId() + " ");
		}
		assertTrue(numHits>0);
	}

	public void indexStaticHelp() 
	{
		String[] components = new String[] {jEditHome, "doc", "users-guide"};
		String path = StringList.join(components, File.separator);
		Field nameField = new Field("name", "jEdit Users Guide", Store.YES, Index.NO);
		Field groupField = new Field("group", "help", Store.YES, Index.NO);
		Field fields[] = new Field[] {nameField, groupField};
		indexDirectory(fields, path);
	}
	
	public Hits search(String queryString) {
		String indexName = "help";
		try {
			query = parser.parse(queryString);
			Hits hits;
			indexReader = getReader(indexName);
			searcher = new IndexSearcher(indexReader);
			hits = searcher.search(query);
			return hits;
		}
		catch (ParseException pe) {
			throw new RuntimeException("Unable to parse: " + queryString , pe);
		}
		catch (IOException ioe) {
			throw new RuntimeException( "Unable to search index", ioe);
		}
	}
	
	public IndexReader getReader(String name) {
		try {
			if (indexReader != null) return indexReader;
			String path = getPath(name);
			indexReader = IndexReader.open(path);
			return indexReader;
		}
		catch (IOException ioe) {return null;}
		
	}

	static final Pattern filenamePattern = Pattern.compile("\\.(txt|html)?$");	

	void indexDirectory(Field[] fields, String dir)  
	{
		getWriter("help");
		String[] files = null;
		FilenameFilter fnf = new FilenameFilter() {
			public boolean accept(File dir, String name)	{
				Matcher m = filenamePattern.matcher(name);
				return m.matches();
			}
		};
		File dirFile = new File(dir);
		files = dirFile.list(fnf);
		
		for (String fileName: files) try 	{
			File f = new File(fileName);
			
			FileReader fr = new FileReader(f);
			Field content= new Field("content", fr);
			Field url = new Field("url", "file://" + f.getCanonicalPath(), Store.YES, Index.NO);
			Document doc = new Document();
			for (Field field: fields) doc.add(field);
			doc.add(content);
			doc.add(url);
			indexModifier.addDocument(doc);
		}
		catch (IOException ioe) {
			throw new RuntimeException("Unable to index file: " + fileName, ioe);
		}
	}

	private IndexModifier getWriter( String name ) {
		
		if (indexModifier != null) return indexModifier;
		
		String path = getPath(name);
		Analyzer sa = new StandardAnalyzer();
		Directory d = null;
		try {
			d = FSDirectory.getDirectory(path, false);
			indexModifier = new IndexModifier(d, sa, false);
		}
		catch (IOException ioe) { // Directory doesn't exist
			try {
				d = FSDirectory.getDirectory(path, true);
				indexModifier = new IndexModifier(d, sa, true);
			}
			catch (IOException ioe1) {
				throw new RuntimeException("Can't create indexModifier: " + name, ioe1);
			}
		}
		
		return indexModifier;
	}

	public static String getPath(String indexName) {
		String[] components = new String[] {userDir, "lucene", indexName};
		return StringList.join(components, File.separator);
	}


}
