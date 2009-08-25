/*
 * ChangeMarker - A text area extension for marking changed lines
 *
 * Copyright (C) 2009 Shlomy Reinstein
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
 */

package lcm;

import java.awt.Color;
import java.awt.Graphics2D;


import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;


public class ChangeMarker extends TextAreaExtension
{
	static final int WIDTH = 12;
	private EditPane editPane;

	public ChangeMarker(EditPane editPane)
	{
		this.editPane = editPane;
		editPane.getTextArea().getGutter().addExtension(this);
	}

	public void paintValidLine(Graphics2D gfx, int screenLine,
			int physicalLine, int start, int end, int y)
	{
		Buffer buffer = editPane.getBuffer();
		if (buffer.isUntitled())
			return;
		BufferChangedLines changes =
			LCMPlugin.getInstance().getBufferChangedLines(buffer);
		if ((changes == null) || (! changes.isChanged(physicalLine)))
			return;
		JEditTextArea ta = editPane.getTextArea();
		int x = 0;
		int width = ta.getGutter().getWidth();
		int lineHeight = ta.getPainter().getFontMetrics().getHeight();
		Color c = gfx.getColor();
		gfx.setColor(LCMOptions.getBgColor());
		gfx.fillRect(x + width - WIDTH, y, WIDTH, lineHeight);
		gfx.setColor(c);
	}

	public void remove()
	{
		editPane.getTextArea().getGutter().removeExtension(this);
	}
}
