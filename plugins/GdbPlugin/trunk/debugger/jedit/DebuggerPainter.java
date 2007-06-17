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

package debugger.jedit;

import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.text.Position;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

public abstract class DebuggerPainter extends TextAreaExtension {
	private EditPane editPane;
	private Buffer buffer;
	private Position pos;
	private int line; // Just in case 'pos' cannot be created
	
	public DebuggerPainter(EditPane e, Buffer b, int l) {
		editPane = e;
		buffer = b;
		pos = buffer.createPosition(buffer.getLineStartOffset(l));
		if (buffer == null || pos == null)
			line = l;
	}
	public EditPane getEditPane() {
		return editPane;
	}
	public Buffer getBuffer() {
		return buffer;
	}
	public int getLine() {
		if (buffer != null && pos != null)
			return buffer.getLineOfOffset(pos.getOffset());
		return line;
	}
	@Override
	public void paintValidLine(Graphics2D gfx, int screenLine,
			int physicalLine, int start, int end, int y) {
		super.paintValidLine(gfx, screenLine, physicalLine, start, end, y);
		int line = getLine();
		if (buffer == editPane.getBuffer() && line == physicalLine + 1) {
			JEditTextArea textArea = editPane.getTextArea();
			Point p = textArea.offsetToXY(textArea.getLineStartOffset(physicalLine));
			paintLine(gfx, screenLine, physicalLine, start, end, y, p);
		}
	}

	protected abstract void paintLine(Graphics2D gfx, int screenLine,
			int physicalLine, int start, int end, int y, Point p);

}
