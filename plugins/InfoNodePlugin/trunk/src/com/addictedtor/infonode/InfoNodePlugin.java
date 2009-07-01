/*
 * Copyright (c) 2009, Romain Francois <francoisromain@free.fr>
 *
 * This file is part of the InfoNodePlugin plugin for jedit
 *
 * The InfoNodePlugin plugin for jedit is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * The InfoNodePlugin plugin for jedit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the InfoNodePlugin plugin for jedit. If not, see <http://www.gnu.org/licenses/>.
 */

package com.addictedtor.infonode;

import java.io.File;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.util.Log;

/**
 * Plugin class. 
 * 
 * @author Romain Francois <francoisromain@free.fr>
 * 
 */
public class InfoNodePlugin extends EBPlugin {

	/**
	 * Name of the plugin
	 */
	public static final String NAME = "InfoNodePlugin";
	
	/**
	 * Start method of the plugin. Currently empty
	 */
	@Override
	public void start() {
		File home = getPluginHome() ;
		Log.log( Log.ERROR, this, "home: " + home ) ;
		if( !home.exists() ){
			home.mkdirs() ;
		}
	}

	/**
	 * Stop method of the plugin. Currently empty
	 */
	@Override
	public void stop() {}

	/**
	 * EBComponent implementation. Currently placeholder
	 */
	@Override
	public void handleMessage(EBMessage message) {}

}
