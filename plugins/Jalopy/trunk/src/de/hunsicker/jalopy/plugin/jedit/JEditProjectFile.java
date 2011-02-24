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

import java.io.File;

import de.hunsicker.jalopy.plugin.Editor;
import de.hunsicker.jalopy.plugin.Project;
import de.hunsicker.jalopy.plugin.ProjectFile;


/**
 * The jEdit project file implementation.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @version $Revision: 1.3 $
 */
final class JEditProjectFile
    implements ProjectFile
{
    //~ Instance variables ---------------------------------------------------------------

    Buffer buffer;
    Project project;

    //~ Constructors ---------------------------------------------------------------------

    /**
     * Creates a new JEditProjectFile object.
     *
     * @param project the enclosing project.
     * @param buffer the file buffer.
     */
    public JEditProjectFile(
        Project project,
        Buffer  buffer)
    {
        this.project = project;
        this.buffer = buffer;
    }

    //~ Methods --------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public Editor getEditor()
    {
        if (isOpened())
        {
            View view = jEdit.getActiveView();
            Buffer buffer = view.getBuffer();

            if (JEditPlugin.isActiveBuffer(buffer))
            {
                return new JEditEditor(this, view.getTextArea());
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getEncoding()
    {
        return (String) this.buffer.getProperty(Buffer.ENCODING);
    }


    /**
     * {@inheritDoc}
     */
    public File getFile()
    {
	// this will fail if the buffer is not local file system
        return new File(this.buffer.getPath());
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return getFile().getName();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isOpened()
    {
        Buffer[] buffers = jEdit.getBuffers();

        for (int i = 0; i < buffers.length; i++)
        {
            if (this.buffer == buffers[i])
            {
                return true;
            }
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public Project getProject()
    {
        return this.project;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReadOnly()
    {
        return this.buffer.isReadOnly();
    }
}
