/*
 * ModeHighlighter.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Evan Wright
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
 
package modelighting;
 
//{{{ Imports
import java.awt.Color;
import java.awt.Font;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

import org.gjt.sp.util.Log;
import org.gjt.sp.util.SyntaxUtilities;
//}}}
 
public class ModeHighlighter
{
	/**
	 * Updates the text area painters for each text area currently showing
	 * the given buffer.
	 *
	 * @param buffer the buffer whose mode has changed
	 */
	public static void updateBuffer(Buffer buffer)
	{	
		View[] views = jEdit.getViews();
		for (View view : views)
		{
			EditPane[] panes = view.getEditPanes();
			for (EditPane pane : panes)
			{
				if (pane.getBuffer() == buffer)
				{
					updatePane(pane);
				}
			}
		}
	}
	
	/**
	 * Updates the text area painters for every edit pane.
	 */
	public static void updateAllEditPanes()
	{
		View[] views = jEdit.getViews();
		for (View view : views)
		{
			EditPane[] panes = view.getEditPanes();
			for (EditPane pane : panes)
			{
				updatePane(pane);
			}
		}
	}
	
	/**
	 * Update the painter for the given edit pane's text area, based on
	 * the current mode for the edit pane's buffer.
	 */
	public static void updatePane(EditPane pane)
	{	
		JEditTextArea textArea = pane.getTextArea();
		TextAreaPainter painter = textArea.getPainter();
		Mode mode = pane.getBuffer().getMode();
		
		StyleSet styleSet = getActiveStyleSet(mode);
		
		painter.setStyles(styleSet.getTokenStyles());
		painter.setFoldLineStyle(styleSet.getFoldLineStyles());
	}
	
	//{{{ Private members
	/**
	 * Get the currently-active style set for the given mode, or the default one if there is
	 * none.
	 *
	 * @param mode the mode whose style set to retrieve
	 */
	private static StyleSet getActiveStyleSet(Mode mode)
	{
		String modeName = mode == null ? null : mode.getName();
		String active = StyleSettings.getActiveStyleSetName(modeName);
		if (active == null)
		{
			String defaultStyleSet = StyleSettings.getActiveStyleSetName(null);
			return StyleSettings.loadStyleSet(null, defaultStyleSet);
		}
		else
		{
			return StyleSettings.loadStyleSet(modeName, active);
		}
	}
	//}}}
}
