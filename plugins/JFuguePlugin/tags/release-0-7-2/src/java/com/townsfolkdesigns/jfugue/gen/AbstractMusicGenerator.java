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

import java.util.List;

/**
 * @author elberry
 *
 */
public abstract class AbstractMusicGenerator<A, T extends MusicTransform> implements MusicGenerator<A, T> {
	
	private List<T> musicTransforms;

	/* (non-Javadoc)
    * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#addTransform(com.townsfolkdesigns.jfugue.gen.AbstractMusicTransform)
    */
   public void addTransform(T musicTransform) {
	   getMusicTransforms().add(musicTransform);
   }

	/* (non-Javadoc)
    * @see com.townsfolkdesigns.jfugue.gen.MusicGenerator#removeTransform(com.townsfolkdesigns.jfugue.gen.AbstractMusicTransform)
    */
   public boolean removeTransform(T musicTransform) {
   	 return getMusicTransforms().remove(musicTransform);
   }

	/**
    * @return the musicTransforms
    */
   protected List<T> getMusicTransforms() {
   	return musicTransforms;
   }

	/**
    * @param musicTransforms the musicTransforms to set
    */
   protected void setMusicTransforms(List<T> musicTransforms) {
   	this.musicTransforms = musicTransforms;
   }

}
