/*
Copyright (C) 2006  Shlomy Reinstein

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

package ctags.sidekick;
import java.util.Vector;


public class KindTreeMapper implements ITreeMapper {

	public void setLang(String lang)
	{
	}
	public Vector<Object> getPath(Tag tag) {
		Vector<Object> path = new Vector<Object>();
		String kind = tag.getKind();
		if (kind != null && kind.length() > 0)
			path.add(kind);
		path.add(tag);
		return path;
	}

}
