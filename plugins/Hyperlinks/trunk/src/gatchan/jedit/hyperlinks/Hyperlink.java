/*
 * Hyperlink.java - An Hyperlink
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package gatchan.jedit.hyperlinks;

import org.gjt.sp.jedit.View;

/**
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public interface Hyperlink
{
	/**
	 * Returns the start offset.
	 */
	int getStartOffset();

	/**
	 * Returns the end offset.
	 */
	int getEndOffset();

	/**
	 * Returns the start physical line where the hyperlink is.
	 *
	 * @return the start physical line
	 */
	int getStartLine();


	/**
	 * Returns the end physical line where the hyperlink is.
	 *
	 * @return the end physical line
	 */
	int getEndLine();

	/**
	 * Returns the tooltip that must be displayed for this tooltip.
	 *
	 * @return a tooltip text. It can be null
	 */
	String getTooltip();

	/**
	 * Do the click action on the link.
	 *
	 * @param view the view
	 */
	void click(View view);
}
