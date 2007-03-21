/*
 * Copyright (c) 2001-2002, Marco Hunsicker. All rights reserved.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 */
package de.hunsicker.jalopy.plugin.jedit;

import org.gjt.sp.jedit.gui.StatusBar;


/**
 * The jEdit status bar implementation.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @version $Revision: 1.3 $
 */
final class JEditStatusBar
    implements de.hunsicker.jalopy.plugin.StatusBar
{
    //~ Instance variables ---------------------------------------------------------------

    /** The underlying JEdit status bar. */
    StatusBar statusBar;

    //~ Constructors ---------------------------------------------------------------------

    /**
     * Creates a new JEditStatusBar object.
     *
     * @param statusBar JEdit's current status bar.
     */
    public JEditStatusBar(StatusBar statusBar)
    {
        this.statusBar = statusBar;
    }

    //~ Methods --------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void setText(String text)
    {
        this.statusBar.setMessage(text);
    }
}
