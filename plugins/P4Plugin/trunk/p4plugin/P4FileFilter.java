/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2005 Marcelo Vanzin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package p4plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import projectviewer.importer.ImporterFileFilter;

import p4plugin.config.P4Config;

/**
 *  File filter that uses perforce to define which files to
 *  import into a project. This is probably gonna be slow, and
 *  error reporting from a file filter is lacking to say the least.
 *
 *  <h4>How a p4 path is mapped to the local directory structure</h4>
 *
 *  <p>The firts thing is to analyze the client configuration. This
 *  gives us two important pieces of information: the client root -
 *  the local path where all the client files go when checked out of
 *  perforce, and the client views. The views map a directory inside
 *  the perforce repository to a local directory under the client
 *  root.</p>
 *
 *  <p>With this information available, the "p4 files" command lists
 *  all the (Perforce) files paths for a given (local or Perforce)
 *  path. Using the "view mappings" from the client spec we can then
 *  figure out in which local directory a certain file should be.</p>
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4FileFilter extends ImporterFileFilter implements Perforce.Visitor {

    private static final int VISITING_FILES     = 0;
    private static final int VISITING_CLIENT    = 1;

    private String  clientRoot;
    private Map     entries;
    private Map     views;

    private int     visiting;
    private boolean inViews;

    public String getRecurseDescription() {
        return jEdit.getProperty("p4plugin.file_filter_desc");
    }

    public String getDescription() {
        return getRecurseDescription();
    }

    public boolean accept(File f) {
        return f.isDirectory() || accept(f.getParentFile(), f.getName());
    }

    public boolean accept(File dir, String name) {
        File f = new File(dir.getAbsolutePath(), name);
        if (f.isDirectory()) return true;

        if (entries == null || !entries.containsKey(dir.getAbsolutePath()))
            findEntries(dir.getAbsolutePath());

        List files = (List) entries.get(dir.getAbsolutePath());
        if (files != null)
            return files.contains(name);
        return false;
    }

    /**
     *  Calls "p4 files [dirpath]" and buils the entries map with the
     *  list of files returned by perforce for each directory.
     *
     *  <p>dirpath should be under the client root. If it's not, it will
     *  be added to the entries array with a "null" list.</p>
     */
    private void findEntries(String dirpath) {
        if (entries == null)
            entries = new HashMap();

        if (clientRoot == null && !findClientRoot()) {
            entries.put(dirpath, null);
            return;
        }

        if (!dirpath.startsWith(clientRoot)) {
            entries.put(dirpath, null);
            return;
        }

        // call p4 files <directory>
        Perforce p4 = new Perforce("files", new String[] { dirpath + "/..." });
        try {
            p4.exec(jEdit.getActiveView()).waitFor();
        } catch (Exception e) {
            Log.log(Log.ERROR, this, e);
            entries.put(dirpath, null);
            return;
        }

        if (!p4.isSuccess()) {
            Log.log(Log.ERROR, this, p4.getError());
            entries.put(dirpath, null);
            return;
        }

        // break the list into individual directories.
        entries.put(dirpath, new ArrayList());
        visiting = VISITING_FILES;
        p4.processOutput(this);

    }

    /**
     *  Calls "p4 -o $P4CLIENT" to figure out what is the client root.
     *
     *  @return     Whether the root was successfully found based on the
     *              currently active project's configuration.
     */
    private boolean findClientRoot() {
        P4Config cfg = P4Config.getProjectConfig(jEdit.getActiveView());
        if (cfg == null) {
            // if no project, set status bar text to error
            // if project doesn't use peforce, set status bar text to error
            return false;
        }

        if (cfg.getClient() == null) {
            // if config has no client, try to read $P4CLIENT
            return false;
        }

        String[] args = new String[] { "-o", cfg.getClient() };
        Perforce cmd = new Perforce("client", args);
        try {
            cmd.exec(jEdit.getActiveView()).waitFor();
        } catch (Exception e) {
            Log.log(Log.WARNING, this, e);
            return false;
        }

        // parse the output looking for the "Root:" entry and the client's views.
        views       = new HashMap();
        inViews     = false;
        visiting    = VISITING_CLIENT;
        cmd.processOutput(this);

        return (clientRoot != null);
    }

    /**
     *  Finds the local path for the given perforce path, based on the
     *  "views" information read from the client configuration. The adds
     *  the given file name to the list of files of that path.
     */
    private void addPath(String p4path) {
        // break the path into smaller paths and return the first match.
        StringTokenizer st = new StringTokenizer(p4path, "/");
        StringBuffer curr = new StringBuffer(st.nextToken());
        while (!views.containsKey(curr.toString()) && st.hasMoreTokens())
            curr.append("/").append(st.nextToken());

        String dirName = (String) views.get(curr.toString());
        curr.setLength(0);
        curr.append(clientRoot).append("/").append(dirName);

        String fname = null;
        while (st.hasMoreTokens()) {
            String next = st.nextToken();
            if (st.hasMoreTokens()) {
                curr.append("/").append(next);
            } else {
                fname = next;
            }
        }

        List files = (List) entries.get(curr.toString());
        if (files == null) {
            files = new ArrayList();
            entries.put(curr.toString(), files);
        }

        if (fname != null) {
            files.add(fname);
        }
    }

    public boolean process(String line) {
        switch (visiting) {

            case VISITING_CLIENT:
                if (inViews) {
                    StringTokenizer view = new StringTokenizer(line);
                    if (view.countTokens() == 2) {
                        String p4path = view.nextToken().substring(2);
                        p4path = p4path.substring(0, p4path.length() - 4);
                        String localPath = view.nextToken();
                        localPath = localPath.substring(localPath.indexOf("/", 2) + 1);
                        localPath = localPath.substring(0, localPath.length() - 4);
                        views.put(p4path, localPath);
                    }
                } else if (line.startsWith("Root:")) {
                    clientRoot = line.substring(5, line.length()).trim();
                } else if (line.startsWith("View:")) {
                    inViews = true;
                }
                break;

            case VISITING_FILES:
                line = line.substring(2, line.indexOf("#"));
                addPath(line);
                break;

            default:
                // will not happen.

        }

        return true;
    }

}

