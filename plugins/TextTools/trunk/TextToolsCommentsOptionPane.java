/*
 * TextToolsCommentsOptionPane.java - Option pane for Text Tools comments actions
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002, 2003 Robert Fletcher
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

//{{{ Imports
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
//}}}

/**
 * Option pane for the Toggle Comments actions.
 *
 * @author    <a href="mailto:rfletch6@yahoo.co.uk">Robert Fletcher</a>
 * @version   $Revision$ $Date$
 */
public class TextToolsCommentsOptionPane extends AbstractOptionPane
{
	
	//{{{ TextToolsCommentsOptionPane constructor
	/** Constructor for the <code>TextToolsCommentsOptionPane</code> object. */
	public TextToolsCommentsOptionPane()
	{
		super("toggle-comments");
	} //}}}
	
	//{{{ init() method
	/** Initialises the option pane. */
	public void _init()
	{
		keepSelected = new JCheckBox(
			jEdit.getProperty("options.toggle-comments.keepSelected.title"),
			jEdit.getBooleanProperty("options.toggle-comments.keepSelected"));
		addComponent(keepSelected);
		addSeparator("options.toggle-comments.line-comments.title");
		commentAsBlock = new JCheckBox(
			jEdit.getProperty("options.toggle-comments.commentAsBlock.title"),
			jEdit.getBooleanProperty("options.toggle-comments.commentAsBlock"));
		addComponent(commentAsBlock);
		// line comment indenting options go in a group
		indentAtLineStart = new JRadioButton(
			jEdit.getProperty("options.toggle-comments.indentAtLineStart.title"),
			jEdit.getBooleanProperty("options.toggle-comments.indentAtLineStart"));
		indentWithCode = new JRadioButton(
			jEdit.getProperty("options.toggle-comments.indentWithCode.title"),
			jEdit.getBooleanProperty("options.toggle-comments.indentWithCode"));
		indentAsBlock = new JRadioButton(
			jEdit.getProperty("options.toggle-comments.indentAsBlock.title"),
			jEdit.getBooleanProperty("options.toggle-comments.indentAsBlock"));
		addComponent(indentAtLineStart);
		addComponent(indentWithCode);
		addComponent(indentAsBlock);
		lineIndentMode = new ButtonGroup();
		lineIndentMode.add(indentAtLineStart);
		lineIndentMode.add(indentWithCode);
		lineIndentMode.add(indentAsBlock);
	} //}}}
	
	//{{{ save() method
	/** Saves properties from the option pane. */
	public void _save()
	{
		jEdit.setBooleanProperty("options.toggle-comments.keepSelected",
			keepSelected.isSelected());
		jEdit.setBooleanProperty("options.toggle-comments.commentAsBlock",
			commentAsBlock.isSelected());
		jEdit.setBooleanProperty("options.toggle-comments.indentAtLineStart",
			indentAtLineStart.isSelected());
		jEdit.setBooleanProperty("options.toggle-comments.indentWithCode",
			indentWithCode.isSelected());
		jEdit.setBooleanProperty("options.toggle-comments.indentAsBlock",
			indentAsBlock.isSelected());
	} //}}}
	
	//{{{ Private members
	private JCheckBox keepSelected;
	private JCheckBox commentAsBlock;
	private ButtonGroup lineIndentMode;
	private JRadioButton indentAtLineStart;
	private JRadioButton indentWithCode;
	private JRadioButton indentAsBlock;
	//}}}

}

