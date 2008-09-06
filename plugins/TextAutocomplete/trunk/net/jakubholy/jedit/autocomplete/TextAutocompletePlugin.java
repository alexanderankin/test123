/* 
 * TextAutocompletePlugin.java
 * $id$
 * author Jakub (Kuba) Holy, 2005
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
package net.jakubholy.jedit.autocomplete;

//import java.util.Vector;

import java.util.Hashtable;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.msg.BufferUpdate;

// TODO (low): Options - also options local to a buffer? for an edit mode? possible to save?
// TODO: options: + button to check beanshell code 
/**
 * Automatic text completion for buffers.
 * Try to complete a word typed in an associated buffer.
 * Automatically try to find any completions for the current
 * word being typed and if there're any, present them to the user 
 * in a pop-up list. A list of possible completions is 
 * constructed from words typed so far in the buffer that
 * satisfy some conditions.
 * 
 * @author Jakub Holý
 */
public class TextAutocompletePlugin extends EBPlugin {
	
	/** The prefix of (nearly) all properties used by this plugin. */
	public static final String PROPS_PREFIX = "plugin.net.jakubholy.jedit.autocomplete.TextAutocompletePlugin.";

	/** Store temp. buffer's name to detect whether it has changed while saving. */
	private Hashtable<Buffer, String> tmpBufferNameStore = new Hashtable<Buffer, String>(); 

	/**
	 * @see org.gjt.sp.jedit.EditPlugin#start()
	 */
	public void start() 
	{
		super.start();
		EditBus.addToBus(this);
	}

	// {{{ stop() method
	/**
	 * Called upon plugin unload - remove all instances of all classes that may
	 * be still bound to some buffers.
	 */
	public void stop() 
	{
		AutoComplete.destroyAllAutoCompletes();
		EditBus.removeFromBus(this);
	} //}}}

	/**
	 * @inheritDoc
	 * @see org.gjt.sp.jedit.EBPlugin#handleMessage(org.gjt.sp.jedit.EBMessage)
	 */
	public void handleMessage(EBMessage message) 
	{
		// Start for new buffers if it is required
		// Stop for closed buffers (== free resources - remebered words...)
		if (message instanceof BufferUpdate) 
		{
			final BufferUpdate bufferUpdateMsg = (BufferUpdate) message;
			final Buffer buffer = bufferUpdateMsg.getBuffer();
			
			if(bufferUpdateMsg.getWhat() == BufferUpdate.LOADED)
			{
				if(PreferencesManager.getPreferencesManager().isStartForBuffers())
				{ 
					AutoComplete.attachAction( bufferUpdateMsg.getBuffer() ); 
				} 
			}
			else if(bufferUpdateMsg.getWhat() == BufferUpdate.CLOSED)
			{ 
				AutoComplete.detachAction( bufferUpdateMsg.getBuffer() ); 
			}
			
			// Default word list loading handling
			if(bufferUpdateMsg.getWhat() == BufferUpdate.CREATED)
			{
				
			}
			else if(bufferUpdateMsg.getWhat() == BufferUpdate.SAVING) 
			{
				// Remember the current buffer name for a later check of its change 
				tmpBufferNameStore.put(buffer, buffer.getName());
			}
			else if(bufferUpdateMsg.getWhat() == BufferUpdate.SAVED) 
			{
				final String oldName = tmpBufferNameStore.remove(buffer);
				if (! buffer.getName().equals(oldName)) {
					// TODO Buffer name has changed => recheck for default words
					
				}
			}
		}
	}
	
	

}
