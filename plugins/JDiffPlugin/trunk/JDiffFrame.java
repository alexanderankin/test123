/*
 * JDiffFrame.java
 * Copyright (c) 2000 Andre Kaplan
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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

public class JDiffFrame
    extends JFrame
    implements EBComponent
{
    public JDiffFrame(View view) {
        super("JDiff");

        this.view = view;

        EditBus.addToBus(this);

        // There's currently no way to make JDiff load properties from a file
        jdiff.JDiff.setProperty("mbars.main", "file buf help");
        jdiff.JDiff.setProperty("menus.buf.label", "Buffers");
        jdiff.JDiff.setProperty("menus.file", "jdiff-compare-dirs jdiff-exit");
        jdiff.JDiff.setProperty("menus.help", "jdiff-about");
        jdiff.JDiff.setProperty("actions.jdiff-about.label", "About");
        jdiff.JDiff.setProperty("actions.jdiff-compare-dirs.label", "Compare Files/Dirs");
        jdiff.JDiff.setProperty("actions.jdiff-exit.label", "Exit");

        jdiff.JDiff.addAction(new jdiff_about());
        jdiff.JDiff.addAction(new jdiff_compare_dirs());
        jdiff.JDiff.addAction(new jdiff_exit(this));

        jdiff.gui.JDiffPanel jdiffPanel = new jdiff.gui.JDiffPanel();

        JMenuBar mBar = jdiff.gui.GUIUtilities.loadMenuBar("main", jdiffPanel);

        for (int i = 0; i < mBar.getMenuCount(); i++) {
            if (mBar.getMenu(i).getText().equals(
                    jdiff.JDiff.getProperty("menus.buf.label"))
            ) {
                this.buffers = mBar.getMenu(i);
                break;
            }
        }

        this.selectBuffer = new jdiff_select_buffer(this.buffers);
        this.updateBuffersMenu(this.buffers);

        this.setJMenuBar(mBar);
        this.setContentPane(jdiffPanel);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                JDiffFrame.this.dispose();
            }
        });
        this.pack();
        GUIUtilities.loadGeometry(this, "jdiff-main-window");
        this.show();
    }

    public void dispose() 
    {
        EditBus.removeFromBus(this);
        GUIUtilities.saveGeometry(this, "jdiff-main-window");
        super.dispose();
    }

    public void handleMessage(EBMessage message) {
        if (message instanceof ViewUpdate) {
            ViewUpdate vu = (ViewUpdate)message;
            if ((vu.getWhat() == ViewUpdate.CLOSED) &&
                (this.view == vu.getView())
            ) {
                this.dispose();
            }
        } else if (message instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate)message;
            if ((bu.getWhat() == BufferUpdate.CREATED) || 
                (bu.getWhat() == BufferUpdate.CLOSED)
            ) {
                this.updateBuffersMenu(this.buffers);
            }
        }
    }

    /**
    Borrowed from View.java
    */
    private void updateBuffersMenu(JMenu menu)
    {
        if (jEdit.getBufferCount() == 0) {
            return;
        }

        for (int i = menu.getMenuComponentCount() - 1; i >= 0; i--) {
            menu.remove(i);
        }

        Buffer[] bufferArray = jEdit.getBuffers();
        for(int i = 0; i < bufferArray.length; i++) {
            Buffer b = bufferArray[i];
            String name = b.getPath();
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name);
            menuItem.addActionListener(this.selectBuffer);
            menuItem.setActionCommand(name);
            menu.add(menuItem);
        }
    }

    private JMenu buffers;
    private jdiff.gui.JDiffAction selectBuffer;
    private View view;
}
