/*
 * ModeHighlightingPlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Evan Wright
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
 
package modelighting;

//{{{ Imports
import java.awt.EventQueue;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;
//}}}

/**
 * Mode highlighting plugin core class. This class handles messages from the
 * edit bus and determines when to update the edit pane painters with new
 * styles.
 */
public class ModeHighlightingPlugin extends EditPlugin
{
	public static final String MODE_PROPERTY = "mode-highlighting.mode";
	
	public void start()
	{
		EditBus.addToBus(this);
	}
	
	public void stop()
	{
		EditBus.removeFromBus(this);
	}
	
	//{{{ Message handlers
	@EBHandler
	public void handleEditPaneUpdate(EditPaneUpdate msg)
	{
		EditPane pane = msg.getEditPane();
		
		if (msg.getWhat() == EditPaneUpdate.BUFFER_CHANGED ||
		    msg.getWhat() == EditPaneUpdate.CREATED)
		{
			ModeHighlighter.updatePane(pane);
		}
	}
	
	@EBHandler
	public void handlePropertiesChanged(PropertiesChanged msg)
	{
		// The edit panes will respond to this same message by reloading the style
		// information, so we will wait for this message to get handled, and then fix them
		// all
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				ModeHighlighter.updateAllEditPanes();
			}
		});
	}
	
	@EBHandler
	public void handleBufferUpdate(BufferUpdate msg)
	{
		Buffer buffer = msg.getBuffer();
		
		if (msg.getWhat() == BufferUpdate.PROPERTIES_CHANGED)
		{
			String previousMode =
				buffer.getStringProperty(ModeHighlightingPlugin.MODE_PROPERTY);
			String currentMode =
				(buffer.getMode() != null) ? buffer.getMode().getName() : "";
			
			if (!currentMode.equals(previousMode))
			{
				buffer.setStringProperty(ModeHighlightingPlugin.MODE_PROPERTY,
					currentMode);
				ModeHighlighter.updateBuffer(buffer);
			}		
		}
	} //}}}
}
