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

package browser;

import java.util.regex.Pattern;

public class GlobalRecord {
	private String [] fields;
	static private Pattern spaces = Pattern.compile("\\s+");
	
	public GlobalRecord(String line) {
    	fields = spaces.split(line, 4);
	}
	public String getName() {
		return fields[0];
	}
	public int getLine() {
		return Integer.parseInt(fields[1]);
	}
	public String getFile() {
		return fields[2];
	}
	public String getText() {
		return fields[3];
	}
}
