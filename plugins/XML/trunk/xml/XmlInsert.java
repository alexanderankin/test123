/*
 * XmlInsert.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

//{{{ Imports
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import xml.completion.*;
import xml.parser.*;
//}}}

public class XmlInsert extends JPanel implements EBComponent
{
	//{{{ XmlInsert constructor
	public XmlInsert(View view, boolean sideBySide)
	{
		this.view = view;
		editPaneHandler = new EditPaneHandler();

		setLayout(new GridLayout(sideBySide ? 1 : 3,
			sideBySide ? 3 : 1,3,3));

		JPanel elementPanel = new JPanel(new BorderLayout());
		elementPanel.add(BorderLayout.NORTH,
			new JLabel(jEdit.getProperty("xml-insert.elements")));

		elementList = new JList();
		elementList.setCellRenderer(new XmlListCellRenderer());
		elementList.addMouseListener(new MouseHandler());
		elementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		elementPanel.add(BorderLayout.CENTER,
			new JScrollPane(elementList));

		add(elementPanel);

		JPanel entityPanel = new JPanel(new BorderLayout());
		entityPanel.add(BorderLayout.NORTH,
			new JLabel(jEdit.getProperty("xml-insert.entities")));

		entityList = new JList();
		entityList.setCellRenderer(new XmlListCellRenderer());
		entityList.addMouseListener(new MouseHandler());
		entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entityPanel.add(BorderLayout.CENTER,
			new JScrollPane(entityList));

		add(entityPanel);

		JPanel idPanel = new JPanel(new BorderLayout());
		idPanel.add(BorderLayout.NORTH,
			new JLabel(jEdit.getProperty("xml-insert.ids")));

		idList = new JList();
		idList.setCellRenderer(new XmlListCellRenderer());
		idList.addMouseListener(new MouseHandler());
		idList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		idPanel.add(BorderLayout.CENTER,new JScrollPane(idList));

		add(idPanel);

		update();
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		//{{{ EditPaneUpdate
		if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate emsg = (EditPaneUpdate)msg;
			EditPane editPane = emsg.getEditPane();
			if(emsg.getWhat() == EditPaneUpdate.BUFFER_CHANGED
				&& editPane == view.getEditPane())
			{
				update();
			}
			else if(emsg.getWhat() == EditPaneUpdate.CREATED
				&& editPane.getView() == view)
			{
				JEditTextArea textArea = editPane.getTextArea();
				textArea.addFocusListener(editPaneHandler);
				textArea.addCaretListener(editPaneHandler);
			}
			else if(emsg.getWhat() == EditPaneUpdate.DESTROYED
				&& editPane.getView() == view)
			{
				JEditTextArea textArea = editPane.getTextArea();
				textArea.removeFocusListener(editPaneHandler);
				textArea.removeCaretListener(editPaneHandler);
			}
			else if(emsg.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
				update();
		} //}}}
		//{{{ BufferUpdate
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if((bmsg.getWhat() == BufferUpdate.MODE_CHANGED
				|| bmsg.getWhat() == BufferUpdate.LOADED)
				&& bmsg.getBuffer() == view.getBuffer())
			{
				update();
			}
		} //}}}
	} //}}}

	//{{{ update() method
	public void update()
	{
		CompletionInfo completionInfo = CompletionInfo.getCompletionInfo(
			view.getEditPane());
		if(completionInfo != null)
		{
			setDeclaredElements(completionInfo.elements);
			setDeclaredEntities(completionInfo.entities);
		}
		else
		{
			setDeclaredElements(null);
			setDeclaredEntities(null);
		}

		ArrayList ids = (ArrayList)view.getEditPane().getClientProperty(
			XmlPlugin.IDS_PROPERTY);
		setDeclaredIDs(ids);
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private EditPaneHandler editPaneHandler;
	private ArrayList elements;
	private JList elementList;
	private JList entityList;
	private JList idList;
	//}}}

	//{{{ showNotParsedMessage() method
	private void showNotParsedMessage()
	{
		setDeclaredElements(null);
		setDeclaredEntities(null);
		setDeclaredIDs(null);
	} //}}}

	//{{{ setDeclaredElements() method
	private void setDeclaredElements(ArrayList elements)
	{
		this.elements = elements;

		if(elements == null)
		{
			DefaultListModel model = new DefaultListModel();
			model.addElement(jEdit.getProperty("xml-insert.not-parsed"));
			elementList.setModel(model);
		}
		else
		{
			// inefficent
			DefaultListModel model = new DefaultListModel();
			for(int i = 0; i < elements.size(); i++)
			{
				model.addElement(elements.get(i));
			}
			elementList.setModel(model);

			if(model.getSize() != 0)
			{
				Rectangle cellBounds = elementList
					.getCellBounds(0,0);
				if(cellBounds != null)
				{
					elementList.setFixedCellHeight(cellBounds.height);
				}
			}
		}
	} //}}}

	//{{{ setDeclaredEntities() method
	private void setDeclaredEntities(ArrayList entities)
	{
		if(entities == null)
		{
			DefaultListModel model = new DefaultListModel();
			model.addElement(jEdit.getProperty("xml-insert.not-parsed"));
			entityList.setModel(model);
		}
		else
		{
			// inefficent
			DefaultListModel model = new DefaultListModel();
			for(int i = 0; i < entities.size(); i++)
			{
				model.addElement(entities.get(i));
			}
			entityList.setModel(model);

			if(model.getSize() != 0)
			{
				Rectangle cellBounds = entityList
					.getCellBounds(0,0);
				if(cellBounds != null)
				{
					entityList.setFixedCellHeight(cellBounds.height);
				}
			}
		}

	} //}}}

	//{{{ setDeclaredIDs() method
	private void setDeclaredIDs(ArrayList ids)
	{
		if(ids == null)
		{
			DefaultListModel model = new DefaultListModel();
			model.addElement(jEdit.getProperty("xml-insert.not-parsed"));
			idList.setModel(model);
		}
		else
		{
			// inefficent
			DefaultListModel model = new DefaultListModel();
			for(int i = 0; i < ids.size(); i++)
			{
				model.addElement(ids.get(i));
			}
			idList.setModel(model);

			if(model.getSize() != 0)
			{
				Rectangle cellBounds = idList.getCellBounds(0,0);
				if(cellBounds != null)
				{
					idList.setFixedCellHeight(cellBounds.height);
				}
			}
		}

	} //}}}

	//{{{ updateTagList() method
	private void updateTagList()
	{
		// TODO: only show tags that can be inserted at caret
		// position
	} //}}}

	//}}}

	//{{{ EditPaneHandler class
	class EditPaneHandler implements FocusListener, CaretListener
	{
		public void focusGained(FocusEvent evt)
		{
			showNotParsedMessage();
		}

		public void focusLost(FocusEvent evt)
		{
		}

		public void caretUpdate(CaretEvent evt)
		{
			if(evt.getSource() == view.getTextArea())
				updateTagList();
		}
	} //}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			//{{{ Handle clicks in element list
			if(evt.getSource() == elementList)
			{
				int index = elementList.locationToIndex(
					evt.getPoint());
				if(index == -1)
					return;

				Object obj = elementList.getModel().getElementAt(index);
				if(!(obj instanceof ElementDecl))
					return;

				ElementDecl element = (ElementDecl)obj;

				EditPane editPane = view.getEditPane();
				JEditTextArea textArea = editPane.getTextArea();
				Buffer buffer = editPane.getBuffer();

				//{{{ Handle right mouse button click
				if(GUIUtilities.isPopupTrigger(evt))
				{
					String openingTag = "<" + element.name
						+ (element.empty && !element.html
						? "/>" : ">");
					String closingTag;
					if(element.empty)
						closingTag = "";
					else
						closingTag = "</" + element.name + ">";

					Selection[] selection = textArea.getSelection();

					if(selection.length > 0)
					{
						try
						{
							buffer.beginCompoundEdit();

							for(int i = 0; i < selection.length; i++)
							{
								buffer.insert(selection[i].getStart(),
									openingTag);
								buffer.insert(selection[i].getEnd(),
									closingTag);
							}
						}
						finally
						{
							buffer.endCompoundEdit();
						}
					}
					else
						textArea.setSelectedText(closingTag);

					textArea.selectNone();
					textArea.requestFocus();
				} //}}}
				else
				{
					// show edit tag dialog box
					XmlActions.showEditTagDialog(view,element);
				}
			} //}}}
			//{{{ Handle clicks in entity list
			else if(evt.getSource() == entityList)
			{
				int index = entityList.locationToIndex(
					evt.getPoint());
				if(index == -1)
					return;

				Object obj = entityList.getModel().getElementAt(index);
				if(!(obj instanceof EntityDecl))
					return;

				EntityDecl entity = (EntityDecl)obj;

				JEditTextArea textArea = view.getTextArea();

				textArea.setSelectedText("&" + entity.name + ";");
				textArea.requestFocus();
			} //}}}
		}
	} //}}}
}
