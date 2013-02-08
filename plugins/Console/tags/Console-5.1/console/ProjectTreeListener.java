/*
 * ProjectTreeListener.java - for listening to ProjectViewer
 * events in the Console.

 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2005, 2009 by Alan Ezust
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
package console;

// {{{ imports


import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import projectviewer.event.ViewerUpdate;
import org.gjt.sp.jedit.bsh.NameSpace;
// }}}

// {{{ ProjectTreeListener class
/**
 *
 * Listener of ProjectViewer project opened events
 * Triggers console beanshell scripts as actions in response.
 * 
 * @author ezust
 *
 */

public class ProjectTreeListener implements EBComponent
{
	
	private Console console;
	// {{{ constructor
	public ProjectTreeListener(Console c)
	{
		console = c;
	}
	// }}}
	public void finalize() {
		console = null;
	}

	// {{{ handleMessage()
	/**
	 * On project change...
	 */
	public void handleMessage(EBMessage msg)
	{
		if (console == null) {
			EditBus.removeFromBus(this);
			return;
		}
		if (!jEdit.getBooleanProperty("console.changedir.pvchange")) return;
		if (!(msg.getClass().getName().endsWith("ViewerUpdate"))) return;
		final ViewerUpdate vu = (ViewerUpdate) msg;
		if (vu.getType() != ViewerUpdate.Type.PROJECT_LOADED) return;
		final View view = vu.getView();
		if ((view == null) || (view != console.getView())) return;
		new Thread()
		{
			public void run()
			{
				try
				{
					sleep(500);
				}
				catch (InterruptedException ie)
				{
					interrupt();
				}
				String code = "changeToPvRoot(view);";
				NameSpace namespace =  BeanShell.getNameSpace();
				BeanShell.eval(view, namespace, code);
			}
		}.start();
	} // }}}


} // }}}
