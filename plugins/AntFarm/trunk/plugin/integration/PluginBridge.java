/*
 *  PluginBridge.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 Brian Knowles
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package plugin.integration;

import org.gjt.sp.jedit.*;

/**
 *  A bridge from one plugin to another. Subclasses of should make sure they
 *  implement a default constructor.
 *
 * @author     steinbeck
 * @created    September 14, 2001
 */
public interface PluginBridge
{

	/**
	 *  Enable the bridge.
	 *
	 * @param  srcPlugin  Description of Parameter
	 * @param  tgtPlugin  Description of Parameter
	 * @param  view       Description of Parameter
	 * @return            <code>true</code> if the bridge was able to enable itself.
	 */
	public boolean enable( EditPlugin srcPlugin, EditPlugin tgtPlugin, View view );

}

