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
import java.net.URL;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;


import sidekick.enhanced.SourceAsset;

public class Tag extends SourceAsset
{
	static final String ICON_PREFIX = "icons.";
	Hashtable info;
	String tag;
	String pat;
	int line;
	String signature = null;
	String kind = null;
	static Hashtable<String, ImageIcon> icons =
		new Hashtable<String, ImageIcon>();
		
	Tag(final Buffer buffer, final Hashtable info)
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
		if (jEdit.getBooleanProperty(OptionPane.SHOW_ICONS, true))
		{
			String iconName =
				jEdit.getProperty(OptionPane.ICONS + kind);
			if (iconName != null && iconName.length() > 0)
			{
				ImageIcon icon = (ImageIcon) icons.get(kind);
				if (icon == null)
				{
					URL url = Tag.class.getClassLoader().getResource(
							"icons/" + iconName);
			        try {
			            icon = new ImageIcon(url);
			        }
			        catch (Exception e) {
			        	e.printStackTrace();
			        }
					if (icon != null)
						icons.put(kind, icon);
				}
				if (icon != null)
					setIcon(icon);
			}
		}
	}
	
	int getLine()
	{
		return line;
	}
	
	String getKind()
	{
		return kind;
	}
	Hashtable getInfo()
	{
		return info;
	}
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		return (getShortString().equals(obj));
	}
};
