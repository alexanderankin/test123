/*
 * RFCListPanel.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010, 2013 Matthieu Casanova
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

import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class RFCListPanel extends JPanel
{
	private final JList list;
	private final HistoryTextField searchField;
	private final RFCListModel model;
	private final RFCReaderPlugin plugin;

	public RFCListPanel()
	{
		super(new BorderLayout());
		searchField = new HistoryTextField("rfc.searchfield");
		searchField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				updateSearch();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				updateSearch();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				updateSearch();
			}
		});


		plugin = (RFCReaderPlugin) jEdit.getPlugin("gatchan.jedit.rfcreader.RFCReaderPlugin");
		model = new RFCListModel(plugin.rfcList);
		list = new JList(model);

		add(searchField, BorderLayout.NORTH);
		add(new JScrollPane(list));
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					RFC rfc = (RFC) list.getSelectedValue();
					if (rfc != null)
						RFCReaderPlugin.openRFC(jEdit.getActiveView(), rfc.getNumber());
				}
			}
		});
	}

	private void updateSearch()
	{
		String s = searchField.getText();
		if (!s.contains(" "))
		{
			s = s + "* " + s + "~";
		}
		if (s.isEmpty())
			model.reset();
		else
		{
			RFCIndex index = plugin.getIndex();
			List<RFC> rfcs = index.search(s);
			model.setData(rfcs);
		}
	}
}
