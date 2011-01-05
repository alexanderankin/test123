/*
 * Ancestor.java - An Ancestor
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
package gatchan.jedit.ancestor;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;

/**
 * An Ancestor. It represents a parent path for a file.
 *
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class Ancestor
{
	private final View view;

	private final String path;
	private final String name;
	
	//{{{ Ancestor constructor
	public Ancestor(View view, String path, String name)
	{
		this.view = view;
		this.path = path;
		this.name = name;
	} //}}}

	//{{{ getName() method 
	public String getName()
	{
		return name;
	} //}}}

	//{{{ doAction() method
	public void doAction()
	{
		VFSBrowser.browseDirectory(view, path);
	} //}}}
	
	//{{{ closeFiles() method
	public void closeContainedFiles()
	{
		Buffer b[] = jEdit.getBuffers();
		
		for(int i=0; i<b.length; i++)
			if(b[i].getDirectory().startsWith(path))
				jEdit.closeBuffer(view,b[i]);
	} //}}}

}
