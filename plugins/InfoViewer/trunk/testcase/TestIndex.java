package testcase;

import junit.framework.JUnit4TestAdapter;
import infoviewer.lucene.IndexBuilder;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexModifier;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * JUnit 4.1 testcase
 * 
 * @author ezust
 *
 */

public class TestIndex
{
	Analyzer analyzer = new StandardAnalyzer();
	QueryParser parser = new QueryParser("content", analyzer);
	IndexReader indexReader;
	IndexModifier indexModifier;
	Searcher searcher;
	Query query;
	IndexBuilder indexBuilder;
	@BeforeClass public void setup() {
		indexBuilder = IndexBuilder.instance();
		
	}
	
	@AfterClass public void tearDown() {
		
	}

	@Test public void searchTest() {
		assertEquals(1, 1);
		assertTrue(true);
		assertFalse(false);
		indexBuilder.search("fold");
		
	}
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestIndex.class);
		
	}

}
