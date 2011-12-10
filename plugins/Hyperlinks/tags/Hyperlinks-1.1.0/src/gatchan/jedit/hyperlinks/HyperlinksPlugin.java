/*
 * HyperlinksPlugin.java - The Hyperlinks plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007, 2009 Matthieu Casanova
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
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/**
 * @author Matthieu Casanova
 * @version $Id: HighlightPlugin.java,v 1.20 2006/06/21 09:40:32 kpouer Exp $
 */
public class HyperlinksPlugin extends EditPlugin
{
	@Override
    public void start()
	{
        EditBus.addToBus(this);
		View view = jEdit.getFirstView();
		while (view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for (int i = 0; i < panes.length; i++)
			{
				JEditTextArea textArea = panes[i].getTextArea();
				initTextArea(textArea);
			}
			view = view.getNext();
		}
	}

	@Override
    public void stop()
	{
        EditBus.removeFromBus(this);
		View view = jEdit.getFirstView();
		while (view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for (int i = 0; i < panes.length; i++)
			{
				JEditTextArea textArea = panes[i].getTextArea();
				uninitTextArea(textArea);
			}
			view = view.getNext();
		}
	}

	private static void uninitTextArea(JEditTextArea textArea)
	{
		HyperlinkManager clientProperty = (HyperlinkManager) textArea.getClientProperty(HyperlinkManager.class);
		clientProperty.dispose();
		textArea.putClientProperty(HyperlinkManager.class, null);
	}

	private static void initTextArea(JEditTextArea textArea)
	{
		textArea.putClientProperty(HyperlinkManager.class, new HyperlinkManager(textArea));
	}

    @EditBus.EBHandler
	public void handlePropertiesChanged(PropertiesChanged message)
	{
        HyperlinkTextAreaPainter.color = jEdit.getColorProperty("options.hyperlink.color.value");
    }

    @EditBus.EBHandler
	public void handleEditPaneMessage(EditPaneUpdate message)
	{
		JEditTextArea textArea = message.getEditPane().getTextArea();
		Object what = message.getWhat();

		if (what == EditPaneUpdate.CREATED)
		{
			initTextArea(textArea);
		}
		else if (what == EditPaneUpdate.DESTROYED)
		{
			uninitTextArea(textArea);
		}
	}

}