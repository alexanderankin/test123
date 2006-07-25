package infoviewer.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

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
 * @author ezust 
 * 
 * based on org.gjt.sp.jedit.help.HelpIndex
 *
 */
public class IndexBuilder implements EBComponent 
{
	/**
	 * Static help that never changes from one installation to another
	 */
	ArrayList<Document> jEditHelp = new ArrayList<Document>(5);
	
	/**
	 * Help that is loaded from plugin jars
	 */
	ArrayList<Document> pluginJarDoc = new ArrayList<Document>(50);;
	/**
	 * Help that is added by the user
	 */
	ArrayList<Document> userDocs = new ArrayList<Document>(50);
	
	IndexWriter helpIndex;
	IndexWriter pluginIndex;
	IndexWriter userIndex;
	
	static IndexBuilder smInstance = null;
	public static IndexBuilder instance() {
		if (smInstance == null)
			smInstance = new IndexBuilder();
		return smInstance;
	}
	
	private IndexBuilder() 
	{
		initWriters();
		indexStaticHelp();
		indexPlugins();
		EditBus.addToBus(this);
		
	}
	
	void initWriters() {
		helpIndex = createWriter("help");
		pluginIndex = createWriter("plugins");
		userIndex = createWriter("user");
	}
	
	IndexWriter createWriter( String name) {
		IndexWriter writer = null;
		String path = MiscUtilities.constructPath(jEdit.getProperty("user.dir"), "lucene", name);
		Analyzer sa = new StandardAnalyzer();
		Directory d = null;
		try {
			d = FSDirectory.getDirectory(path, false);
			writer = new IndexWriter(d, sa, false);
		}
		catch (IOException ioe) { // Directory doesn't exist
			try {
				d = FSDirectory.getDirectory(path, true);
				writer = new IndexWriter(d, sa, true);
			}
			catch (IOException ioe1) {
				Log.log(Log.ERROR, ioe1, "can't create index directory: " + path);
			}
		}
		return writer;
	}

	public void indexStaticHelp()
	{
		String jEditHome = jEdit.getJEditHome();
		if(jEditHome != null)
		{
			String path = MiscUtilities.constructPath(jEditHome,"doc","users-guide");
			Document userGuide = new Document();
			Field title = new Field("title", "jEdit Users Guide", Field.Store.NO, Field.Index.NO);
			userGuide.add(title);
			indexDirectory(userGuide, path);
			jEditHelp.add(userGuide);
			try {
				helpIndex.addDocument(userGuide);
			}
			catch (IOException ioe) {
				Log.log(Log.ERROR, ioe, "Unable to index users guide");
			}
		}

	}
	
	public void indexPlugins() {
		
		EditPlugin[] plugins = jEdit.getPlugins();
		for (EditPlugin plugin: plugins) try {
			String name = plugin.getClassName();
			String label = jEdit.getProperty("plugin." + name + ".name");
			Document doc = new Document();
			Field title = new Field("title", label, Field.Store.NO, Field.Index.NO);
			doc.add(title);
			indexJAR(doc, plugin.getPluginJAR().getPath());
			pluginJarDoc.add(doc);
			pluginIndex.addDocument(doc);
		}
		catch (IOException ioe) 
		{
			Log.log(Log.ERROR,this,"Error indexing plugin docs: " + plugin.getClassName());
			Log.log(Log.ERROR,ioe,ioe);
		}
		
		
	}
	

	
	public static boolean isDoc(String fileName) 
	{
		fileName = fileName.toLowerCase();
		if (fileName.endsWith(".html")) return true;
		if (fileName.endsWith(".txt")) return true;
		return false;
	}
	
	public void indexJAR(Document doc, String path) throws IOException 
	{
		JarFile jarFile = new JarFile(path);
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
				Field f = new Field(fileName, reader);
				doc.add(f);
			}
		}
	}


	/**
	 * Indexes a directory of files.
	 * 
	 * @param doc a document to add fields to
	 * @param dir the directory to search for .txt and .html files
	 */
	public void indexDirectory(Document doc, String dir)  
	{
		
		String[] files = null;
		try {
			files = VFSManager.getFileVFS()._listDirectory(null,dir,"*.{html,txt}",true,null);
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, ioe, "Unable to index directory: " + dir);
			return;
		}
		for (String file: files) try 
		{
			File f = new File(file);
			FileReader fr = new FileReader(f);
			Field field = new Field(file, fr);
			doc.add(field);
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, ioe, "Unable to index file: " + file);
		}
	}

	public void handleMessage(EBMessage message)
	{
		if (message instanceof PluginUpdate) {
			PluginUpdate pu = (PluginUpdate) message;
			if (pu.getWhat() == PluginUpdate.UNLOADED) {
				
			}
			if (pu.getWhat() == PluginUpdate.LOADED) {
				
			}
		}
	}
}