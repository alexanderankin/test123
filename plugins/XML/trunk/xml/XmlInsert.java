/*
 * XmlInsert.java
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

import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;

public class XmlInsert extends JPanel implements DockableWindow, EBComponent
{
	public XmlInsert(View view)
	{
		this.view = view;
		editPaneHandler = new EditPaneHandler();

		setLayout(new GridLayout(2,1));

		JPanel elementPanel = new JPanel(new BorderLayout());
		elementPanel.add(BorderLayout.NORTH,
			new JLabel(jEdit.getProperty("xml-insert.elements")));

		elementList = new JList();
		elementList.setCellRenderer(new Renderer());
		elementList.addMouseListener(new MouseHandler());
		elementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		elementPanel.add(BorderLayout.CENTER,
			new JScrollPane(elementList));

		add(elementPanel);

		JPanel entityPanel = new JPanel(new BorderLayout());
		entityPanel.add(BorderLayout.NORTH,
			new JLabel(jEdit.getProperty("xml-insert.entities")));

		entityList = new JList();
		entityList.setCellRenderer(new Renderer());
		entityList.addMouseListener(new MouseHandler());
		entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entityPanel.add(BorderLayout.CENTER,
			new JScrollPane(entityList));

		add(entityPanel);

		// these are only guaranteed to be up-to-date if the
		// XML tree is visible
		if(view.getDockableWindowManager().getDockableWindow(
			XmlPlugin.TREE_NAME) != null)
		{
			Vector elements = (Vector)view.getEditPane()
				.getClientProperty(XmlPlugin.ELEMENTS_PROPERTY);
			if(elements != null)
				setDeclaredElements(elements);

			Vector entities = (Vector)view.getEditPane()
				.getClientProperty(XmlPlugin.ENTITIES_PROPERTY);
			if(entities != null)
				setDeclaredElements(entities);
		}
		else
			showNotParsedMessage();
	}

	public String getName()
	{
		return XmlPlugin.INSERT_NAME;
	}

	public Component getComponent()
	{
		return this;
	}

	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	}

	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate emsg = (EditPaneUpdate)msg;
			EditPane editPane = emsg.getEditPane();
			if(emsg.getWhat() == EditPaneUpdate.BUFFER_CHANGED
				&& editPane == view.getEditPane())
			{
				showNotParsedMessage();
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
		}
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if((bmsg.getWhat() == BufferUpdate.MODE_CHANGED
				|| bmsg.getWhat() == BufferUpdate.LOADED)
				&& bmsg.getBuffer() == view.getBuffer())
			{
				showNotParsedMessage();
			}
		}
	}

	// package-private members
	void setDeclaredElements(Vector elements)
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
				model.addElement(elements.elementAt(i));
			}
			elementList.setModel(model);

			elementList.setFixedCellHeight(elementList.getCellBounds(0,0)
				.height);
		}
	}

	void setDeclaredEntities(Vector entities)
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
				model.addElement(entities.elementAt(i));
			}
			entityList.setModel(model);

			elementList.setFixedCellHeight(elementList.getCellBounds(0,0)
				.height);
		}

	}

	// private members
	private View view;
	private EditPaneHandler editPaneHandler;
	private Vector elements;
	private JList elementList;
	private JList entityList;

	private void showNotParsedMessage()
	{
		setDeclaredElements(null);
		setDeclaredEntities(null);
	}

	private void updateTagList()
	{
		// TODO: only show tags that can be inserted at caret
		// position
	}

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
	}

	static class Renderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list,value,index,
				isSelected,cellHasFocus);

			if(value instanceof ElementDecl)
			{
				ElementDecl element = (ElementDecl)value;
				setText("<" + element.name
					+ (element.empty ? " /" : "")
					+ ">");
			}
			else if(value instanceof EntityDecl)
			{
				EntityDecl entity = (EntityDecl)value;
				setText("&" + entity.name + "; ("
					+ (entity.type == EntityDecl.INTERNAL
					? entity.value : "external") + ")");
			}

			return this;
		}
	}

	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
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
				int start = textArea.getCaretPosition();
				int caret;

				textArea.setSelectedText("<" + element.name
					+ (element.empty && !element.html
					? " />" : ">"));
				if(!element.empty)
				{
					textArea.userInput('\n');
					caret = textArea.getCaretPosition();
					textArea.userInput('\n');
					textArea.setSelectedText("</" + element.name + ">");
				}
				else
					caret = start;

				if(GUIUtilities.isPopupTrigger(evt))
				{
					// RMB click doesn't show edit tag dialog
					textArea.setCaretPosition(caret);
					textArea.requestFocus();
				}
				else
				{
					// put caret inside tag
					textArea.setCaretPosition(start + 1);

					// show edit tag dialog box
					XmlPlugin.showEditTagDialog(view,editPane,true);
				}
			}
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
			}
		}
	}
}
