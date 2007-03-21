/*
 * Copyright (c) 2001-2002, Marco Hunsicker. All rights reserved.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 */
package de.hunsicker.jalopy.plugin.jedit;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import java.util.Collection;
import java.util.Collections;

import de.hunsicker.jalopy.plugin.Project;
import de.hunsicker.jalopy.plugin.ProjectFile;


/**
 * The jEdit project implementation.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @version $Revision: 1.3 $
 */
final class JEditProject
    implements Project
{
    //~ Constructors ---------------------------------------------------------------------

    /**
     * Creates a new JEditProject object.
     */
    public JEditProject()
    {
    }

    //~ Methods --------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public ProjectFile getActiveFile()
    {
        View view = jEdit.getActiveView();

        if (view != null)
        {
            Buffer buffer = view.getBuffer();

            return new JEditProjectFile(this, buffer);
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Collection getAllFiles()
    {
        return Collections.EMPTY_LIST;
    }


    /**
     * {@inheritDoc}
     */
    public Collection getOpenedFiles()
    {
        /*Buffer[] buffers = jEdit.getBuffers();
        List result = new ArrayList(buffers.length);

        for (int i = 0; i < buffers.length; i++)
        {
            result.add(new JEditProjectFile(this, buffers[i]));
        }

        return result;*/
        return Collections.EMPTY_LIST;
    }


    /**
     * {@inheritDoc}
     */
    public Collection getSelectedFiles()
    {
        return Collections.EMPTY_LIST;
    }
}
