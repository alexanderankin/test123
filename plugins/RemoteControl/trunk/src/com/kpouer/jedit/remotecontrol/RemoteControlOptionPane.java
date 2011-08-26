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

package com.kpouer.jedit.remotecontrol;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.gui.NumericTextField;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
public class RemoteControlOptionPane extends AbstractOptionPane
{

	private JTextField port;
	private JTextField pin;

	public RemoteControlOptionPane()
	{
		super("remotecontrol");
	}

	@Override
	public void _init()
	{
		addComponent(jEdit.getProperty("remotecontrol.port.label", "Port:"), port = new NumericTextField(jEdit.getProperty("remotecontrol.port")));
		addComponent(jEdit.getProperty("remotecontrol.pincode.label", "Pin:"), pin = new NumericTextField(jEdit.getProperty("remotecontrol.pincode")));
		addComponent(new JLabel(jEdit.getProperty("remotecontrol.pincode.detail")));
	}

	@Override
	public void _save()
	{
		jEdit.setIntegerProperty("options.port", Integer.parseInt(port.getText()));
		jEdit.setIntegerProperty("options.pin", Integer.parseInt(pin.getText()));
	}
}
