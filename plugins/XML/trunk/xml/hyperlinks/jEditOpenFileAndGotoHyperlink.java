/*
 * jEditOpenFileAndGotoHyperlink.java - open file in jEdit and move to a given location
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Eric Le Lay
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
package xml.hyperlinks;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import gatchan.jedit.hyperlinks.AbstractHyperlink;

/**
 * This hyperlink will open a file path in jEdit
 * and move to given location
 * @author Eric Le Lay
 * @version $Id$
 */
public class jEditOpenFileAndGotoHyperlink extends AbstractHyperlink
{
	private final String path;
	private final int gotoLine;
	private final int gotoCol;
	
	public jEditOpenFileAndGotoHyperlink(int start, int end, int line, String url, int gotoLine, int gotoColumn)
	{
		super(start, end, line, url);
		path = url;
		this.gotoLine = gotoLine;
		this.gotoCol = gotoColumn;
	}

	public void click(View view)
	{
		xml.XmlInsert.openAndGo(view, view.getTextArea(),
			path, gotoLine, gotoCol); 
	}
	
	public String toString(){
		return "jEditOpenFileAndGotoHyperlink["+path+":"+gotoLine+":"+gotoCol+"]";
	}
}
