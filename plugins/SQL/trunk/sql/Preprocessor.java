/**
 * Preprocessor.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
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

package sql;

import java.beans.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public abstract class Preprocessor
{
	protected boolean enabled;

	protected View view;

	protected PropertyChangeSupport enableChangeSupport;


	/**
	 *Constructor for the Preprocessor object
	 *
	 * @since
	 */
	public Preprocessor()
	{
		enableChangeSupport = new PropertyChangeSupport(this);
		enabled = "true".equals(SqlPlugin.getGlobalProperty(getClass().getName() + ".enabled"));
		Log.log(Log.DEBUG, Preprocessor.class,
		        getClass().getName() + " is enabled: " + enabled);
	}


	/**
	 *  Sets the Enabled attribute of the Preprocessor object
	 *
	 * @param  enabled  The new Enabled value
	 */
	public void setEnabled(boolean enabled)
	{
		final boolean oldval = this.enabled;
		this.enabled = enabled;
		SqlPlugin.setGlobalProperty(getClass().getName() + ".enabled", enabled ? "true" : "false");

		if (oldval != enabled)
			enableChangeSupport.firePropertyChange("enabled", oldval, enabled);
	}


	/**
	 *Sets the View attribute of the Preprocessor object
	 *
	 * @param  v  The new View value
	 * @since
	 */
	public void setView(View v)
	{
		view = v;
	}


	/**
	 *  Gets the Enabled attribute of the Preprocessor object
	 *
	 * @return    The Enabled value
	 */
	public boolean isEnabled()
	{
		return enabled;
	}


	/**
	 *  Gets the OptionPane attribute of the Preprocessor object
	 *
	 * @return    The OptionPane value
	 */
	public OptionPane getOptionPane()
	{
		return null;
	}


	/**
	 *  Adds a feature to the EnabledStateListener attribute of the Preprocessor object
	 *
	 * @param  listener  The feature to be added to the EnabledStateListener attribute
	 */
	public void addEnabledStateListener(PropertyChangeListener listener)
	{
		enableChangeSupport.addPropertyChangeListener("enabled", listener);
	}


	/**
	 *  Description of the Method
	 */
	public void toggleEnabled()
	{
		setEnabled(!enabled);
	}


	/**
	 *Description of the Method
	 *
	 * @param  text  Description of Parameter
	 * @return       Description of the Returned Value
	 * @since
	 */
	public String process(String text)
	{
		if (!enabled)
			return text;
		return doProcess(text);
	}


	/**
	 *Description of the Method
	 *
	 * @param  text  Description of Parameter
	 * @return       Description of the Returned Value
	 * @since
	 */
	protected abstract String doProcess(String text);
}

