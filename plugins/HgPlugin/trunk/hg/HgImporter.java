package hg;

import hg.DirState.DirStateEntry;

import java.io.File;
import java.io.FilenameFilter;
import java.util.TreeSet;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.util.Log;

import projectviewer.importer.ImporterFileFilter;

/** A ProjectViewer file importer service that returns 
    mercurial entries */

public class HgImporter extends ImporterFileFilter
{
    DirState dirState;
    File cachedDirectory;
    HgDirFilter fnf;
    TreeSet<String> cache;    
    
    static class HgDirFilter implements FilenameFilter {
        public boolean accept(File f, String name) {
            return (name.equals(".hg"));
        }
    }
    
    public HgImporter() {
        dirState = null;
        cache = new TreeSet<String>();
        cachedDirectory = null;
        fnf = new HgDirFilter();
    }

    public void hgLsFiles(String path) {
        cachedDirectory = new File(path);
        if (!cachedDirectory.isDirectory()) cachedDirectory = cachedDirectory.getParentFile();
        while (cachedDirectory.list(fnf).length == 0) {
            cachedDirectory = cachedDirectory.getParentFile();
            if (cachedDirectory == null) {
                Log.log(Log.ERROR, this, "Unable to find .hg folder in " + path);
                return;
            }
        }
        dirState = new DirState(cachedDirectory);
        for (DirStateEntry entry: dirState.getDirState()) {
            File f = new File(cachedDirectory, entry.getPath());
			Log.log(Log.DEBUG, this, "Cached: " + f.toString() );
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
        return jEdit.getProperty("hg.importer.description", "Use .hg/dirstate");
    }


    public boolean accept(VFSFile file)
    {
        if (dirState == null) {
            hgLsFiles(file.getPath());
        }
        boolean retval = cache.contains(file.getPath());
        Log.log(Log.DEBUG, this, "accepts " + file.getPath() +  "? " + retval);
        return retval;
    }


    public String getDescription()
    {
        return getRecurseDescription();
    }
    public void done()
    {
        dirState = null;
        cachedDirectory = null;
        cache.clear();
    }

}
