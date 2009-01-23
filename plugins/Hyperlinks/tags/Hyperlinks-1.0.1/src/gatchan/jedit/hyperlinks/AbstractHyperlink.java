/*
 * AbstractHyperlink.java - The Abstract Hyperlink
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

/**
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public abstract class AbstractHyperlink implements Hyperlink
{
	private int start;

	private int end;

	private int startLine;

	private int endLine;

	private String tooltip;

	//{{{ AbstractHyperlink constructors
	protected AbstractHyperlink(int start, int end, int startLine, String tooltip)
	{
		this(start, end, startLine, startLine, tooltip);
	}

	protected AbstractHyperlink(int start, int end, int startLine, int endLine, String tooltip)
	{
		this.start = start;
		this.end = end;
		this.startLine = startLine;
		this.endLine = endLine;
		this.tooltip = tooltip;
	} //}}}


	//{{{ getStartOffset() method
	/**
	 * Returns the start offset.
	 */
	public int getStartOffset()
	{
		return start;
	}

	/**
	 * Returns the end offset.
	 */
	public int getEndOffset()
	{
		return end;
	}

	public int getStartLine()
	{
		return startLine;
	}

	public int getEndLine()
	{
		return endLine;
	}

	public String getTooltip()
	{
		return tooltip;
	}
}
