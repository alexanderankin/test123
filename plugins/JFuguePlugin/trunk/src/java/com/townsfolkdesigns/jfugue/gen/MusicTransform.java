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

/**
 * @author elberry
 * 
 */
public class MusicTransform implements Serializable {

	/**
    * 
    */
	private static final long serialVersionUID = 6971970633057471544L;

	private boolean active;

	private String source;

	private String target;

	/**
    * 
    */
	public MusicTransform() {
	}

	public MusicTransform(String source, String target) {
		setSource(source);
		setTarget(target);
	}

	/**
    * @return the source
    */
	public String getSource() {
		return source;
	}

	/**
    * @return the target
    */
	public String getTarget() {
		return target;
	}

	/**
    * @return the active
    */
	public boolean isActive() {
		return active;
	}

	/**
    * @param active
    *           the active to set
    */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
    * @param source
    *           the source to set
    */
	public void setSource(String transform) {
		this.source = transform;
	}

	/**
    * @param target
    *           the target to set
    */
	public void setTarget(String target) {
		this.target = target;
	}

}
