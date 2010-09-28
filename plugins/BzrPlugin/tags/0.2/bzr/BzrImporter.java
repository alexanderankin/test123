package bzr;

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
    bzr ls -R as a child process */

public class BzrImporter extends ImporterFileFilter
{
    TreeSet<String> cache;
    File cachedDirectory;
    BzrDirFilter fnf;

    static class BzrDirFilter implements FilenameFilter {
        public boolean accept(File f, String name) {
            return (name.equals(".bzr"));
        }
    }
    public BzrImporter() {
        cache = new TreeSet<String>();

        fnf = new BzrDirFilter();
    }

    public void bzrLsFiles(String path) {

        File f = new File(path);
        cachedDirectory = f.getParentFile();
        while (cachedDirectory.list(fnf).length == 0) {
            cachedDirectory = cachedDirectory.getParentFile();
            if (cachedDirectory == null) {
                Log.log(Log.ERROR, this, "Unable to find .bzr folder");
                return;
            }
        }
        Command bzr = new Command(BzrPlugin.bzrPath(), "ls", "-R");
        bzr.setWorkDir(cachedDirectory);
        // Log.log(Log.DEBUG, this, bzr.toString());
        try {
            bzr.exec();
            bzr.waitFor();
        }
        catch (IOException ioe) {
            Log.log(Log.ERROR, this, "Unable to run bzr.  ", ioe);
            return;
        }
        catch (InterruptedException ie) {}
        StringList sl = StringList.split(bzr.getOutput(), "\n");
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
        return jEdit.getProperty("bzr.importer.description", "Use bzr ls -R");
    }


    public boolean accept(VFSFile file)
    {
        if (cache.isEmpty()) {
            bzrLsFiles(file.getPath());
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
