/*
 * XmlCompletion.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
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

package xml.completion;

import javax.swing.SwingUtilities;
import java.util.List;

//{{{ Imports
import javax.swing.Icon;
import javax.swing.ListCellRenderer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;
import sidekick.SideKickCompletion;
import xml.*;
//}}}

public class XmlCompletion extends SideKickCompletion
{
	//{{{ XmlCompletion constructor
	public XmlCompletion(View view, List items, String word, XmlParsedData data,
		String closingTag)
	{
		this.view = view;
		textArea = view.getTextArea();
		this.items = items;
		this.word = word;
		this.data = data;
		this.closingTag = closingTag;
	} //}}}

	//{{{ getRenderer() method
	public ListCellRenderer getRenderer()
	{
		return XmlListCellRenderer.INSTANCE;
	} //}}}

	//{{{ getTokenLength() method
	public int getTokenLength()
	{
		return word.length() + 1;
	} //}}}

	//{{{ insert() method
	public void insert(int index)
	{
		insert(get(index),'\n');
	} //}}}

	//{{{ handleKeystroke() method
	public boolean handleKeystroke(int index, char ch)
	{
		switch(ch)
		{
			case ';': case '>': case ' ': case '\t': case '\n':
				// execute below code
				break;
			case '/':
				if(jEdit.getBooleanProperty("xml.close-complete"))
				{
					XmlActions.completeClosingTag(view,true);
					return false;
				}
			default:
				Macros.Recorder recorder
					= view.getMacroRecorder();
				if(recorder != null)
					recorder.recordInput(1,ch,false);
				textArea.userInput(ch);
				return true;
		}

		if(index != -1)
			insert(get(index),ch);
		else if(ch == '>')
			XmlActions.insertClosingTagKeyTyped(view);
		else
			textArea.userInput(ch);

		return false;
	} //}}}

	//{{{ Private members
	private View view;
	private JEditTextArea textArea;
	private String word;
	private XmlParsedData data;
	private String closingTag;

	//{{{ insert() method
	private void insert(Object obj, char ch)
	{
		Macros.Recorder recorder = view.getMacroRecorder();

		String insert;
		int caret;

		if(obj instanceof XmlListCellRenderer.Comment)
		{
			insert = "!--  -->".substring(word.length());
			caret = 4;
		}
		else if(obj instanceof XmlListCellRenderer.CDATA)
		{
			insert = "![CDATA[]]>".substring(word.length());
			caret = 3;
		}
		else if(obj instanceof XmlListCellRenderer.ClosingTag)
		{
			insert = ("/" + closingTag + ">")
				.substring(word.length());
			caret = 0;
		}
		else if(obj instanceof ElementDecl)
		{
			ElementDecl element = (ElementDecl)obj;

			StringBuffer buf = new StringBuffer();
			buf.append(element.name.substring(word.length()));

			buf.append(element.getRequiredAttributesString());

			if(ch == '\n' || ch == '>')
			{
				if(element.empty)
				{
					if(data.html)
						buf.append(">");
					else
						buf.append(XmlActions.getStandaloneEnd());

					caret = 0;
				}
				else
				{
					buf.append(">");

					int start = buf.length();

					if(jEdit.getBooleanProperty(
						"xml.close-complete-open"))
					{
						buf.append("</");
						buf.append(element.name);
						buf.append(">");
					}

					caret = buf.length() - start;
				}

				if(ch == '\n' && element.attributes.size() != 0)
				{
					// hide the popup first, since the edit tag
					// dialog is modal
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							XmlActions.showEditTagDialog(view);
						}
					});
				}
			}
			else
			{
				buf.append(ch);
				caret = 0;
			}

			insert = buf.toString();
		}
		else if(obj instanceof EntityDecl)
		{
			EntityDecl entity = (EntityDecl)obj;

			insert = entity.name.substring(word.length()) + ";";
			caret = 0;
		}
		else
			throw new IllegalArgumentException("What's this? " + obj);

		if(recorder != null)
			recorder.recordInput(insert,false);
		textArea.setSelectedText(insert);

		if(caret != 0)
		{
			String code = "textArea.setCaretPosition("
				+ "textArea.getCaretPosition() - "
				+ caret + ");";
			if(recorder != null)
				recorder.record(code);
			BeanShell.eval(view,BeanShell.getNameSpace(),code);
		}
	} //}}}

	//}}}
}
