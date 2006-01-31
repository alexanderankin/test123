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

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.msg.PluginUpdate;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;

import p4plugin.config.P4Config;
import p4plugin.config.P4GlobalConfig;

/**
 *  The main plugin class. Takes care of handling EditBus messages.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4Plugin extends EBPlugin {

    private BufferKeyHandler keyHandler = new BufferKeyHandler();

    public void start() {
        // make sure ProjectViewer is loaded. This is needed because
        // jEdit's JARClassLoader (or is it beanshell?) seems to mess
        // things up when loading P4's actions (which reference PV
        // classes) when PV is not yet loaded.
        ProjectManager.getInstance();
    }

    /**
     *  Monitors when read-only buffers are loaded and set a key
     *  interceptor if the buffer path belongs to the current
     *  project and the project is using perforce.
     */
    public void handleMessage(EBMessage msg) {
        if (P4GlobalConfig.getInstance().getMonitorFiles()
            && !(msg instanceof PluginUpdate))
        {
            View v = jEdit.getActiveView();
            if (v != null) {
                Buffer b = v.getBuffer();
                VPTProject proj = ProjectViewer.getActiveProject(v);
                if (b.isReadOnly()
                    && proj != null
                    && proj.getChildNode(b.getPath()) != null
                    && P4Config.getProjectConfig(v) != null)
                {
                    v.setKeyEventInterceptor(keyHandler);
                } else if (v.getKeyEventInterceptor() == keyHandler) {
                    v.setKeyEventInterceptor(null);
                } else {
                    keyHandler.removeInterceptor = true;
                }
            }
        }
    }

}

