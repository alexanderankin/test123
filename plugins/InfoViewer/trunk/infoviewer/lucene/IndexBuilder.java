package infoviewer.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.ListModel;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.index.IndexModifier;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.swing.models.BaseListModel;
import org.apache.lucene.swing.models.ListSearcher;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.util.Log;


/**
 * Builds an index of documentation for the InfoViewer
 * using Lucene. 
 * 
 * @author ezust 
 */
public class IndexBuilder implements EBComponent 
{
	private static IndexBuilder smInstance = null;

	
	Analyzer analyzer = new StandardAnalyzer();
	QueryParser parser = new QueryParser("content", analyzer);
	IndexReader indexReader;
	IndexModifier indexModifier;
	Searcher searcher;
	Query query;

	/**
	 * @return a singleton instance of IndexBuilder
	 */
	public static IndexBuilder instance() {
		if (smInstance == null)
			smInstance = new IndexBuilder();
		return smInstance;
	}
	
	static String userDir = jEdit.getProperty("user.dir");
	
	public static String getPath(String indexName) {
		return MiscUtilities.constructPath(userDir, "lucene", indexName);
	}
	
	public static ListModel hitsToModel(Hits hits) {
	        BaseListModel baseListModel = new BaseListModel(hits.iterator());
	        return new ListSearcher(baseListModel);
	}
	
	/**
	 * Do not create an IndexBuilder directly.
	 * @see instance() instead.
	 *
	 */
	private IndexBuilder() 
	{
		initWriters();
		indexStaticHelp();
		indexPlugins();
		initReaders();

		EditBus.addToBus(this);
	}
	
	
	void initWriters() {
		indexModifier = getWriter("help");
	}
	
	void initReaders() 
	{
		indexReader = getReader("help");
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
			Log.log(Log.ERROR, this, "Unable to parse: " + queryString , pe);
			
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, this, "Unable to search index", ioe);
		}
		return null;
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
				Log.log(Log.ERROR, this, "can't create index directory: " + path, ioe1);
			}
		}
		
		return indexModifier;
	}

	void indexStaticHelp()
	{
		String jEditHome = jEdit.getJEditHome();
		if(jEditHome != null)
		{
			String path = MiscUtilities.constructPath(jEditHome,"doc","users-guide");
			Field nameField = new Field("name", "jEdit Users Guide", Store.YES, Index.NO);
			Field groupField = new Field("group", "help", Store.YES, Index.NO);
			Field fields[] = new Field[] {nameField, groupField};
			indexDirectory(fields, path);
		}

	}
	
	void indexPlugins() {
		EditPlugin[] plugins = jEdit.getPlugins();
		for (EditPlugin plugin: plugins) try {
			indexJAR(plugin);
		}
		catch (IOException ioe) 
		{
			Log.log(Log.ERROR,this,"Error indexing plugin docs: " + plugin.getClassName(), ioe);
		}
		
		
	}
	

	
	public static boolean isDoc(String fileName) 
	{
		fileName = fileName.toLowerCase();
		if (fileName.endsWith(".html")) return true;
		if (fileName.endsWith(".txt")) return true;
		return false;
	}
	
	void indexJAR( EditPlugin plugin) throws IOException 
	{
		String className = plugin.getClassName();
		String name = jEdit.getProperty("plugin." + className + ".name");
		
		Field classField = new Field("className", className, Store.YES, Index.NO);
		Field nameField = new Field("name", name, Store.YES, Index.NO);
		Field groupField = new Field("group", "plugins", Store.YES, Index.NO);
		Field[] fields = new Field[]{classField, nameField, groupField};
		IndexModifier pluginIndex = getWriter("help");
		
		// Remove any documents for this plugin that were indexed before
		Term t = new Term("name", name);
		pluginIndex.deleteDocuments(t);

		// Index all document files in this jar
		File file = plugin.getPluginJAR().getFile();
		JarFile jarFile = new JarFile(file);
		Enumeration<JarEntry> files = jarFile.entries();
		while (files.hasMoreElements()) 
		{
			JarEntry entry = files.nextElement();
			String fileName = entry.getName();
			if (isDoc(fileName)) 
			{
				String url = "jeditresource:/" + 
					MiscUtilities.getFileName(jarFile.getName()) +"!/" + fileName;
				Reader reader = new InputStreamReader(new URL(url).openStream());
				Field contentField = new Field("content", reader);
				Field urlField = new Field("url", url, Store.YES, Index.NO);
				Document doc = new Document();
				for (Field f: fields) doc.add(f);
				doc.add(contentField);
				doc.add(urlField);
				pluginIndex.addDocument(doc);
			}
		}

		
	}


	/**
	 * Indexes a directory of files.
	 * 
	 * @param fields - the common fields that should be added to each document
	 *               in this directory.
	 * @param dir the directory to search for .txt and .html files
	 */
	void indexDirectory(Field[] fields, String dir)  
	{
		
		String[] files = null;
		try {
			files = VFSManager.getFileVFS()._listDirectory(null,dir,"*.{html,txt}",true,null);
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, ioe, "Unable to index directory: " + dir);
			return;
		}
		for (String fileName: files) try 
		{
			File f = new File(fileName);
			
			FileReader fr = new FileReader(f);
			Field content= new Field("content", fr);
			Field url = new Field("url", "file://" + f.getCanonicalPath(), Store.YES, Index.NO);
			Document doc = new Document();
			for (Field field: fields) doc.add(field);
			doc.add(content);
			doc.add(url);
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, ioe, "Unable to index file: " + fileName, ioe);
		}
	}

	public void handleMessage(EBMessage message)
	{
		if (message instanceof PluginUpdate) {
			PluginUpdate pu = (PluginUpdate) message;
			
			if (pu.getWhat() == PluginUpdate.UNLOADED) {
				// TODO - remove old Document from index
				
			}
			if (pu.getWhat() == PluginUpdate.LOADED) {
				// TODO - add new Document to index
			}
		}
	}
}


