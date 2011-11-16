/**
 * GeneralOptionPane - FirstMate Plugin
 *
 * Copyright 2006 Ollie Rutherfurd <oliver@jedit.org>
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
 *
 * $Id$
 */
package firstmate.options;

//{{{ imports
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
//}}}

public class GeneralOptionPane extends AbstractOptionPane
{
	//{{{ constructor
	public GeneralOptionPane()
	{
		super("firstmate");
	} //}}}

	//{{{ _init() method
	public void _init()
	{
		autoEnable = new JCheckBox(jEdit.getProperty("options.firstmate.auto-enable"),
			jEdit.getBooleanProperty("firstmate.auto-enable", true));
		addComponent(autoEnable);
		noApostropheAfterLetter = new JCheckBox(jEdit.getProperty("options.firstmate.no-apostrophe-after-letter"),
			jEdit.getBooleanProperty("firstmate.no-apostrophe-after-letter", true));
		addComponent(noApostropheAfterLetter);
		undoOnBackspace = new JCheckBox(jEdit.getProperty("options.firstmate.undo-on-backspace"),
			jEdit.getBooleanProperty("firstmate.undo-on-backspace", true));
		addComponent(undoOnBackspace);
		wrapSelections = new JCheckBox(jEdit.getProperty("options.firstmate.wrap-selections"),
			jEdit.getBooleanProperty("firstmate.wrap-selections", true));
		addComponent(wrapSelections);
	} //}}}

	//{{{ _save() method
	public void _save()
	{
		jEdit.setBooleanProperty("firstmate.auto-enable",
			autoEnable.isSelected());
		jEdit.setBooleanProperty("firstmate.no-apostrophe-after-letter",
			noApostropheAfterLetter.isSelected());
		jEdit.setBooleanProperty("firstmate.undo-on-backspace",
			undoOnBackspace.isSelected());
		jEdit.setBooleanProperty("firstmate.wrap-selections",
			wrapSelections.isSelected());
	} //}}}

	//{{{ privates
	private JCheckBox autoEnable;
	private JCheckBox noApostropheAfterLetter;
	private JCheckBox undoOnBackspace;
	private JCheckBox wrapSelections;
	//}}}
}

// :folding=explicit:collapseFolds=1:tabSize=4:noTabs=false:
