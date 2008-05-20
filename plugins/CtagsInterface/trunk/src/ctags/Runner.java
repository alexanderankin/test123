package ctags;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import options.GeneralOptionPane;

import org.gjt.sp.jedit.jEdit;

public class Runner {

	private static final String SPACES = "\\s+";
	private static Set<String> tempFiles;
	
	{
		tempFiles = new HashSet<String>();
	}
	
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
		String fileList = getTempFile("files");
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
		String tagFile = run(what);
		releaseFile(fileList);
		return tagFile;
	}
	// Tag file no longer needed
	public void releaseFile(String file) {
		synchronized (tempFiles) {
			tempFiles.remove(file);
		}
	}
	private String run(Vector<String> what) {
		String ctags = GeneralOptionPane.getCtags();
		String cmd = GeneralOptionPane.getCmd();
		String tagFile = getTempFile("tags");
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
	private String getTempFile(String prefix) {
		synchronized (tempFiles) {
			String tempDir = getTempDirectory();
			File d = new File(tempDir);
			if (! d.exists())
				d.mkdirs();
			for (int i = 0; i < 100; i++) {
				String path = tempDir + "/" + prefix + i; 
				if (tempFiles.add(path))
					return path;
			}
		}
		return null; 
	}

}
