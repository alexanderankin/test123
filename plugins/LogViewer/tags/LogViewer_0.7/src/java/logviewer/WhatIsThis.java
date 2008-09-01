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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *  Help button
 *
 * @author    <a href="mailto:mrdon@techie.com">Don Brown</a>
 * @author    <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
class WhatIsThis extends JButton {

    static Icon whatIsThisIcon;

    /**
     *  Constructor
     *
     * @param  title  The title of the help window
     * @param  text   The text of the help window
     */
    WhatIsThis(
            final String title,
            final String text) {
        super(getWhatIsThisIcon());
        setBorderPainted(false);
        setToolTipText(title);
        setMargin(new Insets(0, 0, 0, 0));
        addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(
                            null,
                            text,
                            title,
                            JOptionPane.INFORMATION_MESSAGE
                            );
                }
            });
    }

    /**
     *  Gets the icon
     *
     * @return    The icon
     */
    static Icon getWhatIsThisIcon() {
        if (whatIsThisIcon == null) {
            whatIsThisIcon = new ImageIcon(LogViewer.class.getResource(
                    LogViewer.getProperty("WhatIsThis.icon")
                    ));
        }
        return whatIsThisIcon;
    }

}

