package ctags;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import options.GeneralOptionPane;

import org.gjt.sp.jedit.jEdit;

public class Runner {

	private static final String SPACES = "\\s+";
	
	// Runs Ctags on a single file. Returns the tag file.
	public String runOnFile(String file) {
		Vector<String> what = new Vector<String>();
		what.add(file);
		return run(what);
	}
	// Runs Ctags on a source tree. Returns the tag file.
	public String runOnTree(String tree) {
		Vector<String> what = new Vector<String>();
		what.add("-R");
		what.add(tree);
		return run(what);
	}
	// Runs Ctags on a list of files. Returns the tag file.
	public String runOnFiles(Vector<String> files) {
		String fileList = getTempFileListPath();
		try {
			PrintWriter w = new PrintWriter(new FileWriter(fileList));
			for (int i = 0; i < files.size(); i++)
				w.println(files.get(i));
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not create the file list file for Ctags");
			return null;
		}
		Vector<String> what = new Vector<String>();
		what.add("-L");
		what.add(fileList);
		return run(what);
	}
	private String run(Vector<String> what) {
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
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return tagFile;
	}
	private String getTempDirectory() {
		return jEdit.getSettingsDirectory() + "/CtagsInterface";
	}
	private String getTempFileListPath() {
		return getTempDirectory() + "/files.txt";
	}
	private String getTempTagFilePath() {
		return getTempDirectory() + "/tags";
	}

}
