/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
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

package com.kpouer.jedit.remotecontrol.serializer;

import com.kpouer.jedit.remotecontrol.RemoteControlPlugin;
import com.kpouer.jedit.remotecontrol.jEditListener;
import com.kpouer.jedit.remotecontrol.xstream.BufferConverter;
import com.kpouer.jedit.remotecontrol.xstream.EditPaneConverter;
import com.kpouer.jedit.remotecontrol.xstream.GlobalConverter;
import com.kpouer.jedit.remotecontrol.xstream.ViewConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * @author Matthieu Casanova
 */
public class XStreamJSON implements Serializer
{
	private final XStream xstream;

	public XStreamJSON()
	{
		jEditListener jEditListener = RemoteControlPlugin.server.getjEditListener();
		xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.registerConverter(new GlobalConverter());
		xstream.registerConverter(new BufferConverter());
		xstream.registerConverter(new ViewConverter(jEditListener));
		xstream.registerConverter(new EditPaneConverter(jEditListener));
//		xstream.registerConverter(new EBMessageConverter(), XStream.PRIORITY_NORMAL - 1);
//		xstream.registerConverter(new BufferChangingConverter());
//		xstream.registerConverter(new BufferUpdateConverter());
//		xstream.registerConverter(new BufferSetMessageConverter());
	}

	@Override
	public String serialize(Object object)
	{
		return xstream.toXML(object);
	}
}
