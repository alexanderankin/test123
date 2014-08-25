/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011, 2012 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package gatchan.phpparser.hyperlink;

//{{{ Imports

import gatchan.jedit.hyperlinks.Hyperlink;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
//}}}

/**
 * @author Matthieu Casanova
 */
public class PHPHyperlink implements Hyperlink
{
	private final String name;
	private String tooltip;
	private final String path;
	private final int itemLine;
	private final int startOffset;
	private final int endOffset;
	private final int line;

	//{{{ PHPHyperlink constructors
	public PHPHyperlink(String name, String tooltip, String path, int itemLine, int startOffset, int endOffset,
			    int line)
	{
		this.name = name;
		this.tooltip = tooltip;
		this.path = path;
		this.itemLine = itemLine;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.line = line;
	}

	public PHPHyperlink(String name, String path, int itemLine, int startOffset, int endOffset, int line)
	{
		this(name, "<html><b>"+name + "</b> ("+path+")</html>", path, itemLine, startOffset, endOffset, line);
	} //}}}

	//{{{ getStartOffset() method
	@Override
	public int getStartOffset()
	{
		return startOffset;
	} //}}}

	//{{{ getEndOffset() method
	@Override
	public int getEndOffset()
	{
		return endOffset;
	} //}}}

	//{{{ getStartLine() method
	@Override
	public int getStartLine()
	{
		return line;
	} //}}}

	//{{{ getEndLine() method
	@Override
	public int getEndLine()
	{
		return line;
	} //}}}

	//{{{ getTooltip() method
	@Override
	public String getTooltip()
	{
		return tooltip;
	} //}}}

	//{{{ click() method
	@Override
	public void click(View view)
	{
		final Buffer buffer = jEdit.openFile(view, path);
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				JEditTextArea textArea = jEdit.getActiveView().getTextArea();

				int caretPosition = buffer.getLineStartOffset(itemLine - 1) + itemLine - 1;
				textArea.moveCaretPosition(caretPosition);
				Log.log(Log.MESSAGE, this, "Moving to line " + itemLine + ' ' + caretPosition);
				/*
						Selection[] s = getSelection();
						if (s == null)
						  return;

						JEditTextArea textArea = editPane.getTextArea();
						if (textArea.isMultipleSelectionEnabled())
						  textArea.addToSelection(s);
						else
						  textArea.setSelection(s);

						textArea.moveCaretPosition(occur.endPos.getOffset());*/
			}
		});
	} //}}}
}
