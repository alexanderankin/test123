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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;

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

    private static final int MODE_FILE_VERSION = 1;

    private BufferKeyHandler keyHandler = new BufferKeyHandler();

    public void start() {
        // make sure ProjectViewer is loaded. This is needed because
        // jEdit's JARClassLoader (or is it beanshell?) seems to mess
        // things up when loading P4's actions (which reference PV
        // classes) when PV is not yet loaded.
        ProjectManager.getInstance();

        // install the mode file if it's not installed yet
        int ver = jEdit.getIntegerProperty("p4plugin.mode_file_version", 0);
        if (MODE_FILE_VERSION > ver) {
            String dest = jEdit.getSettingsDirectory() +
                          File.separator + "modes" + File.separator +
                          "p4plugin.mode.xml";
            try {
                InputStream in = null;
                OutputStream out = null;

                try {
                    in = getClass().getResourceAsStream("/perforce.xml");
                    out = new FileOutputStream(dest);

                    byte[] buf = new byte[1024];
                    int read;

                    while ( (read = in.read(buf, 0, buf.length)) >= 0) {
                        out.write(buf, 0, read);
                    }

                } finally {
                    if (in != null) try { in.close(); } catch (Exception e) { }
                    if (out != null) try { out.close(); } catch (Exception e) { }
                }

            } catch (IOException e) {
                Log.log(Log.WARNING, this, "couldn't install mode file");
                Log.log(Log.WARNING, this, e);
                return;
            } catch (Exception e) {
                Log.log(Log.ERROR, this, e);
                return;
            }

            // the following shamelessly copied from TypoScriptPlugin.
            Mode mode = jEdit.getMode("perforce");
            if (mode == null) {
                mode = new Mode("perforce");
                mode.setProperty("file", dest);
                mode.unsetProperty("filenameGlob");
                mode.setProperty("firstlineGlob", "# A Perforce {Branch,Client,Change,User} Specification.*");
                mode.init();
                jEdit.addMode(mode);
            }

            jEdit.setIntegerProperty("p4plugin.mode_file_version", MODE_FILE_VERSION);
        }
    }

    /**
     *  Monitors when read-only buffers are loaded and set a key
     *  interceptor if the buffer path belongs to the current
     *  project and the project is using perforce.
     */
    public void handleMessage(EBMessage msg) {
        if (P4GlobalConfig.getInstance().getMonitorFiles()
            && (msg instanceof BufferUpdate || msg instanceof ViewUpdate))
        {
            Buffer b;
            View v;
            if (msg instanceof ViewUpdate) {
                v = ((ViewUpdate)msg).getView();
                b = v.getBuffer();
            } else {
                b = ((BufferUpdate)msg).getBuffer();
                v = ((BufferUpdate)msg).getView();
                if (v == null)
                    return;
            }
            if (!b.isReadOnly()) {
                if (v.getKeyEventInterceptor() == keyHandler) {
                    v.setKeyEventInterceptor(null);
                } else {
                    keyHandler.removeInterceptor = true;
                }
                return;
            }
            VPTProject proj = ProjectViewer.getActiveProject(jEdit.getActiveView());
            if (proj != null
                && proj.getChildNode(b.getPath()) != null
                && P4Config.getProjectConfig(v) != null)
            {
                v.setKeyEventInterceptor(keyHandler);
            }
        }
    }

}

