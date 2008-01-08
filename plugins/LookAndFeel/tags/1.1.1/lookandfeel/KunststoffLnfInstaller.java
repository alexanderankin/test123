/*
 * KunststoffLnfInstaller.java - Look And Feel plugin
 * Copyright (C) 2002 Calvin Yu
 *
 * :mode=java:tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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
package lookandfeel;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A class for installing nothing.
 */
public class KunststoffLnfInstaller extends LnfInstaller
{

	/**
	 * Install a non standard look and feel.
	 */
	public void install() throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
      UIManager.put("ClassLoader", com.incors.plaf.kunststoff.KunststoffLookAndFeel.class.getClassLoader());
	}

}
