/*
 * JgoodiesExtWindows.java - Look And Feel plugin
 * Copyright (C) 2004 Adrian Petru Dimulescu
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

import com.jgoodies.plaf.windows.ExtWindowsLookAndFeel;
import org.gjt.sp.jedit.AbstractOptionPane;

/**
 * Installs one of the JGoodies look and feels.
 * See <a href="https://looks.dev.java.net/"</a>.
 */
public class JgoodiesExtWindows  implements LookAndFeelInstaller
{
	public String getName() {
		return "Jgoodies Ext Windows";		
	}
	
	/**
	 * Install a non standard look and feel.
	 */
	public void install() throws UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(new ExtWindowsLookAndFeel());
		UIManager.put("ClassLoader", ExtWindowsLookAndFeel.class.getClassLoader());
	}
	
	public AbstractOptionPane getOptionPane() {
		return null;	
	}
}
