package ctags;

import java.io.IOException;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;

import options.GeneralOptionPane;
import db.TagDB;

public class Runner {

	private static final String SPACES = "\\s+";
	private TagDB db;
	private Parser parser;
	
	public Runner(TagDB db) {
		this.db = db;
		parser = null;
	}
	
	public void run(String file) {
		String ctags = GeneralOptionPane.getCtags();
		String cmd = GeneralOptionPane.getCmd();
		String tagFile = getTempTagFilePath();
		Vector<String> cmdLine = new Vector<String>();
		cmdLine.add(ctags);
		cmdLine.add("-f");
		cmdLine.add(tagFile);
		String [] customOptions = cmd.split(SPACES);
		for (int i = 0; i < customOptions.length; i++)
			cmdLine.add(customOptions[i]);
		cmdLine.add(file);
		String [] args = new String[cmdLine.size()]; 
		cmdLine.toArray(args);
		try {
			Process p = Runtime.getRuntime().exec(args);
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (parser == null)
			parser = new Parser();
		parser.parseTagFile(tagFile, db);
	}
	
	private String getTempTagFilePath() {
		return jEdit.getSettingsDirectory() + "/CtagsInterface/tags";
	}

}
