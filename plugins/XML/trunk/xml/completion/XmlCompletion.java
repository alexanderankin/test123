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

public class XmlCompletion implements SideKickCompletion
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

	//{{{ size() method
	public int size()
	{
		return items.size();
	} //}}}

	//{{{ get() method
	public Object get(int index)
	{
		return items.get(index);
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
					XmlActions.completeClosingTag(view);
					return false;
				}
			default:
				textArea.userInput(ch);
				return true;
		}

		if(index == -1)
		{
			if(ch == '>')
				XmlActions.insertClosingTagKeyTyped(view);
			else
				textArea.userInput(ch);

			/* do nothing; dispose() is called below. */
		}
		else
		{
			Object obj = get(index);

			if(obj instanceof XmlListCellRenderer.Comment)
			{
				int caret = textArea.getCaretPosition();
				textArea.setSelectedText("!--  -->".substring(word.length()));
				textArea.setCaretPosition(caret + 4 - word.length());
			}
			else if(obj instanceof XmlListCellRenderer.CDATA)
			{
				int caret = textArea.getCaretPosition();
				textArea.setSelectedText("![CDATA[]]>".substring(word.length()));
				textArea.setCaretPosition(caret + 8 - word.length());
			}
			else if(obj instanceof XmlListCellRenderer.ClosingTag)
			{
				textArea.setSelectedText(("/" + closingTag + ">")
					.substring(word.length()));
				return false;
			}
			else if(obj instanceof ElementDecl)
			{
				ElementDecl element = (ElementDecl)obj;

				StringBuffer buf = new StringBuffer();
				buf.append(element.name.substring(word.length()));

				buf.append(element.getRequiredAttributesString());

				if(ch == '\n' || ch == '>')
				{
					int caret = textArea.getCaretPosition();

					if(element.empty)
					{
						if(data.html)
							buf.append(">");
						else
							buf.append("/>");

						caret += buf.length();
					}
					else
					{
						buf.append(">");

						caret += buf.length();

						if(jEdit.getBooleanProperty(
							"xml.close-complete-open"))
						{
							buf.append("</");
							buf.append(element.name);
							buf.append(">");
						}
					}

					textArea.setSelectedText(buf.toString());
					textArea.setCaretPosition(caret);

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
					return false;
				}
				else
				{
					buf.append(ch);

					textArea.setSelectedText(buf.toString());
				}
			}
			else if(obj instanceof EntityDecl)
			{
				EntityDecl entity = (EntityDecl)obj;

				textArea.setSelectedText(entity.name.substring(
					word.length()) + ";");
			}
			else if(obj instanceof IDDecl)
			{
				IDDecl id = (IDDecl)obj;

				if(ch == '\t')
				{
					textArea.setCaretPosition(id.declaringLocation
						.getOffset());
				}
				else
					textArea.setSelectedText(id.id);
			}
		}

		return false;
	} //}}}

	//{{{ getCompletionDescription() method
	public String getCompletionDescription(int index)
	{
		return null;
	} //}}}

	//{{{ isCompletionSelectable() method
	public boolean isCompletionSelectable(int index)
	{
		return true;
	} //}}}

	//{{{ Private members
	private View view;
	private JEditTextArea textArea;
	private List items;
	private String word;
	private XmlParsedData data;
	private String closingTag;
	//}}}
}
