/*
 * SideKickBindings.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
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

package sidekick;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Component;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

/**
 * Manages our key bindings.
 */
class SideKickBindings extends KeyAdapter
{
	//{{{ keyTyped() method
	public void keyTyped(KeyEvent evt)
	{
		evt = KeyEventWorkaround.processKeyEvent(evt);
		if(evt == null)
			return;

		char ch = evt.getKeyChar();
		if(ch == '\b')
			return;

		View view = GUIUtilities.getView((Component)evt.getSource());
		SideKickParser parser = SideKickPlugin.getParserForView(view);

		if(parser != null && parser.supportsCompletion())
		{
			String parseKeys = parser.getParseTriggers();
			if(parseKeys != null && parseKeys.indexOf(ch) != -1)
				SideKickPlugin.parse(view,false);

			String instantKeys = parser.getInstantCompletionTriggers();
			if(instantKeys != null && instantKeys.indexOf(ch) != -1)
				SideKickActions.keyComplete(view);
			else if(parser.canCompleteAnywhere())
				SideKickActions.keyCompleteWithDelay(view);
		}
	} //}}}
}
