/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package perl.variables;

import java.util.Enumeration;

@SuppressWarnings("serial")
public class ArrayRangeVar extends GdbVar {

	private GdbVar parentVar;
	int from, to;
	
	public ArrayRangeVar(String name, GdbVar parent, int from, int to) {
		super(name);
		this.parentVar = parent;
		this.from = from;
		this.to = to;
	}

	@Override
	protected void createGdbVar() {
		// Do not create a gdb var ... this is a placeholder
	}

	@SuppressWarnings("unchecked")
	@Override
	public void done() {
		Enumeration<GdbVar> c = children();
		while (c.hasMoreElements()) {
			c.nextElement().done();
		}
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public void update() {
		updateChildren();
	}

	@Override
	protected void doCreateChildren() {
		for (int i = from; i <= to; i++) {
			GdbVar child = new GdbArrayElementVar(parentVar.name, i);
			child.setChangeListener(listener);
			add(child);
		}
		notifyListener();
	}

}
