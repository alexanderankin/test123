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
 *  Output interface - for console output devices.
 *
 */

public interface Output
{

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
	 * Changes the attributes of the given text selection to those specified.
	 * @param length length of the text
	 * @param attrs The new attributes
	 * @since Console 4.0
	 */

    void setAttrs(int length, AttributeSet attrs);

	/**
	 * Call when the command finishes executing.
	 */
	void commandDone();
}
