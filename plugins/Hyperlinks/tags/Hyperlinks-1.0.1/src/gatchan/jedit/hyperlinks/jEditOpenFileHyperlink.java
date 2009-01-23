/*
 * jEditOpenFileHyperlink.java - The jEdit open file Hyperlink
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

import gatchan.jedit.hyperlinks.AbstractHyperlink;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

/**
 * This hyperlink will open a file path in jEdit.
 *
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class jEditOpenFileHyperlink extends AbstractHyperlink
{
	private String path;
	public jEditOpenFileHyperlink(int start, int end, int line, String url)
	{
		super(start, end, line, url);
		this.path = url;
	}

	public void click(View view)
	{
		jEdit.openFile(view, path);
	}
}
