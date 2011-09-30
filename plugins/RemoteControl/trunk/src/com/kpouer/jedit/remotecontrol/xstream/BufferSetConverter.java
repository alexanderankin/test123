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

package com.kpouer.jedit.remotecontrol.xstream;

import java.nio.Buffer;

import com.kpouer.jedit.remotecontrol.CommandResponse;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.bufferset.BufferSet;

/**
 * @author Matthieu Casanova
 */
public class BufferSetConverter extends ArrayConverter
{
	public BufferSetConverter(Mapper mapper)
	{
		super(mapper);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		BufferSet bufferSet = (BufferSet) source;
		org.gjt.sp.jedit.Buffer[] allBuffers = bufferSet.getAllBuffers();
		super.marshal(allBuffers, writer,
			      context);
	}

	@Override
	public boolean canConvert(Class type)
	{
		return BufferSet.class.equals(type);
	}
}
