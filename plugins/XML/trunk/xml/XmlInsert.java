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
		elementPanel.add(BorderLayout.CENTER,
			new JScrollPane(elementList));

		add(elementPanel);

		JPanel entityPanel = new JPanel(new BorderLayout());
		entityPanel.add(BorderLayout.NORTH,
			new JLabel(jEdit.getProperty("xml-insert.entities")));

		entityList = new JList();
		entityList.setCellRenderer(new Renderer());
		entityPanel.add(BorderLayout.CENTER,
			new JScrollPane(entityList));

		add(entityPanel);
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

		// inefficent
		DefaultListModel model = new DefaultListModel();
		for(int i = 0; i < elements.size(); i++)
		{
			model.addElement(elements.elementAt(i));
		}
		elementList.setModel(model);
	}

	void setDeclaredEntities(Vector entities)
	{
		// inefficent
		DefaultListModel model = new DefaultListModel();
		for(int i = 0; i < entities.size(); i++)
		{
			model.addElement(entities.elementAt(i));
		}
		entityList.setModel(model);
	}

	// private members
	private View view;
	private EditPaneHandler editPaneHandler;
	private Vector elements;
	private JList elementList;
	private JList entityList;

	private void showNotParsedMessage()
	{
		DefaultListModel model = new DefaultListModel();
		model.addElement(jEdit.getProperty("xml-insert.not-parsed"));
		elementList.setModel(model);

		model = new DefaultListModel();
		model.addElement(jEdit.getProperty("xml-insert.not-parsed"));
		entityList.setModel(model);
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
}
