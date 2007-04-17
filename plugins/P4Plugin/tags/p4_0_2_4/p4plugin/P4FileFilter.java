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

    private Map<String,List>    entries;
    private Map<String,String>  views;
    private P4ClientInfo        clientInfo;

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
        if (clientInfo != null && !clientInfo.isSuccess())
            return false;

        File f = new File(dir.getAbsolutePath(), name);
        if (f.isDirectory()) return true;

        if (entries == null || !entries.containsKey(dir.getAbsolutePath()))
            findEntries(dir.getAbsolutePath());

        List files = entries.get(dir.getAbsolutePath());
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
        if (entries == null) {
            entries = new HashMap<String,List>();
        }

        if (clientInfo == null && !findClientRoot()) {
            return;
        }

        if (clientInfo.getClientRoot() == null
            || !clientInfo.isSuccess())
        {
            entries.put(dirpath, null);
            return;
        }

        if (!dirpath.startsWith(clientInfo.getClientRoot()))
        {
            entries.put(dirpath, null);
            return;
        }

        /*
         * Before calling perforce, do a "brute force" search in the
         * current known entries looking for a parent directory that
         * has been registered as not having any files. This avoids
         * calling p4 repeatedly for directories that are not under
         * version control, speeding up the process tremendously.
         */
        File parent = new File(dirpath).getParentFile();
        String parentPath = parent.getAbsolutePath();
        while (parentPath.length() > clientInfo.getClientRoot().length()) {
            if (entries.containsKey(parentPath)) {
                if (entries.get(parentPath) == null) {
                    /*
                     * Directory is not under a version-control-managed
                     * branch of the project's tree, so just ignore it.
                     */
                    entries.put(dirpath, null);
                    return;
                } else {
                    break;
                }
            }
            parent = parent.getParentFile();
            parentPath = parent.getAbsolutePath();
        }

        // call p4 files <directory>
        Perforce p4 = new Perforce("files", new String[] { dirpath + "/..." });
        p4.setVisitor(this);
        entries.put(dirpath, null);
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
            jEdit.getActiveView().getStatus().setMessageAndClear(
                jEdit.getProperty("p4plugin.filter.no_config"));
            return false;
        }

        clientInfo = new P4ClientInfo(cfg.getClient());

        if (!clientInfo.fetch()) {
            return false;
        }

        return (clientInfo.getClientRoot() != null);
    }

    /**
     *  Finds the local path for the given perforce path, based on the
     *  "views" information read from the client configuration. The adds
     *  the given file name to the list of files of that path.
     */
    private void addPath(String p4path) {
        File lpath = new File(clientInfo.translate(p4path));
        String fname = lpath.getName();
        String dirname = lpath.getParent();

        List files = entries.get(dirname);
        if (files == null) {
            files = new ArrayList();
            entries.put(dirname, files);
        }

        files.add(fname);
    }

    public boolean process(String line) {
        int revIdx = line.indexOf("#");
        int actIdx = -1;
        if (revIdx >= 0) {
            actIdx = line.indexOf("-", revIdx) + 2;
        }

        if (actIdx >= 0) {
            String action = line.substring(actIdx, line.indexOf(" ", actIdx));
            if (!action.equals("delete")) {
                line = line.substring(2, revIdx);
                addPath(line);
            }
        }
        return true;
    }

}

