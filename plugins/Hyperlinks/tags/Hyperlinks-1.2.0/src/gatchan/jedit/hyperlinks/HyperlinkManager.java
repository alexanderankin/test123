/*
 * HyperlinkManager.java - The Hyperlink manager
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007, 2010 Matthieu Casanova
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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;

/**
 * The hyperlink manager is the class that will handle the hyperlinks for the textarea.
 *
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class HyperlinkManager
{
	private final JEditTextArea textArea;

	private HyperlinkTextAreaPainter painter;

	private MyMouseAdapter mouseAdapter;

	private MyMouseMotionAdapter mouseMotionAdapter;
	private MyFocusListener focusListener;

	public HyperlinkManager(JEditTextArea textArea)
	{
		this.textArea = textArea;
		painter = new HyperlinkTextAreaPainter(textArea);
		mouseAdapter = new MyMouseAdapter();

		textArea.getPainter().addExtension(painter);
		textArea.getPainter().addMouseListener(mouseAdapter);
		mouseMotionAdapter = new MyMouseMotionAdapter();
		textArea.getPainter().addMouseMotionListener(mouseMotionAdapter);
		focusListener = new MyFocusListener();
		textArea.addFocusListener(focusListener);
        EditBus.addToBus(this);
	}

    @EditBus.EBHandler
    public void handlerEditPaneUpdate(EditPaneUpdate epu)
    {
        if (epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED
            && epu.getEditPane().getTextArea() == textArea)
        {
            painter.setHyperLink(null);
        }
    }

	public void dispose()
	{
        EditBus.removeFromBus(this);
		textArea.getPainter().removeExtension(painter);
		textArea.getPainter().removeMouseListener(mouseAdapter);
		textArea.getPainter().removeMouseMotionListener(mouseMotionAdapter);
		textArea.removeFocusListener(focusListener);
		painter = null;
		mouseAdapter = null;
		mouseMotionAdapter = null;
		focusListener = null;
	}

	private class MyFocusListener extends FocusAdapter
	{
		@Override
		public void focusLost(FocusEvent e)
		{
			painter.setHyperLink(null);
		}
	}

	private class MyMouseAdapter extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			boolean control = (OperatingSystem.isMacOS() && e.isMetaDown())
					  || (!OperatingSystem.isMacOS() && e.isControlDown());

			if (!control)
			{
				painter.setHyperLink(null);
				return;
			}
			Hyperlink hyperlink = painter.getHyperLink();
			if (hyperlink != null)
			{
				hyperlink.click(textArea.getView());
			}
		}
	}

	private class MyMouseMotionAdapter extends MouseMotionAdapter
	{
		@Override
		public void mouseMoved(MouseEvent e)
		{
			boolean control = (OperatingSystem.isMacOS() && e.isMetaDown())
					  || (!OperatingSystem.isMacOS() && e.isControlDown());

			if (!control)
			{
				painter.setHyperLink(null);
				return;
			}

			Buffer buffer = (Buffer) textArea.getBuffer();
			if (!buffer.isLoaded())
				return;
			HyperlinkSource hyperlinkSource = getHyperlinkSource(buffer);
			if (hyperlinkSource == null)
			{
				painter.setHyperLink(null);
				return;
			}
			int offset = textArea.xyToOffset(e.getX(), e.getY());
			Hyperlink link = hyperlinkSource.getHyperlink(buffer, offset);
			painter.setHyperLink(link);
		}

		private HyperlinkSource getHyperlinkSource(JEditBuffer buffer)
		{
			String hyperlinkSourceName = buffer.getStringProperty(HyperlinkSource.PROPERTY);
			if (hyperlinkSourceName == null)
			{
				hyperlinkSourceName = jEdit.getProperty("mode." + buffer.getMode() + '.' + HyperlinkSource.PROPERTY);
			}
			if (hyperlinkSourceName == null)
			{
				hyperlinkSourceName = jEdit.getProperty(HyperlinkSource.DEFAULT_PROPERTY);
			}

			if (hyperlinkSourceName == null)
				return null;

            String[] split = hyperlinkSourceName.split(",");
            if (split.length == 1)
            {
                return (HyperlinkSource) ServiceManager.getService(HyperlinkSource.SERVICE, hyperlinkSourceName);
            }
            List<HyperlinkSource> sources = new LinkedList<HyperlinkSource>();
            for (String s : split)
            {
                HyperlinkSource source = (HyperlinkSource) ServiceManager.getService(HyperlinkSource.SERVICE, hyperlinkSourceName.trim());
                if (source != null)
                {
                    sources.add(source);
                }
            }
            if (sources.isEmpty())
                return null;
            if (sources.size() == 1)
            {
                return sources.get(0);
            }
            return new FallbackHyperlinkSource(sources);
		}
	}
}
