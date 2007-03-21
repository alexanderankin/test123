/*
 * Copyright (c) 2001-2002, Marco Hunsicker. All rights reserved.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 */
package de.hunsicker.jalopy.plugin.jedit;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import java.util.Collections;
import java.util.List;

import de.hunsicker.jalopy.plugin.Editor;
import de.hunsicker.jalopy.plugin.ProjectFile;


/**
 * The jEdit editor implementation.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @version $Revision: 1.5 $
 */
final class JEditEditor
    implements Editor
{
    //~ Instance variables ---------------------------------------------------------------

    /** The 'physical' editor. */
    JEditTextArea textArea;
    ProjectFile file;

    //~ Constructors ---------------------------------------------------------------------

    /**
     * Creates a new JEditEditor object.
     *
     * @param file the underlying Java source file.
     * @param textArea the underlying editor.
     */
    public JEditEditor(
        ProjectFile   file,
        JEditTextArea textArea)
    {
        this.file = file;
        this.textArea = textArea;
    }

    //~ Methods --------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void setCaretPosition(int offset)
    {
        if ((offset < 0) || (offset > getLength()))
        {
            throw new IllegalArgumentException("invalid range -- " + offset);
        }

        this.textArea.setCaretPosition(offset);
    }


    /**
     * {@inheritDoc}
     */
    public void setCaretPosition(
        int line,
        int column)
    {
        if ((line < 1) || (column < 1))
        {
            throw new IllegalArgumentException(
                "invalid range -- line " + line + ", column " + column);
        }

        setCaretPosition((this.textArea.getLineStartOffset(line - 1) + column) - 1);
    }


    /**
     * {@inheritDoc}
     */
    public int getCaretPosition()
    {
        return this.textArea.getCaretPosition();
    }


    /**
     * {@inheritDoc}
     */
    public int getColumn()
    {
        return 1;
    }


    /**
     * {@inheritDoc}
     */
    public ProjectFile getFile()
    {
        return this.file;
    }


    /**
     * {@inheritDoc}
     */
    public int getLength()
    {
        return this.textArea.getBuffer().getLength();
    }


    /**
     * {@inheritDoc}
     */
    public int getLine()
    {
        return this.textArea.getCaretLine() + 1;
    }


    /**
     * {@inheritDoc}
     */
    public String getSelectedText()
    {
        return this.textArea.getSelectedText();
    }


    /**
     * {@inheritDoc}
     */
    public void setSelection(
        int startOffset,
        int endOffset)
    {
        if ((startOffset < 0) || (endOffset < 0))
        {
            throw new IllegalArgumentException(
                "invalid range -- startOffset " + startOffset + ", endOffset "
                + endOffset);
        }

        Selection selection = new Selection.Range(startOffset, endOffset);
        this.textArea.setSelection(selection);
    }


    /**
     * {@inheritDoc}
     */
    public int getSelectionEnd()
    {
        Selection[] selection = this.textArea.getSelection();

        if (selection.length > 0)
        {
            return selection[selection.length - 1].getStart();
        }

        return getCaretPosition();
    }


    /**
     * {@inheritDoc}
     */
    public int getSelectionStart()
    {
        Selection[] selection = this.textArea.getSelection();

        if (selection.length > 0)
        {
            return selection[0].getStart();
        }

        return getCaretPosition();
    }


    /**
     * {@inheritDoc}
     */
    public void setText(String text)
    {
        this.textArea.setText(text);
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return this.textArea.getText();
    }


    /**
     * {@inheritDoc}
     */
    public void attachAnnotations(List annotations)
    {
        /*
        Buffer buffer = this.textArea.getBuffer();

        for (int i = 0, size = annotations.size(); i < size; i++)
        {
            Annotation annotation = (Annotation) annotations.get(i);
            Marker marker = (Marker) annotation.getData();
            buffer.addMarker(
                marker.getShortcut(), buffer.getLineStartOffset(annotation.getLine() - 1));
        }

        annotations.clear();
        */
    }


    /**
     * {@inheritDoc}
     */
    public List detachAnnotations()
    {
        /*
        Buffer buffer = this.textArea.getBuffer();
        Vector markers = buffer.getMarkers();
        List result = new ArrayList(markers.size());

        for (int i = 0, size = markers.size(); i < size; i++)
        {
            Marker marker = (Marker) markers.elementAt(i);
            result.add(
                new Annotation(
                    this.textArea.getLineOfOffset(marker.getPosition()) + 1, marker));
        }

        buffer.removeAllMarkers();

        return result;
        */
        return Collections.EMPTY_LIST;
    }


    /**
     * {@inheritDoc}
     */
    public void paste(String text)
    {
        this.textArea.setSelectedText(text);
    }


    /**
     * {@inheritDoc}
     */
    public void requestFocus()
    {
        this.textArea.requestFocus();
    }


    /**
     * {@inheritDoc}
     */
    public void selectAll()
    {
        this.textArea.selectAll();
    }
}
