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

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;


/**
 * @author elberry
 *
 */
public class LSysMusicGenerator extends AbstractMusicGenerator<String, StringMusicTransform> implements Serializable {	
	

	/**
    * 
    */
   private static final long serialVersionUID = -9195899792977648100L;

	/**
	 * 
	 */
	public LSysMusicGenerator() {
		setMusicTransforms(new LinkedList<StringMusicTransform>());
	}

	/* (non-Javadoc)
	 * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#generate(java.lang.String, int)
	 */
	public String generate(String axiom, int iterations) {
		String generatedMusic = axiom;
		for(int iteration = 0; iteration < iterations; iteration++) {
			for(StringMusicTransform musicTransform : getMusicTransforms()) {
				if(musicTransform.isActive()) {
					generatedMusic = StringUtils.replace(generatedMusic, musicTransform.getSource(), musicTransform.getTarget());
				}
			}
		}
		return generatedMusic;
	}

	/* (non-Javadoc)
	 * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#getName()
	 */
	public String getName() {
		return "Lindenmayer System";
	}
}
