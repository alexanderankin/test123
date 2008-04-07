/*
 * RFCReaderOptionPane.java - The RFCReader options panel
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
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

package gatchan.jedit.rfcreader;

//{{{ Imports

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import javax.swing.*;
import java.util.Vector;
//}}}


/**
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class RFCReaderOptionPane extends AbstractOptionPane
{
	private JList mirrorList;

	//{{{ RFCReaderOptionPane constructor
	public RFCReaderOptionPane()
	{
		super("rfcreader");
	} //}}}

	//{{{ _init() method
	public void _init()
	{
		String currentMirrorId = jEdit.getProperty(RFCHyperlink.MIRROR_PROPERTY);
		Mirror currentMirror = null;
		String id;
		int i = 0;
		Vector<Mirror> mirrors = new Vector<Mirror>();
		while((id = jEdit.getProperty("options.rfcreader.rfcsources." + i++)) != null)
		{
			Mirror mirror = new Mirror();
			mirror.id = id;
			mirror.label = jEdit.getProperty("options.rfcreader.rfcsources."+id+".label");
			if (id.equals(currentMirrorId))
				currentMirror = mirror;
			mirrors.add(mirror);
		}
		mirrorList = new JList(mirrors);
		if (currentMirror != null)
			mirrorList.setSelectedValue(currentMirror, true);
		else
			mirrorList.setSelectedIndex(0);
		addComponent("Mirror : ", new JScrollPane(mirrorList));
	} //}}}

	//{{{ _save() method
	public void _save()
	{
		Mirror mirror = (Mirror) mirrorList.getSelectedValue();
		jEdit.setProperty(RFCHyperlink.MIRROR_PROPERTY, mirror.id);
		Log.log(Log.DEBUG,this, "Using mirror:"+mirror);
	} //}}}

	//{{{ Mirror class
	public static class Mirror
	{
		public String id;
		public String label;

		public String toString()
		{
			return label;
		}
	} //}}}
} //}}}