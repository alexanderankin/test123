/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package superabbrevs.gui;

import javax.swing.JCheckBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * @author Matthieu Casanova
 */
public class SuperAbbrevsOptionPane extends AbstractOptionPane
{
	//{{{ SuperAbbrevsOptionPane constructor
	public SuperAbbrevsOptionPane()
	{
		super("superabbrevs");
	} //}}}

	//{{{ _init() method
	@Override
	public void _init()
	{
		addComponent(zenCoding = new JCheckBox(
			jEdit.getProperty("options.superabbrevs.zencoding.label")));
		zenCoding.setSelected(jEdit.getBooleanProperty("options.superabbrevs.zencoding"));
	} //}}}

	//{{{ _save() method
	@Override
	public void _save()
	{
		jEdit.setBooleanProperty("options.superabbrevs.zencoding", zenCoding.isSelected());
	} //}}}

	private JCheckBox zenCoding;
}
