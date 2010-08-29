package git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeSet;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.util.Log;

import projectviewer.importer.ImporterFileFilter;

public class GitImporter extends ImporterFileFilter
{
	TreeSet<String> cache;
	File cachedDirectory;
	ProcessBuilder pb;
	Process p;
	GitDirFilter fnf;
	
	static class GitDirFilter implements FilenameFilter {
		public boolean accept(File f, String name) {
			return (name.equals(".git"));			
		}
	}
	public GitImporter() {
		cache = new TreeSet<String>();
		pb = new ProcessBuilder();
		String[] cmd = new String[] {"git", "ls-files"};
		pb.command(cmd);
		fnf = new GitDirFilter();
	}
	
	public void gitLsFiles(String path) {
		
		File f = new File(path);
		cachedDirectory = f.getParentFile();
		while (cachedDirectory.list(fnf).length == 0) {
			cachedDirectory = cachedDirectory.getParentFile();
			if (cachedDirectory == null) {
				Log.log(Log.ERROR, this, "Unable to find .git folder");
				return;
			}
		}
		pb.directory(cachedDirectory);
		Log.log(Log.DEBUG, this, "gitLsFiles: " + cachedDirectory.toString());
		try {
			p = pb.start();
			p.waitFor();
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, this, "Unable to run git. exitcode= " + p.exitValue(), ioe);
			return;
		}
		catch (InterruptedException ie) {}
		InputStream is = p.getInputStream();
		InputStreamReader sr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(sr);
		try  {
			String s = br.readLine();
			while (s != null) {			
				f = new File(cachedDirectory, s);
				Log.log(Log.DEBUG, this, "Cached: " + f.toString() );
				cache.add(f.toString());
				s = br.readLine();
			}
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, this, "error", ioe);
		}
	}
	
	@Override
	public String getRecurseDescription()
	{
		return jEdit.getProperty("git.importer.description", "git entries");
	}

	@Override
	public boolean accept(VFSFile file)
	{
		if (cache.isEmpty()) {
			
			gitLsFiles(file.getPath());
		}
		return cache.contains(file.getPath());
	}

	@Override
	public String getDescription()
	{
		return getRecurseDescription();
	}
	public void done()
	{
		cache.clear();
		cachedDirectory = null;
	}

}
