/*
 *  Copyright (C) 2003 Don Brown (mrdon@techie.com)
 *  Copyright (C) 2000, 2001 Greg Merrill (greghmerrill@yahoo.com)
 *  This file is part of Log Viewer, a plugin for jEdit (http://www.jedit.org).
 *  It is heavily  based off Follow (http://follow.sf.net).
 *  Log Viewer is free software; you can redistribute it and/or modify
 *  it under the terms of version 2 of the GNU General Public
 *  License as published by the Free Software Foundation.
 *  Log Viewer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with Log Viewer; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package logviewer;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;

/**
 *  Deletes the content of the currently selected log file
 *
 * @author    <a href="mailto:mrdon@techie.com">Don Brown</a>
 * @author    <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
class Delete extends AbstractAction {

    LogViewer app;

    /**
     *  Constructor for the Delete action
     *
     * @param  app   The main class
     * @param  name  The name of the action
     */
    public Delete(LogViewer app, String name) {
        super(name);
        this.app = app;
    }

    /**
     *  Performs the action
     *
     * @param  e  The action event
     */
    public void actionPerformed(ActionEvent e) {
        if (app.attributes_.confirmDelete()) {
            DisableableConfirm confirm = new DisableableConfirm(
                    app.getView(),
                    app.getProperty("dialog.confirmDelete.title"),
                    app.getProperty("dialog.confirmDelete.message"),
                    app.getProperty("dialog.confirmDelete.confirmButtonText"),
                    app.getProperty("dialog.confirmDelete.doNotConfirmButtonText"),
                    app.getProperty("dialog.confirmDelete.disableText")
                    );
            confirm.pack();
            confirm.show();
            if (confirm.markedDisabled()) {
                app.attributes_.setConfirmDelete(false);
            }
            if (confirm.markedConfirmed()) {
                performDelete();
            }
        }
        else {
            performDelete();
        }
    }

    /**  Performs the delete */
    private void performDelete() {
        app.setCursor(Cursor.WAIT_CURSOR);
        FileFollowingPane fileFollowingPane = app.getSelectedFileFollowingPane();
        if (fileFollowingPane == null) {
            // TODO: should provide some error message
            return;
        }
        try {
            fileFollowingPane.clear();
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.err);
            app.setCursor(Cursor.DEFAULT_CURSOR);
            JOptionPane.showMessageDialog(
                    app,
                    app.getProperty("message.unableToDelete.text"),
                    app.getProperty("message.unableToDelete.title"),
                    JOptionPane.WARNING_MESSAGE
                    );
        }
        app.setCursor(Cursor.DEFAULT_CURSOR);
    }

}

