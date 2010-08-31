package git;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.TreeSet;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;

import projectviewer.importer.ImporterFileFilter;

/** A ProjectViewer file importer service that runs the 
    git ls-files as a child process */

public class GitImporter extends ImporterFileFilter
{
	TreeSet<String> cache;
	File cachedDirectory;
	GitDirFilter fnf;
	
	static class GitDirFilter implements FilenameFilter {
		public boolean accept(File f, String name) {
			return (name.equals(".git"));			
		}
	}
	public GitImporter() {
		cache = new TreeSet<String>();

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
		Command git = new Command(GitPlugin.gitPath(), "ls-files");
		git.setWorkDir(cachedDirectory);
		// Log.log(Log.DEBUG, this, git.toString());
		try {
			git.exec();
			git.waitFor();
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, this, "Unable to run git.  ", ioe);
			return;
		}
		catch (InterruptedException ie) {}
		StringList sl = StringList.split(git.getOutput(), "\n");
		for (String s: sl) {		
			f = new File(cachedDirectory, s);
//			Log.log(Log.DEBUG, this, "Cached: " + f.toString() );
			cache.add(f.toString());
			// add its directory also
			if (!cache.contains(f.getParent()))cache.add(f.getParent());
		}
	}
	
	@Override
	public String getRecurseDescription()
	{
		return jEdit.getProperty("git.importer.description", "Use git ls-files");
	}

	
	public boolean accept(VFSFile file)
	{
		if (cache.isEmpty()) {
			gitLsFiles(file.getPath());
		}
		boolean retval = cache.contains(file.getPath()); 
//		Log.log(Log.DEBUG, this, "accepts " + file.getPath() +  "? " + retval);
		return retval;
	}

	
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
