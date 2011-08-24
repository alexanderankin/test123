/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 jEdit contributors
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

import com.kpouer.jedit.remotecontrol.jEditListener;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;

import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class EditPaneConverter extends AbstractSingleValueConverter
{
	private final jEditListener listener;

	public EditPaneConverter(jEditListener listener)
	{
		this.listener = listener;
	}

	@Override
	public boolean canConvert(Class aClass)
	{
		return aClass.equals(EditPane.class);
	}

	@Override
	public String toString(Object obj)
	{
		return listener.getEditPaneId(obj);
	}

	@Override
	public Object fromString(String s)
	{
		return null;
	}
}
