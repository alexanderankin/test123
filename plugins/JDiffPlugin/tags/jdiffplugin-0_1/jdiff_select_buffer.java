/*
 * jdiff_select_buffer.java
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.gjt.sp.util.Log;

public class jdiff_select_buffer
    extends jdiff.gui.JDiffAction
{
    public jdiff_select_buffer(JMenu buffers) {
        super("jdiff-select-buffer");

        this.args = new PathCollector();
        this.buffers = buffers;
    }

    public void actionPerformed(ActionEvent evt)
    {
        Log.log(Log.DEBUG, this, "Source: " + evt.getSource().getClass());

        if (evt.getSource() instanceof JCheckBoxMenuItem) {
            JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)evt.getSource();
            Log.log(Log.DEBUG, this, "Selected: " + menuItem.isSelected());

            if (!menuItem.isSelected()) {
                return;
            }
        }

        Log.log(Log.DEBUG, this, "Action: " + evt.getActionCommand());

        if (evt.getActionCommand() != null) {
            this.args.addPath(evt.getActionCommand());
            if (this.args.isFull()) {

                String[] paths = this.args.getPaths();
                jdiff.text.DiffDocument doc = 
                    jdiff.gui.GUIUtilities.loadDiffDocument(paths[0], paths[1]);

                if (doc != null) {
                    jdiff.gui.JDiffPanel myPanel =
                        jdiff.gui.GUIUtilities.getJDiffPanel(evt);
                    myPanel.addDiffPane(new jdiff.gui.DualScrollPane(doc));
                }

                this.reset();
            }
        }
    }

    public JMenu getBuffersMenu() {
        return this.buffers;
    }

    public void setBuffersMenu(JMenu buffers) {
        this.buffers = buffers;
    }

    private void reset() {
        for(int i = this.buffers.getMenuComponentCount() - 1; i >= 0; i--) {
            Component c = this.buffers.getMenuComponent(i);
            if (c instanceof JCheckBoxMenuItem) {
                ((JCheckBoxMenuItem)c).setSelected(false);
            }
        }

        this.args.reset();
    }

    private PathCollector args;
    private JMenu buffers;

    public static class PathCollector {
        public PathCollector() {}

        private String[] paths = {null, null};

        public void reset() {
            this.paths[0] = this.paths[1] = null;
        }
        
        public void addPath(String path) {
            if (this.paths[0] == null) {
                this.paths[0] = path;
                return;
            }

            this.paths[1] = path;
        }

        public String[] getPaths() {
            return this.paths;
        }

        public boolean isFull() {
            return ((this.paths[0] != null) && (this.paths[1] != null));
        }
    }
}
