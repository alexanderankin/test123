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

import java.io.File;
import java.io.Serializable;

/**
 * @author elberry
 *
 */
public class ImageMusicGenerator extends AbstractMusicGenerator<File, ColorMusicTransform> implements Serializable {

	/**
    * 
    */
   private static final long serialVersionUID = -4309942119402328416L;

	/**
	 * 
	 */
	public ImageMusicGenerator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#generate(java.lang.Object, int)
	 */
	public String generate(File axiom, int iterations) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#getName()
	 */
	public String getName() {
		return "Image Music Generator";
	}

}
