/*
Copyright (C) 2010  Shlomy Reinstein

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

package perl;

import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import common.gui.FileTextField;

@SuppressWarnings("serial")
public class GeneralOptionPane extends AbstractOptionPane
{
	static final String OPT = Plugin.OPTION_PREFIX;
	static final String MSG = Plugin.MESSAGE_PREFIX;

	private FileTextField path;
	
	public GeneralOptionPane()
	{
		super("perl-dbg-general");
		setBorder(new EmptyBorder(5, 5, 5, 5));
		path = new FileTextField(getPerlPath(), false);
		addComponent(jEdit.getProperty(MSG + "perl_path"), path);
	}
	
	public void save()
	{
		jEdit.setProperty(OPT + "perl_path", path.getTextField().getText());
	}

	public static String getPerlPath()
	{
		return jEdit.getProperty(OPT + "perl_path");
	}
}

