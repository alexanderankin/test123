/*
 * open_file.java - display a file selection box and open a file in InfoViewer
 * Copyright (C) 1999 Dirk Moebius
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

package infoviewer.actions;

import infoviewer.*;
import java.awt.event.*;
import javax.swing.JFileChooser;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;


public class open_file extends InfoViewerAction {

    public open_file() {
        super("infoviewer.open_file");
    }

    public void actionPerformed(ActionEvent evt) {
        if (lastfile == null) {
            lastfile = jEdit.getJEditHome();
        }

        String files[] = GUIUtilities.showVFSFileDialog(jEdit.getFirstView(),
            lastfile, JFileChooser.OPEN_DIALOG, false);

        if (files == null)
            return;

        lastfile = files[0];
        getViewer(evt).toFront();
        getViewer(evt).gotoURL("file:" + files[0]);
    }

    private static String lastfile = null;
}

