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
package com.townsfolkdesigns.jfugue;

import org.jfugue.Player;

public class MusicPlayer implements Runnable {
	private String musicString;

	public MusicPlayer() {
	}

	public MusicPlayer(String musicString) {
		setMusicString(musicString);
	}

	/**
    * @return the musicString
    */
	public String getMusicString() {
		return musicString;
	}

	public void run() {
		Player player = JFuguePlayerFactory.createJFuguePlayer();
		player.play(getMusicString());
		return;
	}

	/**
    * @param musicString
    *           the musicString to set
    */
	public void setMusicString(String musicString) {
		this.musicString = musicString;
	}
}