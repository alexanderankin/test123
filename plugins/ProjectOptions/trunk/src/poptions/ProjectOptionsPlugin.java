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
package poptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;

import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.PropertiesChanging;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;

/**
 *  The main plugin class.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      POP 0.1.0
 */
public class ProjectOptionsPlugin extends EBPlugin {

    private boolean             ignoreChange;
    private String              activeProjectName;
    private Map<String,String>  savedProperties;

    public void start() {
        ignoreChange = false;
        // check if there's an active project and load it's custom
        // properties
        VPTProject p = ProjectViewer.getActiveProject(jEdit.getActiveView());
        if (p != null && !p.getName().equals(activeProjectName)) {
            setProjectOptions(p);
        }
    }

    public void stop() {
        // restore the global options before jEdit exits, so that
        // they're correctly saved.
        restoreGlobalOptions(true);
    }

    /**
     *  When a PropertiesChanging message is sent on the EditBus,
     *  restore the global properties so that the user can edit
     *  them. When it's done, restore the active project's properties,
     *  if any.
     */
    public void handleMessage(EBMessage msg)
    {
        if (msg instanceof PropertiesChanging) {
            switch (((PropertiesChanging)msg).getState()) {
                case LOADING:
                    restoreGlobalOptions(false);
                    break;

                case CANCELED:
                    restoreProjectOptions();
                    break;
            }
        } else if (!ignoreChange && msg instanceof PropertiesChanged) {
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        restoreProjectOptions();
                    }
                }
            );
        }
    }

    protected void restoreGlobalOptions(boolean send)
    {
        if (savedProperties != null) {
            System.err.println("restoring global opts");
            for (String key : savedProperties.keySet()) {
                System.err.printf("restoring: %s to %s (from %s)", key,
                    savedProperties.get(key), jEdit.getProperty(key));
                jEdit.setProperty(key, savedProperties.get(key));
            }
            savedProperties = null;
        }
        if (send) {
            sendChangeMsg();
        }
    }

    protected void restoreProjectOptions()
    {
        if (activeProjectName != null) {
            VPTProject p = ProjectManager.getInstance()
                                .getProject(activeProjectName);
            if (p != null) {
                setProjectOptions(p);
            }
        }
    }

    protected void setProjectOptions(VPTProject p)
    {
        boolean changed = false;
        boolean restore = true;

        if (p != null) {
            Properties popts = p.getProperties();
            boolean enabled = "true".equalsIgnoreCase(popts.getProperty("poptions.enabled"));
            if (enabled) {
                System.err.println("setting project options for: " + p.getName());
                savedProperties = null;
                for (Object okey : popts.keySet()) {
                    String key = (String) okey;
                    if (key.startsWith("poptions.") &&
                        !key.equals("poptions.enabled"))
                    {
                        String jkey = key.substring(9);
                        if (savedProperties == null) {
                            savedProperties = new HashMap<String,String>();
                        }
                        System.err.printf("setting: %s to %s (old = %s)",
                            jkey, popts.getProperty(key),
                            jEdit.getProperty(jkey));
                        savedProperties.put(jkey, jEdit.getProperty(jkey));
                        jEdit.setProperty(jkey, popts.getProperty(key));
                        changed = true;
                    }
                }
                activeProjectName = p.getName();
                restore = false;
            }
        }

        if (restore) {
            System.err.println("no project selected");
            restoreGlobalOptions(false);
            activeProjectName = null;
            changed = true;
        }

        if (changed) {
            sendChangeMsg();
        }
    }

    private void sendChangeMsg() {
        System.err.println("sending edit bus message");
        ignoreChange = true;
        jEdit.propertiesChanged();
        ignoreChange = false;
    }

}

