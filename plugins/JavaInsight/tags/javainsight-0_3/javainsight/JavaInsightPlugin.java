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
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.gui.DockableWindow;
import org.gjt.sp.jedit.msg.CreateDockableWindow;
import org.gjt.sp.util.Log;
import buildtools.msg.DecompileClassMessage;


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
        if (message instanceof CreateDockableWindow) {
            CreateDockableWindow cmsg = (CreateDockableWindow) message;
            if (cmsg.getDockableWindowName().equals(DOCKABLE_NAME)) {
                JavaInsightDockable jid = new JavaInsightDockable(cmsg.getView());
                javaInsight = (JavaInsight) jid.getComponent();
                cmsg.setDockableWindow(jid);
            }
        }
        else if (message instanceof DecompileClassMessage) {
            DecompileClassMessage decompileMsg = (DecompileClassMessage) message;
            String classname = decompileMsg.getClassName();
            String filename = decompileMsg.getFileName();
            Log.log(Log.MESSAGE, this,
                "Decompiling class "
                + classname
                + (filename != null ? " to " + filename : ""));

            // create JavaInsight instance (and DockableWindow) if it not exists
            if (javaInsight == null) {
                View view = jEdit.getFirstView();
                JavaInsightDockable jid = new JavaInsightDockable(view);
                javaInsight = (JavaInsight) jid.getComponent();
                view.getDockableWindowManager().addDockableWindow(jid);
            }

            try {
                String newFilename = javaInsight.decompileClass(classname, true);
                decompileMsg.setFileName(newFilename);
            }
            catch (Throwable t) {
                Log.log(Log.ERROR, this, "Error decompiling class " + classname + ": " + t.getMessage());
            }
        }
    }


    private JavaInsight javaInsight = null;

}

