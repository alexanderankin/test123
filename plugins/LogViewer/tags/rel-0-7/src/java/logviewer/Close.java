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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *  Closes the currently selected file
 *
 * @author    <a href="mailto:mrdon@techie.com">Don Brown</a>
 * @author    <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
class Close extends AbstractAction {

    LogViewer app;

    /**
     *  Constructor
     *
     * @param  app   The main class
     * @param  name  The name
     */
    public Close(LogViewer app, String name) {
        super(name);
        this.app = app;
    }

    /**
     *  Performs the action
     *
     * @param  e  The action event
     */
    public void actionPerformed(ActionEvent e) {
        FileFollowingPane fileFollowingPane = app.getSelectedFileFollowingPane();
        if (fileFollowingPane == null) {
            // TODO: should provide some error message
            return;
        }
        app.tabbedPane_.removeTabAt(app.tabbedPane_.getSelectedIndex());
        app.attributes_.removeFollowedFile(fileFollowingPane.getFollowedFile());
        fileFollowingPane.stopFollowing();
        app.fileToFollowingPaneMap_.remove(fileFollowingPane.getFollowedFile());
        if (app.fileToFollowingPaneMap_.size() == 0) {
            app.close_.setEnabled(false);
            app.top_.setEnabled(false);
            app.bottom_.setEnabled(false);
            app.clear_.setEnabled(false);
            app.clearAll_.setEnabled(false);
            app.delete_.setEnabled(false);
            app.deleteAll_.setEnabled(false);
        }
    }

}

