/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package browser;

//{{{ imports
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class GlobalPlugin extends EditPlugin
{
	public static final String OPTION_PREFIX = "options.GlobalPlugin.";

	//{{{ EBPlugin methods

	public static final String CALL_TREE_BROWSER = "call-tree-browser";
	public static final String REFERENCE_BROWSER = "reference-browser";
	public static final String DEFINITION_BROWSER = "definition-browser";

	//{{{ start() method
	public void start()
	{
	} //}}}

	//{{{ stop()
	public void stop()
	{
	} //}}}

	static public void showCallTree(View view)
	{
		DockableWindowManager dwm = view.getDockableWindowManager(); 
		dwm.showDockableWindow(CALL_TREE_BROWSER);
		((CallTree)dwm.getDockableWindow(CALL_TREE_BROWSER)).show(view);
		
	}
	static public void showReferences(View view)
	{
		DockableWindowManager dwm = view.getDockableWindowManager(); 
		dwm.showDockableWindow(REFERENCE_BROWSER);
		((ReferenceList)dwm.getDockableWindow(REFERENCE_BROWSER)).show(view);
	}
	static public void showDefinitions(View view)
	{
		DockableWindowManager dwm = view.getDockableWindowManager(); 
		dwm.showDockableWindow(DEFINITION_BROWSER);
		((DefinitionList)dwm.getDockableWindow(DEFINITION_BROWSER)).show(view);
	}
	static public void jump(final View view, final String file, final int line)
	{
		if (file == null)
			return;
		final Buffer buffer = jEdit.openFile(view, file);
		if(buffer == null) {
			view.getStatus().setMessage("Unable to open: " + file);
			return;
		}
		final Runnable moveCaret = new Runnable() {
			public void run() {
				JEditTextArea ta = view.getTextArea();
				ta.setCaretPosition(ta.getLineStartOffset(line - 1));
			}
		};
		if (buffer.isLoaded())
		{
			moveCaret.run();
		}
		else
		{
			buffer.addBufferListener(new BufferAdapter() {
				@Override
				public void bufferLoaded(JEditBuffer buffer) {
					moveCaret.run();
				}
			});
		}
	}
}

// :collapseFolds=1:noTabs=false:lineSeparator=\r\n:tabSize=4:indentSize=4:deepIndent=false:folding=explicit:
