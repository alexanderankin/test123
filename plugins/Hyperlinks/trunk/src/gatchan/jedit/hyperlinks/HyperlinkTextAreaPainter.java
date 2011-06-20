/*
 * HyperlinkTextAreaPainter.java - The Hyperlink painter
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007, 2011 Matthieu Casanova
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
package gatchan.jedit.hyperlinks;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

import java.awt.*;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class HyperlinkTextAreaPainter extends TextAreaExtension
{
	private final JEditTextArea textArea;
	private Hyperlink hyperLink;

	static Color color;

	public HyperlinkTextAreaPainter(JEditTextArea textArea)
	{
		this.textArea = textArea;
	}

	@Override
    public void paintValidLine(Graphics2D gfx, int screenLine, int physicalLine, int start, int end, int y)
	{
		Hyperlink link = hyperLink;
		if (link == null)
			return;
		if (link.getStartLine() != physicalLine)
			return;
        Point startPoint = textArea.offsetToXY(link.getStartOffset());
        if (startPoint == null)
        {
            return;
        }
        int startX = startPoint.x;
        Point endPoint = textArea.offsetToXY(link.getEndOffset());
        if (endPoint == null)
        {
            return;
        }
        int endX = endPoint.x;
		gfx.setColor(color);
		FontMetrics fm = textArea.getPainter().getFontMetrics();
		y += fm.getAscent();
		gfx.drawLine(startX, y + 1, endX, y + 1);
	}

	@Override
    public String getToolTipText(int x, int y)
	{
		Hyperlink link = hyperLink;
		if (link == null)
			return null;

		int offset = textArea.xyToOffset(x, y);
		if (hyperLink.getStartOffset() <= offset && hyperLink.getEndOffset() >= offset)
		{
			return hyperLink.getTooltip();
		}
		return null;
	}

	public Hyperlink getHyperLink()
	{
		return hyperLink;
	}

	public void setHyperLink(Hyperlink hyperLink)
	{
		if (hyperLink != this.hyperLink)
		{
			if (hyperLink == null)
			{
				int line = this.hyperLink.getStartLine();
				textArea.invalidateLine(line);
			}
			else
			{
				int lineNew = hyperLink.getStartLine();
				textArea.invalidateLine(lineNew);
				if (this.hyperLink != null)
				{
					int lineOld = this.hyperLink.getStartLine();
					textArea.invalidateLine(lineOld);
				}
			}
			this.hyperLink = hyperLink;
		}
	}
}
