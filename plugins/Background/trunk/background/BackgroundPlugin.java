/*
 * BackgroundPlugin.java
 * Copyright (c) 2002 Andre Kaplan
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


package background;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.TextAreaPainter;


public class BackgroundPlugin extends EBPlugin
{
    public void start()
    {
	    View view = jEdit.getFirstView();
	    while (view != null)
	    {
		    EditPane[] panes = view.getEditPanes();
		    for (int i = 0; i < panes.length; i++)
			    initEditPane(panes[i]);
		    view = view.getNext();
	    }
    }


    public void stop()
    {
	    View view = jEdit.getFirstView();
	    while (view != null)
	    {
		    EditPane[] panes = view.getEditPanes();
		    for (int i = 0; i < panes.length; i++)
			    uninitEditPane(panes[i]);
		    view = view.getNext();
	    }
    }

    public void handleMessage(EBMessage message) {
        if (message instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate) message;
            EditPane editPane = epu.getEditPane();

            if (epu.getWhat() == EditPaneUpdate.CREATED) {
		    initEditPane(editPane);
            } else if (epu.getWhat() == EditPaneUpdate.DESTROYED) {
                uninitEditPane(editPane);
            }
        } else if (message instanceof PropertiesChanged) {
            BackgroundHighlight.propertiesChanged();
        }
    }

	private static void initEditPane(EditPane editPane)
	{
		TextAreaPainter textAreaPainter = editPane.getTextArea().getPainter();

		BackgroundHighlight backgroundHighlight =
		    (BackgroundHighlight) BackgroundHighlight.addHighlightTo(editPane);

		textAreaPainter.addExtension(TextAreaPainter.BACKGROUND_LAYER, backgroundHighlight);
	}

	private static void uninitEditPane(EditPane editPane)
	{
		TextAreaPainter textAreaPainter = editPane.getTextArea().getPainter();

		BackgroundHighlight backgroundHighlight =
		    BackgroundHighlight.getHighlightFor(editPane);

		textAreaPainter.removeExtension(backgroundHighlight);
		BackgroundHighlight.removeHighlightFrom(editPane);
	}
}

