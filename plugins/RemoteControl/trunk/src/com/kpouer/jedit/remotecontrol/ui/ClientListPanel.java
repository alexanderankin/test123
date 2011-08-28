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

package com.kpouer.jedit.remotecontrol.ui;

import com.kpouer.jedit.remotecontrol.ClientVisitor;
import com.kpouer.jedit.remotecontrol.RemoteClient;
import com.kpouer.jedit.remotecontrol.RemoteControlPlugin;
import com.kpouer.jedit.remotecontrol.ServerListener;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class ClientListPanel extends JPanel implements ServerListener
{

	private ClientTableModel tableModel;

	public ClientListPanel()
	{
		super(new BorderLayout());
		tableModel = new ClientTableModel();
		JTable table = new JTable(tableModel);
		table.setTableHeader(null);
		add(new JScrollPane(table));
	}

	@Override
	public void addNotify()
	{
		RemoteControlPlugin.server.visitClients(new ClientVisitor()
		{
			@Override
			public void visit(RemoteClient remoteClient)
			{
				tableModel.clients.add(remoteClient);
			}
		});
		RemoteControlPlugin.server.addServerListener(this);
		super.addNotify();
	}

	@Override
	public void removeNotify()
	{
		RemoteControlPlugin.server.removeServerListener(this);
		super.removeNotify();
		tableModel.clients.clear();
	}

	@Override
	public void clientConnected(final RemoteClient client)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				tableModel.clients.add(client);
				tableModel.fireTableDataChanged();
			}
		});
	}

	@Override
	public void clientDisconnected(final RemoteClient client)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				tableModel.clients.remove(client);
				tableModel.fireTableDataChanged();
			}
		});
	}

	@Override
	public void serverClosed()
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				tableModel.clients.clear();
				tableModel.fireTableDataChanged();
			}
		});
	}

	private class ClientTableModel extends AbstractTableModel
	{
		private List<RemoteClient> clients;

		private ClientTableModel()
		{
			clients = new ArrayList<RemoteClient>();
		}

		@Override
		public int getRowCount()
		{
			return clients.size();
		}

		@Override
		public int getColumnCount()
		{
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return clients.get(rowIndex).toString();
		}
	}
}
