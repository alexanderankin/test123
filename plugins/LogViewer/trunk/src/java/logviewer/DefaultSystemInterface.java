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
import java.io.File;
import javax.swing.JFileChooser;

/**
 *  Default implementation of {@link SystemInterface} for the Log Viewer plugin.
 *
 * @author    <a href="mailto:mrdon@techie.com">Don Brown</a>
 * @author    <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
class DefaultSystemInterface implements SystemInterface {

    LogViewer app;

    /**
     *  Constructor for the DefaultSystemInterface object
     *
     * @param  app  The main class
     */
    public DefaultSystemInterface(LogViewer app) {
        this.app = app;
    }

    /**
     *  Gets the desired file from the user
     *
     * @return    The user selected file
     */
    public File getFileFromUser() {
        app.setCursor(Cursor.WAIT_CURSOR);
        JFileChooser chooser = new JFileChooser(
                app.attributes_.getLastFileChooserDirectory()
                );
        app.setCursor(Cursor.DEFAULT_CURSOR);
        int returnVal = chooser.showOpenDialog(app);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
}

