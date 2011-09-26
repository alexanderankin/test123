package ise.plugin.svn.pv;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.TreeSet;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;

import projectviewer.importer.ImporterFileFilter;


/** Import file filter for ProjectViewer.
    Uses command-line svn to do its job, instead of 
    relying on a specific version of entries or metadata. 
    
    Based on BzrPlugin by Alan Ezust    
*/
    
public class ImportFilter extends ImporterFileFilter {
    	    
    TreeSet<String> cache;
    File cachedDirectory;
    SvnDirFilter fnf;
    boolean cantFind;

    static class SvnDirFilter implements FilenameFilter {
        public boolean accept(File f, String name) {
            return (name.equals(".svn"));
        }
    }
    public ImportFilter() {
        cache = new TreeSet<String>();
        cantFind=false;

        fnf = new SvnDirFilter();
    }

    public void svnLsFiles(String path) {

        File f = new File(path);
        cachedDirectory = f.getParentFile();
        while (cachedDirectory.list(fnf).length == 0) {
            cachedDirectory = cachedDirectory.getParentFile();
            if (cachedDirectory == null) {
                Log.log(Log.ERROR, this, "Unable to find .svn folder");
                cantFind=true;
                return;
            }
        }
        Command svn = new Command(jEdit.getProperty("svn.path", "svn"), "ls", "-R");
        svn.setWorkDir(cachedDirectory);
        Log.log(Log.DEBUG, this, svn.toString());
        try {
            svn.exec();
            svn.waitFor();
        }
        catch (IOException ioe) {
            Log.log(Log.ERROR, this, "Unable to run svn.  ", ioe);
            return;
        }
        catch (InterruptedException ie) {}
        StringList sl = StringList.split(svn.getOutput(), "\n");
        for (String s: sl) {
            f = new File(cachedDirectory, s);
//			Log.log(Log.DEBUG, this, "Cached: " + f.toString() );
            cache.add(f.toString());

            // add its directory also
            while (!f.getParentFile().equals(cachedDirectory)) {
                if (!cache.contains(f.getParent()))
                    cache.add(f.getParent());
                f = f.getParentFile();
            }

        }
    }

    @Override
    public String getRecurseDescription()
    {
        return jEdit.getProperty("svn.importer.description", "Use svn ls -R");
    }


    public boolean accept(VFSFile file)
    {
        if (cache.isEmpty() && !cantFind) {
        	svnLsFiles(file.getPath());
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
        cantFind=false;
        cachedDirectory = null;
    }

}

