package ctags;

import java.io.IOException;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;

import ctags.Parser.TagHandler;

import options.GeneralOptionPane;
import db.TagDB;

public class Runner {

	private static final String SPACES = "\\s+";
	private Parser parser;
	
	public Runner(TagDB db) {
		parser = null;
	}
	
	public void runOnFile(String file, TagHandler handler) {
		Vector<String> what = new Vector<String>();
		what.add(file);
		run(what, handler);
	}
	public void runOnTree(String tree, TagHandler handler) {
		Vector<String> what = new Vector<String>();
		what.add("-R");
		what.add(tree);
		run(what, handler);
	}
	public void runOnFiles(Vector<String> files, TagHandler handler) {
		run(files, handler);
	}
	private void run(Vector<String> what, TagHandler handler) {
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
		cmdLine.addAll(what);
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
		parser.parseTagFile(tagFile, handler);
	}
	
	private String getTempTagFilePath() {
		return jEdit.getSettingsDirectory() + "/CtagsInterface/tags";
	}

}
