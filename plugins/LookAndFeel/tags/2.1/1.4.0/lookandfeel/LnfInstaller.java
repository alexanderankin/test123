/*
 * LnfInstaller.java - Look And Feel plugin
 * Copyright (C) 2002 Calvin Yu
 *
 * Changed: 11/11/2005 by Nilo J. Gonzalez to add support for NimROD Look And Feel.
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

import java.awt.Component;


/**
 * A class for installing a look and feel.
 */
public abstract class LnfInstaller
{

	/**
	 * Returns the list of the possible look and feel options.
	 */
	public static String[] getAvailableLookAndFeels()
	{
		return new String[] {
			"None",
			"JgoodiesExtWindows",
			"JgoodiesPlastic3D",
			"JgoodiesPlasticXP",
			"Kunststoff",
			"Lipstik",
			"Metouia",
			"Napkin",
			"NimROD",
			"Oyoaha",
			"Skin",
			"Substance",
			"Tonic"
		};
	}

	/**
	 * Returns the installer for the named look and feel.
	 */
	public static LnfInstaller createInstaller(String name) throws Exception
	{
		return (LnfInstaller)Class.forName("lookandfeel." + name + "LnfInstaller").newInstance();
	}

	/**
	 * Returns the name of this installer.
	 */
	public String getName()
	{
		String className = getClass().getName();
		return className.substring(className.lastIndexOf('.') + 1,
			className.length() - "LnfInstaller".length());
	}

	/**
	 * Returns a component used to configure the look and feel.
	 */
	public Component getOptionComponent()
	{
		return null;
	}

	/**
	 * Save the configuration from the given {@link getOptionComponent()}.
	 */
	public void saveOptions(Component comp)
	{
	}

	/**
	 * Install a non standard look and feel.
	 */
	public abstract void install() throws Exception;

	/**
	 * Returns <code>true</code> if the given string is <code>null</code>
	 * or empty.
	 */
	protected static boolean isEmpty(String s)
	{
		return s == null || s.trim().length() == 0;
	}

}
