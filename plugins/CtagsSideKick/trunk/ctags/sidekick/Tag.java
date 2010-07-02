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
import java.util.Hashtable;

import org.gjt.sp.jedit.Buffer;

import sidekick.enhanced.SourceAsset;
import ctags.sidekick.renderers.ITextProvider;

public class Tag extends SourceAsset
{
	static final String ICON_PREFIX = "icons.";
	Hashtable<String, String> info;
	String tag;
	String pat;
	int line;
	String signature = null;
	String kind = null;
	
	public Tag(final Buffer buffer, final Hashtable<String, String> info)
	{
		super((String)info.get("k_tag"),
			  Integer.parseInt((String)info.get("line")),
			  new LinePosition(buffer,
					  Integer.parseInt((String) info.get("line")) - 1, true));
		this.info = info;
		tag = (String)info.get("k_tag");
		pat = (String)info.get("k_pat");
		line = Integer.parseInt((String)info.get("line")) - 1;
		signature = (String)info.get("signature");
		if (signature != null && signature.length() > 0)
			setShortDescription(tag + signature);
		kind = (String)info.get("kind");
	}
	public void setTextProvider(ITextProvider provider)
	{
		setShortDescription(provider.getString(this));
	}
	
	public int getLine()
	{
		return line;
	}
	
	public String getKind()
	{
		return kind;
	}
	public Hashtable<String, String> getInfo()
	{
		return info;
	}
	public String getField(String name)
	{
		return (String) info.get(name);
	}
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj instanceof String)
			return (getShortString().equals((String)obj));
		else if (obj instanceof Tag)
			return getShortString().equals(((Tag)obj).getShortString());
		return false;
	}
};
