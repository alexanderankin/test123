/*
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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

package javainsight;


import java.util.Vector;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.gui.DockableWindow;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.CreateDockableWindow;
import org.gjt.sp.util.Log;
import javainsight.buildtools.msg.DecompileClassMessage;


/**
 * Main JavaInsight plugin class.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class JavaInsightPlugin extends EBPlugin {

    /**
     * The 'name' of the dockable window.
     */
    public static final String DOCKABLE_NAME = "javainsight-dockable";


    /**
     * Start the plugin.
     */
    public void start() {
        VFSManager.registerVFS("class",  new ClassVFS());
        VFSManager.registerVFS("jasmin", new JasminVFS());
        VFSManager.registerVFS("jode",   new JodeVFS());

        EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST, DOCKABLE_NAME);

        // parse out the resources as a thread so that when the plugin is
        // requested there is nothing to do.
        new ThreadedParser().start();
    }


    /**
     * Create the menus.
     */
    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenuItem("javainsight.toggle-dockable"));
    }


    /**
     * Create the option pane.
     *
     * @author Dirk Moebius
     */
    public void createOptionPanes(OptionsDialog optionsDialog) {
        optionsDialog.addOptionPane(new JavaInsightOptionPane());
    }


    /**
     * Handle message for decompile requests.
     */
    public void handleMessage(EBMessage message) {
        if (message instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate) message;

            if (bu.getWhat() == BufferUpdate.CREATED) {
                Buffer buffer = bu.getBuffer();
                VFS    vfs    = buffer.getVFS();
                Mode   mode   = null;
                if (   (vfs instanceof ClassVFS)
                    || (vfs instanceof JasminVFS)
                ) {
                    mode = jEdit.getMode("bcel");
                } else if (vfs instanceof JodeVFS) {
                    mode = jEdit.getMode("java");
                } else {}

                if (mode != null) {
                    buffer.setMode(mode);
                }
            }
        } else if (message instanceof CreateDockableWindow) {
            CreateDockableWindow cmsg = (CreateDockableWindow) message;
            if (cmsg.getDockableWindowName().equals(DOCKABLE_NAME)) {
                JavaInsightDockable jid = new JavaInsightDockable(cmsg.getView());
                javaInsight = (JavaInsight) jid.getComponent();
                cmsg.setDockableWindow(jid);
            }
        } else if (message instanceof DecompileClassMessage) {
            DecompileClassMessage dmsg = (DecompileClassMessage) message;
            String classname = dmsg.getClassName();
            String destination = dmsg.getDestination();
            boolean generateFile = dmsg.isGeneratingFile();

            Log.log(Log.MESSAGE, this,
                "Decompiling class "
                + classname
                + (generateFile ?
                    (destination == null ? " to temp directory" : " to " + destination)
                    : " to new jEdit buffer"));

            // Notify the sender that we received the message, and don't need
            // to propagate it any further on the EditBus:
            dmsg.veto();

            // create JavaInsight instance if it doesn't exist
            if (javaInsight == null) {
                View view = jEdit.getFirstView();
                JavaInsightDockable jid = new JavaInsightDockable(view);
                javaInsight = (JavaInsight) jid.getComponent();
            }

            try {
                if (generateFile) {
                    String filename = javaInsight.decompileToFile(classname, destination);
                    dmsg._setGeneratedFile(filename);
                } else {
                    javaInsight.decompileToBuffer(classname);
                }
            }
            catch (Throwable t) {
                Log.log(Log.ERROR, this, "Error decompiling class " + classname + ": " + t.getMessage());
                dmsg._setException(t);
            }
        }
    }


    private JavaInsight javaInsight = null;

}

