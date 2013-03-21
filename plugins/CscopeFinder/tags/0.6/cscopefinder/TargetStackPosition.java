/*
 * StackPosition.java - part of the Tags plugin.
 *
 * Copyright (C) 2003 Ollie Rutherfurd (oliver@rutherfurd.net)
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
 * $Id: StackPosition.java,v 1.3 2004/11/07 15:52:34 orutherfurd Exp $
 */
/*
 * This file originates from the Tags Plugin version 2.0.1
 * whose copyright and licensing is seen above.
 * The original file was modified to become the derived work you see here
 * in accordance with Section 2 of the Terms and Conditions of the GPL v2.
 *
 * The derived work is called the CscopeFinder Plugin and is
 * Copyright 2006 Dean Hall.
 *
 * 2006/08/09
 */

package cscopefinder;

//{{{ imports
import javax.swing.text.Position;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;
//}}}

public class TargetStackPosition
{
	//{{{ TargetStackPosition constructor
	public TargetStackPosition(View view)
	{
		Buffer buffer = view.getBuffer();
		position = buffer.createPosition(view.getTextArea().getCaretPosition());
		filename = buffer.getName();
		directory = buffer.getDirectory();
		lineNumber = buffer.getLineOfOffset(position.getOffset()) + 1;
		path = buffer.getPath();
		// XXX clean-up
		lineBefore = (lineNumber == 1 ? "" : buffer.getLineText(lineNumber-2).replace('\t', ' '));
		line = buffer.getLineText(lineNumber-1).replace('\t', ' ');
		lineAfter = (lineNumber == buffer.getLineCount() ? "" : buffer.getLineText(lineNumber).replace('\t', ' '));
	} //}}}

	//{{{ goTo() method
	public void goTo(final View view)
	{
        Log.log(Log.DEBUG, this,
            "TargetStackPosition.goTo(" + this + ")");	// ##
        Log.log(Log.DEBUG, this,
            "lineNumber: " + lineNumber);	// ##

        Buffer buffer = null;
        int caret = 0;

        Log.log(Log.DEBUG, this, "this.position=" + this.position); // ##

        // either open or switch to buffer, if it's open
        if(this.position == null)
        {
            buffer = jEdit.openFile(view,this.path);
            if(!buffer.isLoaded())
                VFSManager.waitForRequests();   // wait for buffer to be loaded
            Log.log(Log.DEBUG, this, "opened: " + buffer);
            caret = buffer.getLineStartOffset(this.lineNumber-1);
        }
        else
        {
            buffer = jEdit.openFile(view,this.path);
            caret = position.getOffset();
            if(caret >= buffer.getLength())
                caret = buffer.getLength()-2;
        }

        view.getTextArea().setCaretPosition(caret);

	} //}}}

    // XXX maybe store offset and line number?
	//{{{ releasePosition() method
	public void releasePosition()
	{
        Log.log(Log.DEBUG, this, "Releasing position for " + this);   // ##
		this.position = null;
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		// XXX clean up
		return "" + (lineNumber + 1) + ": " + getName()
            + "(" + getDirectory() + ")";
	} //}}}

	//{{{ getName() method
	public String getName()
	{
		return filename;
	} //}}}

	//{{{ getDirectory() method
	public String getDirectory()
	{
		return directory;
	} //}}}

	//{{{ getLineNumber() method
	public int getLineNumber()
	{
		return lineNumber;
	} //}}}

	//{{{ getLineBefore() method
	public String getLineBefore()
	{
		return lineBefore;
	} //}}}

	//{{{ getLine() method
	public String getLine()
	{
		return line;
	} //}}}

	//{{{ getLineAfter() method
	public String getLineAfter()
	{
		return lineAfter;
	} //}}}

	//{{{ getPath() method
	public String getPath()
	{
		return path;
	} //}}}

	//{{{ declarations
	String path;
	String directory;
	String lineBefore;
	String line;
	String lineAfter;
	String filename;
	int lineNumber;
	Position position;
	//}}}

}

// :collapseFolds=1:noTabs=true:tabSize=4:indentSize=4:deepIndent=false:folding=explicit:
