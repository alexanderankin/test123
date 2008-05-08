/*
 * ModeOptionsPane.java - Option Pane with Mode combobox
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Alan Ezust
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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;


//{{{ ModeOptionsPane class
/**
 * A specialized OptionPane which has mode-overridable properties. 
 * Includes convenience methods for getting and setting mode properties,
 * as well as global defaults. 
 * To define an option pane which gets added to the
 * ModeOptionsDialog of SideKick, you can define a service like this:
 <pre>
 	&lt;SERVICE CLASS=&quot;org.gjt.sp.jedit.options.ModeOptionPane&quot; NAME=&quot;sidekick&quot;&gt;
		new sidekick.SideKickModeOptionsPane();
	&lt;/SERVICE&gt;
 </pre>    
 * 
 * @author Alan Ezust alan@jedit.org
 * @since SideKick 0.7.1
 *
 */

abstract public class ModeOptionsPane 
	extends AbstractOptionPane implements ItemListener
{

	String mode = ModeOptionsDialog.ALL;

	// {{{ static mode property getter/setter interface
	
	public static void setIntegerProperty(String mode, String key, int value) {
		jEdit.setIntegerProperty(modePrefix(mode, key), value);
	}

	public static boolean getBooleanProperty(String mode, String key) {
		if (jEdit.getProperty(modePrefix(mode, key)) == null)
			return jEdit.getBooleanProperty(key);
		else return jEdit.getBooleanProperty(modePrefix(mode, key));
	}
	
	
	public static int getIntegerProperty(String mode, String key, int def) {
		if (jEdit.getProperty(modePrefix(mode, key)) != null) {
			return jEdit.getIntegerProperty(modePrefix(mode, key), def);
		}
		else {
			return jEdit.getIntegerProperty(key, def);
		}
	}
	
	public static void setProperty(String mode, String key, String value) {
		jEdit.setProperty(modePrefix(mode,key), value);
	}
	
	public static String getProperty(String mode, String key) {
		String retval = jEdit.getProperty(modePrefix(mode, key));
		if (retval == null) return jEdit.getProperty(key);
		else return retval;
	}
	
	public static String modePrefix(String mode, String key) {
		if (mode != null && !mode.equals(ModeOptionsDialog.ALL)) 
			return "mode." +mode + "." + key;
		else return key;
	}
	// }}}
	
	// {{{ Methods
	/**
	 * Load mode properties into input components,
	 * using global props if mode props are not set.
	 *
	 */
	
	final public void load() {
		if (initialized) _load();
	}

	/**
	 * Override this method. Called by @ref load().
	 */
	protected abstract void _load();
	
	/**
	 * Un-sets all mode properties, so that the global defaults will be used instead.
	 *
	 */
	protected abstract void _reset();
	
	
	protected ModeOptionsPane(String name) {
		super(name);
	}

	protected String getMode() {
		return mode;
	}

	public void itemStateChanged(ItemEvent e)
	{
		if (e.getSource() instanceof JComboBox) {
			save();
			mode = e.getItem().toString();
			load();
		}
	}

	// }}}
	
	// {{{ Mode Property setter/getter interface 

	/**
	 * @param key a the property name.
	 * @return a mode-specific property, depending on what mode is selected in the combo box
	 * of the SideKickProperties dialog.
	 */
	public String getProperty(String key) {
		return getProperty(getMode(), key);
	}

	public boolean getBooleanProperty(String key ) {
		return getBooleanProperty(getMode(), key);
	}
	public int getIntegerProperty(String key, int def) {
		return getIntegerProperty(getMode(), key, def);
	}
	public void setBooleanProperty(String key, boolean val) {
		jEdit.setBooleanProperty(modePrefix(mode, key), val);
	}
	
	public void clearModeProperty(String key) {
		jEdit.unsetProperty(modePrefix(mode, key));
	}

	public void setIntegerProperty(String key, int value) {
		setIntegerProperty(getMode(), key, value);
	}
	
	public void setProperty(String key, String value) {
		setProperty(getMode(), key, value);
	} // }}} 
	
} // }}}
