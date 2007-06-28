/*
 * JFugue Plugin is a plugin for jEdit that provides basic functionality and 
 * access to JFugue.
 * Copyright (C) 2007 Eric Berry
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * 
 */
package com.townsfolkdesigns.jfugue.gen;

/**
 * @author elberry
 *
 */
public class StringMusicTransform extends AbstractMusicTransform<String> {

	/**
    * 
    */
   private static final long serialVersionUID = 9156963396796425396L;

	/**
	 * 
	 */
	public StringMusicTransform() {
	}

	/**
	 * @param source
	 * @param target
	 */
	public StringMusicTransform(String source, String target) {
		super(source, target);
	}

}
