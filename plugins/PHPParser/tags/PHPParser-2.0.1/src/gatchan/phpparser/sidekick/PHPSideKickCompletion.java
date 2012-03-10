/*
 * PHPSideKickCompletion.java - The PHP Parser
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2010 Matthieu Casanova
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
package gatchan.phpparser.sidekick;

import gatchan.phpparser.project.itemfinder.PHPItemCellRenderer;
import net.sourceforge.phpdt.internal.compiler.ast.ClassDeclaration;
import net.sourceforge.phpdt.internal.compiler.ast.ClassHeader;
import net.sourceforge.phpdt.internal.compiler.ast.MethodDeclaration;
import net.sourceforge.phpdt.internal.compiler.ast.MethodHeader;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.Selection;
import sidekick.SideKickCompletion;

import javax.swing.*;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class PHPSideKickCompletion extends SideKickCompletion
{
	private final String lastWord;

	//{{{ PHPSideKickCompletion constructor
	public PHPSideKickCompletion(String word, String lastWord)
	{
		super(jEdit.getActiveView(), word);
		this.lastWord = lastWord;
	} //}}}

	//{{{ addItem() method
	public void addItem(Object item, String word)
	{
		boolean caseSensitive = !(item instanceof MethodDeclaration);
		if (item.toString().regionMatches(caseSensitive, 0, word, 0, word.length()))
		{
			if (!items.contains(item))
			{
				items.add(item);
			}
		}
	} //}}}

	//{{{ getRenderer() method
	@Override
	public ListCellRenderer getRenderer()
	{
		return new PHPItemCellRenderer();
	} //}}}

	//{{{ getItemsCount()
	public int getItemsCount()
	{
		return items.size();
	} //}}}

	//{{{ addOutlineableList() method
	public void addOutlineableList(List items, String word)
	{
		for (int i = 0; i < items.size(); i++)
		{
			addItem(items.get(i), word);
		}
	} //}}}

	//{{{ insert()
	@Override
	public void insert(int index)
	{
		Object object = items.get(index);
		int caret = textArea.getCaretPosition();
		if (text.length() != 0)
		{
			Selection selection = textArea.getSelectionAtOffset(caret);
			if (selection == null)
			{
				selection = new Selection.Range(caret - text.length(), caret);
			}
			else
			{
				int start = selection.getStart();
				int end = selection.getEnd();
				selection = new Selection.Range(start - text.length(), end);
			}
			textArea.setSelection(selection);
		}
		String insertText;
		if (object instanceof Outlineable)
		{
			insertText = ((Outlineable) object).getName();
			if (object instanceof MethodDeclaration ||
				(object instanceof ClassDeclaration &&
					"new".equals(lastWord)))
			{
				insertText += "()";
				caret--; //to go between the parenthesis
			}
		}
		else if (object instanceof ClassHeader)
		{
			insertText = ((ClassHeader) object).getName();
			if ("new".equals(lastWord))
			{
				insertText += "()";
				caret--; //to go between the parenthesis
			}
		}
		else if (object instanceof MethodHeader)
		{
			insertText = ((MethodHeader) object).getName();
		}
		else
		{
			insertText = (String) object;
		}
		caret += insertText.length();
		textArea.setSelectedText(insertText);
		// textArea.setCaretPosition(caret);
	} //}}}

	//{{{ getTokenLength() method
	@Override
	public int getTokenLength()
	{
		return text.length();
	} //}}}

	//{{{ handleKeystroke() method
	@Override
	public boolean handleKeystroke(int selectedIndex, char keyChar)
	{
		if (keyChar == '\n' || keyChar == ' ' || keyChar == '\t')
		{
			insert(selectedIndex);
			if (keyChar == ' ')
			{
				//inserting the space after the insertion
				textArea.userInput(' ');
			}
			else if (keyChar == '\t')
			{
				//removing the end of the word
				textArea.deleteWord();
			}
			return false;
		}
		else
		{
			textArea.userInput(keyChar);
			return true;
		}
	} //}}}
}
