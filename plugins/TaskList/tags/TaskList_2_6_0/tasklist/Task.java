/*
* Task.java - TaskList plugin
* Copyright (C) 2001 Oliver Rutherfurd
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
*
* $Id$
*/

package tasklist;

//{{{ imports
import javax.swing.Icon;
import javax.swing.text.Position;
import org.gjt.sp.jedit.Buffer;
//}}}

/**
 * A data object containing the attributes of a formatted comment
 * contained in a source file, along with an Icon representing the
 * type of task.
 * @author Oliver Rutherfurd
 */
public class Task {

    //{{{ constructor
    public Task(Buffer buffer, Icon icon, int line, String identifier, String comment, String text, int startOffset, int endOffset) {
        this.buffer = buffer.getPath();
        this.icon = icon;
        this.lineIndex = line;
        this.identifier = identifier;
        this.comment = comment.replace('\t', (char) 187);
        this.text = text.replace('\t', (char) 187);
        int posOffset = buffer.getLineStartOffset(line);
        this.startPosition = buffer.createPosition(posOffset + startOffset);
        startOffset = startPosition.getOffset() - buffer.getLineStartOffset(getLineNumber());
        this.endPosition = buffer.createPosition(posOffset + endOffset);
        endOffset = endPosition.getOffset() - buffer.getLineStartOffset(getLineNumber());
    }    //}}}

    public String getBufferPath() {
        return buffer;
    }
    public Icon getIcon() {
        //return this.icon;
        return TaskListPlugin.getIconForType(getIdentifier());
    }
    public String getIdentifier() {
        return this.identifier;
    }
    public String getComment() {
        return this.comment;
    }
    public String getText() {
        return this.text;
    }
    public int getLineIndex() {
        return this.lineIndex;
    }

    //{{{ getStartOffset() method
    public int getStartOffset() {
        return startOffset;
    }    //}}}

    //{{{ getEndOffset() method
    public int getEndOffset() {
        return endOffset;
    }    //}}}

    //{{{ getStartPosition() method
    public Position getStartPosition() {
        return startPosition;
    }    //}}}

    //{{{ getEndPosition() method
    public Position getEndPosition() {
        return endPosition;
    }    //}}}

    //{{{ getLineNumber() method
    /**
     * Returns the line number of the task.
     * @return The line number of the task as found in the associated buffer
     */
    public int getLineNumber() {
        return lineIndex;
    }    //}}}

    //{{{ toString() method
    /**
     * Provides String representation of the object.
     * @return A String containing the line number and text of the
     * formatted comment.
     */
    public String toString() {
        return "[" + this.getLineNumber() + "]" + this.text;
    }    //}}}

    public boolean equals(Object o) {
        if (! (o instanceof Task)) {
            return false;
        }
        Task other = (Task) o;
        return buffer.equals(other.getBufferPath()) && comment.equals(other.getComment()) && text.equals(other.getText()) && lineIndex == other.getLineNumber() && startOffset == other.getStartOffset() && endOffset == other.getEndOffset();
    }

    //{{{ private members
    private String buffer;    // path for the buffer that this task came from
    private Icon icon;    // icon associated with TaskType

    private String identifier;    // XXX, NOTE, etc...
    private String comment;    // comment text
    private String text;    // identifer, comment, and anything in between

    private int lineIndex;    // line task is on

    private Position startPosition;
    private int startOffset;
    private Position endPosition;
    private int endOffset;
    //}}}
}