/*
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

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


//{{{ AbstractModeOptionPane class
/**
 * AbstractModeOptionPane.java - Option Pane with Mode combobox
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
 * @author Alan Ezust ezust@users.sourceforge.net
 * @since SideKick 0.7.5
 *
 */

abstract public class AbstractModeOptionPane 
	extends AbstractOptionPane implements ModeOptionPane {

	String mode = ModeOptionsDialog.ALL;

	// {{{ static mode property getter/setter interface
	
	public static void setIntegerProperty(String mode, String key, int value) {
		jEdit.setIntegerProperty(modePrefix(mode, key), value);
	}

	public static void setBooleanProperty(String mode, String key, boolean value) {
		jEdit.setBooleanProperty(modePrefix(mode, key), value);
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

	public static void clearModeProperty(String mode, String key) {
		jEdit.unsetProperty(modePrefix(mode, key));
	}

	public static boolean modePropertyExists(String mode, String key) {
		return (jEdit.getProperty(modePrefix(mode, key)) != null);
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
	
	protected AbstractModeOptionPane(String name) {
		super(name);
	}

	protected String getMode() {
		return mode;
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
