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

import org.gjt.sp.jedit.*;
import java.util.*;

/**
 * Manages our key bindings.
 */
class SideKickBindings
{
	//{{{ initBindings() method
	static void initBindings(SideKickParser parser, View view)
	{
		String parseTriggerKeys = parser.getParseTriggers();
		if(parseTriggerKeys != null)
		{
			for(int i = 0; i < parseTriggerKeys.length(); i++)
			{
				char ch = parseTriggerKeys.charAt(i);
				addBinding(ch);
			}
		}

		if(!parser.supportsCompletion())
			return;

		String delayPopupTriggerKeys = parser
			.getDelayCompletionTriggers();
		if(delayPopupTriggerKeys != null)
		{
			for(int i = 0; i < delayPopupTriggerKeys.length(); i++)
			{
				char ch = delayPopupTriggerKeys.charAt(i);
				addBinding(ch);
			}
		}

		String instantPopupTriggerKeys = parser
			.getInstantCompletionTriggers();
		if(instantPopupTriggerKeys != null)
		{
			for(int i = 0; i < instantPopupTriggerKeys.length(); i++)
			{
				char ch = instantPopupTriggerKeys.charAt(i);
				addBinding(ch);
			}
		}
	} //}}}

	//{{{ removeBindings() method
	/**
	 * Called on plugin unload to remove bindings from input handler.
	 */
	static void removeBindings()
	{
		Iterator iter = bindingSet.iterator();
		while(iter.hasNext())
		{
			jEdit.getInputHandler().removeKeyBinding(
				(String)iter.next());
		}
	} //}}}

	//{{{ Private members
	private static Set bindingSet = new TreeSet(new ArrayList());

	//{{{ addBinding() method
	private static void addBinding(char ch)
	{
		String str = String.valueOf(ch);
		bindingSet.add(str);
		jEdit.getInputHandler().addKeyBinding(str,
			new KeyTypedAction(ch));
	} //}}}

	//}}}

	//{{{ KeyTypedAction class
	static class KeyTypedAction extends EditAction
	{
		private char ch;

		KeyTypedAction(char ch)
		{
			super("-sidekick-key-typed-" + ch);
			this.ch = ch;
		}

		public boolean noRecord()
		{
			return true;
		}

		public void invoke(View view)
		{
			Macros.Recorder recorder = view.getMacroRecorder();
			if(recorder != null)
				recorder.record(1,ch);
			view.getTextArea().userInput(ch);

			Buffer buffer = view.getBuffer();
			SideKickParser parser = SideKickPlugin
				.getParserForBuffer(buffer);
			if(parser != null)
			{
				String str = parser.getParseTriggers();
				if(str != null && str.indexOf(ch) != -1)
				{
					SideKickPlugin.parse(view,false);
				}

				str = parser.getInstantCompletionTriggers();
				if(str != null && str.indexOf(ch) != -1)
				{
					SideKickActions.keyComplete(view);
				}

				str = parser.getDelayCompletionTriggers();
				if(str != null && str.indexOf(ch) != -1)
				{
					SideKickActions.keyCompleteWithDelay(view);
				}
			}
		}

		public String getCode()
		{
			return null;
		}
	} //}}}
}
