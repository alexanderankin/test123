package lucene;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import lucene.OptionPane;

public class LucenePlugin extends EBPlugin {
	
	static private Analyzer analyzer = new StandardAnalyzer();

	private static boolean validDir(String path)
	{
		Pattern exc = Pattern.compile(OptionPane.excludeDirs(),Pattern.CASE_INSENSITIVE);
		Matcher matcher = exc.matcher(path);
		return (! matcher.matches());
	}
	private static boolean validFile(String path)
	{
		Pattern inc = Pattern.compile(OptionPane.includeFiles(),Pattern.CASE_INSENSITIVE);
		Pattern exc = Pattern.compile(OptionPane.excludeFiles(),Pattern.CASE_INSENSITIVE);
		Matcher incMatcher = inc.matcher(path);
		Matcher excMatcher = exc.matcher(path);
		return (incMatcher.matches() && (! excMatcher.matches()));
	}
	public static void find(View view)
	{
		String text = JOptionPane.showInputDialog("Enter text to search:");
		if (text == null)
			return;
		DockableWindowManager dwm = view.getDockableWindowManager();
		dwm.showDockableWindow("lucene-search");
		SearchResults sr = (SearchResults) dwm.getDockableWindow(
			"lucene-search");
        sr.search(text);
	}
	private static String contentDir()
	{
		return OptionPane.indexPath() + File.separator + "content";
	}
	private static String metaDir()
	{
		return OptionPane.indexPath() + File.separator + "meta";
	}
	public static void index(View view) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle("Add to Lucene Index");
		int returnVal = fc.showDialog(view, "Add");
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;
		String dir = fc.getSelectedFile().getPath();
        try {
    		IndexWriter contentWriter = new IndexWriter(contentDir(),
    			analyzer, MaxFieldLength.UNLIMITED);
    		IndexWriter metaWriter = new IndexWriter(metaDir(),
    			analyzer, MaxFieldLength.UNLIMITED);
			makeIndex(metaWriter, contentWriter, dir);
			contentWriter.close();
			metaWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	private static void makeIndex(IndexWriter metaWriter,
		IndexWriter contentWriter, String path)
	{
		File f = new File(path);
		if (f.isDirectory()) {
			if (! validDir(path))
				return;
			String [] entries = f.list();
			for (String entry: entries)
				makeIndex(metaWriter, contentWriter,
					path + File.separatorChar + entry);
		} else {
			if (! validFile(path))
				return;
			try {
				metaWriter.addDocument(createFileDocument(f));
				BufferedReader input =  new BufferedReader(new FileReader(f));
				try {
					String line = null;
					int index = 0;
					while ((line = input.readLine()) != null) {
						index++;
						contentWriter.addDocument(createLineDocument(path, index, line));
					}
				}
				finally {
					input.close();
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static Document createLineDocument(String file, int index, String line)
	{
		Document doc = new Document();
		doc.add(new Field("file", file, Field.Store.YES, Field.Index.NO));
		doc.add(new Field("line", String.valueOf(index), Field.Store.YES,
			Field.Index.NOT_ANALYZED));
		doc.add(new Field("content", line, Field.Store.YES, Field.Index.ANALYZED));
		return doc;
	}
	private static Document createFileDocument(File f)
	{
		Document doc = new Document();
		doc.add(new Field("file", f.getPath(), Field.Store.YES, Field.Index.NO));
		doc.add(new Field("modified", String.valueOf(f.lastModified()), Field.Store.YES, Field.Index.NO));
		return doc;
	}

    public void start() {
    }

    private static class SearchResultCollector extends HitCollector
    {
    	Vector<Integer> docs = new Vector<Integer>();
    	
		@Override
		public void collect(int doc, float score) {
			docs.add(doc);
		}
    }
    public static Vector<FileLine> search(String queryString)
    throws ParseException, IOException
	{
    	Searcher searcher = new IndexSearcher(contentDir());
    	Query query = new QueryParser("content", analyzer).parse(queryString);
    	SearchResultCollector collector = new SearchResultCollector();
    	searcher.search(query, collector);
    	if (collector.docs.size() == 0)
    	{
    		JOptionPane.showMessageDialog(null,
    			"No matches were found for \"" + queryString + "\"");
    		return null;
    	}
    	Searcher metaSearcher = new IndexSearcher(metaDir());
    	Vector<FileLine> results = new Vector<FileLine>();
    	for (int i = 0; i < collector.docs.size(); i++)
    	{
    		Document doc = searcher.doc(collector.docs.get(i));
    		results.add(new FileLine(doc.get("file"),
    			Integer.valueOf(doc.get("line")).intValue(), doc.get("content")));
    	}
    	searcher.close();
    	return results; 
    }
}


