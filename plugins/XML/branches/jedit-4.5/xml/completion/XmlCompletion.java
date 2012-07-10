/*
 * XmlCompletion.java
 * :tabSize=4:indentSize=4:noTabs=false:
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

import java.util.List;
import java.util.Map;

import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.Selection;

import sidekick.SideKickCompletion;
import xml.XmlActions;
import xml.XmlListCellRenderer;
import xml.XmlListCellRenderer.WithLabel;
import xml.XmlParsedData;
import xml.completion.ElementDecl.AttributeDecl;
import xml.EditTagDialog;
//}}}

public class XmlCompletion extends SideKickCompletion
{
	private Map<String, String> namespaces;
	private Map<String, String> namespacesToInsert;

	//{{{ XmlCompletion constructor
	public XmlCompletion(View view, List items, Map<String, String> namespaces,  Map<String, String> namespacesToInsert, String txt, XmlParsedData data,
		String closingTag)
	{
		super(view,txt);
		this.items = items;
		this.data = data;
		this.closingTag = closingTag;
		this.namespaces = namespaces;
		this.namespacesToInsert = namespacesToInsert;
	} //}}}

	//{{{ getRenderer() method
	public ListCellRenderer getRenderer()
	{
		return XmlListCellRenderer.INSTANCE;
	} //}}}

	//{{{ getTokenLength() method
	public int getTokenLength()
	{
		return text.length() + 1;
	} //}}}

	//{{{ insert() method
	public void insert(int index)
	{
		insert((WithLabel<Object>)get(index),'\n');
	} //}}}

	//{{{ handleKeystroke() method
	/* handles keystrokes when a completion popup is active */
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
			insert((WithLabel<Object>)get(index),ch);
		else if(ch == '>')
			XmlActions.insertClosingTagKeyTyped(view);
		else
			textArea.userInput(ch);

		return false;
	} //}}}

	//{{{ Private members
	private XmlParsedData data;
	private String closingTag;

	//{{{ insert() method
	/**
	 * @param wqn - an object  to insert (label will be used for EditTagDialog)
	 * @param ch - an additional character to insert afterwards
	 * 
	 */
	private void insert(final WithLabel<Object> wqn, char ch)
	{
		Object obj = wqn.element;
		
		Macros.Recorder recorder = view.getMacroRecorder();

		String insert;
		int caret = 0;

		if(obj instanceof XmlListCellRenderer.Comment)
		{
			insert = "!--  -->".substring(text.length());
			caret = 4;
		}
		else if(obj instanceof XmlListCellRenderer.CDATA)
		{
			insert = "![CDATA[]]>".substring(text.length());
			caret = 3;
		}
		else if(obj instanceof XmlListCellRenderer.ClosingTag)
		{
			insert = ("/" + closingTag + ">")
				.substring(text.length());
			caret = 0;
		}
		else if (obj instanceof AttributeDecl)
		{
			AttributeDecl attrDecl = (AttributeDecl) obj;
			StringBuilder buf = new StringBuilder();
			buf.append(EditTagDialog.composeName(attrDecl.name, attrDecl.namespace, namespaces, namespacesToInsert).substring(text.length()));
			buf.append("=\"\"");
			
			caret = buf.length() - 1;
			
			EditTagDialog.appendNamespaces(namespacesToInsert, buf);
			if(!namespacesToInsert.isEmpty()){
				caret = buf.length() - caret;
			}else{
				caret = 1;
			}
 			insert = buf.toString();
		}
		else if(obj instanceof ElementDecl)
		{
			final ElementDecl elementDecl = (ElementDecl)obj;
			boolean withEndOfTag = ch == '\n' || ch == '>';
			
			StringBuilder[]bufs = EditTagDialog.composeTag(data, elementDecl, namespaces, namespacesToInsert, withEndOfTag);
			bufs[0].replace(0,text.length(),"");
			if(withEndOfTag)
			{
				if(elementDecl.empty)
				{
					caret = 0;
					insert = bufs[0].toString();
				}
				else
				{
					caret = bufs[1].length();
					insert = bufs[0].toString() + bufs[1].toString();
				}

				if(ch == '\n' &&
				   jEdit.getBooleanProperty("xml.tageditor.popupOnComplete")) 
				{
					
					// hide the popup first, since the edit tag
					// dialog is modal
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							int caret = textArea.getCaretPosition();
							Selection s = textArea.getSelectionAtOffset(caret);
							int start = (s == null ? caret : s.getStart());
							Selection textSel = new Selection.Range(start - text.length() - 1,start); // < is not part of text but must be removed also
							XmlActions.showEditTagDialog(view, wqn.label, elementDecl, textSel, namespaces, namespacesToInsert, !elementDecl.attributes.isEmpty());
						}
					});
					return;
				}
			}
			else
			{
				bufs[0].append(ch);
				insert = bufs[0].toString();
				caret = 0;
			}

		}
		else if(obj instanceof EntityDecl)
		{
			EntityDecl entity = (EntityDecl)obj;

			insert = entity.name.substring(text.length()) + ";";
			caret = 0;
		}

		else
			throw new IllegalArgumentException("What's this? " + obj.getClass());

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
