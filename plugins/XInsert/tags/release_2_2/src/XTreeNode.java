/*
 * 17:26:43 01/08/99
 *
 * XTreeNode.java - Part of the XTree system
 * Copyright (C) 1999 Romain Guy - powerteam@chez.com
 * Portions Copyright (C) 2000 Dominic Stolerman - dominic@sspd.org.uk
 * www.chez.com/powerteam
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


import java.util.Hashtable;
import javax.swing.tree.*;

public class XTreeNode extends DefaultMutableTreeNode {
  private int pos = -1;
	private Hashtable table;

  public XTreeNode(String userObject) {
    super(userObject);
  }

  public XTreeNode(String userObject, int pos) {
    super(userObject);
    this.pos = pos;
  }

  public void setIndex(int pos) {
    this.pos = pos;
  }

  public int getIndex() {
    return pos;
  }

	public void addVariable(String key, String value) {
		if(table == null)
			table = new Hashtable();
		table.put(key, value);
	}

	public String getVariable(String key) {
		if(table != null)
			return (String)table.get(key);
		else
			return null;
	}

	public boolean containsVariable(String key) {
		if(table != null)
			return table.containsKey(key);
		else
			return false;
	}

	public boolean hasVariables() {
		if(table != null && !table.isEmpty())
			return true;
		else 
			return false;
	}
}

// End of XTreeNode.java

