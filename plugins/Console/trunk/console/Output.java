/*
 * Output.java - Console output interface
 * Copyright (C) 2001, 2004 Slava Pestov
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

package console;

import java.awt.Color;
import javax.swing.text.AttributeSet;


/**
 * 
 * Console has a single output dockable window, which can contain multiple
 * shells. Each shell has an Output which is a writable thing.
 * It can be selected via a a JComboBox in the upper left corner of the Console. 
 * 
 * By default, each Console has two shells: A SystemShell and a BeanShell.
 * To obtain the Output for a given shell, you can do it one of two ways:
 * 
 *     console.getOutput(shellName); (CHECKME)
 *     console.getOutput(shell);           (CHECKME)
 *     
 *     Each Console manages the mapping of Shells to Outputs.
 *     
 * To create a new Shell for your own plugin (as the Antelope plugin does),
 *  you should register a Shell using the services.xml interface, and obtain
 *  the Output for that Shell using one of the methods above.
 *  
 */

public interface Output
{
	
	/**
	 * Processes the message through the Console's Syntax coloring,
	 * and sends a colored message if there is an error/warning. 
	 * @param message
	 * @since 4.3.3
	 */
	void printColored(String message);
	
	/**
	 * Prints a line of text with the specified color.
	 * @param color The color. If null, the default color will be used
	 * @param msg The message
	 */
	void print(Color color, String msg);

	/**
	 * Prints a string of text with the specified color, without the
	 * terminating newline.
	 * @param attrs Character attributes
	 * @param msg The message
	 * @since Console 4.0
	 */
	void writeAttrs(AttributeSet attrs, String msg);

	/**
	 * Call when the command finishes executing.
	 */
	void commandDone();
}
