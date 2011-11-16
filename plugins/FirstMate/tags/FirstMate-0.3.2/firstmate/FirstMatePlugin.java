/**
 * FirstMatePlugin.java - FirstMate Plugin
 *
 * Copyright 2006 Ollie Rutherfurd <oliver@jedit.org>
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
 *
 * $Id$
 */
package firstmate;

//{{{ imports
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultInputHandler;
import org.gjt.sp.jedit.gui.InputHandler;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;
//}}}

/**
 * FirstMate plugin.
 * 
 * Implements some features from TextMate.
 */
public class FirstMatePlugin extends EBPlugin
{
	//{{{ start() method
	public void start()
	{
		loadProperties();
		enabled = jEdit.getBooleanProperty("firstmate.auto-enable", true);
		if(enabled)
		{
			Log.log(Log.DEBUG, this, "Adding FirstMate input handlers");
			View[] views = jEdit.getViews();
			for(int i=0; i < views.length; i++)
			{
				initView(views[i]);
			}
		}
	} //}}}

	//{{{ start() method
	public void stop()
	{
		if(enabled)
		{
			View[] views = jEdit.getViews();
			for(int i=0; i < views.length; i++)
			{
				unInitView(views[i]);
			}
		}
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof ViewUpdate)
		{
			ViewUpdate vu = (ViewUpdate)msg;
			if(vu.getWhat() == ViewUpdate.CREATED)
			{
				if(enabled)
					initView(vu.getView());
			}
			else if(vu.getWhat() == ViewUpdate.CLOSED)
			{
				if(enabled)
					unInitView(vu.getView());
			}
		}
	} //}}}

	//{{{ loadProperties() method
	private static void loadProperties()
	{
		noApostropheAfterLetter = jEdit.getBooleanProperty("firstmate.no-apostrophe-after-letter", true);
		undoOnBackspace = jEdit.getBooleanProperty("firstmate.undo-on-backspace", true);
		wrapSelections = jEdit.getBooleanProperty("firstmate.wrap-selections", true);
	}//}}}

	//{{{ initView() method
	private static void initView(View view)
	{
		InputHandler defaultHandler = view.getInputHandler();
		if(defaultHandler instanceof DefaultInputHandler)
		{
			FirstMateInputHandler handler = new FirstMateInputHandler(view, 
				(DefaultInputHandler)defaultHandler);
			view.setInputHandler(handler);
		}
		else
		{
			Log.log(Log.ERROR, FirstMatePlugin.class,
				"Current InputHandler not instance of DefaultInputHandler:" + defaultHandler);
		}
	}//}}}

	//{{{ unInitView() method
	private static void unInitView(View view)
	{
		InputHandler handler = view.getInputHandler();
		if(handler instanceof FirstMateInputHandler)
		{
			view.setInputHandler(((FirstMateInputHandler)handler).getDefaultHandler());
		}
		else
		{
			Log.log(Log.DEBUG, FirstMatePlugin.class,
				"handler is not FirstMateInputHandler: " + handler);
		}
	}//}}}

	//{{{ isEnabled() method
	public static boolean isEnabled()
	{
		return enabled;
	}//}}}

	//{{{ setEnabled() method
	public static void setEnabled(boolean enable)
	{
		if(enabled != enable)
		{
			View[] views = jEdit.getViews();
			for(int i = 0; i < views.length; i++)
			{
				if(enable)
					initView(views[i]);
				else
					unInitView(views[i]);
			}
			enabled = enable;
		}
	} //}}}

	//{{{ getInputHandler() method
	public static boolean getIgnoreNext()
	{
		return ignoreNext;
	} //}}}

	//{{{ setIgnoreNext() method
	public static void setIgnoreNext(boolean ignore)
	{
		ignoreNext = ignore;
	} //}}}

	//{{{ getWrapSelections() method
	public static boolean getWrapSelections()
	{
		return wrapSelections;
	} //}}}

	//{{{ getNoApostropheAfterLetter() method
	public static boolean getNoApostropheAfterLetter()
	{
		return noApostropheAfterLetter;
	} //}}}

	//{{{ getUndoOnBackspace() method
	public static boolean getUndoOnBackspace()
	{
		return undoOnBackspace;
	} //}}}

	//{{{ privates
	private static boolean enabled = false;
	private static boolean ignoreNext = false;
	private static boolean noApostropheAfterLetter = true;
	private static boolean undoOnBackspace = true;
	private static boolean wrapSelections = true;
	//}}}
}

// :folding=explicit:collapseFolds=1:tabSize=4:noTabs=false:
