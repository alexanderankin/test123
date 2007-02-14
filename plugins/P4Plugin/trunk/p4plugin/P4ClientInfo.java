/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2007 Marcelo Vanzin
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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 *  Fetches info about a perforce client. Most methods in this class
 *  will misbehave before {@link #fetch()} is called.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.2.3
 */
public class P4ClientInfo implements Perforce.Visitor {

    private boolean cmdok;
    private String clientName;
    private String clientRoot;

    private boolean inViews;
    private Map<String,String> views;

    /**
     *  Fetches info about the client rooted at the active project's
     *  directory. The directory should contain the P4CONFIG in its
     *  hierarchy somewhere, so "p4" knows what client it's using.
     */
    public P4ClientInfo() {
        this(null);
    }

    /**
     *  Fetches info about the given client.
     *
     *  @param  cname   The client's name, or null to use P4CONFIG.
     */
    public P4ClientInfo(String cname) {
        this.clientName = cname;
        this.cmdok      = false;
    }

    /**
     *  Runs "p4 client -o $cname" and parses the output, populating
     *  the internal values.
     *
     *  @return Whether the command was successful.
     */
    public boolean fetch() {
        this.views = new HashMap<String,String>();
        this.clientRoot = null;

        String[] args;
        if (clientName != null) {
            args = new String[] { "-o", clientName };
        } else {
            args = new String[] { "-o" };
        }
        Perforce cmd = new Perforce("client", args);
        cmd.setVisitor(this);
        inViews = false;
        try {
            cmd.exec(jEdit.getActiveView()).waitFor();
        } catch (Exception e) {
            Log.log(Log.WARNING, this, e);
            return false;
        }

        cmdok = cmd.isSuccess();
        if (!cmdok) {
            cmd.showError(jEdit.getActiveView());
        }

        System.err.println("P4ClientInfo:");
        System.err.println("Name: " + clientName);
        System.err.println("Root: " + clientRoot);
        for (String key : views.keySet()) {
            System.err.println("View: " + key + " = " + views.get(key));
        }

        return cmdok;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientRoot() {
        return clientRoot;
    }

    public boolean isSuccess() {
        return cmdok;
    }

    /**
     *  Returns the views mapped in the client spec. This maps the
     *  depot path to the corresponding local path, relative to the
     *  client's root.
     */
    public Map<String,String> getViews() {
        return views;
    }

    /**
     *  Translates a depot path into a local path according to the info
     *  gathered from the views in the client's spec.
     *
     *  @param  depotPath   The depot path (//path/to/file).
     *  @return Absolute path to the local copy.
     */
    public String translate(String depotPath) {
        StringTokenizer st = new StringTokenizer(depotPath, "/");
        StringBuilder fpath = new StringBuilder(st.nextToken());
        while (!views.containsKey(fpath.toString()) && st.hasMoreTokens())
            fpath.append("/").append(st.nextToken());

        String dirName = (String) views.get(fpath.toString());
        fpath.setLength(0);
        fpath.append(clientRoot);
        if (dirName.length() > 0) {
            fpath.append("/").append(dirName);
        }

        while (st.hasMoreTokens()) {
            fpath.append("/").append(st.nextToken());
        }
        return fpath.toString();
    }

    public boolean process(String line) {
        if (inViews) {
            StringTokenizer view = new StringTokenizer(line);
            if (view.countTokens() == 2) {
                String p4path = view.nextToken().substring(2);
                p4path = p4path.substring(0, p4path.length() - 4);
                String localPath = view.nextToken();
                localPath = localPath.substring(localPath.indexOf("/", 2) + 1);
                if ("...".equals(localPath)) {
                    // edge case: the root of the client
                    localPath = "";
                } else {
                    localPath = localPath.substring(0, localPath.length() - 4);
                }
                views.put(p4path, localPath);
            }
        } else if (line.startsWith("Root:")) {
            clientRoot = line.substring(5).trim();
        } else if (line.startsWith("View:")) {
            inViews = true;
        } else if (line.startsWith("Client:") && clientName == null) {
            clientName = line.substring(8).trim();
        }
        return true;
    }

}

