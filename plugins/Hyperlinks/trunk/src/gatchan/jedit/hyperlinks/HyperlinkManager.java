/*
 * HyperlinkManager.java - The Hyperlink manager
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
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

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

import java.awt.event.*;

/**
 * The hyperlink manager is the class that will handle the hyperlinks for the textarea.
 *
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class HyperlinkManager
{
	private JEditTextArea textArea;

	private HyperlinkTextAreaPainter painter;

	private boolean active;
	private MyKeyListener keyListener;
	private MyMouseAdapter mouseAdapter;

	private MyMouseMotionAdapter mouseMotionAdapter;

	public HyperlinkManager(JEditTextArea textArea)
	{
		this.textArea = textArea;
		painter = new HyperlinkTextAreaPainter(textArea);
		keyListener = new MyKeyListener();
		mouseAdapter = new MyMouseAdapter();

		textArea.getPainter().addExtension(painter);
		textArea.addKeyListener(keyListener);
		textArea.getPainter().addMouseListener(mouseAdapter);
		mouseMotionAdapter = new MyMouseMotionAdapter();
		textArea.getPainter().addMouseMotionListener(mouseMotionAdapter);
	}

	public void dispose()
	{
		textArea.getPainter().removeExtension(painter);
		textArea.removeKeyListener(keyListener);
		textArea.removeMouseListener(mouseAdapter);
		textArea.removeMouseMotionListener(mouseMotionAdapter);
	}

	private class MyKeyListener extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			if (!active && e.getKeyCode() == KeyEvent.VK_CONTROL)
			{
				Log.log(Log.DEBUG, this, "Link tracking active");
				active = true;
			}
		}

		public void keyReleased(KeyEvent e)
		{
			if (active && e.getKeyCode() == KeyEvent.VK_CONTROL)
			{
				Log.log(Log.DEBUG, this, "Link tracking inactive");
				active = false;
				painter.setHyperLink(null);
			}
		}
	}

	private class MyMouseAdapter extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (!active)
				return;
			Hyperlink hyperlink = painter.getHyperLink();
			if (hyperlink != null)
			{
				hyperlink.click(textArea.getView());
			}
		}
	}

	private class MyMouseMotionAdapter extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e)
		{
			if (!active)
				return;
			Buffer buffer = (Buffer) textArea.getBuffer();
			HyperlinkSource hyperlinkSource = (HyperlinkSource) buffer.getProperty(HyperlinkSource.PROPERTY);
			if (hyperlinkSource == null)
			{
				painter.setHyperLink(null);
				return;
			}
			int offset = textArea.xyToOffset(e.getX(), e.getY());
			Hyperlink link = hyperlinkSource.getHyperlink(buffer, offset);
			painter.setHyperLink(link);
		}
	}
}
