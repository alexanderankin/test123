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
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;


public class GlobalReference {
	GlobalRecord rec;
	public GlobalReference(GlobalRecord rec) {
		this.rec = rec;
	}
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		String file = rec.getFile();
		int line = rec.getLine();
		if (file != null)
			s.append("[" + file + ":" + line + "] ");
		s.append(rec.getText());
		return s.toString();
	}
	public void jump(View view)
	{
		String file = rec.getFile();
		if (file == null)
			return;
		int line = rec.getLine();
		Buffer buffer = jEdit.openFile(view, file);
		if(buffer == null) {
			view.getStatus().setMessage("Unable to open: " + file);
			return;
		}
		JEditTextArea ta = view.getTextArea();
		ta.setCaretPosition(ta.getLineStartOffset(line - 1));
	}
}
