/*
 * RFCListModel.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Matthieu Casanova
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

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class RFCListModel extends AbstractListModel
{
	private List<RFC> data;
	private List<RFC> defaultList;

	public RFCListModel(Map<Integer, RFC> data)
	{
		defaultList = new ArrayList<RFC>(data.values());
		this.data = defaultList;
	}

	public void setData(List<RFC> data)
	{
		if (data != null)
		{
			this.data = data;
			fireContentsChanged(this, 0, this.data.size());
		}
	}

	void reset()
	{
		setData(defaultList);
	}

	public int getSize()
	{
		return data.size();
	}

	public Object getElementAt(int index)
	{
		return data.get(index);
	}
}