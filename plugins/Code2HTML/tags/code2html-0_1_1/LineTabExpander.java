/*
 * LineTabExpander.java
 * Copyright (c) 2000 Andre Kaplan
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

import org.gjt.sp.util.Log;

public class LineTabExpander {
	private int tabSize;
	private int pos;
	private char[] spacer;
	
	public LineTabExpander() {
		this(4);
	}
	
	public LineTabExpander(int tabSize) {
		if (tabSize > 0) {
			this.tabSize = tabSize;
		}
		this.pos = 0;

		this.spacer = new char[this.tabSize];
		for (int i = 0; i < this.tabSize; i++) {
			this.spacer[i] = ' ';
		}
	}
	
	public String expand(char[] str, int strOff, int strLen) {
		StringBuffer buf = new StringBuffer();
		
		int off = strOff;
		int len = 0;
		char c;

		for (int i = 0; i < strLen; i++) {
			c = str[strOff + i];

			if (c != '\t') {
				len++;
				this.pos++;
			} else {
				int rem = (this.pos % this.tabSize);
				if (rem == 0) { rem = this.tabSize; }
				buf.append(str, off, len).append(this.spacer, 0, rem);
				off += len + 1;
				len = 0;
				this.pos += rem;
			}
		}

		buf.append(str, off, len);
		return buf.toString();
	}

	public String expand(String s) {
		return this.expand(s.toCharArray(), 0, s.length());
	}

	public int getPos() {
		return this.pos;
	}
	
	public void resetPos() {
		this.pos = 0;
	}
}
