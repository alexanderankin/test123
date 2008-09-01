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
import java.io.File;
import javax.swing.AbstractAction;


/**
 *  Reloads the currently selected file.
 *
 * @author    <a href="mailto:geniosity@hotmail.com">James Mc</a>
 * @author    <a href="mailto:mrdon@techie.com">Don Brown</a>
 * @author    <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
class Reload extends AbstractAction {

    LogViewer app;
	
    /**
     *  Constructor
     *
     * @param  app   The main class
     * @param  name  The name
     */
    public Reload(LogViewer app, String name) {
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
        boolean wordwrap = fileFollowingPane.getWordWrap();
        File followedFile = fileFollowingPane.getFollowedFile();
		if (followedFile != null && followedFile.exists()) {
			app.close();
			app.open(followedFile, true);
            fileFollowingPane = app.getSelectedFileFollowingPane();
            fileFollowingPane.setWordWrap(wordwrap);
		}
    
	}

}

