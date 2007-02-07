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
package p4plugin.config;

import java.io.Serializable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.PropertiesBean;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;

/**
 *  P4 configuration stored in a project.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4Config extends PropertiesBean {

    /** For compatibility with plugin version 0.2. */
    public static final long serialVersionUID = -6061609192143296794L;

    public static final String KEY = "p4plugin.config";

    public static final int P4EDITOR_USE_GLOBAL     = 0;
    public static final int P4EDITOR_USE_CUSTOM     = 1;
    public static final int P4EDITOR_DONT_USE       = 2;

    protected static final String P4CONFIG_EDITOR_TYPE  = "p4plugin.cfg.p4EditorType";
    protected static final String P4CONFIG_EDITOR       = "p4plugin.cfg.p4Editor";
    protected static final String P4CONFIG_CLIENT       = "p4plugin.cfg.p4Client";
    protected static final String P4CONFIG_USER         = "p4plugin.cfg.p4User";

    public static P4Config getProjectConfig(View v) {
        return getProjectConfig(ProjectViewer.getActiveProject(v));
    }

    /**
     *  Returns the config for the given project.
     *
     *  @since  P4P 0.2.3
     */
    public static P4Config getProjectConfig(VPTProject proj) {
        if (proj == null)
            return null;

        // new style PropertiesBean
        String _client = proj.getProperty("p4plugin.cfg.client");
        if (_client != null) {
            try {
                P4Config cfg = new P4Config();
                cfg.load(proj.getProperties());
                return cfg;
            } catch (Exception e) {
                Log.log(Log.WARNING, P4Config.class, "corrupt P4Plugin properties: " + e.getMessage());
                return null;
            }
        }

        // old style properties.
        _client = proj.getProperty(P4CONFIG_CLIENT);
        if (_client != null) {
            try {
                P4Config cfg = new P4Config();
                cfg.setEditorConfig(Integer.parseInt(proj.getProperty(P4CONFIG_EDITOR_TYPE)));
                cfg.setEditor(proj.getProperty(P4CONFIG_EDITOR));
                cfg.setClient(_client);
                cfg.setUser(proj.getProperty(P4CONFIG_USER));
                return cfg;
            } catch (Exception e) {
                Log.log(Log.WARNING, P4Config.class, "corrupt P4Plugin properties: " + e.getMessage());
                return null;
            }
        }

        try {
            return (P4Config) proj.getObjectProperty(P4Config.KEY);
        } catch (ClassCastException cce) {
            // in case someone happens to be using a weirdly compiled
            // version of the plugin from before I set the serialVersionUID
            // field for this class.
            Log.log(Log.WARNING, P4Config.class,
                    "wrong class serial UID detected, can't read P4 configuration");
            proj.removeProperty(P4Config.KEY);
            return null;
        }
    }

    private int         p4EditorType;
    private String      p4Editor;
    private String      p4Client;
    private String      p4User;
    private String      p4Config;

    public P4Config() {
        super("p4plugin.cfg");
    }

    public int getEditorConfig() {
        return p4EditorType;
    }

    public void setEditorConfig(int val) {
        if (val >= P4EDITOR_USE_GLOBAL && val <= P4EDITOR_DONT_USE) {
            p4EditorType = val;
        }
    }

    public String getEditor() {
        return p4Editor;
    }

    public void setEditor(String cmd) {
        if (cmd != null && cmd.length() > 0) {
            p4Editor = cmd;
        } else {
            p4Editor = null;
        }
    }

    public String getUser() {
        return p4User;
    }

    public void setUser(String user) {
        if (user != null && user.length() > 0)
            p4User = user;
        else
            p4User = null;
    }

    public String getClient() {
        return p4Client;
    }

    public void setClient(String client) {
        if (client != null && client.length() > 0)
            p4Client = client;
        else
            p4Client = null;
    }

    public String getConfig() {
        return p4Config;
    }

    public void setConfig(String fname) {
        if (fname != null && fname.length() > 0)
            p4Config = fname;
        else
            p4Config = null;
    }

    /**
     *  Builds the environment array to use when calling the p4
     *  executable.
     *
     *  @since  P4P 0.3.0
     */
    public String[] getEnv() {
        Map<String,String> envp = new HashMap();
        envp.putAll(System.getenv());

        String editor = null;
        switch (p4EditorType) {
            case P4EDITOR_USE_GLOBAL:
                editor = P4GlobalConfig.getInstance().getEditor();
                break;

            case P4EDITOR_USE_CUSTOM:
                editor = getEditor();
                break;

            default:
                break;
        }

        if (editor != null) {
            envp.put("P4EDITOR", editor);
        }

        if (p4Config != null) {
            envp.put("P4CONFIG", p4Config);
            envp.remove("P4CLIENT");
        } else if (p4Client != null) {
            envp.put("P4CLIENT", p4Client);
            envp.remove("P4CONFIG");
        }

        if (p4User != null) {
            envp.put("P4USER", p4User);
        }

        if (P4GlobalConfig.getInstance().getDiffTool() != null) {
            envp.put("P4DIFF", P4GlobalConfig.getInstance().getDiffTool());
        }

        String[] env = new String[envp.size()];
        int cnt = 0;
        for (String key : envp.keySet()) {
            env[cnt++] = key + "=" + envp.get(key);
        }

        return env;
    }

}

