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

		update();
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
		}
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if((bmsg.getWhat() == BufferUpdate.MODE_CHANGED
				|| bmsg.getWhat() == BufferUpdate.LOADED)
				&& bmsg.getBuffer() == view.getBuffer())
			{
				update();
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

	}

	// private members
	private View view;
	private EditPaneHandler editPaneHandler;
	private Vector elements;
	private JList elementList;
	private JList entityList;

	private void update()
	{
		CompletionInfo completionInfo = CompletionInfo.getCompletionInfo(
			view.getEditPane());
		if(completionInfo != null)
		{
			setDeclaredElements(completionInfo.elements);
			setDeclaredEntities(completionInfo.entities);
		}
		else
			showNotParsedMessage();
	}

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

				if(GUIUtilities.isPopupTrigger(evt))
				{
					// RMB click doesn't show edit tag dialog
					textArea.setSelectedText("<" + element.name
						+ (element.empty && !element.html
						? " />" : ">"));

					int caret = textArea.getCaretPosition();

					if(!element.empty)
					{
						textArea.setSelectedText("</"
							+ element.name + ">");
					}

					textArea.setCaretPosition(caret);

					textArea.requestFocus();
				}
				else
				{
					Buffer buffer = editPane.getBuffer();

					buffer.beginCompoundEdit();

					// show edit tag dialog box
					if(XmlActions.showEditTagDialog(view,element)
						&& !element.empty)
						XmlActions.insertClosingTag(textArea);

					buffer.endCompoundEdit();
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
